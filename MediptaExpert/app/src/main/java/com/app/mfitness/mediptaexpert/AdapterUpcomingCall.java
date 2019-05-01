package com.app.mfitness.mediptaexpert;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class AdapterUpcomingCall extends RecyclerView.Adapter<AdapterUpcomingCall.HolderUpcoming> {
    private ArrayList<UpcomingCall> mListUpcoming;
    private CardView cv;
    Context context;


    public interface OnPlaceClickListener {
        public void onPlaceClick( UpcomingCall upcomingCall );

    }

    private OnPlaceClickListener mOnPlaceClickListener;

    public void setOnPlaceClickListener( OnPlaceClickListener onPlaceClickListener ) {
        mOnPlaceClickListener = onPlaceClickListener;
    }

    public AdapterUpcomingCall(ArrayList<UpcomingCall> listUpcoming ) {
        mListUpcoming = listUpcoming;
    }

    @NonNull
    @Override
    public HolderUpcoming onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

//        if (mListUpcoming.size()<=0){
//            View abc = LayoutInflater.from(parent.getContext() ).inflate(R.layout.lay_error,parent,false);
//            mTxtEmptyView.setText("abc");
//            return new HolderUpcoming(abc);
//        }else {

            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.lay_upcoming_call, parent, false);

            return new HolderUpcoming(view);

    }

    @Override
    public void onBindViewHolder(@NonNull HolderUpcoming holder, int position) {
        UpcomingCall call = mListUpcoming.get( position );

        holder.mTxtUpcomingName.setText( call.name );
        holder.mTxtUpcomingContact.setText( call.contact );
        holder.mTxtUpcomingTime.setText( call.time);
    }

   private TextView mTxtEmptyView;

    public class HolderUpcoming extends RecyclerView.ViewHolder {
        public TextView mTxtUpcomingName, mTxtUpcomingContact,mTxtUpcomingTime;

        public HolderUpcoming(View itemView) {
            super(itemView);
            mTxtEmptyView = itemView.findViewById(R.id.empty_view);
            mTxtUpcomingName = itemView.findViewById( R.id.txtUpcomingName );
            mTxtUpcomingContact = itemView.findViewById( R.id.txtUpcomingContact );
            mTxtUpcomingTime = itemView.findViewById( R.id.txtUpcomingTime );
            cv = itemView.findViewById(R.id.cardview);

            itemView.setOnClickListener( new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    if( mOnPlaceClickListener != null ) {
                        mOnPlaceClickListener.onPlaceClick(mListUpcoming.get(getAdapterPosition()));
                    }
                }
            });
        }
    }
    @Override
    public int getItemCount() {
//        if (mListUpcoming.size() <= 0){
//            mTxtEmptyView.setText("No Data");
//        }

//           mTxtEmptyView.setVisibility(mListUpcoming.size() > 0 ? View.GONE : View.VISIBLE);
        return mListUpcoming.size();

    }
}
