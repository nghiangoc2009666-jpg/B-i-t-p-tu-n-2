<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <title>Kích hoạt tài khoản</title>
</head>
<body>
<div class="container" style="max-width: 440px; margin-top: 60px;">
    <div class="card shadow-sm">
        <div class="card-body p-4">
            <h4 class="text-center mb-3">Kích hoạt tài khoản</h4>
            <p class="text-muted text-center">Mã OTP gồm 6 chữ số đã được gửi tới email đăng ký của tài khoản
                <strong><c:out value="${verifyUser}" /></strong>. Mã có hiệu lực 5 phút.</p>

            <c:if test="${param.registered == 'true' && param.mailerror != 'true' && alert == null && info == null}">
                <div class="alert alert-success">Đăng ký thành công! Hãy nhập mã OTP để kích hoạt.</div>
            </c:if>
            <c:if test="${param.mailerror == 'true' && alert == null && info == null}">
                <div class="alert alert-warning">Đã tạo tài khoản nhưng chưa gửi được email. Bấm "Gửi lại mã".</div>
            </c:if>
            <c:if test="${alert != null}"><div class="alert alert-danger"><c:out value="${alert}" /></div></c:if>
            <c:if test="${info != null}"><div class="alert alert-info"><c:out value="${info}" /></div></c:if>

            <form action="${pageContext.request.contextPath}/verify-account" method="post">
                <input type="hidden" name="u" value="<c:out value='${verifyUser}'/>">
                <div class="mb-3">
                    <input type="text" name="otp" class="form-control text-center fs-4" maxlength="6"
                           inputmode="numeric" autocomplete="one-time-code" placeholder="______" autofocus>
                </div>
                <button type="submit" name="action" value="verify" class="btn btn-primary w-100">Kích hoạt</button>
                <button type="submit" name="action" value="resend" class="btn btn-link w-100 mt-2">Gửi lại mã</button>
            </form>
        </div>
    </div>
    <p class="text-center text-muted mt-3"><a href="${pageContext.request.contextPath}/login">Quay lại đăng nhập</a></p>
</div>
</body>
</html>
