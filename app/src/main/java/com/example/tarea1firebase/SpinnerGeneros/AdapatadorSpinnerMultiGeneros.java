package com.example.tarea1firebase.SpinnerGeneros;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import com.example.tarea1firebase.R;

import java.util.List;

public class AdapatadorSpinnerMultiGeneros extends ArrayAdapter<ControladorSpinnerMultiGeneros> {
    private Context mContext;
    private List<ControladorSpinnerMultiGeneros> listState;
    private List<String> selectedGeneros;
    private boolean isFromView = false;

    public AdapatadorSpinnerMultiGeneros(Context context, int resource, List<ControladorSpinnerMultiGeneros> objects, List<String> selectedGeneros) {
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

        final ControladorSpinnerMultiGeneros stateVO = listState.get(position);
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
        for (ControladorSpinnerMultiGeneros stateVO : listState) {
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
