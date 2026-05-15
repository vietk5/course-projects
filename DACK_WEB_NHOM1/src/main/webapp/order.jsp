<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<%@ include file="layout_header.jspf" %>

<div class="container my-4">
  <h5 class="mb-3">Đơn hàng của tôi</h5>
  <c:choose>
    <c:when test="${empty orders}">
      <div class="alert alert-info">Chưa có đơn hàng nào.</div>
    </c:when>
    <c:otherwise>
      <div class="card border-0 shadow-sm">
        <div class="table-responsive">
          <table class="table align-middle mb-0">
            <thead><tr>
              <th>Mã đơn</th><th>Ngày tạo</th><th>Trạng thái</th><th>Tổng tiền</th><th></th>
            </tr></thead>
            <tbody>
            <c:forEach items="${orders}" var="o">
              <tr>
                <td class="small">${o.id}</td>
                <td><fmt:formatDate value="${o.createdAt}" type="both" dateStyle="medium" timeStyle="short"/></td>
                <td>
                  <span class="badge text-bg-secondary">${o.status.label}</span>
                </td>
                <td class="fw-bold text-danger">
                  <fmt:formatNumber value="${o.total}" type="number" groupingUsed="true"/> đ
                </td>
                <td><a class="btn btn-sm btn-outline-light-subtle" href="${pageContext.request.contextPath}/order?id=${o.id}">Chi tiết</a></td>
              </tr>
            </c:forEach>
            </tbody>
          </table>
        </div>
      </div>
    </c:otherwise>
  </c:choose>
</div>
<%@ include file="layout_footer.jspf" %>
