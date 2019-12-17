package edu.nccu.mis.passpair.Gift;

import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
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
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import edu.nccu.mis.passpair.Gift.Adapter.RecyclerViewAdapterSend;
import edu.nccu.mis.passpair.Gift.Common.GiftInfo;
import edu.nccu.mis.passpair.Gift.Common.ItemObject;
import edu.nccu.mis.passpair.R;

public class GiftSending extends AppCompatActivity implements RecyclerViewAdapterSend.ItemClickCallback {
    private GridLayoutManager lLayout;
    private ImageView F_Photo;
    private TextView Friend,gift_send_userpoint;
    public static TextView gift_send_giftpoint;
    private Button Send;

    public static List<ItemObject> rowListItem;
    private RecyclerViewAdapterSend rcAdapter;
    private RecyclerView rView;

    private String uid = "";
    private String fid = "";
    private String fname = "";
    private Long User_Rate = new Long(0);
    private String Friend_Photo;
    private int[] g_photo = new int[]{R.drawable.glass, R.drawable.bouquet, R.drawable.teddy_bear, R.drawable.ice_cream, R.drawable.sweater, R.drawable.gift, R.drawable.purse, R.drawable.bicycle, R.drawable.motorbiking};
    private ArrayList<String> g_type = new ArrayList<String>();
    private String[] level = new String[]{"初心者", "新人", "黃金", "白金", "鑽石"};
    private String User_Level;
    private String time_str,time_str_task;
    private int send_count = 0;

    private Long U_Rate = new Long(0);
    private int point_cost = 5;
    public static int user_cost_point = 0;

    public GiftSending() {

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gift_sending);
        getDateInfo();

        F_Photo = (ImageView) findViewById(R.id.friend_photo);
        Friend = (TextView) findViewById(R.id.friend);
        gift_send_userpoint = (TextView) findViewById(R.id.gift_send_userpoint);
        gift_send_giftpoint = (TextView) findViewById(R.id.gift_send_giftpoint);
        Send = (Button) findViewById(R.id.send);

        Intent intent = this.getIntent();
        final Bundle bundle = intent.getExtras();
        uid = bundle.getString("MyUID");
        fid = bundle.getString("FID");
        fname = bundle.getString("Fname");

        gift_send_giftpoint.setText("已花費的積分: 0 分");
        //function to send
        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        final DatabaseReference myRef_gift = database.getReference("Gift");
        final DatabaseReference myRef_user = database.getReference("User");

        myRef_user.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                U_Rate = dataSnapshot.child(uid).child("基本資料").child("積分").getValue(Long.class);
                fname = dataSnapshot.child(fid).child("基本資料").child("暱稱").getValue(String.class);
                Long Friend_point;
                Friend_Photo = dataSnapshot.child(fid).child("基本資料").child("大頭照").getValue(String.class);
                Friend_point = dataSnapshot.child(fid).child("基本資料").child("積分").getValue(Long.class);
                final int Friend_point_int = Friend_point.intValue();
                gift_send_userpoint.setText("您的積分: " + U_Rate + "分");
                User_Rate = U_Rate;
                Uri f_photo = Uri.parse(Friend_Photo);
                Log.e("Photo url", f_photo + "");
                Picasso.with(getApplicationContext())
                        .load(f_photo)
                        .into(F_Photo);
                // F_Photo.setImageURI(f_photo);
                Friend.setText("你想送給" + fname + "什麼禮物呢？");
                Send.setText("送出");
                Send.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        final int U_Rate_int = U_Rate.intValue();
                        final ArrayList<String> allGiftSent = getAllGiftSent();
                        if (user_cost_point <= U_Rate_int){
                            for (int i = 0; i < allGiftSent.size(); i++) {
                                send_count = send_count + 1;
                                GiftInfo new_gift = new GiftInfo(fid, allGiftSent.get(i), uid, time_str);
                                myRef_gift.push().setValue(new_gift).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (send_count == allGiftSent.size()) {
                                            myRef_user.child(uid).child("任務").child(time_str_task).child("贈送禮物給好友").setValue(1);
                                            myRef_user.child(fid).child("基本資料").child("積分").setValue(Friend_point_int + user_cost_point);
                                            myRef_user.child(uid).child("基本資料").child("積分").setValue(U_Rate_int - user_cost_point).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    g_type.clear();
                                                    user_cost_point = 0;
                                                    Toast.makeText(getApplicationContext(), "已送出!", Toast.LENGTH_SHORT).show();
                                                    finish();
                                                }
                                            });
                                        }
                                    }
                                });
                            }
                        }else {
                            Toast.makeText(getApplicationContext(), "您的積分不足!!", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
                if (U_Rate != 0) {
                    //set up recycler view
                    rowListItem = getAllItemList();
                    lLayout = new GridLayoutManager(GiftSending.this, 3);

                    rView = (RecyclerView) findViewById(R.id.recycler_view);
                    rView.setHasFixedSize(true);
                    rView.setLayoutManager(lLayout);

                    rcAdapter = new RecyclerViewAdapterSend(GiftSending.this, rowListItem);
                    rView.setAdapter(rcAdapter);

                    rcAdapter.setItemClickCallback(GiftSending.this);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private List<ItemObject> getAllItemList() {
        List<ItemObject> allItems = new ArrayList<ItemObject>();
        if (User_Rate < 100) {
            allItems.add(new ItemObject("Glass", R.drawable.glass_locked));
            allItems.add(new ItemObject("Bouquet", R.drawable.bouquet_locked));
            allItems.add(new ItemObject("Teddy Bear", R.drawable.teddy_bear_locked));
            allItems.add(new ItemObject("Ice Cream", R.drawable.ice_cream_locked));
            allItems.add(new ItemObject("Sweater", R.drawable.sweater_locked));
            allItems.add(new ItemObject("Gift", R.drawable.gift_locked));
            allItems.add(new ItemObject("Purse", R.drawable.purse_locked));
            allItems.add(new ItemObject("Bicycle", R.drawable.bicycle_locked));
            allItems.add(new ItemObject("Scooter", R.drawable.motorbiking_locked));
        } else if (User_Rate >= 100 && User_Rate < 200) {
            allItems.add(new ItemObject("Glass", g_photo[0]));
            allItems.add(new ItemObject("Bouquet", R.drawable.bouquet_locked));
            allItems.add(new ItemObject("Teddy Bear", R.drawable.teddy_bear_locked));
            allItems.add(new ItemObject("Ice Cream", R.drawable.ice_cream_locked));
            allItems.add(new ItemObject("Sweater", R.drawable.sweater_locked));
            allItems.add(new ItemObject("Gift", R.drawable.gift_locked));
            allItems.add(new ItemObject("Purse", R.drawable.purse_locked));
            allItems.add(new ItemObject("Bicycle", R.drawable.bicycle_locked));
            allItems.add(new ItemObject("Scooter", R.drawable.motorbiking_locked));
        } else if (User_Rate >= 200 && User_Rate < 300) {
            allItems.add(new ItemObject("Glass", g_photo[0]));
            allItems.add(new ItemObject("Bouquet", g_photo[1]));
            allItems.add(new ItemObject("Teddy Bear", R.drawable.teddy_bear_locked));
            allItems.add(new ItemObject("Ice Cream", R.drawable.ice_cream_locked));
            allItems.add(new ItemObject("Sweater", R.drawable.sweater_locked));
            allItems.add(new ItemObject("Gift", R.drawable.gift_locked));
            allItems.add(new ItemObject("Purse", R.drawable.purse_locked));
            allItems.add(new ItemObject("Bicycle", R.drawable.bicycle_locked));
            allItems.add(new ItemObject("Scooter", R.drawable.motorbiking_locked));
        } else if (User_Rate >= 300 && User_Rate < 400) {
            allItems.add(new ItemObject("Glass", g_photo[0]));
            allItems.add(new ItemObject("Bouquet", g_photo[1]));
            allItems.add(new ItemObject("Teddy Bear", g_photo[2]));
            allItems.add(new ItemObject("Ice Cream", R.drawable.ice_cream_locked));
            allItems.add(new ItemObject("Sweater", R.drawable.sweater_locked));
            allItems.add(new ItemObject("Gift", R.drawable.gift_locked));
            allItems.add(new ItemObject("Purse", R.drawable.purse_locked));
            allItems.add(new ItemObject("Bicycle", R.drawable.bicycle_locked));
            allItems.add(new ItemObject("Scooter", R.drawable.motorbiking_locked));
        } else if (User_Rate >= 400 && User_Rate < 500) {
            allItems.add(new ItemObject("Glass", g_photo[0]));
            allItems.add(new ItemObject("Bouquet", g_photo[1]));
            allItems.add(new ItemObject("Teddy Bear", g_photo[2]));
            allItems.add(new ItemObject("Ice Cream", g_photo[3]));
            allItems.add(new ItemObject("Sweater", R.drawable.sweater_locked));
            allItems.add(new ItemObject("Gift", R.drawable.gift_locked));
            allItems.add(new ItemObject("Purse", R.drawable.purse_locked));
            allItems.add(new ItemObject("Bicycle", R.drawable.bicycle_locked));
            allItems.add(new ItemObject("Scooter", R.drawable.motorbiking_locked));
        } else if (User_Rate >= 500 && User_Rate < 600) {
            allItems.add(new ItemObject("Glass", g_photo[0]));
            allItems.add(new ItemObject("Bouquet", g_photo[1]));
            allItems.add(new ItemObject("Teddy Bear", g_photo[2]));
            allItems.add(new ItemObject("Ice Cream", g_photo[3]));
            allItems.add(new ItemObject("Sweater", g_photo[4]));
            allItems.add(new ItemObject("Gift", R.drawable.gift_locked));
            allItems.add(new ItemObject("Purse", R.drawable.purse_locked));
            allItems.add(new ItemObject("Bicycle", R.drawable.bicycle_locked));
            allItems.add(new ItemObject("Scooter", R.drawable.motorbiking_locked));
        } else if (User_Rate >= 600 && User_Rate < 700) {
            allItems.add(new ItemObject("Glass", g_photo[0]));
            allItems.add(new ItemObject("Bouquet", g_photo[1]));
            allItems.add(new ItemObject("Teddy Bear", g_photo[2]));
            allItems.add(new ItemObject("Ice Cream", g_photo[3]));
            allItems.add(new ItemObject("Sweater", g_photo[4]));
            allItems.add(new ItemObject("Gift", g_photo[5]));
            allItems.add(new ItemObject("Purse", R.drawable.purse_locked));
            allItems.add(new ItemObject("Bicycle", R.drawable.bicycle_locked));
            allItems.add(new ItemObject("Scooter", R.drawable.motorbiking_locked));
        } else if (User_Rate >= 700 && User_Rate < 800) {
            allItems.add(new ItemObject("Glass", g_photo[0]));
            allItems.add(new ItemObject("Bouquet", g_photo[1]));
            allItems.add(new ItemObject("Teddy Bear", g_photo[2]));
            allItems.add(new ItemObject("Ice Cream", g_photo[3]));
            allItems.add(new ItemObject("Sweater", g_photo[4]));
            allItems.add(new ItemObject("Gift", g_photo[5]));
            allItems.add(new ItemObject("Purse", g_photo[6]));
            allItems.add(new ItemObject("Bicycle", R.drawable.bicycle_locked));
            allItems.add(new ItemObject("Scooter", R.drawable.motorbiking_locked));
        } else if (User_Rate >= 800 && User_Rate < 900) {
            allItems.add(new ItemObject("Glass", g_photo[0]));
            allItems.add(new ItemObject("Bouquet", g_photo[1]));
            allItems.add(new ItemObject("Teddy Bear", g_photo[2]));
            allItems.add(new ItemObject("Ice Cream", g_photo[3]));
            allItems.add(new ItemObject("Sweater", g_photo[4]));
            allItems.add(new ItemObject("Gift", g_photo[5]));
            allItems.add(new ItemObject("Purse", g_photo[6]));
            allItems.add(new ItemObject("Bicycle", g_photo[7]));
            allItems.add(new ItemObject("Scooter", R.drawable.motorbiking_locked));
        } else if (User_Rate >= 900) {
            allItems.add(new ItemObject("Glass", g_photo[0]));
            allItems.add(new ItemObject("Bouquet", g_photo[1]));
            allItems.add(new ItemObject("Teddy Bear", g_photo[2]));
            allItems.add(new ItemObject("Ice Cream", g_photo[3]));
            allItems.add(new ItemObject("Sweater", g_photo[4]));
            allItems.add(new ItemObject("Gift", g_photo[5]));
            allItems.add(new ItemObject("Purse", g_photo[6]));
            allItems.add(new ItemObject("Bicycle", g_photo[7]));
            allItems.add(new ItemObject("Scooter", g_photo[8]));
        }

        return allItems;
    }


    @Override
    public void onItemClick(int p) {
        rcAdapter.setListData(rowListItem);
        rcAdapter.notifyDataSetChanged();

    }

    private ArrayList<String> getAllGiftSent() {
        for (int x = 0; x < rowListItem.size(); x++) {
            if (rowListItem.get(x).isSent()) {
                g_type.add(String.valueOf(x));
            }
        }
        return g_type;
    }

    private void getDateInfo() {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy/MM/dd");
        Date curDate = new Date(System.currentTimeMillis()); // 獲取當前時間
        time_str = formatter.format(curDate);
        SimpleDateFormat format_2 = new SimpleDateFormat("yyyyMMdd");
        time_str_task = format_2.format(curDate);
    }
}

