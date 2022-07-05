package com.example.musiktea;

import android.graphics.Bitmap;
import android.net.Uri;

import androidx.recyclerview.widget.RecyclerView;

import java.io.Serializable;

public class Album implements Serializable {
    private String AlbumName;
    private Bitmap AlbumImage;
    private String ArtistName;

    public Album(String AlbumName, Bitmap AlbumImage, String ArtistName) {
        this.AlbumImage = AlbumImage;
        this.AlbumName = AlbumName;
        this.ArtistName = ArtistName;
    }

    public String getAlbumName() {
        return AlbumName;
    }

    public void setAlbumName(String AlbumName) {
        this.AlbumName = AlbumName;
    }

    public Bitmap getAlbumImage() {
        return AlbumImage;
    }

    public void setAlbumImage(Bitmap AlbumImage) {
        this.AlbumImage = AlbumImage;
    }

    public String getArtistName() {
        return ArtistName;
    }

    public void setArtistName(String artistName) {
        ArtistName = artistName;
    }
}
