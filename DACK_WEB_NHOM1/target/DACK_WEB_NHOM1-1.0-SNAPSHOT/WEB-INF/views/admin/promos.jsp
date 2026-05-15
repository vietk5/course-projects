<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<%@ include file="/WEB-INF/views/layout_admin_header.jspf" %>

<div class="container my-4">
    <div class="d-flex justify-content-between align-items-center mb-3">
        <h3 class="mb-0">${empty promo.id ? 'Tạo mã khuyến mãi' : 'Chỉnh sửa mã khuyến mãi'}</h3>
        <a class="btn btn-outline-light btn-sm" href="${pageContext.request.contextPath}/admin">← Dashboard</a>
    </div>

    <c:if test="${param.ok == '1'}"><div class="alert alert-success">Thao tác thành công.</div></c:if>
    <c:if test="${param.err == '1'}"><div class="alert alert-danger">Có lỗi xảy ra, vui lòng kiểm tra lại thông tin.</div></c:if>

    <div class="card border-0 shadow-sm mb-4">
        <form method="post" action="${pageContext.request.contextPath}/admin/promos">
            <div class="card-header"><h6 class="mb-0">Thông tin mã</h6></div>
            <div class="card-body">
                <c:if test="${not empty promo.id}"><input type="hidden" name="id" value="${promo.id}"/></c:if>
                <div class="row g-3">
                    <div class="col-md-6"><label class="form-label">Mã</label><input class="form-control" name="code" value="${promo.ma}" required/></div>
                    <div class="col-md-6"><label class="form-label">Kiểu</label>
                        <select class="form-select" name="kieu">
                            <option value="PHAN_TRAM" ${promo.kieu.name() == 'PHAN_TRAM' ? 'selected' : ''}>Phần trăm</option>
                            <option value="TIEN_MAT" ${promo.kieu.name() == 'TIEN_MAT' ? 'selected' : ''}>Tiền mặt</option>
                        </select>
                    </div>
                    <div class="col-md-4"><label class="form-label">Giá trị</label><input class="form-control" type="number" step="any" name="value" value="${promo.giaTri}" required/></div>
                    <div class="col-md-4"><label class="form-label">Giảm tối đa</label><input class="form-control" type="number" step="any" name="maxOff" value="${promo.giamToiDa}"/></div>
                    <div class="col-md-4"><label class="form-label">Đơn tối thiểu</label><input class="form-control" type="number" step="any" name="minOrder" value="${promo.donToiThieu}"/></div>
                    <div class="col-md-6"><label class="form-label">Ngày bắt đầu</label><input class="form-control" type="date" name="start" value="${not empty promo.ngayBatDau ? promo.ngayBatDau.toLocalDate() : ''}"/></div>
                    <div class="col-md-6"><label class="form-label">Ngày kết thúc</label><input class="form-control" type="date" name="end" value="${not empty promo.ngayKetThuc ? promo.ngayKetThuc.toLocalDate() : ''}"/></div>
                    <div class="col-12"><label class="form-label">Mô tả</label><textarea class="form-control" name="desc">${promo.moTa}</textarea></div>
                    <div class="col-12"><div class="form-check form-switch"><input class="form-check-input" type="checkbox" name="active" id="active" ${empty promo.id or promo.active ? 'checked' : ''}><label class="form-check-label" for="active">Kích hoạt</label></div></div>
                    <div class="col-12"><label class="form-label">Phạm vi áp dụng</label>
                        <select class="form-select" name="applyScope" id="applyScope">
                            <option value="ALL" ${promo.apDungToanBo ? 'selected' : ''}>Tất cả sản phẩm</option>
                            <option value="CATEGORY" ${not empty promo.loaiApDung ? 'selected' : ''}>Theo loại sản phẩm</option>
                            <option value="PRODUCT" ${not empty promo.sanPhamApDung ? 'selected' : ''}>Sản phẩm cụ thể</option>
                        </select>
                    </div>
                    <div class="col-12" id="scopeDetails" style="display: none;">
                        <hr/>
                        <div id="categoryScope" style="display: none;">
                            <label class="form-label">Chọn loại sản phẩm</label>
                            <select class="form-select" name="categoryIds" multiple size="5">
                                <c:forEach var="cat" items="${categoryList}"><option value="${cat.id}" <c:forEach var="p" items="${promo.loaiApDung}"><c:if test="${p.id == cat.id}">selected</c:if></c:forEach>>${cat.tenLoai}</option></c:forEach>
                            </select>
                        </div>
                        <div id="productScope" style="display: none;">
                            <label class="form-label">Chọn sản phẩm</label>
                            <select class="form-select" name="productIds" multiple size="5">
                                <c:forEach var="sp" items="${productList}"><option value="${sp.id}" <c:forEach var="p" items="${promo.sanPhamApDung}"><c:if test="${p.id == sp.id}">selected</c:if></c:forEach>>${sp.tenSanPham}</option></c:forEach>
                            </select>
                        </div>
                    </div>
                </div>
            </div>
            <div class="card-footer text-end">
                <c:if test="${not empty promo.id}"><a href="${pageContext.request.contextPath}/admin/promos" class="btn btn-secondary">Hủy</a></c:if>
                <button class="btn btn-primary" type="submit">${empty promo.id ? 'Tạo mã' : 'Lưu thay đổi'}</button>
            </div>
        </form>
    </div>

    <div class="card border-0 shadow-sm">
    <div class="card-header"><h6 class="mb-0">Danh sách mã hiện có</h6></div>
    <div class="table-responsive">
        <table class="table table-dark table-striped align-middle mb-0">
            <thead>
                <tr>
                    <th>Mã</th>
                    <th>Giá trị</th>
                    <th>Ngày bắt đầu</th>
                    <th>Ngày kết thúc</th>
                    <th>Phạm vi áp dụng</th>
                    <th>Trạng thái</th>
                    <th class="text-end">Hành động</th>
                </tr>
            </thead>
            <tbody>
                <c:choose>
                    <c:when test="${empty promoList}">
                        <tr><td colspan="7" class="text-center">Không có mã khuyến mãi nào.</td></tr>
                    </c:when>
                    <c:otherwise>
                        <c:forEach items="${promoList}" var="p">
                            <tr>
                                <td class="fw-bold">${p.ma}</td>
                                <td>
                                    <c:choose>
                                        <c:when test="${p.kieu.name() == 'PHAN_TRAM'}"><fmt:formatNumber value="${p.giaTri}" type="number"/>%</c:when>
                                        <c:otherwise><fmt:formatNumber value="${p.giaTri}" type="currency" currencySymbol="₫"/></c:otherwise>
                                    </c:choose>
                                </td>
                                <td>
                                    <c:choose>
                                        <c:when test="${not empty p.ngayBatDau}">
                                            ${p.ngayBatDau.toLocalDate()}
                                        </c:when>
                                        <c:otherwise>Chưa xác định</c:otherwise>
                                    </c:choose>
                                </td>
                                <td>
                                    <c:choose>
                                        <c:when test="${not empty p.ngayKetThuc}">
                                            ${p.ngayKetThuc.toLocalDate()}
                                        </c:when>
                                        <c:otherwise>Chưa xác định</c:otherwise>
                                    </c:choose>
                                </td>
                                <td>
                                    <c:choose>
                                        <c:when test="${p.apDungToanBo}">Tất cả sản phẩm</c:when>
                                        <c:when test="${not empty p.loaiApDung}">Theo loại sản phẩm (${fn:length(p.loaiApDung)} loại)</c:when>
                                        <c:when test="${not empty p.sanPhamApDung}">Sản phẩm cụ thể (${fn:length(p.sanPhamApDung)} sản phẩm)</c:when>
                                        <c:otherwise>Chưa xác định</c:otherwise>
                                    </c:choose>
                                </td>
                                <td><span class="badge ${p.active ? 'text-bg-success' : 'text-bg-secondary'}">${p.active ? 'Đang bật' : 'Đang tắt'}</span></td>
                                <td class="text-end">
                                    <a class="btn btn-sm btn-outline-warning" href="?action=edit&id=${p.id}">Sửa</a>
                                    <a class="btn btn-sm btn-outline-light" href="?action=toggle&id=${p.id}">${p.active ? 'Tắt' : 'Bật'}</a>
                                    <a class="btn btn-sm btn-outline-danger" href="?action=delete&id=${p.id}" onclick="return confirm('Xóa mã này?')">Xóa</a>
                                </td>
                            </tr>
                        </c:forEach>
                    </c:otherwise>
                </c:choose>
            </tbody>
        </table>
    </div>
</div>

<script>
    document.addEventListener("DOMContentLoaded", function() {
        const applyScope = document.getElementById('applyScope');
        const scopeDetails = document.getElementById('scopeDetails');
        const categoryScope = document.getElementById('categoryScope');
        const productScope = document.getElementById('productScope');
        function toggleScopeDetails() {
            const selectedValue = applyScope.value;
            if (selectedValue === 'CATEGORY') {
                scopeDetails.style.display = 'block';
                categoryScope.style.display = 'block';
                productScope.style.display = 'none';
            } else if (selectedValue === 'PRODUCT') {
                scopeDetails.style.display = 'block';
                categoryScope.style.display = 'none';
                productScope.style.display = 'block';
            } else { // ALL
                scopeDetails.style.display = 'none';
            }
        }
        applyScope.addEventListener('change', toggleScopeDetails);
        toggleScopeDetails(); // Run on page load
    });
</script>

<%@ include file="/WEB-INF/views/layout_admin_footer.jspf" %> 
