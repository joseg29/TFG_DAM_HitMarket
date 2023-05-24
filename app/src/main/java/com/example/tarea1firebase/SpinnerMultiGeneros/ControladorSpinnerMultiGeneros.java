package com.example.tarea1firebase.SpinnerMultiGeneros;

public class ControladorSpinnerMultiGeneros {
    private String title;
    private boolean selected;

    /**
     * Devuelve el título del elemento.
     *
     * @return Título del elemento.
     */
    public String getTitle() {
        return title;
    }

    /**
     * Establece el título del elemento.
     *
     * @param title Título del elemento.
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * Verifica si el elemento está seleccionado.
     *
     * @return `true` si el elemento está seleccionado, `false` en caso contrario.
     */
    public boolean isSelected() {
        return selected;
    }

    /**
     * Establece el estado de selección del elemento.
     *
     * @param selected `true` para marcar el elemento como seleccionado, `false` para desmarcarlo.
     */
    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    /**
     * Devuelve una representación en forma de cadena del objeto.
     *
     * @return Cadena que representa el objeto.
     */
    @Override
    public String toString() {
        return title;
    }
}
