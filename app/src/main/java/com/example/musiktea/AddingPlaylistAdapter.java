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

public class AddingPlaylistAdapter extends ArrayAdapter<String> {
  private ArrayList<Boolean> selectedItems;
  public AddingPlaylistAdapter(@NonNull Context context, @NonNull List<String> objects, ArrayList<Boolean> selectedItems) {
    super(context, 0, objects);
    this.selectedItems = selectedItems;
  }

  public void setSelectedItems(ArrayList<Boolean> selectedItems) {
    this.selectedItems = selectedItems;
  }

  @NonNull
  @Override
  public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
    convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_add_to_playlist, null);

    TextView tvTitle = convertView.findViewById(R.id.tvTitle);
    ImageView selectionPlaylist = convertView.findViewById(R.id.playlistImage);

    tvTitle.setText(getItem(position));


    if (selectedItems != null) {
      if (selectedItems.get(position))
        selectionPlaylist.setImageResource(R.drawable.tick);
      else
        selectionPlaylist.setImageResource(R.drawable.untick);
    }

    return convertView;
  }

}