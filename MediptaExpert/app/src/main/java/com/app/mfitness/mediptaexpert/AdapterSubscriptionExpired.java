package com.app.mfitness.mediptaexpert;

import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import java.util.ArrayList;

public class AdapterSubscriptionExpired extends RecyclerView.Adapter<AdapterSubscriptionExpired.HolderExpired> implements Filterable{
    private ArrayList<Person> mListPerson;
    private ArrayList<Person> mListFiltered;
    private CardView cv;

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                FilterResults results=new FilterResults();
                if(constraint != null && constraint.length() > 0)
                {
                    constraint=constraint.toString().toUpperCase();
                    ArrayList<Person> filteredPlayers=new ArrayList<>();

                    for (int i=0;i<mListFiltered.size();i++)
                    {
                        if(mListFiltered.get(i).name.toUpperCase().contains(constraint))
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
                mListPerson = (ArrayList<Person>) results.values;
                notifyDataSetChanged();
            }
        };
    }
    public interface OnPlaceClickListener {
        public void onPlaceClick( Person person );

    }

    private OnPlaceClickListener mOnPlaceClickListener;

    public void setOnPlaceClickListener( OnPlaceClickListener onPlaceClickListener ) {
        mOnPlaceClickListener = onPlaceClickListener;
    }

    public AdapterSubscriptionExpired(ArrayList<Person> listPerson ) {
        mListPerson = listPerson;
        mListFiltered = listPerson;
    }

    public class HolderExpired extends RecyclerView.ViewHolder {

        public TextView mTxtPersonName, mTxtPersonContact,mTxtPersonGoal;

        public HolderExpired(View itemView) {
            super(itemView);

            mTxtPersonName = itemView.findViewById( R.id.txtPersonName );
            mTxtPersonContact = itemView.findViewById( R.id.txtPersonContact );
            mTxtPersonGoal = itemView.findViewById( R.id.txtPersonGoal );
            cv = itemView.findViewById(R.id.cardview);

            itemView.setOnClickListener( new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    if( mOnPlaceClickListener != null ) {
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
    public HolderExpired onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from( parent.getContext() ).inflate( R.layout.lay_subscription_expired, parent,false );

        return new HolderExpired( view );
    }

    @Override
    public void onBindViewHolder(HolderExpired holder, int position) {
        Person person = mListPerson.get( position );

        holder.mTxtPersonName.setText("Name : "+ person.name );
        holder.mTxtPersonGoal.setText("Goal : "+ person.fitnessGoal );
        holder.mTxtPersonContact.setText("Contact : "+ person.contact);

//        Picasso.with( holder.itemView.getContext() )
//                .load( place.imageUrl )
//                .placeholder( R.mipmap.ic_launcher )
//                .into( holder.mImgPlaceIcon );

    }
}
