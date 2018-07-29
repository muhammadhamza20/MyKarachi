package com.example.muhammadhamza.mykarachi;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by MuhammadHamza on 26/06/2018.
 */

public class RecyclerViewAdapterNOTNEEDEDFORNOW extends RecyclerView.Adapter<RecyclerViewAdapterNOTNEEDEDFORNOW.classViewHolder> {

    Context context;
    ArrayList<String> arrayList = new ArrayList<>();
    public RecyclerViewAdapterNOTNEEDEDFORNOW(Context mcontext, ArrayList<String> array) {
        context = mcontext;
        arrayList = array;
    }

    @NonNull
    @Override
    public classViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view= inflater.inflate(R.layout.feedslistitem, null);
        return new classViewHolder(view);

    }

    @Override
    public void onBindViewHolder(@NonNull classViewHolder holder, int position) {
        String str = arrayList.get(position);
        holder.textView.setText(str);
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    class classViewHolder extends RecyclerView.ViewHolder {
        TextView textView;
        public classViewHolder(View itemView) {
            super(itemView);
            textView = itemView.findViewById(R.id.textView);
        }
    }
}
