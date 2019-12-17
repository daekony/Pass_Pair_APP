package edu.nccu.mis.passpair.Gift.Adapter;


import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import edu.nccu.mis.passpair.Gift.Common.ItemObject;
import edu.nccu.mis.passpair.Gift.Common.RecyclerViewHolderSend;
import edu.nccu.mis.passpair.R;

public class RecyclerViewAdapterSend extends RecyclerView.Adapter<RecyclerViewHolderSend>{
    private List<ItemObject> itemList;
    private Context context;
    private int gift_cost = 5;

    public ItemClickCallback itemClickCallback;

    public interface ItemClickCallback{
        void onItemClick(int p);
    }

    public void setItemClickCallback(final ItemClickCallback itemClickCallback) {
        this.itemClickCallback = itemClickCallback;
    }

    public RecyclerViewAdapterSend(Context context, List<ItemObject> itemList) {
        this.itemList = itemList;
        this.context = context;
    }

    public void setListData(List<ItemObject> exerciseList) {
        this.itemList.clear();
        this.itemList.addAll(exerciseList);
    }

    @Override
    public RecyclerViewHolderSend onCreateViewHolder(ViewGroup parent, int viewType) {

        View layoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.cardview_gift, null);
        RecyclerViewHolderSend rcv = new RecyclerViewHolderSend(layoutView);
        return rcv;
    }

    @Override
    public void onBindViewHolder(RecyclerViewHolderSend holder, int position) {
        holder.giver_name.setText(itemList.get(position).getName());
        holder.gift_view.setImageResource(itemList.get(position).getPhoto());
        int point = (position + 1) * gift_cost;
        holder.gift_point.setText("花費 "+ point + " 分");
    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }
}
