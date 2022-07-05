package com.example.musiktea;

import android.content.Context;
import android.view.DragEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.List;

public class PlayingQueueAdapter extends ArrayAdapter<Song> {
    public PlayingQueueAdapter(@NonNull Context context, @NonNull List<Song> objects) {
        super(context, 0, objects);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_song_of_queue, null);

        TextView tvSong = convertView.findViewById(R.id.tvSong);
        TextView tvArtist = convertView.findViewById(R.id.tvArtist);

        ImageView remover = convertView.findViewById(R.id.remover);
        ImageView lifter = convertView.findViewById(R.id.lifter);
        ImageView dropper = convertView.findViewById(R.id.dropper);

        remover.setOnClickListener(view -> {
            if (Singleton.getInstance().getSongs().size() == 1) {
                Toast.makeText(getContext(), "Playing queue must contain at least one song", Toast.LENGTH_SHORT).show();
                return;
            }
            if (Singleton.getInstance().getSongs().get(position).getPath().equals(Singleton.getInstance().getCurrentSong().getPath())) {
                try {
                    if (Singleton.getInstance().getMediaPlayer().isPlaying())
                        Singleton.getInstance().toggleNotificationSong();
                    Singleton.getInstance().setCurrentSong(Singleton.getInstance().getSongs().get(position - 1));
                    Singleton.getInstance().getSongs().remove(position);
                    Toast.makeText(getContext(), "Song Removed", Toast.LENGTH_SHORT).show();
                }
                catch (Exception e) {
                    try {
                        Singleton.getInstance().setCurrentSong(Singleton.getInstance().getSongs().get(position + 1));
                        Singleton.getInstance().getSongs().remove(position);
                        Toast.makeText(getContext(), "Song Removed", Toast.LENGTH_SHORT).show();
                    }
                    catch (Exception e2) {
                        Toast.makeText(getContext(), "Playing queue must contain at least one song", Toast.LENGTH_SHORT).show();
                    }
                }
                TextView tvName = Singleton.getInstance().getOuterActivity().findViewById(R.id.outerSongName);
                tvName.setText(Singleton.getInstance().getCurrentSong().getTitle());

                TextView tvA = Singleton.getInstance().getOuterActivity().findViewById(R.id.outerArtistName);
                tvA.setText(Singleton.getInstance().getCurrentSong().getArtist());
                Singleton.getInstance().playThisSong(Singleton.getInstance().getCurrentSong());
                Singleton.getInstance().getMediaPlayer().pause();
            }
            else {
                try {
                    Singleton.getInstance().setCurrentSong(Singleton.getInstance().getSongs().get(position - 1));
                    Singleton.getInstance().getSongs().remove(position);
                    Toast.makeText(getContext(), "Song Removed", Toast.LENGTH_SHORT).show();
                }
                catch (Exception e) {
                    try {
                        Singleton.getInstance().setCurrentSong(Singleton.getInstance().getSongs().get(position + 1));
                        Singleton.getInstance().getSongs().remove(position);
                        Toast.makeText(getContext(), "Song Removed", Toast.LENGTH_SHORT).show();
                    }
                    catch (Exception e1) {
                        Toast.makeText(getContext(), "Playing queue must contain at least one song", Toast.LENGTH_SHORT).show();
                    }
                }
            }
            notifyDataSetChanged();
            Singleton.getInstance().updateSongSource(Singleton.getInstance().getCurrentPaths());
            Singleton.getInstance().updateSavedSettings("Queue");
        });
        lifter.setOnClickListener(view -> {
            if (position != 0) {
                Song song = Singleton.getInstance().getSongs().get(position);
                Singleton.getInstance().getSongs().set(position, Singleton.getInstance().getSongs().get(position - 1));
                Singleton.getInstance().getSongs().set(position - 1, song);
                notifyDataSetChanged();
                Toast.makeText(getContext(), "Song moved UP", Toast.LENGTH_SHORT).show();

                Singleton.getInstance().updateSavedSettings("Queue");
                Singleton.getInstance().updateSongSource(Singleton.getInstance().getCurrentPaths());
            }
        });
        dropper.setOnClickListener(view -> {
            if (position != Singleton.getInstance().getSongs().size() - 1) {
                Song song = Singleton.getInstance().getSongs().get(position);
                Singleton.getInstance().getSongs().set(position, Singleton.getInstance().getSongs().get(position + 1));
                Singleton.getInstance().getSongs().set(position + 1, song);
                notifyDataSetChanged();
                Toast.makeText(getContext(), "Song moved DOWN", Toast.LENGTH_SHORT).show();

                Singleton.getInstance().updateSavedSettings("Queue");
                Singleton.getInstance().updateSongSource(Singleton.getInstance().getCurrentPaths());
            }
        });

        tvSong.setText(getItem(position).getTitle());
        tvArtist.setText(getItem(position).getArtist());

        return convertView;
    }
}