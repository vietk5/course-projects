<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c"  uri="jakarta.tags.core" %>
<%@ include file="../layout_admin_header.jspf" %>

<div>
  <div class="d-flex justify-content-between align-items-center mb-3">
    <h3 class="mb-0">Quản lý khách hàng</h3>
    <a class="btn btn-outline-light btn-sm" href="${pageContext.request.contextPath}/admin">← Dashboard</a>
  </div>

  <form class="card border-0 shadow-sm mb-3" method="get">
    <div class="card-body row g-2">
      <div class="col-md-6">
        <input class="form-control" name="q" placeholder="Tìm theo tên / email" value="${q}">
      </div>
      <div class="col-md-2">
        <select class="form-select" name="size">
          <c:forEach var="s" items="${[10,20,50]}">
            <option value="${s}" ${size==s?'selected':''}>${s}/trang</option>
          </c:forEach>
        </select>
      </div>
      <div class="col-md-2">
        <button class="btn btn-rog w-100" type="submit">Lọc</button>
      </div>
    </div>
  </form>

  <div class="card border-0 shadow-sm">
    <div class="table-responsive">
      <table class="table table-dark table-striped align-middle mb-0">
        <thead>
        <tr>
          <th>ID</th>
          <th>Họ tên</th>
          <th>Email</th>
          <th>Hạng</th>
          <th>Ngày đăng ký</th>
          <th>Mật khẩu</th>
        </tr>
        </thead>
        <tbody>
        <!-- items: List<Object[]>; 0=id,1=ten,2=email,3=hang,4=ngayTao -->
        <c:forEach items="${items}" var="row">
          <tr>
            <td>${row[0]}</td>
            <td><c:out value="${row[1]}"/></td>
            <td><c:out value="${row[2]}"/></td>
            <td><c:out value="${row[3]}"/></td>
            <!-- Tránh fmt:formatDate vì có thể là LocalDateTime -> in thẳng -->
            <td><c:out value="${row[4]}"/></td>
            <td>••••••</td>
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
        <a class="btn btn-outline-light btn-sm" href="?q=${q}&size=${size}&page=${prev}">«</a>
        <span class="btn btn-outline-light btn-sm disabled">${page}</span>
        <a class="btn btn-outline-light btn-sm" href="?q=${q}&size=${size}&page=${next}">»</a>
      </div>
    </div>
  </div>
</div>

<%@ include file="../layout_admin_footer.jspf" %>
