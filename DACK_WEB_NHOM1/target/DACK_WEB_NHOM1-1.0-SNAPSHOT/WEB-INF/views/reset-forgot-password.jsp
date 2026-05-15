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
            <p class="text-muted">Nhập mật khẩu mới cho tài khoản của bạn</p>
          </div>

          <!-- Thông báo -->
          <c:if test="${not empty mess}">
            <div class="alert alert-info alert-dismissible fade show" role="alert">
              ${mess}
              <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
            </div>
          </c:if>

          <!-- Form đổi mật khẩu -->
          <form method="post" action="${pageContext.request.contextPath}/resetPassword" onsubmit="return validatePassword()">
            <input type="hidden" name="email" value="${email}">

            <div class="mb-3">
              <label class="form-label">Email</label>
              <input type="email" class="form-control" value="${email}" disabled>
            </div>

            <div class="mb-3">
              <label class="form-label">Mật khẩu mới</label>
              <input type="password" class="form-control" name="password" id="password" minlength="6" required>
              <small class="text-muted">Tối thiểu 6 ký tự</small>
            </div>

            <div class="mb-3">
              <label class="form-label">Xác nhận mật khẩu</label>
              <input type="password" class="form-control" name="confirm_password" id="confirm_password" minlength="6" required>
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
              const pass = document.getElementById("password").value;
              const confirm = document.getElementById("confirm_password").value;
              if (pass !== confirm) {
                alert("Mật khẩu xác nhận không khớp!");
                return false;
              }
              return true;
            }
          </script>

        </div>
      </div>
    </div>
  </div>
</div>

<%@ include file="layout_footer.jspf" %>
