package com.teamnexters.tonight;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;

public class RecordFragment extends Fragment {

    private static final String ARG_PARAM = "param";
    private String mParam;

    private boolean isRecording = false;
    private String OUTPUT_FILE = Environment.getExternalStorageDirectory() + "/audiorecorder.wav";

    private MediaPlayer player;
    private MediaRecorder recorder;

    private TextView text;
    private Button btnStart;
    private Button btnPlay;
    private Button btnCancel;
    private Button btnUpload;

    private RecordTimer recordTimer = new RecordTimer(180000, 1000);


    public static RecordFragment newInstance(String param) {
        RecordFragment fragment = new RecordFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM, param);
        fragment.setArguments(args);
        return fragment;
    }

    public RecordFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam = getArguments().getString(ARG_PARAM);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_record, container, false);

        text = (TextView) view.findViewById(R.id.timer);
        btnStart = (Button) view.findViewById(R.id.btn_start);
        btnPlay = (Button) view.findViewById(R.id.btn_play);
        btnCancel = (Button) view.findViewById(R.id.cancel);
        btnUpload = (Button) view.findViewById(R.id.upload);

        btnStart.setOnClickListener(btnClickListener);
        btnPlay.setOnClickListener(btnClickListener);
        btnCancel.setOnClickListener(btnClickListener);
        btnUpload.setOnClickListener(btnClickListener);

        return view;
    }

    View.OnClickListener btnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.btn_start:
                    try {
                        if(!isRecording) {
                            startRecording();
                            btnPlay.setVisibility(View.INVISIBLE);
                            btnCancel.setVisibility(View.INVISIBLE);
                            btnUpload.setVisibility(View.INVISIBLE);

                        } else {
                            stopRecording();
                            btnPlay.setVisibility(View.VISIBLE);
                            btnCancel.setVisibility(View.VISIBLE);
                            btnUpload.setVisibility(View.VISIBLE);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;
                case R.id.btn_play:
                    try {
                        if(player != null && player.isPlaying()){
                            stopPlaying();
                        } else {
                            playRecording();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;

                case R.id.cancel:
                    try {
                        dialog_Cancel(getActivity());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;
                case R.id.upload:
                    try {
                        dialog_Upload(getActivity());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

            }
        }
    };

    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }

    @Override
    public void onDetach() {
        super.onDetach();
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

        isRecording = true;
    }

    private void ditchMediaRecorder() {
        if (recorder != null)
            recorder.release();
    }

    public void dialog_Cancel(Context ctx) {

        DialogInterface.OnClickListener cancelListener = new DialogInterface.OnClickListener(){

            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case DialogInterface.BUTTON_POSITIVE:
                        Toast.makeText(getActivity().getApplicationContext(),"File Deleted",Toast.LENGTH_SHORT).show();
                        break;

                    case DialogInterface.BUTTON_NEGATIVE:
                        Toast.makeText(getActivity().getApplicationContext(),"File Saved", Toast.LENGTH_SHORT).show();
                        break;
                }
            }
        };
        AlertDialog.Builder cancelDialog = new AlertDialog.Builder(ctx);
        cancelDialog.setMessage("Do you want to delete the record?").setPositiveButton("Yes", cancelListener).setNegativeButton("No", cancelListener).show();

    }

    public void dialog_Upload(Context ctx) {
        DialogInterface.OnClickListener uploadListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case DialogInterface.BUTTON_POSITIVE:
                        Toast.makeText(getActivity().getApplicationContext(), "File Uploaded", Toast.LENGTH_SHORT).show();
                        break;
                    case DialogInterface.BUTTON_NEGATIVE:
                        Toast.makeText(getActivity().getApplicationContext(), "Upload Cancel", Toast.LENGTH_SHORT).show();
                        break;
                }
            }
        };

        AlertDialog.Builder uploadDialog = new AlertDialog.Builder(ctx);
        uploadDialog.setMessage("Do you want to upload the record?").setPositiveButton("Upload", uploadListener).setNegativeButton("No", uploadListener).show();

    }
    private void stopRecording() {
        if (recorder != null && isRecording) {
            if (recordTimer.getTimeRemain() > 165) {
                Toast.makeText(getActivity().getApplicationContext(), "Min 15 sec", Toast.LENGTH_SHORT).show();
                recorder.stop();

                recordTimer.cancel();
                isRecording = false;
            } else {
                Toast.makeText(getActivity().getApplicationContext(), "Record Done" , Toast.LENGTH_SHORT).show();
                recorder.stop();

                recordTimer.cancel();
                isRecording = false;
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
        if (player != null) {
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

    public class RecordTimer extends CountDownTimer {
        private long timeRemain;

        public RecordTimer(long startTime, long interval) {
            super(startTime, interval);
            timeRemain = 0;
        }

        @Override
        public void onTick(long millisUntilFinished) {
            text.setText("Seconds Remaining: " + millisUntilFinished / 1000);
            timeRemain = millisUntilFinished / 1000;
        }

        @Override
        public void onFinish() {
            text.setText("Record Done");
        }

        public long getTimeRemain() {
            return timeRemain;
        }
    }
}
