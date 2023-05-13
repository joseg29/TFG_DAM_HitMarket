package com.example.tarea1firebase.adaptadores;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class CustomSpinnerAdapter extends ArrayAdapter<String> {

    private Context context;
    private String[] values;

    public CustomSpinnerAdapter(Context context, String[] values) {
        super(context, android.R.layout.simple_spinner_item, values);
        this.context = context;
        this.values = values;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        TextView textView = (TextView) super.getView(position, convertView, parent);
        textView.setBackgroundColor(context.getResources().getColor(android.R.color.black));
        textView.setTextColor(context.getResources().getColor(android.R.color.white));
        return textView;
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        TextView textView = (TextView) super.getDropDownView(position, convertView, parent);
        textView.setBackgroundColor(context.getResources().getColor(android.R.color.black));
        textView.setTextColor(context.getResources().getColor(android.R.color.white));
        return textView;
    }
}

