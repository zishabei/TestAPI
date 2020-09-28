package com.example.testapi;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class ShowItemAdapter extends RecyclerView.Adapter<ShowItemAdapter.ViewHolder> {
    private ArrayList<String> mData;

    public ShowItemAdapter(ArrayList<String> data) {
        this.mData = data;
    }

    public void setNotifyData(ArrayList<String> data) {
        this.mData = data;
        notifyDataSetChanged();
    }

    public void addItem(String data) {
        mData.add(data);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_log_recycler, parent, false);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.text.setText(mData.get(position));
    }

    @Override
    public int getItemCount() {
        if (mData == null || mData.size() == 0) {
            return 0;
        }
        return mData.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        TextView text;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            text = (TextView) itemView.findViewById(R.id.item_text);
        }
    }
}
