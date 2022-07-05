package com.example.musiktea;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.io.File;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;

public class FoldersAdapter extends ArrayAdapter<File> {
    private Bitmap folderImage;
    private ArrayList<Boolean> selectedItems;
    public FoldersAdapter(@NonNull Context context, @NonNull List<File> objects, ArrayList<Boolean> selectedItems) {
        super(context,0, objects);
        this.selectedItems = selectedItems;
    }

    private boolean isFileImage(String path) {
        String mimeType = URLConnection.guessContentTypeFromName(path);
        return mimeType != null && mimeType.startsWith("image");
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_folder, null);

        TextView tvTitle = convertView.findViewById(R.id.tvTitle);
        ImageView songImage = convertView.findViewById(R.id.songImage);
        ImageView selectionFolder = convertView.findViewById(R.id.selectionFolder);

        File file = getItem(position);
        tvTitle.setText(file.getName());

        if (file.isDirectory())
            songImage.setImageResource(R.drawable.inner_folder);
        else if (folderImage == null)
            songImage.setImageResource(R.drawable.foldertea);
        else
            songImage.setImageBitmap(folderImage);

        if (selectedItems != null) {
            if (selectedItems.size() == 0)
                return convertView;
            if (selectedItems.get(position))
                selectionFolder.setImageResource(R.drawable.tick);
            else
                selectionFolder.setImageResource(R.drawable.untick);
        }
        return convertView;
    }

    public Bitmap getFolderImage() {
        return folderImage;
    }

    public void setFolderImage(Bitmap folderImage) {
        this.folderImage = folderImage;
    }

    public ArrayList<Boolean> getSelectedItems() {
        return selectedItems;
    }

    public void setSelectedItems(ArrayList<Boolean> selectedItems) {
        this.selectedItems = selectedItems;
    }
}
