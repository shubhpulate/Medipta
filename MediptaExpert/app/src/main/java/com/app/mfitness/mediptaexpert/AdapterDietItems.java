package com.app.mfitness.mediptaexpert;

import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class AdapterDietItems extends RecyclerView.Adapter<AdapterDietItems.HolderDiet> implements Filterable{

    private ArrayList<Diet> mListDiet;
    private CardView cv;
    private ArrayList<Diet> mListFiltered;

    public interface OnPlaceClickListener {
         void onDeleteClick( Diet diet );
         void onEditClick(Diet diet);

    }

    private OnPlaceClickListener mOnPlaceClickListener;

    public void setOnPlaceClickListener( OnPlaceClickListener onPlaceClickListener ) {
        mOnPlaceClickListener = onPlaceClickListener;
    }

    public AdapterDietItems(ArrayList<Diet> listDiet ) {
        mListDiet = listDiet;
        mListFiltered = listDiet;
    }

    public class HolderDiet extends RecyclerView.ViewHolder {

        public TextView mTxtDietName, mTxtDietType;
        public ImageView mImgDelete,mImgEdit;

        public HolderDiet(View itemView) {
            super(itemView);

           mTxtDietName = itemView.findViewById(R.id.txtDietName);
           mTxtDietType = itemView.findViewById(R.id.txtDietType);
//           mImgDelete = itemView.findViewById(R.id.imgDelete);
           mImgEdit = itemView.findViewById(R.id.imgEditDietItem);

//            mImgDelete.setOnClickListener( new View.OnClickListener() {
//                @Override
//                public void onClick(View view) {
//
//                    if( mOnPlaceClickListener != null ) {
//                        mOnPlaceClickListener.onDeleteClick(mListDiet.get(getAdapterPosition()));
//                    }
//                }
//            });

            mImgEdit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mOnPlaceClickListener != null){
                        mOnPlaceClickListener.onEditClick(mListDiet.get(getAdapterPosition()));
                    }
                }
            });

        }
    }

    @Override
    public int getItemCount() {
        if (mListDiet.size() == 0){
            Log.w("tag","0");
        }else {
            Log.w("tag","1");
        }
        return mListDiet.size();
    }

    @Override
    public HolderDiet onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from( parent.getContext() ).inflate( R.layout.lay_diet, parent,false );

        return new HolderDiet( view );
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                FilterResults results=new FilterResults();
                if(constraint != null && constraint.length() > 0)
                {
                    constraint=constraint.toString().toUpperCase();
                    ArrayList<Diet> filteredPlayers=new ArrayList<>();

                    for (int i=0;i<mListFiltered.size();i++)
                    {
                        if(mListFiltered.get(i).itemName.toUpperCase().contains(constraint)){
                            filteredPlayers.add(mListFiltered.get(i));
                        }
                    }

                    results.count=filteredPlayers.size();
                    results.values=filteredPlayers;
                }else
                {
                    results.count=mListFiltered.size();
                    results.values=mListFiltered;

                }

                return results;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                mListDiet = (ArrayList<Diet>) results.values;
                notifyDataSetChanged();
            }
        };
    }

    @Override
    public void onBindViewHolder(HolderDiet holder, int position) {
//        Diet diet = mListFiltered.get( position );

        holder.mTxtDietName.setText(mListDiet.get(position).itemName);
        holder.mTxtDietType.setText(mListDiet.get(position).itemType );

//        Picasso.with( holder.itemView.getContext() )
//                .load( place.imageUrl )
//                .placeholder( R.mipmap.ic_launcher )
//                .into( holder.mImgPlaceIcon );

    }
}