package edu.nccu.mis.passpair.Chat.Adapter;


import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.amulyakhare.textdrawable.TextDrawable;
import com.amulyakhare.textdrawable.util.ColorGenerator;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.quickblox.chat.model.QBChatDialog;
import com.quickblox.core.QBEntityCallback;
import com.quickblox.core.exception.QBResponseException;
import com.quickblox.users.QBUsers;
import com.quickblox.users.model.QBUser;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import edu.nccu.mis.passpair.R;

public class ChatDialogAdapter extends BaseAdapter{
    private Context context;
    private ArrayList<QBChatDialog> qbChatDialogs;
    private String UID;
    private DatabaseReference Ref_user = FirebaseDatabase.getInstance().getReference().child("User");
    String occupant_Firebase_UID;

    public ChatDialogAdapter(Context context, ArrayList<QBChatDialog> qbChatDialogs, String UID) {
        this.context = context;
        this.qbChatDialogs = qbChatDialogs;
        this.UID = UID;
    }


    @Override
    public int getCount() {
        return qbChatDialogs.size();
    }

    @Override
    public Object getItem(int position) {
        return qbChatDialogs.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        if (view == null){
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.list_chat_dialog_layout,null);
            TextView txtTitle,txtMessage;
            final ImageView imageView;
            txtTitle = (TextView) view.findViewById(R.id.list_chat_dialog_title);
            txtMessage = (TextView) view.findViewById(R.id.list_chat_dialog_message);
            imageView = (ImageView) view.findViewById(R.id.image_chatDialog);
            for (final Integer occupantID : qbChatDialogs.get(position).getOccupants()){
                String occupantID_str = occupantID.toString();
                Log.e("occupantID",occupantID_str);
                QBUsers.getUser(occupantID).performAsync(new QBEntityCallback<QBUser>() {
                    @Override
                    public void onSuccess(QBUser qbUser, Bundle bundle) {
                        if (!TextUtils.equals(qbUser.getCustomData().toString() ,UID)) {
                            occupant_Firebase_UID = qbUser.getCustomData().toString();

                            Log.e("occupant_Firebase_UID", occupant_Firebase_UID);
                            Ref_user.child(occupant_Firebase_UID).child("基本資料").addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    String user_image = dataSnapshot.child("大頭照").getValue(String.class);
                                    Uri uri = Uri.parse(user_image);
                                    Picasso.with(context).load(uri).into(imageView, new com.squareup.picasso.Callback() {
                                        @Override
                                        public void onSuccess() {

                                        }

                                        @Override
                                        public void onError() {

                                        }
                                    });
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }
                            });
                        }
                    }
                    @Override
                    public void onError (QBResponseException e){

                    }
                });
            }
            txtMessage.setText(qbChatDialogs.get(position).getLastMessage());
            txtTitle.setText(qbChatDialogs.get(position).getName());

//            //Random color from Material color gallery(amulyakhare套件)
//            ColorGenerator generator = ColorGenerator.MATERIAL;
//            int randomColor = generator.getRandomColor();
//            //Build round drawable (amulyakhare套件)
//            TextDrawable.IBuilder builder = TextDrawable.builder().beginConfig()
//                    .withBorder(4)
//                    .endConfig()
//                    .round();
//            //建立文字大頭貼(User的第一個字)
//            //set text and color for drawable
//            //builder.build(文字, color)
//            TextDrawable drawable = builder.build(txtTitle.getText().toString().substring(0,1).toUpperCase(),randomColor);
//            imageView.setImageDrawable(drawable);
        }
        return view;
    }
}
