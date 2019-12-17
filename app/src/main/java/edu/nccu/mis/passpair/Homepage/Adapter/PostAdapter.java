package edu.nccu.mis.passpair.Homepage.Adapter;


import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import edu.nccu.mis.passpair.Post.PostLoad;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.ViewHolder>{
    private Context context;
    private ArrayList<String> imageurl_list;
    private ArrayList<String> post_id_list;
    private String UID;

    public interface ItemClickListener{
        void onClick(View view,int position);
    }

    public PostAdapter(Context context,ArrayList<String> imageurl_list,ArrayList<String> post_id_list, String UID){
        this.context = context;
        this.imageurl_list = imageurl_list;
        this.post_id_list = post_id_list;
        this.UID = UID;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater =  LayoutInflater.from(parent.getContext());
        View view = inflater.from(parent.getContext())
                .inflate(edu.nccu.mis.passpair.R.layout.cardview_post,parent,false);
        ViewHolder viewHolder = new ViewHolder(view);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(PostAdapter.ViewHolder holder, int position) {
        String post = imageurl_list.get(position);
        Uri uri = Uri.parse(post);
        Picasso.with(holder.cardviewpostimg1.getContext())
                .load(uri)
                .error(android.R.drawable.stat_notify_error)
                .into(holder.cardviewpostimg1);
        holder.setItemClickListener(new ItemClickListener(){
            @Override
            public void onClick(View view, int position) {
//                Toast.makeText(context, imageurl_list.get(position),Toast.LENGTH_SHORT).show();
//                Toast.makeText(context,position + "",Toast.LENGTH_SHORT).show();
                Intent intent = new Intent();
                Bundle bundle = new Bundle();
                bundle.putString("UID", UID);
                bundle.putString("PostID", post_id_list.get(position));
                intent.putExtras(bundle);
                intent.setClass(context, PostLoad.class);
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        if (imageurl_list != null){
            return imageurl_list.size();
        }else {
            return 0;
        }
    }


    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        public ImageView cardviewpostimg1;
        private ItemClickListener itemClickListener;

        public ViewHolder(View itemView) {
            super(itemView);
            cardviewpostimg1 = (ImageView) itemView.findViewById(edu.nccu.mis.passpair.R.id.cardviewpostimg1);
            itemView.setOnClickListener(this);
        }

        public void setItemClickListener(ItemClickListener itemClickListener){
            this.itemClickListener = itemClickListener;
        }

        @Override
        public void onClick(View view) {
            itemClickListener.onClick(view,getAdapterPosition());
        }

    }
}

