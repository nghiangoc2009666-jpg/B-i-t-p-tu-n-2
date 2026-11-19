package vn.iotstar.entity;

import java.io.Serializable;
import java.sql.Date;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

/** Bảng "users". Quan hệ 1-n với Video (một user có nhiều video). */
@Entity
@Table(name = "users")
public class User implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;

    @Column(name = "email", columnDefinition = "nvarchar(255) not null", unique = true)
    private String email;

    @Column(name = "username", columnDefinition = "nvarchar(100) not null", unique = true)
    private String userName;

    @Column(name = "fullname", columnDefinition = "nvarchar(255) not null")
    private String fullName;

    /** Lưu mã băm BCrypt (60 ký tự), không lưu mật khẩu thô. */
    @Column(name = "password", columnDefinition = "nvarchar(255) not null")
    private String passWord;

    @Column(name = "avatar", columnDefinition = "nvarchar(255) null")
    private String avatar;

    // 1: admin, 2: manager, 5: user
    @Column(name = "roleid")
    private int roleid;

    @Column(name = "phone", columnDefinition = "nvarchar(20) null")
    private String phone;

    @Column(name = "createddate")
    private Date createdDate;

    // ---- Kích hoạt tài khoản / OTP ----
    /** null (dữ liệu cũ) được coi là đã kích hoạt. */
    @Column(name = "active")
    private Boolean active;

    @Column(name = "otp_hash", columnDefinition = "nvarchar(255) null")
    private String otpHash;

    @Column(name = "otp_expiry")
    private LocalDateTime otpExpiry;

    @Column(name = "otp_purpose", columnDefinition = "nvarchar(20) null")
    private String otpPurpose;

    @Column(name = "otp_attempts")
    private Integer otpAttempts;

    @OneToMany(mappedBy = "user")
    private List<Video> videos = new ArrayList<>();

    public User() {
    }

    public User(String email, String userName, String fullName, String passWord, String avatar, int roleid,
            String phone, Date createdDate) {
        this.email = email;
        this.userName = userName;
        this.fullName = fullName;
        this.passWord = passWord;
        this.avatar = avatar;
        this.roleid = roleid;
        this.phone = phone;
        this.createdDate = createdDate;
        this.active = Boolean.TRUE;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getUserName() { return userName; }
    public void setUserName(String userName) { this.userName = userName; }
    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }
    public String getPassWord() { return passWord; }
    public void setPassWord(String passWord) { this.passWord = passWord; }
    public String getAvatar() { return avatar; }
    public void setAvatar(String avatar) { this.avatar = avatar; }
    public int getRoleid() { return roleid; }
    public void setRoleid(int roleid) { this.roleid = roleid; }
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
    public Date getCreatedDate() { return createdDate; }
    public void setCreatedDate(Date createdDate) { this.createdDate = createdDate; }

    public boolean isActive() { return active == null || active; }
    public void setActive(boolean active) { this.active = active; }
    public String getOtpHash() { return otpHash; }
    public void setOtpHash(String otpHash) { this.otpHash = otpHash; }
    public LocalDateTime getOtpExpiry() { return otpExpiry; }
    public void setOtpExpiry(LocalDateTime otpExpiry) { this.otpExpiry = otpExpiry; }
    public String getOtpPurpose() { return otpPurpose; }
    public void setOtpPurpose(String otpPurpose) { this.otpPurpose = otpPurpose; }
    public int getOtpAttempts() { return otpAttempts == null ? 0 : otpAttempts; }
    public void setOtpAttempts(int otpAttempts) { this.otpAttempts = otpAttempts; }

    public List<Video> getVideos() { return videos; }
    public void setVideos(List<Video> videos) { this.videos = videos; }

    public void clearOtp() {
        this.otpHash = null;
        this.otpExpiry = null;
        this.otpPurpose = null;
        this.otpAttempts = 0;
    }
}
