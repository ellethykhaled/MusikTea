package com.example.musiktea;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;


public class PopActivityNewPlaylist extends Activity {

    public static final int PICK_IMAGE = 1;

    TextView popUpName;
    ImageView cancel, add, playlistImage;
    EditText playlistName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pop_playlist_add_edit);

        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);

        getWindow().setLayout((int) (dm.widthPixels * 0.8),(int) (dm.heightPixels * 0.6));

        cancel = findViewById(R.id.cancelNewPlaylist);
        add = findViewById(R.id.addNewPlaylist);
        playlistImage = findViewById(R.id.addPlaylistImage);
        playlistName = findViewById(R.id.etPlaylistName);
        popUpName = findViewById(R.id.popUpName);

        popUpName.setText("Create New Playlist");

        Singleton.getInstance().setNewPlaylistName(null);
        Singleton.getInstance().setNewPlaylistImageUri(null);

        cancel.setOnClickListener(view -> {
            Singleton.getInstance().setNewPlaylistExist(false);
            Singleton.getInstance().setNewPlaylistName(null);
            Singleton.getInstance().setNewPlaylistImageUri(null);
            finish();
        });

        playlistImage.setOnClickListener(view -> {
            //take Image
            Intent getIntent = new Intent(Intent.ACTION_GET_CONTENT);
            getIntent.setType("image/*");

            Intent pickIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            pickIntent.setType("image/*");

            Intent chooserIntent = Intent.createChooser(getIntent, "Select Image");
            chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, new Intent[] {pickIntent});

            startActivityForResult(chooserIntent, PICK_IMAGE);
        });
        add.setOnClickListener(view -> {
            boolean allspace = true;
            for (char c : playlistName.getText().toString().toCharArray()) {
                if (c != ' ') {
                    allspace = false;
                    break;
                }
            }
            boolean falseName = false;
            for (char c : playlistName.getText().toString().toCharArray()) {
                if (c == '/') {
                    falseName = true;
                    break;
                }
            }
            if (allspace || falseName)
                Toast.makeText(getApplicationContext(), "Invalid Name", Toast.LENGTH_LONG).show();
            else {
                Singleton.getInstance().setNewPlaylistName(playlistName.getText().toString());
                Singleton.getInstance().setNewPlaylistExist(true);
                setResult(5);
                finish();
            }
        });
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PICK_IMAGE && data != null) {
            Singleton.getInstance().setNewPlaylistImageUri(data.getData());
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), data.getData());
                playlistImage.setImageBitmap(bitmap);
                Singleton.getInstance().setNewPlaylistBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}