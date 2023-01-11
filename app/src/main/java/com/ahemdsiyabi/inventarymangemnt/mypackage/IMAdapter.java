package com.ahemdsiyabi.inventarymangemnt.mypackage;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.ahemdsiyabi.inventarymangemnt.R;
import com.bumptech.glide.Glide;

import java.util.ArrayList;

public class IMAdapter extends RecyclerView.Adapter<IMAdapter.MyViewHolder> {

    private OnItemClickedCallback onClickCallback;
    private OnItemDeleteCallback onDeleteCallback;

    private ArrayList<IMItem> itemsArraylist;


    public IMAdapter() {
        itemsArraylist = new ArrayList<>();
    }

    public void addItems(ArrayList<IMItem> newItems) {
        itemsArraylist = newItems;
        notifyItemRangeChanged(0, newItems.size());
    }

    public void addItems(IMItem newItems) {
        itemsArraylist.add(newItems);
        int position = itemsArraylist.indexOf(newItems);
        notifyItemInserted(position);
    }

    public void reset() {
        int size = itemsArraylist.size();
        itemsArraylist.clear();
        notifyItemRangeRemoved(0, size);
    }


    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.inventory_item, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        IMItem imItem = itemsArraylist.get(position);
        holder.textItemName.setText(imItem.getItemName());
        holder.textItemPrice.setText(imItem.getItemPrice() +" OMR");
        holder.textQTY.setText(imItem.getItemQTY());
        holder.itemView.setOnClickListener(v -> {
            onClickCallback.onItemClicked(imItem);
        });
        holder.imgButtonRemove.setOnClickListener(view -> {
            onDeleteCallback.onItemDeleteClicked(imItem);
        });


        Glide
                .with(holder.itemView.getContext())
                .load(imItem.getItemImg())
                .placeholder(R.drawable.icon_add_item)
                .into(holder.imgItem);

    }

    @Override
    public int getItemCount() {
        return itemsArraylist.size();
    }


    public void setOnItemClicked(OnItemClickedCallback listener) {
        onClickCallback = listener;
    }

    public void setOnItemDeleteClicked(OnItemDeleteCallback listener) {
        onDeleteCallback = listener;
    }


    public static class MyViewHolder extends RecyclerView.ViewHolder {
        ImageView imgItem;
        TextView textItemName;
        TextView textItemPrice;
        TextView textQTY;
        ImageView imgButtonRemove;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            imgItem = itemView.findViewById(R.id.imgItem);
            textItemName = itemView.findViewById(R.id.textItemName);
            textItemPrice = itemView.findViewById(R.id.textItemPrice);
            textQTY = itemView.findViewById(R.id.textQTY);
            imgButtonRemove = itemView.findViewById(R.id.imgbuttonRemove);
        }
    }
}
