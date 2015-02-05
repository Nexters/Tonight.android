package com.teamnexters.tonight;

import android.app.Activity;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Environment;
import android.support.v4.app.Fragment;
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
    private Button btnStopPlay;

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
        btnStopPlay = (Button) view.findViewById(R.id.btn_stop_play);

        btnStart.setOnClickListener(btnClickListener);
        btnPlay.setOnClickListener(btnClickListener);
        btnStopPlay.setOnClickListener(btnClickListener);

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
                        } else {
                            stopRecording();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;
                case R.id.btn_play:
                    try {
                        playRecording();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;
                case R.id.btn_stop_play:
                    try {
                        stopPlaying();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;
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

    private void stopRecording() {
        if (recorder != null) {
            if (recordTimer.getTimeRemain() > 165) {
                Toast.makeText(getActivity().getApplicationContext(), "Min 15 sec", Toast.LENGTH_SHORT).show();
                recorder.stop();

                recordTimer.cancel();
            } else {
                Toast.makeText(getActivity().getApplicationContext(), "Record Done" , Toast.LENGTH_SHORT).show();
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
