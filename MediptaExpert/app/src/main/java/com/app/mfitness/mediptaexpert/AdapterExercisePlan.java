package com.app.mfitness.mediptaexpert;

import android.content.Context;
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

public class AdapterExercisePlan extends RecyclerView.Adapter<AdapterExercisePlan.HolderExePlan> {
    private ArrayList<ExercisePlan> mListPlans;
    private CardView cv;
    private Context mContext;

    public interface OnPlaceClickListener {
        public void onPlaceClick( ExercisePlan exercisePlan );
        void onEditClick( ExercisePlan exercisePlan);
        void onDeleteClick( ExercisePlan exercisePlan);

    }

    private OnPlaceClickListener mOnPlaceClickListener;

    public void setOnPlaceClickListener( OnPlaceClickListener onPlaceClickListener ) {
        mOnPlaceClickListener = onPlaceClickListener;
    }

    public AdapterExercisePlan(ArrayList<ExercisePlan> listPlan,Context context ) {
        mListPlans = listPlan;
        mContext = context;
    }

    @NonNull
    @Override
    public HolderExePlan onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.lay_exercise_plan,parent,false);

        return new HolderExePlan(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HolderExePlan holder, int position) {
        ExercisePlan exe = mListPlans.get(position);
        holder.mTxtExercisePlan.setText(exe.exercisePlan);
        if (exe.isActive.equals("1")){
            holder.mTxtIsActive.setText("ACTIVE");
            holder.mTxtIsActive.setTextColor(ContextCompat.getColor(mContext,R.color.blue));
        }else {
            holder.mTxtIsActive.setText("InActive");
            holder.mTxtIsActive.setTextColor(ContextCompat.getColor(mContext,R.color.bg_row_background));
        }

    }

    @Override
    public int getItemCount() {
        return mListPlans.size();
    }

    public class HolderExePlan extends RecyclerView.ViewHolder {
        public TextView mTxtExercisePlan,mTxtIsActive;
        public ImageView mImgEdit,mImgDelete;

        public HolderExePlan(View itemView) {
            super(itemView);
            mTxtExercisePlan = itemView.findViewById(R.id.txtExercisePlan);
            mImgEdit = itemView.findViewById(R.id.imgEditExePlan);
            mImgDelete = itemView.findViewById(R.id.imgDeleteExePlan);
            mTxtIsActive = itemView.findViewById(R.id.txtIsActive);
            itemView.setOnClickListener( new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    if( mOnPlaceClickListener != null ) {
                        mOnPlaceClickListener.onPlaceClick(mListPlans.get(getAdapterPosition()));
                    }
                }
            });

            mImgEdit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if( mOnPlaceClickListener != null ) {
                        mOnPlaceClickListener.onEditClick(mListPlans.get(getAdapterPosition()));
                    }
                }
            });

            mImgDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if( mOnPlaceClickListener != null ) {
                        mOnPlaceClickListener.onDeleteClick(mListPlans.get(getAdapterPosition()));
                    }
                }
            });
        }
    }
}
