package edu.nccu.mis.passpair.Gift.Adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import edu.nccu.mis.passpair.Gift.Common.ItemObject;
import edu.nccu.mis.passpair.Gift.Common.RecyclerViewHolderRec;
import edu.nccu.mis.passpair.R;


public class RecyclerViewAdapterRec extends RecyclerView.Adapter<RecyclerViewHolderRec> {
    private List<ItemObject> itemList;
    private Context context;
    public RecyclerViewAdapterRec(Context context, List<ItemObject> itemList) {
        this.itemList = itemList;
        this.context = context;
    }

    public void setListData(List<ItemObject> exerciseList) {
        this.itemList.clear();
        this.itemList.addAll(exerciseList);
    }

    @Override
    public RecyclerViewHolderRec onCreateViewHolder(ViewGroup parent, int viewType) {
        View layoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.cardview_gift, null);
        RecyclerViewHolderRec rcv = new RecyclerViewHolderRec(layoutView);
        return rcv;
    }

    @Override
    public void onBindViewHolder(RecyclerViewHolderRec holder, int position) {
        if (itemList.get(position).getName() == "我"){
            holder.giver_name.setText("我");
            holder.gift_view.setImageResource(itemList.get(position).getPhoto());
            holder.gift_send_time.setText(itemList.get(position).getTime());
            holder.gift_point.setText(String.valueOf(position +1));
        }else {
            holder.giver_name.setText(itemList.get(position).getName());
            holder.gift_view.setImageResource(itemList.get(position).getPhoto());
            holder.gift_send_time.setText(itemList.get(position).getTime());
            holder.gift_point.setText(String.valueOf(position +1));
        }

    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }
}
