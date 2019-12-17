package edu.nccu.mis.passpair.PassIndex;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
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


import edu.nccu.mis.passpair.R;


public class RecAdapterMap extends RecyclerView.Adapter<RecAdapterMap.ViewHolderMap> {
    private Context context;
    private ArrayList<String> Imglist;
    private ArrayList<String> namelist;
    private ArrayList<String> IDlist;
    private ArrayList<Double> dislist;
    private String UID;
    DatabaseReference Ref_user = FirebaseDatabase.getInstance().getReference().child("User");
    private boolean today_met = true;
    private String time_str;

    public RecAdapterMap(Context context, ArrayList<String> Imglist, ArrayList<String> namelist, ArrayList<String> IDlist, ArrayList<Double> dislist, String UID) {
        this.context = context;
        this.Imglist = Imglist;
        this.namelist = namelist;
        this.IDlist = IDlist;
        this.dislist = dislist;
        this.UID = UID;
        getDateInfo();
    }

    public interface ItemClickListenerMap {
        void onClick(View view, int position);
    }

    @Override
    public ViewHolderMap onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.from(parent.getContext())
                .inflate(R.layout.cardview_map, parent, false);
        ViewHolderMap viewHolder = new ViewHolderMap(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolderMap holder, int position) {
        String img = Imglist.get(position);
        String name = namelist.get(position);
        Double dis = dislist.get(position);
        Uri uri = Uri.parse(img);
        Picasso.with(holder.card_map_image.getContext())
                .load(uri)
                .error(android.R.drawable.stat_notify_error)
                .into(holder.card_map_image);
        holder.card_map_name.setText(name);
        int dis_dot = String.valueOf(dis).indexOf(".");
        String dis_convert = String.valueOf(dis).substring(0, dis_dot + 2);
        holder.card_map_dis.setText(dis_convert + "  m");
        holder.setItemClickListener(new ItemClickListenerMap() {
            @Override
            public void onClick(View view, final int position) {
                Ref_user.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.child(UID).hasChild("Map資料")){
                            if (dataSnapshot.child(UID).child("Map資料").hasChild(IDlist.get(position))){
                                Long pass_count = dataSnapshot.child(UID).child("Map資料").child(IDlist.get(position)).getChildrenCount();
                                int pass_count_int = pass_count.intValue();
                                int i =0;
                                for (DataSnapshot ds : dataSnapshot.child(UID).child("Map資料").child(IDlist.get(position)).getChildren()){
                                    String pass_date = dataSnapshot.child(UID).child("Map資料").child(IDlist.get(position)).child(ds.getKey()).getValue(String.class);
                                    if (TextUtils.equals(pass_date,time_str)){
                                        today_met = true;
                                        Toast.makeText(context,"今天已經過計算相遇次數",Toast.LENGTH_SHORT).show();
                                        ToSecond(position);
                                    }
                                }
                                if (!today_met){
                                    Ref_user.child(UID).child("Map資料").child(IDlist.get(position)).push().setValue(time_str).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            today_met = true;
                                            ToSecond(position);
                                        }
                                    });
                                }
//                                Long count = dataSnapshot.child(UID).child("Map資料").child(IDlist.get(position)).getValue(Long.class);
//                                int count_int = count.intValue();
//                                int current_count = count_int + 1;
//                                Ref_user.child(UID).child("Map資料").child(IDlist.get(position)).setValue(current_count);
                                ToSecond(position);
                            }else{
                                Ref_user.child(UID).child("Map資料").child(IDlist.get(position)).push().setValue(time_str);
                                ToSecond(position);
                            }
                        }else {
                            Ref_user.child(UID).child("Map資料").child(IDlist.get(position)).push().setValue(time_str);
                            ToSecond(position);
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
                ToSecond(position);
            }
        });
    }
    private void ToSecond(int position){
        Intent intent = new Intent();
        Bundle bundle = new Bundle();
        bundle.putString("ID", IDlist.get(position));
        bundle.putString("NAME", namelist.get(position));
        bundle.putString("UID", UID);
        intent.putExtras(bundle);
        intent.setClass(context, Second.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
        ((Activity)context).finish();
    }

    @Override
    public int getItemCount() {
        return namelist.size();
    }

    public static class ViewHolderMap extends RecyclerView.ViewHolder implements View.OnClickListener {
        public ImageView card_map_image;
        public TextView card_map_name, card_map_dis;
        private RecAdapterMap.ItemClickListenerMap itemClickListenerMap;

        public ViewHolderMap(View itemView) {
            super(itemView);
            card_map_image = (ImageView) itemView.findViewById(R.id.card_map_image);
            card_map_name = (TextView) itemView.findViewById(R.id.card_map_name);
            card_map_dis = (TextView) itemView.findViewById(R.id.card_map_dis);
            itemView.setOnClickListener(this);
        }
        public void setItemClickListener(RecAdapterMap.ItemClickListenerMap itemClickListenerMap) {
            this.itemClickListenerMap = itemClickListenerMap;
        }

        @Override
        public void onClick(View view) {
            itemClickListenerMap.onClick(view, getAdapterPosition());
        }

    }
    private void getDateInfo() {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy/MM/dd");
        Date curDate = new Date(System.currentTimeMillis()); // 獲取當前時間
        time_str = formatter.format(curDate);
    }
}

