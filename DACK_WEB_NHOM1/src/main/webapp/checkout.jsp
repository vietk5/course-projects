<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<%@ include file="layout_header.jspf" %>
<c:set var="cart" value="${sessionScope.cart}" />
<div class="container my-4">
  <h5 class="mb-3">Xác nhận đơn hàng</h5>
  <div class="row g-3">
    <div class="col-lg-7">
      <div class="card border-0 shadow-sm p-3">
        <form method="post" action="${pageContext.request.contextPath}/checkout" class="row g-3">
          <div class="col-md-6">
            <label class="form-label">Họ tên</label>
            <input class="form-control" name="fullName" required>
          </div>
          <div class="col-md-6">
            <label class="form-label">Số điện thoại</label>
            <input class="form-control" name="phone" required>
          </div>
          <div class="col-12">
            <label class="form-label">Địa chỉ</label>
            <textarea class="form-control" name="address" rows="3" required></textarea>
          </div>
          <div class="col-12">
            <button class="btn btn-rog w-100" type="submit">Đặt hàng</button>
          </div>
        </form>
      </div>
    </div>
    <div class="col-lg-5">
      <div class="card border-0 shadow-sm">
        <div class="card-header bg-transparent fw-semibold">Tóm tắt đơn hàng</div>
        <ul class="list-group list-group-flush">
          <c:set var="sum" value="0" scope="page"/>
          <c:forEach items="${cart}" var="it">
            <li class="list-group-item d-flex justify-content-between">
              <span>${it.name} × ${it.qty}</span>
              <span><fmt:formatNumber value="${it.price * it.qty}" type="number" groupingUsed="true"/> đ</span>
            </li>
            <c:set var="sum" value="${sum + (it.price * it.qty)}"/>
          </c:forEach>
        </ul>
        <div class="card-body d-flex justify-content-between">
          <span class="fw-semibold">Tổng</span>
          <span class="fs-5 text-danger fw-bold"><fmt:formatNumber value="${sum}" type="number" groupingUsed="true"/> đ</span>
        </div>
      </div>
    </div>
  </div>
</div>
<%@ include file="layout_footer.jspf" %>
