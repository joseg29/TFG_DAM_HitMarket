package com.example.tarea1firebase.SpinnerMultiGeneros;

public class ControladorSpinnerMultiGeneros {
    private String title;
    private boolean selected;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    @Override
    public String toString() {
        return title;
    }
}