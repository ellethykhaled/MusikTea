package com.example.musiktea;

import java.io.File;

public class SearchItemObject {

    String itemName;
    String subName;

    public SearchItemObject(Song song) {
        this.itemName = song.getTitle();
        this.subName = song.getArtist();
    }
    public SearchItemObject(String folder) {
        this.itemName = folder;
    }
    public SearchItemObject(Playlist playlist) {
        this.itemName = playlist.getPlaylistName();
    }
    public SearchItemObject(Album album) {
        this.itemName = album.getAlbumName();
        this.subName = album.getArtistName();
    }
    public SearchItemObject(Artist artist) {
        this.itemName = artist.getArtistName();
        this.subName = String.valueOf(artist.getTrackCount());
    }

    public String getItemName() {
        return itemName;
    }

    public String getSubName() {
        return subName;
    }
}
