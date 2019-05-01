package com.app.mfitness.mediptaexpert;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Typeface;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.HashMap;
import java.util.List;

public class AdapterExpandableList extends BaseExpandableListAdapter {

    private Context context;
    private List<Group> listDataGroup;
    private HashMap<Group, List<Child>> listDataChild;

    public interface OnPlaceClickListener {
        public void onDeleteClick( Child child );
        public void onEditClick(Child child);

    }

    private OnPlaceClickListener mOnPlaceClickListener;

    public void setOnPlaceClickListener( OnPlaceClickListener onPlaceClickListener ) {
        mOnPlaceClickListener = onPlaceClickListener;
    }

    public AdapterExpandableList(Context context, List<Group> listDataHeader,
                                 HashMap<Group, List<Child>> listChildData) {

        this.context = context;
        this.listDataGroup = listDataHeader;
        this.listDataChild = listChildData;
    }

    @Override
    public Object getChild(int groupPosition, int childPosititon) {
        return this.listDataChild.get(this.listDataGroup.get(groupPosition))
                .get(childPosititon);
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public View getChildView(final int groupPosition, final int childPosition,
                             boolean isLastChild, View convertView, ViewGroup parent) {

         Child child = (Child) getChild(groupPosition, childPosition);

        if (convertView == null) {
            LayoutInflater infalInflater = (LayoutInflater) this.context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            convertView = infalInflater.inflate(R.layout.expandable_list_item, null);
        }

        TextView txtFood = convertView.findViewById(R.id.listFood);
        TextView txtQuantity = convertView.findViewById(R.id.listQuantity);
        ImageView imgDelete = convertView.findViewById(R.id.imgDelete);

        imgDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mOnPlaceClickListener != null){
//                    mOnPlaceClickListener.onEditClick(listDataChild.get(listDataGroup.get(groupPosition)));
                    mOnPlaceClickListener.onEditClick(listDataChild.get(
                            listDataGroup.get(groupPosition)).get(
                            childPosition));
                }
            }
        });

//        imgDelete.setOnClickListener(new View.OnClickListener() {
//
//            public void onClick(View v) {
//                AlertDialog.Builder builder = new AlertDialog.Builder(context);
//                builder.setMessage("Do you want to remove?");
//                builder.setCancelable(false);
//                builder.setPositiveButton("Yes",
//                        new DialogInterface.OnClickListener() {
//                            public void onClick(DialogInterface dialog, int id) {
//                                List<Child> child = listDataChild.get(listDataGroup.get(groupPosition));
//                                String tabletName = String.valueOf(listDataChild.get(listDataGroup.get(groupPosition).diet));
//                                System.out.println("place : "+tabletName);
//                                child.remove(childPosition);
//                                notifyDataSetChanged();
//                            }
//                        });
//
//                builder.setNegativeButton("No",
//                        new DialogInterface.OnClickListener() {
//                            public void onClick(DialogInterface dialog, int id) {
//                                dialog.cancel();
//                            }
//                        });
//
//                AlertDialog alertDialog = builder.create();
//                alertDialog.show();
//            }
//        });

        txtFood.setText(child.food);
        txtQuantity.setText(child.quantity);
        return convertView;

    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return this.listDataChild.get(this.listDataGroup.get(groupPosition))
                .size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return this.listDataGroup.get(groupPosition);
    }

    @Override
    public int getGroupCount() {
        return this.listDataGroup.size();
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded,
                             View convertView, ViewGroup parent) {
        Group group = listDataGroup.get(groupPosition);
        if (convertView == null) {
            LayoutInflater infalInflater = (LayoutInflater) this.context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(R.layout.expandable_list_group, null);
        }

        TextView txtDiet = convertView.findViewById(R.id.txtListDiet);
        TextView txtCalories = convertView.findViewById(R.id.txtListCalories);

        txtDiet.setTypeface(null, Typeface.BOLD);
        txtDiet.setText(group.diet);

        txtCalories.setText(group.Calories + " Calories");

        return convertView;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }
}

