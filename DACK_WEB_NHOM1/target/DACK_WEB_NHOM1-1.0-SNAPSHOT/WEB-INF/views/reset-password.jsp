<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ include file="layout_header.jspf" %>

<div class="container my-5">
  <div class="row justify-content-center">
    <div class="col-md-5">
      <div class="card border-0 shadow-sm">
        <div class="card-body p-4">
          <div class="text-center mb-4">
            <i class="bi bi-shield-lock-fill text-primary" style="font-size: 3rem;"></i>
            <h4 class="mt-3">Đặt lại mật khẩu</h4>
            <p class="text-muted">Tạo mật khẩu mới cho tài khoản của bạn</p>
          </div>

          <c:if test="${not empty error}">
            <div class="alert alert-danger alert-dismissible fade show" role="alert">
              <i class="bi bi-exclamation-triangle me-2"></i>${error}
              <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
            </div>
          </c:if>

          <c:if test="${not empty success}">
            <div class="alert alert-success" role="alert">
              <i class="bi bi-check-circle me-2"></i>${success}
              <div class="mt-3">
                <a href="${pageContext.request.contextPath}/login" class="btn btn-rog w-100">
                  Đăng nhập ngay
                </a>
              </div>
            </div>
          </c:if>

          <c:if test="${empty success}">
            <form method="post" action="${pageContext.request.contextPath}/forgot-password" 
                  onsubmit="return validatePassword()">
              <input type="hidden" name="action" value="resetPassword">
              <input type="hidden" name="token" value="${token}">

              <div class="mb-3">
                <label class="form-label">Email</label>
                <input type="email" class="form-control" value="${email}" disabled>
              </div>

              <div class="mb-3">
                <label class="form-label">Mật khẩu mới</label>
                <input type="password" class="form-control" name="newPassword" 
                       id="newPassword" minlength="6" required>
                <small class="text-muted">Tối thiểu 6 ký tự</small>
              </div>

              <div class="mb-3">
                <label class="form-label">Xác nhận mật khẩu</label>
                <input type="password" class="form-control" name="confirmPassword" 
                       id="confirmPassword" minlength="6" required>
              </div>

              <button type="submit" class="btn btn-rog w-100 mb-3">
                <i class="bi bi-check-circle me-2"></i>Đặt lại mật khẩu
              </button>

              <div class="text-center">
                <a href="${pageContext.request.contextPath}/login" class="text-decoration-none">
                  <i class="bi bi-arrow-left me-2"></i>Quay lại đăng nhập
                </a>
              </div>
            </form>

            <script>
              function validatePassword() {
                const newPass = document.getElementById('newPassword').value;
                const confirmPass = document.getElementById('confirmPassword').value;
                if (newPass !== confirmPass) {
                  alert('Mật khẩu xác nhận không khớp!');
                  return false;
                }
                return true;
              }
            </script>
          </c:if>
        </div>
      </div>
    </div>
  </div>
</div>

<%@ include file="layout_footer.jspf" %>

