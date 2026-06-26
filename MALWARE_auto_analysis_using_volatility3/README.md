# MemSentry - Automated Memory Dump IOC Analyzer

MemSentry là ứng dụng web hỗ trợ phân tích memory dump nhằm phát hiện dấu hiệu
xâm nhập (IOC), hành vi bất thường và bằng chứng liên quan đến malware.

Trọng tâm của project là **Volatility 3 Framework**. Hệ thống tự động chạy một tập hợp plugin
memory forensics, chuẩn hóa kết quả, áp dụng heuristic, quét YARA và tạo báo cáo
điều tra hoàn chỉnh dưới dạng HTML, JSON và ZIP.

> Dự án phục vụ nghiên cứu, DFIR, malware analysis và đào tạo an toàn thông tin.
> Memory dump có thể chứa mật khẩu, token, khóa mã hóa và dữ liệu cá nhân. Chỉ
> triển khai hệ thống trong môi trường tin cậy.

## Tính năng chính

- Người dùng upload file memory dump trực tiếp trên giao diện web.
- Hệ thống tự động phân tích file bằng Volatility 3 và trích xuất các dấu hiệu nghi ngờ như tiến trình, kết nối mạng, command line, persistence, DLL ẩn, code injection và IOC liên quan đến mã độc.
- Sau khi phân tích xong, ứng dụng tạo báo cáo HTML dễ xem, có bằng chứng chi tiết để hỗ trợ điều tra.
- Người dùng có thể tải kết quả dưới dạng JSON hoặc ZIP để lưu trữ, chia sẻ hoặc tích hợp với hệ thống khác.

## Volatility 3

[Volatility 3](https://github.com/volatilityfoundation/volatility3) là engine
phân tích chính. MemSentry gọi trực tiếp từng plugin,
lưu kết quả thô, sau đó đưa dữ liệu qua lớp trích xuất và tương quan IOC.

Các plugin Windows đang được sử dụng:

| Plugin | Mục đích |
| --- | --- |
| `windows.info` | Xác định thông tin hệ điều hành và memory image |
| `windows.pslist` | Liệt kê tiến trình |
| `windows.pstree` | Phân tích quan hệ cha-con giữa các tiến trình |
| `windows.cmdline` | Thu thập command line |
| `windows.netscan` | Tìm kết nối mạng và socket |
| `windows.malfind` | Tìm vùng nhớ có dấu hiệu code injection |
| `windows.dlllist` | Liệt kê DLL của tiến trình |
| `windows.filescan` | Tìm file object còn tồn tại trong bộ nhớ |
| `windows.svcscan` | Phân tích Windows service |
| `windows.handles` | Tìm mutex thông qua handle kiểu `Mutant` |
| `windows.ldrmodules` | Phát hiện module bị ẩn khỏi PEB |
| `windows.registry.printkey` | Kiểm tra các khóa `Run` và `RunOnce` |

## Quy trình phân tích

```text
Memory dump
    |
    +-- SHA-256 fingerprint
    |
    +-- Volatility 3 plugins
    |      |
    |      +-- Process, network, injection, DLL, file, service, registry...
    |
    +-- IOC extraction và heuristic correlation
    |
    +-- YARA scan
    |
    +-- AI-assisted triage (tùy chọn)
    |
    +-- HTML report + JSON + raw evidence + ZIP
```

Mức độ cảnh báo được chuẩn hóa thành:

- `CRITICAL`: bằng chứng mạnh về malware, injection hoặc attack tool.
- `HIGH`: hành vi rất đáng ngờ, cần điều tra ngay.
- `MEDIUM`: bất thường có thể hợp lệ nhưng cần xác minh.
- `LOW`: dữ liệu tham khảo hoặc IOC có độ tin cậy thấp hơn.

## Công nghệ sử dụng

- **Python 3**: ngôn ngữ chính của backend và analysis engine.
- **Volatility 3**: framework memory forensics cốt lõi.
- **Flask**: web server, API upload và quản lý analysis job.
- **YARA / yara-python**: nhận diện mẫu malware trong memory và PE artifacts.
- **OpenRouter API**: lớp AI triage tùy chọn.
- **HTML, CSS, JavaScript**: giao diện upload, theo dõi tiến độ và nhận report.
- **Werkzeug**: xử lý file upload và chuẩn hóa tên file.
- **pefile**: hỗ trợ xử lý cấu trúc Portable Executable.

## Cấu trúc project

```text
auto-analyze/
|-- app.py                 # Flask web application và background jobs
|-- auto_ioc_v5.py         # Volatility, IOC, YARA, AI và report engine
|-- requirements.txt       # Python dependencies
|-- .env                   # Cấu hình cục bộ, không commit
|-- templates/
|   `-- index.html         # Giao diện chính
|-- static/
|   |-- app.css
|   `-- app.js
|-- volatility3/           # Volatility 3 source và vol.py
|-- yara_rules/            # Bộ YARA rules
|-- uploads/               # File tạm trong quá trình upload
|-- web_results/           # Kết quả tạo từ giao diện web
`-- vol_results/           # Kết quả tạo từ CLI
```

## Cài đặt

### 1. Mở project

```powershell
cd \auto-analyze
```

Nếu project được đặt ở thư mục khác, các đường dẫn mặc định vẫn tự động được
xác định dựa trên vị trí của source code.

### 2. Tạo virtual environment

```powershell
python -m venv .venv
.\.venv\Scripts\Activate.ps1
```

Nếu PowerShell chặn script activation:

```powershell
Set-ExecutionPolicy -Scope Process -ExecutionPolicy Bypass
.\.venv\Scripts\Activate.ps1
```

### 3. Cài dependency

```powershell
python -m pip install --upgrade pip
python -m pip install -r requirements.txt
```

Kiểm tra Volatility 3:

```powershell
python volatility3\vol.py --help
```

Nếu lệnh hiển thị phiên bản và danh sách tùy chọn, Volatility đã sẵn sàng.

## Cấu hình môi trường

Tạo hoặc cập nhật file `.env` tại thư mục gốc:

```dotenv
# AI analysis qua OpenRouter - không bắt buộc
OPENROUTER_API_KEY=sk-or-v1-your-api-key
CHAT_MODEL=deepseek/deepseek-chat-v3-0324

# Web server
HOST=127.0.0.1
PORT=5000
FLASK_DEBUG=0

# Giới hạn upload mặc định: 16 GiB
MAX_UPLOAD_BYTES=17179869184

# Số analysis job được chạy đồng thời
MAX_CONCURRENT_JOBS=1

# 0: xóa dump sau khi chạy, 1: giữ file trong uploads/
KEEP_UPLOADS=0

# Các đường dẫn sau là tùy chọn
VOL_PATH=E:\auto-analyze\volatility3\vol.py
OUTPUT_BASE=E:\auto-analyze\vol_results
YARA_RULES_DIR=E:\auto-analyze\yara_rules
```

`OPENROUTER_API_KEY` không bắt buộc. Nếu không cấu hình hoặc API gặp lỗi,
Volatility, IOC extraction, YARA và report vẫn tiếp tục hoạt động; report chỉ
không có phần nhận định AI.

## Chạy giao diện web

Khởi động ứng dụng:

```powershell
python app.py
```

Mặc định, truy cập:

```text
http://127.0.0.1:5000
```

Các bước sử dụng:

1. Kéo-thả hoặc chọn một memory dump.
2. Nhấn **Bắt đầu phân tích IOC**.
3. Theo dõi tiến độ chạy Volatility, IOC extraction, YARA và AI.
4. Khi hoàn tất, chọn **Xem báo cáo** để mở HTML report.
5. Chọn **Tải gói report ZIP** để tải toàn bộ bằng chứng.

Định dạng file được chấp nhận:

```text
.raw, .mem, .dmp, .dump, .vmem, .lime, .elf, .bin
```

## Kết quả phân tích

Mỗi web job được lưu trong:

```text
web_results/<job-id>/
|-- status.json
|-- <ten-dump>_forensic_report.zip
`-- artifacts/
    |-- ioc_report.html
    |-- ioc_report.json
    |-- info.txt
    |-- pslist.txt
    |-- pstree.txt
    |-- cmdline.txt
    |-- netscan.txt
    |-- malfind.txt
    |-- dlllist.txt
    |-- filescan.txt
    |-- svcscan.txt
    |-- ldrmodules.txt
    |-- reg_run*.txt
    `-- yarascan_*.txt
```

Trong đó:

- `ioc_report.html`: báo cáo trực quan dành cho analyst.
- `ioc_report.json`: metadata, IOC, YARA summary và AI analysis.
- `*.txt`: output thô của từng plugin Volatility và YARA scan.
- `*_forensic_report.zip`: gói báo cáo để lưu trữ hoặc chuyển giao.
- `status.json`: trạng thái job để web app khôi phục sau khi restart.

## Định hướng phát triển

- Bổ sung profile phân tích Linux và macOS.
- Dùng Celery/RQ và Redis cho job queue.
- Thêm authentication, audit log và retention policy.
- Hỗ trợ PDF/STIX 2.1 và export sang SIEM.
- Thêm timeline, process graph và network graph.
- Cho phép lựa chọn bộ plugin hoặc analysis profile.
- Cải thiện scoring và correlation giữa Volatility, YARA và threat intelligence.

## License và nguồn tham khảo

Volatility 3 nằm trong thư mục `volatility3/` và tuân theo giấy phép riêng của
Volatility Foundation. Các YARA rules có thể đến từ nhiều nguồn và có điều
khoản sử dụng riêng; cần kiểm tra license trước khi phân phối hoặc sử dụng trong
môi trường thương mại.

Tham khảo:

- [Volatility 3 GitHub](https://github.com/volatilityfoundation/volatility3)
- [Volatility 3 Documentation](https://volatility3.readthedocs.io/)
- [YARA Documentation](https://yara.readthedocs.io/)
- [Flask Documentation](https://flask.palletsprojects.com/)
- [YARA Rules](https://github.com/yara-rules/rules/)

