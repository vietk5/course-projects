<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c"  uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<%@ taglib prefix="fn"  uri="jakarta.tags.functions" %>
<%@ include file="../layout_admin_header.jspf" %>

<div>
  <div class="d-flex align-items-center justify-content-between mb-3">
    <h3 class="mb-0">Quản lý doanh thu</h3>
    <a class="btn btn-outline-light btn-sm" href="${pageContext.request.contextPath}/admin">← Dashboard</a>
  </div>

  <!-- KPIs -->
  <div class="row g-3 mb-3">
    <div class="col-md-4">
      <div class="card border-0 shadow-sm">
        <div class="card-body">
          <div class="text-secondary small">Tổng doanh thu</div>
          <div class="fs-4 fw-bold">
            <fmt:formatNumber value="${totalRevenue}" type="currency" currencySymbol="₫" maxFractionDigits="0"/>
          </div>
        </div>
      </div>
    </div>
    <div class="col-md-4">
      <div class="card border-0 shadow-sm">
        <div class="card-body">
          <div class="text-secondary small">Số đơn</div>
          <div class="fs-4 fw-bold"><c:out value="${orderCount}"/></div>
        </div>
      </div>
    </div>
    <div class="col-md-4">
      <div class="card border-0 shadow-sm">
        <div class="card-body">
          <div class="text-secondary small">AOV (Doanh thu/đơn)</div>
          <div class="fs-4 fw-bold">
            <fmt:formatNumber value="${avgOrder}" type="currency" currencySymbol="₫" maxFractionDigits="0"/>
          </div>
        </div>
      </div>
    </div>
  </div>

  <!-- Bộ lọc -->
  <form class="card border-0 shadow-sm mb-3" method="get" action="${pageContext.request.contextPath}/admin/revenue">
    <div class="card-body row g-3 align-items-end">
      <div class="col-sm-3">
        <label class="form-label">Từ ngày</label>
        <input type="date" class="form-control" name="from" value="${from}"/>
      </div>
      <div class="col-sm-3">
        <label class="form-label">Đến ngày</label>
        <input type="date" class="form-control" name="to" value="${to}"/>
      </div>
      <div class="col-sm-3">
        <label class="form-label">Nhóm theo</label>
        <select class="form-select" name="period">
          <option value="day"     ${period=='day'     ? 'selected' : ''}>Ngày</option>
          <option value="week"    ${period=='week'    ? 'selected' : ''}>Tuần</option>
          <option value="month"   ${period=='month'   ? 'selected' : ''}>Tháng</option>
          <option value="quarter" ${period=='quarter' ? 'selected' : ''}>Quý</option>
          <option value="year"    ${period=='year'    ? 'selected' : ''}>Năm</option>
        </select>
      </div>
      <div class="col-sm-3 d-flex gap-2">
        <button class="btn btn-rog flex-fill" type="submit">Lọc</button>
        <a class="btn btn-outline-light"
           href="${pageContext.request.contextPath}/admin/revenue?period=${period}&from=${from}&to=${to}&export=csv">Export CSV</a>
      </div>
    </div>
  </form>

  <!-- Chart -->
  <div class="card border-0 shadow-sm mb-3">
    <div class="card-body">
      <canvas id="revChart" height="100"></canvas>
    </div>
  </div>

  <!-- Bảng chi tiết -->
  <div class="card border-0 shadow-sm">
    <div class="card-body">
      <h6 class="mb-3">Chi tiết</h6>
      <div class="table-responsive">
        <table class="table table-dark table-striped align-middle mb-0">
          <thead>
          <tr>
            <th>Kỳ</th>
            <th class="text-end">Doanh thu</th>
          </tr>
          </thead>
          <tbody>
          <c:if test="${not empty labels}">
            <c:forEach items="${labels}" var="lb" varStatus="s">
              <tr>
                <td><c:out value="${lb}"/></td>
                <td class="text-end">
                  <fmt:formatNumber value="${values[s.index]}" type="currency" currencySymbol="₫" maxFractionDigits="0"/>
                </td>
              </tr>
            </c:forEach>
          </c:if>
          <c:if test="${empty labels}">
            <tr>
              <td colspan="2" class="text-center text-muted">Không có dữ liệu doanh thu trong khoảng lọc.</td>
            </tr>
          </c:if>
          </tbody>
        </table>
      </div>
    </div>
  </div>
</div>

<!-- Chart.js -->
<script src="https://cdn.jsdelivr.net/npm/chart.js@4.4.1/dist/chart.umd.min.js"></script>
<script>
  // Build dữ liệu cho biểu đồ
  const labels = [
    <c:forEach var="l" items="${labels}" varStatus="st">${st.first?'':','}'${fn:escapeXml(l)}'</c:forEach>
  ];
  const values = [
    <c:forEach var="v" items="${values}" varStatus="st">${st.first?'':','}${v}</c:forEach>
  ];

  const ctx = document.getElementById('revChart');
  new Chart(ctx, {
    type: 'line',
    data: {
      labels: labels,
      datasets: [{
        label: 'Doanh thu',
        data: values,
        tension: 0.3,
        fill: true
      }]
    },
    options: {
      responsive: true,
      interaction: { mode: 'index', intersect: false },
      plugins: {
        legend: { display: false },
        tooltip: {
          callbacks: {
            label: (ctx) => new Intl.NumberFormat('vi-VN').format(ctx.parsed.y) + ' ₫'
          }
        }
      },
      scales: {
        y: {
          ticks: {
            callback: (v) => new Intl.NumberFormat('vi-VN', { notation: 'compact' }).format(v)
          }
        }
      }
    }
  });
</script>

<%@ include file="../layout_admin_footer.jspf" %>
