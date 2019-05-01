package com.app.mfitness.mediptaexpert;

import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class AdapterStrength extends RecyclerView.Adapter<AdapterStrength.HolderStrength> {

    private ArrayList<Strength> mListPerson;
    private CardView cv;

    public interface OnPlaceClickListener {
         void onPlaceClick(Strength strength);

    }

    private OnPlaceClickListener mOnPlaceClickListener;

    public void setOnPlaceClickListener(OnPlaceClickListener onPlaceClickListener) {
        mOnPlaceClickListener = onPlaceClickListener;
    }

    public AdapterStrength(ArrayList<Strength> listPerson) {
        mListPerson = listPerson;
    }

    public class HolderStrength extends RecyclerView.ViewHolder {

        public TextView mTxtExerciseName,mTxtExerciseType;
        public ImageView mImgStrength;

        public HolderStrength(View itemView) {
            super(itemView);

            mTxtExerciseName = itemView.findViewById(R.id.txtStrengthName);
            mTxtExerciseType = itemView.findViewById(R.id.txtStrengthType);
            mImgStrength = itemView.findViewById(R.id.imgStrength);

            cv = itemView.findViewById(R.id.cardview);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    if (mOnPlaceClickListener != null) {
                        mOnPlaceClickListener.onPlaceClick(mListPerson.get(getAdapterPosition()));

                    }
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return mListPerson.size();
    }

    @Override
    public HolderStrength onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.lay_strength, parent, false);

        return new HolderStrength(view);
    }

    @Override
    public void onBindViewHolder(HolderStrength holder, int position) {
        Strength strength = mListPerson.get(position);

        holder.mTxtExerciseName.setText("Name : " + strength.exercise);
        holder.mTxtExerciseType.setText("Type : " + strength.type);

        Picasso.with( holder.itemView.getContext() )
                .load( RestAPI.dev_api + strength.image )
                .placeholder( R.drawable.strength)
                .resize(500,0)
                .into( holder.mImgStrength );

    }
}
