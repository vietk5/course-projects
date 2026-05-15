<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ include file="layout_header.jspf" %>

<div class="container my-4">
  <div class="row justify-content-center">
    <div class="col-md-6">
      <div class="card border-0 shadow-sm p-3">
        <h5 class="mb-3">Đăng nhập</h5>
        <c:if test="${not empty error}">
          <div class="alert alert-danger">${error}</div>
        </c:if>
        <form method="post" action="${pageContext.request.contextPath}/login" class="row g-3">
          <div class="col-12">
            <label class="form-label">Tài khoản / Email</label>
            <input class="form-control" name="account" required>
          </div>
          <div class="col-12">
            <label class="form-label">Mật khẩu</label>
            <input class="form-control" type="password" name="password" required>
          </div>
          <div class="col-12">
            <button class="btn btn-rog w-100" type="submit">Đăng nhập</button>
          </div>
          <div class="col-12 text-center">
            <a href="${pageContext.request.contextPath}/forgot-password" class="small text-muted">
              Quên mật khẩu?
            </a>
          </div>
          <div class="col-12 text-center">
            Chưa có tài khoản? <a href="${pageContext.request.contextPath}/register">Đăng ký</a>
          </div>
        </form>
      </div>
    </div>
  </div>
</div>

<%@ include file="layout_footer.jspf" %>
