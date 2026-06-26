import json
import os
import threading
import traceback
import uuid
from datetime import datetime, timezone
from pathlib import Path

from flask import Flask, abort, jsonify, render_template, request, send_file
from werkzeug.utils import secure_filename

from auto_ioc_v5 import analyze_dump


BASE_DIR = Path(__file__).resolve().parent
UPLOAD_DIR = BASE_DIR / "uploads"
RESULT_DIR = BASE_DIR / "web_results"
ALLOWED_EXTENSIONS = {
    ".raw", ".mem", ".dmp", ".dump", ".vmem", ".lime", ".elf", ".bin"
}

UPLOAD_DIR.mkdir(exist_ok=True)
RESULT_DIR.mkdir(exist_ok=True)

app = Flask(__name__)
app.config["MAX_CONTENT_LENGTH"] = int(
    os.getenv("MAX_UPLOAD_BYTES", str(16 * 1024**3))
)
app.config["SEND_FILE_MAX_AGE_DEFAULT"] = 0

jobs = {}
jobs_lock = threading.Lock()
analysis_slots = threading.Semaphore(int(os.getenv("MAX_CONCURRENT_JOBS", "1")))


def load_saved_jobs():
    for status_path in RESULT_DIR.glob("*/status.json"):
        try:
            job = json.loads(status_path.read_text(encoding="utf-8"))
            if job.get("id"):
                jobs[job["id"]] = job
        except (OSError, ValueError):
            continue


def utc_now():
    return datetime.now(timezone.utc).isoformat()


def write_job(job_id):
    with jobs_lock:
        snapshot = dict(jobs[job_id])
    job_dir = RESULT_DIR / job_id
    job_dir.mkdir(exist_ok=True)
    (job_dir / "status.json").write_text(
        json.dumps(snapshot, ensure_ascii=False, indent=2),
        encoding="utf-8",
    )


def update_job(job_id, **changes):
    with jobs_lock:
        jobs[job_id].update(changes)
        jobs[job_id]["updated_at"] = utc_now()
    write_job(job_id)


def public_job(job):
    fields = {
        "id", "filename", "size", "status", "progress", "message",
        "created_at", "updated_at", "error", "summary",
    }
    return {key: job.get(key) for key in fields if key in job}


def run_analysis(job_id, upload_path):
    job_dir = RESULT_DIR / job_id

    def progress(percent, message):
        update_job(
            job_id,
            status="running",
            progress=percent,
            message=message,
        )

    try:
        update_job(
            job_id,
            status="queued",
            progress=1,
            message="Đang chờ tài nguyên phân tích",
        )
        with analysis_slots:
            result = analyze_dump(
                str(upload_path),
                output_dir=str(job_dir / "artifacts"),
                progress_callback=progress,
            )
        summary = {
            "sha256": result["sha256"],
            "total_ioc": result["total_ioc"],
            "yara_hits": result["yara_hits"],
            "severity_counts": result["severity_counts"],
            "ai_enabled": result["ai_enabled"],
            "ai_error": result["ai_error"],
        }
        update_job(
            job_id,
            status="completed",
            progress=100,
            message="Báo cáo đã sẵn sàng",
            summary=summary,
            html_path=result["html_path"],
            json_path=result["json_path"],
            archive_path=result["archive_path"],
        )
    except Exception as exc:
        update_job(
            job_id,
            status="failed",
            message="Phân tích thất bại",
            error=str(exc),
            traceback=traceback.format_exc(),
        )
    finally:
        if os.getenv("KEEP_UPLOADS", "0") != "1":
            upload_path.unlink(missing_ok=True)


def get_job_or_404(job_id):
    with jobs_lock:
        job = jobs.get(job_id)
    if not job:
        abort(404)
    return job


@app.get("/")
def index():
    return render_template(
        "index.html",
        max_upload_gb=app.config["MAX_CONTENT_LENGTH"] / 1024**3,
        allowed_extensions=", ".join(sorted(ALLOWED_EXTENSIONS)),
    )


@app.post("/api/jobs")
def create_job():
    uploaded = request.files.get("memory_dump")
    if not uploaded or not uploaded.filename:
        return jsonify(error="Vui lòng chọn một file memory dump."), 400

    filename = secure_filename(uploaded.filename)
    extension = Path(filename).suffix.lower()
    if extension not in ALLOWED_EXTENSIONS:
        return jsonify(
            error=f"Định dạng {extension or '(không có đuôi)'} chưa được hỗ trợ."
        ), 400

    job_id = uuid.uuid4().hex
    upload_path = UPLOAD_DIR / f"{job_id}_{filename}"
    uploaded.save(upload_path)
    size = upload_path.stat().st_size
    if size == 0:
        upload_path.unlink(missing_ok=True)
        return jsonify(error="File upload rỗng."), 400

    now = utc_now()
    job = {
        "id": job_id,
        "filename": filename,
        "size": size,
        "status": "queued",
        "progress": 0,
        "message": "Đã nhận file, chuẩn bị phân tích",
        "created_at": now,
        "updated_at": now,
        "error": None,
        "summary": None,
    }
    with jobs_lock:
        jobs[job_id] = job
    write_job(job_id)

    thread = threading.Thread(
        target=run_analysis,
        args=(job_id, upload_path),
        daemon=True,
        name=f"analysis-{job_id[:8]}",
    )
    thread.start()
    return jsonify(public_job(job)), 202


@app.get("/api/jobs/<job_id>")
def job_status(job_id):
    return jsonify(public_job(get_job_or_404(job_id)))


@app.get("/jobs/<job_id>/report")
def view_report(job_id):
    job = get_job_or_404(job_id)
    if job.get("status") != "completed":
        abort(409)
    return send_file(job["html_path"], mimetype="text/html")


@app.get("/jobs/<job_id>/download")
def download_report(job_id):
    job = get_job_or_404(job_id)
    if job.get("status") != "completed":
        abort(409)
    stem = Path(job["filename"]).stem
    return send_file(
        job["archive_path"],
        as_attachment=True,
        download_name=f"{stem}_forensic_report.zip",
    )


@app.errorhandler(413)
def upload_too_large(_error):
    return jsonify(error="File vượt quá giới hạn upload của hệ thống."), 413


load_saved_jobs()


if __name__ == "__main__":
    app.run(
        host=os.getenv("HOST", "127.0.0.1"),
        port=int(os.getenv("PORT", "5000")),
        debug=os.getenv("FLASK_DEBUG", "0") == "1",
        threaded=True,
    )
