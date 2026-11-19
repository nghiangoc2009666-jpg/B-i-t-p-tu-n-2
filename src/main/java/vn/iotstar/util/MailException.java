package vn.iotstar.util;

@SuppressWarnings("serial")
public class MailException extends RuntimeException {
    public MailException(String message, Throwable cause) {
        super(message, cause);
    }
}
