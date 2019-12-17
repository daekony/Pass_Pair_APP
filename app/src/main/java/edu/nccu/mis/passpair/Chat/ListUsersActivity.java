package edu.nccu.mis.passpair.Chat;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.quickblox.chat.QBChatService;
import com.quickblox.chat.QBRestChatService;
import com.quickblox.chat.model.QBChatDialog;
import com.quickblox.chat.model.QBDialogType;
import com.quickblox.chat.utils.DialogUtils;
import com.quickblox.core.QBEntityCallback;
import com.quickblox.core.exception.QBResponseException;
import com.quickblox.users.QBUsers;
import com.quickblox.users.model.QBUser;

import java.util.ArrayList;

import edu.nccu.mis.passpair.Chat.Adapter.ListUserAdapter;
import edu.nccu.mis.passpair.Chat.Common.Common;
import edu.nccu.mis.passpair.Chat.Holder.QBUsersHolder;
import edu.nccu.mis.passpair.R;

public class ListUsersActivity extends AppCompatActivity {
    ListView lstUsers;
    Button btnCreateChat;
    TextView none;
    String UID;
    DatabaseReference Ref_User = FirebaseDatabase.getInstance().getReference().child("User");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_users);

        Intent intent = this.getIntent();
        Bundle bundle = intent.getExtras();
        UID = bundle.getString("UID");

        retrieveAllUser();
        lstUsers = (ListView) findViewById(R.id.lstUsers);
        none = (TextView) findViewById(R.id.chat_lst_nome);
        // 設定 ListView 選擇的方式 :
        // 單選 : ListView.CHOICE_MODE_SINGLE
        // 多選 : ListView.CHOICE_MODE_MULTIPLE
        lstUsers.setChoiceMode(ListView.CHOICE_MODE_SINGLE);

        btnCreateChat = (Button) findViewById(R.id.btn_create_chat);
        btnCreateChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int countChoice = lstUsers.getCount();
                //getCheckedItemPositions(): Returns the set of checked items in the list.
                if (lstUsers.getCheckedItemPositions().size() == 1){
                    createPrivateChat(lstUsers.getCheckedItemPositions());
                }else if (lstUsers.getCheckedItemPositions().size() > 1){
                    createGroupChat(lstUsers.getCheckedItemPositions());
                }else {
                    Toast.makeText(ListUsersActivity.this,"Please select friend to chat",Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private void createGroupChat(SparseBooleanArray checkedItemPositions) {
        final ProgressDialog mDialog = new ProgressDialog(ListUsersActivity.this);
        mDialog.setMessage("Please waiting...");
        mDialog.setCanceledOnTouchOutside(false);
        mDialog.show();
        int countChoice = lstUsers.getCount();
        ArrayList<Integer> occupantIdsList = new ArrayList<>();
        //loop all users, and if user has been selected , we will build chat dialog with this user
        for (int i= 0;i<countChoice;i++){
            //True表示該位置是選取的地方
            if (checkedItemPositions.get(i)){
                QBUser user = (QBUser) lstUsers.getItemAtPosition(i);
                occupantIdsList.add(user.getId());
            }
        }
        //Create Chat Dialog
        //QBChatDialog:  is responsible for all chat related operations
        QBChatDialog dialog = new QBChatDialog();
        dialog.setName(Common.createChatDialogName(occupantIdsList));
        //GROUP: The group dialog type. Users from dialog's occupants_ids will be able to chat in this dialog.
        //PRIVATE: The private dialog type.
        dialog.setType(QBDialogType.GROUP);
        dialog.setOccupantsIds(occupantIdsList);

        QBRestChatService.createChatDialog(dialog).performAsync(new QBEntityCallback<QBChatDialog>() {
            @Override
            public void onSuccess(QBChatDialog qbChatDialog, Bundle bundle) {
                mDialog.dismiss();
                Toast.makeText(getBaseContext(),"Create chat dialog successfully",Toast.LENGTH_LONG).show();
                finish();
            }

            @Override
            public void onError(QBResponseException e) {
                Log.e("Error",e.getMessage());
            }
        });
    }

    private void createPrivateChat(SparseBooleanArray checkedItemPositions) {
        final ProgressDialog mDialog = new ProgressDialog(ListUsersActivity.this);
        mDialog.setMessage("Please waiting...");
        mDialog.setCanceledOnTouchOutside(false);
        mDialog.show();

        int countChoice = lstUsers.getCount();
        ArrayList<Integer> occupantIdsList = new ArrayList<>();
        //loop all users, and if user has been selected , we will build chat dialog with this user
        for (int i= 0;i<countChoice;i++){
            if (checkedItemPositions.get(i)){
                QBUser user = (QBUser) lstUsers.getItemAtPosition(i);
                //buildPrivateDialog(): Creates private dialog.
                QBChatDialog dialog = DialogUtils.buildPrivateDialog(user.getId());
                QBRestChatService.createChatDialog(dialog).performAsync(new QBEntityCallback<QBChatDialog>() {
                    @Override
                    public void onSuccess(QBChatDialog qbChatDialog, Bundle bundle) {
                        mDialog.dismiss();
                        Toast.makeText(getBaseContext(),"Create private chat dialog successfully",Toast.LENGTH_LONG).show();
                        finish();
                    }

                    @Override
                    public void onError(QBResponseException e) {
                        Log.e("Error",e.getMessage());
                    }
                });
            }
        }
    }

    private void retrieveAllUser() {
        //getUsers() :Retrieve users
        QBUsers.getUsers(null).performAsync(new QBEntityCallback<ArrayList<QBUser>>() {
            @Override
            public void onSuccess(ArrayList<QBUser> qbUsers, Bundle bundle) {
                //add to cache
                //將先前讀進來的資料留著，預備下一次讀取
                QBUsersHolder.getInstance().putUsers(qbUsers);
                //Create new Arraylist to add all user from web services without current user logined
                final ArrayList<QBUser> qbUserWithoutCurrent = new ArrayList<QBUser>();
                for (final QBUser user : qbUsers){
                    if (!user.getLogin().equals(QBChatService.getInstance().getUser().getLogin())){
                        final String firebase_UID = user.getCustomData().toString();
                        Log.e("firebase_UID",firebase_UID);
                        Ref_User.child(UID).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                if (dataSnapshot.hasChild("朋友")){
                                    int friend_count = 0;
                                    for (DataSnapshot ds : dataSnapshot.child("朋友").getChildren()){
                                        Log.e("ds.getKey",ds.getKey());
                                        if (TextUtils.equals(ds.getKey(),firebase_UID)){
                                            qbUserWithoutCurrent.add(user);
                                            friend_count = friend_count +1;
                                            Log.e("firebase_UID_added",ds.getKey());
                                        }
                                    }
                                    if (qbUserWithoutCurrent.size() >= friend_count && friend_count != 0){
                                        none.setText("");
                                        ListUserAdapter adapter = new ListUserAdapter(getBaseContext(),qbUserWithoutCurrent);
                                        lstUsers.setAdapter(adapter);
                                        adapter.notifyDataSetChanged();
                                    }
                                }else {
                                    none.setText("尚未有任何好友");
                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });
                    }
                }
//                ListUserAdapter adapter = new ListUserAdapter(getBaseContext(),qbUserWithoutCurrent);
//                lstUsers.setAdapter(adapter);
//                adapter.notifyDataSetChanged();
            }

            @Override
            public void onError(QBResponseException e) {
                Log.e("Error",e.getMessage());
            }
        });
    }
}
