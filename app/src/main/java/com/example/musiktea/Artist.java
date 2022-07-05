package com.example.musiktea;

import android.graphics.Bitmap;

import java.io.Serializable;

public class Artist implements Serializable {
    private String ArtistName;
    private Bitmap ArtistImage;
    private int trackCount;

    public Artist(String ArtistName, Bitmap ArtistImage) {
        this.ArtistName = ArtistName;
        this.ArtistImage = ArtistImage;
    }

    public Bitmap getArtistImage() {
        return ArtistImage;
    }

    public void setArtistImage(Bitmap AlbumImage) {
        this.ArtistImage = AlbumImage;
    }

    public String getArtistName() {
        return ArtistName;
    }

    public void setArtistName(String artistName) {
        ArtistName = artistName;
    }

    public int getTrackCount() {
        return trackCount;
    }

    public void setTrackCount(int trackCount) {
        this.trackCount = trackCount;
    }
}
