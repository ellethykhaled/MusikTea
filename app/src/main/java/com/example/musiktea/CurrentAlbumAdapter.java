package com.example.musiktea;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.List;

public class CurrentAlbumAdapter extends ArrayAdapter<Song> {
    Bitmap bitmap = null;
    public CurrentAlbumAdapter(@NonNull Context context, @NonNull List<Song> objects, Bitmap bitmap) {
        super(context, 0, objects);
        this.bitmap = bitmap;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_song_of_album, null);

        TextView tvTitle = convertView.findViewById(R.id.tvTitle);
        ImageView image = convertView.findViewById(R.id.image);

        tvTitle.setText(getItem(position).getTitle());
        if (bitmap != null)
            image.setImageBitmap(bitmap);

        return convertView;
    }

}