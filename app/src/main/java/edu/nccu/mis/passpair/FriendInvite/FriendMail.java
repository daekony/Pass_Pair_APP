package edu.nccu.mis.passpair.FriendInvite;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import edu.nccu.mis.passpair.FriendInvite.Adapter.RecAdaperFriend;
import edu.nccu.mis.passpair.Homepage.Fragment.FriendFragment;
import edu.nccu.mis.passpair.R;

public class FriendMail extends AppCompatActivity {

    private String MyUID;
    final ArrayList<String> imglist = new ArrayList<String>();
    final ArrayList<String> namelist = new ArrayList<String>();
    final ArrayList<String> IDlist = new ArrayList<String>();
    final ArrayList<String> reasonlist = new ArrayList<String>();

    RecyclerView recyclerView;
    DatabaseReference Ref_User = FirebaseDatabase.getInstance().getReference().child("User");
    TextView friend_none;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friend_mail);

        Intent intent = this.getIntent();
        Bundle bundle = intent.getExtras();
        MyUID = bundle.getString("UID");

        recyclerView = (RecyclerView) findViewById(R.id.friendmail_recycler);
        friend_none = (TextView) findViewById(R.id.friendmail_none);

        LinearLayoutManager manager = new LinearLayoutManager(FriendMail.this);
        recyclerView.setLayoutManager(manager);
        recyclerView.setHasFixedSize(true);
        recyclerView.setItemViewCacheSize(20);
        recyclerView.setDrawingCacheEnabled(true);
        recyclerView.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);

        Ref_User.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.child(MyUID).hasChild("好友邀請")){
                    friend_none.setText("");
                    Long invitation_count = dataSnapshot.child(MyUID).child("好友邀請").getChildrenCount();
                    int invitation_count_int = invitation_count.intValue();
                    Log.e("invitation_count",String.valueOf(invitation_count));
                    Log.e("invitation_count",String.valueOf(invitation_count_int));
                    if (invitation_count_int > 0 && invitation_count_int >= namelist.size()){
                        friend_none.setText("");
                        for (DataSnapshot ds : dataSnapshot.child(MyUID).child("好友邀請").getChildren()){
                            IDlist.add(ds.getKey());
                            Log.e("IDlist",ds.getKey());
                            String reason = ds.getValue(String.class);
                            reasonlist.add(reason);
                            Log.e("IDlist",ds.getValue(String.class));
                            String user_image = dataSnapshot.child(ds.getKey()).child("基本資料").child("大頭照").getValue(String.class);
                            imglist.add(user_image);
                            Log.e("user_image",user_image);
                            String user_name = dataSnapshot.child(ds.getKey()).child("基本資料").child("暱稱").getValue(String.class);
                            namelist.add(user_name);
                            Log.e("user_name",user_name);
                        }
                        if (namelist.size() >= invitation_count_int){
                            RecAdaperFriend adaperFriend = new RecAdaperFriend(FriendMail.this,imglist,namelist,IDlist,MyUID,reasonlist);
                            recyclerView.setAdapter(adaperFriend);
                            adaperFriend.notifyDataSetChanged();
                        }
                    }else {
                        friend_none.setText("尚未有好友邀請");
                    }
                }else {
                    friend_none.setText("尚未有好友邀請");
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}


