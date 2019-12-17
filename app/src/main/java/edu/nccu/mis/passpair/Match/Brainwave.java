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
import java.util.List;

import edu.nccu.mis.passpair.R;

import static android.content.ContentValues.TAG;

public class Brainwave extends AppCompatActivity {
    private ImageView right, left,image;

    TextView txtMatch,name;
    Button send;
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference myRef_EEG = database.getReference().child("Match").child("EEG");
    DatabaseReference myRef_User = database.getReference().child("User");
    Long n;
    int[] lowA, highA, lowB, highB, theTa;
    int[][] data;
    String UID;
    ArrayList<String> recommand_list = new ArrayList<String>();
    ArrayList<String> recommand_image_list = new ArrayList<String>();
    ArrayList<String> recommand_name_list = new ArrayList<>();
    boolean[] is_send_list = {false,false,false};
    int[] match_percent_list = {1000,1000,1000};
    Double match_percent = 0.0;
    int position = 0;
    ProgressDialog mProgressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_match_brainwave);
        mProgressDialog  = new ProgressDialog(this);
        txtMatch = (TextView) findViewById(R.id.brainwave_match);
        right = (ImageView) findViewById(R.id.brainwave_right);
        left = (ImageView) findViewById(R.id.brainwave_left);
        image = (ImageView) findViewById(R.id.brainwave_photo);
        name = (TextView) findViewById(R.id.brainwave_name);
        send = (Button) findViewById(R.id.brainwave_send);

        Intent intent = this.getIntent();
        Bundle bundle = intent.getExtras();
        UID = bundle.getString("UID");
        recommand_list = bundle.getStringArrayList("recommand_list");
        recommand_image_list = bundle.getStringArrayList("recommand_image_list");
        recommand_name_list = bundle.getStringArrayList("recommand_name_list");

        Uri uri = Uri.parse(recommand_image_list.get(position));
        Picasso.with(getApplicationContext()).load(uri).into(image);
        name.setText(recommand_name_list.get(position));

        matchBrainData(recommand_list.get(position));

        right.setOnClickListener(btn_listener);
        left.setOnClickListener(btn_listener);
        send.setOnClickListener(send_listener);
    }
    private ImageView.OnClickListener btn_listener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            switch (view.getId()){
                case R.id.brainwave_left:{
                    if (position ==0){
                        position = 2;
                    }else {
                        position = position -1;
                    }
                    setUI();
                }
                case R.id.brainwave_right:{
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

    private Button.OnClickListener send_listener = new Button.OnClickListener() {
        @Override
        public void onClick(View view) {
            if (is_send_list[position] == false){
                myRef_User.child(recommand_list.get(position)).child("好友邀請").child(UID).setValue("腦波契合度推薦").addOnCompleteListener(new OnCompleteListener<Void>() {
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

    private void setUI(){
        if (match_percent_list[position] == 1000){
            matchBrainData(recommand_list.get(position));
            Uri uri = Uri.parse(recommand_image_list.get(position));
            Picasso.with(getApplicationContext()).load(uri).into(image);
        }else {
            Uri uri = Uri.parse(recommand_image_list.get(position));
            Picasso.with(getApplicationContext()).load(uri).into(image);
            txtMatch.setText("契合度："+match_percent_list[position]+"%");
            name.setText(recommand_name_list.get(position));
        }
    }
    private void matchBrainData(String match_ID){
        mProgressDialog.setMessage("正在計算中...");
        mProgressDialog.show();
        final String[] names = {UID,match_ID};
        myRef_EEG.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                int dataLength;
                double matchPoint = 0.0;
                int t = 0;
                for (String name:names) {
                    if (dataSnapshot.hasChild(name)){
                        n = dataSnapshot.child(name).getChildrenCount();
                        int n_int = n.intValue();
                        lowA = new int[n_int];
                        highA = new int[n_int];
                        lowB = new int[n_int];
                        highB = new int[n_int];
                        theTa = new int[n_int];
                        data = new int[2][n_int];
                        Log.d("n大小", String.valueOf(n));
                        for (int i=0;i < n_int;i++) {
                            String lowA_str = dataSnapshot.child(name).child(String.valueOf(i)).child("Low Alpha").getValue(String.class);
                            String highA_str = dataSnapshot.child(name).child(String.valueOf(i)).child("High Alpha").getValue(String.class);
                            String lowB_str = dataSnapshot.child(name).child(String.valueOf(i)).child("Low Beta").getValue(String.class);
                            String highB_str = dataSnapshot.child(name).child(String.valueOf(i)).child("High Beta").getValue(String.class);
                            String theTa_str = dataSnapshot.child(name).child(String.valueOf(i)).child("Theta").getValue(String.class);
                            lowA[i] = Integer.parseInt(lowA_str);
                            highA[i] = Integer.parseInt(highA_str);
                            lowB[i] = Integer.parseInt(lowB_str);
                            highB[i] = Integer.parseInt(highB_str);;
                            theTa[i] = Integer.parseInt(theTa_str);
                            Log.e("lowA_str",lowA_str);
                            data[t][i] = lowA[i]+highA[i]-lowB[i]-highB[i]-theTa[i];
                            Log.d("次數","第i"+i+"次跑");
                        }
                    }
                    Log.d("次數","第"+name+"次跑");
                    t = t +1;
                }
                if (data[0].length > 0 && data[1].length >0){
                    if (data[0].length>=data[1].length){
                        dataLength = data[1].length;
                    }else {
                        dataLength = data[0].length;
                    }
                    for (int i = 0;i<dataLength;i++){
                        int score = Math.abs(data[0][i]-data[1][i]);
                        if (score < 25000){
                            matchPoint++;
                        }
                    }
                    match_percent = (matchPoint/n.intValue())*100;
                    if (match_percent < 60){
                        double Random = (Math.random() * (70 - 60) + 60);
                        match_percent = Random;
                    }
                    Log.d("原始分數", String.valueOf(match_percent));
                    txtMatch.setText("契合度："+(int)Math.ceil(match_percent/10)*10+"%");
                    name.setText(recommand_name_list.get(position));
                    match_percent_list[position] = ((int)Math.ceil(match_percent/10)*10);
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
