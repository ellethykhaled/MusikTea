package com.example.musiktea;


import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.PowerManager;
import android.provider.MediaStore;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.net.URI;

public class MusicPlayer extends AppCompatActivity implements View.OnClickListener {

    //Views Declarations
    private int Mode = 0;

    TextView tvTime, tvDuration, tvTitle, tvArtist;
    SeekBar seekBarTime;
    ImageView btnPlay, btnNext, btnBack, btnRepeat, btnShuffle, songImage;

    Singleton singleton = Singleton.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= 16) {
            ActionBar actionBar = getSupportActionBar();
            actionBar.hide();
        }
        Song song = (Song) getIntent().getSerializableExtra("song");

        if (song != null)
            mainThread(song);
        else {
            Uri songUri = getIntent().getData();
            String path = songUri.getPath();
            File file = new File(path);
            String title = file.getName();

            song = new Song(title, null, path, 0, null, null);
            MediaPlayer mediaPlayer = new MediaPlayer();
            try {
                mediaPlayer.setDataSource(path);
                mediaPlayer.prepare();
                mediaPlayer.start();
            } catch (IOException e) {
                e.printStackTrace();
            }
            Mode = 1;
        }
    }

    private void mainThread(Song song) {
        setContentView(R.layout.musicplayer);

        tvTime = findViewById(R.id.tvTime);
        tvDuration = findViewById(R.id.tvDuration);
        tvTitle = findViewById(R.id.tvTitle);
        tvArtist = findViewById(R.id.tvArtist);

        tvTitle.setText(song.getTitle());
        setArtist(song);

        seekBarTime = findViewById(R.id.seekBarTime);
        btnPlay = findViewById(R.id.btnPlay);
        btnNext = findViewById(R.id.forward);
        btnBack = findViewById(R.id.backward);
        btnRepeat = findViewById(R.id.repeat);
        btnShuffle = findViewById(R.id.shuffle);
        songImage = findViewById(R.id.songImage);

        if (Singleton.getInstance().isShuffle())
            btnShuffle.setImageResource(R.drawable.shuffleon);
        else
            btnShuffle.setImageResource(R.drawable.shuffle);

        publicFn(song);

        String duration = millisecondsToString(Singleton.getInstance().getMediaPlayer().getDuration());
        tvDuration.setText(duration);

        //Setting Clickers

        btnPlay.setOnClickListener(this);
        btnRepeat.setOnClickListener(this);
        btnBack.setOnClickListener(this);
        btnNext.setOnClickListener(this);
        btnShuffle.setOnClickListener(this);

        seekBarTime.setMax(Singleton.getInstance().getMediaPlayer().getDuration());
        seekBarTime.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean isFromUser) {
                if (isFromUser) {
                    Singleton.getInstance().getMediaPlayer().seekTo(progress);
                    seekBar.setProgress(progress);
                    tvTime.setText(millisecondsToString(progress));
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });

        new Thread(() -> {
            while (Singleton.getInstance().getMediaPlayer() != null) {
                if (Singleton.getInstance().getMediaPlayer().isPlaying()) {

                    try {
                        final double current = Singleton.getInstance().getMediaPlayer().getCurrentPosition();
                        final String elapsedTime = millisecondsToString((int) current);

                        runOnUiThread(() -> {
                            tvTime.setText(elapsedTime);
                            seekBarTime.setProgress((int) current);
                            if (Singleton.getInstance().updateMusicPlayerUIinner2) {
                                Singleton.getInstance().setUpdateMusicPlayerUIinner2(false);
                                tvTitle.setText(Singleton.getInstance().getCurrentSong().getTitle());
                                setArtist(Singleton.getInstance().getCurrentSong());
                                tvDuration.setText(millisecondsToString(Singleton.getInstance().getMediaPlayer().getDuration()));
                                seekBarTime.setMax(Singleton.getInstance().getMediaPlayer().getDuration());
                            }
                        });

                        Thread.sleep(500);
                    } catch (InterruptedException ignored) {
                    }
                } else if (Singleton.getInstance().updateMusicPlayerUIinner) {
                    try {
                        btnPlay.setImageResource(R.drawable.playbutton);
                        if ((float) Singleton.getInstance().getMediaPlayer().getCurrentPosition() / Singleton.getInstance().getMediaPlayer().getDuration() >= 0.99) {
                            seekBarTime.setProgress(0);
                            tvTime.setText(millisecondsToString(0));
                        }
                    } catch (Exception e) {
                    }
                }
                runOnUiThread(() -> {
                    if (Singleton.getInstance().isUpdateMusicPlayerUIinner() && !Singleton.getInstance().getMediaPlayer().isPlaying()) {
                        btnPlay.setImageResource(R.drawable.playbutton);
                        Singleton.getInstance().setUpdateMusicPlayerUIinner(false);
                    } else if (Singleton.getInstance().isUpdateMusicPlayerUIinner()) {
                        btnPlay.setImageResource(R.drawable.pause);
                        Singleton.getInstance().setUpdateMusicPlayerUIinner(false);
                    }
                });
            }
        }).start();

        Intent intent = new Intent();
        intent.putExtra("song", song);
        if (Singleton.getInstance().getMediaPlayer().isPlaying()) {
            btnPlay.setImageResource(R.drawable.pause);
            setResult(1, intent);
        } else {
            btnPlay.setImageResource(R.drawable.playbutton);
            setResult(0, intent);
        }
        int repeatState = Singleton.getInstance().getRepeatState();
        if (repeatState == 1)
            btnRepeat.setImageResource(R.drawable.repeat_all);
        else if (repeatState == 2)
            btnRepeat.setImageResource(R.drawable.repeat_one);

        if (Singleton.getInstance().getPlayingSongBitmap() != null)
            songImage.setImageBitmap(Singleton.getInstance().getPlayingSongBitmap());
        else
            songImage.setImageResource(R.drawable.playtea);

        double current = Singleton.getInstance().getMediaPlayer().getCurrentPosition();
        String elapsedTime = millisecondsToString((int) current);

        tvTime.setText(elapsedTime);
        seekBarTime.setProgress((int) current);

        Singleton.getInstance().setInnerActivity(this);
    }

    public String millisecondsToString(int time) {
        String elapsedTime;
        int minutes = time / 1000 / 60;
        int seconds = time / 1000 % 60;
        elapsedTime = minutes + ":";
        if (seconds < 10)
            elapsedTime += "0";
        elapsedTime += seconds;

        return elapsedTime;
    }

    public void publicFn(Song song) {
        if (Singleton.getInstance().getMediaPlayer() == null) {
            Singleton.getInstance().setMediaPlayer(new MediaPlayer());
            try {
                Singleton.getInstance().getMediaPlayer().setDataSource(song.getPath());
                Singleton.getInstance().getMediaPlayer().prepare();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else if (!Singleton.getInstance().getCurrentSong().getPath().equals(song.getPath())) {
            Singleton.getInstance().getMediaPlayer().stop();
            Singleton.getInstance().getMediaPlayer().reset();
            Singleton.getInstance().setMediaPlayer(new MediaPlayer());
            try {
                Singleton.getInstance().getMediaPlayer().setDataSource(song.getPath());
                Singleton.getInstance().getMediaPlayer().prepare();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        Singleton.getInstance().getMediaPlayer().setVolume(1.0f, 1.0f);
        if (!Singleton.getInstance().getCurrentSong().getPath().equals(song.getPath())) {
            Singleton.getInstance().playThisSong(song);
        } else {
            Singleton.getInstance().getMediaPlayer().setOnCompletionListener(mediaPlayer -> {
                if (Singleton.getInstance().getCurrentSong().getID() == Singleton.getInstance().getSongs().size() - 1 && Singleton.getInstance().getRepeatState() == 0) {
                } else {
                    Singleton.getInstance().playNextSong(null);
                    TextView tvTitle = Singleton.getInstance().getOuterActivity().findViewById(R.id.outerSongName);
                    tvTitle.setText(Singleton.getInstance().getCurrentSong().getTitle());
                    TextView tvArtist = Singleton.getInstance().getOuterActivity().findViewById(R.id.outerArtistName);
                    tvArtist.setText(Singleton.getInstance().getCurrentSong().getArtist());
                    mediaPlayer.setWakeMode(Singleton.getInstance().getOuterActivity().getApplicationContext(), PowerManager.PARTIAL_WAKE_LOCK);
                }
                Singleton.getInstance().updateMusicPlayerUIouter = true;
                Singleton.getInstance().updateMusicPlayerUIinner = true;
                Singleton.getInstance().updateMusicPlayerUIinner2 = true;
            });
        }
        Singleton.getInstance().setCurrentSong(song);
    }

    @Override
    public void onClick(View view) {
        Intent intent = new Intent();
        if (view.getId() == R.id.btnPlay)   //Play Button
        {
            if (Singleton.getInstance().getMediaPlayer().isPlaying()) {
                Singleton.getInstance().getMediaPlayer().pause();
                btnPlay.setImageResource(R.drawable.playbutton);
                intent.putExtra("song", Singleton.getInstance().getCurrentSong());
                setResult(0, intent);
            } else {
                if (singleton.getMediaPlayer() != null) {
                    if (singleton.getMediaPlayer().isPlaying()) {
                        singleton.getMediaPlayer().pause();
                        btnPlay.setImageResource(R.drawable.playbutton);
                        intent.putExtra("song", Singleton.getInstance().getCurrentSong());
                        setResult(0, intent);
                    }
                }
                char t1 = tvDuration.getText().charAt(tvDuration.getText().length() - 1);
                char t2 = tvTime.getText().charAt(tvTime.getText().length() - 1);
                if (Singleton.getInstance().getCurrentSong().getID() == Singleton.getInstance().getSongs().size() - 1 && (t1 == t2 || t1 - 1 == t2)) {
                    playSong(Singleton.getInstance().getCurrentSong());
                }
                btnPlay.setImageResource(R.drawable.pause);
                intent.putExtra("song", Singleton.getInstance().getCurrentSong());
                setResult(1, intent);
                Singleton.getInstance().getMediaPlayer().start();
            }
        }

        if (view.getId() == R.id.repeat)        //Repeat Button
        {
            if (Singleton.getInstance().getRepeatState() == 0) {
                btnRepeat.setImageResource(R.drawable.repeat_all);
                Singleton.getInstance().setRepeatState(1);
                Singleton.getInstance().getMediaPlayer().setLooping(false);
                Toast.makeText(getBaseContext(), "Repeat All", Toast.LENGTH_SHORT).show();
            } else if (Singleton.getInstance().getRepeatState() == 1) {
                btnRepeat.setImageResource(R.drawable.repeat_one);
                Singleton.getInstance().setRepeatState(2);
                Singleton.getInstance().getMediaPlayer().setLooping(true);
                Toast.makeText(getBaseContext(), "Repeat One", Toast.LENGTH_SHORT).show();
            } else {
                btnRepeat.setImageResource(R.drawable.repeat);
                Singleton.getInstance().setRepeatState(0);
                Singleton.getInstance().getMediaPlayer().setLooping(false);
                Toast.makeText(getBaseContext(), "Repeat Off", Toast.LENGTH_SHORT).show();
            }
        }

        if (view.getId() == R.id.backward)      //Backward Button
        {
            btnPlay.setImageResource(R.drawable.pause);
            intent.putExtra("song", Singleton.getInstance().getCurrentSong());
            setResult(1, intent);
            if (Singleton.getInstance().getMediaPlayer().getCurrentPosition() > 4000 && Singleton.getInstance().getMediaPlayer().isPlaying()) {
                String duration = millisecondsToString(Singleton.getInstance().getMediaPlayer().getDuration());
                tvDuration.setText(duration);
            }
            Singleton.getInstance().playPreviousSong();
            tvDuration.setText(millisecondsToString(Singleton.getInstance().getMediaPlayer().getDuration()));
            tvTitle.setText(Singleton.getInstance().getCurrentSong().getTitle());
            setArtist(Singleton.getInstance().getCurrentSong());
            seekBarTime.setMax(Singleton.getInstance().getMediaPlayer().getDuration());
        }

        if (view.getId() == R.id.forward)       //Forward Button
            playNext(view);

        if (view.getId() == R.id.shuffle)       //Shuffle Button
        {
            if (Singleton.getInstance().isShuffle()) {
                btnShuffle.setImageResource(R.drawable.shuffle);
                Singleton.getInstance().setShuffle(false);
                Singleton.getInstance().clearShuffledSongs(Singleton.getInstance().getCurrentSong());
                Toast.makeText(getBaseContext(), "Shuffle Off", Toast.LENGTH_SHORT).show();
            } else {
                btnShuffle.setImageResource(R.drawable.shuffleon);
                Singleton.getInstance().setShuffle(true);
                Singleton.getInstance().setCurrentSong(Singleton.getInstance().getCurrentSong());
                Toast.makeText(getBaseContext(), "Shuffle On", Toast.LENGTH_SHORT).show();
            }
        }

    }

    private void setArtist(Song song) {
        if (song.getArtist().equals("<unknown>"))
            tvArtist.setText("");
        else
            tvArtist.setText(song.getArtist());
    }

    private void playSong(Song song) {
        Intent intent = new Intent();
        if (song == null) {
            btnPlay.setImageResource(R.drawable.playbutton);
            intent.putExtra("song", Singleton.getInstance().getCurrentSong());
            setResult(0, intent);
            return;
        }
        btnPlay.setImageResource(R.drawable.pause);
        intent.putExtra("song", song);
        setResult(1, intent);

        Singleton.getInstance().playThisSong(song);
        Singleton.getInstance().toggleNotificationSong();
        tvDuration.setText(millisecondsToString(Singleton.getInstance().getMediaPlayer().getDuration()));
        tvTitle.setText(Singleton.getInstance().getCurrentSong().getTitle());
        setArtist(Singleton.getInstance().getCurrentSong());
        seekBarTime.setMax(Singleton.getInstance().getMediaPlayer().getDuration());

    }

    public void playNext(View view) {
        seekBarTime.setProgress(0);
        tvTime.setText("0:00");

        Singleton.getInstance().playNextSong(view);
        tvDuration.setText(millisecondsToString(Singleton.getInstance().getMediaPlayer().getDuration()));
        tvTitle.setText(Singleton.getInstance().getCurrentSong().getTitle());
        setArtist(Singleton.getInstance().getCurrentSong());
        btnPlay.setImageResource(R.drawable.pause);
        seekBarTime.setMax(Singleton.getInstance().getMediaPlayer().getDuration());

    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.home) {
            finish();
            if (Singleton.getInstance().getMediaPlayer().isPlaying()) {
                Singleton.getInstance().getMediaPlayer().stop();
                Singleton.getInstance().getMediaPlayer().reset();
            }
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (Mode == 0) {
            if (Singleton.getInstance().getMediaPlayer() != null) {
                if (Singleton.getInstance().getMediaPlayer().isPlaying())
                    btnPlay.setImageResource(R.drawable.pause);
                else
                    btnPlay.setImageResource(R.drawable.playbutton);
            } else
                btnPlay.setImageResource(R.drawable.playbutton);

            tvTitle.setText(Singleton.getInstance().getCurrentSong().getTitle());
            setArtist(Singleton.getInstance().getCurrentSong());
            tvDuration.setText(millisecondsToString(Singleton.getInstance().getMediaPlayer().getDuration()));
            seekBarTime.setMax(Singleton.getInstance().getMediaPlayer().getDuration());
        }
    }

    @Override
    protected void onPause() {
        Singleton.getInstance().updateSavedSettings(null);
        super.onPause();
    }
}