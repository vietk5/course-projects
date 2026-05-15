
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>
<%@ include file="layout_header.jspf" %>

<div class="container my-4">
    <h3 class="mb-4">Giỏ hàng</h3>

    <c:choose>
        <c:when test="${empty cart}">
            <div class="alert alert-info">
                Giỏ hàng trống.
                <a href="${pageContext.request.contextPath}/home" class="btn btn-outline-light ms-2">Tiếp tục mua hàng</a>
            </div>
        </c:when>
        <c:otherwise>
            <table class="table table-dark table-striped text-center align-middle">
                <thead>
                    <tr>
                        <th>Chọn</th>
                        <th>Hình</th>
                        <th>Sản phẩm</th>
                        <th>Giá</th>
                        <th>Số lượng</th>
                        <th>Tổng</th>
                        <th></th>
                    </tr>
                </thead>
                <tbody>
                    <%-- Bỏ dòng <c:set var="sum" value="0"/> ở đây --%>
                    <c:forEach var="it" items="${cart}">
                        <tr>
                            <td>
                                <%-- **SỬA Ở ĐÂY:** Thêm class và data-price để JavaScript sử dụng --%>
                                <input type="checkbox" name="selectedItems" value="${it.sku}" class="form-check-input item-checkbox"
                                       data-price="${it.gia * it.soLuong}"/>
                            </td>
                            <td><img src="${pageContext.request.contextPath}/${it.hinh}" width="60" class="rounded"
                                     onerror="this.onerror=null; this.src='${pageContext.request.contextPath}/assets/img/laptop_placeholder.jpg';"/>
                            </td>
                            <td><a href="${pageContext.request.contextPath}/product?id=${fn:substringAfter(it.sku, 'SP-')}">${it.ten}</a></td>
                            <td><fmt:formatNumber value="${it.gia}" type="number"/> đ</td>
                            <td>
                                <div class="update-quantity d-flex justify-content-center align-items-center gap-1">
                                    <form action="${pageContext.request.contextPath}/cart" method="post">
                                        <input type="hidden" name="action" value="update"/>
                                        <input type="hidden" name="productId" value="${fn:substringAfter(it.sku, 'SP-')}"/>
                                        <input type="hidden" name="qty" value="-1"/>
                                        <button type="submit" class="btn btn-outline-light btn-sm">−</button>
                                    </form>
                                    <span class="text-white mx-2">${it.soLuong}</span>
                                    <form action="${pageContext.request.contextPath}/cart" method="post">
                                        <input type="hidden" name="action" value="update"/>
                                        <input type="hidden" name="productId" value="${fn:substringAfter(it.sku, 'SP-')}"/>
                                        <input type="hidden" name="qty" value="1"/>
                                        <button type="submit" class="btn btn-outline-light btn-sm">+</button>
                                    </form>
                                </div>
                            </td>
                            <td><fmt:formatNumber value="${it.gia * it.soLuong}" type="number"/> đ</td>
                            <td>
                                <form method="post" action="${pageContext.request.contextPath}/cart">
                                    <input type="hidden" name="action" value="remove"/>
                                    <input type="hidden" name="sku" value="${it.sku}"/>
                                    <button class="btn btn-outline-danger btn-sm">Xóa</button>
                                </form>
                            </td>
                        </tr>
                    </c:forEach>
                </tbody>
            </table>
            <form method="post" action="${pageContext.request.contextPath}/checkout">
                <div class="d-flex justify-content-between align-items-center mt-3">
                    <a href="${pageContext.request.contextPath}/home" class="btn btn-outline-light">
                        ← Tiếp tục mua hàng
                    </a>
                    <div>
                        <%-- **SỬA Ở ĐÂY:** Tạo một thẻ để JavaScript cập nhật tổng tiền --%>
                        <strong class="text-warning me-3 fs-5">Tổng tiền đã chọn:
                            <span id="selected-total">0 đ</span>
                        </strong>
                        <button type="submit" name="action" value="checkoutSelected" class="btn btn-rog" id="checkoutSelectedBtn">
                            Thanh toán sản phẩm đã chọn
                        </button>
                    </div>
                </div>
            </form>
        </c:otherwise>
    </c:choose>
</div>

<%-- **SỬA Ở ĐÂY:** Thêm đoạn script để tính toán lại tổng tiền --%>
<script>
    document.addEventListener("DOMContentLoaded", function () {
        const checkboxes = document.querySelectorAll('.item-checkbox');
        const totalDisplay = document.getElementById('selected-total');

        // Hàm để cập nhật tổng tiền
        function updateSelectedTotal() {
            let currentTotal = 0;
            checkboxes.forEach(function (checkbox) {
                // Nếu checkbox được chọn
                if (checkbox.checked) {
                    // Lấy giá trị từ thuộc tính data-price và cộng vào tổng
                    currentTotal += parseFloat(checkbox.dataset.price);
                }
            });

            // Định dạng số và hiển thị ra màn hình
            totalDisplay.textContent = currentTotal.toLocaleString('vi-VN') + ' đ';
        }

        // Thêm sự kiện "change" cho mỗi checkbox
        checkboxes.forEach(function (checkbox) {
            checkbox.addEventListener('change', updateSelectedTotal);
        });

        // Chạy hàm lần đầu khi tải trang để tổng tiền là 0
        updateSelectedTotal();
    });
    
    document.addEventListener("DOMContentLoaded", function () {
        const checkoutBtn = document.getElementById("checkoutSelectedBtn");

        if (checkoutBtn) {
            checkoutBtn.addEventListener("click", function (e) {
                e.preventDefault();

                // Lấy tất cả checkbox sản phẩm
                const checkboxes = document.querySelectorAll('input[name="selectedItems"]:checked');

                if (checkboxes.length === 0) {
                    // ⚠️ Nếu chưa chọn sản phẩm nào
                    alert("⚠️ Vui lòng chọn ít nhất một sản phẩm để thanh toán!");
                    return;
                }

                // Nếu có chọn → tạo form gửi đi
                const form = document.createElement("form");
                form.method = "post";
                form.action = "checkout"; // servlet xử lý khi chọn thanh toán
                
                const actionInput = document.createElement("input");
                actionInput.type = "hidden";
                actionInput.name = "action";
                actionInput.value = "checkoutSelected";
                form.appendChild(actionInput);
                
                checkboxes.forEach(cb => {
                    const input = document.createElement("input");
                    input.type = "hidden";
                    input.name = "selectedItems";
                    input.value = cb.value;
                    form.appendChild(input);
                });
                document.body.appendChild(form);
                form.submit();
            });
        }
    });
</script>

<%@ include file="layout_footer.jspf" %>
