package com.example.musiktea;import android.content.Context;import android.view.LayoutInflater;import android.view.View;import android.view.ViewGroup;import android.widget.ArrayAdapter;import android.widget.ImageView;import android.widget.TextView;import androidx.annotation.NonNull;import androidx.annotation.Nullable;import java.util.ArrayList;import java.util.List;public class PlaylistAdapter extends ArrayAdapter<Playlist> {    private ArrayList<Boolean> selectedItems;    public PlaylistAdapter(@NonNull Context context, @NonNull List<Playlist> objects, ArrayList<Boolean> selectedItems) {        super(context, 0, objects);        this.selectedItems = selectedItems;    }    @NonNull    @Override    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {        convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_playlist, null);        TextView tvTitle = convertView.findViewById(R.id.tvTitle);        ImageView playlistImage = convertView.findViewById(R.id.playlistImage);        ImageView selectionPlaylist = convertView.findViewById(R.id.selectionPlaylist);        if (position == 0) {            tvTitle.setText("Create New Playlist");            playlistImage.setImageResource(R.drawable.addplaylist);        }        else {            tvTitle.setText(getItem(position).getPlaylistName());            if (getItem(position).getPlaylistImage() == null)                playlistImage.setImageResource(R.drawable.playlisttea);            else {                playlistImage.setImageBitmap(getItem(position).getPlaylistImage());            }        }        if (selectedItems != null  && position != 0) {            if (selectedItems.size() == 0)                return convertView;            if (selectedItems.get(position - 1))                selectionPlaylist.setImageResource(R.drawable.tick);            else                selectionPlaylist.setImageResource(R.drawable.untick);        }        return convertView;    }    public ArrayList<Boolean> getSelectedItems() {        return selectedItems;    }    public void setSelectedItems(ArrayList<Boolean> selectedItems) {        this.selectedItems = selectedItems;    }}