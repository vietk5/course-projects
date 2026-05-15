<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c"  uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<%@ taglib prefix="fn"  uri="jakarta.tags.functions" %>
<%@ include file="../layout_admin_header.jspf" %>

<div>
  <div class="d-flex justify-content-between align-items-center mb-3">
    <h3 class="mb-0">Quản lý sản phẩm</h3>
    <a class="btn btn-outline-light btn-sm" href="${pageContext.request.contextPath}/admin">← Dashboard</a>
  </div>

  <!-- Alert sau khi xóa/cập nhật -->
  <c:if test="${param.deleted == '1'}">
    <div class="alert alert-success">Đã xóa sản phẩm.</div>
  </c:if>
  <c:if test="${param.deleted == '0'}">
    <div class="alert alert-warning">Không thể xóa sản phẩm (có thể đang được tham chiếu).</div>
  </c:if>
  <c:if test="${param.updated == '1'}">
    <div class="alert alert-success">Đã cập nhật sản phẩm.</div>
  </c:if>
  <c:if test="${param.updated == '0'}">
    <div class="alert alert-warning">Cập nhật thất bại.</div>
  </c:if>

  <form class="card border-0 shadow-sm mb-3" method="get">
    <div class="card-body row g-2">
      <div class="col-md-6">
        <input class="form-control" name="q"
               placeholder="Tìm theo tên / thương hiệu / loại"
               value="${q}">
      </div>
      <div class="col-md-2">
        <select class="form-select" name="size">
          <option value="10" ${size==10?'selected':''}>10/trang</option>
          <option value="20" ${size==20?'selected':''}>20/trang</option>
          <option value="50" ${size==50?'selected':''}>50/trang</option>
        </select>
      </div>
      <div class="col-md-2">
        <button class="btn btn-rog w-100" type="submit">Lọc</button>
      </div>
    </div>
  </form>

  <div class="card border-0 shadow-sm">
    <div class="table-responsive">
      <!-- thêm table-bordered để có viền dọc -->
      <table class="table table-dark table-striped table-bordered align-middle mb-0">
        <thead>
        <tr>
          <th>STT</th>
          <th>Tên sản phẩm</th>
          <th>Thương hiệu</th>
          <th>Loại</th>
          <th class="text-end">Giá</th>
          <th class="text-end">Tồn kho</th>
          <th>Ngày nhập/cập nhật</th>
          <th>Thao tác</th>
        </tr>
        </thead>
        <tbody>
        <c:if test="${empty items}">
          <tr>
            <td colspan="8" class="text-center py-4">Không có sản phẩm</td>
          </tr>
        </c:if>

        <c:forEach items="${items}" var="p" varStatus="st">
          <tr>
            <td><c:out value="${offset + st.index + 1}"/></td>
            <td><c:out value="${p.tenSanPham}"/></td>
            <td><c:out value="${p.thuongHieu != null ? p.thuongHieu.tenThuongHieu : '-'}"/></td>
            <td><c:out value="${p.loai != null ? p.loai.tenLoai : '-'}"/></td>
            <td class="text-end">
              <fmt:formatNumber value="${p.gia}" type="currency" currencySymbol="₫" maxFractionDigits="0"/>
            </td>
            <td class="text-end"><c:out value="${empty p.soLuongTon ? 0 : p.soLuongTon}"/></td>
            <td><c:out value="${p.ngayCapPhat}"/></td>

            <td class="text-nowrap">
              <!-- Nút Sửa -->
              <a class="btn btn-sm btn-warning me-1"
                 href="${pageContext.request.contextPath}/admin/products/edit?id=${p.id}&q=${q}&page=${page}&size=${size}">
                Sửa
              </a>

              <!-- Nút Xóa: mở modal -->
              <form method="post" action="${pageContext.request.contextPath}/admin/products" class="d-inline">
                <input type="hidden" name="action" value="delete"/>
                <input type="hidden" name="id" value="${p.id}"/>
                <input type="hidden" name="q" value="${q}"/>
                <input type="hidden" name="page" value="${page}"/>
                <input type="hidden" name="size" value="${size}"/>
                <button type="button"
                        class="btn btn-sm btn-danger"
                        data-id="${p.id}"
                        data-name="${fn:escapeXml(p.tenSanPham)}"
                        onclick="openDeleteModal(this.dataset.id, this.dataset.name)">
                  Xóa
                </button>
              </form>
            </td>
          </tr>
        </c:forEach>
        </tbody>
      </table>
    </div>

    <div class="card-footer d-flex justify-content-between">
      <div>Tổng: <c:out value="${total}"/></div>

      <!-- Tính trang -->
      <c:set var="totalPages" value="${(total + size - 1) / size}" />
      <c:if test="${totalPages < 1}">
        <c:set var="totalPages" value="1"/>
      </c:if>
      <c:set var="prev" value="${page>1 ? page-1 : 1}"/>
      <c:set var="next" value="${page<totalPages ? page+1 : totalPages}"/>

      <!-- Link phân trang bằng c:url -->
      <c:url var="uFirst" value="">
        <c:param name="q" value="${q}"/>
        <c:param name="size" value="${size}"/>
        <c:param name="page" value="1"/>
      </c:url>
      <c:url var="uPrev" value="">
        <c:param name="q" value="${q}"/>
        <c:param name="size" value="${size}"/>
        <c:param name="page" value="${prev}"/>
      </c:url>
      <c:url var="uNext" value="">
        <c:param name="q" value="${q}"/>
        <c:param name="size" value="${size}"/>
        <c:param name="page" value="${next}"/>
      </c:url>
      <c:url var="uLast" value="">
        <c:param name="q" value="${q}"/>
        <c:param name="size" value="${size}"/>
        <c:param name="page" value="${totalPages}"/>
      </c:url>

      <div class="btn-group">
        <a class="btn btn-outline-light btn-sm" href="${uFirst}">«</a>
        <a class="btn btn-outline-light btn-sm" href="${uPrev}">‹</a>
        <span class="btn btn-outline-light btn-sm disabled">${page} / ${totalPages}</span>
        <a class="btn btn-outline-light btn-sm" href="${uNext}">›</a>
        <a class="btn btn-outline-light btn-sm" href="${uLast}">»</a>
      </div>
    </div>
  </div>
</div>

<!-- Modal Xác nhận xóa -->
<div class="modal fade" id="deleteConfirmModal" tabindex="-1">
  <div class="modal-dialog modal-dialog-centered">
    <div class="modal-content bg-dark text-light border-0">
      <div class="modal-header border-0">
        <h5 class="modal-title">Xác nhận xoá</h5>
        <button type="button" class="btn-close btn-close-white" data-bs-dismiss="modal"></button>
      </div>
      <div class="modal-body">
        <p>Bạn có chắc muốn xoá sản phẩm:</p>
        <p class="fw-bold text-warning" id="deleteProductName">[Tên sản phẩm]</p>
      </div>
      <div class="modal-footer border-0">
        <form id="deleteForm" method="post" action="${pageContext.request.contextPath}/admin/products">
          <input type="hidden" name="action" value="delete"/>
          <input type="hidden" name="id" id="deleteProductId"/>
          <input type="hidden" name="q" value="${q}"/>
          <input type="hidden" name="page" value="${page}"/>
          <input type="hidden" name="size" value="${size}"/>
          <button type="submit" class="btn btn-danger">Xác nhận</button>
          <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Huỷ</button>
        </form>
      </div>
    </div>
  </div>
</div>

<!-- Popup tick xanh -->
<div id="successPopup" class="alert-popup">
  <i class="bi bi-check-circle-fill me-2"></i> Đã xoá sản phẩm thành công!
</div>

<!-- JS: đảm bảo chạy sau khi DOM & Bootstrap sẵn sàng -->
<script>
  window.addEventListener('DOMContentLoaded', function () {
    const modalEl = document.getElementById('deleteConfirmModal');
    if (!modalEl || !window.bootstrap) return;

    const deleteModal = new bootstrap.Modal(modalEl);

    window.openDeleteModal = function(id, name) {
      document.getElementById('deleteProductId').value = id;
      document.getElementById('deleteProductName').textContent = name || '';
      deleteModal.show();
    };

    const urlParams = new URLSearchParams(window.location.search);
    if (urlParams.get("deleted") === "1") {
      const popup = document.getElementById("successPopup");
      if (popup) {
        popup.classList.add("show");
        setTimeout(() => popup.classList.remove("show"), 3000);
      }
    }
  });
</script>

<%@ include file="../layout_admin_footer.jspf" %>
