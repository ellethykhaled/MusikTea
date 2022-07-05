package com.example.musiktea;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.io.File;
import java.util.ArrayList;

public class SearchAdapter extends BaseAdapter {
    Activity activity;
    ArrayList<SearchItem> searchItems;

    public SearchAdapter(Activity activity, ArrayList<SearchItem> searchItems) {
        this.activity = activity;
        this.searchItems = searchItems;
    }

    @Override
    public int getItemViewType(int position) {
        return searchItems.get(position).getType();
    }

    @Override
    public int getCount() {
        return searchItems.size();
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(final int position, View convertView, final ViewGroup arg2) {

        LayoutInflater inflater = activity.getLayoutInflater();
        Object listObject = searchItems.get(position).getObject();
        ViewHolderItemName holder;
        ViewHolderHeader headerHolder = null;

        switch (getItemViewType(position)) {
            case SearchItem.SONGTITLE:
                if (headerHolder == null) {
                    headerHolder = new ViewHolderHeader();
                    convertView = inflater.inflate(R.layout.search_category, null);
                    headerHolder.itemHeaderName = (TextView) convertView.findViewById(R.id.tvCategoryTitle);
                    convertView.setTag(R.id.HeaderHolder_Tag, headerHolder);
                }
                headerHolder = (ViewHolderHeader) convertView.getTag(R.id.HeaderHolder_Tag);
                headerHolder.itemHeaderName.setText("Songs");
                convertView.setOnClickListener(null);
                return convertView;
            case SearchItem.SONG:
                SearchItemObject songitemObject = new SearchItemObject((Song) listObject);
                if (headerHolder == null) {
                    holder = new ViewHolderItemName();
                    convertView = inflater.inflate(R.layout.item_song, null);
                    holder.itemNameView = (TextView) convertView.findViewById(R.id.tvTitle);
                    holder.itemSubNameView = (TextView) convertView.findViewById(R.id.tvArtist);
                    convertView.setTag(R.id.Holder_Tag, holder);
                }
                holder = (ViewHolderItemName) convertView.getTag(R.id.Holder_Tag);

                try {
                    holder.itemSubNameView.setText(songitemObject.getSubName());
                    holder.itemNameView.setText(songitemObject.getItemName());
                } catch (Exception e) {
                }
                return convertView;

            case SearchItem.ALBUMTITLE:
                if (headerHolder == null) {
                    headerHolder = new ViewHolderHeader();
                    convertView = inflater.inflate(R.layout.search_category, null);
                    headerHolder.itemHeaderName = (TextView) convertView.findViewById(R.id.tvCategoryTitle);
                    convertView.setTag(R.id.HeaderHolder_Tag, headerHolder);
                }
                headerHolder = (ViewHolderHeader) convertView.getTag(R.id.HeaderHolder_Tag);
                headerHolder.itemHeaderName.setText("Albums");
                convertView.setOnClickListener(null);
                return convertView;

            case SearchItem.ALBUM:
                SearchItemObject albumitemObject = new SearchItemObject((Album) listObject);
                if (headerHolder == null) {
                    holder = new ViewHolderItemName();
                    convertView = inflater.inflate(R.layout.item_album, null);
                    holder.itemNameView = (TextView) convertView.findViewById(R.id.tvTitle);
                    convertView.setTag(R.id.Holder_Tag, holder);
                }
                holder = (ViewHolderItemName) convertView.getTag(R.id.Holder_Tag);

                try {
                    holder.itemNameView.setText(albumitemObject.getItemName());
                } catch (Exception e) {
                }
                return convertView;

            case SearchItem.ARTISTTITLE:
                if (headerHolder == null) {
                    headerHolder = new ViewHolderHeader();
                    convertView = inflater.inflate(R.layout.search_category, null);
                    headerHolder.itemHeaderName = (TextView) convertView.findViewById(R.id.tvCategoryTitle);
                    convertView.setTag(R.id.HeaderHolder_Tag, headerHolder);
                }
                headerHolder = (ViewHolderHeader) convertView.getTag(R.id.HeaderHolder_Tag);
                headerHolder.itemHeaderName.setText("Artists");
                convertView.setOnClickListener(null);
                return convertView;

            case SearchItem.ARTIST:
                SearchItemObject artistitemObject = new SearchItemObject((Artist) listObject);
                if (headerHolder == null) {
                    holder = new ViewHolderItemName();
                    convertView = inflater.inflate(R.layout.item_artist, null);
                    holder.itemNameView = (TextView) convertView.findViewById(R.id.tvTitle);
                    holder.itemSubNameView = (TextView) convertView.findViewById(R.id.trackCount);
                    convertView.setTag(R.id.Holder_Tag, holder);
                }
                holder = (ViewHolderItemName) convertView.getTag(R.id.Holder_Tag);

                try {
                    holder.itemNameView.setText(artistitemObject.getItemName());
                    holder.itemSubNameView.setText(artistitemObject.getSubName());
                } catch (Exception e) {
                }
                return convertView;

            case SearchItem.FOLDERTITLE:
                if (headerHolder == null) {
                    headerHolder = new ViewHolderHeader();
                    convertView = inflater.inflate(R.layout.search_category, null);
                    headerHolder.itemHeaderName = (TextView) convertView.findViewById(R.id.tvCategoryTitle);
                    convertView.setTag(R.id.HeaderHolder_Tag, headerHolder);
                }
                headerHolder = (ViewHolderHeader) convertView.getTag(R.id.HeaderHolder_Tag);
                headerHolder.itemHeaderName.setText("Folders");
                convertView.setOnClickListener(null);
                return convertView;

            case SearchItem.FOLDER:
                SearchItemObject folderitemObject = new SearchItemObject((String) listObject);
                if (headerHolder == null) {
                    holder = new ViewHolderItemName();
                    convertView = inflater.inflate(R.layout.item_folder, null);
                    holder.itemNameView = (TextView) convertView.findViewById(R.id.tvTitle);
                    convertView.setTag(R.id.Holder_Tag, holder);
                }
                holder = (ViewHolderItemName) convertView.getTag(R.id.Holder_Tag);
                try {
                    holder.itemNameView.setText(folderitemObject.getItemName());
                } catch (Exception e) {
                }
                return convertView;

            case SearchItem.PLAYLISTTITLE:
                if (headerHolder == null) {
                    headerHolder = new ViewHolderHeader();
                    convertView = inflater.inflate(R.layout.search_category, null);
                    headerHolder.itemHeaderName = (TextView) convertView.findViewById(R.id.tvCategoryTitle);
                    convertView.setTag(R.id.HeaderHolder_Tag, headerHolder);
                }
                headerHolder = (ViewHolderHeader) convertView.getTag(R.id.HeaderHolder_Tag);
                headerHolder.itemHeaderName.setText("Playlists");
                convertView.setOnClickListener(null);
                return convertView;

            case SearchItem.PLAYLIST:
                SearchItemObject playlistitemObject = new SearchItemObject((Playlist) listObject);
                if (headerHolder == null) {
                    holder = new ViewHolderItemName();
                    convertView = inflater.inflate(R.layout.item_playlist, null);
                    holder.itemNameView = (TextView) convertView.findViewById(R.id.tvTitle);
                    convertView.setTag(R.id.Holder_Tag, holder);
                }
                holder = (ViewHolderItemName) convertView.getTag(R.id.Holder_Tag);

                try {
                    holder.itemNameView.setText(playlistitemObject.getItemName());
                } catch (Exception e) {
                }
                return convertView;
            default:
                break;
        }
        return null;
    }

    private static class ViewHolderItemName {
        TextView itemNameView;
        TextView itemSubNameView;
    }

    private static class ViewHolderHeader {
        TextView itemHeaderName;
    }

}
