<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>
<html><head><title>Đặt lại mật khẩu</title></head>
<body>
<div class="card auth-card"><div class="card-body p-4">
    <div class="auth-icon"><i class="bi bi-shield-lock"></i></div>
    <h4 class="text-center mb-3">Đặt lại mật khẩu</h4>
    <c:if test="${param.sent == 'true' && alert == null}"><div class="alert alert-info">Nếu email tồn tại trong hệ thống, mã OTP đã được gửi (hiệu lực 5 phút).</div></c:if>
    <c:if test="${alert != null}"><div class="alert alert-danger"><c:out value="${alert}"/></div></c:if>
    <form action="${pageContext.request.contextPath}/reset-password" method="post" class="needs-validation" novalidate>
        <t:field name="email" label="Email" icon="envelope" type="email" value="${param.email}" required="true" msg="Email không hợp lệ"/>
        <t:field name="otp" label="Mã OTP" icon="123" value="${param.otp}" required="true" pattern="[0-9]{6}" maxlength="6" msg="Mã OTP gồm 6 chữ số"/>
        <t:field name="password" label="Mật khẩu mới" icon="lock" type="password" required="true" minlength="6" maxlength="50" msg="Mật khẩu từ 6 đến 50 ký tự"/>
        <t:field name="repassword" label="Nhập lại mật khẩu mới" icon="lock-fill" type="password" required="true" match="password" msg="Mật khẩu nhập lại không khớp"/>
        <button type="submit" class="btn btn-primary w-100">Đổi mật khẩu</button>
    </form>
</div></div>
<p class="text-center"><a href="${pageContext.request.contextPath}/forgot-password">Gửi lại mã</a> | <a href="${pageContext.request.contextPath}/login">Quay lại đăng nhập</a></p>
</body></html>
