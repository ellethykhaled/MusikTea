package com.example.musiktea;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;


public class PopActivityEditPlaylist extends Activity {

    public static final int PICK_IMAGE = 1;

    TextView popUpName;
    ImageView cancel, edit, playlistImage;
    EditText playlistName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pop_playlist_add_edit);

        String name = (String) getIntent().getSerializableExtra("playlistName");

        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);

        getWindow().setLayout((int) (dm.widthPixels * 0.8),(int) (dm.heightPixels * 0.6));

        cancel = findViewById(R.id.cancelNewPlaylist);
        edit = findViewById(R.id.addNewPlaylist);
        playlistImage = findViewById(R.id.addPlaylistImage);
        playlistName = findViewById(R.id.etPlaylistName);
        popUpName = findViewById(R.id.popUpName);

        popUpName.setText("Edit Playlist");

        if (Singleton.getInstance().getNewPlaylistBitmap() == null)
            playlistImage.setImageResource(R.drawable.addimage);
        else
            playlistImage.setImageBitmap(Singleton.getInstance().getNewPlaylistBitmap());

        playlistName.setText(name, TextView.BufferType.EDITABLE);

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        playlistImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //take Image
                Intent getIntent = new Intent(Intent.ACTION_GET_CONTENT);
                getIntent.setType("image/*");

                Intent pickIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                pickIntent.setType("image/*");

                Intent chooserIntent = Intent.createChooser(getIntent, "Select Image");
                chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, new Intent[] {pickIntent});

                startActivityForResult(chooserIntent, PICK_IMAGE);
            }
        });
        edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
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
                    finish();
                }
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