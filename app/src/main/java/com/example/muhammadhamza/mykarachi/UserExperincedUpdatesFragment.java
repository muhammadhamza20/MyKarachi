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

public class UserExperincedUpdatesFragment extends Fragment {
    private RecyclerView recyclerView;
    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    private DatabaseReference myRef = database.getReference();
    private DatabaseReference myRefChild=myRef.child("UserExperincedUpdates");

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_user_experinced_updates, container, false);
        myRefChild.keepSynced(true);
        recyclerView = rootView.findViewById(R.id.updateRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity())); // vertical orientation by default
        recyclerView.setHasFixedSize(true);
        return rootView;
    }

    @Override
    public void onStart() {
        super.onStart();

        FirebaseRecyclerAdapter<String, UpdateViewHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<String, UpdateViewHolder>
                (String.class, R.layout.updateslistitem, UpdateViewHolder.class, myRefChild) {
            @Override
            protected void populateViewHolder(UpdateViewHolder viewHolder, String model, int position) {
                viewHolder.textView.setText(model);
            }
        };
        recyclerView.setAdapter(firebaseRecyclerAdapter);
    }
    public static class UpdateViewHolder extends RecyclerView.ViewHolder {
        TextView textView;
        public UpdateViewHolder(View itemView) {
            super(itemView);
            textView = itemView.findViewById(R.id.textView);
        }
    }
}