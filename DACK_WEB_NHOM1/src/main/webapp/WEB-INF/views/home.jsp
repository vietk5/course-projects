    <%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>
<%@ include file="layout_header.jspf" %>
<%-- PROMOS / CAROUSEL --%>
<section id="promos" class="mb-3">
    <div id="promoCarousel" class="carousel slide" data-bs-ride="carousel">
        <div class="carousel-inner rounded-3 shadow-sm">
            <div class="carousel-item active">
                <img src="${pageContext.request.contextPath}/assets/img/hero1.jpg" class="d-block w-100" alt="promo">
                <div class="carousel-caption text-start"></div>
            </div>
            <div class="carousel-item">
                <img src="${pageContext.request.contextPath}/assets/img/hero2.png" class="d-block w-100" alt="promo">
                <div class="carousel-caption"></div>
            </div>
            <div class="carousel-item">
                <img src="${pageContext.request.contextPath}/assets/img/hero3.png" class="d-block w-100" alt="promo">
                <div class="carousel-caption text-end"></div>
            </div>
        </div>
        <button class="carousel-control-prev" type="button" data-bs-target="#promoCarousel" data-bs-slide="prev">
            <span class="carousel-control-prev-icon"></span>
        </button>
        <button class="carousel-control-next" type="button" data-bs-target="#promoCarousel" data-bs-slide="next">
            <span class="carousel-control-next-icon"></span>
        </button>
    </div>
</section>

<%-- FEATURED CATEGORIES --%>
<%-- HOT DEALS / GIẢM SÂU HÔM NAY --%>
<section id="hot-deals" class="mb-4">
    <div class="card border-0 shadow-sm" style="background: linear-gradient(135deg, #dc3545 0%, #c82333 100%); border-radius: 15px; border: 2px solid rgba(255, 255, 255, 0.2) !important;">
        <div class="card-header bg-transparent border-0 pb-0 d-flex justify-content-between align-items-center">
            <div class="d-flex align-items-center gap-2">
                <span class="badge bg-warning text-dark fw-bold fs-6 px-3 py-2">
                    <i class="bi bi-lightning-charge-fill"></i> FLASH SALE 10 PHÚT
                </span>
                <h5 class="mb-0 text-white fw-bold">
                    <i class="bi bi-fire"></i> Giảm sâu hôm nay
                </h5>
            </div>
            <div class="text-white d-flex align-items-center gap-2">
                <i class="bi bi-clock-fill fs-4"></i>
                <span class="fw-bold fs-5">Kết thúc sau:</span> 
                <span id="dealsCountdown" class="badge px-3 py-2">09 : 00</span>
            </div>
        </div>

        <div class="card-body pt-2">
            <div class="dr-viewport position-relative">
                <%-- Nút điều hướng (giữa cạnh trái/phải) --%>
                <button class="dr-nav prev" type="button" aria-label="Trước">
                    <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" aria-hidden="true">
                    <path d="M15 18l-6-6 6-6"/>
                    </svg>
                </button>

                <%-- Track cuộn ngang --%>
                <div class="dr-track" id="hotDealsTrack">
                    <c:forEach items="${empty deals ? best : deals}" var="p">
                        <div class="deal-card">
                            <jsp:include page="partials/product_card.jsp">
                                <jsp:param name="id" value="${p.getId()}"/>
                                <jsp:param name="name" value="${p.getTenSanPham()}"/>
                                <jsp:param name="brand" value="${p.getThuongHieu().getTenThuongHieu()}"/>
                                <jsp:param name="category" value="${p.getLoai().getTenLoai()}"/>
                                <jsp:param name="price" value="${p.getGia()}"/>
                                <jsp:param name="oldPrice" value="${p.getGia()}"/>
                                <jsp:param name="stock" value="${p.getSoLuongTon()}"/>
                                <jsp:param name="image" value="${pageContext.request.contextPath}/assets/img/products/${p.getId()}.jpg"/>
                            </jsp:include>
                        </div>
                    </c:forEach>
                </div>

                <button class="dr-nav next" type="button" aria-label="Tiếp">
                    <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" aria-hidden="true">
                    <path d="M9 6l6 6-6 6"/>
                    </svg>
                </button>
            </div>
        </div>
    </div>
</section>


<%-- BRANDS STRIP --%>
<section class="mb-4">
    <div class="card border-0 shadow-sm">
        <div class="card-body">
            <h5 class="card-title d-flex align-items-center gap-2">
                Thương hiệu nổi bật
                <c:if test="${not empty activeBrand}">
                    <span class="badge bg-info-subtle text-white border">
                        Đang lọc: <strong>${activeBrand}</strong>
                    </span>
                    <a class="btn btn-sm btn-outline-light-subtle ms-auto"
                       href="${pageContext.request.contextPath}/home">Xóa lọc</a>
                </c:if>
            </h5>

            <div class="d-flex flex-wrap gap-2">
                <%-- Nút 'Tất cả' --%>
                <a href="${pageContext.request.contextPath}/home"
                   class="badge rounded-pill text-bg-light border brand-pill
                   <c:if test='${empty activeBrand}'>active</c:if>">Tất cả</a>

                <%-- Các brand -> link kèm ?brand=... --%>
                <c:forEach items="${brands}" var="b">
                    <a href="${pageContext.request.contextPath}/home?brand=${fn:escapeXml(b)}"
                       class="badge rounded-pill text-bg-light border brand-pill
                       <c:if test='${not empty activeBrand and fn:toLowerCase(activeBrand) == fn:toLowerCase(b)}'>active</c:if>">
                        ${b}
                    </a>
                </c:forEach>
            </div>
        </div>
    </div>
</section>

<%-- BEST SELLERS --%>
<section id="best" class="mb-4">
    <div class="card border-0 shadow-sm">
        <div class="card-body">
<!--            <div class="d-flex justify-content-between align-items-center mb-2">
                <h5 class="mb-0">Sản phẩm bán chạy</h5>
                <a class="link-primary small" href="#">Xem tất cả</a>
            </div>-->

            <c:choose>
                <c:when test="${not empty best}">
                    <div class="d-flex justify-content-between align-items-center mb-2">
                        <h5 class="mb-0">Sản phẩm bán chạy</h5>
                        <span class="badge bg-primary">
                            Tổng: ${fn:length(best)} sản phẩm
                        </span>
                    </div>

                    <div class="row row-cols-2 row-cols-md-3 row-cols-xl-4 g-3">
                        <c:forEach items="${best}" var="p">
                            <jsp:include page="partials/product_card.jsp">
                                <jsp:param name="id" value="${p.getId()}"/>
                                <jsp:param name="name" value="${p.getTenSanPham()}"/>
                                <jsp:param name="brand" value="${p.getThuongHieu().getTenThuongHieu()}"/>
                                <jsp:param name="category" value="${p.getLoai().getTenLoai()}"/>
                                <jsp:param name="price" value="${p.getGia()}"/>
                                <jsp:param name="oldPrice" value="${p.getGia()}"/>
                                <jsp:param name="stock" value="${p.getSoLuongTon()}"/>
                                <jsp:param name="image" value="${pageContext.request.contextPath}/assets/img/products/${p.getId()}.jpg"/>
                            </jsp:include>
                        </c:forEach>
                    </div>
                </c:when>
                <c:otherwise>
                    <div class="text-muted">Không có sản phẩm phù hợp.</div>
                </c:otherwise>
            </c:choose>

        </div>
    </div>
</section>

<%-- Laptops --%>
<section id="laptops" class="mb-4">
    <div class="card border-0 shadow-sm">
        <div class="card-body">
            <div class="d-flex justify-content-between align-items-center mb-2">
                <h5 class="mb-0">Laptop nổi bật</h5>
                <a class="link-primary small" href="search?q=Laptop">Xem tất cả</a>
            </div>

            <c:choose>
                <c:when test="${not empty laptops}">
                    <div class="row row-cols-2 row-cols-md-3 row-cols-xl-4 g-3">
                        <c:forEach items="${laptops}" var="p">
                            <jsp:include page="partials/product_card.jsp">
                                <jsp:param name="id" value="${p.getId()}"/>
                                <jsp:param name="name" value="${p.getTenSanPham()}"/>
                                <jsp:param name="brand" value="${p.getThuongHieu().getTenThuongHieu()}"/>
                                <jsp:param name="category" value="${p.getLoai().getTenLoai()}"/>
                                <jsp:param name="price" value="${p.getGia()}"/>
                                <jsp:param name="oldPrice" value="${p.getGia()}"/>
                                <jsp:param name="stock" value="${p.getSoLuongTon()}"/>
                                <jsp:param name="image" value="${pageContext.request.contextPath}/assets/img/products/${p.getId()}.jpg"/>
                            </jsp:include>
                        </c:forEach>
                    </div>
                </c:when>
                <c:otherwise>
                    <div class="text-muted">Không có sản phẩm phù hợp.</div>
                </c:otherwise>
            </c:choose>
        </div>
    </div>
</section>

<%-- PCs --%>
<section id="pcs" class="mb-4">
    <div class="card border-0 shadow-sm">
        <div class="card-body">
            <div class="d-flex justify-content-between align-items-center mb-2">
                <h5 class="mb-0">Bộ PC gợi ý</h5>
                <a class="link-primary small" href="#">Xem tất cả</a>
            </div>

            <c:choose>
                <c:when test="${not empty pcs}">
                    <div class="row row-cols-2 row-cols-md-3 row-cols-xl-4 g-3">
                        <c:forEach items="${pcs}" var="p">
                            <jsp:include page="partials/product_card.jsp">
                                <jsp:param name="id" value="${p.getId()}"/>
                                <jsp:param name="name" value="${p.getTenSanPham()}"/>
                                <jsp:param name="brand" value="${p.getThuongHieu().getTenThuongHieu()}"/>
                                <jsp:param name="category" value="${p.getLoai().getTenLoai()}"/>
                                <jsp:param name="price" value="${p.getGia()}"/>
                                <jsp:param name="oldPrice" value="${p.getGia()}"/>
                                <jsp:param name="stock" value="${p.getSoLuongTon()}"/>
                                <jsp:param name="image" value="${pageContext.request.contextPath}/assets/img/products/${p.getId()}.jpg"/>
                            </jsp:include>
                        </c:forEach>
                    </div>
                </c:when>
                <c:otherwise>
                    <div class="text-muted">Không có sản phẩm phù hợp.</div>
                </c:otherwise>
            </c:choose>
        </div>
    </div>
</section>

<%@ include file="layout_footer.jspf" %>
