<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c"   uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>

<%@ include file="../layout_admin_header.jspf" %>

<div class="container py-4">
  <div class="d-flex align-items-center justify-content-between mb-3">
    <h2 class="text-white m-0">Chỉnh sửa sản phẩm</h2>
    <a class="btn btn-outline-light"
       href="<c:url value='/admin/products'>
               <c:param name='q' value='${q}'/>
               <c:param name='page' value='${page}'/>
               <c:param name='size' value='${size}'/>
             </c:url>">← Quay về danh sách</a>
  </div>

  <div class="card bg-dark text-white shadow-lg border-0">
    <div class="card-body">
      <form method="post" enctype="multipart/form-data"
            action="<c:url value='/admin/products/edit'/>">
        <input type="hidden" name="id"   value="${item.id}"/>
        <input type="hidden" name="q"    value="${q}"/>
        <input type="hidden" name="page" value="${page}"/>
        <input type="hidden" name="size" value="${size}"/>

        <div class="row g-4">
          <!-- Cột trái: Ảnh -->
          <div class="col-lg-4">
            <div class="mb-3">
              <label class="form-label">Ảnh hiện tại</label>
              <div class="border rounded p-2 bg-black-50 text-center">
                <a href="${pageContext.request.contextPath}/assets/img/products/${item.id}.jpg" target="_blank">
                  <img
                    id="currentImage"
                    src="${pageContext.request.contextPath}/assets/img/products/${item.id}.jpg"
                    alt="Ảnh sản phẩm"
                    class="img-fluid rounded"
                    onerror="
                      (function(img){
                        const base='${pageContext.request.contextPath}/assets/img/products/${item.id}';
                        const tried = img.dataset.tries ? img.dataset.tries.split(',') : [];
                        const exts = ['.png','.webp','.jpeg','.gif','.bmp'];
                        const next = exts.find(e => !tried.includes(e));
                        if (next) { tried.push(next); img.dataset.tries = tried.join(','); img.src = base + next; }
                        else if (img.dataset.step!=='p') { img.dataset.step='p'; img.src='${pageContext.request.contextPath}/assets/img/products/p${item.id}.jpg'; }
                        else { img.onerror=null; img.src='${pageContext.request.contextPath}/assets/img/no-image.png'; }
                      })(this);
                    "
                  />
                </a>
              </div>
            </div>

            <div class="mb-3">
              <label class="form-label">Chọn ảnh mới (tùy chọn)</label>
              <input type="file" name="hinhAnh" class="form-control" accept=".jpg,.jpeg,.png,.gif,.webp,.bmp">
              <div class="form-text text-secondary">Bỏ qua nếu muốn giữ ảnh cũ.</div>
            </div>
          </div>

          <!-- Cột phải: Thông tin sản phẩm -->
          <div class="col-lg-8">
            <div class="mb-3">
              <label class="form-label">Tên sản phẩm</label>
              <input type="text" name="tenSanPham" class="form-control" required
                     value="${item.tenSanPham}">
            </div>

            <div class="row g-3">
              <div class="col-md-6">
                <label class="form-label">Thương hiệu</label>
                <select name="thuongHieuId" class="form-select" required>
                  <c:forEach var="b" items="${brands}">
                    <option value="${b.id}"
                      <c:if test="${item.thuongHieu != null && item.thuongHieu.id == b.id}">selected</c:if>>
                      ${b.tenThuongHieu}
                    </option>
                  </c:forEach>
                </select>
              </div>
              <div class="col-md-6">
                <label class="form-label">Loại</label>
                <select name="loaiId" class="form-select" required>
                  <c:forEach var="l" items="${categories}">
                    <option value="${l.id}"
                      <c:if test="${item.loai != null && item.loai.id == l.id}">selected</c:if>>
                      ${l.tenLoai}
                    </option>
                  </c:forEach>
                </select>
              </div>
            </div>

            <div class="row g-3 mt-1">
              <div class="col-md-6">
                <label class="form-label">Giá (VND)</label>
                <input type="text" name="gia" class="form-control"
                       value="<fmt:formatNumber value='${item.gia}' type='number' groupingUsed='true'/>">
                <div class="form-text text-secondary">Có thể nhập số có dấu chấm/phẩy, hệ thống sẽ tự lọc.</div>
              </div>
              <div class="col-md-6">
                <label class="form-label">Tồn kho</label>
                <!-- CHUẨN HÓA THEO SanPham.java: soLuongTon -->
                <input type="number" name="soLuongTon" min="0" step="1"
                       class="form-control" value="${item.soLuongTon}">
              </div>
            </div>

            <div class="d-flex gap-2 mt-4">
              <button class="btn btn-warning text-dark px-4">Lưu thay đổi</button>
              <a class="btn btn-outline-secondary"
                 href="<c:url value='/admin/products'>
                         <c:param name='q' value='${q}'/>
                         <c:param name='page' value='${page}'/>
                         <c:param name='size' value='${size}'/>
                       </c:url>">Hủy</a>
            </div>
          </div>
        </div>
      </form>
    </div>
  </div>
</div>

<!-- Preview ảnh ngay khi chọn file -->
<script>
  (function () {
    const input = document.querySelector('input[name="hinhAnh"]');
    const img   = document.getElementById('currentImage');
    if (!input || !img) return;
    input.addEventListener('change', function () {
      if (this.files && this.files[0]) {
        const url = URL.createObjectURL(this.files[0]);
        img.dataset.tries = ''; img.dataset.step = '';
        img.src = url;
        img.onload = () => URL.revokeObjectURL(url);
      }
    });
  })();
</script>

<%@ include file="../layout_admin_footer.jspf" %>
