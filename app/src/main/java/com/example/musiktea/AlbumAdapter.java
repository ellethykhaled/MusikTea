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
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class AlbumAdapter extends ArrayAdapter<Album> {
    private ArrayList<Boolean> selectedItems;
    public AlbumAdapter(@NonNull Context context, @NonNull List<Album> objects, ArrayList<Boolean> selectedItems) {
        super(context, 0, objects);
        this.selectedItems = selectedItems;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_album, null);

        TextView tvTitle = convertView.findViewById(R.id.tvTitle);
        ImageView albumImage = convertView.findViewById(R.id.albumImage);
        ImageView selectionAlbum = convertView.findViewById(R.id.selectionAlbum);


        tvTitle.setText(getItem(position).getAlbumName());
        if (getItem(position).getAlbumImage() == null)
            albumImage.setImageResource(R.drawable.albumtea);
        else
            albumImage.setImageBitmap(getItem(position).getAlbumImage());

        if (selectedItems != null) {
            if (selectedItems.size() == 0)
                return convertView;
            if (selectedItems.get(position))
                selectionAlbum.setImageResource(R.drawable.tick);
            else
                selectionAlbum.setImageResource(R.drawable.untick);
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