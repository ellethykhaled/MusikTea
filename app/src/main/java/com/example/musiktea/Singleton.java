package com.example.musiktea;

import android.app.Activity;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.PowerManager;
import android.util.Base64;
import android.view.View;
import android.widget.RemoteViews;
import android.widget.TextView;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;

public class Singleton {
    private static Singleton instance = null;
    private MediaPlayer mediaPlayer = null;

    private ArrayList<Song> songs;
    private int repeatState = 0;
    private boolean shuffle = false;
    private Song currentSong = null;
    private float songProgress;
    private Activity outerActivity;
    private Activity innerActivity;

    private String viewState = null;

    private ArrayList<Song> shuffledSongs = new ArrayList<>();

    private int shuffleCursor = 0;

    Uri folderImage = null;
    String folderImagePath = null;

    Uri newPlaylistImageUri = null;
    String newPlaylistName = null;
    boolean newPlaylistExist = false;
    Bitmap newPlaylistBitmap = null;

    Bitmap playingSongBitmap;

    boolean chooseSongToPlaylist = false;
    boolean updateMusicPlayerUIouter = false;
    boolean updateMusicPlayerUIinner = false;
    boolean updateMusicPlayerUIinner2 = false;

    RemoteViews collapsedView;
    RemoteViews expandedView;

    ArrayList<Integer> playListNumbers = null;

    SharedPreferences savedSettings;


    protected Singleton() {
        // Exists only to defeat instantiation.
    }

    public static Singleton getInstance() {
        if (instance == null) {
            instance = new Singleton();
        }
        return instance;
    }

    public SharedPreferences getSavedSettings() {
        return savedSettings;
    }

    public void setSavedSettings(SharedPreferences savedSettings) {
        this.savedSettings = savedSettings;
    }

    public void updateSavedSettings(String source) {
        SharedPreferences.Editor mEditor = savedSettings.edit();

        mEditor.putBoolean("shuffleState", isShuffle());
        try {
            mEditor.putString("currentSongPath", getCurrentSong().getPath());
        } catch (Exception e) {
            mEditor.putString("currentSongPath", null);
        }
        mEditor.putInt("repeatState", getRepeatState());
        if (getMediaPlayer() != null) {
            mEditor.putInt("progress", getMediaPlayer().getCurrentPosition());
        }

        mEditor.remove("initialState");
        switch (getViewState()) {
            case "Folders":
                mEditor.putInt("initialState", 1);
                break;
            case "Playlist":
                mEditor.putInt("initialState", 2);
                break;
            case "Artist":
                mEditor.putInt("initialState", 3);
                break;
            case "Album":
                mEditor.putInt("initialState", 4);
                break;
            default:
                mEditor.putInt("initialState", 0);
        }
        if (source != null)
            mEditor.putString("songSource", source);

        // 0: Songs
        // 1: Folders
        // 2: Playlist
        // 3: Artist
        // 4: Album
        // 5: Search

        mEditor.commit();
    }

    public void updateSongSource(String source) {
        SharedPreferences.Editor mEditor = savedSettings.edit();

        mEditor.putString("songSource2", source);

        mEditor.commit();
    }

    public static void setInstance(Singleton instance) {
        Singleton.instance = instance;
    }

    public MediaPlayer getMediaPlayer() {
        return mediaPlayer;
    }

    public void setMediaPlayer(MediaPlayer mediaPlayer) {
        this.mediaPlayer = mediaPlayer;
    }

    public int getRepeatState() {
        return repeatState;
    }

    public void setRepeatState(int repeatState) {
        this.repeatState = repeatState;
    }

    public ArrayList<Song> getSongs() {
        return songs;
    }

    public void setSongs(ArrayList<Song> songs) {
        if (this.songs == null)
            this.songs = new ArrayList<>();
        else
            this.songs.clear();
        this.songs.addAll(songs);
    }

    public boolean isShuffle() {
        return shuffle;
    }

    public void setShuffle(boolean shuffle) {
        this.shuffle = shuffle;
    }

    public void clearShuffledSongs(Song song) {
        shuffledSongs.clear();
        shuffleCursor = 0;
        addForwardSong(song);
    }

    public void addForwardSong(Song song) {
        if (shuffledSongs.size() < 21)
            shuffledSongs.add(song);
        else {
            shuffledSongs.remove(0);
            shuffledSongs.add(song);
        }
    }

    public void addBackwardSong(Song song) {
        if (shuffledSongs.size() < 21) {
            if (shuffleCursor > 0)
                shuffledSongs.add(--shuffleCursor, song);
            else
                shuffledSongs.add(0, song);
        } else {
            shuffledSongs.remove(shuffledSongs.size() - 1);
            shuffledSongs.add(0, song);
        }
    }

    public Song getForwardSong() {
        if (shuffleCursor < 20) {
            if (shuffledSongs.get(shuffleCursor + 1) == null)
                return null;
            return shuffledSongs.get(++shuffleCursor);
        } else
            return shuffledSongs.get(shuffleCursor);
    }

    public Song getBackwardSong() {
        if (shuffleCursor == 0)
            return null;
        setCurrentSong(shuffledSongs.get(--shuffleCursor));
        return shuffledSongs.get(shuffleCursor);
    }

    public boolean isShuffledSongsEmpty() {
        return shuffledSongs.size() == 0;
    }

    public Song getCurrentSong() {
        return currentSong;
    }

    public void setCurrentSong(Song currentSong) {
        this.currentSong = currentSong;
    }

    public float getSongProgress() {
        return songProgress;
    }

    public void setSongProgress(float songProgress) {
        this.songProgress = songProgress;
    }

    public String getViewState() {
        return viewState;
    }

    public void setViewState(String viewState) {
        this.viewState = viewState;
    }

    public Activity getOuterActivity() {
        return outerActivity;
    }

    public void setOuterActivity(Activity outerActivity) {
        this.outerActivity = outerActivity;
    }

    public Activity getInnerActivity() {
        return innerActivity;
    }

    public void setInnerActivity(Activity innerActivity) {
        this.innerActivity = innerActivity;
    }

    public Uri getFolderImage() {
        return folderImage;
    }

    public void setFolderImage(Uri folderImage) {
        this.folderImage = folderImage;
    }

    public String getFolderImagePath() {
        return folderImagePath;
    }

    public void setFolderImagePath(String folderImagePath) {
        this.folderImagePath = folderImagePath;
    }

    public Uri getNewPlaylistImageUri() {
        return newPlaylistImageUri;
    }

    public boolean isUpdateMusicPlayerUIouter() {
        return updateMusicPlayerUIouter;
    }

    public void setUpdateMusicPlayerUIouter(boolean updateMusicPlayerUIouter) {
        this.updateMusicPlayerUIouter = updateMusicPlayerUIouter;
    }

    public boolean isUpdateMusicPlayerUIinner() {
        return updateMusicPlayerUIinner;
    }

    public void setUpdateMusicPlayerUIinner(boolean updateMusicPlayerUIinner) {
        this.updateMusicPlayerUIinner = updateMusicPlayerUIinner;
    }

    public boolean isUpdateMusicPlayerUIinner2() {
        return updateMusicPlayerUIinner2;
    }

    public void setUpdateMusicPlayerUIinner2(boolean updateMusicPlayerUIinner2) {
        this.updateMusicPlayerUIinner2 = updateMusicPlayerUIinner2;
    }

    public void setNewPlaylistImageUri(Uri newPlaylistImageUri) {
        this.newPlaylistImageUri = newPlaylistImageUri;
    }

    public String getCurrentPaths() {
        String source = "";
        for (Song s : getSongs()) {
            source = source.concat(s.getPath().concat("|"));
        }
        return source;
    }

    public String getNewPlaylistName() {
        return newPlaylistName;
    }

    public void setNewPlaylistName(String newPlaylistName) {
        this.newPlaylistName = newPlaylistName;
    }

    public boolean isNewPlaylistExist() {
        return newPlaylistExist;
    }

    public void setNewPlaylistExist(boolean newPlaylistExist) {
        this.newPlaylistExist = newPlaylistExist;
    }

    public Bitmap getNewPlaylistBitmap() {
        return newPlaylistBitmap;
    }

    public void setNewPlaylistBitmap(Bitmap newPlaylistBitmap) {
        this.newPlaylistBitmap = newPlaylistBitmap;
    }

    public void savePlaylists(ArrayList<Playlist> playlistList, ArrayList<ArrayList<Song>> playlistSongs) {
        SharedPreferences.Editor mEditor = savedSettings.edit();
        String playlistNames = "";
        String playlistImages = "";
        String playlist_Songs = "";

        for (Playlist p : playlistList) {
            if (p.getPlaylistName() != null) {
                playlistNames += p.getPlaylistName() + "/";
                if (p.getPlaylistImage() != null) {
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    p.getPlaylistImage().compress(Bitmap.CompressFormat.PNG, 100, baos); //bm is the bitmap object
                    byte[] b = baos.toByteArray();
                    playlistImages += Base64.encodeToString(b, Base64.DEFAULT) + "|";
                } else
                    playlistImages += "|";
            }
        }
        for (ArrayList<Song> a : playlistSongs) {
            for (Song s : a) {
                playlist_Songs += s.getPath().concat("?");
            }
            playlist_Songs += "|";
        }
        if (songs.equals(""))
            songs = null;

        mEditor.putString("playlistNames", playlistNames);
        mEditor.putString("playlistImages", playlistImages);
        mEditor.putString("playlistSongs", playlist_Songs);
        mEditor.commit();
    }

    public boolean isChooseSongToPlaylist() {
        return chooseSongToPlaylist;
    }

    public void setChooseSongToPlaylist(boolean chooseSongToPlaylist) {
        this.chooseSongToPlaylist = chooseSongToPlaylist;
    }

    public ArrayList<Integer> getPlayListNumbers() {
        return playListNumbers;
    }

    public void setPlayListNumbers(ArrayList<Integer> playListNumbers) {
        this.playListNumbers = playListNumbers;
    }

    public Bitmap getPlayingSongBitmap() {
        return playingSongBitmap;
    }

    public void setPlayingSongBitmap(Bitmap playingSongBitmap) {
        this.playingSongBitmap = playingSongBitmap;
    }

    public void playPreviousSong() {
        if (mediaPlayer.getCurrentPosition() > 4000 && mediaPlayer.isPlaying()) {
            mediaPlayer.seekTo(0);
        } else {
            mediaPlayer.reset();
            Song song;
            if (!shuffle) {
                if (songs.get(0).getPath().equals(currentSong.getPath())) {
                    song = songs.get(songs.size() - 1);
                } else if (!songs.contains(currentSong)) {
                    int i = 0;
                    for (Song s : songs) {
                        if (s.getPath().equals(currentSong.getPath()))
                            break;
                        i++;
                    }
                    song = songs.get(i - 1);
                } else
                    song = songs.get(songs.indexOf(currentSong) - 1);
            } else {
                song = getBackwardSong();
                if (song == null) {
                    int rand;
                    do {
                        rand = (int) (Math.random() * (songs.size()));
                    }
                    while (rand == currentSong.getID());
                    song = songs.get(rand);
                    addBackwardSong(song);
                }
            }
            currentSong = song;
            playThisSong(song);
        }
    }

    public void playThisSong(Song song) {
        if (mediaPlayer == null)
            mediaPlayer = new MediaPlayer();
        else
            mediaPlayer.reset();
        try {
            mediaPlayer.setDataSource(song.getPath());
            mediaPlayer.prepare();
        } catch (IOException e) {
            e.printStackTrace();
        }
        mediaPlayer.setLooping(repeatState == 2);
        mediaPlayer.seekTo(0);

        mediaPlayer.start();
        mediaPlayer.setWakeMode(outerActivity.getApplicationContext(), PowerManager.PARTIAL_WAKE_LOCK);
        mediaPlayer.setOnCompletionListener(mediaPlayer -> {
            if (currentSong.getID() != songs.size() - 1 || repeatState != 0) {
                playNextSong(null);
                TextView tvTitle = outerActivity.findViewById(R.id.outerSongName);
                tvTitle.setText(currentSong.getTitle());
                TextView tvArtist = outerActivity.findViewById(R.id.outerArtistName);
                tvArtist.setText(currentSong.getArtist());
                mediaPlayer.setWakeMode(outerActivity.getApplicationContext(), PowerManager.PARTIAL_WAKE_LOCK);
            }
            updateMusicPlayerUIouter = true;
            updateMusicPlayerUIinner = true;
            updateMusicPlayerUIinner2 = true;
        });
        updateMusicPlayerUIouter = true;
        updateMusicPlayerUIinner = true;
        updateMusicPlayerUIinner2 = true;

    }

    public void playNextSong(View view) {
        if (mediaPlayer == null)
            mediaPlayer = new MediaPlayer();
        else
            mediaPlayer.reset();
        Song song = null;
        if (!shuffle) {
            if (songs.get(songs.size() - 1).getPath().equals(currentSong.getPath())) {
                if (repeatState == 1) {
                    song = songs.get(0);
                } else if (view != null) {
                    if (view.getId() == R.id.forward)
                        song = songs.get(0);
                } else if (repeatState == 0) {
                    try {
                        mediaPlayer.setDataSource(currentSong.getPath());
                    } catch (IOException e) {
                        currentSong = songs.get(songs.size() - 1);
                    }
                }
            } else {
                if (!songs.contains(currentSong)) {
                    int i = 0;
                    for (Song s : songs)
                        if (s.getPath().equals(currentSong.getPath()))
                            break;
                        else
                            i++;
                    song = songs.get(i + 1);
                } else
                    song = songs.get(songs.indexOf(currentSong) + 1);
            }
        } else {
            if (songs.size() == 1)
                song = songs.get(0);
            else {
                int rand;
                do {
                    rand = (int) (Math.random() * (songs.size()));
                }
                while (rand == currentSong.getID());
                song = songs.get(rand);
            }
            if (isShuffledSongsEmpty())
                clearShuffledSongs(currentSong);
            addForwardSong(song);
            song = getForwardSong();
        }
        if (song != null) {
            currentSong = song;
            playThisSong(song);
        } else {
            try {
                mediaPlayer.setDataSource(currentSong.getPath());
                mediaPlayer.prepare();
                mediaPlayer.setOnCompletionListener(mediaPlayer -> {
                    if (currentSong.getID() != songs.size() - 1 || repeatState != 0) {
                        playNextSong(null);
                        TextView tvTitle = outerActivity.findViewById(R.id.outerSongName);
                        tvTitle.setText(currentSong.getTitle());
                        TextView tvArtist = outerActivity.findViewById(R.id.outerArtistName);
                        tvArtist.setText(currentSong.getArtist());
                        mediaPlayer.setWakeMode(outerActivity.getApplicationContext(), PowerManager.PARTIAL_WAKE_LOCK);
                    }
                    updateMusicPlayerUIouter = true;
                    updateMusicPlayerUIinner = true;
                    updateMusicPlayerUIinner2 = true;
                });
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        mediaPlayer.setWakeMode(outerActivity.getApplicationContext(), PowerManager.PARTIAL_WAKE_LOCK);
    }

    public RemoteViews getCollapsedView() {
        return collapsedView;
    }

    public void setCollapsedView(RemoteViews collapsedView) {
        this.collapsedView = collapsedView;
    }

    public RemoteViews getExpandedView() {
        return expandedView;
    }

    public void setExpandedView(RemoteViews expandedView) {
        this.expandedView = expandedView;
    }

    public void toggleNotificationSong() {
        if (mediaPlayer.isPlaying()) {
            mediaPlayer.pause();
        } else {
            mediaPlayer.start();
            mediaPlayer.setWakeMode(outerActivity.getApplicationContext(), PowerManager.PARTIAL_WAKE_LOCK);
        }
        updateMusicPlayerUIouter = true;
        updateMusicPlayerUIinner = true;
    }
}