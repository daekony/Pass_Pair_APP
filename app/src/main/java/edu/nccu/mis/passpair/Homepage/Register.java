package edu.nccu.mis.passpair.Homepage;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.quickblox.auth.QBAuth;
import com.quickblox.auth.session.QBSession;
import com.quickblox.core.QBEntityCallback;
import com.quickblox.core.exception.QBResponseException;
import com.quickblox.users.QBUsers;
import com.quickblox.users.model.QBUser;

import java.sql.Ref;
import java.text.SimpleDateFormat;
import java.util.Date;

import edu.nccu.mis.passpair.R;

public class Register extends AppCompatActivity {
    EditText RgUsername, RgPassword, RgConfirm, RgNickname, RgBirthday, RgEmail;
    Button RgButton;
    Spinner RgSexual;
    String[] sexual = new String[]{"男", "女"};
    String userSexual;
    String Error = "請輸入";
    String Uncorrect = "";
    DatePicker RgdatePicker;

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;

    // Write a message to the database
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference Ref_user = database.getReference("User");
    private String time_str;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        registerChatSession();
        getDateInfo();

        RgUsername = (EditText) findViewById(R.id.RgUsername);
        RgPassword = (EditText) findViewById(R.id.RgPassword);
        RgConfirm = (EditText) findViewById(R.id.RgConfirm);
        RgNickname = (EditText) findViewById(R.id.RgNickname);
//        RgBirthday = (EditText) findViewById(R.id.RgBirthday);
        RgButton = (Button) findViewById(R.id.RgBotton);
        RgSexual = (Spinner) findViewById(R.id.RgSexual);
        RgdatePicker = (DatePicker) findViewById(R.id.datePicker);
        int year = RgdatePicker.getYear();
        int month = RgdatePicker.getMonth() + 1;
        int day = RgdatePicker.getDayOfMonth();
        RgButton.setOnClickListener(btnListener);

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, sexual);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        RgSexual.setAdapter(adapter);
        RgSexual.setOnItemSelectedListener(spnListener);

        String birth = String.valueOf(year+"/"+month+"/"+day);

        mAuth = FirebaseAuth.getInstance();

        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    // User is signed in
                    Log.d("TAG", "onAuthStateChanged:signed_in:" + user.getUid());
                } else {
                    // User is signed out
                    Log.d("TAG", "onAuthStateChanged:signed_out");
                }
                // ...
            }
        };
    }
    @Override
    public void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }

    Button.OnClickListener btnListener = new Button.OnClickListener(){

        @Override
        public void onClick(View view) {
            onSignIn();
        }
    };

    private void onSignIn(){
        final String email = RgUsername.getText().toString();
        final String pass = RgPassword.getText().toString();
        String confirmpass = RgConfirm.getText().toString();
        final String Nickname = RgNickname.getText().toString();

        int year = RgdatePicker.getYear();
        int month = RgdatePicker.getMonth() + 1;
        int day = RgdatePicker.getDayOfMonth();

        final String birth = String.valueOf(year+"/"+month+"/"+day);

        if (!email.isEmpty() && !pass.isEmpty() && !(pass.length() < 8)) {
            if(pass.equals(confirmpass)&& !Nickname.isEmpty() && !userSexual.isEmpty()){
                mAuth.createUserWithEmailAndPassword(email, pass)
                        .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                            @Override
                            public void onSuccess(AuthResult authResult) {
                                Toast.makeText(Register.this,"註冊成功"+authResult.getUser().getEmail(),Toast.LENGTH_SHORT).show();
                                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                                if (user != null) {
                                    // The user's ID, unique to the Firebase project. Do NOT use this value to
                                    // authenticate with your backend server, if you have one. Use
                                    // FirebaseUser.getToken() instead.
                                    final String uid = user.getUid();
                                    DatabaseReference myRef = database.getReference();
                                    //製作個人基本資料
                                    myRef.child("User").child(uid).child("基本資料").child("使用者信箱").setValue(email);
                                    myRef.child("User").child(uid).child("基本資料").child("密碼").setValue(pass);
                                    myRef.child("User").child(uid).child("基本資料").child("性別").setValue(userSexual);
                                    myRef.child("User").child(uid).child("基本資料").child("生日").setValue(birth);
                                    myRef.child("User").child(uid).child("基本資料").child("暱稱").setValue(Nickname);
                                    myRef.child("User").child(uid).child("基本資料").child("積分").setValue(300);
                                    Ref_user.child(uid).child("任務").child(time_str).child("每日登入").setValue(1);
                                    //製作map所需資訊
                                    // myRef.child("Map資訊").child("其他使用者").setValue(Nickname);
                                    //移除本身多餘的資訊
                                    //  myRef.child(uid).child("Map資訊").child("其他使用者").child(Nickname).removeValue();
                                    QBUser qbUser = new QBUser(email,pass);
                                    //設定Quickblox使用者姓名
                                    qbUser.setFullName(Nickname);
                                    //註冊Quickblox資料
                                    qbUser.setCustomData(uid);
                                    QBUsers.signUp(qbUser).performAsync(new QBEntityCallback<QBUser>() {
                                        @Override
                                        public void onSuccess(QBUser qbUser, Bundle bundle) {
                                            Toast.makeText(getApplicationContext(),"Sign up successfully",Toast.LENGTH_LONG).show();
                                            Intent intent = new Intent();
                                            //以bundle物件進行打包
                                            Bundle bundleR=new Bundle();
                                            bundleR.putString("UID", uid);
                                            intent.putExtras(bundleR);

                                            intent.setClass(Register.this, Photo.class);
                                            startActivity(intent);
                                            finish();
                                        }
                                        @Override
                                        public void onError(QBResponseException e) {
                                            Toast.makeText(getApplicationContext(),"" + e.getMessage(),Toast.LENGTH_LONG).show();
                                        }
                                    });
                                }

                            }
                        }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(Register.this,"註冊失敗請重試",Toast.LENGTH_SHORT).show();

                    }
                });
            }else{
                Toast.makeText(Register.this,"密碼不一致",Toast.LENGTH_SHORT).show();
            }
        }else if (email.isEmpty()){
            Toast.makeText(Register.this,"請填寫信箱",Toast.LENGTH_SHORT).show();
        }else if (pass.isEmpty() || pass.length() < 8){
            Toast.makeText(Register.this,"請確認密碼長度必須大於八",Toast.LENGTH_SHORT).show();
        }
    }

    private Spinner.OnItemSelectedListener spnListener = new Spinner.OnItemSelectedListener() {

        @Override
        public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
            userSexual = adapterView.getSelectedItem().toString();
        }

        @Override
        public void onNothingSelected(AdapterView<?> adapterView) {

        }
    };
    private void registerChatSession() {
        QBAuth.createSession().performAsync(new QBEntityCallback<QBSession>() {
            @Override
            public void onSuccess(QBSession qbSession, Bundle bundle) {

            }

            @Override
            public void onError(QBResponseException e) {
                Log.e("Error",e.getMessage());
            }
        });
    }
    private void getDateInfo() {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMdd");
        Date curDate = new Date(System.currentTimeMillis()); // 獲取當前時間
        time_str = formatter.format(curDate);
    }
}
