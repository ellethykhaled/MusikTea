package com.example.musiktea;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;

import org.w3c.dom.Text;

import java.util.ArrayList;


public class PopActivityAddToPlaylist extends Activity{

    private static final int NEW_PLAYLIST = 2;

    ImageView createNewPlaylist, addToPlaylist;
    TextView tvNoPlaylist;
    ListView lvPlaylistList;
    AddingPlaylistAdapter addingPlaylistAdapter;
    ArrayList<String> playlistNames;
    ArrayList<Boolean> selectedItems;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pop_addtoplaylist);

        String names = (String) getIntent().getSerializableExtra("playlistNames");
        if (!names.equals(""))
            setPlaylistNames(names);

        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);

        getWindow().setLayout((int) (dm.widthPixels * 0.8),(int) (dm.heightPixels * 0.6));

        lvPlaylistList = findViewById(R.id.lvPlaylistsList);
        addToPlaylist = findViewById(R.id.addToPlaylist);
        createNewPlaylist = findViewById(R.id.createPlaylist);
        tvNoPlaylist = findViewById(R.id.tvNoPlaylist);

        addingPlaylistAdapter = new AddingPlaylistAdapter(this, playlistNames, selectedItems);
        if (playlistNames != null) {
            lvPlaylistList.setAdapter(addingPlaylistAdapter);
            addingPlaylistAdapter.notifyDataSetChanged();
            tvNoPlaylist.setVisibility(View.GONE);
            lvPlaylistList.setOnItemClickListener((adapterView, view, i, l) -> {
                ImageView imageView = view.findViewById(R.id.playlistImage);

                selectedItems.set(i, !selectedItems.get(i));
                if (selectedItems.get(i))
                    imageView.setImageResource(R.drawable.tick);
                else
                    imageView.setImageResource(R.drawable.untick);
            });
        }
        else
            tvNoPlaylist.setVisibility(View.VISIBLE);

        addToPlaylist.setOnClickListener(view -> {
            if (selectedItems == null) {
                Toast.makeText(getApplicationContext(), "No Playlists Found\nCreate New Playlist", Toast.LENGTH_LONG).show();
                return;
            }
            if (!selectedItems.contains(true)) {
                Toast.makeText(getApplicationContext(), "Select Playlists", Toast.LENGTH_LONG).show();
                return;
            }
            Singleton.getInstance().setPlayListNumbers(new ArrayList<>());
            int i = 0;
            for (boolean b : selectedItems) {
                if (b)
                    Singleton.getInstance().getPlayListNumbers().add(i++);
                else
                    i++;
            }
            finish();
        });
        createNewPlaylist.setOnClickListener(view -> {
            Intent intent = new Intent(Singleton.getInstance().getOuterActivity(), PopActivityNewPlaylist.class);
            startActivityForResult(intent, NEW_PLAYLIST);
        });
    }

    private void setPlaylistNames(String names) {
        playlistNames = new ArrayList<>();
        selectedItems = new ArrayList<>();
        playlistNames.add(getNextPlaylistName(names));
        selectedItems.add(false);
        names = getRestPlaylistNames(names);
        while (getRestPlaylistNames(names) != null) {
            playlistNames.add(getNextPlaylistName(names));
            selectedItems.add(false);
            names = getRestPlaylistNames(names);
        }
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

    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == NEW_PLAYLIST && resultCode == 5) {
            if (Singleton.getInstance().isNewPlaylistExist()) {
                int i = 0;
                for (String name : playlistNames)
                    if (name != null)
                        if (name.equals(Singleton.getInstance().getNewPlaylistName())) {
                            i++;
                            if (i == 1)
                                Singleton.getInstance().setNewPlaylistName(Singleton.getInstance().getNewPlaylistName() + " " + i);
                            else if (i > 1)
                                Singleton.getInstance().setNewPlaylistName(Singleton.getInstance().getNewPlaylistName().substring(0, Singleton.getInstance().getNewPlaylistName().length() - 1) + i);
                        }
            }
            Singleton.getInstance().setPlayListNumbers(new ArrayList<>());
            Singleton.getInstance().getPlayListNumbers().add(selectedItems.size() + 1);
            finish();
        }

    }
}