package edu.nccu.mis.passpair.Match;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import edu.nccu.mis.passpair.Homepage.HomePage;
import edu.nccu.mis.passpair.R;
import edu.nccu.mis.passpair.RandomCall.QuestionConfirmActivity;

import static android.content.ContentValues.TAG;


public class Match extends AppCompatActivity {
    private ImageView smile, brainwave;
    DatabaseReference myRef_EEG = FirebaseDatabase.getInstance().getReference().child("Match").child("EEG");
    DatabaseReference myRef_User = FirebaseDatabase.getInstance().getReference().child("User");
    String UID;

    ArrayList<String> recommand_list = new ArrayList<String>();
    ArrayList<String> recommand_image_list = new ArrayList<>();
    ArrayList<String> recommand_name_list = new ArrayList<>();
    ArrayList<String> userUID_list_exclude_friend = new ArrayList<>();
    ProgressDialog mProgressDialog;
    private String time_str;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.showmatch_main);
        mProgressDialog = new ProgressDialog(this);
        smile = (ImageView) findViewById(R.id.match_smile);
        brainwave = (ImageView) findViewById(R.id.match_brain);
        smile.setOnClickListener(listener);
        brainwave.setOnClickListener(listener);

        getDateInfo();

        Intent intent = this.getIntent();
        Bundle bundle = intent.getExtras();
        UID = bundle.getString("UID");

        confirmHasData();
        getRecommandID();
        myRef_User.child(UID).child("任務").child(time_str).child("使用腦波或微笑配對").setValue(1);
    }

    private ImageView.OnClickListener listener = new ImageView.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.match_smile:
                    Intent intent = new Intent();
                    intent.setClass(Match.this, Smile.class);
                    Bundle bundle = new Bundle();
                    bundle.putString("UID", UID);
                    bundle.putString("UID", UID);
                    bundle.putStringArrayList("recommand_list", recommand_list);
                    bundle.putStringArrayList("recommand_image_list", recommand_image_list);
                    bundle.putStringArrayList("recommand_name_list", recommand_name_list);
                    intent.putExtras(bundle);
                    startActivity(intent);
                    break;
                case R.id.match_brain:
                    Intent intentB = new Intent();
                    intentB.setClass(Match.this, Brainwave.class);
                    Bundle bundleB = new Bundle();
                    bundleB.putString("UID", UID);
                    bundleB.putStringArrayList("recommand_list", recommand_list);
                    bundleB.putStringArrayList("recommand_image_list", recommand_image_list);
                    bundleB.putStringArrayList("recommand_name_list", recommand_name_list);
                    intentB.putExtras(bundleB);
                    startActivity(intentB);
                    break;
            }
        }
    };

    private void getRecommandID() {
        mProgressDialog.setMessage("取得資料中...");
        mProgressDialog.show();
        Log.e("開始取得資料", "getRecommandID");
        myRef_EEG.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                final ArrayList<String> userUID_list = new ArrayList<String>();
//                Long user_count = dataSnapshot.getChildrenCount();
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    if (!TextUtils.equals(ds.getKey(),UID)) {
                        userUID_list.add(ds.getKey());
                        Log.e("ds", ds.getKey());
                    }
                }
                excludeFriends(userUID_list);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void confirmHasData() {
        myRef_EEG.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (!dataSnapshot.hasChild(UID)) {
                    new AlertDialog.Builder(Match.this)
                            .setIcon(R.mipmap.ic_passpair_launch)
                            .setTitle("尚未進行腦波及微笑測驗")
                            .setMessage("點擊確認後返回主頁")
                            .setPositiveButton("確認", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    Intent intent = new Intent(Match.this, HomePage.class);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                    Bundle bundle = new Bundle();
                                    bundle.putString("UID", UID);
                                    intent.putExtras(bundle);
                                    startActivity(intent);
                                }
                            })
                            .show();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void getDateInfo() {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMdd");
        Date curDate = new Date(System.currentTimeMillis()); // 獲取當前時間
        time_str = formatter.format(curDate);
    }

    private void excludeFriends(final ArrayList<String> userUID_list) {
        myRef_User.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.child(UID).hasChild("朋友")) {
                    String user_sexual = dataSnapshot.child(UID).child("基本資料").child("性別").getValue(String.class);
                    for (int i = 0; i < userUID_list.size(); i++) {
                        Log.e("excludeFriends", "filtering");
                        boolean is_friend = false;
                        String sexual = dataSnapshot.child(userUID_list.get(i)).child("基本資料").child("性別").getValue(String.class);
                        for (DataSnapshot ds_user_friend : dataSnapshot.child(UID).child("朋友").getChildren()) {
                            String user_friend = ds_user_friend.getKey();
                            if (TextUtils.equals(user_friend, userUID_list.get(i))) {
                                is_friend = true;
                                Log.e("excludeFriends", "is_friend");
                            }
                        }
                        if (!is_friend && !TextUtils.equals(user_sexual,sexual)){
                            userUID_list_exclude_friend.add(userUID_list.get(i));
                            Log.e("excludeFriends", userUID_list.get(i));
                        }
                    }
                    if (userUID_list_exclude_friend.size() > 0) {
                        Log.e("getRandomID", "start");
                        getRandomID();
                    } else {
                        new AlertDialog.Builder(Match.this)
                                .setIcon(R.mipmap.ic_passpair_launch)
                                .setTitle("找不到其他推薦者")
                                .setMessage("點擊確認後返回主頁")
                                .setPositiveButton("確認", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        Intent intent = new Intent(Match.this, HomePage.class);
                                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                        Bundle bundle = new Bundle();
                                        bundle.putString("UID", UID);
                                        intent.putExtras(bundle);
                                        startActivity(intent);
                                    }
                                })
                                .show();
                    }
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void getRandomID() {
        int min = 0;
        int max = userUID_list_exclude_friend.size();
        int[] mark = {10000, 1010, 10500};
        if (userUID_list_exclude_friend.size() == 1) {
            mark[0] = 0;
            mark[1] = 0;
            mark[2] = 0;
        } else if (userUID_list_exclude_friend.size() == 2) {
            mark[0] = 0;
            mark[1] = 1;
            mark[2] = 1;
        } else if (userUID_list_exclude_friend.size() >= 3) {
            for (int j = 0; j < 3; j++) {
                int Random = (int) (Math.random() * (max - min) + min);
                Log.e("Random_number", Random + "");
                mark[j] = Random;
                if (mark[0] == mark[1]) {
                    j = j - 1;
                }
                if (mark[0] == mark[2]) {
                    j = j - 1;
                }
                if (mark[1] == mark[2]) {
                    j = j - 1;
                }
            }
        }
        Log.e("max: ", max + "");
        for (int i = 0; i < 3; i++) {
            recommand_list.add(userUID_list_exclude_friend.get(mark[i]));
        }
        myRef_User.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (int i = 0; i < 3; i++) {
                    String recommand_user_image = dataSnapshot.child(recommand_list.get(i)).child("基本資料").child("大頭照").getValue(String.class);
                    String recommand_user_name = dataSnapshot.child(recommand_list.get(i)).child("基本資料").child("暱稱").getValue(String.class);
                    recommand_image_list.add(recommand_user_image);
                    recommand_name_list.add(recommand_user_name);
                    Log.e("recommand_image_list ADD : ", recommand_user_image);
                }
                mProgressDialog.dismiss();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}
