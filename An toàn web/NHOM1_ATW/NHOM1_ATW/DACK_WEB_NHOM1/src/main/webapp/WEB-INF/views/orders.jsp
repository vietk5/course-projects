
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="java.time.format.DateTimeFormatter" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>

<c:set var="cp" value="${pageContext.request.contextPath}" />
<c:set var="pageTitle" value="Đơn hàng của tôi - ElectroMart" scope="request"/>
<c:set var="dateFormatter" value="<%= DateTimeFormatter.ofPattern(\"dd/MM/yyyy HH:mm\") %>" />

<%@ include file="layout_header.jspf" %>

<%-- **[CODE MỚI ĐƯỢC THÊM VÀO]** --%>
<c:if test="${param.cancel_success == 'true'}">
    <div class="alert alert-success alert-dismissible fade show" role="alert">
        <i class="bi bi-check-circle-fill me-2"></i>Đơn hàng của bạn đã được hủy thành công.
        <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
    </div>
</c:if>
<c:if test="${param.cancel_error == 'true'}">
    <div class="alert alert-danger alert-dismissible fade show" role="alert">
        <i class="bi bi-exclamation-triangle-fill me-2"></i>Không thể hủy đơn hàng này hoặc đơn hàng không còn ở trạng thái "Mới".
        <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
    </div>
</c:if>

<c:if test="${param.checkout_success == 'true'}">
    <div class="alert alert-success alert-dismissible fade show" role="alert">
        <h5 class="alert-heading">
            <i class="bi bi-check-circle-fill me-2"></i>Đặt hàng thành công!
        </h5>
        <p class="mb-2">
            Đơn hàng của bạn đã được tạo thành công. 
            Chúng tôi sẽ xử lý và giao hàng trong vòng 2-3 ngày làm việc.
        </p>
        <hr>
        <p class="mb-0">
            <i class="bi bi-envelope-check me-2"></i>
            <strong>Email xác nhận đã được gửi đến:</strong> 
            <span class="text-primary">${sessionScope.user.email}</span>
        </p>
        <p class="mb-0 mt-1 small">
            <i class="bi bi-info-circle me-1"></i>
            Vui lòng kiểm tra hộp thư (bao gồm cả thư mục Spam/Junk).
        </p>
        <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
    </div>
</c:if>

<div class="row">
    <div class="col-12">
        <div class="d-flex justify-content-between align-items-center mb-4">
            <h2 class="text-white">
                <i class="bi bi-box-seam me-2"></i>Đơn hàng của tôi
            </h2>
            <a href="${cp}/home" class="btn btn-outline-light">
                <i class="bi bi-house me-1"></i>Về trang chủ
            </a>
        </div>

        <div class="alert alert-info mb-4">
            <i class="bi bi-info-circle me-2"></i>
            Tổng số đơn hàng: <strong>${totalOrders}</strong>
        </div>

        <div class="card bg-dark border-secondary mb-4">
            <div class="card-body">
                <h6 class="text-white mb-3">
                    <i class="bi bi-funnel me-2"></i>Lọc theo trạng thái
                </h6>
                <div class="btn-group" role="group">
                    <a href="${cp}/orders" class="btn ${empty filterStatus ? 'btn-primary' : 'btn-outline-secondary'}">
                        Tất cả
                    </a>
                    <a href="${cp}/orders?status=MOI" class="btn ${filterStatus == 'MOI' ? 'btn-primary' : 'btn-outline-secondary'}">
                        Mới
                    </a>
                    <a href="${cp}/orders?status=DANG_XU_LY" class="btn ${filterStatus == 'DANG_XU_LY' ? 'btn-primary' : 'btn-outline-secondary'}">
                        Đang xử lý
                    </a>
                    <a href="${cp}/orders?status=DANG_GIAO" class="btn ${filterStatus == 'DANG_GIAO' ? 'btn-primary' : 'btn-outline-secondary'}">
                        Đang giao
                    </a>
                    <a href="${cp}/orders?status=HOAN_THANH" class="btn ${filterStatus == 'HOAN_THANH' ? 'btn-primary' : 'btn-outline-secondary'}">
                        Hoàn thành
                    </a>
                    <a href="${cp}/orders?status=HUY" class="btn ${filterStatus == 'HUY' ? 'btn-primary' : 'btn-outline-secondary'}">
                        Đã hủy
                    </a>
                </div>
            </div>
        </div>

        <c:choose>
            <c:when test="${empty orders}">
                <div class="card bg-dark border-secondary text-center py-5">
                    <div class="card-body">
                        <i class="bi bi-inbox display-1 text-muted mb-3"></i>
                        <h4 class="text-white mb-3">Chưa có đơn hàng nào</h4>
                        <p class="text-muted mb-4">Bạn chưa có đơn hàng nào. Hãy bắt đầu mua sắm!</p>
                        <a href="${cp}/search" class="btn btn-rog">
                            <i class="bi bi-search me-2"></i>Khám phá sản phẩm
                        </a>
                    </div>
                </div>
            </c:when>
            <c:otherwise>
                <c:forEach items="${orders}" var="order">
                    <div class="card bg-dark border-secondary mb-3">
                        <div class="card-header d-flex justify-content-between align-items-center flex-wrap">
                            <div>
                                <h6 class="text-white mb-1">
                                    <i class="bi bi-receipt me-2"></i>Đơn hàng #${order.id}
                                </h6>
                                <small class="text-muted">
                                    ${order.ngayDatHang.format(dateFormatter)}
                                </small>
                            </div>
                            <div class="d-flex align-items-center gap-2 mt-2 mt-md-0">
                                <c:choose>
                                    <c:when test="${order.trangThai.name() == 'MOI'}">
                                        <span class="badge bg-info">Mới</span>
                                    </c:when>
                                    <c:when test="${order.trangThai.name() == 'DANG_XU_LY'}">
                                        <span class="badge bg-primary">Đang xử lý</span>
                                    </c:when>
                                    <c:when test="${order.trangThai.name() == 'DANG_GIAO'}">
                                        <span class="badge bg-warning">Đang giao</span>
                                    </c:when>
                                    <c:when test="${order.trangThai.name() == 'HOAN_TAT'}">
                                        <span class="badge bg-success">Hoàn thành</span>
                                    </c:when>
                                    <c:when test="${order.trangThai.name() == 'DA_HUY'}">
                                        <span class="badge bg-danger">Đã hủy</span>
                                    </c:when>
                                    <c:otherwise>
                                        <span class="badge bg-secondary">${order.trangThai.name()}</span>
                                    </c:otherwise>
                                </c:choose>
                                
                                <%-- **[CODE MỚI ĐƯỢC THÊM VÀO]** --%>
                                <c:if test="${order.trangThai.name() == 'MOI'}">
                                    <form action="${cp}/orders" method="post" class="d-inline">
                                        <input type="hidden" name="action" value="cancel"/>
                                        <input type="hidden" name="orderId" value="${order.id}"/>
                                        <button type="submit" class="btn btn-sm btn-outline-danger" 
                                                onclick="return confirm('Bạn chắc chắn muốn hủy đơn hàng #${order.id}?')">
                                            Hủy đơn
                                        </button>
                                    </form>
                                </c:if>
                            </div>
                        </div>
                        <div class="card-body">
                            <h6 class="text-white mb-3">Chi tiết đơn hàng:</h6>
                            <div class="table-responsive">
                                <table class="table table-dark table-hover">
                                    <thead>
                                        <tr>
                                            <th>Sản phẩm</th>
                                            <th class="text-end">Đơn giá</th>
                                            <th class="text-center">Số lượng</th>
                                            <th class="text-end">Thành tiền</th>
                                        </tr>
                                    </thead>
                                    <tbody>
                                        <c:set var="total" value="0"/>
                                        <c:forEach items="${order.chiTiet}" var="detail">
                                            <tr>
                                                <td>
                                                    <a href="${cp}/product?id=${detail.sanPham.id}" class="text-white text-decoration-none">
                                                        ${fn:escapeXml(detail.sanPham.tenSanPham)}
                                                    </a>
                                                </td>
                                                <td class="text-end">
                                                    <fmt:formatNumber value="${detail.donGia}" type="number" groupingUsed="true"/> đ
                                                </td>
                                                <td class="text-center">${detail.soLuong}</td>
                                                <td class="text-end">
                                                    <fmt:formatNumber value="${detail.donGia * detail.soLuong}" type="number" groupingUsed="true"/> đ
                                                </td>
                                            </tr>
                                            <c:set var="total" value="${total + (detail.donGia * detail.soLuong)}"/>
                                        </c:forEach>
                                    </tbody>
                                    <tfoot>
                                        <tr class="table-active">
                                            <th colspan="3" class="text-end">Tổng cộng:</th>
                                            <th class="text-end text-warning">
                                                <fmt:formatNumber value="${total}" type="number" groupingUsed="true"/> đ
                                            </th>
                                        </tr>
                                    </tfoot>
                                </table>
                            </div>

                            <div class="row mt-3">
                                <div class="col-md-6">
                                    <h6 class="text-white mb-2">
                                        <i class="bi bi-person me-2"></i>Thông tin khách hàng
                                    </h6>
                                    <p class="text-muted mb-1">
                                        <strong>Họ tên:</strong> ${fn:escapeXml(order.khachHang.hoTen)}
                                    </p>
                                    <p class="text-muted mb-1">
                                        <strong>Email:</strong> ${fn:escapeXml(order.khachHang.email)}
                                    </p>
                                    <c:if test="${not empty order.khachHang.sdt}">
                                        <p class="text-muted mb-1">
                                            <strong>SĐT:</strong> ${fn:escapeXml(order.khachHang.sdt)}
                                        </p>
                                    </c:if>
                                </div>
                                <div class="col-md-6">
                                    <h6 class="text-white mb-2">
                                        <i class="bi bi-credit-card me-2"></i>Phương thức thanh toán
                                    </h6>
                                    <p class="text-muted">
                                        ${order.thanhToan != null ? fn:escapeXml(order.thanhToan.tenPhuongThuc) : 'Chưa xác định'}
                                    </p>
                                </div>
                            </div>
                        </div>
                    </div>
                </c:forEach>

                <c:if test="${totalPages > 1}">
                    <nav aria-label="Orders pagination">
                        <ul class="pagination justify-content-center">
                            <c:if test="${currentPage > 0}">
                                <li class="page-item">
                                    <a class="page-link" href="${cp}/orders?status=${filterStatus}&page=${currentPage - 1}">
                                        <i class="bi bi-chevron-left"></i> Trước
                                    </a>
                                </li>
                            </c:if>
                            
                            <c:forEach begin="0" end="${totalPages - 1}" var="i">
                                <li class="page-item ${i == currentPage ? 'active' : ''}">
                                    <a class="page-link" href="${cp}/orders?status=${filterStatus}&page=${i}">
                                        ${i + 1}
                                    </a>
                                </li>
                            </c:forEach>
                            
                            <c:if test="${currentPage < totalPages - 1}">
                                <li class="page-item">
                                    <a class="page-link" href="${cp}/orders?status=${filterStatus}&page=${currentPage + 1}">
                                        Sau <i class="bi bi-chevron-right"></i>
                                    </a>
                                </li>
                            </c:if>
                        </ul>
                    </nav>
                </c:if>
            </c:otherwise>
        </c:choose>
    </div>
</div>

<%@ include file="layout_footer.jspf" %>
