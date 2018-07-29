package com.example.muhammadhamza.mykarachi;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class FeedsFragment extends Fragment {

    private RecyclerView recyclerView;
    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    private DatabaseReference myRef = database.getReference();
    private DatabaseReference myRefChild=myRef.child("Feeds");

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_feeds, container, false);
        myRefChild.keepSynced(true);
        recyclerView = rootView.findViewById(R.id.feedsRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity())); // vertical orientation by default
        recyclerView.setHasFixedSize(true);
//      RecyclerViewAdapterNOTNEEDEDFORNOW adapter = new RecyclerViewAdapterNOTNEEDEDFORNOW(getActivity(), array);
//      recyclerView.setAdapter(adapter);
        return rootView;
    }


    @Override
    public void onStart() {
        super.onStart();

        FirebaseRecyclerAdapter<String, FeedViewHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<String, FeedViewHolder>
                (String.class, R.layout.feedslistitem, FeedViewHolder.class, myRefChild) {
            @Override
            protected void populateViewHolder(FeedViewHolder viewHolder, String model, int position) {
                viewHolder.textView.setText(model);
            }
        };
        recyclerView.setAdapter(firebaseRecyclerAdapter);
    }
    public static class FeedViewHolder extends RecyclerView.ViewHolder {
        TextView textView;
        public FeedViewHolder(View itemView) {
            super(itemView);
            textView = itemView.findViewById(R.id.textView);
        }
    }
}