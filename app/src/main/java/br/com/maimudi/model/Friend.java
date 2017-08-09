package br.com.maimudi.model;

import java.io.Serializable;

/**
 * Created by brunolemgruber on 16/03/16.
 */
public class Friend implements Serializable {

    private String nickname;
    private String nome;
    private String user;
    private String imgUrl;
    private String mudis;
    private String listener;
    private int img;
    private boolean isFollowing;
    //Utilizado na v1 para evitar de fazer uma nova consulta, quando o usuário selecionar um amigo
    //na página de amigos e redirecionar para a página de perfil do usuário
    private int qtdAmigos;
    private String status;
    private String one_signal_id;


    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getMudis() {
        return mudis;
    }

    public void setMudis(String mudis) {
        this.mudis = mudis;
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

    public boolean isFollowing() {
        return isFollowing;
    }

    public void setFollowing(boolean following) {
        isFollowing = following;
    }

    public String getNome() { return nome; }

    public void setNome(String nome) { this.nome = nome; }

    public String getImgUrl() { return imgUrl; }

    public void setImgUrl(String imgUrl) { this.imgUrl = imgUrl; }

    public int getQtdAmigos() { return qtdAmigos; }

    public void setQtdAmigos(int qtdAmigos) { this.qtdAmigos = qtdAmigos; }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getOne_signal_id() {
        return one_signal_id;
    }

    public void setOne_signal_id(String one_signal_id) {
        this.one_signal_id = one_signal_id;
    }
}
