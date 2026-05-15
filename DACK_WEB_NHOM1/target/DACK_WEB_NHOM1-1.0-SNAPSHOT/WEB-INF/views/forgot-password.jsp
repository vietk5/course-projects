<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ include file="layout_header.jspf" %>

<div class="container my-5">
  <div class="row justify-content-center">
    <div class="col-md-5">
      <div class="card border-0 shadow-sm">
        <div class="card-body p-4">
          <div class="text-center mb-4">
            <i class="bi bi-key-fill text-primary" style="font-size: 3rem;"></i>
            <h4 class="mt-3">Quên mật khẩu?</h4>
            <p class="text-muted">Nhập email để nhận link đặt lại mật khẩu</p>
          </div>

          <!-- Hiển thị lỗi -->
          <c:if test="${not empty error}">
            <div class="alert alert-danger alert-dismissible fade show" role="alert">
              <i class="bi bi-exclamation-triangle me-2"></i>${error}
              <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
            </div>
          </c:if>

          <!-- Hiển thị thành công -->
          <c:if test="${not empty success}">
            <div class="alert alert-success alert-dismissible fade show" role="alert">
              <i class="bi bi-check-circle me-2"></i>${success}
              <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
            </div>
          </c:if>

          <!-- Form nhập email -->
          <form method="post" action="${pageContext.request.contextPath}/requestPassword">
            <div class="mb-3">
              <label class="form-label">Email đã đăng ký</label>
              <input type="email" class="form-control" name="email" 
                     placeholder="example@email.com" required>
            </div>

            <button type="submit" class="btn btn-rog w-100 mb-3">
              <i class="bi bi-send me-2"></i>Gửi link đặt lại mật khẩu
            </button>

            <div class="text-center">
              <a href="${pageContext.request.contextPath}/login" class="text-decoration-none">
                <i class="bi bi-arrow-left me-2"></i>Quay lại đăng nhập
              </a>
            </div>
          </form>

        </div>
      </div>
    </div>
  </div>
</div>

<%@ include file="layout_footer.jspf" %>
