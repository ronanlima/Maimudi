package br.com.maimudi.model;

import java.io.Serializable;
import java.util.List;

/**
 * Created by brunolemgruber on 16/03/16.
 */
public class Profile implements Serializable {

    private String nickName;

    private String user;

    private String mudis;

    private String listening;

    private String listener;

    private int img;

    private List<Timeline> timeline;

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getListening() {
        return listening;
    }

    public void setListening(String listening) {
        this.listening = listening;
    }

    public String getListener() {
        return listener;
    }

    public void setListener(String listener) {
        this.listener = listener;
    }

    public int getImg() {
        return img;
    }

    public void setImg(int img) {
        this.img = img;
    }

    public String getMudis() {
        return mudis;
    }

    public void setMudis(String mudis) {
        this.mudis = mudis;
    }

    public List<Timeline> getTimeline() {
        return timeline;
    }

    public void setTimeline(List<Timeline> timeline) {
        this.timeline = timeline;
    }
}
