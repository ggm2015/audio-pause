package ua.com.sezone.full_screen;

import android.app.Activity;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by ggm on 19.12.15.
 */
public class Player extends Activity {

    Toast toast;
    AudioManager audioManager;
    Timer timerSound;
    Button buttonSound;
    VideoView videoView;
    MediaController mc;
    Handler handler;

    final long delay = 1000 * 30;
    private int position = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.player);

        getToast(R.string.sound_off_30sec);
        getToast(R.string.sound_off);

        audioManager = (AudioManager) getSystemService(AUDIO_SERVICE);
        videoView = (VideoView) findViewById(R.id.vv);
        buttonSound = (Button) findViewById(R.id.play_buttonSound);
        if (mc == null) {
            mc = new MediaController(Player.this);
            mc.setAnchorView(videoView);
            mc.setMediaPlayer(videoView);
        }

        Intent intent = getIntent();
        String urlMovie = intent.getStringExtra("urlMovie");

        try {
            videoView.setMediaController(mc);
            videoView.setVideoURI(Uri.parse(urlMovie));

            videoView.setOnPreparedListener(PreparedListener);//
            videoView.requestFocus();
            soundOff();

            handler = new Handler() {
                public void handleMessage(android.os.Message msg) {
                    if (audioManager.getStreamVolume(AudioManager.STREAM_MUSIC)==0) {
                        getToast(R.string.sound_off);
                    }
                };
            };

        } catch (Exception e) {
            Log.e("Error", e.getMessage());
            e.printStackTrace();
        }

        videoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                //start Previous Activity here
                Player.this.finish();
            }
        });
    }




    //PreparedListener
    MediaPlayer.OnPreparedListener PreparedListener = new MediaPlayer.OnPreparedListener() {
        @Override
        public void onPrepared(MediaPlayer m) {
            try {
                if (m.isPlaying()) {
                    m.stop();
                    m.release();
                }
                m.setVolume(15, 15);
                m.setLooping(false);
                m.start();


            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };
// end PreparedListener

    private void soundOff() {
        timerSound = new Timer();
        class UpdateLevelSound extends TimerTask {
            public void run() {
                audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, 0, 0);
                int volume_level = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
                if (volume_level == 0) {
                    handler.sendEmptyMessage(0);
                    buttonSound.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, 15, 15);
                            timerSound.cancel();
                            soundOff();
                        }
                    });
                }
            }
        }
        UpdateLevelSound updateLevelSound = new UpdateLevelSound();
        timerSound.schedule(updateLevelSound, delay);
    }// end soundOff




    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        savedInstanceState.putInt("Position", videoView.getCurrentPosition());
        videoView.pause();

    }


    @Override

    public void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        position = savedInstanceState.getInt("Position");
        videoView.seekTo(position);
    }

    public void getToast(int mText) {
        toast = Toast.makeText(getApplicationContext(), mText, toast.LENGTH_SHORT);
        LinearLayout toastLayout = (LinearLayout) toast.getView();
        TextView toastTV = (TextView) toastLayout.getChildAt(0);
        toastTV.setTextSize(12);
        toast.setGravity(Gravity.BOTTOM, 0, 0);
        toast.show();
    }

    //ACTIVITY Life cycle
    @Override
    protected void onDestroy() {
        super.onDestroy();
        timerSound.cancel();
    }

}