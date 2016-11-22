package com.example.dev.voice_height;

import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import java.text.DecimalFormat;

public class MainActivity extends ActionBarActivity implements OnClickListener{

    private static final int sampleRate = 8000; //Hz
    private int bufferSize;
    private AudioRecord audio;
    private double lastLevel = 0;
    private Thread thread;
    private static final int SAMPLE_DELAY = 75;

    Button BtStartStop;
    TextView txtHz, txtHighHz;
    boolean isStarted = false;

    double highHz = 0;
    String pattern = "#####";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //View 선언부
        BtStartStop = (Button)findViewById(R.id.BtStartStop);
        txtHz = (TextView)findViewById(R.id.txtHz);
        txtHighHz = (TextView)findViewById(R.id.txtHighHz);

        //View 구현부
        BtStartStop.setOnClickListener(this);



        initBufferSize();
    }

    /**
     * 버퍼 초기화 하는 메소드
     */
    private void initBufferSize() {
        try {
            bufferSize = AudioRecord
                    .getMinBufferSize(sampleRate, AudioFormat.CHANNEL_IN_MONO,
                            AudioFormat.ENCODING_PCM_16BIT);
        } catch (Exception e) {
            Log.e("TrackingFlow", "Exception", e);
        }
    }


    /**
     * Audio 멈추는 메소드
     */
    private void stopAudio() {
        thread.interrupt();
        thread = null;
        try {
            if (audio != null) {
                audio.stop();
                audio.release();
                audio = null;

                //// 변경 필요하면 넣으면 됨.
                DecimalFormat decimalFormat = new DecimalFormat(pattern);      //정수만 출력되게 형 변환.
                txtHighHz.setText("high Hz : "+decimalFormat.format(highHz)+"Hz");
                highHz=0;      // high Hz를 초기화 함.
            }
        } catch (Exception e) {e.printStackTrace();}
    }

    /**
     * Audio 인식하는 메소드
     */
    private void audioStart() {
        audio = new AudioRecord(MediaRecorder.AudioSource.MIC, sampleRate,
                AudioFormat.CHANNEL_IN_MONO,
                AudioFormat.ENCODING_PCM_16BIT, bufferSize);

        audio.startRecording();
        thread = new Thread(new Runnable() {
            public void run() {
                while(thread != null && !thread.isInterrupted()){
                    //Let's make the thread sleep for a the approximate sampling time
                    try{Thread.sleep(SAMPLE_DELAY);}catch(InterruptedException ie){ie.printStackTrace();}
                    readAudioBuffer();//After this call we can get the last value assigned to the lastLevel variable

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if(highHz < lastLevel) {    // 최대값 변경 하는 코드
                                highHz = lastLevel;
                            }

                            //Hz(?) 별로 값 나뉘게 적용하면 됨.
                            if(lastLevel > 0 && lastLevel <= 10) {
                                txtHz.setText("current Level : 10 dB");
                            } else if(lastLevel > 10 && lastLevel <= 20){
                                txtHz.setText("current Level : 20 dB");
                            } else if(lastLevel > 20 && lastLevel <= 30){
                                txtHz.setText("current Level : 30 dB");
                            } else if(lastLevel > 30 && lastLevel <= 40){
                                txtHz.setText("current Level : 40 dB");
                            } else if(lastLevel > 40 && lastLevel <= 50){
                                txtHz.setText("current Level : 50 dB");
                            } else if(lastLevel > 50 && lastLevel <= 60){
                                txtHz.setText("current Level : 60 dB");
                            } else if(lastLevel > 80 && lastLevel <= 70){
                               txtHz.setText("current Level : 70 dB");
                            } else if(lastLevel > 70 && lastLevel <= 120){
                                txtHz.setText("current Level : 120 dB");
                            } else if(lastLevel > 120 && lastLevel <= 170){
                                txtHz.setText("current Level : 170 dB");
                            } else if(lastLevel > 170){
                                txtHz.setText("current Level : 220 dB");
                            }
                        }
                    });
                }
            }
        });
        thread.start();
    }

    /**
     * Functionality that gets the sound level out of the sample
     */
    private void readAudioBuffer() {

        try {
            short[] buffer = new short[bufferSize];

            int bufferReadResult = 1;

            if (audio != null) {

                // Sense the voice...
                bufferReadResult = audio.read(buffer, 0, bufferSize);
                double sumLevel = 0;
                for (int i = 0; i < bufferReadResult; i++) {
                    sumLevel += buffer[i];
                }
                lastLevel = Math.abs((sumLevel / bufferReadResult));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Override
    public void onClick(View v){
        switch (v.getId()) {
            case R.id.BtStartStop:      //Start, Stop 버튼 클릭 시
                if (isStarted) {        //Stop 버튼을 눌렀을 때
                    BtStartStop.setText("Start");
                    stopAudio();    // Audio 멈춤.
                    isStarted = false;
                } else {                //Start 버튼을 눌렀을 때
                    BtStartStop.setText("Stop");
                    audioStart();    // Audio 시작함.
                    isStarted = true;
                }
                break;
        }
    }
}
