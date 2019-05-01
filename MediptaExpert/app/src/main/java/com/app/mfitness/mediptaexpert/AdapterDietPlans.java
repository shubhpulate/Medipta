package com.app.mfitness.mediptaexpert;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

public class AdapterDietPlans extends RecyclerView.Adapter<AdapterDietPlans.HolderPlans> {

    private ArrayList<DietPlan> mListPlans;
    private CardView cv;
    private Context mContext;

    public interface OnPlaceClickListener {
        public void onPlaceClick( DietPlan dietPlans );
    }

    private OnPlaceClickListener mOnPlaceClickListener;

    public void setOnPlaceClickListener( OnPlaceClickListener onPlaceClickListener ) {
        mOnPlaceClickListener = onPlaceClickListener;
    }

    public AdapterDietPlans(ArrayList<DietPlan> listPlan ,Context context) {
        mListPlans = listPlan;
        mContext = context;
    }

    @NonNull
    @Override
    public HolderPlans onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from( parent.getContext() ).inflate( R.layout.lay_diet_plans, parent,false );

        return new HolderPlans(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HolderPlans holder, int position) {
        final DietPlan dietPlans = mListPlans.get( position );

        holder.mTxtDietPlan.setText( dietPlans.dietPlan );
        holder.mTxtIsActive.setText(dietPlans.isActive);
        if (dietPlans.isActive.equals("1")){
            holder.mTxtIsActive.setText("ACTIVE");
            holder.mTxtIsActive.setTextColor(ContextCompat.getColor(mContext,R.color.blue));
        }else {
            holder.mTxtIsActive.setText("InActive");
            holder.mTxtIsActive.setTextColor(ContextCompat.getColor(mContext,R.color.bg_row_background));
        }
//        holder.mImgEdtDietPlan.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
////                mContext.startActivity(new Intent(mContext, EditDietPlanActivity.class));
////                Intent i = new Intent(mContext,EditDietPlanActivity.class);
////                i.putExtra("planName",dietPlans.dietPlan);
////                i.putExtra("start",dietPlans.startDate);
////                i.putExtra("end",dietPlans.endDate);
////                i.putExtra("name",dietPlans.name);
////                mContext.startActivity(i);
//            }
//        });

    }

    @Override
    public int getItemCount() {
        return mListPlans.size();
    }

    public class HolderPlans extends RecyclerView.ViewHolder {
        public TextView mTxtDietPlan,mTxtIsActive;
        public ImageView mImgEdtDietPlan;

        public HolderPlans(View itemView) {
            super(itemView);
            mTxtDietPlan = itemView.findViewById(R.id.txtDietPlan);
            mTxtIsActive = itemView.findViewById(R.id.txtIsActive);

//            mImgEdtDietPlan.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    Intent i = new Intent(context.getApplicationContext(),EditDietPlanActivity.class);
//                    context.getApplicationContext().startActivity(i);
//                }
//            });

            itemView.setOnClickListener( new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    if( mOnPlaceClickListener != null ) {
                        mOnPlaceClickListener.onPlaceClick(mListPlans.get(getAdapterPosition()));
                    }
                }
            });
        }
    }
}
