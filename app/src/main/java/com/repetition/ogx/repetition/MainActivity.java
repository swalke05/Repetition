package com.repetition.ogx.repetition;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.content.Intent;
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
    int paused;
    int startTime, endTime;

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

                if (mediaPlayer.getCurrentPosition() >= 40000){
                    mediaPlayer.seekTo(startTime);
                }
            }
        }

    };

    Thread loopMusic;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Toast.makeText(getApplicationContext(), "testerino", Toast.LENGTH_SHORT).show()


        //startActivityForResult(intent, 1);
        //showChooser();

;    }

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

    /*protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            Toast.makeText(getApplicationContext(), "OK", Toast.LENGTH_SHORT).show();
            fileUri = data.
        }
        else {
            Toast.makeText(getApplicationContext(), "I'm not OkayyyyYYyYyyy", Toast.LENGTH_SHORT).show();
        }
     }*/

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

            startTime = 30000;
            endTime = 40000;

            mediaPlayer.seekTo(startTime);
            //mediaPlayer.setLooping(true);
            mediaPlayer.start();
            loopMusic = new Thread(loopRunnable);

            loopMusic.start(); //Start the thread to loop

           /* Handler mHandler = new Handler();

            mHandler.postDelayed(new Runnable() {
                public void run() {
                    for (int i = 0; i > -1; i++) {
                        try {
                            Thread.sleep(500);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }

                        if (mediaPlayer.getCurrentPosition() >= 40000){
                            mediaPlayer.seekTo(startTime);
                        }
                    }
                }
            }, 1000);*/



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
    }

    public void stop(View view) {
        loopMusic.interrupt();
        mediaPlayer.release();
        mediaPlayer = null;
    }

    public void pause(View view){
        mediaPlayer.pause();

        paused = mediaPlayer.getCurrentPosition();
        Toast.makeText(getApplicationContext(), String.valueOf(mediaPlayer.getDuration()), Toast.LENGTH_LONG).show();
    }
}
