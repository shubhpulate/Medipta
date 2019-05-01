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

public class AdapterCardio extends RecyclerView.Adapter<AdapterCardio.HolderCardio> {
    private ArrayList<Cardio> mListCardio;
    private CardView cv;

    public interface OnPlaceClickListener {
        public void onPlaceClick( Cardio cardio );

    }

    private OnPlaceClickListener mOnPlaceClickListener;

    public void setOnPlaceClickListener( OnPlaceClickListener onPlaceClickListener ) {
        mOnPlaceClickListener = onPlaceClickListener;
    }

    public AdapterCardio(ArrayList<Cardio> listCardio ) {
        mListCardio = listCardio;
    }
    @NonNull
    @Override
    public HolderCardio onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.lay_cardio,parent,false);

        return new HolderCardio(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HolderCardio holder, int position) {
        Cardio cardio = mListCardio.get( position );
        holder.mTxtCardioName.setText(cardio.cardioName);
        holder.mTxtCardioType.setText(cardio.cardioType);

                Picasso.with( holder.itemView.getContext() )
                .load(RestAPI.dev_api + cardio.cardioImage )
                .placeholder( R.mipmap.ic_launcher )
                .into( holder.mImgCardio );
    }

    @Override
    public int getItemCount() {
        return mListCardio.size();
    }

    public class HolderCardio extends RecyclerView.ViewHolder {
        public TextView mTxtCardioName,mTxtCardioType;
        public ImageView mImgCardio;

        public HolderCardio(View itemView) {
            super(itemView);
            mTxtCardioName = itemView.findViewById(R.id.txtCardioName);
            mTxtCardioType = itemView.findViewById(R.id.txtCardioType);
            mImgCardio = itemView.findViewById(R.id.imgCardio);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mOnPlaceClickListener != null){
                        mOnPlaceClickListener.onPlaceClick(mListCardio.get(getAdapterPosition()));

                    }
                }
            });
        }
    }

}
