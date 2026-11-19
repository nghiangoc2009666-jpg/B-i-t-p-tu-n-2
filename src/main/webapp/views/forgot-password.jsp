<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>
<html><head><title>Quên mật khẩu</title></head>
<body>
<div class="card auth-card"><div class="card-body p-4">
    <div class="auth-icon"><i class="bi bi-key"></i></div>
    <h4 class="text-center mb-2">Quên mật khẩu</h4>
    <p class="text-muted text-center">Nhập email đã đăng ký, hệ thống sẽ gửi mã OTP để đặt lại mật khẩu.</p>
    <c:if test="${alert != null}"><div class="alert alert-danger"><c:out value="${alert}"/></div></c:if>
    <form action="${pageContext.request.contextPath}/forgot-password" method="post" class="needs-validation" novalidate>
        <t:field name="email" label="Email" icon="envelope" type="email" value="${param.email}" required="true" msg="Email không hợp lệ"/>
        <button type="submit" class="btn btn-primary w-100">Gửi mã xác nhận</button>
    </form>
</div></div>
<p class="text-center"><a href="${pageContext.request.contextPath}/login">Quay lại đăng nhập</a></p>
</body></html>
