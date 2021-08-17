package com.csi.meghnacooler.Technician;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.BaseAdapter;

import com.csi.meghnacooler.R;
import java.util.ArrayList;

/**
 * Created by Jahid on 5/2/2019.
 */

public class SQLiteListAdapter extends BaseAdapter {

    Context context;
    ArrayList<String> id;
    ArrayList<String> productName;
    ArrayList<String> quantity;

    public SQLiteListAdapter(Context context2, ArrayList<String> id, ArrayList<String> productName, ArrayList<String> quantity) {
        this.context = context2;
        this.id = id;
        this.productName = productName;
        this.quantity = quantity;
    }

    public int getCount() {
        // TODO Auto-generated method stub
        return id.size();
    }

    public Object getItem(int position) {
        // TODO Auto-generated method stub
        return null;
    }

    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return 0;
    }

    public View getView(int position, View child, ViewGroup parent) {

        Holder holder;

        LayoutInflater layoutInflater;

        if (child == null) {
            layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            child = layoutInflater.inflate(R.layout.requisition_card, null);

            holder = new Holder();

            holder.textViewProductName = (TextView) child.findViewById(R.id.textViewProductName);
            holder.textViewQuantity = (TextView) child.findViewById(R.id.textViewQuantity);
            child.setTag(holder);

        } else {

            holder = (Holder) child.getTag();
        }

        holder.textViewProductName.setText(productName.get(position));
        holder.textViewQuantity.setText(quantity.get(position));

        return child;
    }

    public class Holder {
        TextView textViewProductName,textViewQuantity;

    }

}
