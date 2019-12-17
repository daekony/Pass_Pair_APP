package edu.nccu.mis.passpair.RandomCall;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.media.AudioManager;
import android.os.CountDownTimer;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.sinch.android.rtc.PushPair;
import com.sinch.android.rtc.Sinch;
import com.sinch.android.rtc.SinchClient;
import com.sinch.android.rtc.calling.Call;
import com.sinch.android.rtc.calling.CallClient;
import com.sinch.android.rtc.calling.CallClientListener;
import com.sinch.android.rtc.calling.CallListener;

import java.util.List;
import java.util.concurrent.TimeUnit;

import edu.nccu.mis.passpair.Homepage.HomePage;
import edu.nccu.mis.passpair.R;
import me.itangqi.waveloadingview.WaveLoadingView;

public class CallMainActivity extends AppCompatActivity {
    private static final String APP_KEY = "7fc486e6-dbaa-41f1-b34a-10d09feaf9e8";
    private static final String APP_SECRET = "B/R/DCPiuEGSe3B3kCK+bA==";
    private static final String ENVIRONMENT = "sandbox.sinch.com";
    private Call call;
    private TextView call_status;
    private SinchClient sinchClient;
    private Button button, answer;
    private String callerId;
    private String recipientId;
    private AudioManager audioManager;
    private WaveLoadingView mWaveLoadingView;
    private long timeCountInMilliSeconds = 30000;
    private CountDownTimer countDownTimer;
    private DatabaseReference Ref_callonline = FirebaseDatabase.getInstance().getReference().child("callonline");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_call_main);
        audioManager = (AudioManager) this.getSystemService(Context.AUDIO_SERVICE);
        Intent intent = getIntent();
        callerId = intent.getStringExtra("UID");
        recipientId = intent.getStringExtra("recipient");
        sinchClient = Sinch.getSinchClientBuilder()
                .context(this)
                .userId(callerId)
                .applicationKey(APP_KEY)
                .applicationSecret(APP_SECRET)
                .environmentHost(ENVIRONMENT)
                .build();

        sinchClient.setSupportCalling(true);
        sinchClient.startListeningOnActiveConnection();
        sinchClient.start();

        sinchClient.getCallClient().addCallClientListener(new SinchCallClientListener());

        button = (Button) findViewById(R.id.call_dial_btn);
        answer = (Button) findViewById(R.id.call_answer_btn);
        answer.setEnabled(false);
        call_status = (TextView) findViewById(R.id.call_status);

        mWaveLoadingView = (WaveLoadingView) findViewById(R.id.waveLoadingView);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (call == null) {
                    call = sinchClient.getCallClient().callUser(recipientId);
                    call.addCallListener(new SinchCallListener());
                    //button.setText("Hang Up");
                } else {
                    call.hangup();
                    Ref_callonline.child(callerId).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            Intent intent_main = new Intent();
                            Bundle bundle_select = new Bundle();
                            bundle_select.putString("UID", callerId);
                            bundle_select.putString("recipient", recipientId);
                            intent_main.putExtras(bundle_select);
                            intent_main.setClass(CallMainActivity.this, QuestionRandomActivity.class);
                            startActivity(intent_main);
                        }
                    });
                }
            }
        });
    }

    private class SinchCallListener implements CallListener {
        @Override
        public void onCallEnded(Call endedCall) {
            call = null;
            button.setText("Call");
            button.setBackgroundColor(Color.GREEN);
            answer.setAlpha(0);
            answer.setEnabled(false);
            call_status.setText("結束通話");
            Toast.makeText(getApplicationContext(), "通話結束", Toast.LENGTH_SHORT).show();
//            callState.setText("disconnected");
            setVolumeControlStream(audioManager.USE_DEFAULT_STREAM_TYPE);
            Ref_callonline.child(callerId).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    Intent intent_main = new Intent();
                    Bundle bundle_select = new Bundle();
                    bundle_select.putString("UID", callerId);
                    bundle_select.putString("recipient", recipientId);
                    intent_main.putExtras(bundle_select);
                    intent_main.setClass(CallMainActivity.this, QuestionRandomActivity.class);
                    startActivity(intent_main);
                }
            });
        }

        @Override
        public void onCallEstablished(Call establishedCall) {
//            callState.setText("connected");
            audioManager.setSpeakerphoneOn(true);
            setVolumeControlStream(audioManager.STREAM_MUSIC);
            call_status.setText("開始通話");
            button.setText("Hang Up");
            button.setBackgroundColor(Color.RED);
            startCountdown();
        }

        @Override
        public void onCallProgressing(Call progressingCall) {
//            callState.setText("ringing");
            call_status.setText("撥打中");
        }

        @Override
        public void onShouldSendPushNotification(Call call, List<PushPair> pushPairs) {
        }
    }

    private class SinchCallClientListener implements CallClientListener {
        @Override
        public void onIncomingCall(CallClient callClient, Call incomingCall) {
            call = incomingCall;
//            callState.setText("Have an Incoming call");
            //設定接電話按鈕
            call_status.setText("來電中");
            answer.setEnabled(true);
            answer.setAlpha(1);
            answer.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    call.answer();
                    call.addCallListener(new SinchCallListener());
                    button.setText("Hang Up");
                    button.setBackgroundColor(Color.RED);
                }
            });
//            call.answer();
//            call.addCallListener(new SinchCallListener());
//            button.setText("Hang Up");
        }
    }

    // 消除登入狀態
    @Override
    protected void onStop() {
        super.onStop();
        if (call != null){
            call.hangup();
        }
        Ref_callonline.child(callerId).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (call != null){
                    call.hangup();
                }
//                Intent intent_main = new Intent();
//                Bundle bundle_select = new Bundle();
//                bundle_select.putString("UID", callerId);
//                intent_main.putExtras(bundle_select);
//                intent_main.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//                intent_main.setClass(CallMainActivity.this, HomePage.class);
//                startActivity(intent_main);
            }
        });

    }

    @Override
    protected void onPause() {
        super.onPause();
        if (call != null){
            call.hangup();
        }
        Ref_callonline.child(callerId).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
//                if (call != null){
//                    call.hangup();
//                }
//                Intent intent_main = new Intent();
//                Bundle bundle_select = new Bundle();
//                bundle_select.putString("UID", callerId);
//                intent_main.putExtras(bundle_select);
//                intent_main.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//                intent_main.setClass(CallMainActivity.this, HomePage.class);
//                startActivity(intent_main);
            }
        });

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (call != null){
            call.hangup();
        }
        Ref_callonline.child(callerId).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {

//                Intent intent_main = new Intent();
//                Bundle bundle_select = new Bundle();
//                bundle_select.putString("UID", callerId);
//                intent_main.putExtras(bundle_select);
//                intent_main.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//                intent_main.setClass(CallMainActivity.this, HomePage.class);
//                startActivity(intent_main);
            }
        });

    }

    private void startCountdown() {
        // call to initialize the progress bar values
        setProgressBarValues();
        // call to start the count down timer
        startCountDownTimer();
    }

    private void setProgressBarValues() {
        mWaveLoadingView.setProgressValue(100);
    }

    private void startCountDownTimer() {
        countDownTimer = new CountDownTimer(timeCountInMilliSeconds, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                Long second_long = millisUntilFinished;
                int second_int_percent = second_long.intValue() * 100;
                mWaveLoadingView.setProgressValue(second_int_percent / 30000);
                mWaveLoadingView.setCenterTitle(TimeFormatter(millisUntilFinished));
            }

            @Override
            public void onFinish() {
                // call to initialize the progress bar values
                mWaveLoadingView.setProgressValue(0);
                mWaveLoadingView.setCenterTitle(":00");
                if (call != null) {
                    call.hangup();
                }
            }

        }.start();
        countDownTimer.start();
    }

    private String TimeFormatter(long milliSeconds) {
        String hms = String.format(":%02d",
                TimeUnit.MILLISECONDS.toSeconds(milliSeconds));
        return hms;
    }
}
