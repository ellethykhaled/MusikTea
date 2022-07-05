package com.example.musiktea;

public class SearchItem {
    final public static int SONG = 0;
    final public static int ALBUM = 1;
    final public static int ARTIST = 2;
    final public static int FOLDER = 3;
    final public static int PLAYLIST = 4;

    final public static int SONGTITLE = 5;
    final public static int ALBUMTITLE = 6;
    final public static int ARTISTTITLE = 7;
    final public static int FOLDERTITLE = 8;
    final public static int PLAYLISTTITLE = 9;


    int type;
    Object object;
    int ID;

    public SearchItem(int type, Object object) {
        this.type = type;
        this.object = object;
    }
    public SearchItem(int type) {
        this.type = type;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public Object getObject() {
        return object;
    }

    public void setObject(Object object) {
        this.object = object;
    }

    public int getID() {
        return ID;
    }

    public void setID(int ID) {
        this.ID = ID;
    }
}