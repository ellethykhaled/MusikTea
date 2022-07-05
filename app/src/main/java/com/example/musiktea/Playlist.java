package com.example.musiktea;

import android.graphics.Bitmap;

import java.io.Serializable;
import java.util.ArrayList;

public class Playlist implements Serializable {
    private String playlistName;
    private Bitmap playlistImage;
    private ArrayList<Song> songs;

    public Playlist(String playlistName, Bitmap playlistImage) {
        this.playlistImage = playlistImage;
        this.playlistName = playlistName;
    }

    public String getPlaylistName() {
        return playlistName;
    }

    public void setPlaylistName(String playlistName) {
        this.playlistName = playlistName;
    }

    public Bitmap getPlaylistImage() {
        return playlistImage;
    }

    public void setPlaylistImage(Bitmap playlistImage) {
        this.playlistImage = playlistImage;
    }

    public void addSongToPlaylist(Song song) {
        songs.add(song);
    }
}
