package com.teamnexters.tonight;

import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.CountDownTimer;
import android.os.Environment;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;


public class RecordActivity extends ActionBarActivity {

    private MediaPlayer player;
    private MediaRecorder recorder;
    private String OUTPUT_FILE;
    private TextView text;
    private recordTimer recordTimer = new recordTimer(180000, 1000);


    public class recordTimer extends CountDownTimer {
        private long timeRemain;
        public recordTimer(long startTime, long interval){
            super(startTime, interval);
            timeRemain = 0;
        }

        @Override
        public void onTick(long millisUntilFinished) {
            text.setText("Seconds Remaining: " + millisUntilFinished / 1000 );
            timeRemain = millisUntilFinished / 1000 ;
        }

        @Override
        public void onFinish() {
            text.setText("Record Done");
        }

        public long getTimeRemain() {
            return timeRemain;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record);
        text = (TextView) this.findViewById(R.id.timer);

        OUTPUT_FILE = Environment.getExternalStorageDirectory() + "/audiorecorder.wav";
    }

    @Override
    public void onPause() {
        super.onPause();
        stopRecording();
        stopPlaying();
        recordTimer.cancel();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    public void buttonTapped(View view) {

        switch (view.getId()) {
            case R.id.startBttn:
                try {
                    startRecording();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            case R.id.stopBttn:
                try {
                    stopRecording();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            case R.id.playBttn:
                try {
                    playRecording();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            case R.id.stopPlay:
                try {
                    stopPlaying();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
        }

    }

    private void startRecording() throws IOException {

        ditchMediaRecorder();
        File outFile = new File(OUTPUT_FILE);

        if (outFile.exists())
            outFile.delete();

        recorder = new MediaRecorder();
        recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        recorder.setOutputFormat(MediaRecorder.OutputFormat.DEFAULT);
        recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
        recorder.setOutputFile(OUTPUT_FILE);
        recorder.prepare();
        recorder.start();
        recordTimer.start();

    }

    private void ditchMediaRecorder() {
        if (recorder != null)
            recorder.release();
    }

    private void stopRecording() {

        if (recorder != null) {
            if (recordTimer.getTimeRemain() > 165) {
                Toast.makeText(getApplicationContext(), "Min 15 sec", Toast.LENGTH_SHORT).show();
                recorder.stop();
                recordTimer.cancel();
            } else {
                Toast.makeText(getApplicationContext(), "Record Done" , Toast.LENGTH_SHORT).show();
                recorder.stop();
                recordTimer.cancel();
            }
        }
    }

    private void playRecording() throws IOException {

        ditchMediaPlayer();
        player = new MediaPlayer();
        player.setDataSource(OUTPUT_FILE);
        player.prepare();
        player.start();
    }

    private void ditchMediaPlayer() {
        if (player != null){
            try {
                player.release();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void stopPlaying() {
        if (player != null && player.isPlaying()) {
            player.stop();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_record, menu);
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
}
