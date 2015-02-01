package com.teamnexters.tonight;

import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Environment;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import java.io.File;
import java.io.IOException;


public class RecordActivity extends ActionBarActivity {

    private MediaPlayer mPlayer;
    private MediaRecorder recorder;
    private String OUTPUT_FILE;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record);


        OUTPUT_FILE = Environment.getExternalStorageDirectory()+"/audiorecorder.3ggp";
    }

    public void buttonTapped(View view){

        switch (view.getId()) {
            case R.id.startBttn:
                try{
                    startRecording();
                } catch (Exception e){
                    e.printStackTrace();
                }
                break;
            case R.id.stopBttn:
                try{
                    stopRecording();
                }catch (Exception e){
                    e.printStackTrace();
                }
                break;
            case R.id.playBttn:
                try{
                    playRecording();
                } catch (Exception e){
                    e.printStackTrace();
                }
                break;
        }

    }



    private void startRecording() throws IOException {

        ditchMediaRecorder();
        File outFile = new File(OUTPUT_FILE);

        if(outFile.exists())
            outFile.delete();

        recorder = new MediaRecorder();
        recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        recorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
        recorder.setOutputFile(OUTPUT_FILE);

        recorder.prepare();
        recorder.start();
    }

    private void ditchMediaRecorder() {
        if(recorder != null)
            recorder.release();
    }

    private void stopRecording() {

        if(recorder != null)
            recorder.stop();
    }

    private void playRecording() throws IOException {

        ditchMediaPlayer();
        mPlayer = new MediaPlayer();
        mPlayer.setDataSource(OUTPUT_FILE);
        mPlayer.prepare();
        mPlayer.start();
    }

    private void ditchMediaPlayer() {
        if(mPlayer != null){
            try{
                mPlayer.release();
            } catch (Exception e){
                e.printStackTrace();
            }
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
