
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c"  uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<%@ taglib prefix="fn"  uri="jakarta.tags.functions" %>

<%@ include file="layout_admin_header.jspf" %>
<c:set var="ctx" value="${pageContext.request.contextPath}" />

<div class="container my-4">
  <div class="row justify-content-center">
    <div class="col-lg-10 col-xl-8">
      <div class="card border-0 shadow-sm">
        <div class="card-body">
          <div class="d-flex align-items-center justify-content-between">
            <h5 class="mb-0">Nhập kho hàng</h5>
            <a class="btn btn-outline-light btn-sm" href="${ctx}/admin">← Dashboard</a>
          </div>
          <hr/>

          <!-- Alerts theo query param -->
          <c:if test="${param.success == 'true'}">
            <div class="alert alert-success">Đã thêm sản phẩm mới thành công.</div>
          </c:if>
          <c:if test="${param.update_success == 'true'}">
            <div class="alert alert-success">Đã cập nhật số lượng tồn kho thành công.</div>
          </c:if>
          <c:if test="${param.duplicate == 'true'}">
            <div class="alert alert-warning">
              Sản phẩm <strong>${param.tenSanPham}</strong> đã tồn tại. Bạn muốn tăng số lượng?
            </div>
          </c:if>

          <!-- Form add -->
          <form class="row g-3" method="post"
                action="${ctx}/receiving" enctype="multipart/form-data" autocomplete="off">
            <input type="hidden" name="action" value="add"/>

            <div class="col-md-6">
              <label class="form-label">Tên sản phẩm</label>
              <input class="form-control" name="tenSanPham" required/>
            </div>

            <div class="col-md-3">
              <label class="form-label">Thương hiệu</label>
              <select class="form-select" name="thuongHieu" required>
                <option value="">-- Chọn --</option>
                <c:forEach var="b" items="${brands}">
                  <option value="${b}">${b}</option>
                </c:forEach>
              </select>
            </div>

            <div class="col-md-3">
              <label class="form-label">Loại sản phẩm</label>
              <select class="form-select" name="loaiSanPham" required>
                <option value="">-- Chọn --</option>
                <c:forEach var="cat" items="${categories}">
                  <option value="${cat}">${cat}</option>
                </c:forEach>
              </select>
            </div>

            <div class="col-md-6">
              <label class="form-label">Giá (VND)</label>
              <input id="gia" name="gia" class="form-control" placeholder="VD: 12.500.000" required
                     oninput="this.value=this.value.replace(/[^0-9.]/g,'')">
              <div class="form-text">Nhập số VND (không ký tự chữ).</div>
            </div>

            <div class="col-md-3">
              <label class="form-label">Số lượng</label>
              <input class="form-control" type="number" name="soLuong" min="1" value="1" required>
            </div>

            <div class="col-md-3">
              <label class="form-label">Hình ảnh</label>
              <input class="form-control" type="file" name="hinhAnh" accept="image/*">
              <div class="form-text">Tùy chọn (JPG/PNG, &lt; 2MB).</div>
            </div>

            <div class="col-12">
              <label class="form-label">Mô tả ngắn</label>
              <textarea class="form-control" name="moTaNgan" rows="3"></textarea>
            </div>

            <div class="col-12 d-flex gap-2">
              <button class="btn btn-rog" type="submit">Gửi</button>
              <button class="btn btn-outline-light" type="reset">Xóa</button>
            </div>
          </form>

          <!-- Khối xác nhận tăng số lượng khi duplicate -->
          <c:if test="${param.duplicate == 'true'}">
            <hr/>
            <form class="d-flex flex-wrap gap-2 align-items-end" method="post" action="${ctx}/receiving">
              <input type="hidden" name="action" value="confirm_update"/>
              <input type="hidden" name="tenSanPham" value="${param.tenSanPham}"/>
              <div>
                <label class="form-label">Tăng thêm</label>
                <input class="form-control" type="number" name="soLuong" min="1" value="1" required>
              </div>
              <button class="btn btn-warning" type="submit">Xác nhận cập nhật</button>
            </form>
          </c:if>

        </div>
      </div>
    </div>
  </div>
</div>

<%@ include file="layout_footer.jspf" %>

