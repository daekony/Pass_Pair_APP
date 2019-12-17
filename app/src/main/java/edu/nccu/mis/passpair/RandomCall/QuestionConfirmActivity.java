package edu.nccu.mis.passpair.RandomCall;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Date;

import edu.nccu.mis.passpair.Homepage.HomePage;
import edu.nccu.mis.passpair.R;

public class QuestionConfirmActivity extends AppCompatActivity {
    private TextView question_confirm;
    private String myid = "";
    private String otherid = "";
    private String nodeid = "";
    private int[] question_switch = {4, 5, 8, 9};

    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    private DatabaseReference question_Ref = database.getReference("question");
    private DatabaseReference user_Ref = database.getReference("User");

    private int question_consistence = 0;
    private ProgressDialog progressDialog;
    private String time_str;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_question_confirm);
        getDateInfo();
        question_confirm = (TextView) findViewById(R.id.question_confirm);

        Bundle bundle = this.getIntent().getExtras();
        myid = bundle.getString("myid");
        otherid = bundle.getString("otherid");
        nodeid = bundle.getString("nodeid");
        Log.e("otherid", otherid);
        Log.e("nodeid", nodeid);

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("正在等待對方回答...");
        progressDialog.show();

        question_Ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.child(nodeid).hasChild(otherid + "answer1_pos") &&
                        dataSnapshot.child(nodeid).hasChild(otherid + "answer2_pos") &&
                        dataSnapshot.child(nodeid).hasChild(otherid + "answer3_pos")) {
                    Toast.makeText(getApplicationContext(), "取得資料", Toast.LENGTH_LONG).show();
                    int[] question = {dataSnapshot.child(nodeid).child("question1").getValue(Long.class).intValue(),
                            dataSnapshot.child(nodeid).child("question2").getValue(Long.class).intValue(),
                            dataSnapshot.child(nodeid).child("question3").getValue(Long.class).intValue()};
//                    int[] question = {1,2,3};
                    int[] my_answer_pos = {dataSnapshot.child(nodeid).child(myid + "answer1_pos").getValue(Long.class).intValue(),
                            dataSnapshot.child(nodeid).child(myid + "answer2_pos").getValue(Long.class).intValue(),
                            dataSnapshot.child(nodeid).child(myid + "answer3_pos").getValue(Long.class).intValue()};
                    int[] other_answer_pos = {dataSnapshot.child(nodeid).child(otherid + "answer1_pos").getValue(Long.class).intValue(),
                            dataSnapshot.child(nodeid).child(otherid + "answer2_pos").getValue(Long.class).intValue(),
                            dataSnapshot.child(nodeid).child(otherid + "answer3_pos").getValue(Long.class).intValue()};
                    for (int i = 0; i < 3; i++) {
                        for (int j = 0; j < 4; j++) {
                            if (question_switch[j] == question[i]) {
                                if (my_answer_pos[i] + other_answer_pos[i] == 3) {
                                    question_consistence = question_consistence + 1;
                                    Log.e("confirm", question_consistence + "");
                                    Log.e("confirmno", j + "  " + i);
                                }
                            }
                        }
                        if (question_switch[0] != question[i] && question_switch[1] != question[i] && question_switch[2] != question[i] && question_switch[3] != question[i]) {
                            if (my_answer_pos[i] == other_answer_pos[i]) {
                                question_consistence = question_consistence + 1;
                                Log.e("confirm", question_consistence + "");
                                Log.e("confirmno", "" + i);
                            }
                        }
                        question_confirm.setText("答案相同" + question_consistence + "題\n" + "相同率" + (question_consistence * 100) / 3 + "%");
                        progressDialog.dismiss();
                        if (question_consistence == 3) {
                            new AlertDialog.Builder(QuestionConfirmActivity.this)
                                    .setIcon(R.mipmap.ic_passpair_launch)
                                    .setTitle("配對成功")
                                    .setMessage("恭喜! \n已送出好友邀請")
                                    .setPositiveButton("確認", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                            question_Ref.addListenerForSingleValueEvent(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(DataSnapshot dataSnapshot) {
                                                    if (dataSnapshot.hasChild(nodeid)) {
                                                        question_Ref.child(nodeid).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<Void> task) {
                                                                user_Ref.child(myid).child("任務").child(time_str).child("使用隨機通話功能").setValue(1);
                                                                user_Ref.child(otherid).child("好友邀請").child(myid).setValue("隨機通話推薦").addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                    @Override
                                                                    public void onComplete(@NonNull Task<Void> task) {
                                                                        Intent intent = new Intent(QuestionConfirmActivity.this, HomePage.class);
                                                                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                                                        Bundle bundle = new Bundle();
                                                                        bundle.putString("UID",myid);
                                                                        intent.putExtras(bundle);
                                                                        startActivity(intent);
                                                                    }
                                                                });

                                                            }
                                                        });
                                                    }
                                                }

                                                @Override
                                                public void onCancelled(DatabaseError databaseError) {

                                                }
                                            });
                                        }
                                    })
                                    .show();
                        } else {
                            new AlertDialog.Builder(QuestionConfirmActivity.this)
                                    .setIcon(R.mipmap.ic_passpair_launch)
                                    .setTitle("配對失敗")
                                    .setMessage("點擊確認後返回主頁")
                                    .setPositiveButton("確認", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                            question_Ref.addListenerForSingleValueEvent(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(DataSnapshot dataSnapshot) {
                                                    if (dataSnapshot.hasChild(nodeid)) {
                                                        question_Ref.child(nodeid).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<Void> task) {
                                                                user_Ref.child(myid).child("任務").child(time_str).child("使用隨機通話功能").setValue(1);
                                                                Intent intent = new Intent(QuestionConfirmActivity.this, HomePage.class);
                                                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                                                Bundle bundle = new Bundle();
                                                                bundle.putString("UID",myid);
                                                                intent.putExtras(bundle);
                                                                startActivity(intent);
                                                            }
                                                        });
                                                    }
                                                }

                                                @Override
                                                public void onCancelled(DatabaseError databaseError) {

                                                }
                                            });
                                        }
                                    })
                                    .show();


                        }
                    }
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
}
