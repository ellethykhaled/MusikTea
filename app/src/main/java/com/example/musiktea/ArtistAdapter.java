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

public class ArtistAdapter extends ArrayAdapter<Artist> {
    private ArrayList<Boolean> selectedItems;
    public ArtistAdapter(@NonNull Context context, @NonNull List<Artist> objects, ArrayList<Boolean> selectedItems) {
        super(context, 0, objects);
        this.selectedItems = selectedItems;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_artist, null);

        TextView tvTitle = convertView.findViewById(R.id.tvTitle);
        TextView trackCount = convertView.findViewById(R.id.trackCount);
        ImageView artistImage = convertView.findViewById(R.id.artistImage);
        ImageView selectionArtist = convertView.findViewById(R.id.selectionArtist);


        tvTitle.setText(getItem(position).getArtistName());
        if (getItem(position).getTrackCount() == 1)
            trackCount.setText(String.valueOf(getItem(position).getTrackCount()).concat(" Track"));
        else
            trackCount.setText(String.valueOf(getItem(position).getTrackCount()).concat(" Tracks"));

        switch (position % 4) {
            case 0:
                artistImage.setImageResource(R.drawable.artist1);
                break;
            case 1:
                artistImage.setImageResource(R.drawable.artist3);
                break;
            case 2:
                artistImage.setImageResource(R.drawable.artist2);
                break;
            case 3:
                artistImage.setImageResource(R.drawable.artist4);
                break;
            default:
                artistImage.setImageResource(R.drawable.artisttea);
                break;
        }

        if (selectedItems != null) {
            if (selectedItems.size() == 0)
                return convertView;
            if (selectedItems.get(position))
                selectionArtist.setImageResource(R.drawable.tick);
            else
                selectionArtist.setImageResource(R.drawable.untick);
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