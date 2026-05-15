<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c"  uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<%@ taglib prefix="fn"  uri="jakarta.tags.functions" %>
<%@ include file="../layout_admin_header.jspf" %>

<c:set var="ctx" value="${pageContext.request.contextPath}" />

<!-- Tiêu đề + action nhanh -->
<div class="d-flex align-items-center justify-content-between mb-3">
  <h3 class="mb-0">Bảng điều khiển Admin</h3>
  <div class="d-flex flex-wrap gap-2">
    <a class="btn btn-outline-light btn-sm" href="${ctx}/admin/revenue">Doanh thu</a>
    <a class="btn btn-outline-light btn-sm" href="${ctx}/admin/orders">Đơn hàng</a>
    <a class="btn btn-outline-light btn-sm" href="${ctx}/admin/products">Sản phẩm</a>
    <a class="btn btn-outline-light btn-sm" href="${ctx}/admin/customers">Khách hàng</a>
    <a class="btn btn-outline-light btn-sm" href="${ctx}/admin/promos">
      <i class="bi bi-percent me-1"></i> Khuyến mãi
    </a>
    <a class="btn btn-rog btn-sm" href="${ctx}/receiving">
      <i class="bi bi-box-arrow-in-down me-1"></i> Nhập hàng
    </a>
  </div>
</div>

<!-- KPI -->
<div class="row g-3 mb-3">
  <div class="col-md-4">
    <div class="card border-0 shadow-sm">
      <div class="card-body">
        <div class="text-secondary small">Khách hàng</div>
        <div class="fs-4 fw-bold"><c:out value="${totalCustomers}"/></div>
      </div>
    </div>
  </div>
  <div class="col-md-4">
    <div class="card border-0 shadow-sm">
      <div class="card-body">
        <div class="text-secondary small">Sản phẩm</div>
        <div class="fs-4 fw-bold"><c:out value="${totalProducts}"/></div>
      </div>
    </div>
  </div>
  <div class="col-md-4">
    <div class="card border-0 shadow-sm">
      <div class="card-body">
        <div class="text-secondary small">Tồn kho</div>
        <div class="fs-4 fw-bold"><c:out value="${totalStock}"/></div>
      </div>
    </div>
  </div>
</div>

<!-- Trạng thái đơn (badge + donut chart) -->
<div class="row g-3 mb-3">
  <div class="col-md-6">
    <div class="card border-0 shadow-sm h-100">
      <div class="card-body">
        <h6 class="mb-3">Tổng quan trạng thái đơn</h6>
        <div class="d-flex flex-wrap gap-2 mb-3">
          <c:forEach var="e" items="${statusCounts}">
            <span class="badge text-bg-dark border">
              <strong><c:out value="${e.key}"/></strong>:
              <span class="ms-1"><c:out value="${e.value}"/></span>
            </span>
          </c:forEach>
          <c:if test="${empty statusCounts}">
            <span class="text-muted">Chưa có đơn hàng.</span>
          </c:if>
        </div>
        <canvas id="statusChart" height="40"></canvas>
      </div>
    </div>
  </div>

  <!-- Hành động nhanh + Nhập hàng -->
  <div class="col-md-6">
    <div class="card border-0 shadow-sm h-100">
      <div class="card-body d-flex flex-column">
        <h6 class="mb-3">Hành động nhanh</h6>
        <div class="row g-2">
          <div class="col-6">
            <a class="btn w-100 btn-outline-light" href="${ctx}/admin/products">
              <i class="bi bi-cpu me-1"></i> Quản lý sản phẩm
            </a>
          </div>
          <div class="col-6">
            <a class="btn w-100 btn-outline-light" href="${ctx}/admin/orders">
              <i class="bi bi-receipt me-1"></i> Quản lý đơn hàng
            </a>
          </div>
          <div class="col-6">
            <a class="btn w-100 btn-outline-light" href="${ctx}/admin/customers">
              <i class="bi bi-people me-1"></i> Quản lý khách hàng
            </a>
          </div>
          <div class="col-6">
            <a class="btn w-100 btn-outline-light" href="${ctx}/admin/promos">
              <i class="bi bi-percent me-1"></i> Khuyến mãi
            </a>
          </div>
          <div class="col-12">
            <a class="btn w-100 btn-rog" href="${ctx}/receiving">
              <i class="bi bi-box-arrow-in-down me-1"></i> Nhập hàng
            </a>
          </div>
        </div>
        <div class="mt-auto small text-muted pt-3">
          * Gợi ý: thêm hàng mới hoặc tăng tồn kho nhanh ở mục “Nhập hàng”.
        </div>
      </div>
    </div>
  </div>
</div>

<!-- Hàng sắp hết -->
<div class="card border-0 shadow-sm mb-3">
  <div class="card-body">
    <h6 class="mb-3">Hàng sắp hết (Top 10)</h6>
    <div class="table-responsive">
      <table class="table table-dark table-striped align-middle">
        <thead>
          <tr>
            <th style="width:60%">Sản phẩm</th>
            <th class="text-end" style="width:20%">Tồn</th>
            <th class="text-end" style="width:20%">Nhập kho</th>
          </tr>
        </thead>
        <tbody>
          <c:forEach var="p" items="${lowStocks}">
            <tr>
              <td><c:out value="${p.tenSanPham}"/></td>
              <td class="text-end"><c:out value="${p.soLuongTon}"/></td>
              <td class="text-Send">
                <a class="btn btn-sm btn-outline-light"
                   href="${ctx}/receiving?duplicate=true&tenSanPham=${fn:escapeXml(p.tenSanPham)}">
                  +1
                </a>
              </td>
            </tr>
          </c:forEach>
          <c:if test="${empty lowStocks}">
            <tr><td colspan="3" class="text-center text-muted">Không có sản phẩm nào sắp hết.</td></tr>
          </c:if>
        </tbody>
      </table>
    </div>
  </div>
</div>

<!-- Đơn gần đây -->
<div class="card border-0 shadow-sm mb-3">
  <div class="card-body">
    <div class="d-flex align-items-center justify-content-between">
      <h6 class="mb-0">Đơn hàng gần đây</h6>
      <div class="d-flex gap-2">
        <a class="btn btn-sm btn-outline-light" href="${ctx}/admin/orders">Xem tất cả</a>
      </div>
    </div>
    <div class="table-responsive mt-3">
      <table class="table table-dark table-striped align-middle">
        <thead>
          <tr>
            <th style="width:15%">Mã đơn</th>
            <th style="width:35%">Ngày đặt</th>
            <th style="width:20%">Trạng thái</th>
            <th class="text-end" style="width:30%"></th>
          </tr>
        </thead>
        <tbody>
          <c:forEach var="o" items="${orders}">
            <tr>
              <td>#<c:out value="${o.id}"/></td>
              <td>
                <c:choose>
                  <c:when test="${not empty o.ngayDatHang}">
                    <c:out value="${o.ngayDatHang}"/>
                  </c:when>
                  <c:otherwise>-</c:otherwise>
                </c:choose>
              </td>
              <td><span class="badge text-bg-light border"><c:out value="${o.trangThai}"/></span></td>
              <td class="text-end">
                <a class="btn btn-sm btn-outline-light" href="${ctx}/admin/orders?status=${o.trangThai}">Lọc theo trạng thái</a>
              </td>
            </tr>
          </c:forEach>
          <c:if test="${empty orders}">
            <tr><td colspan="4" class="text-center text-muted">Chưa có đơn nào.</td></tr>
          </c:if>
        </tbody>
      </table>
    </div>
  </div>
</div>

<!-- Chart.js cho biểu đồ trạng thái -->
<script src="https://cdn.jsdelivr.net/npm/chart.js@4.4.1/dist/chart.umd.min.js"></script>
<script>
  (function () {
    const labels = [
      <c:forEach var="e" items="${statusCounts}" varStatus="st">
        ${st.first ? "" : ","}'${fn:escapeXml(e.key)}'
      </c:forEach>
    ];
    const values = [
      <c:forEach var="e" items="${statusCounts}" varStatus="st">
        ${st.first ? "" : ","}${e.value}
      </c:forEach>
    ];
    const el = document.getElementById('statusChart');
    if (el && values.length > 0) {
      new Chart(el, {
        type: 'doughnut',
        data: { labels, datasets: [{ data: values }] },
        options: { responsive: true, plugins: { legend: { position: 'bottom' } } }
      });
    }
  })();
</script>

<%@ include file="../layout_admin_footer.jspf" %> 