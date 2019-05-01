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

public class AdapterFitnessPackage extends RecyclerView.Adapter<AdapterFitnessPackage.HolderPackage> implements Filterable {

    private ArrayList<FitnessPackage> mListPackage;
    private ArrayList<FitnessPackage> mListFiltered;
    private CardView cv;

    public interface OnPlaceClickListener {
        public void onPlaceClick( FitnessPackage fitnessPackage );

    }

    private OnPlaceClickListener mOnPlaceClickListener;

    public void setOnPlaceClickListener( OnPlaceClickListener onPlaceClickListener ) {
        mOnPlaceClickListener = onPlaceClickListener;
    }

    public AdapterFitnessPackage(ArrayList<FitnessPackage> listPackage ) {
        mListPackage = listPackage;
        mListFiltered = listPackage;
    }

    @NonNull
    @Override
    public HolderPackage onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from( parent.getContext() ).inflate( R.layout.lay_fitness_package, parent,false );

        return new HolderPackage( view );
    }

    @Override
    public void onBindViewHolder(@NonNull HolderPackage holder, int position) {
        FitnessPackage fitnessPackage = mListFiltered.get( position );

        holder.mTxtPackageName.setText(fitnessPackage.packageName );
        holder.mTxtPackageType.setText("Package Type : "+ fitnessPackage.packageType );
        holder.mTxtPrice.setText("Price : Rs."+ fitnessPackage.price);
        holder.mTxtCalls.setText("Expert Calls : "+ fitnessPackage.calls);
        holder.mTxtDietPlans.setText("Diet Plans : "+ fitnessPackage.dietPlans);
        holder.mTxtExcercisePlans.setText("Exercise Plans : "+ fitnessPackage.exercisePlans);
        holder.mTxtDuration.setText("Duration : "+ fitnessPackage.duration);
        Picasso.with( holder.itemView.getContext() )
                .load( RestAPI.dev_api + fitnessPackage.imageUrl )
                .placeholder( R.drawable.ic_package )
                .into( holder.mImgFitness );
    }

    @Override
    public int getItemCount() {
        return mListFiltered.size();
    }

    public class HolderPackage extends RecyclerView.ViewHolder {

        public TextView mTxtPackageName, mTxtPackageType,mTxtPrice,mTxtCalls,mTxtDietPlans,mTxtDuration,mTxtExcercisePlans;
        public ImageView mImgFitness;
        public HolderPackage(View itemView) {
            super(itemView);

            mTxtPackageName = itemView.findViewById( R.id.txtPackageName );
            mTxtPackageType = itemView.findViewById( R.id.txtPackageType );
            mTxtPrice = itemView.findViewById( R.id.txtPrice );
            mTxtCalls = itemView.findViewById( R.id.txtCalls );
            mTxtDietPlans = itemView.findViewById( R.id.txtDietPlans );
            mTxtDuration = itemView.findViewById( R.id.txtDuration );
            mTxtExcercisePlans = itemView.findViewById( R.id.txtExercisePlans );
            mImgFitness = itemView.findViewById(R.id.imgFitnessImage);
            cv = itemView.findViewById(R.id.cardview);
        }
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {

                String charString = charSequence.toString();

                if (charString.isEmpty()) {

                    mListFiltered = mListPackage;
                } else {

                    ArrayList<FitnessPackage> filteredList = new ArrayList<>();

                    for (FitnessPackage fitnessPackage : mListPackage) {

                        if (fitnessPackage.packageName.toLowerCase().contains(charString) || fitnessPackage.packageType.toLowerCase().contains(charString) || fitnessPackage.price.toLowerCase().contains(charString)) {

                            filteredList.add(fitnessPackage);
                        }
                    }

                    mListFiltered = filteredList;
                }

                FilterResults filterResults = new FilterResults();
                filterResults.values = mListFiltered;
                return filterResults;

            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults filterResults) {
                mListFiltered = (ArrayList<FitnessPackage>) filterResults.values;
                notifyDataSetChanged();
            }
        };
    }
}
