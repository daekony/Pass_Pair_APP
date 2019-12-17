package edu.nccu.mis.passpair.Task;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;


import edu.nccu.mis.passpair.R;


public class MissionList extends AppCompatActivity {

//    private RecyclerView recyclerView;
    private SimpleAdapter simpleAdapter;
    private ListView listView;
    private int[] complete = {R.drawable.ic_mission_complete,R.drawable.ic_mission_undone};
    private String time_str,uid;
    private DatabaseReference Ref_user = FirebaseDatabase.getInstance().getReference().child("User");
    private ArrayList<String> mission_name = new ArrayList<>();
    private ArrayList<String> missiom_piont = new ArrayList<>();
    private ArrayList<Integer> mission_complete = new ArrayList<Integer>();

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_misson_list);

        getDateInfo();

        Intent intent = this.getIntent();
        final Bundle bundle = intent.getExtras();
        uid = bundle.getString("UID");

        final String[] task = getResources().getStringArray(R.array.task);
        final String[] taskpoint = getResources().getStringArray(R.array.taskpoint);
        listView = (ListView) findViewById(R.id.mission_list);

        for (int i = 0; i < task.length; i++) {
            Log.d("任務", task[i]);
            final int finalI = i;
            Ref_user.child(uid).child("任務").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.hasChild(time_str)){
                        if (dataSnapshot.child(time_str).hasChild(task[finalI])){
                            mission_name.add(task[finalI]);
                            missiom_piont.add(taskpoint[finalI]);
                            mission_complete.add(complete[0]);
                        }else {
                            mission_name.add(task[finalI]);
                            missiom_piont.add(taskpoint[finalI]);
                            mission_complete.add(complete[1]);
                        }
                    }else {
                        mission_name.add(task[finalI]);
                        missiom_piont.add(taskpoint[finalI]);
                        mission_complete.add(complete[1]);
                    }
                    if (mission_complete.size() == 5){
                        MissionAdapter adapter = new MissionAdapter(MissionList.this,mission_name,missiom_piont,mission_complete);
                        listView.setAdapter(adapter);
                        adapter.notifyDataSetChanged();
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }
    }

    private void getDateInfo() {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMdd");
        Date curDate = new Date(System.currentTimeMillis()); // 獲取當前時間
        time_str = formatter.format(curDate);
    }
}
