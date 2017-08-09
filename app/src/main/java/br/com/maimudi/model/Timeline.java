package br.com.maimudi.model;

import java.io.Serializable;
import java.util.List;

/**
 * Created by brunolemgruber on 16/03/16.
 */
public class Timeline implements Serializable, Comparable<Timeline> {

    private String id;
    private String action;
    private String feeling;
    private String lyric;
    private String nickname;
    private Long timestamp;
    private String timestamp_formatado;
    private String to;
    private String user;
    private String user_img;
    private String preview;
    private String post_img;
    private Integer likes;
    private String commentsNumber;
    private int img;
    private int sing;
    private int imgComment;
    private boolean comment;
    private boolean isCantandoJunto = false;
    private List<Comment> comments;

    @Override
    public int compareTo(Timeline timeline) {
        return timeline.getTimestamp().compareTo(this.getTimestamp());
    }

    public String getPreview() { return preview; }

    public void setPreview(String preview) { this.preview = preview; }

    public String getPost_img() { return post_img; }

    public void setPost_img(String post_img) { this.post_img = post_img; }

    public Long getTimestamp() { return timestamp; }

    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }

    public String getFeeling() {
        return feeling;
    }

    public void setFeeling(String feeling) {
        this.feeling = feeling;
    }

    public String getLyric() {
        return lyric;
    }

    public void setLyric(String lyric) {
        this.lyric = lyric;
    }

    public Integer getLikes() {
        return likes;
    }

    public void setLikes(Integer likes) { this.likes = likes; }

    public String getCommentsNumber() {
        return commentsNumber;
    }

    public void setCommentsNumber(String commentsNumber) { this.commentsNumber = commentsNumber; }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public int getImg() {
        return img;
    }

    public void setImg(int img) {
        this.img = img;
    }

    public int getSing() {
        return sing;
    }

    public void setSing(int sing) {
        this.sing = sing;
    }

    public boolean isComment() {
        return comment;
    }

    public void setComment(boolean comment) { this.comment = comment; }

    public int getImgComment() { return imgComment;}

    public void setImgComment(int imgComment) {
        this.imgComment = imgComment;
    }

    public List<Comment> getComments() {
        return comments;
    }

    public void setComments(List<Comment> comments) {
        this.comments = comments;
    }

    public String getUser_img() {
        return user_img;
    }

    public void setUser_img(String user_img) { this.user_img = user_img; }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public boolean isCantandoJunto() { return isCantandoJunto; }

    public void setCantandoJunto(boolean cantandoJunto) { isCantandoJunto = cantandoJunto; }

    public String getNickname() { return nickname; }

    public void setNickname(String nickname) { this.nickname = nickname; }

    public String getTimestamp_formatado() { return timestamp_formatado; }

    public void setTimestamp_formatado(String timestamp_formatado) { this.timestamp_formatado = timestamp_formatado; }

    public String getTo() { return to; }

    public void setTo(String to) { this.to = to; }
}
