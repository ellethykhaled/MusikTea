package com.example.musiktea;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.List;

public class CurrentArtistAdapter extends ArrayAdapter<Song> {
    public CurrentArtistAdapter(@NonNull Context context, @NonNull List<Song> objects) {
        super(context, 0, objects);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_song_of_artist, null);

        TextView tvTitle = convertView.findViewById(R.id.tvTitle);

        tvTitle.setText(getItem(position).getTitle());

        return convertView;
    }

}