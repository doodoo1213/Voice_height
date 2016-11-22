package com.example.dev.voice_height;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.example.dev.voice_height.AppPreference;

public class MainActivity extends Activity implements OnClickListener{

    int mcompare=0;
    int frequency = 8000;
    int channelConfiguration = AudioFormat.CHANNEL_CONFIGURATION_MONO;
    int audioEncoding = AudioFormat.ENCODING_PCM_16BIT;
    private RealDoubleFFT transformer;
    private AppPreference mPref;
    int blockSize=256;
    Button startStopButton;
    Button mSaveButton;
    boolean started = false;

    RecordAudio recordTask;

    ImageView imageView;
    Bitmap bitmap;
    Canvas canvas;
    Paint paint;
    TextView testview;
    EditText medit;

    int result;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mPref = new AppPreference(getApplicationContext());

        startStopButton = (Button)findViewById(R.id.StartStopButton);
        startStopButton.setOnClickListener(this);
        mSaveButton = (Button)findViewById(R.id.saveButton);
        mSaveButton.setOnClickListener(this);

        transformer = new RealDoubleFFT(blockSize);
        imageView = (ImageView)findViewById(R.id.ImageView01);
        testview = (TextView)findViewById(R.id.test);
        medit = (EditText)findViewById(R.id.frequency);


        bitmap = Bitmap.createBitmap((int)256,(int)100,Bitmap.Config.ARGB_8888);
        //그림 자르기(Bitmap source,int x, int y, int width, int height)
        canvas = new Canvas(bitmap);
        paint = new Paint();
        paint.setColor(Color.GREEN);
        imageView.setImageBitmap(bitmap);

        String input = medit.getText().toString();
        if (input.compareTo("") == 1) {
            int a = Integer.parseInt(input);
            mPref.addHZ(a);
        }
        //testview.setText(String.valueOf(result));
    }


    @Override
    protected void onPause() {
        // TODO Auto-generated method stub
        super.onPause();
        mPref.getHZ();
    }
    @Override
    public void onStop(){
        super.onStop();


    }
    private class RecordAudio extends AsyncTask<Void, double[], Void> {
        protected void onProgressUpdate(double[]... toTransform) {
            canvas.drawColor(Color.BLACK);

            for (int i = 0; i < toTransform[0].length; i++)
            {

                int x = i;

                int downy = (int) (100 - (toTransform[0][i] * 10));
                int upy = 100;
                int test1 = (int) (toTransform[0][i] * 100);

                if(test1 > 2000){
                    Log.e("주파수 : ",test1+"HZ");
                }
                if(test1>mcompare)
                    mcompare=test1;

                testview.setText(String.valueOf(mcompare));
                canvas.drawLine(x, downy, x, upy, paint);
            }


            imageView.invalidate();
        }
        @Override
        protected Void doInBackground(Void... params) {
            try{
                int bufferSize = AudioRecord.getMinBufferSize(frequency, channelConfiguration, audioEncoding);

                AudioRecord audioRecord = new AudioRecord(
                        MediaRecorder.AudioSource.VOICE_RECOGNITION,frequency,channelConfiguration,audioEncoding, bufferSize);

                short[] buffer = new short[blockSize];
                double[] toTransform = new double[blockSize];



                audioRecord.startRecording();

                while(started){
                    int bufferReadResult = audioRecord.read(buffer,0,blockSize);

                    for(int i = 0; i < blockSize && i < bufferReadResult; i++){


                        toTransform[i] = (double)buffer[i] / Short.MAX_VALUE;

                        //result = (int)toTransform[i];
                        //testview.setText(String.valueOf(result));
//         if(toTransform[i]>test){
//          Log.e("", "" + toTransform[i]);
//          test =  toTransform[i];
//          result =  toTransform[i];
//         }
//         else{
//          result = test;
//         }
                    }
                    transformer.ft(toTransform);
                    publishProgress(toTransform);
                }
                audioRecord.stop();
            }catch(Throwable t){
                Log.e("AudioRecord","Recording Failed");
            }
            return null;
        }
    }

    @Override
    public void onClick(View v){

        switch (v.getId()) {
            case R.id.StartStopButton:
                if (started) {
                    started = false;
                    startStopButton.setText("Start");
                    recordTask.cancel(true);
                } else {
                    started = true;
                    startStopButton.setText("Stop");
                    recordTask = new RecordAudio();
                    recordTask.execute();
                }
                break;
            case R.id.saveButton:
                Toast.makeText(this, "저장되었습니다.", Toast.LENGTH_SHORT).show();
                break;
        }
    }

}
