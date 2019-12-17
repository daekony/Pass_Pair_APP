package edu.nccu.mis.passpair.Homepage.Friendpage;

import android.content.Intent;
import android.os.Handler;
import android.os.HandlerThread;
import android.support.annotation.IdRes;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.roughike.bottombar.BottomBar;
import com.roughike.bottombar.OnMenuTabClickListener;
import com.squareup.picasso.Picasso;

import edu.nccu.mis.passpair.Homepage.Fragment.GiftFragment;
import edu.nccu.mis.passpair.Homepage.Fragment.PostFragment;
import edu.nccu.mis.passpair.Homepage.Fragment.ProfileFragment;
import edu.nccu.mis.passpair.R;

public class FriendPage extends AppCompatActivity {
    BottomBar mBottomBar;
    // Write a message to the database
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    String FUID = "";
    String MyUID = "";
    TextView name,friend,post;
    ImageView img;

    //宣告特約工人的經紀人
    private Handler mThreadHandler;
    //宣告特約工人
    private HandlerThread mThread;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friend_page);
        name = (TextView) findViewById(R.id.friendPage_txtName);
        img = (ImageView) findViewById(R.id.friendPage_image);
        friend = (TextView) findViewById(R.id.friendPage_txtFriends);
        post = (TextView) findViewById(R.id.friendPage_txtPosts);

        Intent intent = this.getIntent();
        Bundle bundle = intent.getExtras();
        FUID = bundle.getString("FUID");
        MyUID = bundle.getString("MyUID");

        mBottomBar = (BottomBar) findViewById(R.id.friendPage_Bottombar);
        mBottomBar.setItems(R.menu.menu_friend_page);
        mBottomBar.mapColorForTab(0, "#BDBDBD");
        mBottomBar.mapColorForTab(1, "#BDBDBD");
        mBottomBar.mapColorForTab(2, "#BDBDBD");

        mThread = new HandlerThread("loadimages");
        mThread.start();
        mThreadHandler = new Handler(mThread.getLooper());
        mThreadHandler.post(run_loadimage);

    }

    Runnable run_loadimage = new Runnable() {
        @Override
        public void run() {
            DatabaseReference myRef = database.getReference().child("User");

            myRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    String n = dataSnapshot.child(FUID).child("基本資料").child("暱稱").getValue(String.class);

                    if (dataSnapshot.child(FUID).hasChild("貼文")){
                        Long post_count = dataSnapshot.child(FUID).child("貼文").getChildrenCount();
                        post.setText(post_count + " Post");
                    }else {
                        post.setText("0  Post");
                    }
                    if (dataSnapshot.child(FUID).hasChild("朋友")){
                        Long friend_count = dataSnapshot.child(FUID).child("朋友").getChildrenCount();
                        friend.setText(friend_count + "  Friend");
                    }else {
                        friend.setText("0  Friend");
                    }

                    String url = dataSnapshot.child(FUID).child("基本資料").child("大頭照").getValue(String.class);
                    Log.d("TAG", "Value is: " + n);

                    name.setText(n);
                    Picasso.with(getApplicationContext()).load(url).into(img,new com.squareup.picasso.Callback(){
                        @Override
                        public void onSuccess() {
                            mThreadHandler.post(run_loadrecycler);
                        }

                        @Override
                        public void onError() {

                        }
                    });
                }

                @Override
                public void onCancelled(DatabaseError error) {
                    // Failed to read value
                    Log.w("TAG", "Failed to read value.", error.toException());
                }
            });
        }
    };

    Runnable run_loadrecycler = new Runnable() {
        @Override
        public void run() {
            mBottomBar.setOnMenuTabClickListener( new OnMenuTabClickListener() {
                @Override
                public void onMenuTabSelected(@IdRes int menuItemId) {
                    if (menuItemId == R.id.friend_bottom_post){
                        PostFragment postFragment = new PostFragment();
                        Bundle bundle = new Bundle();
                        bundle.putString("UID", FUID);
                        postFragment.setArguments(bundle);
                        getSupportFragmentManager().beginTransaction().replace(R.id.Replace_friendPage_Fragment,postFragment).commit();
                    }else if (menuItemId == R.id.friend_bottom_info){
                        ProfileFragment profileFragment = new ProfileFragment();
                        Bundle bundlePro=new Bundle();
                        bundlePro.putString("ProUID", FUID);
                        profileFragment.setArguments(bundlePro);
                        getSupportFragmentManager().beginTransaction().replace(R.id.Replace_friendPage_Fragment,profileFragment).commit();
                    }else if (menuItemId == R.id.friend_bottom_gift){
                        GiftFragment giftFragment = new GiftFragment();
                        Bundle bundleG=new Bundle();
                        bundleG.putString("UID", MyUID);
                        bundleG.putString("FUID", FUID);
                        giftFragment.setArguments(bundleG);
                        getSupportFragmentManager().beginTransaction().replace(R.id.Replace_friendPage_Fragment,giftFragment).commit();
                    }
                }

                @Override
                public void onMenuTabReSelected(@IdRes int menuItemId) {

                }
            });
        }
    };
}
