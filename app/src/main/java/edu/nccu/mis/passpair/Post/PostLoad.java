package edu.nccu.mis.passpair.Post;

import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.IllegalFormatCodePointException;
import java.util.Map;

import edu.nccu.mis.passpair.R;

public class PostLoad extends AppCompatActivity {
    private String image_url;
    private String voice_url;

    private MediaPlayer mediaPlayer;
    private TextView textView;
    private String UID,PostID;

    private boolean waitDouble = true;
    private static final int DOUBLE_CLICK_TIME = 350; //兩次單擊的時間間隔
    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    private DatabaseReference myRef_User = database.getReference().child("User");
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_load);
        final int position = 0;

        Intent intent = this.getIntent();
        Bundle bundle = intent.getExtras();
        UID = bundle.getString("UID");
        PostID = bundle.getString("PostID");

        final ImageButton post = (ImageButton) findViewById(R.id.postload_image);
        textView = (TextView) findViewById(R.id.postload_txt);
        post.setAdjustViewBounds(true);
        post.setMaxHeight(8000);
        post.setMaxWidth(7000);


        myRef_User.child(UID).child("貼文").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChild(PostID)){
                    String image_url = dataSnapshot.child(PostID).child("image").getValue(String.class);
                    String voice_url = dataSnapshot.child(PostID).child("voice").getValue(String.class);
                    Uri image = Uri.parse(image_url);
                    Uri voice = Uri.parse(voice_url);
                    Picasso.with(getApplicationContext())
                            .load(image) // 圖片路徑
                            .placeholder(R.mipmap.ic_passpair_launch)  // 圖片讀取完成之前先顯示的佔位圖
                            .error(R.mipmap.ic_passpair_launch)
                            .resize(800, 700)   // 將圖片寬高轉為200*200 pixel
//                                                       .centerInside()     // 與resize搭配使用，將調整過的圖片完整塞進ImageView中
//                                                       .fit()              // 與resize只能擇一使用，將圖片寬高轉為ImageView的大小
//                                                       .rotate(90)         // 將圖片旋轉90度
                            .into(post);  // 要顯示圖的View
                    mediaPlayer = MediaPlayer.create(PostLoad.this,voice);// 建立網路資源音樂檔案Uri物件
                    if (mediaPlayer != null){
                        mediaPlayer.start();// 開始播放
                        Toast.makeText(getApplicationContext(),"開始播放",Toast.LENGTH_SHORT).show();
                        mediaPlayer.setLooping(true);
                    }
                    post.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            if (waitDouble == true) {
                                waitDouble = false;
                                Thread thread = new Thread() {
                                    @Override
                                    public void run() {
                                        try {
                                            sleep(DOUBLE_CLICK_TIME);
                                            if (waitDouble == false) {
                                                waitDouble = true;
                                                singleClick();
                                            }
                                        } catch (InterruptedException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                };
                                thread.start();
                            } else {
                                waitDouble = true;
                                doubleClick();
                            }
                        }
                    });
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
    // 單擊響應事件
    private void singleClick(){
        // 暫停播放
        if (mediaPlayer != null && mediaPlayer.isPlaying()){
            mediaPlayer.pause();
        }
        Log.i("DoubleClickTest", "singleClick");
    }

    // 雙擊響應事件
    private void doubleClick(){
        // 開始播放
        if (mediaPlayer != null) {
            mediaPlayer.start();
        }
        Log.i("DoubleClickTest", "doubleClick");
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mediaPlayer != null && mediaPlayer.isPlaying()){
            mediaPlayer.pause();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mediaPlayer != null && mediaPlayer.isPlaying()){
            mediaPlayer.pause();
        }
    }
}

