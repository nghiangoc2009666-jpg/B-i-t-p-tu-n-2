<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>
<html><head><title>Đăng ký</title></head>
<body>
<div class="card auth-card"><div class="card-body p-4">
    <div class="auth-icon"><i class="bi bi-person-plus"></i></div>
    <h4 class="text-center mb-4">Tạo tài khoản mới</h4>
    <c:if test="${alert != null}"><div class="alert alert-danger"><c:out value="${alert}"/></div></c:if>
    <form action="${pageContext.request.contextPath}/register" method="post" class="needs-validation" novalidate>
        <t:field name="username" label="Tài khoản" icon="person" value="${param.username}" required="true" pattern="[A-Za-z0-9_.]{3,30}" msg="3 đến 30 ký tự: chữ, số, _ hoặc ."/>
        <t:field name="fullname" label="Họ tên" icon="card-text" value="${param.fullname}" required="true" minlength="2" maxlength="100" msg="Họ tên từ 2 đến 100 ký tự"/>
        <t:field name="email" label="Email" icon="envelope" type="email" value="${param.email}" required="true" msg="Email không hợp lệ"/>
        <t:field name="phone" label="Số điện thoại" icon="telephone" value="${param.phone}" required="true" pattern="0[0-9]{9}" msg="Gồm 10 chữ số, bắt đầu bằng 0"/>
        <t:field name="password" label="Mật khẩu" icon="lock" type="password" required="true" minlength="6" maxlength="50" msg="Mật khẩu từ 6 đến 50 ký tự"/>
        <t:field name="repassword" label="Nhập lại mật khẩu" icon="lock-fill" type="password" required="true" match="password" msg="Mật khẩu nhập lại không khớp"/>
        <button type="submit" class="btn btn-primary w-100">Tạo tài khoản</button>
    </form>
</div></div>
<p class="text-center text-muted">Đã có tài khoản? <a href="${pageContext.request.contextPath}/login">Đăng nhập</a></p>
</body></html>
