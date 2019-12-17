package edu.nccu.mis.passpair.Homepage.Fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
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

import edu.nccu.mis.passpair.Homepage.Adapter.PostAdapter;
import edu.nccu.mis.passpair.R;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link PostFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link PostFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class PostFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    RecyclerView recyclerView;
    TextView none_post;
    DatabaseReference Ref_user = FirebaseDatabase.getInstance().getReference().child("User");

//    private static final String ARG_PARAM1 = "param1";
//    private static final String ARG_PARAM2 = "param2";
//
//    // TODO: Rename and change types of parameters
//    private String mParam1;
//    private String mParam2;
//
//    private OnFragmentInteractionListener mListener;

    public PostFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment PostFragment.
     */
    // TODO: Rename and change types and number of parameters
//    public static PostFragment newInstance(String param1, String param2) {
//        PostFragment fragment = new PostFragment();
//        Bundle args = new Bundle();
//        args.putString(ARG_PARAM1, param1);
//        args.putString(ARG_PARAM2, param2);
//        fragment.setArguments(args);
//        return fragment;
//    }

//    @Override
//    public void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        if (getArguments() != null) {
//            mParam1 = getArguments().getString(ARG_PARAM1);
//            mParam2 = getArguments().getString(ARG_PARAM2);
//        }
//    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(edu.nccu.mis.passpair.R.layout.fragment_post, container, false);
        final String UID = this.getArguments().getString("UID").toString();

        recyclerView = (RecyclerView) view.findViewById(R.id.recyclepost);
        none_post = (TextView) view.findViewById(R.id.post_none);

        LinearLayoutManager manager = new GridLayoutManager(PostFragment.this.getActivity(),3);
        recyclerView.setLayoutManager(manager);
        recyclerView.setHasFixedSize(true);

        recyclerView.setItemViewCacheSize(20);
        recyclerView.setDrawingCacheEnabled(true);
        recyclerView.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);

        final ArrayList<String> postlist = new ArrayList<String>();
        Ref_user.child(UID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChild("貼文")){
                    none_post.setText("");
                    Long post_count_long = dataSnapshot.child("貼文").getChildrenCount();
                    int post_count = post_count_long.intValue();
                    ArrayList<String> post_key_list = new ArrayList<String>();
                    for (DataSnapshot ds : dataSnapshot.child("貼文").getChildren()){
                        post_key_list.add(ds.getKey());
                    }
                    for (int i = 0;i < post_key_list.size() ; i++){
                        //不考慮刪除貼文功能
                        String chlid_name = post_key_list.get(i);
                        String url = dataSnapshot.child("貼文").child(chlid_name).child("image").getValue(String.class);
                        url.trim();
                        postlist.add(url);
                    }
                    if (postlist.size() >= post_count){
                        PostAdapter adapter = new PostAdapter(PostFragment.this.getActivity(), postlist,post_key_list,UID);
                        recyclerView.setAdapter(adapter);
                    }
                }else {
                    none_post.setText("尚未上傳貼文");
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
//        generatePost();

//        PostAdapter adapter = new PostAdapter(PostFragment.this.getActivity(), postlist);
//        recyclerView.setAdapter(adapter);
//        recyclerView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener(){
//            @Override
//            public void onGlobalLayout() {
//
//            }
//        });
        return view;
    }
//    private void generatePost(){
//        final ArrayList<String> list = new ArrayList<String>();
//        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot dataSnapshot) {
//                for (int i = 0;i < 9 ; i++){
//                    String chlid_name = i +"";
//                    String url = dataSnapshot.child(chlid_name).getValue(String.class);
//                    url.trim();
//                    list.add(url);
//                }
//            }
//
//            @Override
//            public void onCancelled(DatabaseError databaseError) {
//
//            }
//        });
//    }

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
//
//    /**
//     * This interface must be implemented by activities that contain this
//     * fragment to allow an interaction in this fragment to be communicated
//     * to the activity and potentially other fragments contained in that
//     * activity.
//     * <p>
//     * See the Android Training lesson <a href=
//     * "http://developer.android.com/training/basics/fragments/communicating.html"
//     * >Communicating with Other Fragments</a> for more information.
//     */
//    public interface OnFragmentInteractionListener {
//        // TODO: Update argument type and name
//        void onFragmentInteraction(String data);
//    }
}
