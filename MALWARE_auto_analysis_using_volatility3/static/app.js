const form = document.getElementById("upload-form");
const input = document.getElementById("file-input");
const dropzone = document.getElementById("dropzone");
const selected = document.getElementById("selected-file");
const analyzeBtn = document.getElementById("analyze-btn");
const errorBox = document.getElementById("form-error");
const analysisPanel = document.getElementById("analysis-panel");
let chosenFile = null;
let pollTimer = null;

function formatBytes(bytes) {
  const units = ["B", "KB", "MB", "GB", "TB"];
  let value = bytes, index = 0;
  while (value >= 1024 && index < units.length - 1) {
    value /= 1024;
    index += 1;
  }
  return `${value.toFixed(index > 1 ? 2 : 0)} ${units[index]}`;
}

function selectFile(file) {
  chosenFile = file || null;
  errorBox.textContent = "";
  if (!chosenFile) {
    selected.hidden = true;
    analyzeBtn.disabled = true;
    input.value = "";
    return;
  }
  document.getElementById("file-name").textContent = chosenFile.name;
  document.getElementById("file-size").textContent = formatBytes(chosenFile.size);
  selected.hidden = false;
  analyzeBtn.disabled = false;
}

input.addEventListener("change", () => selectFile(input.files[0]));
document.getElementById("remove-file").addEventListener("click", () => selectFile(null));
["dragenter", "dragover"].forEach(name => dropzone.addEventListener(name, event => {
  event.preventDefault();
  dropzone.classList.add("dragging");
}));
["dragleave", "drop"].forEach(name => dropzone.addEventListener(name, event => {
  event.preventDefault();
  dropzone.classList.remove("dragging");
}));
dropzone.addEventListener("drop", event => selectFile(event.dataTransfer.files[0]));

function setStatus(job) {
  const progress = job.progress || 0;
  document.getElementById("progress-bar").style.width = `${progress}%`;
  document.getElementById("progress-value").textContent = `${progress}%`;
  document.getElementById("progress-message").textContent = job.message || "";
  const pill = document.getElementById("status-pill");
  pill.className = `status-pill ${job.status}`;
  pill.innerHTML = `<span></span> ${job.status.toUpperCase()}`;

  if (job.status === "completed") {
    clearInterval(pollTimer);
    const counts = job.summary.severity_counts;
    document.getElementById("critical-count").textContent = counts.CRITICAL || 0;
    document.getElementById("high-count").textContent = counts.HIGH || 0;
    document.getElementById("medium-count").textContent = counts.MEDIUM || 0;
    document.getElementById("yara-count").textContent = job.summary.yara_hits || 0;
    document.getElementById("result-grid").hidden = false;
    document.getElementById("result-actions").hidden = false;
    document.getElementById("view-report").href = `/jobs/${job.id}/report`;
    document.getElementById("download-report").href = `/jobs/${job.id}/download`;
    analyzeBtn.disabled = false;
    analyzeBtn.querySelector("span").textContent = "Phân tích file khác";
  } else if (job.status === "failed") {
    clearInterval(pollTimer);
    errorBox.textContent = job.error || "Không thể hoàn thành phân tích.";
    analyzeBtn.disabled = false;
  }
}

async function pollJob(jobId) {
  try {
    const response = await fetch(`/api/jobs/${jobId}`, {cache: "no-store"});
    if (!response.ok) throw new Error("Không đọc được trạng thái job.");
    setStatus(await response.json());
  } catch (error) {
    clearInterval(pollTimer);
    errorBox.textContent = error.message;
    analyzeBtn.disabled = false;
  }
}

form.addEventListener("submit", async event => {
  event.preventDefault();
  if (!chosenFile) return;
  clearInterval(pollTimer);
  errorBox.textContent = "";
  analyzeBtn.disabled = true;
  analyzeBtn.querySelector("span").textContent = "Đang tải file...";
  const body = new FormData();
  body.append("memory_dump", chosenFile);
  try {
    const response = await fetch("/api/jobs", {method: "POST", body});
    const data = await response.json();
    if (!response.ok) throw new Error(data.error || "Upload thất bại.");
    analysisPanel.hidden = false;
    document.getElementById("analysis-file").textContent = chosenFile.name;
    document.getElementById("result-grid").hidden = true;
    document.getElementById("result-actions").hidden = true;
    setStatus(data);
    analysisPanel.scrollIntoView({behavior: "smooth", block: "start"});
    analyzeBtn.querySelector("span").textContent = "Đang phân tích...";
    pollTimer = setInterval(() => pollJob(data.id), 1500);
    pollJob(data.id);
  } catch (error) {
    errorBox.textContent = error.message;
    analyzeBtn.disabled = false;
    analyzeBtn.querySelector("span").textContent = "Bắt đầu phân tích IOC";
  }
});
