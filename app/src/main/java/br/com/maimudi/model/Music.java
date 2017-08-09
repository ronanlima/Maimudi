package br.com.maimudi.model;

import android.graphics.Bitmap;
import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by brunolemgruber on 17/03/16.
 */
public class Music implements Parcelable {
    private String artista;
    private String musica;
    private String album;
    private int img;
    private String imgSpotify;
    private String previewUrl;
    //private String mudis;
    private Bitmap bitmap;

    public Music() {
    }

    private Music(Parcel in) {
        artista = in.readString();
        musica = in.readString();
        //img = in.readInt();
        imgSpotify = in.readString();
        previewUrl = in.readString();
        //mudis = in.readString();
        //bitmap = in.readParcelable(Bitmap.class.getClassLoader());
    }

    public static final Creator<Music> CREATOR = new Creator<Music>() {
        @Override
        public Music createFromParcel(Parcel in) {
            return new Music(in);
        }

        @Override
        public Music[] newArray(int size) {
            return new Music[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(artista);
        dest.writeString(musica);
        dest.writeString(imgSpotify);
        dest.writeString(previewUrl);
        //dest.writeParcelable(bitmap, flags);
    }

    public String getArtista() {
        return artista;
    }

    public void setArtista(String artista) {
        this.artista = artista;
    }

    public String getMusica() {
        return musica;
    }

    public void setMusica(String musica) {
        this.musica = musica;
    }

    public int getImg() {
        return img;
    }

    public void setImg(int img) {
        this.img = img;
    }

    /*public String getMudis() {
        return mudis;
    }

    public void setMudis(String mudis) {
        this.mudis = mudis;
    }*/

    public String getPreviewUrl() { return previewUrl; }

    public void setPreviewUrl(String previewUrl) { this.previewUrl = previewUrl; }

    public String getImgSpotify() {
        return imgSpotify;
    }

    public void setImgSpotify(String imgSpotify) {
        this.imgSpotify = imgSpotify;
    }

    public Bitmap getBitmap() {
        return bitmap;
    }

    public void setBitmap(Bitmap bitmap) {
        this.bitmap = bitmap;
    }

    public String getAlbum() {
        return album;
    }

    public void setAlbum(String album) {
        this.album = album;
    }
}
