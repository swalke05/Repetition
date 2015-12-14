package com.repetition.ogx.repetition;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.content.Intent;
import android.widget.SeekBar;
import android.widget.Toast;
import com.ipaulpro.afilechooser.utils.FileUtils;
import android.content.ActivityNotFoundException;
import android.net.Uri;
import android.media.MediaPlayer;
import android.media.AudioManager;
import android.view.View;
import android.util.Log;
import android.os.Handler;
import java.io.IOException;



public class MainActivity extends AppCompatActivity {


    private static final int REQUEST_CODE = 6384; // onActivityResult request code
    Uri fileUri;
    MediaPlayer mediaPlayer;
    SeekBar seekBar;
    Handler seekHandler = new Handler();

    int paused;
    int startTime = -1, endTime = -1;

    Runnable loopRunnable = new Runnable() {
        @Override
        public void run() {
            while (mediaPlayer != null) { //infinite loop

                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    return;
                }

                if (mediaPlayer.getCurrentPosition() >= endTime && endTime != -1) {
                    if (startTime != -1) {
                        mediaPlayer.seekTo(startTime);
                    }
                    else {
                        startTime = 0;
                    }
                }

            }
        }
    };

    Thread loopMusic;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        seekBar = (SeekBar) findViewById(R.id.seeker);
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (mediaPlayer != null && fromUser) {
                    mediaPlayer.seekTo(progress * 1000);
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void showChooser() {
        // Use the GET_CONTENT intent from the utility class
        Intent target = FileUtils.createGetContentIntent();
        // Create the chooser Intent
        Intent intent = Intent.createChooser(
                target, getString(R.string.chooser_title));
        try {
            startActivityForResult(intent, REQUEST_CODE);
        } catch (ActivityNotFoundException e) {
            // The reason for the existence of aFileChooser
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQUEST_CODE:
                // If the file selection was successful
                if (resultCode == RESULT_OK) {
                    if (data != null) {
                        // Get the URI of the selected file
                        fileUri = data.getData();

                        try {
                            // Get the file path from the URI
                            final String path = FileUtils.getPath(this, fileUri);
                            Log.d("URI", "Might not have set URI correctly" + fileUri.toString());
                        } catch (Exception e) {
                            Log.d("URI", "File select error" + fileUri.toString());
                        }
                    }
                }
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    public void play(View view) throws IOException {

        if (mediaPlayer == null) {
            mediaPlayer = new MediaPlayer();
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            Log.d("URI", "Might not have set URI correctly" + fileUri.toString());

            try {
                mediaPlayer.setDataSource(getApplicationContext(), fileUri);
            } catch (IllegalArgumentException e) {
                Log.d("URI", "Might not have set URI correctly" + fileUri.toString());
            } catch (SecurityException e) {
                Log.d("URI", "Might not have set URI correctly" + fileUri.toString());
            } catch (IllegalStateException e) {
                Log.d("URI", "Might not have set URI correctly" + fileUri.toString());
            } catch (IOException e) {
                e.printStackTrace();
            }

            try {
                mediaPlayer.prepare();
            } catch (IllegalStateException e) {
                Log.d("URI", "Might not have set URI correctly" + fileUri.toString());
            } catch (IOException e) {
                Log.d("URI", "Might not have set URI correctly" + fileUri.toString());
            }

            mediaPlayer.start();
            initializeSeekBar();
            loopMusic = new Thread(loopRunnable);

            loopMusic.start(); //Start the thread to loop
        }
        else {
            if (!mediaPlayer.isPlaying()) { //resume only if song is currently paused
                mediaPlayer.seekTo(paused);

                mediaPlayer.start();
            }
        }
    }

    public void openFile(View view) {
        showChooser();

        //initializeSeekBar();
    }

    public void stop(View view) {
        loopMusic.interrupt();
        mediaPlayer.release();
        mediaPlayer = null;
    }

    public void pause(View view){
        mediaPlayer.pause();

        paused = mediaPlayer.getCurrentPosition();
    }

    public void initializeSeekBar() {
        seekBar.setMax(mediaPlayer.getDuration() / 1000); //Set seekBar to song length



        MainActivity.this.runOnUiThread(new Runnable() {

            @Override
            public void run() {
                if (mediaPlayer != null) {
                    int currentPosition = mediaPlayer.getCurrentPosition() / 1000;
                    seekBar.setProgress(currentPosition);
                }
                seekHandler.postDelayed(this, 1000);
            }
        });
    }

    public void setStartTime(View view) {
        startTime = mediaPlayer.getCurrentPosition();
    }

    public void setEndTime(View view) {
        endTime = mediaPlayer.getCurrentPosition();
    }

    public void clearStartTime(View view) {
        startTime = -1;
    }
    public void clearEndTime(View view) {
        endTime = -1;
    }
}

