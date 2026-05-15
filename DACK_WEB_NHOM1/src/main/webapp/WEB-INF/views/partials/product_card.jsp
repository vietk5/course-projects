<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c"  uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>

<c:set var="cp" value="${pageContext.request.contextPath}" />
<c:set var="imgSrc" value="${empty param.image ? cp.concat('/assets/img/placeholder.jpg') : param.image}" />
<c:set var="skuVal" value="${empty param.sku ? fn:replace(param.name,' ','-') : param.sku}" />

<div class="card product-card h-100 d-flex flex-column shadow-sm">
  <!-- Image -->
  <div class="product-img-wrap position-relative">
    <img class="product-img" src="${imgSrc}" alt="${fn:escapeXml(param.name)}"/>
    <c:if test="${not empty param.rating}">
      <span class="rating-badge position-absolute top-0 start-0 m-2 px-2 py-1 rounded-pill">
        ★ ${param.rating}
      </span>
    </c:if>
  </div>

  <!-- Body -->
  <div class="card-body d-flex flex-column">
    <div class="small text-muted product-meta mb-1">
      <c:out value="${param.brand}" /> • <c:out value="${param.category}" />
    </div>

    <h6 class="product-title mb-2" title="${fn:escapeXml(param.name)}">
      <c:out value="${param.name}" />
    </h6>

    <!-- Price -->
    <div class="price-block mb-2">
      <c:choose>
        <c:when test="${not empty param.oldPrice and param.oldPrice ne '0'}">
          <div class="old-price text-decoration-line-through text-muted">
            <fmt:formatNumber value="${param.oldPrice}" type="number" groupingUsed="true" maxFractionDigits="0"/> đ
          </div>
        </c:when>
        <c:otherwise>
          <div class="old-price">&nbsp;</div>
        </c:otherwise>
      </c:choose>

      <div class="current-price">
        <fmt:formatNumber value="${param.price}" type="number" groupingUsed="true" maxFractionDigits="0"/> đ
      </div>
    </div>

    <!-- Stock Status -->
    <c:if test="${not empty param.stock}">
      <div class="stock-status mb-2">
        <c:choose>
          <c:when test="${param.stock <= 0}">
            <span class="badge bg-danger">
              <i class="bi bi-x-circle me-1"></i>Hết hàng
            </span>
          </c:when>
          <c:when test="${param.stock < 10}">
            <span class="badge bg-warning text-dark">
              <i class="bi bi-exclamation-triangle me-1"></i>Chỉ còn ${param.stock} sản phẩm
            </span>
          </c:when>
          <c:otherwise>
            <span class="badge bg-success">
              <i class="bi bi-check-circle me-1"></i>Còn hàng (${param.stock})
            </span>
          </c:otherwise>
        </c:choose>
      </div>
    </c:if>

    <!-- Actions -->
    <div class="mt-auto d-grid gap-2 product-foot">
      <form method="post" action="${cp}/cart" class="mb-0">
        <input type="hidden" name="action" value="add">
        <input type="hidden" name="productId" value="${param.id}">
        <input type="hidden" name="qty" value="1">
        <button type="submit" class="btn btn-sm btn-rog w-100" ${not empty param.stock && param.stock <= 0 ? 'disabled' : ''}>
          <i class="bi bi-cart-plus me-1"></i>${not empty param.stock && param.stock <= 0 ? 'Hết hàng' : 'Thêm vào giỏ'}
        </button>
      </form>
      <form method="post" action="${cp}/checkout" class="mb-0">
        <input type="hidden" name="action" value="buy_now">
        <input type="hidden" name="productId" value="${param.id}">
        <input type="hidden" name="qty" value="1">
        <button class="btn btn-rog w-100" type="submit" ${param.stock <= 0 ? 'disabled' : ''}>
            <i class="bi bi-cart-plus me-2"></i>
            ${param.stock > 0 ? 'Mua ngay' : 'Hết hàng'}
        </button>
      </form>
      <a class="btn btn-sm btn-outline-light-subtle" 
         href="${cp}/product?id=${param.id}">
        <i class="bi bi-eye me-1"></i>Xem chi tiết
      </a>
    </div>
  </div>
</div>

