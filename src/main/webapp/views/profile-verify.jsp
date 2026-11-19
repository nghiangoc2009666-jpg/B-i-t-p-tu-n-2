<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>
<html><head><title>Xác nhận email mới</title></head>
<body>
<div class="card auth-card"><div class="card-body p-4">
    <div class="auth-icon"><i class="bi bi-envelope-check"></i></div>
    <h4 class="text-center mb-3">Xác nhận cập nhật hồ sơ</h4>
    <p class="text-muted text-center">Mã OTP gồm 6 chữ số đã được gửi tới email mới
        <strong><c:out value="${sessionScope.pendingProfile.email}"/></strong>. Mã có hiệu lực 5 phút.</p>
    <c:if test="${param.sent == 'true' && alert == null && info == null}"><div class="alert alert-success">Đã gửi mã OTP. Hãy kiểm tra email.</div></c:if>
    <c:if test="${alert != null}"><div class="alert alert-danger"><c:out value="${alert}"/></div></c:if>
    <c:if test="${info != null}"><div class="alert alert-info"><c:out value="${info}"/></div></c:if>
    <form action="${pageContext.request.contextPath}/profile/verify" method="post" class="needs-validation" novalidate>
        <t:field name="otp" label="Mã OTP" icon="123" required="true" pattern="[0-9]{6}" maxlength="6" msg="Mã OTP gồm 6 chữ số"/>
        <button type="submit" name="action" value="verify" class="btn btn-primary w-100">Xác nhận</button>
        <button type="submit" name="action" value="resend" class="btn btn-link w-100 mt-2" formnovalidate>Gửi lại mã</button>
        <button type="submit" name="action" value="cancel" class="btn btn-link text-muted w-100" formnovalidate>Hủy thay đổi</button>
    </form>
</div></div>
</body></html>
