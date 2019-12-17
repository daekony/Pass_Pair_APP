package edu.nccu.mis.passpair.Homepage;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.support.annotation.IdRes;
import android.util.Log;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
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

import edu.nccu.mis.passpair.FriendInvite.FriendMail;
import edu.nccu.mis.passpair.Gift.GiftReceived;
import edu.nccu.mis.passpair.Homepage.Fragment.FriendFragment;
import edu.nccu.mis.passpair.Homepage.Fragment.ProfileFragment;
import edu.nccu.mis.passpair.Homepage.Fragment.ManuFragment;
import edu.nccu.mis.passpair.Homepage.Fragment.PostFragment;
import edu.nccu.mis.passpair.R;

public class HomePage extends AppCompatActivity{
       // implements NavigationView.OnNavigationItemSelectedListener
    BottomBar mBottomBar;
    // Write a message to the database
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    String HomeUID = "";
    TextView name,post,friend;
    ImageView img;

    //宣告特約工人的經紀人
    private Handler mThreadHandler;
    //宣告特約工人
    private HandlerThread mThread;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(edu.nccu.mis.passpair.R.layout.activity_home_page);
        Toolbar toolbar = (Toolbar) findViewById(edu.nccu.mis.passpair.R.id.toolbar);
        setSupportActionBar(toolbar);
        name = (TextView) findViewById(edu.nccu.mis.passpair.R.id.txtNameHome);
        img = (ImageView)findViewById(edu.nccu.mis.passpair.R.id.imageHome);
        post = (TextView) findViewById(R.id.txtPostsHome);
        friend = (TextView)  findViewById(R.id.txtFriendsHome);

        Intent intent = this.getIntent();
        Bundle bundle = intent.getExtras();
        HomeUID = bundle.getString("UID");

        mBottomBar = (BottomBar) findViewById(edu.nccu.mis.passpair.R.id.BottombarHome);
        mBottomBar.setItems(edu.nccu.mis.passpair.R.menu.menu_home_page);
        mBottomBar.mapColorForTab(0, "#BDBDBD");
        mBottomBar.mapColorForTab(1, "#BDBDBD");
        mBottomBar.mapColorForTab(2, "#BDBDBD");
        mBottomBar.mapColorForTab(3, "#BDBDBD");

        mThread = new HandlerThread("loadimages");
        mThread.start();
        mThreadHandler=new Handler(mThread.getLooper());
        mThreadHandler.post(run_loadimage);

//        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
 /**       ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
**/
    }
/**
    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }
 **/

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(edu.nccu.mis.passpair.R.menu.home_page, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        Intent intent = this.getIntent();
        Bundle bundle = intent.getExtras();
        final String HomeUID = bundle.getString("UID");
        //noinspection SimplifiableIfStatement
    //        if (id == R.id.action_settings) {
     //          return true;
    //    }

        if(id == edu.nccu.mis.passpair.R.id.action_refresh){
            Intent intentF = new Intent();
            intentF.setClass(HomePage.this,FriendMail.class);
            Bundle bundleFri = new Bundle();
            bundleFri.putString("UID", HomeUID);
            intentF.putExtras(bundleFri);
            startActivity(intentF);
        }
        if(id == edu.nccu.mis.passpair.R.id.action_refresh2){
            Intent intentG = new Intent();
            intentG.setClass(HomePage.this,GiftReceived.class);
            Bundle bundleG = new Bundle();
            bundleG.putString("UID", HomeUID);
            intentG.putExtras(bundleG);
            startActivity(intentG);
        }
        return super.onOptionsItemSelected(item);
    }

    Runnable run_loadimage = new Runnable() {
        @Override
        public void run() {
            DatabaseReference myRef = database.getReference().child("User");

            myRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    // This method is called once with the initial value and again
                    // whenever data at this location is updated.
                    String n = dataSnapshot.child(HomeUID).child("基本資料").child("暱稱").getValue(String.class);
                    if (dataSnapshot.child(HomeUID).hasChild("貼文")){
                        Long post_count = dataSnapshot.child(HomeUID).child("貼文").getChildrenCount();
                        post.setText(post_count + " Post");
                    }else {
                        post.setText("0  Post");
                    }
                    if (dataSnapshot.child(HomeUID).hasChild("朋友")){
                        Long friend_count = dataSnapshot.child(HomeUID).child("朋友").getChildrenCount();
                        Log.e("friend",String.valueOf(friend_count));
                        friend.setText(String.valueOf(friend_count) + "  Friend");
                    }else {
                        friend.setText("0  Friend");
                    }

                    String url = dataSnapshot.child(HomeUID).child("基本資料").child("大頭照").getValue(String.class);
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
                    if (menuItemId == edu.nccu.mis.passpair.R.id.bottom_post){
                        PostFragment postFragment = new PostFragment();
                        Bundle bundle = new Bundle();
                        bundle.putString("UID", HomeUID);
                        postFragment.setArguments(bundle);
                        getSupportFragmentManager().beginTransaction().replace(edu.nccu.mis.passpair.R.id.Replace_Home_Fragment,postFragment).commitAllowingStateLoss();
                    }else if (menuItemId == edu.nccu.mis.passpair.R.id.bottom_friend){
                        FriendFragment friendFragment = new FriendFragment();
                        Bundle bundle = new Bundle();
                        bundle.putString("UID", HomeUID);
                        friendFragment.setArguments(bundle);
                        getSupportFragmentManager().beginTransaction().replace(edu.nccu.mis.passpair.R.id.Replace_Home_Fragment,friendFragment).commitAllowingStateLoss();

                    }else if (menuItemId == edu.nccu.mis.passpair.R.id.bottom_info){
                        ProfileFragment profileFragment = new ProfileFragment();

                        Bundle bundlePro=new Bundle();
                        bundlePro.putString("ProUID",HomeUID);
                        profileFragment.setArguments(bundlePro);

                        getSupportFragmentManager().beginTransaction().replace(edu.nccu.mis.passpair.R.id.Replace_Home_Fragment,profileFragment).commitAllowingStateLoss();

                    }else if (menuItemId == edu.nccu.mis.passpair.R.id.bottom_manu){
                        ManuFragment manuFragment = new ManuFragment();

                        Bundle bundleManu = new Bundle();
                        bundleManu.putString("ManuUID",HomeUID);
                        manuFragment.setArguments(bundleManu);

                        getSupportFragmentManager().beginTransaction().replace(edu.nccu.mis.passpair.R.id.Replace_Home_Fragment,manuFragment).commitAllowingStateLoss();
                    }
                }

                @Override
                public void onMenuTabReSelected(@IdRes int menuItemId) {

                }
            });
        }
    };

    /**
    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_home) {

        } else if (id == R.id.nav_pair) {

        } else if (id == R.id.nav_location) {

        } else if (id == R.id.nav_call) {

        } else if (id == R.id.nav_voicephoto) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
    **/
}
