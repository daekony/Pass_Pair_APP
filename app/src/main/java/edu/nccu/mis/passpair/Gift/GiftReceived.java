package edu.nccu.mis.passpair.Gift;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

import edu.nccu.mis.passpair.Gift.Adapter.RecyclerViewAdapterRec;
import edu.nccu.mis.passpair.Gift.Common.GiftInfo;
import edu.nccu.mis.passpair.Gift.Common.ItemObject;
import edu.nccu.mis.passpair.R;

public class GiftReceived extends Activity {
    private GridLayoutManager lLayout;
    private ImageView user_photo;
    private TextView user_level;
    private TextView user_rate;
    private TextView gift_rec_none;

    private String uname;
    private String uid;
    private int User_Rate;
    private String User_Photo;
    private ArrayList<GiftInfo> gift_list = new ArrayList<GiftInfo>();
    private ArrayList<GiftInfo> gift_list_filtered = new ArrayList<GiftInfo>();
    private int[] g_photo = new int[]{R.drawable.glass, R.drawable.bouquet, R.drawable.teddy_bear, R.drawable.ice_cream, R.drawable.sweater, R.drawable.gift, R.drawable.purse, R.drawable.bicycle, R.drawable.motorbiking};
    private String[] level = new String[]{"初心者","新人","黃金","白金","鑽石"};
    private String User_Level;
    private DatabaseReference myRef = FirebaseDatabase.getInstance().getReference();
    private DatabaseReference Ref_user = myRef.child("User");

    private ArrayList<ItemObject> allItems = new ArrayList<>();

    public GiftReceived() {
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gift_received);

        user_photo = (ImageView)findViewById(R.id.user_photo);
        user_level = (TextView)findViewById(R.id.user_level);
        user_rate = (TextView)findViewById(R.id.user_rate);
        gift_rec_none = (TextView)findViewById(R.id.gift_rec_none);

        Intent intent = this.getIntent();
        Bundle bundle = intent.getExtras();
        uid = bundle.getString("UID");

        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef_gift = database.getReference("Gift");
        DatabaseReference myRef_user = database.getReference("User");

        // Read from the database
        myRef_gift.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                for (DataSnapshot ds : dataSnapshot.getChildren()){
                    GiftInfo gift = ds.getValue(GiftInfo.class);
                    gift_list.add(gift);
                    Log.e("giftinfo",gift.getRuid());
                }
                for (int i=0; i<gift_list.size(); i++) {
                    if (gift_list.get(i).getRuid().equals(uid)) {
                        Log.e("filtered","get");
                        gift_list_filtered.add(gift_list.get(i));
                    }
                }

                getAllItemList();

                Log.e("row",String.valueOf(allItems.isEmpty()));
                Log.e("gift_send",String.valueOf(gift_list_filtered.isEmpty()));
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
            }
        });
        //User_level
        myRef_user.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Long U_Rate = (Long)dataSnapshot.child(uid).child("基本資料").child("積分").getValue();
                String U_Photo = dataSnapshot.child(uid).child("基本資料").child("大頭照").getValue().toString();

                User_Rate = U_Rate.intValue();
                User_Photo = U_Photo;

                if (User_Rate < 100){
                    User_Level = level[0];
                } else if (User_Rate >= 100 && User_Rate < 300){
                    User_Level = level[1];
                } else if (User_Rate >= 300 && User_Rate < 700){
                    User_Level = level[2];
                } else if (User_Rate >= 700 && User_Rate < 1500){
                    User_Level = level[3];
                } else if (User_Rate >= 1500){
                    User_Level = level[4];
                }

                Uri u_photo = Uri.parse(User_Photo);
                Picasso.with(getApplication())
                        .load(u_photo)
                        .into(user_photo);
                user_level.setText(User_Level);
                user_rate.setText("目前積分：" + User_Rate);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }


    private void getAllItemList(){
//        final List<ItemObject> allItems = new ArrayList<ItemObject>();
//        final ArrayList<String> user_name_list = new ArrayList<String>();
        Ref_user.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.e("getAllItemList","");
                for (int i=0; i<gift_list_filtered.size(); i++) {
                    final int finalI = i;
                    Log.e("getAllItemList For","" + i);
                    String user_name = dataSnapshot.child(gift_list_filtered.get(finalI).getGuid()).child("基本資料").child("暱稱").getValue(String.class);
                    allItems.add(new ItemObject(user_name, g_photo[Integer.parseInt(gift_list_filtered.get(i).getGtype())],gift_list_filtered.get(i).getTime()));
                    Log.e("getAllItemList For","" + user_name);
                    if (!allItems.isEmpty()) {
                        Log.e("yes","yes");
                        lLayout = new GridLayoutManager(GiftReceived.this, 3);
                        RecyclerView rView = (RecyclerView) findViewById(R.id.recycler_view);
                        rView.setHasFixedSize(true);
                        rView.setLayoutManager(lLayout);

                        RecyclerViewAdapterRec rcAdapter = new RecyclerViewAdapterRec(GiftReceived.this, allItems);
                        rView.setAdapter(rcAdapter);
                        rcAdapter.notifyDataSetChanged();
                    } else {
                        // 設定textView = "目前尚未收到禮物"
                        Toast.makeText(GiftReceived.this,"尚未收到任何禮物",Toast.LENGTH_SHORT).show();
                        gift_rec_none.setText("尚未收到任何禮物");
                    }
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}
