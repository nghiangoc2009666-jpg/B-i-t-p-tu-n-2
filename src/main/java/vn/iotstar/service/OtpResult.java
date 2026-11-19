package vn.iotstar.service;

/** Kết quả các thao tác OTP (kích hoạt tài khoản, quên mật khẩu). */
public enum OtpResult {
    OK, INVALID, EXPIRED, TOO_MANY, NOT_FOUND, TOO_SOON, ALREADY_ACTIVE, CONFLICT
}
