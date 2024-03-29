/**
 * CustomSpinnerAdapter es una clase adaptadora que extiende ArrayAdapter para personalizar
 * la apariencia de los elementos del spinner.
 * @author Alex
 */
package com.example.tarea1firebase.adaptadores;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class CustomSpinnerAdapter extends ArrayAdapter<String> {

    private Context context;
    private String[] values;
    /**
     * Constructor para crear una nueva instancia de CustomSpinnerAdapter.
     * @param context Contexto actual de la aplicación.
     * @param values Arreglo de cadenas de texto que representan los valores del spinner.
     */
    public CustomSpinnerAdapter(Context context, String[] values) {
        super(context, android.R.layout.simple_spinner_item, values);
        this.context = context;
        this.values = values;
    }
    /**
     * Devuelve la vista personalizada que se muestra cuando el spinner está cerrado.
     * @param position Posición del elemento en el spinner.
     * @param convertView Vista del elemento que se está reutilizando.
     * @param parent Grupo de vistas al que pertenece el elemento.
     * @return La vista personalizada del elemento.
     */
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        TextView textView = (TextView) super.getView(position, convertView, parent);
        textView.setBackgroundColor(context.getResources().getColor(android.R.color.black));
        textView.setTextColor(context.getResources().getColor(android.R.color.white));
        return textView;
    }
    /**
     * Devuelve la vista personalizada que se muestra cuando el spinner está desplegado.
     * @param position Posición del elemento en el spinner.
     * @param convertView Vista del elemento que se está reutilizando.
     * @param parent Grupo de vistas al que pertenece el elemento.
     * @return La vista personalizada del elemento.
     */
    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        TextView textView = (TextView) super.getDropDownView(position, convertView, parent);
        textView.setBackgroundColor(context.getResources().getColor(android.R.color.black));
        textView.setTextColor(context.getResources().getColor(android.R.color.white));
        return textView;
    }
}

