package com.example.musiktea;

import static com.example.musiktea.Singleton.notificationManager;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationManagerCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.AudioAttributes;
import android.media.AudioFocusRequest;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.regex.Pattern;


public class MusicList extends AppCompatActivity {

    public static final int REQUEST_PERMISSION = 99;
    static final int OUTER_PLAY = 1;
    private static final int NEW_PLAYLIST = 2;
    private static final int EDIT_PLAYLIST = 3;

    String oldSearchKey;

    ImageView play, addSongToPlaylist, playingQueue, backQueue;
    ImageView backSelectionOptionsPlaylist, deleteSelectedPlaylists, editSelectedPlaylist;
    ImageView backSelectionOptionsSongs, addSongsToPlaylist;
    ImageView settings, search, backSearch;
    ImageView backCurrentPlaylist, deleteFromPlaylist;
    ImageView backSelectionAlbums, playSelectedAlbums;
    ImageView backSelectionArtists, playSelectedArtists;
    ImageView backSelectionFolders, playSelectedFolders;

    TextView tvSongName, tvArtist, tvPath, tvNoPlaylistSongs, tvPlaylistName;
    TextView tvSelectedPlaylistCount, tvSelectedSongsCount, tvSelectedAlbumsCount, tvSelectedArtistsCount, tvSelectedFoldersCount;
    TextView tvListTitle;
    EditText etSearch;

    private boolean isSearch = false;
    private boolean isQueue = false;

    SeekBar seekBar;

    LinearLayout playingSong, Songs, Folders, Playlists, Album, Artist;
    ConstraintLayout selectionOptions, selectionOptionsSongs, selectionCurrentPlaylist;
    ConstraintLayout selectionAlbums, selectionArtists, selectionFolders;

    SongsAdapter songsAdapter;
    FoldersAdapter foldersAdapter;
    File parentFile;
    PlaylistAdapter playlistAdapter;
    SongsAdapter playlistSongsAdapter;
    AlbumAdapter albumAdapter;
    CurrentAlbumAdapter currentAlbumAdapter;
    CurrentArtistAdapter currentArtistAdapter;
    ArtistAdapter artistAdapter;

    SearchAdapter searchAdapter;
    PlayingQueueAdapter queueAdapter;

    ArrayList<Song> songArrayList;
    ArrayList<Song> songFolderList;
    ArrayList<File> folderArrayList;
    ArrayList<Playlist> playlistList;
    ArrayList<Album> albumArrayList;
    ArrayList<Artist> artistArrayList;

    ArrayList<Boolean> selectedItems = null;

    ArrayList<ArrayList<Song>> playlistSongs;
    ArrayList<ArrayList<Song>> albumSongs;
    ArrayList<ArrayList<Song>> artistSongs;

    int dummyInteger = 0;
    int playlistNumber;
    int albumNumber;
    int artistNumber;

    ArrayList<SearchItem> searchItems;
    ArrayList<SearchItem> hiddenItems;

    ArrayList<String> filteredFolders;
    ArrayList<String> foldersNames;
    Bitmap foldersImage;

    ListView lvSongs, lvFolders, lvPlaylist, lvCurrentPlaylist, lvArtist;
    GridView gvAlbum;
    ListView lvCurrentAlbum, lvCurrentArtist;

    ListView lvSearch, lvQueue;

    Thread outerChanger = null;
    Thread bitmapSetter = null;

    AudioManager audioManager;


    @RequiresApi(api = Build.VERSION_CODES.O)
    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        ActionBar actionBar = getSupportActionBar();
        assert actionBar != null;
        actionBar.hide();

        audioManager = (AudioManager) getSystemService(getBaseContext().AUDIO_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            audioManager.requestAudioFocus(new AudioFocusRequest.Builder(AudioManager.AUDIOFOCUS_GAIN)
                    .setAudioAttributes(
                            new AudioAttributes.Builder()
                                    .setUsage(AudioAttributes.USAGE_GAME)
                                    .setContentType(AudioAttributes.CONTENT_TYPE_SPEECH)
                                    .build()
                    )
                    .setAcceptsDelayedFocusGain(true)
                    .setOnAudioFocusChangeListener(focusChange -> {
                        if ((focusChange == AudioManager.AUDIOFOCUS_LOSS || focusChange == AudioManager.AUDIOFOCUS_LOSS_TRANSIENT) && Singleton.getInstance().getMediaPlayer() != null) {
                            if (Singleton.getInstance().getMediaPlayer().isPlaying())
                                Singleton.getInstance().toggleNotificationSong();
                        } else if ((focusChange == AudioManager.AUDIOFOCUS_GAIN || focusChange == AudioManager.AUDIOFOCUS_GAIN_TRANSIENT) && Singleton.getInstance().getMediaPlayer() != null && Singleton.getInstance().getNotification() != null) {
                            if (!Singleton.getInstance().getMediaPlayer().isPlaying())
                                Singleton.getInstance().toggleNotificationSong();
                        }
                    }).build()
            );
        } else {

            audioManager.requestAudioFocus(focusChange -> {
                        if ((focusChange == AudioManager.AUDIOFOCUS_LOSS || focusChange == AudioManager.AUDIOFOCUS_LOSS_TRANSIENT) && Singleton.getInstance().getMediaPlayer() != null)
                            if (Singleton.getInstance().getMediaPlayer().isPlaying())
                                Singleton.getInstance().toggleNotificationSong();
                            else if ((focusChange == AudioManager.AUDIOFOCUS_GAIN || focusChange == AudioManager.AUDIOFOCUS_GAIN_TRANSIENT) && Singleton.getInstance().getMediaPlayer() != null && Singleton.getInstance().getNotification() != null)
                                if (!Singleton.getInstance().getMediaPlayer().isPlaying())
                                    Singleton.getInstance().toggleNotificationSong();
                    },
                    AudioManager.STREAM_MUSIC,
                    AudioManager.AUDIOFOCUS_GAIN);
        }

        setContentView(R.layout.activity_music_list);

        lvSongs = findViewById(R.id.lvSongs);
        lvFolders = findViewById(R.id.lvFolders);
        lvPlaylist = findViewById(R.id.lvPlaylist);
        lvCurrentPlaylist = findViewById(R.id.lvCurrentPlaylist);
        gvAlbum = findViewById(R.id.lvAlbum);
        lvCurrentAlbum = findViewById(R.id.lvCurrentAlbum);
        lvArtist = findViewById(R.id.lvArtist);
        lvCurrentArtist = findViewById(R.id.lvCurrentArtist);

        lvSearch = findViewById(R.id.lvSearch);
        lvQueue = findViewById(R.id.lvQueue);

        songArrayList = new ArrayList<>();
        songFolderList = new ArrayList<>();
        folderArrayList = new ArrayList<>();
        playlistList = new ArrayList<>();
        albumArrayList = new ArrayList<>();
        artistArrayList = new ArrayList<>();

        playlistSongs = new ArrayList<>();

        filteredFolders = new ArrayList<>();

        songsAdapter = new SongsAdapter(this, songArrayList, selectedItems);
        foldersAdapter = new FoldersAdapter(this, folderArrayList, selectedItems);
        playlistAdapter = new PlaylistAdapter(this, playlistList, selectedItems);
        albumAdapter = new AlbumAdapter(this, albumArrayList, selectedItems);
        artistAdapter = new ArtistAdapter(this, artistArrayList, selectedItems);

        lvSongs.setAdapter(songsAdapter);
        lvFolders.setAdapter(foldersAdapter);
        lvPlaylist.setAdapter(playlistAdapter);
        lvCurrentPlaylist.setAdapter(playlistSongsAdapter);
        gvAlbum.setAdapter(albumAdapter);
        lvCurrentAlbum.setAdapter(currentAlbumAdapter);
        lvArtist.setAdapter(artistAdapter);

        Singleton.getInstance().setSavedSettings(PreferenceManager.getDefaultSharedPreferences(getBaseContext()));

        play = findViewById(R.id.outerplay);
        playingQueue = findViewById(R.id.playingQueue);

        backSelectionOptionsSongs = findViewById(R.id.backSelectionOptionsSongs);
        backSelectionOptionsPlaylist = findViewById(R.id.backSelectionOptions);
        deleteSelectedPlaylists = findViewById(R.id.deleteSelectedPlaylists);
        editSelectedPlaylist = findViewById(R.id.editSelectedPlaylist);
        addSongsToPlaylist = findViewById(R.id.addSelectedSongsToPlaylist);

        backCurrentPlaylist = findViewById(R.id.backSelectionCurrentPlaylist);
        deleteFromPlaylist = findViewById(R.id.deleteSelectedSongsFromPlaylist);

        backSelectionAlbums = findViewById(R.id.backSelectionAlbums);
        playSelectedAlbums = findViewById(R.id.playSelectedAlbums);

        backSelectionArtists = findViewById(R.id.backSelectionArtists);
        playSelectedArtists = findViewById(R.id.playSelectedArtists);

        backSelectionFolders = findViewById(R.id.backSelectionFolders);
        playSelectedFolders = findViewById(R.id.playSelectedFolders);

        settings = findViewById(R.id.settings);
        search = findViewById(R.id.search);
        backSearch = findViewById(R.id.backSearch);
        backQueue = findViewById(R.id.backQueue);

        Songs = findViewById(R.id.allSongs);
        Folders = findViewById(R.id.folders);
        Playlists = findViewById(R.id.playLists);
        Album = findViewById(R.id.albums);
        Artist = findViewById(R.id.artist);
        selectionOptions = findViewById(R.id.selectionOptions);
        selectionOptionsSongs = findViewById(R.id.selectionOptionsSongs);
        selectionCurrentPlaylist = findViewById(R.id.selectionCurrentPlaylist);
        selectionAlbums = findViewById(R.id.selectionAlbums);
        selectionArtists = findViewById(R.id.selectionArtists);
        selectionFolders = findViewById(R.id.selectionFolders);

        addSongToPlaylist = findViewById(R.id.addSongToPlaylist);

        playingSong = findViewById(R.id.playingSong);
        tvSongName = findViewById(R.id.outerSongName);
        tvArtist = findViewById(R.id.outerArtistName);
        tvPath = findViewById(R.id.folderPath);
        tvNoPlaylistSongs = findViewById(R.id.tvNoSongs);
        tvPlaylistName = findViewById(R.id.tvPlaylistName);
        tvSelectedPlaylistCount = findViewById(R.id.tvSelectedPlaylistCount);
        tvSelectedSongsCount = findViewById(R.id.tvSelectedSongsCount);
        tvSelectedAlbumsCount = findViewById(R.id.tvSelectedAlbumsCount);
        tvSelectedArtistsCount = findViewById(R.id.tvSelectedArtistsCount);
        tvSelectedFoldersCount = findViewById(R.id.tvSelectedFoldersCount);
        tvListTitle = findViewById(R.id.tvListTitle);
        etSearch = findViewById(R.id.etSearchKey);

        seekBar = findViewById(R.id.outerSeekBar);

        if (Singleton.getInstance().getMediaPlayer() != null) {
            if (Singleton.getInstance().getMediaPlayer().isPlaying())
                play.setImageResource(R.drawable.pause);
            else
                play.setImageResource(R.drawable.playbutton);
        }

        requestStoragePermission();

        Singleton.getInstance().setShuffle(Singleton.getInstance().getSavedSettings().getBoolean("shuffleState", false));
        Singleton.getInstance().setRepeatState(Singleton.getInstance().getSavedSettings().getInt("repeatState", 0));
        Singleton.getInstance().setSongProgress(Singleton.getInstance().getSavedSettings().getInt("progress", 0));


        lvSongs.setOnItemClickListener((adapterView, view, position, l) -> {
            if (selectedItems != null) {
                selectedItems.set(position, !selectedItems.get(position));

                ImageView selectionSong = view.findViewById(R.id.selectionSong);
                toggleSelection(selectionSong, selectedItems.get(position));
                songsAdapter.notifyDataSetChanged();
                tvSelectedSongsCount.setText(String.valueOf(getSelectedItemsCount()));
            } else {
                Intent openMusicPlayer = new Intent(MusicList.this, MusicPlayer.class);
                openMusicPlayer.putExtra("song", songArrayList.get(position));
                Singleton.getInstance().updateSavedSettings("Songs");
                Singleton.getInstance().setSongs(songArrayList);
                setPlayingQueue();

                Singleton.getInstance().setPlayingSongBitmap(BitmapFactory.decodeFile(songArrayList.get(position).getImage()));

                startActivityForResult(openMusicPlayer, OUTER_PLAY);
            }

        });
        lvSongs.setOnItemLongClickListener((adapterView, view, i, l) -> {
            selectionOptionsSongs.setVisibility(View.VISIBLE);
            if (selectedItems == null) {
                selectedItems = new ArrayList<>();
                for (int j = 0; j < lvSongs.getCount(); j++)
                    selectedItems.add(i == j);
            } else
                selectedItems.set(i, !selectedItems.get(i));
            songsAdapter.setSelectedItems(selectedItems);
            tvSelectedSongsCount.setText(String.valueOf(getSelectedItemsCount()));
            songsAdapter.notifyDataSetChanged();
            return true;
        });

        backSelectionOptionsSongs.setOnClickListener(view -> clearSelectedSongs());
        addSongsToPlaylist.setOnClickListener(view -> {
            if (Singleton.getInstance().isChooseSongToPlaylist()) {
                Singleton.getInstance().setChooseSongToPlaylist(false);
                int j = 0;
                for (Boolean b : selectedItems) {
                    if (b) {
                        boolean e = true;
                        for (Song s : playlistSongs.get(playlistNumber - 1))
                            if (s.getPath().equals(songArrayList.get(j).getPath())) {
                                e = false;
                                break;
                            }
                        if (e)
                            playlistSongs.get(playlistNumber - 1).add(songArrayList.get(j));
                    }
                    j++;
                }
                lvSongs.setVisibility(View.GONE);
                tvPlaylistName.setVisibility(View.VISIBLE);
                addSongToPlaylist.setVisibility(View.VISIBLE);
                lvCurrentPlaylist.setVisibility(View.VISIBLE);
                playlistSongsAdapter.notifyDataSetChanged();
                savePlaylists();
                selectionOptionsSongs.setVisibility(View.GONE);
                selectedItems.clear();
                songsAdapter.notifyDataSetChanged();
                return;
            }
            Intent intent = new Intent(MusicList.this, PopActivityAddToPlaylist.class);
            StringBuilder names = new StringBuilder();
            for (Playlist p : playlistList) {
                if (p.getPlaylistName() != null) {
                    names.append(p.getPlaylistName() + "/");
                }
            }
            intent.putExtra("playlistNames", names.toString());

            startActivity(intent);
        });

        backSelectionOptionsSongs.setOnLongClickListener(view -> {
            Toast.makeText(getBaseContext(), "Back", Toast.LENGTH_SHORT).show();
            return true;
        });
        addSongsToPlaylist.setOnLongClickListener(view -> {
            Toast.makeText(getBaseContext(), "Add Songs", Toast.LENGTH_SHORT).show();
            return true;
        });

        lvFolders.setOnItemClickListener((adapterView, view, position, l) -> {
            if (selectedItems == null) {
                if (folderArrayList.get(position).isDirectory()) {
                    tvPath.setText(folderArrayList.get(position).getPath());
                    trimPathStart();
                    getFolderChildren(folderArrayList.get(position));
                } else {
                    Intent openMusicPlayer = new Intent(MusicList.this, MusicPlayer.class);
                    String mimeType = URLConnection.guessContentTypeFromName(folderArrayList.get(position).getPath());

                    songFolderList.clear();
                    int numberOfFolders = 0;
                    for (File f : folderArrayList) {
                        if (URLConnection.guessContentTypeFromName(folderArrayList.get(position).getPath()) != null && mimeType.startsWith("audio") && !f.isDirectory()) {
                            for (Song song : songArrayList) {
                                if (song.getPath().equals(f.getPath())) {
                                    songFolderList.add(song);
                                    break;
                                }
                            }
                        } else
                            numberOfFolders++;
                    }
                    Song song = songFolderList.get(position - numberOfFolders);
                    openMusicPlayer.putExtra("song", song);

                    if (foldersImage != null)
                        Singleton.getInstance().setPlayingSongBitmap(foldersImage);
                    else
                        Singleton.getInstance().setPlayingSongBitmap(null);

                    Singleton.getInstance().updateSavedSettings("Folders");
                    Singleton.getInstance().updateSongSource(folderArrayList.get(position).getParentFile().getPath());
                    Singleton.getInstance().setSongs(songFolderList);
                    setPlayingQueue();
                    startActivityForResult(openMusicPlayer, OUTER_PLAY);
                }
            }
            else {
                selectedItems.set(position, !selectedItems.get(position));

                ImageView selectionFolder = view.findViewById(R.id.selectionFolder);
                toggleSelection(selectionFolder, selectedItems.get(position));
                foldersAdapter.notifyDataSetChanged();
                tvSelectedFoldersCount.setText(String.valueOf(getSelectedItemsCount()));
            }
        });
        lvFolders.setOnItemLongClickListener((adapterView, view, i, l) -> {
            selectionFolders.setVisibility(View.VISIBLE);
            if (selectedItems == null) {
                selectedItems = new ArrayList<>();
                for (int j = 0; j < lvFolders.getCount(); j++)
                    selectedItems.add(i == j);
            } else
                selectedItems.set(i, !selectedItems.get(i));
            foldersAdapter.setSelectedItems(selectedItems);
            foldersAdapter.notifyDataSetChanged();
            tvSelectedFoldersCount.setText(String.valueOf(getSelectedItemsCount()));
            search.setVisibility(View.GONE);
            return true;
        });

        backSelectionFolders.setOnClickListener(view -> clearSelectedFolders());
        playSelectedFolders.setOnClickListener(view -> {
            if (!selectedItems.contains(true))
                Toast.makeText(getBaseContext(), "No folders selected", Toast.LENGTH_SHORT).show();
            else {
                int i = 0;
                Singleton.getInstance().getSongs().clear();
                for (Boolean b : selectedItems) {
                    if (b) {
                       String path = folderArrayList.get(i).getPath();
                       for (Song s : songArrayList) {
                           if (s.getPath().contains(path))
                               Singleton.getInstance().getSongs().add(s);
                       }
                    } else
                        i++;
                }
                Singleton.getInstance().setCurrentSong(Singleton.getInstance().getSongs().get(0));
                Singleton.getInstance().playThisSong(Singleton.getInstance().getSongs().get(0));
                clearSelectedFolders();
            }
        });

        backSelectionFolders.setOnLongClickListener(view -> {
            Toast.makeText(getBaseContext(), "Back", Toast.LENGTH_SHORT).show();
            return true;
        });
        playSelectedFolders.setOnLongClickListener(view -> {
            Toast.makeText(getBaseContext(), "Play selection", Toast.LENGTH_SHORT).show();
            return true;
        });

        lvPlaylist.setOnItemClickListener((adapterView, view, position, l) -> {
            if (position == 0) {
                Intent intent = new Intent(this, PopActivityNewPlaylist.class);
                startActivityForResult(intent, NEW_PLAYLIST);
            } else {
                if (selectedItems == null) {
                    lvPlaylist.setVisibility(View.GONE);
                    tvPlaylistName.setText(playlistList.get(position).getPlaylistName());
                    tvPlaylistName.setVisibility(View.VISIBLE);
                    addSongToPlaylist.setVisibility(View.VISIBLE);
                    lvCurrentPlaylist.setVisibility(View.VISIBLE);
                    playlistNumber = position;
                    playlistSongsAdapter = new SongsAdapter(this, playlistSongs.get(playlistNumber - 1), selectedItems);
                    lvCurrentPlaylist.setAdapter(playlistSongsAdapter);
                    if (playlistSongs.get(playlistNumber - 1).size() == 0)
                        tvNoPlaylistSongs.setVisibility(View.VISIBLE);
                    tvListTitle.setText("");
                } else {
                    selectedItems.set(position - 1, !selectedItems.get(position - 1));

                    ImageView selectionPlaylist = view.findViewById(R.id.selectionPlaylist);
                    toggleSelection(selectionPlaylist, selectedItems.get(position - 1));
                    playlistAdapter.notifyDataSetChanged();

                    tvSelectedPlaylistCount.setText(String.valueOf(getSelectedItemsCount()));
                    if (getSelectedItemsCount() != 1)
                        editSelectedPlaylist.setVisibility(View.GONE);
                    else
                        editSelectedPlaylist.setVisibility(View.VISIBLE);
                }
            }
        });
        lvPlaylist.setOnItemLongClickListener((adapterView, view, i, l) -> {
            selectionOptions.setVisibility(View.VISIBLE);
            search.setVisibility(View.GONE);
            if (selectedItems == null) {
                selectedItems = new ArrayList<>();
                for (int j = 0; j < lvPlaylist.getCount() - 1; j++)
                    selectedItems.add(i - 1 == j);
                tvSelectedPlaylistCount.setText("1");
            } else if (i != 0) {
                selectedItems.set(i - 1, !selectedItems.get(i - 1));
                tvSelectedPlaylistCount.setText(String.valueOf(getSelectedItemsCount()));
            }

            playlistAdapter.setSelectedItems(selectedItems);

            playlistAdapter.notifyDataSetChanged();
            if (getSelectedItemsCount() == 1)
                editSelectedPlaylist.setVisibility(View.VISIBLE);
            else
                editSelectedPlaylist.setVisibility(View.GONE);
            return true;
        });

        backSelectionOptionsPlaylist.setOnClickListener(view -> clearSelectedPlaylists());
        deleteSelectedPlaylists.setOnClickListener(view -> {
            int i = 0;
            for (Boolean b : selectedItems) {
                if (b) {
                    playlistList.remove(i + 1);
                    playlistSongs.get(i).clear();
                    playlistSongs.remove(i);
                } else
                    i++;
            }
            clearSelectedPlaylists();
            playlistAdapter.notifyDataSetChanged();
            savePlaylists();
        });
        editSelectedPlaylist.setOnClickListener(view -> {
            Intent openEditPlaylist = new Intent(MusicList.this, PopActivityEditPlaylist.class);
            int i = 0;
            for (Boolean b : selectedItems) {
                if (b)
                    break;
                i++;
            }
            openEditPlaylist.putExtra("playlistName", playlistList.get(i + 1).getPlaylistName());
            Singleton.getInstance().setNewPlaylistBitmap(playlistList.get(i + 1).getPlaylistImage());
            startActivityForResult(openEditPlaylist, EDIT_PLAYLIST);
        });

        backSelectionOptionsPlaylist.setOnLongClickListener(view -> {
            Toast.makeText(getBaseContext(), "Back", Toast.LENGTH_SHORT).show();
            return true;
        });
        deleteSelectedPlaylists.setOnLongClickListener(view -> {
            Toast.makeText(getBaseContext(), "Delete", Toast.LENGTH_SHORT).show();
            return true;
        });
        editSelectedPlaylist.setOnLongClickListener(view -> {
            Toast.makeText(getBaseContext(), "Edit", Toast.LENGTH_SHORT).show();
            return true;
        });

        addSongToPlaylist.setOnClickListener(view -> {
            Singleton.getInstance().setChooseSongToPlaylist(true);
            lvSongs.setVisibility(View.VISIBLE);
            lvCurrentPlaylist.setVisibility(View.GONE);
            addSongToPlaylist.setVisibility(View.GONE);
            tvPlaylistName.setVisibility(View.GONE);
            tvNoPlaylistSongs.setVisibility(View.GONE);
            selectionOptionsSongs.setVisibility(View.VISIBLE);
            selectedItems = new ArrayList<>();
            for (int j = 0; j < lvSongs.getCount(); j++)
                selectedItems.add(false);
            songsAdapter.setSelectedItems(selectedItems);
            tvSelectedSongsCount.setText(String.valueOf(getSelectedItemsCount()));
            songsAdapter.notifyDataSetChanged();
        });

        lvCurrentPlaylist.setOnItemClickListener((adapterView, view, i, l) -> {
            if (selectedItems != null) {
                selectedItems.set(i, !selectedItems.get(i));

                ImageView selectionSong = view.findViewById(R.id.selectionSong);
                toggleSelection(selectionSong, selectedItems.get(i));
                playlistSongsAdapter.notifyDataSetChanged();
            } else {
                Intent openMusicPlayer = new Intent(MusicList.this, MusicPlayer.class);
                Song song = playlistSongs.get(playlistNumber - 1).get(i);
                openMusicPlayer.putExtra("song", song);
                Singleton.getInstance().updateSavedSettings("Playlist");
                Singleton.getInstance().updateSongSource(String.valueOf(playlistNumber - 1));
                Singleton.getInstance().setSongs(playlistSongs.get(playlistNumber - 1));
                setPlayingQueue();

                Singleton.getInstance().setPlayingSongBitmap(playlistList.get(playlistNumber).getPlaylistImage());

                startActivityForResult(openMusicPlayer, OUTER_PLAY);
            }
        });
        lvCurrentPlaylist.setOnItemLongClickListener((adapterView, view, i, l) -> {
            selectionCurrentPlaylist.setVisibility(View.VISIBLE);
            if (selectedItems == null) {
                selectedItems = new ArrayList<>();
                for (int j = 0; j < lvCurrentPlaylist.getCount(); j++)
                    selectedItems.add(i == j);
            } else
                selectedItems.set(i, !selectedItems.get(i));
            playlistSongsAdapter.setSelectedItems(selectedItems);
            playlistSongsAdapter.notifyDataSetChanged();
            return true;
        });

        backCurrentPlaylist.setOnClickListener(view -> clearSelectedPlaylistSongs());
        deleteFromPlaylist.setOnClickListener(view -> {
            if (!selectedItems.contains(true))
                Toast.makeText(getBaseContext(), "No songs selected", Toast.LENGTH_SHORT).show();
            else {
                int i = 0;
                for (Boolean b : selectedItems) {
                    if (b) {
                        playlistSongs.get(playlistNumber - 1).remove(i);
                    } else
                        i++;
                }
                clearSelectedPlaylistSongs();
                playlistSongsAdapter.notifyDataSetChanged();
                savePlaylists();
            }
        });

        backCurrentPlaylist.setOnLongClickListener(view -> {
            Toast.makeText(getBaseContext(), "Back", Toast.LENGTH_SHORT).show();
            return true;
        });
        deleteFromPlaylist.setOnLongClickListener(view -> {
            Toast.makeText(getBaseContext(), "Delete", Toast.LENGTH_SHORT).show();
            return true;
        });

        gvAlbum.setOnItemClickListener((adapterView, view, i, l) -> {
            if (selectedItems == null) {
                lvCurrentAlbum.setVisibility(View.VISIBLE);
                gvAlbum.setVisibility(View.GONE);
                currentAlbumAdapter = new CurrentAlbumAdapter(this, albumSongs.get(i), albumArrayList.get(i).getAlbumImage());
                lvCurrentAlbum.setAdapter(currentAlbumAdapter);
                currentAlbumAdapter.notifyDataSetChanged();
                albumNumber = i;
                tvListTitle.setText(albumArrayList.get(i).getAlbumName());
            } else {
                selectedItems.set(i, !selectedItems.get(i));

                ImageView selectionAlbum = view.findViewById(R.id.selectionAlbum);
                toggleSelection(selectionAlbum, selectedItems.get(i));
                albumAdapter.notifyDataSetChanged();
                tvSelectedAlbumsCount.setText(String.valueOf(getSelectedItemsCount()));
            }
        });
        gvAlbum.setOnItemLongClickListener((adapterView, view, i, l) -> {
            selectionAlbums.setVisibility(View.VISIBLE);
            if (selectedItems == null) {
                selectedItems = new ArrayList<>();
                for (int j = 0; j < gvAlbum.getCount(); j++)
                    selectedItems.add(i == j);
            } else
                selectedItems.set(i, !selectedItems.get(i));
            albumAdapter.setSelectedItems(selectedItems);
            albumAdapter.notifyDataSetChanged();
            tvSelectedAlbumsCount.setText(String.valueOf(getSelectedItemsCount()));
            search.setVisibility(View.GONE);
            return true;
        });
        lvCurrentAlbum.setOnItemClickListener((adapterView, view, i, l) -> {
            Intent openMusicPlayer = new Intent(MusicList.this, MusicPlayer.class);
            Song song = albumSongs.get(albumNumber).get(i);
            openMusicPlayer.putExtra("song", song);
            Singleton.getInstance().updateSavedSettings("Album");
            Singleton.getInstance().updateSongSource(String.valueOf(albumNumber));
            Singleton.getInstance().setSongs(albumSongs.get(albumNumber));
            setPlayingQueue();

            Singleton.getInstance().setPlayingSongBitmap(albumArrayList.get(albumNumber).getAlbumImage());

            startActivity(openMusicPlayer);
        });

        backSelectionAlbums.setOnClickListener(view -> clearSelectedAlbums());
        playSelectedAlbums.setOnClickListener(view -> {
            int i = 0;
            Singleton.getInstance().getSongs().clear();
            for (Boolean b : selectedItems) {
                if (b)
                    Singleton.getInstance().getSongs().addAll(albumSongs.get(i));
                i++;
            }
            Singleton.getInstance().setCurrentSong(Singleton.getInstance().getSongs().get(0));
            Singleton.getInstance().playThisSong(Singleton.getInstance().getSongs().get(0));
            clearSelectedAlbums();
        });

        backSelectionAlbums.setOnLongClickListener(view -> {
            Toast.makeText(getBaseContext(), "Back", Toast.LENGTH_SHORT).show();
            return true;
        });
        playSelectedAlbums.setOnLongClickListener(view -> {
            Toast.makeText(getBaseContext(), "Play selection", Toast.LENGTH_SHORT).show();
            return true;
        });

        lvArtist.setOnItemClickListener((adapterView, view, i, l) -> {
            if (selectedItems == null) {
                lvCurrentArtist.setVisibility(View.VISIBLE);
                lvArtist.setVisibility(View.GONE);
                currentArtistAdapter = new CurrentArtistAdapter(this, artistSongs.get(i));
                lvCurrentArtist.setAdapter(currentArtistAdapter);
                currentArtistAdapter.notifyDataSetChanged();
                artistNumber = i;
                tvListTitle.setText(artistArrayList.get(i).getArtistName());
            } else {
                selectedItems.set(i, !selectedItems.get(i));

                ImageView selectionArtist = view.findViewById(R.id.selectionArtist);
                toggleSelection(selectionArtist, selectedItems.get(i));
                artistAdapter.notifyDataSetChanged();
                tvSelectedAlbumsCount.setText(String.valueOf(getSelectedItemsCount()));
            }
        });
        lvArtist.setOnItemLongClickListener((adapterView, view, i, l) -> {
            selectionArtists.setVisibility(View.VISIBLE);
            if (selectedItems == null) {
                selectedItems = new ArrayList<>();
                for (int j = 0; j < lvArtist.getCount(); j++)
                    selectedItems.add(i == j);
            } else
                selectedItems.set(i, !selectedItems.get(i));
            artistAdapter.setSelectedItems(selectedItems);
            artistAdapter.notifyDataSetChanged();
            tvSelectedArtistsCount.setText(String.valueOf(getSelectedItemsCount()));
            search.setVisibility(View.GONE);
            return true;
        });
        lvCurrentArtist.setOnItemClickListener((adapterView, view, i, l) -> {
            Intent openMusicPlayer = new Intent(MusicList.this, MusicPlayer.class);
            Song song = artistSongs.get(artistNumber).get(i);
            openMusicPlayer.putExtra("song", song);
            Singleton.getInstance().updateSavedSettings("Artist");
            Singleton.getInstance().updateSongSource(String.valueOf(artistNumber));
            Singleton.getInstance().setSongs(artistSongs.get(artistNumber));
            setPlayingQueue();

            Singleton.getInstance().setPlayingSongBitmap(null);

            startActivity(openMusicPlayer);
        });

        backSelectionArtists.setOnClickListener(view -> clearSelectedArtists());
        playSelectedArtists.setOnClickListener(view -> {
            int i = 0;
            Singleton.getInstance().getSongs().clear();
            for (Boolean b : selectedItems) {
                if (b)
                    Singleton.getInstance().getSongs().addAll(artistSongs.get(i));
                i++;
            }
            Singleton.getInstance().setCurrentSong(Singleton.getInstance().getSongs().get(0));
            Singleton.getInstance().playThisSong(Singleton.getInstance().getSongs().get(0));
            clearSelectedArtists();
        });

        backSelectionArtists.setOnLongClickListener(view -> {
            Toast.makeText(getBaseContext(), "Back", Toast.LENGTH_SHORT).show();
            return true;
        });
        playSelectedArtists.setOnLongClickListener(view -> {
            Toast.makeText(getBaseContext(), "Play selection", Toast.LENGTH_SHORT).show();
            return true;
        });

        search.setOnClickListener(view -> {
            if (!isSearch)
                goSearch();
        });
        backSearch.setOnClickListener(view -> {
            backSearch();
        });
        settings.setOnClickListener(view -> {
            Intent openSettings = new Intent(MusicList.this, PopActivitySettings.class);
            startActivity(openSettings);
        });

        search.setOnLongClickListener(view -> {
            Toast.makeText(getBaseContext(), "Search", Toast.LENGTH_SHORT).show();
            return true;
        });
        backSearch.setOnLongClickListener(view -> {
            Toast.makeText(getBaseContext(), "Back", Toast.LENGTH_SHORT).show();
            return true;
        });
        settings.setOnLongClickListener(view -> {
            Toast.makeText(getBaseContext(), "Settings", Toast.LENGTH_SHORT).show();
            return true;
        });

        lvSearch.setOnItemClickListener((adapterView, view, i, l) -> {
            switch (searchItems.get(i).getType()) {
                case SearchItem.SONG: {
                    Intent openMusicPlayer = new Intent(MusicList.this, MusicPlayer.class);
                    Song song = songArrayList.get(searchItems.get(i).getID() - 1);
                    openMusicPlayer.putExtra("song", song);
                    Singleton.getInstance().updateSavedSettings("Search");
                    Singleton.getInstance().updateSongSource(oldSearchKey);
                    Singleton.getInstance().setSongs(new ArrayList<>());

                    int j = 1;
                    while (true) {
                        try {
                            if (searchItems.get(j).getType() == SearchItem.SONG)
                                Singleton.getInstance().getSongs().add(songArrayList.get(searchItems.get(j++).getID() - 1));
                            else
                                break;
                        } catch (Exception e) {
                            break;
                        }
                    }
                    setPlayingQueue();
                    Singleton.getInstance().setPlayingSongBitmap(null);

                    startActivityForResult(openMusicPlayer, OUTER_PLAY);
                    break;
                }
                case SearchItem.ALBUM: {
                    int differenceCount = songArrayList.size() + 1;
                    differenceCount = searchItems.get(i).getID() - differenceCount - 1;
                    lvCurrentAlbum.setVisibility(View.VISIBLE);
                    settings.setVisibility(View.VISIBLE);
                    tvListTitle.setVisibility(View.VISIBLE);
                    lvSearch.setVisibility(View.GONE);
                    etSearch.setVisibility(View.GONE);
                    backSearch.setVisibility(View.GONE);
                    currentAlbumAdapter = new CurrentAlbumAdapter(this, albumSongs.get(differenceCount), albumArrayList.get(differenceCount).getAlbumImage());
                    lvCurrentAlbum.setAdapter(currentAlbumAdapter);
                    currentAlbumAdapter.notifyDataSetChanged();
                    albumNumber = differenceCount;
                    tvListTitle.setText(albumArrayList.get(differenceCount).getAlbumName());
                    break;
                }
                case SearchItem.ARTIST: {
                    int differenceCount = songArrayList.size() + albumArrayList.size() + 2;
                    differenceCount = searchItems.get(i).getID() - differenceCount - 1;
                    lvCurrentArtist.setVisibility(View.VISIBLE);
                    settings.setVisibility(View.VISIBLE);
                    tvListTitle.setVisibility(View.VISIBLE);
                    lvSearch.setVisibility(View.GONE);
                    etSearch.setVisibility(View.GONE);
                    backSearch.setVisibility(View.GONE);
                    currentArtistAdapter = new CurrentArtistAdapter(this, artistSongs.get(differenceCount));
                    lvCurrentArtist.setAdapter(currentArtistAdapter);
                    currentArtistAdapter.notifyDataSetChanged();
                    artistNumber = differenceCount;
                    tvListTitle.setText(artistArrayList.get(differenceCount).getArtistName());
                    break;
                }
                case SearchItem.FOLDER: {
                    int differenceCount = songArrayList.size() + albumArrayList.size() + artistArrayList.size() + 3;
                    differenceCount = searchItems.get(i).getID() - differenceCount - 1;
                    lvFolders.setVisibility(View.VISIBLE);
                    settings.setVisibility(View.VISIBLE);
                    lvSearch.setVisibility(View.GONE);
                    etSearch.setVisibility(View.GONE);
                    backSearch.setVisibility(View.GONE);

                    tvPath.setVisibility(View.VISIBLE);
                    tvPath.setText(filteredFolders.get(differenceCount));
                    trimPathStart();
                    dummyInteger = differenceCount;
                    getFolderChildren(new File(filteredFolders.get(differenceCount)));
                    break;
                }
                case SearchItem.PLAYLIST: {
                    int differenceCount = songArrayList.size() + albumArrayList.size()
                            + artistArrayList.size() + foldersNames.size() + 5;
                    differenceCount = searchItems.get(i).getID() - differenceCount;
                    settings.setVisibility(View.VISIBLE);
                    lvCurrentPlaylist.setVisibility(View.VISIBLE);
                    //tvListTitle.setVisibility(View.VISIBLE);
                    lvSearch.setVisibility(View.GONE);
                    etSearch.setVisibility(View.GONE);
                    backSearch.setVisibility(View.GONE);

                    tvPlaylistName.setText(playlistList.get(differenceCount + 1).getPlaylistName());
                    tvPlaylistName.setVisibility(View.VISIBLE);
                    addSongToPlaylist.setVisibility(View.VISIBLE);
                    playlistNumber = differenceCount + 1;
                    playlistSongsAdapter = new SongsAdapter(this, playlistSongs.get(playlistNumber - 1), selectedItems);
                    lvCurrentPlaylist.setAdapter(playlistSongsAdapter);
                    if (playlistSongs.get(playlistNumber - 1).size() == 0)
                        tvNoPlaylistSongs.setVisibility(View.VISIBLE);
                    //tvListTitle.setText(playlistList.get(dummyInteger).getPlaylistName());
                    playlistSongsAdapter.notifyDataSetChanged();
                    break;
                }
                default:
                    break;
            }
        });

        lvQueue.setOnItemClickListener((adapterView, view, i, l) -> {
            Intent openMusicPlayer = new Intent(MusicList.this, MusicPlayer.class);
            Song song = Singleton.getInstance().getSongs().get(i);
            openMusicPlayer.putExtra("song", song);

            Singleton.getInstance().updateSavedSettings("Queue");
            Singleton.getInstance().updateSongSource(Singleton.getInstance().getCurrentPaths());

            Singleton.getInstance().setPlayingSongBitmap(null);

            startActivityForResult(openMusicPlayer, OUTER_PLAY);
        });

        playingSong.setOnClickListener(view -> {
            Song song = Singleton.getInstance().getCurrentSong();
            if (song == null)
                return;
            Intent openMusicPlayer = new Intent(MusicList.this, MusicPlayer.class);
            openMusicPlayer.putExtra("song", song);
            startActivityForResult(openMusicPlayer, OUTER_PLAY);
        });

        play.setOnClickListener(view -> {
            if (Singleton.getInstance().getSongs() == null)
                requestStoragePermission();
            else {
                if (Singleton.getInstance().getMediaPlayer() != null)
                    toggleSong();
                else {
                    Singleton.getInstance().playThisSong(Singleton.getInstance().getCurrentSong());
                    play.setImageResource(R.drawable.pause);
                }
            }
        });
        play.setOnLongClickListener(view -> {
            if (Singleton.getInstance().getMediaPlayer() == null || !Singleton.getInstance().getMediaPlayer().isPlaying())
                Toast.makeText(getBaseContext(), "Play", Toast.LENGTH_SHORT).show();
            else if (Singleton.getInstance().getMediaPlayer().isPlaying())
                Toast.makeText(getBaseContext(), "Pause", Toast.LENGTH_SHORT).show();
            return true;
        });

        playingQueue.setOnClickListener(view -> {
            if (Singleton.getInstance().getSongs() == null)
                requestStoragePermission();
            else
                goQueue();
        });
        backQueue.setOnClickListener(view -> backQueue());

        playingQueue.setOnLongClickListener(view -> {
            Toast.makeText(getBaseContext(), "Playing Queue", Toast.LENGTH_SHORT).show();
            return true;
        });
        backQueue.setOnLongClickListener(view -> {
            Toast.makeText(getBaseContext(), "Back", Toast.LENGTH_SHORT).show();
            return true;
        });

        switch (Singleton.getInstance().getSavedSettings().getInt("initialState", 0)) {
            case 1:
                goFolders();
                break;
            case 2:
                goPlaylist();
                break;
            case 3:
                goArtist();
                break;
            case 4:
                goAlbum();
                break;
            case 5:
                Singleton.getInstance().setViewState("Search");
                break;
            default:
                goSongs();
        }

        Songs.setOnClickListener(view -> {
            if (!Singleton.getInstance().getViewState().equals("Songs") || isQueue)
                goSongs();
        });
        Folders.setOnClickListener(view -> {
            if (!Singleton.getInstance().getViewState().equals("Folders") || isQueue)
                goFolders();
        });
        Playlists.setOnClickListener(view -> {
            if (!Singleton.getInstance().getViewState().equals("Playlist") || isQueue)
                goPlaylist();
        });
        Album.setOnClickListener(view -> {
            if (!Singleton.getInstance().getViewState().equals("Album") || isQueue)
                goAlbum();
        });
        Artist.setOnClickListener(view -> {
            if (!Singleton.getInstance().getViewState().equals("Artist") || isQueue)
                goArtist();
        });

        oldSearchKey = "";
        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                String searchKey = etSearch.getText().toString().trim();
                if (i2 != 0 && searchKey.compareTo(oldSearchKey) > 0) {
                    if (hiddenItems == null)
                        hiddenItems = new ArrayList<>();
                    for (SearchItem s : searchItems) {
                        if (s.getType() < SearchItem.SONGTITLE) {
                            SearchItemObject object = null;
                            switch (s.getType()) {
                                case SearchItem.SONG:
                                    object = new SearchItemObject((Song) s.getObject());
                                    break;
                                case SearchItem.ALBUM:
                                    object = new SearchItemObject((Album) s.getObject());
                                    break;
                                case SearchItem.ARTIST:
                                    object = new SearchItemObject((Artist) s.getObject());
                                    break;
                                case SearchItem.FOLDER:
                                    object = new SearchItemObject((String) s.getObject());
                                    break;
                                case SearchItem.PLAYLIST:
                                    object = new SearchItemObject((Playlist) s.getObject());
                                    break;
                            }
                            if (!Pattern.compile(Pattern.quote(searchKey), Pattern.CASE_INSENSITIVE).matcher(object.getItemName()).find()) {
                                int j = 0;
                                for (SearchItem ss : hiddenItems) {
                                    if (ss.getID() > s.getID()) {
                                        break;
                                    } else
                                        j++;
                                }
                                hiddenItems.add(j, s);
                            }
                        }
                    }

                    for (SearchItem s : hiddenItems)
                        searchItems.remove(s);
                    boolean[] removeTitles = new boolean[5];
                    removeTitles[0] = getCountFromSearchItems(searchItems, SearchItem.SONG) == 0;
                    removeTitles[1] = getCountFromSearchItems(searchItems, SearchItem.ALBUM) == 0;
                    removeTitles[2] = getCountFromSearchItems(searchItems, SearchItem.ARTIST) == 0;
                    removeTitles[3] = getCountFromSearchItems(searchItems, SearchItem.FOLDER) == 0;
                    removeTitles[4] = getCountFromSearchItems(searchItems, SearchItem.PLAYLIST) == 0;

                    int songCount = getCountFromSearchItems(hiddenItems, SearchItem.SONG);
                    int albumCount = getCountFromSearchItems(hiddenItems, SearchItem.ALBUM);
                    int artistCount = getCountFromSearchItems(hiddenItems, SearchItem.ARTIST);
                    int folderCount = getCountFromSearchItems(hiddenItems, SearchItem.FOLDER);
                    for (SearchItem s : searchItems) {
                        switch (s.getType()) {
                            case SearchItem.SONGTITLE:
                                if (removeTitles[0])
                                    hiddenItems.add(0, s);
                                break;
                            case SearchItem.ALBUMTITLE:
                                if (removeTitles[1]) {
                                    int j = 0;
                                    if (removeTitles[0])
                                        j += 1 + songCount;
                                    else
                                        j += songCount;
                                    hiddenItems.add(j, s);
                                }
                                break;
                            case SearchItem.ARTISTTITLE:
                                if (removeTitles[2]) {
                                    int j = 0;
                                    if (removeTitles[0])
                                        j += 1 + songCount;
                                    else
                                        j += songCount;
                                    if (removeTitles[1])
                                        j += 1 + albumCount;
                                    else
                                        j += albumCount;
                                    hiddenItems.add(j, s);
                                }
                                break;
                            case SearchItem.FOLDERTITLE:
                                if (removeTitles[3]) {
                                    int j = 0;
                                    if (removeTitles[0])
                                        j += 1 + songCount;
                                    else
                                        j += songCount;
                                    if (removeTitles[1])
                                        j += 1 + albumCount;
                                    else
                                        j += albumCount;
                                    if (removeTitles[2])
                                        j += 1 + artistCount;
                                    else
                                        j += artistCount;
                                    hiddenItems.add(j, s);
                                }
                                break;
                            case SearchItem.PLAYLISTTITLE:
                                if (removeTitles[4]) {
                                    int j = 0;
                                    if (removeTitles[0])
                                        j += 1 + songCount;
                                    else
                                        j += songCount;
                                    if (removeTitles[1])
                                        j += 1 + albumCount;
                                    else
                                        j += albumCount;
                                    if (removeTitles[2])
                                        j += 1 + artistCount;
                                    else
                                        j += artistCount;
                                    if (removeTitles[2])
                                        j += 1 + folderCount;
                                    else
                                        j += folderCount;
                                    hiddenItems.add(j, s);
                                }
                                break;
                        }
                    }

                    for (SearchItem s : hiddenItems)
                        searchItems.remove(s);
                } else if (hiddenItems != null) {
                    for (SearchItem s : hiddenItems) {
                        SearchItemObject object = null;
                        switch (s.getType()) {
                            case SearchItem.SONG:
                                object = new SearchItemObject((Song) s.getObject());
                                break;
                            case SearchItem.ALBUM:
                                object = new SearchItemObject((Album) s.getObject());
                                break;
                            case SearchItem.ARTIST:
                                object = new SearchItemObject((Artist) s.getObject());
                                break;
                            case SearchItem.FOLDER:
                                object = new SearchItemObject((String) s.getObject());
                                break;
                            case SearchItem.PLAYLIST:
                                object = new SearchItemObject((Playlist) s.getObject());
                                break;
                        }
                        if (object != null && Pattern.compile(Pattern.quote(searchKey), Pattern.CASE_INSENSITIVE).matcher(object.getItemName()).find()) {
                            int j = 0;
                            for (SearchItem ss : searchItems) {
                                if (ss.getID() > s.getID()) {
                                    break;
                                } else
                                    j++;
                            }
                            searchItems.add(j, s);
                        }
                    }
                    for (SearchItem s : searchItems)
                        hiddenItems.remove(s);

                    boolean[] removeTitles = new boolean[5];
                    removeTitles[0] = getCountFromSearchItems(searchItems, SearchItem.SONG) != 0;
                    removeTitles[1] = getCountFromSearchItems(searchItems, SearchItem.ALBUM) != 0;
                    removeTitles[2] = getCountFromSearchItems(searchItems, SearchItem.ARTIST) != 0;
                    removeTitles[3] = getCountFromSearchItems(searchItems, SearchItem.FOLDER) != 0;
                    removeTitles[4] = getCountFromSearchItems(searchItems, SearchItem.PLAYLIST) != 0;
                    int songCount = getCountFromSearchItems(searchItems, SearchItem.SONG);
                    int albumCount = getCountFromSearchItems(searchItems, SearchItem.ALBUM);
                    int artistCount = getCountFromSearchItems(searchItems, SearchItem.ARTIST);
                    int folderCount = getCountFromSearchItems(searchItems, SearchItem.FOLDER);
                    for (SearchItem s : hiddenItems) {
                        switch (s.getType()) {
                            case SearchItem.SONGTITLE:
                                if (removeTitles[0])
                                    searchItems.add(0, s);
                                break;
                            case SearchItem.ALBUMTITLE:
                                if (removeTitles[1]) {
                                    int j = 0;
                                    if (songCount != 0)
                                        j += 1 + songCount;
                                    searchItems.add(j, s);
                                }
                                break;
                            case SearchItem.ARTISTTITLE:
                                if (removeTitles[2]) {
                                    int j = 0;
                                    if (songCount != 0)
                                        j += 1 + songCount;
                                    if (albumCount != 0)
                                        j += 1 + albumCount;
                                    searchItems.add(j, s);
                                }
                                break;
                            case SearchItem.FOLDERTITLE:
                                if (removeTitles[3]) {
                                    int j = 0;
                                    if (songCount != 0)
                                        j += 1 + songCount;
                                    if (albumCount != 0)
                                        j += 1 + albumCount;
                                    if (artistCount != 0)
                                        j += 1 + artistCount;
                                    searchItems.add(j, s);
                                }
                                break;
                            case SearchItem.PLAYLISTTITLE:
                                if (removeTitles[4]) {
                                    int j = 0;
                                    if (songCount != 0)
                                        j += 1 + songCount;
                                    if (albumCount != 0)
                                        j += 1 + albumCount;
                                    if (artistCount != 0)
                                        j += 1 + artistCount;
                                    if (folderCount != 0)
                                        j += 1 + folderCount;
                                    searchItems.add(j, s);
                                }
                                break;
                        }
                    }

                    for (SearchItem s : searchItems)
                        hiddenItems.remove(s);
                }
                searchAdapter.notifyDataSetChanged();
                oldSearchKey = searchKey;
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        seekBar.setEnabled(false);

        if (outerChanger == null) {
            outerChanger = new Thread(() -> {
                while (true) {
                    while (Singleton.getInstance().getMediaPlayer() != null) {
                        if (Singleton.getInstance().getMediaPlayer().isPlaying()) {
                            try {
                                final double current = Singleton.getInstance().getMediaPlayer().getCurrentPosition() * 100 / Singleton.getInstance().getMediaPlayer().getDuration();
                                runOnUiThread(() -> {
                                    seekBar.setProgress((int) current);
                                    if (seekBar.getProgress() < 2) {
                                        tvSongName.setText(Singleton.getInstance().getCurrentSong().getTitle());
                                        tvArtist.setText(Singleton.getInstance().getCurrentSong().getArtist());
                                    }
                                });
                                Thread.sleep(500);
                            } catch (InterruptedException ignored) {
                            }
                        }
                        runOnUiThread(() -> {
                            if (Singleton.getInstance().isUpdateMusicPlayerUIouter() && !Singleton.getInstance().getMediaPlayer().isPlaying()) {
                                Singleton.getInstance().setUpdateMusicPlayerUIouter(false);
                                play.setImageResource(R.drawable.playbutton);
                            } else if (Singleton.getInstance().isUpdateMusicPlayerUIouter()) {
                                Singleton.getInstance().setUpdateMusicPlayerUIouter(false);
                                play.setImageResource(R.drawable.pause);
                            }
                        });
                    }
                }
            });
            outerChanger.start();
        }
        Singleton.getInstance().setOuterActivity(this);

        notificationManager = NotificationManagerCompat.from(this);

        if (bitmapSetter == null) {
            bitmapSetter = new Thread(() -> {
                int i = 0;
                for (Album a : albumArrayList) {
                    Bitmap image = null;
                    File[] f = new File(albumSongs.get(i).get(0).getPath()).getParentFile().listFiles();
                    if (f.length > 1) {
                        File ff = f[0];
                        String mimeType = URLConnection.guessContentTypeFromName(ff.getPath());
                        if (!ff.isDirectory() && mimeType != null && mimeType.startsWith("image"))
                            image = BitmapFactory.decodeFile(ff.getPath());
                        else {
                            ff = f[f.length - 1];
                            mimeType = URLConnection.guessContentTypeFromName(ff.getPath());
                            if (!ff.isDirectory() && mimeType != null && mimeType.startsWith("image"))
                                image = BitmapFactory.decodeFile(ff.getPath());
                        }
                    }
                    a.setAlbumImage(image);
                    try {
                        if (i % 10 == 0)
                            runOnUiThread(() -> albumAdapter.notifyDataSetChanged());
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    i++;
                }
                try {
                    runOnUiThread(() -> albumAdapter.notifyDataSetChanged());
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            });
            bitmapSetter.start();
        }
    }


    private void requestStoragePermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_EXTERNAL_STORAGE))
            new CustomPermissionDialog(this).show();
        else
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_PERMISSION);
    }

    private void goSongs() {
        if (isSearch)
            backSearch();
        if (isQueue)
            backQueue();

        String state = Singleton.getInstance().getViewState();
        if (state == "Folders")
            backFolders();
        else if (state == "Playlist")
            backPlaylist();
        else if (state == "Album")
            backAlbum();
        else if (state == "Artist")
            backArtist();

        lvSongs.setVisibility(View.VISIBLE);
        Singleton.getInstance().setViewState("Songs");
        tvListTitle.setText("All Songs");
    }

    private void backSongs() {
        lvSongs.setVisibility(View.GONE);
        clearSelectedSongs();
    }

    private void goFolders() {
        if (isSearch)
            backSearch();
        if (isQueue)
            backQueue();

        String state = Singleton.getInstance().getViewState();
        if (state == "Songs")
            backSongs();
        else if (state == "Playlist")
            backPlaylist();
        else if (state == "Album")
            backAlbum();
        else if (state == "Artist")
            backArtist();

        tvPath.setVisibility(View.VISIBLE);
        lvFolders.setVisibility(View.VISIBLE);
        Singleton.getInstance().setViewState("Folders");
        if (folderArrayList == null)
            tvPath.setText(Environment.getExternalStorageDirectory().getPath());
        else {
            try {
                tvPath.setText(folderArrayList.get(0).getParentFile().getPath());
            } catch (Exception e) {
            }
        }
        trimPathStart();
        tvListTitle.setText("Folders");
    }

    private void backFolders() {
        lvFolders.setVisibility(View.GONE);
        tvPath.setVisibility(View.GONE);
    }

    private void goPlaylist() {
        if (isSearch)
            backSearch();
        if (isQueue)
            backQueue();

        String state = Singleton.getInstance().getViewState();
        if (state == "Songs")
            backSongs();
        else if (state == "Folders")
            backFolders();
        else if (state == "Album")
            backAlbum();
        else if (state == "Artist")
            backArtist();

        lvPlaylist.setVisibility(View.VISIBLE);
        Singleton.getInstance().setViewState("Playlist");
        tvListTitle.setText("Playlists");
    }

    private void backPlaylist() {
        lvPlaylist.setVisibility(View.GONE);
        lvCurrentPlaylist.setVisibility(View.GONE);
        addSongToPlaylist.setVisibility(View.GONE);
        tvPlaylistName.setVisibility(View.GONE);
        tvNoPlaylistSongs.setVisibility(View.GONE);
        selectionOptionsSongs.setVisibility(View.GONE);
        lvSongs.setVisibility(View.GONE);
        clearSelectedPlaylists();
        clearSelectedPlaylistSongs();
    }

    private void goAlbum() {
        if (isSearch)
            backSearch();
        if (isQueue)
            backQueue();

        String state = Singleton.getInstance().getViewState();
        if (state == "Songs")
            backSongs();
        else if (state == "Folders")
            backFolders();
        else if (state == "Playlist")
            backPlaylist();
        else if (state == "Artist")
            backArtist();

        gvAlbum.setVisibility(View.VISIBLE);
        Singleton.getInstance().setViewState("Album");
        tvListTitle.setText("Albums");

    }

    private void backAlbum() {
        gvAlbum.setVisibility(View.GONE);
        lvCurrentAlbum.setVisibility(View.GONE);
        clearSelectedAlbums();
    }

    private void goArtist() {
        if (isSearch)
            backSearch();
        if (isQueue)
            backQueue();

        String state = Singleton.getInstance().getViewState();
        if (state == "Songs")
            backSongs();
        else if (state == "Folders")
            backFolders();
        else if (state == "Playlist")
            backPlaylist();
        else if (state == "Album")
            backAlbum();

        lvArtist.setVisibility(View.VISIBLE);
        Singleton.getInstance().setViewState("Artist");
        tvListTitle.setText("Artists");

    }

    private void backArtist() {
        lvArtist.setVisibility(View.GONE);
        lvCurrentArtist.setVisibility(View.GONE);
        clearSelectedArtists();
    }

    private void goSearch() {
        String state = Singleton.getInstance().getViewState();
        if (state == "Songs")
            backSongs();
        else if (state == "Folders")
            backFolders();
        else if (state == "Playlist")
            backPlaylist();
        else if (state == "Album")
            backAlbum();
        else if (state == "Artist")
            backArtist();
        lvSearch.setVisibility(View.VISIBLE);
        etSearch.setVisibility(View.VISIBLE);
        backSearch.setVisibility(View.VISIBLE);
        tvListTitle.setVisibility(View.GONE);
        settings.setVisibility(View.GONE);
        isSearch = true;
        etSearch.requestFocus();
        InputMethodManager imm = (InputMethodManager) getSystemService(getBaseContext().INPUT_METHOD_SERVICE);
        imm.showSoftInput(etSearch, InputMethodManager.SHOW_IMPLICIT);
    }

    private void backSearch() {
        String state = Singleton.getInstance().getViewState();
        isSearch = false;
        if (state == "Songs")
            goSongs();
        else if (state == "Folders") {
            goFolders();
        } else if (state == "Playlist") {
            goPlaylist();
            lvCurrentPlaylist.setVisibility(View.GONE);
        } else if (state == "Album") {
            goAlbum();
            lvCurrentAlbum.setVisibility(View.GONE);
        } else if (state == "Artist") {
            goArtist();
            lvCurrentArtist.setVisibility(View.GONE);
        }
        lvSearch.setVisibility(View.GONE);
        etSearch.setVisibility(View.GONE);
        backSearch.setVisibility(View.GONE);
        if (state != "Folders")
            lvFolders.setVisibility(View.GONE);
        tvListTitle.setVisibility(View.VISIBLE);
        settings.setVisibility(View.VISIBLE);
    }

    private void goQueue() {
        if (isSearch)
            backSearch();
        queueAdapter.notifyDataSetChanged();
        String state = Singleton.getInstance().getViewState();
        if (state == "Songs")
            backSongs();
        else if (state == "Folders")
            backFolders();
        else if (state == "Playlist")
            backPlaylist();
        else if (state == "Album")
            backAlbum();
        else if (state == "Artist")
            backArtist();
        isQueue = true;
        lvQueue.setVisibility(View.VISIBLE);
        tvListTitle.setText("Playing Queue");
        backQueue.setVisibility(View.VISIBLE);
        settings.setVisibility(View.GONE);
        search.setVisibility(View.GONE);
    }

    private void backQueue() {
        String state = Singleton.getInstance().getViewState();
        isQueue = false;
        if (state == "Songs")
            goSongs();
        else if (state == "Folders")
            goFolders();
        else if (state == "Playlist")
            goPlaylist();
        else if (state == "Album")
            goAlbum();
        else if (state == "Artist")
            goArtist();
        lvQueue.setVisibility(View.GONE);
        backQueue.setVisibility(View.GONE);
        settings.setVisibility(View.VISIBLE);
        search.setVisibility(View.VISIBLE);
    }

    private int getCountFromSearchItems(ArrayList<SearchItem> list, int type) {
        int i = 0;
        for (SearchItem s : list)
            if (s.getType() == type)
                i++;
        return i;
    }

    private void setSongList() {
        try {
            String songSource2 = Singleton.getInstance().getSavedSettings().getString("songSource2", null);
            switch (Singleton.getInstance().getSavedSettings().getString("songSource", "Songs")) {
                case "Folders": //Folders
                    if (songSource2 != null) {

                        File file = new File(songSource2);

                        songFolderList.clear();

                        for (File f : file.listFiles()) {
                            String mimeType = URLConnection.guessContentTypeFromName(f.getPath());
                            if (URLConnection.guessContentTypeFromName(f.getPath()) != null && mimeType.startsWith("audio") && !f.isDirectory()) {
                                for (Song song : songArrayList) {
                                    if (song.getPath().equals(f.getPath())) {
                                        songFolderList.add(song);
                                        break;
                                    }
                                }
                            }
                        }

                        if (foldersImage != null)
                            Singleton.getInstance().setPlayingSongBitmap(foldersImage);
                        else
                            Singleton.getInstance().setPlayingSongBitmap(null);

                        Singleton.getInstance().setSongs(songFolderList);
                        break;
                    }
                case "Playlist": //Playlist
                    if (songSource2 != null) {
                        Singleton.getInstance().setSongs(playlistSongs.get(Integer.parseInt(songSource2)));
                        break;
                    }
                case "Artist": //Artist
                    if (songSource2 != null) {
                        Singleton.getInstance().setSongs(artistSongs.get(Integer.parseInt(songSource2)));
                        break;
                    }
                case "Album": //Album
                    if (songSource2 != null) {
                        Singleton.getInstance().setSongs(albumSongs.get(Integer.parseInt(songSource2)));
                        break;
                    }
                case "Search": //Search
                    if (songSource2 != null) {
                        ArrayList<Song> songs = new ArrayList<>();
                        for (Song s : songArrayList) {
                            if (Pattern.compile(Pattern.quote(songSource2), Pattern.CASE_INSENSITIVE).matcher(s.getTitle()).find())
                                songs.add(s);
                        }
                        if (songs.size() == 0)
                            Singleton.getInstance().setSongs(songArrayList);
                        else
                            Singleton.getInstance().setSongs(songs);
                        break;
                    }
                case "Queue": //Last Queue Played
                    if (songSource2 != null && !songSource2.equals("")) {
                        ArrayList<Song> songs = new ArrayList<>();
                        while (songSource2.length() > 1) {
                            String string = getNextPath(songSource2);
                            for (Song s : songArrayList) {
                                if (s.getPath().equals(string)) {
                                    songs.add(s);
                                    break;
                                }
                            }
                            songSource2 = getRestPaths(songSource2);
                        }
                        if (songs.size() == 0)
                            Singleton.getInstance().setSongs(songArrayList);
                        else
                            Singleton.getInstance().setSongs(songs);
                        break;
                    }
                default:
                    Singleton.getInstance().setSongs(songArrayList);
            }
        } catch (Exception e) {
            Singleton.getInstance().setSongs(songArrayList);
            Singleton.getInstance().setCurrentSong(Singleton.getInstance().getSongs().get(0));
        }
        setPlayingQueue();
    }

    private void setCurrentSong() {
        String songPath = Singleton.getInstance().getSavedSettings().getString("currentSongPath", null);
        if (songPath == null) {
            try {
                Singleton.getInstance().setCurrentSong(Singleton.getInstance().getSongs().get(0));
            }
            catch (Exception e) {
                if (Singleton.getInstance().getSongs() == null || Singleton.getInstance().getSongs().size() == 0) {
                    Singleton.getInstance().setSongs(songArrayList);
                    Singleton.getInstance().setCurrentSong(Singleton.getInstance().getSongs().get(0));
                }
            }
            return;
        }
        int i = 0;
        for (Song s : Singleton.getInstance().getSongs()) {
            if (s.getPath().equals(songPath)) {
                Singleton.getInstance().setCurrentSong(Singleton.getInstance().getSongs().get(i));
                return;
            }
            i++;
        }
        if (Singleton.getInstance().getCurrentSong() == null)
            Singleton.getInstance().setCurrentSong(Singleton.getInstance().getSongs().get(0));
    }

    private void setPlayingQueue() {
        queueAdapter = new PlayingQueueAdapter(this, Singleton.getInstance().getSongs());
        lvQueue.setAdapter(queueAdapter);
    }

    private void getSongs() {
        if (songArrayList.size() == 0) {
            ContentResolver contentResolver = getContentResolver();
            Uri songUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;

            Cursor songCursor = contentResolver.query(songUri, null, null, null, null);
            if (songCursor != null && songCursor.moveToFirst()) {

                int indexTitle = songCursor.getColumnIndex(MediaStore.Audio.Media.TITLE);
                int indexArtist = songCursor.getColumnIndex(MediaStore.Audio.Media.ARTIST);
                int indexAlbum = songCursor.getColumnIndex(MediaStore.Audio.Media.ALBUM);
                int indexData = songCursor.getColumnIndex(MediaStore.Audio.Media.DATA);
                int indexPosition = songCursor.getColumnIndex(MediaStore.Audio.Media._ID);
                int i;

                filteredFolders.clear();

                do {
                    String title = songCursor.getString(indexTitle);
                    String artist = songCursor.getString(indexArtist);
                    String album = songCursor.getString(indexAlbum);
                    String position = songCursor.getString(indexPosition);
                    if (album == null || album.equals(""))
                        album = "Unknown";
                    if (artist == null || artist.equals("") || artist.equals("<unknown>")) {
                        artist = getArtistName(title);
                        if (!artist.equals("Unknown")) {
                            title = getSongName(title);
                        }
                    }
                    String path = songCursor.getString(indexData);
                    Song song = new Song(title, artist, path, 0, album, position);
                    i = 0;
                    for (Song s : songArrayList) {
                        if (s.getTitle().compareTo(song.getTitle()) < 0) {
                            i++;
                        }
                    }
                    songArrayList.add(i, song);

                    path = getDirectory(path);
                    filteredFolders.add(path);


                } while (songCursor.moveToNext());
                i = 0;
                for (Song s : songArrayList) {
                    s.setID(i++);
                }
            }
            songsAdapter.notifyDataSetChanged();
            removeDuplicatePaths(filteredFolders);
            getFoldersNames(filteredFolders);
        }
    }

    private String getSongName(String title) {
        int i = title.indexOf('-');
        try {
            return title.substring(i + 2);
        } catch (Exception e) {
            return title;
        }
    }

    private String getArtistName(String title) {
        if (title.contains(" - ")) {
            int i = title.indexOf('-');
            if (i > 1) {
                return title.substring(0, i - 1);
            }
        }
        return "Unknown";
    }

    private void toggleSong() {
        if (Singleton.getInstance().getMediaPlayer() != null)
            Singleton.getInstance().toggleNotificationSong();
    }

    private String getDirectory(String path) {
        for (int i = path.length() - 1; i >= 0; i--) {
            if (path.charAt(i) == '/')
                return path.substring(0, i);
        }
        return null;
    }

    private String removeExtension(String title) {
        for (int i = title.length() - 1; i >= 0; i--) {
            if (title.charAt(i) == '.')
                return title.substring(0, i);
        }
        return null;
    }

    private void removeDuplicatePaths(ArrayList<String> filteredFolders) {
        ArrayList<String> newFiltered = new ArrayList<>();
        for (String s : filteredFolders) {
            if (!newFiltered.contains(s))
                newFiltered.add(s);
        }
        filteredFolders.clear();
        for (String s : newFiltered)
            filteredFolders.add(s);
    }

    private void getFolders() {
        File file = new File(Environment.getExternalStorageDirectory().getPath());
        getFolderChildren(file);
        foldersAdapter.setFolderImage(foldersImage);
        foldersAdapter.notifyDataSetChanged();
    }

    private void getFolderChildren(File parent) {
        File[] files = parent.listFiles();
        parentFile = parent;
        folderArrayList.clear();
        boolean imageChanged = false;
        int foldersCount = 0;
        for (File f : files) {
            if (!f.getPath().contains(".m3u")) {
                String mimeType = URLConnection.guessContentTypeFromName(f.getPath());
                for (String ff : filteredFolders) {
                    if (mimeType != null && mimeType.startsWith("audio")) {
                        folderArrayList.add(f);
                        break;
                    } else if (ff.contains(f.getPath())) {
                        int j = 0;
                        for (int i = 0; i < foldersCount; i++) {
                            if (f.getName().compareTo(folderArrayList.get(i).getName()) > 0) {
                                j++;
                            }
                        }
                        folderArrayList.add(j, f);
                        foldersCount++;
                        break;
                    } else if (mimeType != null && mimeType.startsWith("image")) {
                        foldersImage = BitmapFactory.decodeFile(f.getPath());
                        imageChanged = true;
                        break;
                    }
                }
            }
        }
        if (!imageChanged)
            foldersImage = null;
        foldersAdapter.setFolderImage(foldersImage);
        foldersAdapter.notifyDataSetChanged();
    }

    private void getFoldersNames(ArrayList<String> filteredFolders) {
        if (foldersNames == null)
            foldersNames = new ArrayList<>();
        foldersNames.clear();
        for (String ff : filteredFolders) {
            char[] cA = ff.toCharArray();
            int i;
            for (i = cA.length - 1; i >= 0; i--) {
                if (cA[i] == '/')
                    break;
            }
            foldersNames.add(ff.substring(i + 1));
        }
    }

    private void savePlaylists() {
        Singleton.getInstance().savePlaylists(playlistList, playlistSongs);
    }

    private void loadPlaylists() {
        playlistList.add(0, new Playlist(null, null));

        String names = Singleton.getInstance().getSavedSettings().getString("playlistNames", null);
        String bitmaps = Singleton.getInstance().getSavedSettings().getString("playlistImages", null);
        String songs = Singleton.getInstance().getSavedSettings().getString("playlistSongs", null);

        while (true) {
            if (names == null || names.length() == 0)
                break;
            playlistList.add(new Playlist(getNextPlaylistName(names), getNextPlaylistBitmap(bitmaps)));

            names = getRestPlaylistNames(names);
            bitmaps = getRestPlaylistBitmaps(bitmaps);
        }

        for (int i = 0; i < playlistList.size() - 1; i++) {
            playlistSongs.add(new ArrayList<>());
            while (true) {
                int status = getNextSongID(songs);
                songs = getRestSongIDs(songs);
                if (status < 0)
                    break;
                else
                    playlistSongs.get(i).add(songArrayList.get(status));
            }
        }
        playlistAdapter.notifyDataSetChanged();
    }

    private void getAlbums() {
        ContentResolver contentResolver = getContentResolver();
        Uri albumUri = MediaStore.Audio.Albums.EXTERNAL_CONTENT_URI;
        ArrayList<String> titles = new ArrayList<>();
        Cursor albumCursor = contentResolver.query(albumUri, null, null, null, null);
        if (albumCursor != null && albumCursor.moveToFirst()) {
            int indexTitle = albumCursor.getColumnIndex(MediaStore.Audio.Albums.ALBUM);
            int indexArtist = albumCursor.getColumnIndex(MediaStore.Audio.Albums.ARTIST);
            do {
                String title = albumCursor.getString(indexTitle);
                String artist = albumCursor.getString(indexArtist);
                if (title == null || title.equals(""))
                    title = "Unknown";
                boolean b = true;
                for (String s : titles)
                    if (s.equals(title)) {
                        b = false;
                        break;
                    }
                if (b) {
                    Album album = new Album(title, null, artist);
                    albumArrayList.add(album);
                    titles.add(title);
                }

            } while (albumCursor.moveToNext());
            titles.clear();
        }

        albumAdapter.notifyDataSetChanged();

        albumSongs = new ArrayList<>();

        int i = 0;
        for (Album album : albumArrayList) {
            albumSongs.add(new ArrayList<>());
            for (Song song : songArrayList) {
                if (song.getAlbum().equals(album.getAlbumName())) {
                    if (albumSongs.get(i).size() == 0)
                        albumSongs.get(i).add(song);
                    else {
                        int j = 0;
                        for (Song s : albumSongs.get(i)) {
                            if (s.getPosition().compareTo(song.getPosition()) < 0)
                                j++;
                        }
                        albumSongs.get(i).add(j, song);
                    }
                }
            }
            i++;
        }
    }

    private void getArtists() {
        for (Song s : songArrayList) {
            boolean b = false;
            for (Artist a : artistArrayList) {
                if (a.getArtistName().equals(s.getArtist())) {
                    b = true;
                    break;
                }
            }
            if (!b)
                artistArrayList.add(new Artist(s.getArtist(), null));
        }


        artistAdapter.notifyDataSetChanged();

        artistSongs = new ArrayList<>();

        int i = 0;
        for (Artist artist : artistArrayList) {
            artistSongs.add(new ArrayList<>());
            for (Song song : songArrayList) {
                if (song.getArtist().equals(artist.getArtistName())) {
                    if (artistSongs.get(i).size() == 0)
                        artistSongs.get(i).add(song);
                    else {
                        int j = 0;
                        for (Song s : artistSongs.get(i)) {
                            if (s.getTitle().compareTo(song.getTitle()) < 0)
                                j++;
                        }
                        artistSongs.get(i).add(j, song);
                    }
                }
            }
            artistArrayList.get(i).setTrackCount(artistSongs.get(i).size());
            i++;
        }

    }

    private void prepareSearch() {
        if (searchItems != null) {
            searchItems.clear();
        }
        searchItems = new ArrayList<>();

        searchItems.add(new SearchItem(SearchItem.SONGTITLE));
        for (Song s : songArrayList)
            searchItems.add(new SearchItem(SearchItem.SONG, s));

        searchItems.add(new SearchItem(SearchItem.ALBUMTITLE));
        for (Album a : albumArrayList)
            searchItems.add(new SearchItem(SearchItem.ALBUM, a));

        searchItems.add(new SearchItem(SearchItem.ARTISTTITLE));
        for (Artist a : artistArrayList)
            searchItems.add(new SearchItem(SearchItem.ARTIST, a));

        searchItems.add(new SearchItem(SearchItem.FOLDERTITLE));
        for (String f : foldersNames)
            searchItems.add(new SearchItem(SearchItem.FOLDER, f));

        searchItems.add(new SearchItem(SearchItem.PLAYLISTTITLE));
        int i = 0;
        for (Playlist p : playlistList)
            if (i != 0) {
                searchItems.add(new SearchItem(SearchItem.PLAYLIST, p));
            } else
                i++;

        searchAdapter = new SearchAdapter(this, searchItems);
        lvSearch.setAdapter(searchAdapter);
        searchAdapter.notifyDataSetChanged();

        i = 0;
        for (SearchItem s : searchItems)
            s.setID(i++);

        int songCount = getCountFromSearchItems(searchItems, SearchItem.SONG);
        int albumCount = getCountFromSearchItems(searchItems, SearchItem.ALBUM);
        int artistCount = getCountFromSearchItems(searchItems, SearchItem.ARTIST);
        int folderCount = getCountFromSearchItems(searchItems, SearchItem.FOLDER);
        int playlistCount = getCountFromSearchItems(searchItems, SearchItem.PLAYLIST);

        if (hiddenItems == null)
            hiddenItems = new ArrayList<>();
        hiddenItems.clear();

        for (SearchItem s : searchItems)
            switch (s.getType()) {
                case SearchItem.SONGTITLE:
                    if (songCount == 0)
                        hiddenItems.add(s);
                    break;
                case SearchItem.ALBUMTITLE:
                    if (albumCount == 0)
                        hiddenItems.add(s);
                    break;
                case SearchItem.ARTISTTITLE:
                    if (artistCount == 0)
                        hiddenItems.add(s);
                    break;
                case SearchItem.FOLDERTITLE:
                    if (folderCount == 0)
                        hiddenItems.add(s);
                    break;
                case SearchItem.PLAYLISTTITLE:
                    if (playlistCount == 0)
                        hiddenItems.add(s);
                    break;
            }

        for (SearchItem h : hiddenItems)
            searchItems.remove(h);

        searchAdapter.notifyDataSetChanged();

    }

    private String getNextPath(String songSource2) {
        int i = 0;
        for (char c : songSource2.toCharArray()) {
            if (c == '|')
                break;
            i++;
        }
        return songSource2.substring(0, i);
    }

    private String getRestPaths(String songSource2) {
        int i = 0;
        for (char c : songSource2.toCharArray()) {
            if (c == '|')
                break;
            i++;
        }
        return songSource2.substring(i + 1);
    }

    private String getNextPlaylistName(String names) {
        int i = 0;
        for (char c : names.toCharArray()) {
            if (c == '/')
                break;
            i++;
        }
        if (i == 0)
            return null;
        return names.substring(0, i);
    }

    private Bitmap getNextPlaylistBitmap(String bitmaps) {
        int i = 0;
        if (bitmaps != null) {
            for (char c : bitmaps.toCharArray()) {
                if (c == '|')
                    break;
                i++;
            }
        }
        if (i == 0)
            return null;

        byte[] imageAsBytes = android.util.Base64.decode(bitmaps.substring(0, i).getBytes(), android.util.Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(imageAsBytes, 0, imageAsBytes.length);
    }

    private int getNextSongID(String songs) {
        if (songs == null || songs.length() == 0)
            return -1;
        int i = 0;
        for (char c : songs.toCharArray()) {
            if (c == '?')
                break;
            else if (c == '|')
                return -2;
            i++;
        }

        String path = songs.substring(0, i);
        for (Song s : songArrayList) {
            if (path.equals(s.getPath()))
                return s.getID();
        }
        return -3;
    }

    private String getRestSongIDs(String songs) {
        int i = 0;
        for (char c : songs.toCharArray()) {
            if (c == '?' || c == '|')
                break;
            i++;
        }
        return songs.substring(i + 1);
    }

    private String getRestPlaylistNames(String names) {
        int i = 0;
        for (char c : names.toCharArray()) {
            if (c == '/')
                break;
            i++;
        }
        if (i == 0)
            return null;
        return names.substring(i + 1);
    }

    private String getRestPlaylistBitmaps(String paths) {
        int i = 0;
        if (paths != null) {
            for (char c : paths.toCharArray()) {
                if (c == '|')
                    break;
                i++;
            }

            if (i == 0 && paths.length() == 0)
                return null;
        }
        return paths.substring(i + 1);
    }

    private int getSelectedItemsCount() {
        int i = 0;
        for (Boolean b : selectedItems) {
            if (b)
                i++;
        }
        return i;
    }

    private void clearSelectedSongs() {
        if (selectedItems != null) {
            selectedItems.clear();
            selectedItems = null;
        }
        selectionOptionsSongs.setVisibility(View.GONE);
        songsAdapter.notifyDataSetChanged();
    }

    private void clearSelectedPlaylists() {
        if (selectedItems != null) {
            selectedItems.clear();
            selectedItems = null;
        }
        selectionOptions.setVisibility(View.GONE);
        selectionOptionsSongs.setVisibility(View.GONE);
        playlistAdapter.notifyDataSetChanged();
    }

    private void clearSelectedPlaylistSongs() {
        if (selectedItems != null) {
            selectedItems.clear();
            selectedItems = null;
        }
        selectionCurrentPlaylist.setVisibility(View.GONE);
        if (playlistSongsAdapter != null)
            playlistSongsAdapter.notifyDataSetChanged();
        search.setVisibility(View.VISIBLE);
    }

    private void clearSelectedAlbums() {
        if (selectedItems != null) {
            selectedItems.clear();
            selectedItems = null;
        }
        selectionAlbums.setVisibility(View.GONE);
        albumAdapter.notifyDataSetChanged();
        search.setVisibility(View.VISIBLE);
    }

    private void clearSelectedArtists() {
        if (selectedItems != null) {
            selectedItems.clear();
            selectedItems = null;
        }
        selectionArtists.setVisibility(View.GONE);
        artistAdapter.notifyDataSetChanged();
        search.setVisibility(View.VISIBLE);
    }

    private void clearSelectedFolders() {
        if (selectedItems != null) {
            selectedItems.clear();
            selectedItems = null;
        }
        selectionFolders.setVisibility(View.GONE);
        foldersAdapter.notifyDataSetChanged();
        search.setVisibility(View.VISIBLE);
    }

    private void toggleSelection(ImageView selectionImage, boolean b) {
        if (b)
            selectionImage.setImageResource(R.drawable.tick);
        else
            selectionImage.setImageResource(R.drawable.untick);
    }

    private void trimPathStart() {
        String path = tvPath.getText().toString();
        if (Environment.getExternalStorageDirectory().getPath().equals(path)) {
            tvPath.setText("0");
            return;
        }
        for (int i = 0; i < path.length(); i++) {
            if (Environment.getExternalStorageDirectory().getPath().equals(path.substring(0, i))) {
                path = path.substring(i - 1);
                tvPath.setText(path);
                return;
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getSongs();
                loadPlaylists();
                getAlbums();
                getArtists();
                getFolders();

                //Before setting the SongList, Check for its presence
                setSongList();
                setCurrentSong();

                tvSongName.setText(Singleton.getInstance().getCurrentSong().getTitle());
                tvArtist.setText(Singleton.getInstance().getCurrentSong().getArtist());

                prepareSearch();
            } else {
                Toast.makeText(getBaseContext(), "Permission Denied", Toast.LENGTH_SHORT).show();
                //if (!ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_EXTERNAL_STORAGE))
                    requestStoragePermission();
            }
        }
    }

    @Override
    public void onBackPressed() {
        if (isSearch) {
            if (lvCurrentAlbum.getVisibility() == View.VISIBLE) {
                lvCurrentAlbum.setVisibility(View.GONE);
                settings.setVisibility(View.GONE);
                tvListTitle.setVisibility(View.GONE);
                lvSearch.setVisibility(View.VISIBLE);
                etSearch.setVisibility(View.VISIBLE);
                backSearch.setVisibility(View.VISIBLE);
            } else if (lvCurrentArtist.getVisibility() == View.VISIBLE) {
                lvCurrentArtist.setVisibility(View.GONE);
                settings.setVisibility(View.GONE);
                tvListTitle.setVisibility(View.GONE);
                lvSearch.setVisibility(View.VISIBLE);
                etSearch.setVisibility(View.VISIBLE);
                backSearch.setVisibility(View.VISIBLE);
            } else if (lvFolders.getVisibility() == View.VISIBLE) {
                if (filteredFolders.get(dummyInteger).contains(tvPath.getText().toString())) {
                    lvFolders.setVisibility(View.GONE);
                    settings.setVisibility(View.GONE);
                    lvSearch.setVisibility(View.VISIBLE);
                    etSearch.setVisibility(View.VISIBLE);
                    backSearch.setVisibility(View.VISIBLE);
                    tvPath.setVisibility(View.GONE);
                } else {
                    if (!folderArrayList.isEmpty()) {
                        if (folderArrayList.get(0).getParentFile().getPath().equals(Environment.getExternalStorageDirectory().getPath()))
                            super.onBackPressed();
                        else
                            getFolderChildren(folderArrayList.get(0).getParentFile().getParentFile());
                    } else {
                        if (parentFile.getPath().equals(Environment.getExternalStorageDirectory().getPath())) {
                            folderArrayList.clear();
                            getFolderChildren(parentFile);
                        } else {
                            folderArrayList.clear();
                            getFolderChildren(parentFile.getParentFile());
                            foldersAdapter.setFolderImage(foldersImage);
                            foldersAdapter.notifyDataSetChanged();
                        }
                    }
                    if (!Environment.getExternalStorageDirectory().getPath().equals(folderArrayList.get(0).getPath()))
                        tvPath.setText(parentFile.getPath());
                    else
                        tvPath.setText(parentFile.getParentFile().getPath());
                    trimPathStart();
                }
            } else if (lvCurrentPlaylist.getVisibility() == View.VISIBLE) {
                settings.setVisibility(View.GONE);
                lvCurrentPlaylist.setVisibility(View.GONE);
                tvListTitle.setVisibility(View.GONE);
                lvSearch.setVisibility(View.VISIBLE);
                etSearch.setVisibility(View.VISIBLE);
                backSearch.setVisibility(View.VISIBLE);

                tvPlaylistName.setVisibility(View.GONE);
                addSongToPlaylist.setVisibility(View.GONE);
            } else
                backSearch();
        } else if (isQueue)
            backQueue();
        else if (Singleton.getInstance().getViewState().equals("Folders")) {
            if (selectedItems != null)
                clearSelectedFolders();
            else if (!folderArrayList.isEmpty()) {
                if (folderArrayList.get(0).getParentFile().getPath().equals(Environment.getExternalStorageDirectory().getPath()))
                    super.onBackPressed();
                else
                    getFolderChildren(folderArrayList.get(0).getParentFile().getParentFile());
            } else {
                if (parentFile.getPath().equals(Environment.getExternalStorageDirectory().getPath())) {
                    folderArrayList.clear();
                    getFolderChildren(parentFile);
                } else {
                    folderArrayList.clear();
                    getFolderChildren(parentFile.getParentFile());
                    foldersAdapter.setFolderImage(foldersImage);
                    foldersAdapter.notifyDataSetChanged();
                }
            }
            if (!Environment.getExternalStorageDirectory().getPath().equals(folderArrayList.get(0).getPath()))
                tvPath.setText(parentFile.getPath());
            else
                tvPath.setText(parentFile.getParentFile().getPath());
            trimPathStart();
        } else if (Singleton.getInstance().getViewState().equals("Songs")) {
            if (selectedItems != null)
                clearSelectedSongs();
            else
                super.onBackPressed();
        } else if (Singleton.getInstance().getViewState().equals("Playlist")) {
            if (lvCurrentPlaylist.getVisibility() == View.VISIBLE) {
                if (selectionCurrentPlaylist.getVisibility() == View.VISIBLE)
                    clearSelectedPlaylistSongs();
                else {
                    lvCurrentPlaylist.setVisibility(View.GONE);
                    tvNoPlaylistSongs.setVisibility(View.GONE);
                    tvPlaylistName.setVisibility(View.GONE);
                    addSongToPlaylist.setVisibility(View.GONE);
                    lvPlaylist.setVisibility(View.VISIBLE);
                    tvListTitle.setText("Playlists");
                }
            } else if (lvSongs.getVisibility() == View.VISIBLE) {
                clearSelectedPlaylists();
                lvSongs.setVisibility(View.GONE);
                lvCurrentPlaylist.setVisibility(View.VISIBLE);
                if (playlistSongs.get(playlistNumber - 1).size() == 0)
                    tvNoPlaylistSongs.setVisibility(View.VISIBLE);
                tvPlaylistName.setVisibility(View.VISIBLE);
                addSongToPlaylist.setVisibility(View.VISIBLE);
            } else if (selectedItems != null)
                clearSelectedPlaylists();
            else
                super.onBackPressed();
        } else if (Singleton.getInstance().getViewState().equals("Album")) {
            if (lvCurrentAlbum.getVisibility() == View.VISIBLE) {
                lvCurrentAlbum.setVisibility(View.GONE);
                gvAlbum.setVisibility(View.VISIBLE);
                tvListTitle.setText("Albums");
            } else if (selectedItems != null)
                clearSelectedAlbums();
            else
                super.onBackPressed();
        } else if (Singleton.getInstance().getViewState().equals("Artist")) {
            if (lvCurrentArtist.getVisibility() == View.VISIBLE) {
                lvCurrentArtist.setVisibility(View.GONE);
                lvArtist.setVisibility(View.VISIBLE);
                tvListTitle.setText("Artists");
            } else if (selectedItems != null)
                clearSelectedArtists();
            else
                super.onBackPressed();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == OUTER_PLAY) {
            if (resultCode == 0)
                play.setImageResource(R.drawable.playbutton);
            else if (resultCode == 1)
                play.setImageResource(R.drawable.pause);
        } else if (requestCode == NEW_PLAYLIST) {
            if (Singleton.getInstance().isNewPlaylistExist()) {
                int i = 0;
                for (Playlist p : playlistList)
                    if (p.getPlaylistName() != null)
                        if (p.getPlaylistName().equals(Singleton.getInstance().getNewPlaylistName())) {
                            i++;
                            if (i == 1)
                                Singleton.getInstance().setNewPlaylistName(Singleton.getInstance().getNewPlaylistName() + " " + i);
                            else if (i > 1)
                                Singleton.getInstance().setNewPlaylistName(Singleton.getInstance().getNewPlaylistName().substring(0, Singleton.getInstance().getNewPlaylistName().length() - 1) + i);
                        }


                playlistList.add(new Playlist(Singleton.getInstance().getNewPlaylistName(), Singleton.getInstance().getNewPlaylistBitmap()));

                Singleton.getInstance().setNewPlaylistExist(false);
                Singleton.getInstance().setNewPlaylistBitmap(null);
                playlistSongs.add(new ArrayList<>());
                playlistAdapter.notifyDataSetChanged();
                savePlaylists();
                prepareSearch();
            }
        } else if (requestCode == EDIT_PLAYLIST) {
            int i = 0, j = 0;
            for (Boolean b : selectedItems) {
                if (b)
                    break;
                j++;
            }

            int k = 0;
            for (Playlist p : playlistList) {
                if (p.getPlaylistName() != null)
                    if (p.getPlaylistName().equals(Singleton.getInstance().getNewPlaylistName()) && (k != j + 1)) {
                        i++;
                        if (i == 1)
                            Singleton.getInstance().setNewPlaylistName(Singleton.getInstance().getNewPlaylistName() + " " + i);
                        else if (i > 1)
                            Singleton.getInstance().setNewPlaylistName(Singleton.getInstance().getNewPlaylistName().substring(0, Singleton.getInstance().getNewPlaylistName().length() - 1) + i);
                    }
                k++;
            }

            playlistList.get(j + 1).setPlaylistName(Singleton.getInstance().getNewPlaylistName());
            if (Singleton.getInstance().getNewPlaylistBitmap() != null) {
                playlistList.get(j + 1).setPlaylistImage(Singleton.getInstance().getNewPlaylistBitmap());
                Singleton.getInstance().setNewPlaylistBitmap(null);
            }

            Singleton.getInstance().setNewPlaylistExist(false);
            playlistAdapter.notifyDataSetChanged();
            savePlaylists();
            clearSelectedPlaylists();
        }
        Song song = Singleton.getInstance().getCurrentSong();
        if (song != null) {
            tvSongName.setText(song.getTitle());
            tvArtist.setText(song.getArtist());
        }
    }

    @Override
    protected void onPause() {
        Singleton.getInstance().updateSavedSettings(null);
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (Singleton.getInstance().getMediaPlayer() != null) {
            if (Singleton.getInstance().getMediaPlayer().isPlaying())
                play.setImageResource(R.drawable.pause);
            else
                play.setImageResource(R.drawable.playbutton);
            Singleton.getInstance().showNotification();
        }
        if (Singleton.getInstance().getPlayListNumbers() != null && Singleton.getInstance().getViewState() == "Songs") {
            if (!(Singleton.getInstance().getPlayListNumbers().size() == 1 && Singleton.getInstance().getPlayListNumbers().get(0) == playlistList.size())) {
                int i = 0;
                for (Boolean b : selectedItems) {
                    if (b) {
                        for (int j : Singleton.getInstance().getPlayListNumbers()) {
                            playlistSongs.get(j).add(songArrayList.get(i));
                        }
                    }
                    i++;
                }
                setPlayingQueue();
            } else {
                playlistList.add(new Playlist(Singleton.getInstance().getNewPlaylistName(), Singleton.getInstance().getNewPlaylistBitmap()));

                Singleton.getInstance().setNewPlaylistExist(false);
                Singleton.getInstance().setNewPlaylistBitmap(null);

                playlistSongs.add(new ArrayList<>());
                int i = 0;
                for (Boolean b : selectedItems) {
                    if (b)
                        playlistSongs.get(playlistSongs.size() - 1).add(songArrayList.get(i));
                    i++;
                }
            }
            clearSelectedSongs();
            playlistAdapter.notifyDataSetChanged();
            savePlaylists();
            Singleton.getInstance().getPlayListNumbers().clear();
            Singleton.getInstance().setPlayListNumbers(null);
        }

        if (Singleton.getInstance().notification != null)
            Singleton.getInstance().updateNotificationPlay();

        if (Singleton.getInstance().getMediaPlayer() == null)
            notificationManager.cancel(1);
    }

    @Override
    protected void onDestroy() {
        if (Singleton.getInstance().getMediaPlayer() == null || !Singleton.getInstance().getMediaPlayer().isPlaying())
            notificationManager.cancel(1);

        super.onDestroy();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_HEADSETHOOK)
            toggleSong();
        else if (keyCode == KeyEvent.KEYCODE_MEDIA_PAUSE && Singleton.getInstance().getMediaPlayer() != null && Singleton.getInstance().getMediaPlayer().isPlaying())
            toggleSong();
        else if (keyCode == KeyEvent.KEYCODE_MEDIA_PLAY && Singleton.getInstance().getMediaPlayer() != null && !Singleton.getInstance().getMediaPlayer().isPlaying())
            toggleSong();
        else if (keyCode == KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE && Singleton.getInstance().getMediaPlayer() != null)
            toggleSong();
        else if (keyCode == KeyEvent.KEYCODE_MEDIA_CLOSE && Singleton.getInstance().getMediaPlayer() != null) {
            if (Singleton.getInstance().getMediaPlayer().isPlaying())
                toggleSong();
            Singleton.getInstance().getMediaPlayer().stop();
        } else if (keyCode == KeyEvent.KEYCODE_MEDIA_NEXT)
            Singleton.getInstance().playNextSong(null);
        else if (keyCode == KeyEvent.KEYCODE_MEDIA_PREVIOUS)
            Singleton.getInstance().playPreviousSong();
        return super.onKeyDown(keyCode, event);
    }
}
