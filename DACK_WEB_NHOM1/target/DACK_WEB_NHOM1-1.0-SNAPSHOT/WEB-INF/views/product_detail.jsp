
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>

<%@ include file="/WEB-INF/views/layout_header.jspf" %>

<!-- Breadcrumb -->
<nav aria-label="breadcrumb" class="mb-4">
    <ol class="breadcrumb">
        <li class="breadcrumb-item"><a href="${cp}/home" class="text-warning">Trang chủ</a></li>
        <li class="breadcrumb-item"><a href="${cp}/search?category=${fn:escapeXml(product.loai.tenLoai)}" class="text-warning">${fn:escapeXml(product.loai.tenLoai)}</a></li>
        <li class="breadcrumb-item active text-white">${fn:escapeXml(product.tenSanPham)}</li>
    </ol>
</nav>

<!-- Product Detail -->
<div class="row mb-5">
    <div class="col-lg-6">
        <!-- Product Image -->
        <div class="product-detail-image position-relative">
            <img src="${cp}/assets/img/products/${product.id}.jpg" alt="${fn:escapeXml(product.tenSanPham)}" 
                 class="img-fluid rounded-3 shadow" style="width: 100%; max-height: 500px; object-fit: cover;"/>

            <!-- Rating Badge -->
            <%-- <c:if test="${product.rating > 0}">
              <span class="rating-badge position-absolute top-0 start-0 m-3 px-3 py-2 rounded-pill">
                <i class="bi bi-star-fill me-1"></i>${product.rating}
              </span>
            </c:if> --%>

            <!-- Discount Badge -->
            <%-- <c:if test="${product.oldPrice != null && product.oldPrice > 0}">
              <span class="discount-badge position-absolute top-0 end-0 m-3 px-3 py-2 rounded-pill">
                -<fmt:formatNumber value="${((product.oldPrice - product.price) / product.oldPrice) * 100}" 
                                type="number" maxFractionDigits="0"/>%
              </span>
            </c:if> --%>
            <div class="product-description mb-4">
                <h5 class="text-white mb-3"><br>Mô tả sản phẩm</h5>
                <div class="card bg-dark border-secondary">
                    <div class="card-body">
                        <p class="text-muted mb-0" style="white-space: pre-line;">
                            ${fn:escapeXml(product.getMoTaNgan())}
                        </p>
                    </div>
                </div>
            </div>

        </div>
    </div>

    <div class="col-lg-6">
        <div class="product-detail-info">
            <!-- Product Name -->
            <h1 class="text-white mb-3">${fn:escapeXml(product.tenSanPham)}</h1>

            <!-- Brand & Category -->
            <div class="d-flex align-items-center mb-3">
                <span class="badge bg-primary me-2">
                    <i class="bi bi-tag me-1"></i>${fn:escapeXml(product.thuongHieu.tenThuongHieu)}
                </span>
                <span class="badge bg-secondary">
                    <i class="bi bi-grid me-1"></i>${fn:escapeXml(product.loai.tenLoai)}
                </span>
            </div>

            <!-- Rating -->
            <%-- <c:if test="${product.rating > 0}">
              <div class="d-flex align-items-center mb-3">
                <div class="rating-stars me-2">
                  <c:forEach begin="1" end="5" var="i">
                    <i class="bi bi-star${i <= product.rating ? '-fill' : ''} text-warning"></i>
                  </c:forEach>
                </div>
                <span class="text-muted">(${product.rating}/5)</span>
              </div>
            </c:if> --%>

            <!-- Price -->
            <div class="price-section mb-4">
                <div class="d-flex align-items-center">
                    <span class="h2 text-warning fw-bold mb-0">
                        <fmt:formatNumber value="${product.gia}" type="currency" currencyCode="VND"/>
                    </span>
                    <%-- <c:if test="${product.oldPrice != null && product.oldPrice > 0}">
                      <span class="text-muted text-decoration-line-through ms-3 h5">
                        <fmt:formatNumber value="${product.oldPrice}" type="currency" currencyCode="VND"/>
                      </span>
                      <span class="badge bg-danger ms-2">
                        Tiết kiệm <fmt:formatNumber value="${product.oldPrice - product.price}" type="currency" currencyCode="VND"/>
                      </span>
                    </c:if> --%>
                </div>
            </div>

            <!-- Stock Status -->
            <div class="stock-info mb-4">
                <c:choose>
                    <c:when test="${product.soLuongTon <= 0}">
                        <div class="alert alert-danger d-flex align-items-center" role="alert">
                            <i class="bi bi-x-circle-fill me-2 fs-4"></i>
                            <div>
                                <strong>Hết hàng</strong>
                                <div class="small">Sản phẩm này hiện tại không có sẵn</div>
                            </div>
                        </div>
                    </c:when>
                    <c:when test="${product.soLuongTon < 10}">
                        <div class="alert alert-warning d-flex align-items-center" role="alert">
                            <i class="bi bi-exclamation-triangle-fill me-2 fs-4"></i>
                            <div>
                                <strong>Còn ${product.soLuongTon} sản phẩm</strong>
                                <div class="small">Số lượng có sẵn giới hạn - Đặt hàng ngay!</div>
                            </div>
                        </div>
                    </c:when>
                    <c:otherwise>
                        <div class="alert alert-success d-flex align-items-center" role="alert">
                            <i class="bi bi-check-circle-fill me-2 fs-4"></i>
                            <div>
                                <strong>Còn hàng</strong>
                                <div class="small">Có sẵn ${product.soLuongTon} sản phẩm</div>
                            </div>
                        </div>
                    </c:otherwise>
                </c:choose>
            </div>

            <!-- Product Description -->
            <%-- <div class="product-description mb-4">
              <h5 class="text-white mb-3">Mô tả sản phẩm</h5>
              <div class="card bg-dark border-secondary">
                <div class="card-body">
                  <p class="text-muted mb-0" style="white-space: pre-line;">
                    ${fn:escapeXml(product.getMoTaNgan())}
                  </p>
                </div>
              </div>
            </div> --%>


            <!--       Add to Cart 
                  <div class="add-to-cart-section">
                    <div class="row g-3">
                      <div class="col-md-6">
                        <label class="form-label text-white">Số lượng</label>
                        <div class="input-group">
                          <button class="btn btn-outline-secondary" type="button" onclick="decreaseQuantity()" ${product.soLuongTon <= 0 ? 'disabled' : ''}>-</button>
                          <input type="number" class="form-control text-center bg-dark border-secondary text-white" 
                                 id="quantity" value="1" min="1" max="${product.soLuongTon > 0 ? product.soLuongTon : 1}" ${product.soLuongTon <= 0 ? 'disabled' : ''}>
                          <button class="btn btn-outline-secondary" type="button" onclick="increaseQuantity()" ${product.soLuongTon <= 0 ? 'disabled' : ''}>+</button>
                        </div>
            <c:if test="${product.soLuongTon > 0}">
              <small class="text-muted">Tối đa: ${product.soLuongTon} sản phẩm</small>
            </c:if>
          </div>
          <div class="col-md-6 d-flex align-items-end">
            <form method="post" action="${cp}/cart" class="w-100">
              <input type="hidden" name="action" value="add">
              <input type="hidden" name="productId" value="${product.id}">
              <input type="hidden" name="qty" id="qtyHidden" value="1">
              <button class="btn btn-rog w-100" type="submit" ${product.soLuongTon <= 0 ? 'disabled' : ''}>
                <i class="bi bi-cart-plus me-2"></i>
            ${product.soLuongTon > 0 ? 'Thêm vào giỏ hàng' : 'Hết hàng'}
          </button>
        </form>
      </div>
    </div>
  </div>-->
            
            <!-- Add to Cart -->
            <div class="add-to-cart-section">
                <div class="quantity-row">

                    <!-- Cột 1: Số lượng -->
                    <div class="quantity-control">
                        <label class="form-label text-white mb-1">Số lượng</label>
                        <div class="quantity-box">
                            <button type="button" class="btn-qty" onclick="decreaseQuantity()">−</button>
                            <input type="number" id="quantity" class="quantity-input" value="1">
                            <button type="button" class="btn-qty" onclick="increaseQuantity()">+</button>
                        </div>
                    </div>

                    <!-- Cột 2: Thêm vào giỏ -->
                    <form method="post" action="${cp}/cart" class="action-form">
                        <input type="hidden" name="action" value="add">
                        <input type="hidden" name="productId" value="${product.id}">
                        <input type="hidden" name="qty" id="qtyHidden" value="1">
                        <button class="btn btn-rog w-100" type="submit" ${product.soLuongTon <= 0 ? 'disabled' : ''}>
                            <i class="bi bi-cart-plus me-2"></i>
                            ${product.soLuongTon > 0 ? 'Thêm vào giỏ' : 'Hết hàng'}
                        </button>
                    </form>

                    <!-- Cột 3: Mua ngay -->
                    <form method="post" action="${cp}/checkout" class="action-form">
                        <input type="hidden" name="action" value="buy_now">
                        <input type="hidden" name="productId" value="${product.id}">
                        <input type="hidden" name="qty" id="qtyHiddenForBuyNow" value="1">
                        <button class="btn btn-rog w-100" type="submit" ${product.soLuongTon <= 0 ? 'disabled' : ''}>
                            <i class="bi bi-lightning-charge me-2"></i>
                            ${product.soLuongTon > 0 ? 'Mua ngay' : 'Hết hàng'}
                        </button>
                    </form>

                </div>
            </div>
            <!-- Product Features -->
            <div class="product-features mt-4">
                <h6 class="text-white mb-3">Chính sách bán hàng</h6>
                <div class="row">
                    <div class="col-md-6">
                        <ul class="list-unstyled">
                            <li class="mb-2"><i class="bi bi-check-circle text-success me-2"></i>Chính hãng 100%</li>
                            <li class="mb-2"><i class="bi bi-check-circle text-success me-2"></i>Bảo hành chính thức</li>
                        </ul>
                    </div>
                    <div class="col-md-6">
                        <ul class="list-unstyled">
                            <li class="mb-2"><i class="bi bi-check-circle text-success me-2"></i>Giao hàng nhanh</li>
                            <li class="mb-2"><i class="bi bi-check-circle text-success me-2"></i>Hỗ trợ 24/7</li>
                        </ul>
                    </div>
                </div>
            </div>

            <div class="product-features mt-4">
                <h6 class="text-white mb-3">Mã giảm giá liên quan</h6>
                <div class="card bg-dark border-secondary">
                    <div class="card-body">
                        <ul class="list-unstyled mb-0">
                            <c:forEach items="${promoList}" var="p">
                                <li class="mb-2">
                                    <i class="bi bi-ticket-perforated text-warning me-2"></i>
                                    <strong>${p.ma}</strong> 
                                    <c:choose>
                                        <c:when test="${p.kieu == 'PHAN_TRAM'}">
                                            – Giảm <fmt:formatNumber value="${p.giaTri}" type="number"/>% 
                                            <c:if test="${not empty p.donToiThieu}">cho đơn từ <fmt:formatNumber value="${p.donToiThieu}" type="currency" currencySymbol="₫"/></c:if>
                                        </c:when>
                                        <c:when test="${p.kieu == 'TIEN_MAT'}">
                                            – Giảm <fmt:formatNumber value="${p.giaTri}" type="currency" currencySymbol="₫"/> 
                                            <c:if test="${not empty p.donToiThieu}">cho đơn từ <fmt:formatNumber value="${p.donToiThieu}" type="currency" currencySymbol="₫"/></c:if>
                                        </c:when>
                                        <c:otherwise>– Chi tiết chưa rõ</c:otherwise>
                                    </c:choose>
                                </li>
                            </c:forEach>
                            <c:if test="${empty promoList}">
                                <li class="mb-2 text-muted">Không có mã giảm giá nào liên quan.</li>
                                </c:if>
                        </ul>
                    </div>
                </div>
            </div>

        </div>
    </div>
</div>

<!-- Related Products -->
<c:if test="${not empty relatedProducts}">
    <div class="related-products">
        <h3 class="text-white mb-4">
            <i class="bi bi-grid me-2"></i>Sản phẩm liên quan
        </h3>
        <div class="row">
            <c:forEach items="${relatedProducts}" var="relatedProduct">
                <div class="col-lg-3 col-md-4 col-sm-6 mb-4">
                    <div class="card product-card h-100 d-flex flex-column shadow-sm border-0 bg-dark">
                        <!-- Image -->
                        <div class="product-img-wrap position-relative overflow-hidden">
                            <img class="product-img w-100" src="${cp}/assets/img/products/${relatedProduct.id}.jpg" 
                                 alt="${fn:escapeXml(relatedProduct.getTenSanPham())}" 
                                 style="height: 200px; object-fit: cover;"/>
                            <%-- <c:if test="${relatedProduct.rating > 0}">
                              <span class="rating-badge position-absolute top-0 start-0 m-2 px-2 py-1 rounded-pill">
                                <i class="bi bi-star-fill me-1"></i>${relatedProduct.rating}
                              </span>
                            </c:if> --%>
                        </div>

                        <!-- Body -->
                        <div class="card-body d-flex flex-column p-3">
                            <h6 class="card-title text-white mb-2" style="font-size: 0.95rem; line-height: 1.3;">
                                <a href="${cp}/product?name=${fn:escapeXml(relatedProduct.getTenSanPham())}" class="text-white text-decoration-none">
                                    ${fn:escapeXml(relatedProduct.getTenSanPham())}
                                </a>
                            </h6>
                            <p class="text-muted small mb-2">
                                <i class="bi bi-tag me-1"></i>${fn:escapeXml(relatedProduct.getThuongHieu().getTenThuongHieu())} 
                                <span class="mx-1">•</span>
                                <i class="bi bi-grid me-1"></i>${fn:escapeXml(relatedProduct.getLoai().getTenLoai())}
                            </p>

                            <div class="mt-auto">
                                <div class="d-flex align-items-center mb-2">
                                    <span class="h6 text-warning mb-0 fw-bold">
                                        <fmt:formatNumber value="${relatedProduct.getGia()}" type="currency" currencyCode="VND"/>
                                    </span>
                                    <%-- <c:if test="${relatedProduct.oldPrice != null && relatedProduct.oldPrice > 0}">
                                      <span class="text-muted text-decoration-line-through ms-2 small">
                                        <fmt:formatNumber value="${relatedProduct.oldPrice}" type="currency" currencyCode="VND"/>
                                      </span>
                                    </c:if> --%>
                                </div>

                                <!-- Stock Badge -->
                                <div class="mb-2">
                                    <c:choose>
                                        <c:when test="${relatedProduct.soLuongTon <= 0}">
                                            <span class="badge bg-danger w-100">
                                                <i class="bi bi-x-circle me-1"></i>Hết hàng
                                            </span>
                                        </c:when>
                                        <c:when test="${relatedProduct.soLuongTon < 10}">
                                            <span class="badge bg-warning text-dark w-100">
                                                <i class="bi bi-exclamation-triangle me-1"></i>Còn ${relatedProduct.soLuongTon}
                                            </span>
                                        </c:when>
                                        <c:otherwise>
                                            <span class="badge bg-success w-100">
                                                <i class="bi bi-check-circle me-1"></i>Còn hàng
                                            </span>
                                        </c:otherwise>
                                    </c:choose>
                                </div>

                                <a href="${cp}/product?id=${relatedProduct.getId()}" class="btn btn-rog w-100">
                                    <i class="bi bi-eye me-1"></i>Xem chi tiết
                                </a>
                            </div>
                        </div>
                    </div>
                </div>
            </c:forEach>
        </div>
    </div>
</c:if> 

<!-- JavaScript -->
<script>
    const maxStock = ${product.soLuongTon > 0 ? product.soLuongTon : 1};

    function increaseQuantity() {
        const quantityInput = document.getElementById('quantity');
        const currentValue = parseInt(quantityInput.value) || 1;
        if (currentValue < maxStock) {
            quantityInput.value = currentValue + 1;
            document.getElementById('qtyHidden').value = currentValue + 1;
            document.getElementById('qtyHiddenForBuyNow').value = currentValue + 1;
        }
    }

    function decreaseQuantity() {
        const quantityInput = document.getElementById('quantity');
        const currentValue = parseInt(quantityInput.value) || 1;
        if (currentValue > 1) {
            quantityInput.value = currentValue - 1;
            document.getElementById('qtyHidden').value = currentValue - 1;
            document.getElementById('qtyHiddenForBuyNow').value = currentValue - 1;
        }
    }

// Sync quantity field to hidden input for form submit
    document.getElementById('quantity').addEventListener('input', function () {
        const value = parseInt(this.value) || 1;
        this.value = Math.max(1, Math.min(maxStock, value));
        document.getElementById('qtyHidden').value = this.value;
    });

// Add animation when adding to cart
    document.querySelector('form[action*="cart"]').addEventListener('submit', function (e) {
        const btn = this.querySelector('button[type="submit"]');
        btn.innerHTML = '<i class="bi bi-check2 me-2"></i>Đã thêm!';
        btn.classList.add('btn-success');
        btn.classList.remove('btn-rog');

        setTimeout(() => {
            btn.innerHTML = '<i class="bi bi-cart-plus me-2"></i>Thêm vào giỏ hàng';
            btn.classList.add('btn-rog');
            btn.classList.remove('btn-success');
        }, 1500);
    });
</script>

<style>
    /* Enhanced Add to Cart Button */
    .btn-rog {
        transition: all 0.3s ease;
    }

    .btn-rog:hover {
        transform: translateY(-2px);
        box-shadow: 0 8px 20px rgba(255, 0, 102, 0.4);
    }

    .btn-rog:active {
        transform: translateY(0);
    }

    /* Quantity Buttons Enhancement */
    .btn-outline-secondary {
        transition: all 0.2s ease;
    }

    .btn-outline-secondary:hover {
        transform: scale(1.1);
        background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
        border-color: #667eea;
        color: white;
    }

    .btn-outline-secondary:active {
        transform: scale(0.95);
    }

    /* Product Image Hover Effect */
    .product-detail-image img {
        transition: transform 0.5s ease;
    }

    .product-detail-image:hover img {
        transform: scale(1.05);
    }

    /* Badge Animations */
    .rating-badge, .discount-badge {
        animation: pulse 2s infinite;
    }

    @keyframes pulse {
        0%, 100% {
            transform: scale(1);
        }
        50% {
            transform: scale(1.05);
        }
    }

    .discount-badge {
        background: linear-gradient(135deg, #e74c3c, #c0392b);
        color: white;
        font-weight: 700;
        box-shadow: 0 4px 15px rgba(231, 76, 60, 0.4);
    }

    .rating-badge {
        background: linear-gradient(135deg, #f39c12, #e67e22);
        color: white;
        font-weight: 700;
        box-shadow: 0 4px 15px rgba(243, 156, 18, 0.4);
    }
</style>


<%@ include file="/WEB-INF/views/layout_footer.jspf" %>
