package br.com.maimudi.model;

import java.io.Serializable;

/**
 * Created by Ronan Lima on 14/08/2016.
 */
public class Post implements Serializable {

    private String id;
    private String action;
    private String feeling;
    private String lyric;
    private String nickname;
    private Long timestamp;
    private String to;//dedicado a
    private String user;
    private String user_img;
    private String preview;
    private String post_img;
    private String artist;
    private String track;
    private String year;
    private String country;
    private String genre;

    public Post() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
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

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public Long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }

    public String getNickname() { return nickname; }

    public void setNickname(String nickname) { this.nickname = nickname; }

    public String getUser_img() { return user_img; }

    public void setUser_img(String user_img) { this.user_img = user_img; }

    public String getPreview() { return preview; }

    public void setPreview(String preview) { this.preview = preview; }

    public String getPost_img() { return post_img; }

    public void setPost_img(String post_img) { this.post_img = post_img; }

    public String getArtist() { return artist; }

    public void setArtist(String artist) { this.artist = artist; }

    public String getTrack() { return track; }

    public void setTrack(String track) { this.track = track; }

    public String getYear() { return year; }

    public void setYear(String year) { this.year = year; }

    public String getCountry() { return country; }

    public void setCountry(String country) { this.country = country; }

    public String getGenre() { return genre; }

    public void setGenre(String genre) { this.genre = genre; }
}
