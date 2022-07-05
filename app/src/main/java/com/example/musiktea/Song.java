package com.example.musiktea;

import android.graphics.Bitmap;

import java.io.Serializable;

public class Song implements Serializable {

    private String title;
    private String artist;
    private String path;
    private int ID;
    private String album;
    private String position;
    private String image;

    public Song(String title, String artist, String path, int ID, String album, String position) {
        this.title = title;
        this.artist = artist;
        this.path = path;
        this.ID = ID;
        this.album = album;
        this.position = position;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getAlbum() {
        return album;
    }

    public void setAlbum(String album) {
        this.album = album;
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public int getID() {
        return ID;
    }

    public void setID(int ID) {
        this.ID = ID;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }
}
