package edu.nccu.mis.passpair.Homepage.Fragment;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.zip.Inflater;

import edu.nccu.mis.passpair.Gift.Adapter.RecyclerViewAdapterRec;
import edu.nccu.mis.passpair.Gift.Common.ItemObject;
import edu.nccu.mis.passpair.R;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link GiftFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link GiftFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class GiftFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
//    private static final String ARG_PARAM1 = "param1";
//    private static final String ARG_PARAM2 = "param2";
    // TODO: Rename and change types of parameters
//    private String mParam1;
//    private String mParam2;
//
//    private OnFragmentInteractionListener mListener;
    String UID,FID;
    TextView gift_none;
    RecyclerView recyclerView;
    DatabaseReference Ref_User = FirebaseDatabase.getInstance().getReference().child("User");
    DatabaseReference Ref_Gift = FirebaseDatabase.getInstance().getReference().child("Gift");
    private int[] g_photo = new int[]{R.drawable.glass, R.drawable.bouquet, R.drawable.teddy_bear, R.drawable.ice_cream, R.drawable.sweater, R.drawable.gift, R.drawable.purse, R.drawable.bicycle, R.drawable.motorbiking};
    public GiftFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment GiftFragment.
     */
    // TODO: Rename and change types and number of parameters
//    public static GiftFragment newInstance(String param1, String param2) {
//        GiftFragment fragment = new GiftFragment();
//        Bundle args = new Bundle();
//        args.putString(ARG_PARAM1, param1);
//        args.putString(ARG_PARAM2, param2);
//        fragment.setArguments(args);
//        return fragment;
//    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_gift,container,false);
        UID = this.getArguments().getString("UID").toString();
        FID = this.getArguments().getString("FUID").toString();
        final ArrayList<ItemObject> gift_info = new ArrayList<ItemObject>();

        gift_none = (TextView) view.findViewById(R.id.frag_gift_none);
        recyclerView = (RecyclerView) view.findViewById(R.id.recycle_frag_gift);

        LinearLayoutManager manager = new GridLayoutManager(GiftFragment.this.getActivity(),3);
        recyclerView.setLayoutManager(manager);
        recyclerView.setHasFixedSize(true);
        recyclerView.setItemViewCacheSize(20);
        recyclerView.setDrawingCacheEnabled(true);
        recyclerView.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);

        Ref_Gift.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot ds : dataSnapshot.getChildren()){
                    String ruid = dataSnapshot.child(ds.getKey()).child("ruid").getValue(String.class);
                    String guid = dataSnapshot.child(ds.getKey()).child("guid").getValue(String.class);
                    if (TextUtils.equals(ruid,FID)&&TextUtils.equals(guid,UID)){
                        Log.e("Send","send");
                        String gtype = dataSnapshot.child(ds.getKey()).child("gtype").getValue(String.class);
                        String time = dataSnapshot.child(ds.getKey()).child("time").getValue(String.class);
                        gift_info.add(new ItemObject("我",g_photo[Integer.parseInt(gtype)],time));
                        RecyclerViewAdapterRec adapter = new RecyclerViewAdapterRec(getActivity(),gift_info);
                        recyclerView.setAdapter(adapter);
                        adapter.notifyDataSetChanged();
                    }
                }
                if (gift_info.isEmpty()){
                    gift_none.setText("尚未送出禮物");
                }else {
                    gift_none.setText("");
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        RecyclerViewAdapterRec adapter = new RecyclerViewAdapterRec(getActivity(),gift_info);
        recyclerView.setAdapter(adapter);
        adapter.notifyDataSetChanged();

        return view;
    }

}
