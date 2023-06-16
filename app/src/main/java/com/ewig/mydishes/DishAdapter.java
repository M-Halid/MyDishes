package com.ewig.mydishes;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.ewig.mydishes.databinding.RecyclerRowBinding;

import java.util.ArrayList;

public class DishAdapter extends RecyclerView.Adapter<DishAdapter.DishHolder> {
    ArrayList<Dish> dishArrayList;
    public DishAdapter(ArrayList<Dish> dishArrayList){
        this.dishArrayList=dishArrayList;
    }
    @NonNull
    @Override
    public DishHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        RecyclerRowBinding recyclerRowBinding=RecyclerRowBinding.inflate(LayoutInflater.from(parent.getContext()),
                parent,false);
        return new DishHolder(recyclerRowBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull DishHolder holder, int position) {
    holder.binding.textView.setText(dishArrayList.get(position).name);
    holder.itemView.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent intent =new Intent(holder.itemView.getContext(), DishesActivity.class);
            intent.putExtra("info","old");
            intent.putExtra("dishId",dishArrayList.get(holder.getAdapterPosition()).id);
            holder.itemView.getContext().startActivity(intent);
        }
    });
    }

    @Override
    public int getItemCount() {
        return dishArrayList.size();
    }

    public class DishHolder extends RecyclerView.ViewHolder {
        private RecyclerRowBinding binding;
        public DishHolder(RecyclerRowBinding binding) {
            super(binding.getRoot());
            this.binding=binding;
        }
    }
}
