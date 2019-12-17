package edu.nccu.mis.passpair.Task;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import edu.nccu.mis.passpair.R;

public class MissionAdapter extends BaseAdapter{
    private LayoutInflater myInflater;
    private Context context;
    private ArrayList<String> mission_name = new ArrayList<>();
    private ArrayList<String> missiom_piont = new ArrayList<>();
    private ArrayList<Integer> mission_complete = new ArrayList<Integer>();

    public MissionAdapter(Context context, ArrayList<String> mission_name, ArrayList<String> missiom_piont, ArrayList<Integer> mission_complete) {
        myInflater = LayoutInflater.from(context);
        this.context = context;
        this.mission_name = mission_name;
        this.missiom_piont = missiom_piont;
        this.mission_complete = mission_complete;
    }

    @Override
    public int getCount() {
        return missiom_piont.size();
    }

    @Override
    public Object getItem(int i) {
        return missiom_piont.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        View v = view;
        if (v == null){
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = inflater.inflate(R.layout.list_mission,null);
            TextView name = (TextView) v.findViewById(R.id.mission_list_name);
            TextView point = (TextView) v.findViewById(R.id.mission_list_point);
            ImageView complete = (ImageView) v.findViewById(R.id.mission_list_complete);
            name.setText(mission_name.get(i));
            point.setText(missiom_piont.get(i));
            complete.setImageResource(mission_complete.get(i));
        }
        return v;
    }
    @Override
    public int getViewTypeCount() {
        return getCount();
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }
}
