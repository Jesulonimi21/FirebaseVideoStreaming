package com.jesulonimi.user.firebasevideostreaming;

import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.VideoView;

public class MainActivity extends AppCompatActivity {
ImageView playButton;
ProgressBar videoProgress;
VideoView mainVideoView;
TextView currentTimer;
TextView durationTimer;
Uri videoUri;
ProgressBar bufferProgress;
boolean isPlaying;
int duration=0;
int currentTime=0;
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        playButton=findViewById(R.id.play_btn);
        videoProgress=findViewById(R.id.videoProgress);
        mainVideoView=findViewById(R.id.MainVideo);

        currentTimer=findViewById(R.id.current_timer);
        durationTimer=findViewById(R.id.duration_timer);
        bufferProgress=findViewById(R.id.indeterminate_progress);
        isPlaying=false;

        videoProgress.setMax(100);
        videoUri=Uri.parse("https://firebasestorage.googleapis.com/v0/b/fir-videostreaming-deeb5.appspot.com/o/Firestore%20Tutorial%20Part%201%20-%20WHAT%20IS%20CLOUD%20FIRESTORE_%20-%20Android%20Studio%20Tutorial_Full-HD.mp4?alt=media&token=41ba6ec0-287a-4b23-a615-c87bf0be402e");
        mainVideoView.setVideoURI(videoUri);
new VideoProgress().execute();
        mainVideoView.setOnInfoListener(new MediaPlayer.OnInfoListener() {
            @Override
            public boolean onInfo(MediaPlayer mp, int what, int extra) {

                if(what==mp.MEDIA_INFO_BUFFERING_START){
                    bufferProgress.setVisibility(View.VISIBLE);
                }else{
                    bufferProgress.setVisibility(View.INVISIBLE);
                }
                return false;
            }
        });
        mainVideoView.setOnPreparedListener(
                new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                duration=mp.getDuration()/1000;
                Log.d("duration",duration+"");
                String durationString=String.format("%02d:%02d",duration/60,duration%60);
                durationTimer.setText(durationString);
            }
        });
        mainVideoView.start();
        mainVideoView.requestFocus();
        isPlaying=true;
        playButton.setImageResource(R.mipmap.ic_launcher);

        playButton.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(isPlaying){
                            mainVideoView.pause();
                            isPlaying=false;
                            playButton.setImageResource(R.drawable.play_btn);
                        }else{
                            mainVideoView.start();
                            isPlaying=true;
                            playButton.setImageResource(R.mipmap.ic_launcher);
                        }

                    }
                }
        );

    }

    public class VideoProgress extends AsyncTask<Void,Integer,Void>{

        @Override
        protected Void doInBackground(Void... voids) {

            do{
                if(isPlaying){
                currentTime=mainVideoView.getCurrentPosition()/1000;
                publishProgress(currentTime);}

            }while (videoProgress.getProgress()<=100);
            return null;
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);

            try{
                int currentPercent=values[0]*100/duration;
                videoProgress.setProgress(currentPercent);
                String currentProgress=String.format("%02d:%02d",values[0]/60,values[0]%60);
                currentTimer.setText(currentProgress);

            }catch (Exception e){

            };

        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        isPlaying=false;
    }
}
