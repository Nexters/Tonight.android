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
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class RecordFragment extends Fragment {

    private static final String ARG_PARAM = "param";
    private String mParam;

    private boolean isRecording = false;
    private String OUTPUT_FILE = Environment.getExternalStorageDirectory() + "/audiorecorder.wav";

    private MediaPlayer player;
    private MediaRecorder recorder;

    private TextView text;
    private ImageButton btnStart;
    private ImageButton btnPlay;
    private ImageButton btnCancel;
    private ImageButton btnUpload;

    private long seconds = 180000;
    private SimpleDateFormat timeFormat = new SimpleDateFormat("mm:ss");
    private RecordTimer recordTimer = new RecordTimer(seconds, 1000);

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

        btnStart = (ImageButton) view.findViewById(R.id.btn_start);
        btnPlay = (ImageButton) view.findViewById(R.id.btn_play);
        btnCancel = (ImageButton) view.findViewById(R.id.cancel);
        btnUpload = (ImageButton) view.findViewById(R.id.upload);
        text = (TextView) view.findViewById(R.id.timer);

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

    // 녹음 시작
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

    //취소 버튼 클릭 시 확인창
    public void dialog_Cancel(Context ctx) {

        DialogInterface.OnClickListener cancelListener = new DialogInterface.OnClickListener(){

            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case DialogInterface.BUTTON_POSITIVE:
                        Toast.makeText(getActivity().getApplicationContext(),"File Deleted",Toast.LENGTH_SHORT).show();
                        deleteFile();
                        btnPlay.setVisibility(View.INVISIBLE);
                        btnCancel.setVisibility(View.INVISIBLE);
                        btnUpload.setVisibility(View.INVISIBLE);
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

    // 업로드버튼 클릭시 확인창
    public void dialog_Upload(Context ctx) {
        DialogInterface.OnClickListener uploadListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case DialogInterface.BUTTON_POSITIVE:
                        Toast.makeText(getActivity().getApplicationContext(), "File Uploaded", Toast.LENGTH_SHORT).show();
                        try {
                            toZip();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
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

    //파일 삭제
    private void deleteFile() {
        File file = new File(OUTPUT_FILE);
        if(file.exists()){
            file.delete();
        }
    }

    // 녹음 중지
    private void stopRecording() {
        if (recorder != null && isRecording) {
            if (recordTimer.getTimeRemain() > 165) {
                Toast.makeText(getActivity().getApplicationContext(), "Min 15 sec", Toast.LENGTH_SHORT).show();
                recorder.stop();

                recordTimer.cancel();
                deleteFile();
                isRecording = false;
            } else {
                Toast.makeText(getActivity().getApplicationContext(), "Record Done" , Toast.LENGTH_SHORT).show();
                recorder.stop();

                recordTimer.cancel();
                isRecording = false;
            }
        }
    }

    // 녹음파일 재생
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

    //녹음재생 중지
    private void stopPlaying() {
        if (player != null && player.isPlaying()) {
            player.stop();
        }
    }

    //오디오 파일 압축
    private void toZip() throws IOException {

        byte[] buffer = new byte[2048];
        try {
            String audioZip = Environment.getExternalStorageDirectory() + "/audio.zip";
            ZipOutputStream zipFile = new ZipOutputStream(new FileOutputStream(audioZip));
            ZipEntry zipEntry = new ZipEntry(OUTPUT_FILE);
            zipFile.putNextEntry(zipEntry);
            FileInputStream inputStream = new FileInputStream(OUTPUT_FILE);

            int len;
            while ((len = inputStream.read(buffer)) > 0) {
                zipFile.write(buffer, 0, len);
            }

            inputStream.close();
            zipFile.closeEntry();
            zipFile.close();

            String encodedFile = Base64.encodeToString(buffer, Base64.DEFAULT);

        } catch (IOException e) {
            e.printStackTrace();
        }

    }


    //녹음시간 카운트
    public class RecordTimer extends CountDownTimer {
        private long timeRemain;

        public RecordTimer(long startTime, long interval) {
            super(startTime, interval);
            timeRemain = 0;
        }

        @Override
        public void onTick(long millisUntilFinished) {
            text.setText(timeFormat.format(millisUntilFinished));
            timeRemain = millisUntilFinished / 1000;

        }

        @Override
        public void onFinish() {
            text.setText(timeFormat.format(0));
        }

        public long getTimeRemain() {
            return timeRemain;
        }


    }



}
