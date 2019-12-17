package edu.nccu.mis.passpair.Homepage.Fragment;

import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import edu.nccu.mis.passpair.Homepage.Adapter.FriendAdapter;
import edu.nccu.mis.passpair.R;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link FriendFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link FriendFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FriendFragment extends Fragment {
    private String MyUID;
    final ArrayList<String> imglist = new ArrayList<String>();
    final ArrayList<String> namelist = new ArrayList<String>();
    final ArrayList<String> IDlist = new ArrayList<String>();

    RecyclerView recyclerView;
    DatabaseReference database_Ref = FirebaseDatabase.getInstance().getReference();
    TextView friend_none;
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
//    private static final String ARG_PARAM1 = "param1";
//    private static final String ARG_PARAM2 = "param2";
    // TODO: Rename and change types of parameters
//    private String mParam1;
//    private String mParam2;
//
//    private OnFragmentInteractionListener mListener;

    public FriendFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment FriendFragment.
     */
    // TODO: Rename and change types and number of parameters
//    public static FriendFragment newInstance(String param1, String param2) {
//        FriendFragment fragment = new FriendFragment();
//        Bundle args = new Bundle();
//        args.putString(ARG_PARAM1, param1);
//        args.putString(ARG_PARAM2, param2);
//        fragment.setArguments(args);
//        return fragment;
//    }
//
//    @Override
//    public void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        if (getArguments() != null) {
//            mParam1 = getArguments().getString(ARG_PARAM1);
//            mParam2 = getArguments().getString(ARG_PARAM2);
//        }
//    }

    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View view= inflater.inflate(R.layout.fragment_friend,container,false);
        MyUID = this.getArguments().getString("UID").toString();
        friend_none = (TextView) view.findViewById(R.id.friend_none);
        recyclerView = (RecyclerView) view.findViewById(R.id.recyclehome);

        database_Ref.child("User").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.child(MyUID).hasChild("朋友")){
                    friend_none.setText("");
                    int friends_count_include = 0;
                    Log.e("myUid:",MyUID);
                    Long friend_list = dataSnapshot.child(MyUID).child("朋友").getChildrenCount();
                    int friend_list_int = friend_list.intValue();
                    for (DataSnapshot ds :  dataSnapshot.child(MyUID).child("朋友").getChildren()){
//                        Long cofirm_status = ds.getValue(Long.class);
//                        int cofirm_status_int = cofirm_status.intValue();
//                        if (cofirm_status_int == 1){
                            IDlist.add(ds.getKey());
//                        }
                        friends_count_include = friends_count_include + 1;
                    }
                    //確認已經讀取完
                    if (friends_count_include >= friend_list_int){
                        for (int i=0; i < IDlist.size(); i++){
                            String Fid = IDlist.get(i);
                            String Fimage = dataSnapshot.child(Fid).child("基本資料").child("大頭照").getValue(String.class);
                            String Fname = dataSnapshot.child(Fid).child("基本資料").child("暱稱").getValue(String.class);
                            imglist.add(Fimage);
                            namelist.add(Fname);
                        }
                        FriendAdapter adapter = new FriendAdapter(FriendFragment.this.getActivity(), imglist,namelist,IDlist,MyUID);
                        recyclerView.setAdapter(adapter);

                    }
                }else {
                    friend_none.setText("尚未有任何好友");
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        LinearLayoutManager manager = new LinearLayoutManager(FriendFragment.this.getActivity());
        recyclerView.setLayoutManager(manager);
        recyclerView.setHasFixedSize(true);
        recyclerView.setItemViewCacheSize(20);
        recyclerView.setDrawingCacheEnabled(true);
        recyclerView.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);
//        LinearLayoutManager manager = new LinearLayoutManager(FriendFragment.this.getActivity());
//        recyclerView.setLayoutManager(manager);
//        recyclerView.setHasFixedSize(true);
//        recyclerView.setItemViewCacheSize(20);
//        recyclerView.setDrawingCacheEnabled(true);
//        recyclerView.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);
        return view;
    }

//    // TODO: Rename method, update argument and hook method into UI event
//    public void onButtonPressed(Uri uri) {
//        if (mListener != null) {
//            mListener.onFragmentInteraction(uri);
//        }
//    }

//    @Override
//    public void onAttach(Context context) {
//        super.onAttach(context);
//        if (context instanceof OnFragmentInteractionListener) {
//            mListener = (OnFragmentInteractionListener) context;
//        } else {
//            throw new RuntimeException(context.toString()
//                    + " must implement OnFragmentInteractionListener");
//        }
//    }
//
//    @Override
//    public void onDetach() {
//        super.onDetach();
//        mListener = null;
//    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
//    public interface OnFragmentInteractionListener {
//        // TODO: Update argument type and name
//        void onFragmentInteraction(Uri uri);
//    }
}
