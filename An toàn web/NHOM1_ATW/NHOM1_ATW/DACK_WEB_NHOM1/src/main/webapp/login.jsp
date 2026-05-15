<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ include file="layout_header.jspf" %>

<div class="container my-4" style="max-width:580px;">
  <div class="card border-0 shadow-sm">
    <div class="card-body p-4">
      <h4 class="mb-3">Đăng nhập</h4>

      <c:if test="${param.error == '1'}">
        <div class="alert alert-danger">Vui lòng nhập email & mật khẩu.</div>
      </c:if>

      <form method="post" action="${pageContext.request.contextPath}/login">
        <c:set var="ret" value="${not empty param.returnUrl ? param.returnUrl : (not empty param.next ? param.next : '/home')}"/>
        <input type="hidden" name="returnUrl" value="${ret}"/>

        <div class="mb-3">
          <label class="form-label">Email</label>
          <input class="form-control" type="email" name="email" placeholder="you@example.com" required>
        </div>
        <div class="mb-3">
          <label class="form-label">Mật khẩu</label>
          <input class="form-control" type="password" name="password" required>
        </div>

        <button class="btn btn-rog w-100" type="submit">Đăng nhập</button>
      </form>

      <div class="text-center mt-3">
        <small>Chưa có tài khoản?
          <a class="link-primary" href="${pageContext.request.contextPath}/register">Đăng ký</a>
        </small>
      </div>
    </div>
  </div>
</div>

<%@ include file="layout_footer.jspf" %>
