<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<%@ include file="layout_header.jspf" %>

<div class="container my-4">
  <div class="d-flex justify-content-between align-items-center">
    <h5 class="mb-0">Chi tiết đơn hàng</h5>
    <span class="badge text-bg-secondary">${order.status.label}</span>
  </div>

  <div class="row g-3 mt-1">
    <div class="col-lg-8">
      <div class="card border-0 shadow-sm">
        <div class="card-header bg-transparent">
          Mã đơn: <span class="small">${order.id}</span> • Ngày tạo:
          <fmt:formatDate value="${order.createdAt}" type="both" dateStyle="medium" timeStyle="short"/>
        </div>
        <ul class="list-group list-group-flush">
          <c:forEach items="${order.items}" var="it">
            <li class="list-group-item d-flex justify-content-between">
              <span>${it.name} × ${it.qty}</span>
              <span><fmt:formatNumber value="${it.subtotal}" type="number" groupingUsed="true"/> đ</span>
            </li>
          </c:forEach>
        </ul>
        <div class="card-body d-flex justify-content-end">
          <div class="fs-5 fw-bold text-danger">
            <fmt:formatNumber value="${order.total}" type="number" groupingUsed="true"/> đ
          </div>
        </div>
      </div>
    </div>

    <div class="col-lg-4">
      <div class="card border-0 shadow-sm">
        <div class="card-header bg-transparent">Người nhận</div>
        <div class="card-body">
          <div class="fw-semibold">${order.customerName}</div>
          <div class="text-muted">${order.phone}</div>
          <div class="text-muted">${order.address}</div>
        </div>
      </div>

      <div class="card border-0 shadow-sm mt-3">
        <div class="card-header bg-transparent">Tiến trình</div>
        <div class="card-body">
          <ol class="list-unstyled vstack gap-2">
            <li>✓ Đã đặt</li>
            <li><c:if test="${order.status == 'CONFIRMED' || order.status == 'SHIPPED' || order.status == 'DELIVERED'}">✓</c:if> Đã xác nhận</li>
            <li><c:if test="${order.status == 'SHIPPED' || order.status == 'DELIVERED'}">✓</c:if> Đang giao</li>
            <li><c:if test="${order.status == 'DELIVERED'}">✓</c:if> Đã giao</li>
          </ol>
        </div>
      </div>
    </div>
  </div>
</div>

<%@ include file="layout_footer.jspf" %>
