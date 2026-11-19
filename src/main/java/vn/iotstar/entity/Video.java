package vn.iotstar.entity;

import java.io.Serializable;
import java.util.Date;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;

/** Bảng Videos. Mỗi video thuộc về 1 user (n-1) và (tùy chọn) 1 category. */
@Entity
@Table(name = "Videos")
@NamedQuery(name = "Video.findAll", query = "SELECT v FROM Video v")
public class Video implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "VideoId")
    private String videoId;

    /** 1: công khai (hiện ở trang chủ), 0: ẩn. */
    @Column(name = "Active")
    private int active;

    @Column(name = "Description", columnDefinition = "nvarchar(500) null")
    private String description;

    /** Tên file ảnh đã upload hoặc link http(s). */
    @Column(name = "Poster", columnDefinition = "nvarchar(500) null")
    private String poster;

    @Column(name = "Title", columnDefinition = "nvarchar(500) null")
    private String title;

    @Column(name = "Views")
    private int views;

    /** Tên file video đã upload hoặc link http(s) (mp4 / YouTube). */
    @Column(name = "VideoUrl", columnDefinition = "nvarchar(500) null")
    private String videoUrl;

    @Column(name = "CreatedDate")
    @Temporal(TemporalType.TIMESTAMP)
    private Date createdDate;

    // bi-directional many-to-one association to Category
    @ManyToOne
    @JoinColumn(name = "CategoryId")
    private Category category;

    // bi-directional many-to-one association to User (quan hệ 1-n: User 1 - n Video)
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    public Video() {
    }

    public String getVideoId() { return videoId; }
    public void setVideoId(String videoId) { this.videoId = videoId; }
    public int getActive() { return active; }
    public void setActive(int active) { this.active = active; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getPoster() { return poster; }
    public void setPoster(String poster) { this.poster = poster; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public int getViews() { return views; }
    public void setViews(int views) { this.views = views; }
    public String getVideoUrl() { return videoUrl; }
    public void setVideoUrl(String videoUrl) { this.videoUrl = videoUrl; }
    public Date getCreatedDate() { return createdDate; }
    public void setCreatedDate(Date createdDate) { this.createdDate = createdDate; }
    public Category getCategory() { return category; }
    public void setCategory(Category category) { this.category = category; }
    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }
}
