package br.com.maimudi.model;

import java.io.Serializable;

/**
 * Created by brunolemgruber on 17/03/16.
 */
public class Mood implements Serializable {

    private String nome;

    private int img;

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public int getImg() {
        return img;
    }

    public void setImg(int img) {
        this.img = img;
    }
}
