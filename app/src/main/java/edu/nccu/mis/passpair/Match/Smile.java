package edu.nccu.mis.passpair.Match;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import edu.nccu.mis.passpair.R;

import static android.content.ContentValues.TAG;

public class Smile extends AppCompatActivity {
    private ImageView right, left,image;

    TextView txtMatch,name;
    Button send;
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference myRef_Lovehouse = database.getReference().child("Match").child("Lovehouse");
    DatabaseReference myRef_User= database.getReference().child("User");

    int[][] data;
    String UID;
    ArrayList<String> recommand_list = new ArrayList<String>();
    ArrayList<String> recommand_image_list = new ArrayList<String>();
    ArrayList<String> recommand_name_list = new ArrayList<>();
    int[] match_percent_list = {1000,1000,1000};
    boolean[] is_send_list = {false,false,false};
    int position = 0;
    ProgressDialog mProgressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_match_smile);
        right = (ImageView) findViewById(R.id.smile_right);
        left = (ImageView) findViewById(R.id.smile_left);
        image = (ImageView) findViewById(R.id.smile_photo);
        name = (TextView) findViewById(R.id.smile_name);
        send = (Button) findViewById(R.id.smile_send);
        mProgressDialog = new ProgressDialog(this);

        Intent intent = this.getIntent();
        Bundle bundle = intent.getExtras();
        UID = bundle.getString("UID");
        recommand_list = bundle.getStringArrayList("recommand_list");
        recommand_image_list = bundle.getStringArrayList("recommand_image_list");
        recommand_name_list = bundle.getStringArrayList("recommand_name_list");

        Uri uri = Uri.parse(recommand_image_list.get(position));
        Picasso.with(getApplicationContext()).load(uri).into(image);
        name.setText(recommand_name_list.get(position));

        txtMatch = (TextView)findViewById(R.id.smile_match);
        matchSmileData(recommand_list.get(position));

        right.setOnClickListener(btn_listener);
        left.setOnClickListener(btn_listener);
        send.setOnClickListener(send_listener);
    }

    private ImageView.OnClickListener btn_listener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            switch (view.getId()){
                case R.id.smile_left:{
                    if (position ==0){
                        position = 2;
                    }else {
                        position = position -1;
                    }
                    setUI();
                }
                case R.id.smile_right:{
                    if (position ==2){
                        position = 0;
                    }else {
                        position = position + 1;
                    }
                    setUI();
                }
            }
        }
    };

    private void setUI(){
        if (match_percent_list[position] == 1000){
            matchSmileData(recommand_list.get(position));
            Uri uri = Uri.parse(recommand_image_list.get(position));
            Picasso.with(getApplicationContext()).load(uri).into(image);
        }else {
            Uri uri = Uri.parse(recommand_image_list.get(position));
            Picasso.with(getApplicationContext()).load(uri).into(image);
            txtMatch.setText("契合度："+match_percent_list[position]+"%");
            name.setText(recommand_name_list.get(position));
        }
    }

    private Button.OnClickListener send_listener = new Button.OnClickListener() {
        @Override
        public void onClick(View view) {
            if (is_send_list[position] == false){
                myRef_User.child(recommand_list.get(position)).child("好友邀請").child(UID).setValue("微笑契合度推薦").addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Toast.makeText(getApplicationContext(),"已送出好友邀請",Toast.LENGTH_SHORT).show();
                        is_send_list[position] = true;
                    }
                });
            }else {
                Toast.makeText(getApplicationContext(),"已在邀請名單內",Toast.LENGTH_SHORT).show();
            }
        }
    };

    private void matchSmileData(final String match_ID){
        mProgressDialog.setMessage("正在計算中...");
        mProgressDialog.show();
        final String[] names = {UID, match_ID};
        myRef_Lovehouse.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                int dataLength;
                int matchPoint = 0;
                //讀取user1以及user2的微笑指數資料
                int t = 0;
                for (String name: names) {
                    //共讀取兩個人
                    if (dataSnapshot.hasChild(name)){
                        Long n = dataSnapshot.child(name).getChildrenCount();
                        int n_int = n.intValue();
                        data = new int[names.length][n_int];
                        Log.d("n大小", String.valueOf(n));
                        for (int i=0;i<n_int;i++) {
                            String data_string= dataSnapshot.child(name).child(String.valueOf(i)).child("Data").getValue(String.class);
                            int data_int = 0;
                            if (data_string.toLowerCase().indexOf("e") != -1){
                                data_int = 0;
                            }else if (data_string.toLowerCase().indexOf(".") != -1){
                                String data_string_cut = data_string.substring(0,data_string.indexOf("."));
                                data_int = Integer.parseInt(data_string_cut);
                            }else {
                                data_int = Integer.parseInt(data_string);
                            }
                            data[t][i] = data_int;
                            Log.d("次數","第"+i+"次跑");
                            Log.e("data",data_string);
                        }
                    }
                    t = t +1;
                    Log.d("次數","第"+name+"次跑");
                }

                if (data[0].length > 0 && data[1].length >0){
                    if (data[0].length>=data[1].length){
                        dataLength = data[1].length;
                    }else {
                        dataLength = data[0].length;
                    }

                    for (int i = 0;i<dataLength;i++){
                        int score = Math.abs(data[0][i]-data[1][i]);
                        if (score < 3.5){
                            matchPoint++;
                        }
                    }
                    if (matchPoint > 100){
                        matchPoint = 100;
                    }
                    if (matchPoint < 60){
                        int Random = (int) (Math.random() * (70 - 60) + 60);
                        matchPoint = Random;
                    }
                    Log.d("原始分數", String.valueOf(matchPoint));
                    txtMatch.setText("契合度："+matchPoint+"%");
                    name.setText(recommand_name_list.get(position));
                    match_percent_list[position] = matchPoint;
                    mProgressDialog.dismiss();
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                Log.w(TAG,"Failed to read value.", error.toException());
            }
        });
    }
}
