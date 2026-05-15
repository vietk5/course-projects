<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c"  uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<%@ include file="../layout_admin_header.jspf" %>

<div>
  <div class="d-flex justify-content-between align-items-center mb-3">
    <h3 class="mb-0">Quản lý đơn hàng</h3>
    <a class="btn btn-outline-light btn-sm" href="${pageContext.request.contextPath}/admin">← Dashboard</a>
  </div>

  <!-- Filters -->
  <form class="card border-0 shadow-sm mb-3" method="get">
    <div class="card-body row g-2 align-items-end">
      <div class="col-md-3">
        <label class="form-label">Từ</label>
        <input type="date" class="form-control" name="from" value="${from}">
      </div>
      <div class="col-md-3">
        <label class="form-label">Đến</label>
        <input type="date" class="form-control" name="to" value="${to}">
      </div>
      <div class="col-md-2">
        <label class="form-label">Trạng thái</label>
        <select class="form-select" name="status">
          <option value="">Tất cả</option>
          <c:forEach items="${allStatus}" var="s">
            <option value="${s}" ${status==s?'selected':''}>${s}</option>
          </c:forEach>
        </select>
      </div>
      <div class="col-md-3">
        <label class="form-label">Tìm (ID hoặc email KH)</label>
        <input class="form-control" name="q" value="${q}">
      </div>
      <div class="col-md-1">
        <button class="btn btn-rog w-100" type="submit">Lọc</button>
      </div>
    </div>
  </form>

  <div class="card border-0 shadow-sm">
    <div class="table-responsive">
      <table class="table table-dark table-striped align-middle mb-0">
        <thead>
        <tr>
          <th>Mã</th>
          <th>Ngày đặt</th>
          <th>Khách hàng</th>
          <th class="text-end">Tổng tiền</th>
          <th>Trạng thái</th>
          <th>Cập nhật</th>
        </tr>
        </thead>
        <tbody>
        <c:forEach items="${orders}" var="row">
          <c:set var="d" value="${row.d}"/>
          <tr>
            <td>${d.id}</td>
            <!-- tránh fmt:formatDate vì trường là LocalDateTime -->
            <td><c:out value="${d.ngayDatHang}"/></td>
            <td><c:out value="${d.khachHang != null ? d.khachHang.ten : '-'}"/></td>
            <td class="text-end">
              <fmt:formatNumber value="${row.total}" type="currency" currencySymbol="₫" maxFractionDigits="0"/>
            </td>
            <td><span class="badge text-bg-secondary">${d.trangThai}</span></td>
            <td>
              <form method="post" class="d-flex gap-2">
                <input type="hidden" name="id" value="${d.id}">
                <select class="form-select form-select-sm" name="newStatus">
                  <c:forEach items="${allStatus}" var="s">
                    <option value="${s}" ${d.trangThai==s?'selected':''}>${s}</option>
                  </c:forEach>
                </select>
                <button class="btn btn-sm btn-rog" type="submit">Lưu</button>
              </form>
            </td>
          </tr>
        </c:forEach>
        </tbody>
      </table>
    </div>

    <div class="card-footer d-flex justify-content-between">
      <div>Tổng: ${total}</div>
      <div class="btn-group">
        <c:set var="prev" value="${page>1 ? page-1 : 1}"/>
        <c:set var="next" value="${(page*size)<total ? page+1 : page}"/>
        <a class="btn btn-outline-light btn-sm"
           href="?q=${q}&status=${status}&from=${from}&to=${to}&size=${size}&page=${prev}">«</a>
        <span class="btn btn-outline-light btn-sm disabled">${page}</span>
        <a class="btn btn-outline-light btn-sm"
           href="?q=${q}&status=${status}&from=${from}&to=${to}&size=${size}&page=${next}">»</a>
      </div>
    </div>
  </div>
</div>

<%@ include file="../layout_admin_footer.jspf" %>
