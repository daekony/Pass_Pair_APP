package edu.nccu.mis.passpair.PassIndex;


import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
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

import java.text.SimpleDateFormat;
import java.util.Date;

import edu.nccu.mis.passpair.R;


public class Second extends AppCompatActivity {

    private ImageView imgPhoto, imgBHeart, imgBHeart2, imgBHeart3, imgBHeart4, imgBHeart5;
    private Button btnSend, btnPrev;
    private TextView textView, textView2;

    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference Ref_User = database.getReference().child("User");
    String ID, UID,time_str;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);

        getDateInfo();

        // 取得介面元件
        textView = (TextView) findViewById(R.id.second_name);
        btnSend = (Button) findViewById(R.id.second_send);
        btnPrev = (Button) findViewById(R.id.second_prev);
        imgPhoto = (ImageView) findViewById(R.id.imgPhoto);
        imgBHeart = (ImageView) findViewById(R.id.imgBHeart);
        imgBHeart2 = (ImageView) findViewById(R.id.imgBHeart2);
        imgBHeart3 = (ImageView) findViewById(R.id.imgBHeart3);
        imgBHeart4 = (ImageView) findViewById(R.id.imgBHeart4);
        imgBHeart5 = (ImageView) findViewById(R.id.imgBHeart5);
        textView2 = (TextView) findViewById(R.id.textView2);

        textView2.setVisibility(View.GONE);
        btnSend.setVisibility(View.GONE);


        // 設定 button 元件 Click 事件的 listener
        btnPrev.setOnClickListener(btnPrevListener);
        btnSend.setOnClickListener(btnSendListener);


        // 設定 TextView 的資料來源
        Intent intent = this.getIntent();
        Bundle bundle = intent.getExtras();
        final String name = bundle.getString("NAME");
        UID = bundle.getString("UID");
        ID = bundle.getString("ID");
        textView.setText(name);


        //讀取資料
        final DatabaseReference reference_contacts = FirebaseDatabase.getInstance().getReference("User");
        reference_contacts.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String image = dataSnapshot.child(ID).child("基本資料").child("大頭照").getValue(String.class);
                Uri uri = Uri.parse(image);
                Picasso.with(getApplicationContext()).load(uri).into(imgPhoto);
                if (dataSnapshot.child(UID).hasChild("Map資料") && dataSnapshot.child(UID).child("Map資料").hasChild(ID)) {
                    int i = 0;
                    for (DataSnapshot ds : dataSnapshot.child(UID).child("Map資料").child(ID).getChildren()){
                        String meet_date = ds.getValue(String.class);
                        if (TextUtils.equals(meet_date,time_str)){
                            i = i +1;
                        }
                    }
                    if (i  == 0){
                        reference_contacts.child(UID).child("Map資料").child(ID).push().setValue(time_str);
                    }
                    Long heart = dataSnapshot.child(UID).child("Map資料").child(ID).getChildrenCount();
                    int heart_int = heart.intValue();
                    if (heart_int > 0) {
                        switch (heart_int) {
                            case 1:
                                imgBHeart.setImageResource(R.drawable.rheart);
                                btnSend.setVisibility(View.GONE);
                                textView2.setVisibility(View.VISIBLE);
                                textView2.setText("加油！還差4個愛心就可以解鎖交友邀請＞＜");

                                break;
                            case 2:
                                imgBHeart.setImageResource(R.drawable.rheart);
                                imgBHeart2.setImageResource(R.drawable.rheart);
                                btnSend.setVisibility(View.GONE);
                                textView2.setVisibility(View.VISIBLE);
                                textView2.setText("加油！還差3個愛心就可以解鎖交友邀請＞＜");
                                break;
                            case 3:
                                imgBHeart.setImageResource(R.drawable.rheart);
                                imgBHeart2.setImageResource(R.drawable.rheart);
                                imgBHeart3.setImageResource(R.drawable.rheart);
                                btnSend.setVisibility(View.GONE);
                                textView2.setVisibility(View.VISIBLE);
                                textView2.setText("加油！還差2個愛心就可以解鎖交友邀請＞＜");

                                break;
                            case 4:
                                imgBHeart.setImageResource(R.drawable.rheart);
                                imgBHeart2.setImageResource(R.drawable.rheart);
                                imgBHeart3.setImageResource(R.drawable.rheart);
                                imgBHeart4.setImageResource(R.drawable.rheart);
                                btnSend.setVisibility(View.GONE);
                                textView2.setVisibility(View.VISIBLE);
                                textView2.setText("加油！還差1個愛心就可以解鎖交友邀請＞＜");
                                break;
                            case 5:
                                imgBHeart.setImageResource(R.drawable.rheart);
                                imgBHeart2.setImageResource(R.drawable.rheart);
                                imgBHeart3.setImageResource(R.drawable.rheart);
                                imgBHeart4.setImageResource(R.drawable.rheart);
                                imgBHeart5.setImageResource(R.drawable.rheart);
                                textView2.setVisibility(View.GONE);
                                btnSend.setVisibility(View.VISIBLE);
                                break;
                            default:
                                imgBHeart.setImageResource(R.drawable.rheart);
                                imgBHeart2.setImageResource(R.drawable.rheart);
                                imgBHeart3.setImageResource(R.drawable.rheart);
                                imgBHeart4.setImageResource(R.drawable.rheart);
                                imgBHeart5.setImageResource(R.drawable.rheart);
                                textView2.setVisibility(View.GONE);
                                btnSend.setVisibility(View.VISIBLE);
                                break;
                        }
                    }
                }else {
                    reference_contacts.child(UID).child("Map資料").child(ID).push().setValue(time_str);
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {

            }
        });
    }

    // 定義 btnPrev 按鈕的onClick()方法
    private Button.OnClickListener btnPrevListener = new Button.OnClickListener() {
        public void onClick(View v) {
            Intent intent = new Intent();
            Bundle bundle = new Bundle();
            bundle.putString("UID", UID);
            intent.putExtras(bundle);
            intent.setClass(Second.this, MapsActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        }
    };

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent();
        Bundle bundle = new Bundle();
        bundle.putString("UID", UID);
        intent.putExtras(bundle);
        intent.setClass(Second.this, MapsActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }

    // 定義 btnNext 按鈕的onClick() 方法
    private Button.OnClickListener btnSendListener = new Button.OnClickListener() {
        public void onClick(View v) {

            new AlertDialog.Builder(Second.this)
                    .setTitle("確認視窗")
                    .setIcon(R.mipmap.ic_passpair_launch)
                    .setMessage("確定要送出交友邀請嗎？")
                    .setPositiveButton("確定", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialoginterface, int i) {
                            Ref_User.child(ID).child("好友邀請").child(UID).setValue("擦肩次數推薦").addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    Toast.makeText(getApplicationContext(), "已送出好友邀請", Toast.LENGTH_SHORT).show();
                                    Intent intent = new Intent();
                                    Bundle bundle = new Bundle();
                                    bundle.putString("UID", UID);
                                    intent.putExtras(bundle);
                                    intent.setClass(Second.this, MapsActivity.class);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                    startActivity(intent);
                                }
                            });
                        }
                    })
                    .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialoginterface, int i) {

                        }
                    })
                    .show();
        }
    };
    private void getDateInfo() {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy/MM/dd");
        Date curDate = new Date(System.currentTimeMillis()); // 獲取當前時間
        time_str = formatter.format(curDate);
    }
}
