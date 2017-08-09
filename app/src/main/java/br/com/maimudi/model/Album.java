package br.com.maimudi.model;

import java.io.Serializable;
import java.util.List;

/**
 * Created by brunolemgruber on 17/03/16.
 */
public class Album implements Serializable {

    private String nome;

    private int img;

    private List<Music> musics;

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

    public List<Music> getMusics() {
        return musics;
    }

    public void setMusics(List<Music> musics) {
        this.musics = musics;
    }
}
