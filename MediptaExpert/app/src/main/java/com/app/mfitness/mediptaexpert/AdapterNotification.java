package com.app.mfitness.mediptaexpert;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

public class AdapterNotification extends RecyclerView.Adapter<AdapterNotification.HolderNotification>{
    private ArrayList<Notification> mListNotification;
    private Context mContext;

    public interface OnPlaceClickListener {
        public void onPlaceClick( Notification notification );
    }

    private OnPlaceClickListener mOnPlaceClickListener;

    public void setOnPlaceClickListener( OnPlaceClickListener onPlaceClickListener ) {
        mOnPlaceClickListener = onPlaceClickListener;
    }

    public AdapterNotification(ArrayList<Notification> listNotification ,Context context) {
        mListNotification = listNotification;
        mContext = context;
    }

    @NonNull
    @Override
    public HolderNotification onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.lay_notification,parent,false);

        return new HolderNotification(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HolderNotification holder, int position) {
        Notification notification = mListNotification.get(position);
        holder.mTxtMessage.setText(notification.message);

    }

    @Override
    public int getItemCount() {
        return mListNotification.size();
    }

    public class HolderNotification extends RecyclerView.ViewHolder {
        public TextView mTxtMessage;
        public HolderNotification(View itemView) {
            super(itemView);
            mTxtMessage = itemView.findViewById(R.id.txtNotification);
        }
    }
}
