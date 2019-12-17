package edu.nccu.mis.passpair.FriendInvite.Adapter;


import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import edu.nccu.mis.passpair.R;

public class RecAdaperFriend extends RecyclerView.Adapter<RecAdaperFriend.ViewHolderFriendMail>{
    private Context context;
    private ArrayList<String> Imglist;
    private ArrayList<String> namelist;
    private ArrayList<String> IDlist;
    private String MyUID;
    private ArrayList<String> reasonlist;
    DatabaseReference Ref_user = FirebaseDatabase.getInstance().getReference().child("User");

    public RecAdaperFriend(Context context, ArrayList<String> Imglist, ArrayList<String> namelist,ArrayList<String> IDlist,String MyUID,ArrayList<String> reasonlist) {
        this.context = context;
        this.Imglist = Imglist;
        this.namelist = namelist;
        this.IDlist = IDlist;
        this.MyUID = MyUID;
        this.reasonlist = reasonlist;
    }
    public interface ItemClickListenerFriendMail{
        void onClick(View view, int position);
    }
    @Override
    public ViewHolderFriendMail onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater =  LayoutInflater.from(parent.getContext());
        View view = inflater.from(parent.getContext())
                .inflate(R.layout.cardview_friendmail,parent,false);
        ViewHolderFriendMail viewHolder = new ViewHolderFriendMail(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final ViewHolderFriendMail holder, int position) {
        String img = Imglist.get(position);
        String name = namelist.get(position);
        String reason = reasonlist.get(position);
        Uri uri = Uri.parse(img);
        Picasso.with(holder.card_friendmail_image.getContext())
                .load(uri)
                .error(android.R.drawable.stat_notify_error)
                .into(holder.card_friendmail_image);
        holder.card_friendmail_name.setText(name);
        holder.card_friendmail_reason.setText(reason);
        holder.setItemClickListener(new ItemClickListenerFriendMail() {
            @Override
            public void onClick(View view, final int position) {
                if (view.getId() == R.id.card_friendmail_confirm){
                    Ref_user.child(MyUID).child("好友邀請").child(IDlist.get(position)).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            Toast.makeText(context,"已接受: "+namelist.get(position) + " 的好友邀請",Toast.LENGTH_SHORT).show();
                            Ref_user.child(MyUID).child("朋友").child(IDlist.get(position)).setValue(reasonlist.get(position));
                            Ref_user.child(IDlist.get(position)).child("朋友").child(MyUID).setValue(reasonlist.get(position));
                            ((Activity) context).finish();
                        }
                    });
                }else if (view.getId() == R.id.card_friendmail_decline){
                    Ref_user.child(MyUID).child("好友邀請").child(IDlist.get(position)).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            Toast.makeText(context,"已拒絕: " + namelist.get(position) + " 的好友邀請",Toast.LENGTH_SHORT).show();
                            ((Activity) context).finish();
                        }
                    });
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        if (Imglist != null){
            return Imglist.size();
        }else {
            return 0;
        }
    }
    public static class ViewHolderFriendMail extends RecyclerView.ViewHolder implements View.OnClickListener{
        public ImageView card_friendmail_image;
        public TextView card_friendmail_name,card_friendmail_reason;
        public ImageButton card_friendmail_confirm,card_friendmail_decline;
        private ItemClickListenerFriendMail itemClickListenerFriendMail;

        public ViewHolderFriendMail(View itemView) {
            super(itemView);
            card_friendmail_image = (ImageView) itemView.findViewById(R.id.card_friendmail_image);
            card_friendmail_name = (TextView) itemView.findViewById(R.id.card_friendmail_name);
            card_friendmail_reason = (TextView) itemView.findViewById(R.id.card_friendmail_reason);
            card_friendmail_confirm = (ImageButton) itemView.findViewById(R.id.card_friendmail_confirm);
            card_friendmail_decline = (ImageButton) itemView.findViewById(R.id.card_friendmail_decline);
            card_friendmail_confirm.setOnClickListener(this);
            card_friendmail_decline.setOnClickListener(this);
        }
        public void setItemClickListener(ItemClickListenerFriendMail itemClickListenerFriendMail){
            this.itemClickListenerFriendMail = itemClickListenerFriendMail;
        }
        @Override
        public void onClick(View view) {
            itemClickListenerFriendMail.onClick(view,getAdapterPosition());
        }
    }

}
