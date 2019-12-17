package edu.nccu.mis.passpair.Gift.Common;


import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import edu.nccu.mis.passpair.Gift.Adapter.RecyclerViewAdapterSend;
import edu.nccu.mis.passpair.Gift.GiftSending;
import edu.nccu.mis.passpair.R;

public class RecyclerViewHolderSend extends RecyclerView.ViewHolder implements View.OnClickListener {

    public TextView giver_name,gift_point;
    public ImageView gift_view;
    public RecyclerViewAdapterSend.ItemClickCallback itemClickCallback;
    public int gift_cost = 5;

    public RecyclerViewHolderSend(final View itemView) {
        super(itemView);
        itemView.setOnClickListener(this);
        giver_name = (TextView)itemView.findViewById(R.id.giver_name);
        gift_view = (ImageView)itemView.findViewById(R.id.gift_view);
        gift_point = (TextView) itemView.findViewById(R.id.gift_point);
        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (GiftSending.rowListItem.get(getAdapterPosition()).isSent()){
                    GiftSending.rowListItem.get(getAdapterPosition()).setToSend(false);
                    itemView.setBackgroundResource(0);
                    GiftSending.user_cost_point = GiftSending.user_cost_point - (gift_cost * (getAdapterPosition() + 1));
                    GiftSending.gift_send_giftpoint.setText("已花費的積分: " + GiftSending.user_cost_point + " 分");
                }else {
                    if (GiftSending.rowListItem.get(getAdapterPosition()).getPhoto() == R.drawable.glass_locked){
                        GiftSending.rowListItem.get(getAdapterPosition()).setToSend(false);
                        itemView.setBackgroundResource(0);
                    } else if (GiftSending.rowListItem.get(getAdapterPosition()).getPhoto() == R.drawable.bouquet_locked){
                        GiftSending.rowListItem.get(getAdapterPosition()).setToSend(false);
                        itemView.setBackgroundResource(0);
                    } else if (GiftSending.rowListItem.get(getAdapterPosition()).getPhoto() == R.drawable.teddy_bear_locked){
                        GiftSending.rowListItem.get(getAdapterPosition()).setToSend(false);
                        itemView.setBackgroundResource(0);
                    } else if (GiftSending.rowListItem.get(getAdapterPosition()).getPhoto() == R.drawable.ice_cream_locked){
                        GiftSending.rowListItem.get(getAdapterPosition()).setToSend(false);
                        itemView.setBackgroundResource(0);
                    } else if (GiftSending.rowListItem.get(getAdapterPosition()).getPhoto() == R.drawable.sweater_locked){
                        GiftSending.rowListItem.get(getAdapterPosition()).setToSend(false);
                        itemView.setBackgroundResource(0);
                    } else if (GiftSending.rowListItem.get(getAdapterPosition()).getPhoto() == R.drawable.gift_locked){
                        GiftSending.rowListItem.get(getAdapterPosition()).setToSend(false);
                        itemView.setBackgroundResource(0);
                    } else if (GiftSending.rowListItem.get(getAdapterPosition()).getPhoto() == R.drawable.purse_locked){
                        GiftSending.rowListItem.get(getAdapterPosition()).setToSend(false);
                        itemView.setBackgroundResource(0);
                    } else if (GiftSending.rowListItem.get(getAdapterPosition()).getPhoto() == R.drawable.bicycle_locked){
                        GiftSending.rowListItem.get(getAdapterPosition()).setToSend(false);
                        itemView.setBackgroundResource(0);
                    } else if (GiftSending.rowListItem.get(getAdapterPosition()).getPhoto() == R.drawable.motorbiking_locked){
                        GiftSending.rowListItem.get(getAdapterPosition()).setToSend(false);
                        itemView.setBackgroundResource(0);
                    } else {
                        GiftSending.rowListItem.get(getAdapterPosition()).setToSend(true);
                        itemView.setBackgroundResource(R.drawable.chose_style);
                        GiftSending.user_cost_point = GiftSending.user_cost_point + (gift_cost * (getAdapterPosition() + 1));
                        GiftSending.gift_send_giftpoint.setText("已花費的積分: " + GiftSending.user_cost_point + " 分");
                    }
                }

            }
        });
    }

    @Override
    public void onClick(View view) {
        Log.e("Position", "" + getAdapterPosition());
        itemClickCallback.onItemClick(getAdapterPosition());
    }
}