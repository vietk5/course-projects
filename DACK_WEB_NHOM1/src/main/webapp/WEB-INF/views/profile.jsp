<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ include file="layout_header.jspf" %>

<div class="container my-4">
  <div class="row">
    <!-- Sidebar -->
    <div class="col-md-3">
      <div class="card border-0 shadow-sm">
        <div class="card-body">
          <h6 class="card-title mb-3">Tài khoản của tôi</h6>
          <div class="list-group list-group-flush">
            <a href="#profile" class="list-group-item list-group-item-action active" data-bs-toggle="tab">
              <i class="bi bi-person me-2"></i>Thông tin cá nhân
            </a>
            <a href="#password" class="list-group-item list-group-item-action" data-bs-toggle="tab">
              <i class="bi bi-lock me-2"></i>Đổi mật khẩu
            </a>
            <a href="${pageContext.request.contextPath}/orders" class="list-group-item list-group-item-action">
              <i class="bi bi-bag me-2"></i>Đơn hàng
            </a>
            <a href="${pageContext.request.contextPath}/logout" class="list-group-item list-group-item-action text-danger">
              <i class="bi bi-box-arrow-right me-2"></i>Đăng xuất
            </a>
          </div>
        </div>
      </div>
    </div>

    <!-- Main Content -->
    <div class="col-md-9">
      <div class="tab-content">
        <!-- Thông tin cá nhân -->
        <div class="tab-pane fade show active" id="profile">
          <div class="card border-0 shadow-sm">
            <div class="card-body p-4">
              <h5 class="card-title mb-4">Thông tin cá nhân</h5>
              
              <c:if test="${not empty success}">
                <div class="alert alert-success alert-dismissible fade show" role="alert">
                  <i class="bi bi-check-circle me-2"></i>${success}
                  <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
                </div>
              </c:if>
              
              <c:if test="${not empty error}">
                <div class="alert alert-danger alert-dismissible fade show" role="alert">
                  <i class="bi bi-exclamation-triangle me-2"></i>${error}
                  <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
                </div>
              </c:if>

              <form method="post" action="${pageContext.request.contextPath}/profile">
                <input type="hidden" name="action" value="update">
                
                <div class="row g-3">
                  <div class="col-md-6">
                    <label class="form-label">Họ tên <span class="text-danger">*</span></label>
                    <input type="text" class="form-control" name="fullName" 
                           value="${khachHang.ten}" required>
                  </div>
                  
                  <div class="col-md-6">
                    <label class="form-label">Email</label>
                    <input type="email" class="form-control" value="${khachHang.email}" disabled>
                    <small class="text-muted">Email không thể thay đổi</small>
                  </div>
                  
                  <div class="col-md-6">
                    <label class="form-label">Số điện thoại</label>
                    <input type="tel" class="form-control" name="phone" 
                           value="${khachHang.sdt}" placeholder="Chưa cập nhật">
                  </div>
                  
                  <div class="col-md-6">
                    <label class="form-label">Hạng thành viên</label>
                    <input type="text" class="form-control" 
                           value="${khachHang.hangThanhVien}" disabled>
                  </div>
                  
                  <div class="col-12">
                    <button type="submit" class="btn btn-rog">
                      <i class="bi bi-save me-2"></i>Lưu thay đổi
                    </button>
                  </div>
                </div>
              </form>
            </div>
          </div>
        </div>

        <!-- Đổi mật khẩu -->
        <div class="tab-pane fade" id="password">
          <div class="card border-0 shadow-sm">
            <div class="card-body p-4">
              <h5 class="card-title mb-4">Đổi mật khẩu</h5>
              
              <c:if test="${not empty successPassword}">
                <div class="alert alert-success alert-dismissible fade show" role="alert">
                  <i class="bi bi-check-circle me-2"></i>${successPassword}
                  <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
                </div>
              </c:if>
              
              <c:if test="${not empty errorPassword}">
                <div class="alert alert-danger alert-dismissible fade show" role="alert">
                  <i class="bi bi-exclamation-triangle me-2"></i>${errorPassword}
                  <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
                </div>
              </c:if>

              <form method="post" action="${pageContext.request.contextPath}/profile">
                <input type="hidden" name="action" value="changePassword">
                
                <div class="row g-3">
                  <div class="col-12">
                    <label class="form-label">Mật khẩu hiện tại <span class="text-danger">*</span></label>
                    <input type="password" class="form-control" name="currentPassword" required>
                  </div>
                  
                  <div class="col-md-6">
                    <label class="form-label">Mật khẩu mới <span class="text-danger">*</span></label>
                    <input type="password" class="form-control" name="newPassword" 
                           minlength="6" required>
                    <small class="text-muted">Tối thiểu 6 ký tự</small>
                  </div>
                  
                  <div class="col-md-6">
                    <label class="form-label">Xác nhận mật khẩu mới <span class="text-danger">*</span></label>
                    <input type="password" class="form-control" name="confirmPassword" 
                           minlength="6" required>
                  </div>
                  
                  <div class="col-12">
                    <button type="submit" class="btn btn-rog">
                      <i class="bi bi-key me-2"></i>Đổi mật khẩu
                    </button>
                  </div>
                </div>
              </form>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</div>

<%@ include file="layout_footer.jspf" %>

