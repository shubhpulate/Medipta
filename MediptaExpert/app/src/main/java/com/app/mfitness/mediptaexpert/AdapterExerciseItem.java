package com.app.mfitness.mediptaexpert;

import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class AdapterExerciseItem extends RecyclerView.Adapter<AdapterExerciseItem.HolderItem> implements Filterable{
    private ArrayList<ExerciseItem> mListItem;
    private ArrayList<ExerciseItem> mListFiltered;
    private CardView cv;

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                String charString = charSequence.toString();

                if (charString.isEmpty()){
                    mListFiltered = mListItem;
                } else  {
                    ArrayList<ExerciseItem> filteredList = new ArrayList<>();

                    for (ExerciseItem exerciseItem : mListItem){
                        if (exerciseItem.exercise.toLowerCase().contains(charString) || exerciseItem.type.toLowerCase().contains(charString)){
                            filteredList.add(exerciseItem);
                        }
                    }
                    mListFiltered = filteredList;
                }
                FilterResults filterResults = new FilterResults();
                filterResults.values = mListFiltered;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                mListFiltered = (ArrayList<ExerciseItem>) results.values;
                notifyDataSetChanged();
            }
        };
    }

    public interface OnPlaceClickListener {
        void onPlaceClick( ExerciseItem item );
        void onEditClick(ExerciseItem item);

    }

    private OnPlaceClickListener mOnPlaceClickListener;

    public void setOnPlaceClickListener( OnPlaceClickListener onPlaceClickListener ) {
        mOnPlaceClickListener = onPlaceClickListener;
    }

    public AdapterExerciseItem(ArrayList<ExerciseItem> listItem ) {
        mListItem = listItem;
        mListFiltered = listItem;
    }

    @NonNull
    @Override
    public HolderItem onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.lay_exercise_item,parent,false);

        return new HolderItem(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HolderItem holder, int position) {
        ExerciseItem item = mListFiltered.get(position);
        holder.mTxtExeName.setText(item.exercise);
        holder.mTxtExeType.setText(item.type);

        Picasso.with( holder.itemView.getContext() )
                .load(RestAPI.dev_api + item.imageUrl )
                .resize(500,0)
                .placeholder( R.mipmap.ic_launcher )
                .into( holder.mImgItem );
    }

    @Override
    public int getItemCount() {
        return mListFiltered.size();
    }

    public class HolderItem extends RecyclerView.ViewHolder {
        public TextView mTxtExeName,mTxtExeType;
        public ImageView mImgItem,mImgEdit;

        public HolderItem(View itemView) {
            super(itemView);
            mTxtExeName = itemView.findViewById(R.id.txtExeName);
            mTxtExeType = itemView.findViewById(R.id.txtExeType);
            mImgItem = itemView.findViewById(R.id.imgExeItem);
            mImgEdit = itemView.findViewById(R.id.imgEdit);

            mImgEdit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mOnPlaceClickListener != null){
                        mOnPlaceClickListener.onEditClick(mListItem.get(getAdapterPosition()));
                    }
                }
            });
        }
    }
}
