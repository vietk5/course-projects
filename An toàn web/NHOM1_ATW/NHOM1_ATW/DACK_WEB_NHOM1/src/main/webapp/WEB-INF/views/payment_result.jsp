<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c"  uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>

<c:set var="cp" value="${pageContext.request.contextPath}" />
<c:set var="pageTitle" value="Kết quả thanh toán - ElectroMart" scope="request"/>

<%@ include file="layout_header.jspf" %>

<div class="container py-5">
    <div class="row justify-content-center">
        <div class="col-md-8 col-lg-6">
            <div class="card bg-dark border-secondary shadow-lg">
                <div class="card-body text-center py-5">

                    <c:choose>
                        <c:when test="${success}">
                            <!-- THÀNH CÔNG -->
                            <div class="mb-4">
                                <i class="bi bi-check-circle-fill text-success" style="font-size: 5rem;"></i>
                            </div>
                            <h2 class="text-success mb-3">Thanh toán thành công!</h2>
                            <p class="text-white mb-4">${message}</p>

                            <div class="alert alert-success text-start mb-4">
                                <div class="row">
                                    <div class="col-6">
                                        <strong>Mã đơn hàng:</strong>
                                    </div>
                                    <div class="col-6 text-end">
                                        #${orderId}
                                    </div>
                                </div>
                                <div class="row mt-2">
                                    <div class="col-6">
                                        <strong>Mã giao dịch:</strong>
                                    </div>
                                    <div class="col-6 text-end">
                                        ${transactionNo}
                                    </div>
                                </div>
                                <div class="row mt-2">
                                    <div class="col-6">
                                        <strong>Số tiền:</strong>
                                    </div>
                                    <div class="col-6 text-end">
                                        <fmt:formatNumber value="${amount}" type="number" groupingUsed="true"/> đ
                                    </div>
                                </div>
                            </div>

                            <div class="d-grid gap-2">
                                <a href="${cp}/orders" class="btn btn-rog btn-lg">
                                    <i class="bi bi-list-check me-2"></i>Xem đơn hàng của tôi
                                </a>
                                <a href="${cp}/home" class="btn btn-outline-secondary">
                                    <i class="bi bi-house me-2"></i>Về trang chủ
                                </a>
                            </div>

                            <div class="alert alert-info mt-3 mb-0 text-start">
                                <i class="bi bi-envelope-check me-2"></i>
                                Email xác nhận đơn hàng đã được gửi đến <strong>${sessionScope.user.email}</strong>. 
                                Vui lòng kiểm tra hộp thư (bao gồm cả thư mục Spam/Junk).
                            </div>
                        </c:when>

                        <c:otherwise>
                            <!-- THẤT BẠI -->
                            <div class="mb-4">
                                <i class="bi bi-x-circle-fill text-danger" style="font-size: 5rem;"></i>
                            </div>
                            <h2 class="text-danger mb-3">Thanh toán thất bại!</h2>
                            <p class="text-white mb-4">${message}</p>

                            <c:if test="${not empty responseCode}">
                                <div class="alert alert-danger text-start mb-4">
                                    <strong>Mã lỗi:</strong> ${responseCode}
                                </div>
                            </c:if>

                            <div class="d-grid gap-2">
                                <a href="${cp}/checkout" class="btn btn-warning btn-lg">
                                    <i class="bi bi-arrow-clockwise me-2"></i>Thử lại
                                </a>
                                <a href="${cp}/cart" class="btn btn-outline-secondary">
                                    <i class="bi bi-cart me-2"></i>Quay lại giỏ hàng
                                </a>
                                <a href="${cp}/home" class="btn btn-outline-secondary">
                                    <i class="bi bi-house me-2"></i>Về trang chủ
                                </a>
                            </div>
                        </c:otherwise>
                    </c:choose>

                    <div class="alert alert-info mt-4 mb-0 small text-start">
                        <i class="bi bi-info-circle me-2"></i>
                        Nếu có bất kỳ vấn đề gì, vui lòng liên hệ với chúng tôi qua hotline: 
                        <strong>1900-xxxx</strong>
                    </div>

                </div>
            </div>
        </div>
    </div>
</div>

<style>
    .btn-rog {
        background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
        border: none;
        color: white;
        transition: all 0.3s ease;
    }
    .btn-rog:hover {
        transform: translateY(-2px);
        box-shadow: 0 8px 20px rgba(102,126,234,.4);
        color: white;
    }
</style>

<%@ include file="layout_footer.jspf" %>