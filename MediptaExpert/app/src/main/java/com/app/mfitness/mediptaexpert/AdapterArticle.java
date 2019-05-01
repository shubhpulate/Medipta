package com.app.mfitness.mediptaexpert;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;
import com.squareup.picasso.Picasso;
import java.util.ArrayList;

public class AdapterArticle extends RecyclerView.Adapter<AdapterArticle.HolderArticle> implements Filterable{
    private ArrayList<Article> mListArticle;
    private ArrayList<Article> mListFiltered;

    public interface OnPlaceClickListener {
         void onPlaceClick( Article article);
         void onDeleteClick(Article article);

    }

    public AdapterArticle(ArrayList<Article> listArticle ) {
        mListArticle = listArticle;
        mListFiltered = listArticle;
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
                    ArrayList<Article> filteredPlayers=new ArrayList<>();

                    for (int i=0;i<mListFiltered.size();i++)
                    {
                        if(mListFiltered.get(i).title.toUpperCase().contains(constraint))
                        {
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
                mListArticle = (ArrayList<Article>) results.values;
                notifyDataSetChanged();
            }
        };
    }

    private OnPlaceClickListener mOnPlaceClickListener;

    public void setOnPlaceClickListener(OnPlaceClickListener onPlaceClickListener ) {
        mOnPlaceClickListener = onPlaceClickListener;
    }

    @NonNull
    @Override
    public HolderArticle onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.lay_article,parent,false);

        return new HolderArticle(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HolderArticle holder, int position) {
     //   Article article = mListFiltered.get(position);
        holder.mTxtTitle.setText(mListArticle.get(position).title);
        holder.mTxtAuthor.setText(mListArticle.get(position).author);

        Picasso.with(holder.itemView.getContext())
                .load(RestAPI.dev_api + mListArticle.get(position).image)
                .placeholder(R.drawable.no_image_available)
                .into(holder.mImg);

    }

    @Override
    public int getItemCount() {
        return mListArticle.size();

    }

    public class HolderArticle extends RecyclerView.ViewHolder {
        private TextView mTxtTitle,mTxtAuthor;
        private ImageView mImg,mImgDelete;
        public HolderArticle(View itemView) {
            super(itemView);
            mTxtTitle = itemView.findViewById(R.id.tvTitle);
            mTxtAuthor = itemView.findViewById(R.id.tvAuthor);
            mImg = itemView.findViewById(R.id.ivBookCover);
            mImgDelete = itemView.findViewById(R.id.imgDeleteArticle);

            itemView.setOnClickListener( new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    if( mOnPlaceClickListener != null ) {
                        mOnPlaceClickListener.onPlaceClick(mListArticle.get(getAdapterPosition()));
                    }
                }
            });

            mImgDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if( mOnPlaceClickListener != null ) {
                        mOnPlaceClickListener.onDeleteClick(mListArticle.get(getAdapterPosition()));
                    }
                }
            });

        }
    }
}
