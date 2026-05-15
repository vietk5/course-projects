<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>

<%@ include file="/WEB-INF/views/layout_header.jspf" %>

<style>
    .compare-header {
        background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
        padding: 2rem;
        border-radius: 15px;
        margin-bottom: 2rem;
        box-shadow: 0 10px 30px rgba(102, 126, 234, 0.3);
    }

    .compare-slot {
        background: #1a1a2e;
        border: 3px dashed #667eea;
        border-radius: 15px;
        padding: 3rem 2rem;
        text-align: center;
        min-height: 400px;
        transition: all 0.3s ease;
        display: flex;
        flex-direction: column;
        justify-content: center;
        align-items: center;
    }

    .compare-slot:hover {
        border-color: #764ba2;
        background: rgba(102, 126, 234, 0.05);
    }

    .compare-slot.filled {
        background: linear-gradient(135deg, #1a1a2e 0%, #16213e 100%);
        border-style: solid;
    }

    .compare-product {
        width: 100%;
    }

    .compare-product-image {
        width: 200px;
        height: 200px;
        object-fit: cover;
        border-radius: 10px;
        border: 3px solid #667eea;
        box-shadow: 0 5px 15px rgba(102, 126, 234, 0.3);
        margin-bottom: 1.5rem;
    }

    .select-product-btn {
        background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
        border: none;
        padding: 1rem 2rem;
        border-radius: 25px;
        color: white;
        font-weight: 600;
        font-size: 1.1rem;
        transition: all 0.3s ease;
        box-shadow: 0 4px 15px rgba(102, 126, 234, 0.3);
    }

    .select-product-btn:hover {
        transform: translateY(-2px);
        box-shadow: 0 6px 20px rgba(102, 126, 234, 0.5);
        color: white;
    }

    .empty-slot-icon {
        font-size: 4rem;
        color: #667eea;
        opacity: 0.5;
        margin-bottom: 1rem;
    }

    .product-modal .modal-dialog {
        max-width: 900px;
    }

    .product-list-item {
        padding: 1rem;
        border: 1px solid #2a2a3e;
        border-radius: 10px;
        margin-bottom: 1rem;
        background: #1a1a2e;
        transition: all 0.3s ease;
        cursor: pointer;
    }

    .product-list-item:hover {
        border-color: #667eea;
        background: rgba(102, 126, 234, 0.1);
        transform: translateX(5px);
    }

    .product-list-item img {
        width: 80px;
        height: 80px;
        object-fit: cover;
        border-radius: 8px;
    }

    .compare-table {
        background: #1a1a2e;
        border-radius: 15px;
        overflow: hidden;
        box-shadow: 0 10px 40px rgba(0, 0, 0, 0.5);
        margin-top: 2rem;
    }

    .compare-table th {
        background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
        color: white;
        padding: 1.5rem;
        font-weight: 600;
        border: none;
    }

    .compare-table td {
        padding: 1.5rem;
        border-bottom: 1px solid #2a2a3e;
        color: #e0e0e0;
        vertical-align: top;
    }

    .compare-table .row-label {
        background: #16213e;
        font-weight: 600;
        color: #667eea;
        width: 200px;
    }

    .remove-btn {
        background: linear-gradient(135deg, #e74c3c, #c0392b);
        border: none;
        padding: 0.5rem 1rem;
        border-radius: 20px;
        color: white;
        font-weight: 600;
        transition: all 0.3s ease;
    }

    .remove-btn:hover {
        transform: translateY(-2px);
        box-shadow: 0 4px 15px rgba(231, 76, 60, 0.4);
    }

    .price-highlight {
        font-size: 1.5rem;
        font-weight: 700;
        color: #f39c12;
    }

    .search-box {
        background: #1a1a2e;
        border: 1px solid #667eea;
        border-radius: 10px;
        padding: 0.75rem;
        color: white;
        margin-bottom: 1.5rem;
    }

    .search-box:focus {
        outline: none;
        border-color: #764ba2;
        box-shadow: 0 0 10px rgba(102, 126, 234, 0.3);
    }
</style>

<!-- Header -->
<div class="compare-header">
    <div class="d-flex justify-content-between align-items-center">
        <div>
            <h1 class="text-white mb-2">
                <i class="bi bi-arrow-left-right me-3"></i>So sánh sản phẩm
            </h1>
            <p class="text-white-50 mb-0">
                Chọn 2 sản phẩm cùng loại để so sánh chi tiết
            </p>
            <c:if test="${not empty selectedCategory}">
                <p class="text-warning mb-0 mt-2">
                    <i class="bi bi-info-circle me-2"></i>Đang chọn sản phẩm loại: <strong>${selectedCategory}</strong>
                </p>
            </c:if>
        </div>
        <c:if test="${compareCount > 0}">
            <a href="${pageContext.request.contextPath}/compare?action=clear" 
               class="btn btn-light btn-lg"
               onclick="return confirm('Bạn có chắc muốn bỏ chọn tất cả?')">
                <i class="bi bi-arrow-counterclockwise me-2"></i>Chọn lại
            </a>
        </c:if>
    </div>
</div>

<!-- Error Message -->
<c:if test="${not empty errorMessage}">
    <div class="alert alert-danger alert-dismissible fade show" role="alert">
        <i class="bi bi-exclamation-triangle-fill me-2"></i>
        <strong>Lỗi!</strong> ${errorMessage}
        <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
    </div>
</c:if>

<!-- Product Selection Slots -->
<div class="row g-4 mb-4">
    <!-- Slot 1 -->
    <div class="col-md-6">
        <c:choose>
            <c:when test="${compareCount >= 1}">
                <div class="compare-slot filled">
                    <div class="compare-product">
                        <img src="${pageContext.request.contextPath}/assets/img/products/${compareProducts[0].id}.jpg" 
                             alt="${fn:escapeXml(compareProducts[0].tenSanPham)}"
                             class="compare-product-image"
                             onerror="this.src='${pageContext.request.contextPath}/assets/img/laptop_placeholder.jpg'"/>
                        <h4 class="text-white mb-3">${fn:escapeXml(compareProducts[0].tenSanPham)}</h4>
                        <p class="text-muted mb-2">
                            <i class="bi bi-tag me-1"></i>${fn:escapeXml(compareProducts[0].thuongHieu.tenThuongHieu)} 
                            <span class="mx-2">•</span>
                            <i class="bi bi-grid me-1"></i>${fn:escapeXml(compareProducts[0].loai.tenLoai)}
                        </p>
                        <p class="price-highlight mb-3">
                            <fmt:formatNumber value="${compareProducts[0].gia}" type="currency" currencyCode="VND"/>
                        </p>
                        <a href="${pageContext.request.contextPath}/compare?action=remove&productId=${compareProducts[0].id}" 
                           class="btn remove-btn">
                            <i class="bi bi-x-circle me-1"></i>Bỏ chọn
                        </a>
                    </div>
                </div>
            </c:when>
            <c:otherwise>
                <div class="compare-slot">
                    <div class="empty-slot-icon">
                        <i class="bi bi-1-circle"></i>
                    </div>
                    <h4 class="text-white mb-3">Chọn sản phẩm thứ nhất</h4>
                    <p class="text-muted mb-4">Click nút bên dưới để chọn sản phẩm</p>
                    <button class="btn select-product-btn" data-bs-toggle="modal" data-bs-target="#productModal">
                        <i class="bi bi-plus-circle me-2"></i>Chọn sản phẩm
                    </button>
                </div>
            </c:otherwise>
        </c:choose>
    </div>

    <!-- Slot 2 -->
    <div class="col-md-6">
        <c:choose>
            <c:when test="${compareCount >= 2}">
                <div class="compare-slot filled">
                    <div class="compare-product">
                        <img src="${pageContext.request.contextPath}/assets/img/products/${compareProducts[1].id}.jpg" 
                             alt="${fn:escapeXml(compareProducts[1].tenSanPham)}"
                             class="compare-product-image"
                             onerror="this.src='${pageContext.request.contextPath}/assets/img/laptop_placeholder.jpg'"/>
                        <h4 class="text-white mb-3">${fn:escapeXml(compareProducts[1].tenSanPham)}</h4>
                        <p class="text-muted mb-2">
                            <i class="bi bi-tag me-1"></i>${fn:escapeXml(compareProducts[1].thuongHieu.tenThuongHieu)} 
                            <span class="mx-2">•</span>
                            <i class="bi bi-grid me-1"></i>${fn:escapeXml(compareProducts[1].loai.tenLoai)}
                        </p>
                        <p class="price-highlight mb-3">
                            <fmt:formatNumber value="${compareProducts[1].gia}" type="currency" currencyCode="VND"/>
                        </p>
                        <a href="${pageContext.request.contextPath}/compare?action=remove&productId=${compareProducts[1].id}" 
                           class="btn remove-btn">
                            <i class="bi bi-x-circle me-1"></i>Bỏ chọn
                        </a>
                    </div>
                </div>
            </c:when>
            <c:otherwise>
                <div class="compare-slot">
                    <div class="empty-slot-icon">
                        <i class="bi bi-2-circle"></i>
                    </div>
                    <c:choose>
                        <c:when test="${compareCount == 0}">
                            <h4 class="text-white mb-3">Chọn sản phẩm thứ hai</h4>
                            <p class="text-muted mb-4">Chọn sản phẩm thứ nhất trước</p>
                            <button class="btn select-product-btn" disabled>
                                <i class="bi bi-lock me-2"></i>Chưa thể chọn
                            </button>
                        </c:when>
                        <c:otherwise>
                            <h4 class="text-white mb-3">Chọn sản phẩm thứ hai</h4>
                            <p class="text-muted mb-4">Phải cùng loại: <strong class="text-info">${selectedCategory}</strong></p>
                            <button class="btn select-product-btn" data-bs-toggle="modal" data-bs-target="#productModal">
                                <i class="bi bi-plus-circle me-2"></i>Chọn sản phẩm
                            </button>
                        </c:otherwise>
                    </c:choose>
                </div>
            </c:otherwise>
        </c:choose>
    </div>
</div>

<!-- Compare Table (chỉ hiện khi đã chọn đủ 2 sản phẩm) -->
<c:if test="${compareCount == 2}">
    <div class="compare-table-section">
        <h3 class="text-white mb-4">
            <i class="bi bi-table me-2"></i>Chi tiết so sánh
        </h3>
        
        <div class="table-responsive">
            <table class="table compare-table">
                <thead>
                    <tr>
                        <th style="width: 200px;">Thông tin</th>
                        <th class="text-center">Sản phẩm 1</th>
                        <th class="text-center">Sản phẩm 2</th>
                    </tr>
                </thead>
                <tbody>
                    <!-- Tên sản phẩm -->
                    <tr>
                        <td class="row-label"><i class="bi bi-box me-2"></i>Tên sản phẩm</td>
                        <td>
                            <strong class="text-white" style="font-size: 1.1rem;">${fn:escapeXml(compareProducts[0].tenSanPham)}</strong>
                        </td>
                        <td>
                            <strong class="text-white" style="font-size: 1.1rem;">${fn:escapeXml(compareProducts[1].tenSanPham)}</strong>
                        </td>
                    </tr>
                    
                    <!-- Giá -->
                    <tr style="background: rgba(102, 126, 234, 0.05);">
                        <td class="row-label"><i class="bi bi-currency-dollar me-2"></i>Giá bán</td>
                        <td>
                            <div class="price-highlight" style="font-size: 1.8rem;">
                                <fmt:formatNumber value="${compareProducts[0].gia}" pattern="#,###"/> đ
                            </div>
                        </td>
                        <td>
                            <div class="price-highlight" style="font-size: 1.8rem;">
                                <fmt:formatNumber value="${compareProducts[1].gia}" pattern="#,###"/> đ
                            </div>
                        </td>
                    </tr>

                    <!-- Thương hiệu -->
                    <tr>
                        <td class="row-label"><i class="bi bi-tag me-2"></i>Thương hiệu</td>
                        <td>
                            <span class="badge bg-primary" style="font-size: 0.95rem; padding: 0.5rem 1rem;">
                                ${fn:escapeXml(compareProducts[0].thuongHieu.tenThuongHieu)}
                            </span>
                        </td>
                        <td>
                            <span class="badge bg-primary" style="font-size: 0.95rem; padding: 0.5rem 1rem;">
                                ${fn:escapeXml(compareProducts[1].thuongHieu.tenThuongHieu)}
                            </span>
                        </td>
                    </tr>

                    <!-- Tồn kho -->
                    <tr>
                        <td class="row-label"><i class="bi bi-box-seam me-2"></i>Tình trạng</td>
                        <td>
                            <c:choose>
                                <c:when test="${compareProducts[0].soLuongTon <= 0}">
                                    <span class="badge bg-danger" style="font-size: 0.95rem; padding: 0.5rem 1rem;">
                                        <i class="bi bi-x-circle me-1"></i>Hết hàng
                                    </span>
                                </c:when>
                                <c:when test="${compareProducts[0].soLuongTon < 10}">
                                    <span class="badge bg-warning text-dark" style="font-size: 0.95rem; padding: 0.5rem 1rem;">
                                        <i class="bi bi-exclamation-triangle me-1"></i>Còn ${compareProducts[0].soLuongTon} sản phẩm
                                    </span>
                                </c:when>
                                <c:otherwise>
                                    <span class="badge bg-success" style="font-size: 0.95rem; padding: 0.5rem 1rem;">
                                        <i class="bi bi-check-circle me-1"></i>Còn hàng
                                    </span>
                                </c:otherwise>
                            </c:choose>
                        </td>
                        <td>
                            <c:choose>
                                <c:when test="${compareProducts[1].soLuongTon <= 0}">
                                    <span class="badge bg-danger" style="font-size: 0.95rem; padding: 0.5rem 1rem;">
                                        <i class="bi bi-x-circle me-1"></i>Hết hàng
                                    </span>
                                </c:when>
                                <c:when test="${compareProducts[1].soLuongTon < 10}">
                                    <span class="badge bg-warning text-dark" style="font-size: 0.95rem; padding: 0.5rem 1rem;">
                                        <i class="bi bi-exclamation-triangle me-1"></i>Còn ${compareProducts[1].soLuongTon} sản phẩm
                                    </span>
                                </c:when>
                                <c:otherwise>
                                    <span class="badge bg-success" style="font-size: 0.95rem; padding: 0.5rem 1rem;">
                                        <i class="bi bi-check-circle me-1"></i>Còn hàng
                                    </span>
                                </c:otherwise>
                            </c:choose>
                        </td>
                    </tr>

                    <!-- Mô tả -->
                    <tr>
                        <td class="row-label"><i class="bi bi-file-text me-2"></i>Mô tả chi tiết</td>
                        <td>
                            <div class="text-white" style="white-space: pre-line; line-height: 1.8;">
                                ${fn:escapeXml(compareProducts[0].moTaNgan)}
                            </div>
                        </td>
                        <td>
                            <div class="text-white" style="white-space: pre-line; line-height: 1.8;">
                                ${fn:escapeXml(compareProducts[1].moTaNgan)}
                            </div>
                        </td>
                    </tr>

                    <!-- Hành động -->
                    <tr>
                        <td class="row-label"><i class="bi bi-cart-plus me-2"></i>Hành động</td>
                        <td class="text-center">
                            <div class="d-grid gap-2">
                                <c:if test="${compareProducts[0].soLuongTon > 0}">
                                    <!-- Mua ngay -->
                                    <form method="post" action="${pageContext.request.contextPath}/checkout" class="mb-0">
                                        <input type="hidden" name="action" value="buy_now">
                                        <input type="hidden" name="productId" value="${compareProducts[0].id}">
                                        <input type="hidden" name="qty" value="1">
                                        <button type="submit" class="btn btn-warning btn-sm w-100">
                                            <i class="bi bi-lightning-charge-fill me-1"></i>Mua ngay
                                        </button>
                                    </form>
                                    
                                    <!-- Thêm vào giỏ -->
                                    <form method="post" action="${pageContext.request.contextPath}/cart" class="mb-0">
                                        <input type="hidden" name="action" value="add">
                                        <input type="hidden" name="productId" value="${compareProducts[0].id}">
                                        <input type="hidden" name="qty" value="1">
                                        <button type="submit" class="btn btn-rog btn-sm w-100">
                                            <i class="bi bi-cart-plus me-1"></i>Thêm vào giỏ
                                        </button>
                                    </form>
                                </c:if>
                                
                                <!-- Xem chi tiết -->
                                <a href="${pageContext.request.contextPath}/product?id=${compareProducts[0].id}" 
                                   class="btn btn-outline-light btn-sm">
                                    <i class="bi bi-eye me-1"></i>Xem chi tiết
                                </a>
                            </div>
                        </td>
                        <td class="text-center">
                            <div class="d-grid gap-2">
                                <c:if test="${compareProducts[1].soLuongTon > 0}">
                                    <!-- Mua ngay -->
                                    <form method="post" action="${pageContext.request.contextPath}/checkout" class="mb-0">
                                        <input type="hidden" name="action" value="buy_now">
                                        <input type="hidden" name="productId" value="${compareProducts[1].id}">
                                        <input type="hidden" name="qty" value="1">
                                        <button type="submit" class="btn btn-warning btn-sm w-100">
                                            <i class="bi bi-lightning-charge-fill me-1"></i>Mua ngay
                                        </button>
                                    </form>
                                    
                                    <!-- Thêm vào giỏ -->
                                    <form method="post" action="${pageContext.request.contextPath}/cart" class="mb-0">
                                        <input type="hidden" name="action" value="add">
                                        <input type="hidden" name="productId" value="${compareProducts[1].id}">
                                        <input type="hidden" name="qty" value="1">
                                        <button type="submit" class="btn btn-rog btn-sm w-100">
                                            <i class="bi bi-cart-plus me-1"></i>Thêm vào giỏ
                                        </button>
                                    </form>
                                </c:if>
                                
                                <!-- Xem chi tiết -->
                                <a href="${pageContext.request.contextPath}/product?id=${compareProducts[1].id}" 
                                   class="btn btn-outline-light btn-sm">
                                    <i class="bi bi-eye me-1"></i>Xem chi tiết
                                </a>
                            </div>
                        </td>
                    </tr>
                </tbody>
            </table>
        </div>
    </div>
</c:if>

<!-- Modal chọn sản phẩm -->
<div class="modal fade product-modal" id="productModal" tabindex="-1">
    <div class="modal-dialog modal-dialog-scrollable">
        <div class="modal-content bg-dark">
            <div class="modal-header border-secondary">
                <h5 class="modal-title text-white">
                    <i class="bi bi-search me-2"></i>Chọn sản phẩm
                    <c:if test="${not empty selectedCategory}">
                        <span class="badge bg-info ms-2">${selectedCategory}</span>
                    </c:if>
                </h5>
                <button type="button" class="btn-close btn-close-white" data-bs-dismiss="modal"></button>
            </div>
            <div class="modal-body">
                <!-- Search box -->
                <input type="text" id="searchInput" class="form-control search-box" 
                       placeholder="Tìm kiếm sản phẩm..."/>
                
                <!-- Product list -->
                <div id="productList">
                    <c:forEach items="${availableProducts}" var="product">
                        <a href="${pageContext.request.contextPath}/compare?action=add&productId=${product.id}" 
                           class="text-decoration-none product-item" data-name="${fn:escapeXml(product.tenSanPham)}">
                            <div class="product-list-item">
                                <div class="d-flex align-items-center">
                                    <img src="${pageContext.request.contextPath}/assets/img/products/${product.id}.jpg" 
                                         alt="${fn:escapeXml(product.tenSanPham)}"
                                         onerror="this.src='${pageContext.request.contextPath}/assets/img/laptop_placeholder.jpg'"/>
                                    <div class="ms-3 flex-grow-1">
                                        <h6 class="text-white mb-1">${fn:escapeXml(product.tenSanPham)}</h6>
                                        <p class="text-muted small mb-1">
                                            <i class="bi bi-tag me-1"></i>${fn:escapeXml(product.thuongHieu.tenThuongHieu)}
                                            <span class="mx-2">•</span>
                                            <i class="bi bi-grid me-1"></i>${fn:escapeXml(product.loai.tenLoai)}
                                        </p>
                                        <p class="text-warning mb-0 fw-bold">
                                            <fmt:formatNumber value="${product.gia}" type="currency" currencyCode="VND"/>
                                        </p>
                                    </div>
                                    <i class="bi bi-chevron-right text-muted"></i>
                                </div>
                            </div>
                        </a>
                    </c:forEach>
                </div>
            </div>
        </div>
    </div>
</div>

<!-- JavaScript cho tìm kiếm -->
<script>
document.getElementById('searchInput').addEventListener('input', function(e) {
    const searchTerm = e.target.value.toLowerCase();
    const items = document.querySelectorAll('.product-item');
    
    items.forEach(item => {
        const productName = item.getAttribute('data-name').toLowerCase();
        if (productName.includes(searchTerm)) {
            item.style.display = 'block';
        } else {
            item.style.display = 'none';
        }
    });
});
</script>

<%@ include file="/WEB-INF/views/layout_footer.jspf" %>
