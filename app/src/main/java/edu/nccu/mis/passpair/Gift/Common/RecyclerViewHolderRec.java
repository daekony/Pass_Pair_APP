package edu.nccu.mis.passpair.Gift.Common;


import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import edu.nccu.mis.passpair.R;

public class RecyclerViewHolderRec extends RecyclerView.ViewHolder {

    public TextView giver_name,gift_send_time,gift_point;
    public ImageView gift_view;

    public RecyclerViewHolderRec(final View itemView) {
        super(itemView);
        giver_name = (TextView)itemView.findViewById(R.id.giver_name);
        gift_view = (ImageView)itemView.findViewById(R.id.gift_view);
        gift_point = (TextView) itemView.findViewById(R.id.gift_point);
        gift_send_time = (TextView) itemView.findViewById(R.id.gift_sendtime);
    }
}