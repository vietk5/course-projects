<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ include file="layout_header.jspf" %>
<div class="container my-4">
  <div class="row justify-content-center">
    <div class="col-md-6">
      <div class="card border-0 shadow-sm p-3">
        <h5 class="mb-3">Đăng ký</h5>
        <c:if test="${not empty error}"><div class="alert alert-danger">${error}</div></c:if>
        <form method="post" action="${pageContext.request.contextPath}/register" class="row g-3">
          <div class="col-12 col-md-6">
            <label class="form-label">Họ tên</label>
            <input class="form-control" name="fullName" required>
          </div>
          <div class="col-12 col-md-6">
            <label class="form-label">Email</label>
            <input class="form-control" type="email" name="email" required>
          </div>
          <div class="col-12 col-md-6">
            <label class="form-label">Mật khẩu</label>
            <input class="form-control" type="password" name="password" minlength="6" required>
          </div>
          <div class="col-12 col-md-6">
            <label class="form-label">Xác nhận mật khẩu</label>
            <input class="form-control" type="password" name="confirm" minlength="6" required
                   oninput="this.setCustomValidity(this.value!==this.form.password.value?'Không khớp':'')">
          </div>
          <div class="col-12">
            <button class="btn btn-rog w-100" type="submit">Tạo tài khoản</button>
          </div>
        </form>
      </div>
    </div>
  </div>
</div>
        
        
<%@ include file="layout_footer.jspf" %>
