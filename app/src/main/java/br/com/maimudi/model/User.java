package br.com.maimudi.model;

import android.widget.ImageView;

import java.io.Serializable;

/**
 * Created by brunolemgruber on 23/03/16.
 */
public class User implements Serializable {

    private String nome;

    private String email;

    private String nickname;

    private String uid;

    private ImageView imageProfile;

    private ImageView imageBackground;

    private String profile_img;

    private String background_img;

    private String one_signal_id;

    private String status;

    public User(){

    }

    public User(String nome,String nickname,String one_signal_id,String status){
        this.nome = nome;
        this.nickname = nickname;
        this.one_signal_id = one_signal_id;
        this.status = status;
    }

    public User(String email, String nome, String profileImg){
        this.email = email;
        this.nome = nome;
        this.profile_img = profileImg;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public ImageView getImageProfile() {
        return imageProfile;
    }

    public void setImageProfile(ImageView imageProfile) {
        this.imageProfile = imageProfile;
    }

    public ImageView getImageBackground() {
        return imageBackground;
    }

    public void setImageBackground(ImageView imageBackground) {
        this.imageBackground = imageBackground;
    }

    public String getProfile_img() {
        return profile_img;
    }

    public void setProfile_img(String profile_img) {
        this.profile_img = profile_img;
    }

    public String getBackground_img() {
        return background_img;
    }

    public void setBackground_img(String background_img) {
        this.background_img = background_img;
    }

    public String getOne_signal_id() {
        return one_signal_id;
    }

    public void setOne_signal_id(String one_signal_id) {
        this.one_signal_id = one_signal_id;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
