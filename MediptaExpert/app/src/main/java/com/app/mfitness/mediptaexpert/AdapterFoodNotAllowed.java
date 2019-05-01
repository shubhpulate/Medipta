package com.app.mfitness.mediptaexpert;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class AdapterFoodNotAllowed extends RecyclerView.Adapter<AdapterFoodNotAllowed.HolderFood> {
    private ArrayList<FoodNotAllowed> mListFood;

    public interface OnPlaceClickListener {
        public void onPlaceClick( FoodNotAllowed foodNotAllowed );

    }

    private OnPlaceClickListener mOnPlaceClickListener;

    public void setOnPlaceClickListener( OnPlaceClickListener onPlaceClickListener ) {
        mOnPlaceClickListener = onPlaceClickListener;
    }

    public AdapterFoodNotAllowed(ArrayList<FoodNotAllowed> listFood ) {
        mListFood = listFood;
    }

    @NonNull
    @Override
    public HolderFood onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.lay_food_not_allowed,parent,false);
        return new HolderFood(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HolderFood holder, int position) {
        FoodNotAllowed food = mListFood.get(position);
        holder.mTxtFoodNotAllowed.setText(food.foodNotAllowed);
    }

    @Override
    public int getItemCount() {
        return mListFood.size();
    }

    public class HolderFood extends RecyclerView.ViewHolder {
        public TextView mTxtFoodNotAllowed;

        public HolderFood(View itemView) {
            super(itemView);
            mTxtFoodNotAllowed = itemView.findViewById(R.id.txtFoodNAllowed);
        }
    }
}
