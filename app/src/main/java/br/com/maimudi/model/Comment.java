package br.com.maimudi.model;

import java.io.Serializable;

/**
 * Created by brunolemgruber on 20/03/16.
 */
public class Comment implements Serializable {
    private String idComment;
    private String comment;
    private String user_img;
    private String nickname;
    private String user;
    private long timestamp;

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getUser_img() {
        return user_img;
    }

    public void setUser_img(String user_img) {
        this.user_img = user_img;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getIdComment() {
        return idComment;
    }

    public void setIdComment(String idComment) {
        this.idComment = idComment;
    }

    public long getTimestamp() { return timestamp; }

    public void setTimestamp(long timestamp) { this.timestamp = timestamp; }

    public String getUser() { return user; }

    public void setUser(String user) { this.user = user; }
}
