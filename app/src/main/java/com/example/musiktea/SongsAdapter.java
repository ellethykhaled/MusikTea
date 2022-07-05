package com.example.musiktea;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

public class SongsAdapter extends ArrayAdapter<Song> {
    private ArrayList<Boolean> selectedItems;
    public SongsAdapter(@NonNull Context context, @NonNull List<Song> objects, ArrayList<Boolean> selectedItems) {
        super(context, 0, objects);
        this.selectedItems = selectedItems;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_song, null);

        TextView tvTitle = convertView.findViewById(R.id.tvTitle);
        TextView tvArtist = convertView.findViewById(R.id.tvArtist);
        ImageView selectionSong = convertView.findViewById(R.id.selectionSong);

        Song song = getItem(position);
        tvArtist.setText(song.getArtist());
        tvTitle.setText(song.getTitle());

        if (selectedItems != null) {
            if (selectedItems.size() == 0)
                return convertView;
            if (selectedItems.get(position))
                selectionSong.setImageResource(R.drawable.tick);
            else
                selectionSong.setImageResource(R.drawable.untick);
        }

        return convertView;
    }

    public ArrayList<Boolean> getSelectedItems() {
        return selectedItems;
    }

    public void setSelectedItems(ArrayList<Boolean> selectedItems) {
        this.selectedItems = selectedItems;
    }
}
