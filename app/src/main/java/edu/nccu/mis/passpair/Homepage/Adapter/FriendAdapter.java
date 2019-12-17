package edu.nccu.mis.passpair.Homepage.Adapter;


import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import edu.nccu.mis.passpair.Gift.GiftSending;
import edu.nccu.mis.passpair.Homepage.Friendpage.FriendPage;
import edu.nccu.mis.passpair.R;

public class FriendAdapter extends RecyclerView.Adapter<FriendAdapter.ViewHolderFriends>{
    private Context context;
    private ArrayList<String> Imglist;
    private ArrayList<String> namelist;
    private ArrayList<String> IDlist;
    private String MyUID;

    public FriendAdapter(Context context, ArrayList<String> Imglist, ArrayList<String> namelist,ArrayList<String> IDlist,String MyUID) {
        this.context = context;
        this.Imglist = Imglist;
        this.namelist = namelist;
        this.IDlist = IDlist;
        this.MyUID = MyUID;
    }
    public interface ItemClickListenerFriend{
        void onClick(View view,int position);
    }
    @Override
    public ViewHolderFriends onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater =  LayoutInflater.from(parent.getContext());
        View view = inflater.from(parent.getContext())
                .inflate(R.layout.cardview_friend,parent,false);
        ViewHolderFriends viewHolder = new ViewHolderFriends(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final FriendAdapter.ViewHolderFriends holder, int position) {
        String img = Imglist.get(position);
        String name = namelist.get(position);
        Uri uri = Uri.parse(img);
        Picasso.with(holder.card_friend_image.getContext())
                .load(uri)
                .error(android.R.drawable.stat_notify_error)
                .into(holder.card_friend_image);
        holder.card_friend_name.setText(name);
        holder.setItemClickListener(new ItemClickListenerFriend() {
            @Override
            public void onClick(View view, int position) {
                if (view.getId() == R.id.card_friend_message){
                    Intent intent = new Intent();
                    Bundle bundle=new Bundle();
                    bundle.putString("MyUID", MyUID);
                    bundle.putString("FUID",IDlist.get(position));
                    intent.putExtras(bundle);
                    intent.setClass(context, FriendPage.class);
                    context.startActivity(intent);
                }else if (view.getId() == R.id.card_friend_gift){
                    Toast.makeText(context,"送禮囉 ! : "+namelist.get(position),Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent();
                    Bundle bundle=new Bundle();
                    bundle.putString("MyUID", MyUID);
                    bundle.putString("FID",IDlist.get(position));
                    bundle.putString("Fname",namelist.get(position));
                    intent.putExtras(bundle);
                    intent.setClass(context, GiftSending.class);
                    context.startActivity(intent);
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
    public static class ViewHolderFriends extends RecyclerView.ViewHolder implements View.OnClickListener{
        public ImageView card_friend_image;
        public TextView card_friend_name;
        public ImageButton card_friend_message,card_friend_gift;
        private ItemClickListenerFriend itemClickListenerFriend;

        public ViewHolderFriends(View itemView) {
            super(itemView);
            card_friend_image = (ImageView) itemView.findViewById(edu.nccu.mis.passpair.R.id.card_friend_image);
            card_friend_name = (TextView) itemView.findViewById(R.id.card_friend_name);
            card_friend_message = (ImageButton) itemView.findViewById(R.id.card_friend_message);
            card_friend_gift = (ImageButton) itemView.findViewById(R.id.card_friend_gift);
            card_friend_message.setOnClickListener(this);
            card_friend_gift.setOnClickListener(this);
        }
        public void setItemClickListener(ItemClickListenerFriend itemClickListenerFriend){
            this.itemClickListenerFriend = itemClickListenerFriend;
        }
        @Override
        public void onClick(View view) {
            itemClickListenerFriend.onClick(view,getAdapterPosition());
        }
    }
}
