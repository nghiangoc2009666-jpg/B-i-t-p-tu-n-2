<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>
<html><head><title>Đăng nhập</title></head>
<body>
<div class="card auth-card"><div class="card-body p-4">
    <div class="auth-icon"><i class="bi bi-box-arrow-in-right"></i></div>
    <h4 class="text-center mb-4">Đăng nhập vào hệ thống</h4>
    <c:if test="${alert != null}"><div class="alert alert-danger"><c:out value="${alert}"/></div></c:if>
    <c:if test="${inactiveUser != null}">
        <c:url value="/verify-account" var="verifyUrl"><c:param name="u" value="${inactiveUser}"/></c:url>
        <div class="mb-3"><a href="${verifyUrl}">Nhập mã OTP để kích hoạt tài khoản</a></div>
    </c:if>
    <c:if test="${param.activated == 'true'}"><div class="alert alert-success">Kích hoạt tài khoản thành công! Hãy đăng nhập.</div></c:if>
    <c:if test="${param.reset == 'true'}"><div class="alert alert-success">Đặt lại mật khẩu thành công! Hãy đăng nhập bằng mật khẩu mới.</div></c:if>

    <form action="${pageContext.request.contextPath}/login" method="post" class="needs-validation" novalidate>
        <t:field name="username" label="Tài khoản" icon="person" value="${param.username}" required="true" msg="Vui lòng nhập tài khoản"/>
        <t:field name="password" label="Mật khẩu" icon="lock" type="password" required="true" msg="Vui lòng nhập mật khẩu"/>
        <div class="form-check mb-3">
            <input class="form-check-input" type="checkbox" name="remember" id="remember">
            <label class="form-check-label" for="remember">Nhớ tôi</label>
        </div>
        <button type="submit" class="btn btn-primary w-100">Đăng nhập</button>
        <div class="text-center mt-3"><a href="${pageContext.request.contextPath}/forgot-password">Quên mật khẩu?</a></div>
    </form>
</div></div>
<p class="text-center text-muted">Chưa có tài khoản? <a href="${pageContext.request.contextPath}/register">Đăng ký</a></p>
</body></html>
