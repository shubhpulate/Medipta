package com.app.mfitness.mediptaexpert;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

public class AdapterTodayCall extends RecyclerView.Adapter<AdapterTodayCall.HolderToday> {
    private ArrayList<UpcomingCall> mListToday;
    private CardView cv;

    public interface OnPlaceClickListener {
        public void onPlaceClick( UpcomingCall upcomingCall );

    }

    private OnPlaceClickListener mOnPlaceClickListener;

    public void setOnPlaceClickListener( OnPlaceClickListener onPlaceClickListener ) {
        mOnPlaceClickListener = onPlaceClickListener;
    }

    public AdapterTodayCall(ArrayList<UpcomingCall> listToday ) {
        mListToday = listToday;
    }

    @NonNull
    @Override
    public HolderToday onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from( parent.getContext() ).inflate( R.layout.lay_today_call, parent,false );
        return new HolderToday( view );

    }

    @Override
    public void onBindViewHolder(@NonNull HolderToday holder, int position) {
        UpcomingCall call = mListToday.get( position );

        holder.mTxtTodayName.setText("NAME : "+ call.name );
        holder.mTxtTodayContact.setText("CONTACT : "+ call.contact );
        holder.mTxtTodayTime.setText( call.time);

    }

    @Override
    public int getItemCount() {
        return mListToday.size();
    }

    public class HolderToday extends RecyclerView.ViewHolder {
        public TextView mTxtTodayName, mTxtTodayContact,mTxtTodayTime;
        public ImageView mImgCall;

        public HolderToday(View itemView) {
            super(itemView);

            mTxtTodayName = itemView.findViewById( R.id.txtTodayName );
            mTxtTodayContact = itemView.findViewById( R.id.txtTodayContact );
            mTxtTodayTime = itemView.findViewById( R.id.txtTodayTime );
            mImgCall = itemView.findViewById(R.id.imgCall);
            cv = itemView.findViewById(R.id.cardview);

            mImgCall.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                }
            });
            itemView.setOnClickListener( new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    if( mOnPlaceClickListener != null ) {
                        mOnPlaceClickListener.onPlaceClick(mListToday.get(getAdapterPosition()));
                    }
                }
            });
        }
    }
}
