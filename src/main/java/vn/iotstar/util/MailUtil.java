package vn.iotstar.util;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.util.Properties;

import jakarta.mail.Authenticator;
import jakarta.mail.Message;
import jakarta.mail.MessagingException;
import jakarta.mail.PasswordAuthentication;
import jakarta.mail.Session;
import jakarta.mail.Transport;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;

/**
 * Gửi email qua SMTP. Cấu hình trong src/main/resources/mail.properties (xem mail.properties.example)
 * hoặc biến môi trường MAIL_USERNAME / MAIL_PASSWORD.
 * Nếu CHƯA cấu hình: chạy chế độ DEV, in mã OTP ra Console thay vì gửi mail.
 */
public class MailUtil {
    private static final Properties CFG = new Properties();

    static {
        try (InputStream in = MailUtil.class.getClassLoader().getResourceAsStream("mail.properties")) {
            if (in != null) {
                CFG.load(new InputStreamReader(in, StandardCharsets.UTF_8));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static String get(String key, String env, String def) {
        String v = env == null ? null : System.getenv(env);
        if (v == null || v.isBlank()) {
            v = CFG.getProperty(key, def);
        }
        return v == null ? "" : v.trim();
    }

    public static boolean isConfigured() {
        String user = get("mail.username", "MAIL_USERNAME", "");
        String pass = get("mail.password", "MAIL_PASSWORD", "");
        return !user.isEmpty() && !pass.isEmpty() && !user.contains("your_") && !pass.contains("your_");
    }

    /** Gửi email chứa mã OTP. */
    public static void sendOtp(String to, String subject, String heading, String code) {
        if (!isConfigured()) {
            System.out.println("=================================================================");
            System.out.println("[MAIL - CHẾ ĐỘ DEV] Chưa cấu hình mail.properties nên KHÔNG gửi email thật.");
            System.out.println("  Gửi tới : " + to);
            System.out.println("  Tiêu đề : " + subject);
            System.out.println("  Mã OTP  : " + code);
            System.out.println("=================================================================");
            return;
        }
        String html = "<div style=\"font-family:Arial,sans-serif;max-width:480px\">"
                + "<h3>" + heading + "</h3>"
                + "<p>Mã xác nhận (OTP) của bạn là:</p>"
                + "<p style=\"font-size:28px;font-weight:bold;letter-spacing:6px\">" + code + "</p>"
                + "<p>Mã có hiệu lực trong " + Constant.OTP_MINUTES + " phút. "
                + "Không chia sẻ mã này cho bất kỳ ai.</p>"
                + "<p style=\"color:#888\">Nếu bạn không thực hiện yêu cầu này, hãy bỏ qua email.</p></div>";
        send(to, subject, html);
    }

    public static void send(String to, String subject, String html) {
        final String user = get("mail.username", "MAIL_USERNAME", "");
        final String pass = get("mail.password", "MAIL_PASSWORD", "");
        Properties props = new Properties();
        props.put("mail.smtp.host", get("mail.host", null, "smtp.gmail.com"));
        props.put("mail.smtp.port", get("mail.port", null, "587"));
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.connectiontimeout", "10000");
        props.put("mail.smtp.timeout", "10000");
        props.put("mail.smtp.writetimeout", "10000");

        Session session = Session.getInstance(props, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(user, pass);
            }
        });
        try {
            MimeMessage msg = new MimeMessage(session);
            msg.setFrom(new InternetAddress(user, get("mail.from.name", null, "Shopping JPA"), "UTF-8"));
            msg.setRecipients(Message.RecipientType.TO, InternetAddress.parse(to));
            msg.setSubject(subject, "UTF-8");
            msg.setContent(html, "text/html; charset=UTF-8");
            Transport.send(msg);
        } catch (MessagingException | UnsupportedEncodingException e) {
            throw new MailException("Không gửi được email: " + e.getMessage(), e);
        }
    }
}
