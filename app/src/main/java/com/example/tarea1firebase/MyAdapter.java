package com.example.tarea1firebase;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class MyAdapter extends ArrayAdapter<StateVO> {
    private Context mContext;
    private List<StateVO> listState;
    private List<String> selectedGeneros;
    private boolean isFromView = false;

    public MyAdapter(Context context, int resource, List<StateVO> objects, List<String> selectedGeneros) {
        super(context, resource, objects);
        this.mContext = context;
        this.listState = objects;
        this.selectedGeneros = selectedGeneros;
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        return getCustomView(position, convertView, parent);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        return getCustomView(position, convertView, parent);
    }

    public View getCustomView(final int position, View convertView, ViewGroup parent) {
        final ViewHolder holder;
        if (convertView == null) {
            LayoutInflater layoutInflater = LayoutInflater.from(mContext);
            convertView = layoutInflater.inflate(R.layout.spinner_item, null);
            holder = new ViewHolder();
            holder.mTextView = convertView.findViewById(R.id.text);
            holder.mCheckBox = convertView.findViewById(R.id.checkbox);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        final StateVO stateVO = listState.get(position);
        holder.mTextView.setText(stateVO.getTitle());

        // To check whether checked event is fired from getView() or user input
        isFromView = true;
        holder.mCheckBox.setChecked(stateVO.isSelected());
        isFromView = false;

        if (position == 0) {
            holder.mCheckBox.setVisibility(View.INVISIBLE);
        } else {
            holder.mCheckBox.setVisibility(View.VISIBLE);
        }

        holder.mCheckBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (!isFromView) {
                stateVO.setSelected(isChecked);
                updateSelectedGeneros(); // Actualiza la lista de géneros seleccionados
            }
        });

        return convertView;
    }

    private void updateSelectedGeneros() {
        selectedGeneros.clear();
        for (StateVO stateVO : listState) {
            if (stateVO.isSelected()) {
                selectedGeneros.add(stateVO.getTitle());
            }
        }
    }

    private static class ViewHolder {
        private TextView mTextView;
        private CheckBox mCheckBox;
    }
}
