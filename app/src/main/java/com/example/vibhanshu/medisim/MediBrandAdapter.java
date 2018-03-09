package com.example.vibhanshu.medisim;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.text.NumberFormat;
import java.util.ArrayList;

/**
 * Created by Vibhanshu Rai on 28-01-2018.
 */

public class MediBrandAdapter extends ArrayAdapter<MediBrand> {

    public MediBrandAdapter(Context context, ArrayList<MediBrand> mediBrands){
        super(context,0,mediBrands);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View listItemView = convertView;
        if (listItemView == null){
            listItemView = LayoutInflater.from(getContext()).inflate(R.layout.list_view_items,parent,false);
        }
        MediBrand currentBrand = getItem(position);
        TextView name = (TextView) listItemView.findViewById(R.id.medicine_name);
        TextView price = (TextView) listItemView.findViewById(R.id.medicine_price);
        name.setText(currentBrand.getName());
        price.setText(NumberFormat.getCurrencyInstance().format(currentBrand.getPrice()));
        return listItemView;
    }
}
