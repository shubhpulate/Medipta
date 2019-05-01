package com.app.mfitness.mediptaexpert;

import android.content.ClipData;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import javax.net.ssl.HttpsURLConnection;

public class AdapterTablets extends RecyclerView.Adapter<AdapterTablets.HolderTablets> {
    private ArrayList<Tablet> mListTablet;
    CardView cv;

    public interface OnPlaceClickListener {
        public void onPlaceClick( Tablet tablet );
    }

    private OnPlaceClickListener mOnPlaceClickListener;

    public void setOnPlaceClickListener( OnPlaceClickListener onPlaceClickListener ) {
        mOnPlaceClickListener = onPlaceClickListener;
    }

    public AdapterTablets(ArrayList<Tablet> listTablet ) {
        mListTablet = listTablet;
    }

    @NonNull
    @Override
    public HolderTablets onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.lay_tablet,parent,false);
        return new HolderTablets(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HolderTablets holder, int position) {
        Tablet tablet = mListTablet.get(position);
        holder.mTxtTabletName.setText(tablet.tabletName);
        holder.mTxtTabletTime.setText(tablet.tabletTime);
    }

    @Override
    public int getItemCount() {
        return mListTablet.size();
    }

    public class HolderTablets extends RecyclerView.ViewHolder {
        private TextView mTxtTabletName, mTxtTabletTime;
        public ImageView mImgDelete;
        public LinearLayout viewBackground, viewForeground;

        public HolderTablets(View itemView) {
            super(itemView);
            mTxtTabletName = itemView.findViewById(R.id.txtTabletName);
            mTxtTabletTime = itemView.findViewById(R.id.txtTabletTime);
            mImgDelete = itemView.findViewById(R.id.imgDeleteTablet);
//            viewBackground = itemView.findViewById(R.id.view_background);
            viewForeground = itemView.findViewById(R.id.view_foreground);

            mImgDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mOnPlaceClickListener != null){
                        mOnPlaceClickListener.onPlaceClick(mListTablet.get(getAdapterPosition()));
                    }
                }
            });
        }
    }

    public void removeItem(int position) {
        mListTablet.remove(position);
        // notify the item removed by position
        // to perform recycler view delete animations
        // NOTE: don't call notifyDataSetChanged()
        notifyItemRemoved(position);
    }

    public void restoreItem(Tablet tablet, int position) {
        mListTablet.add(position, tablet);
        // notify item added by position
        notifyItemInserted(position);
    }

}
