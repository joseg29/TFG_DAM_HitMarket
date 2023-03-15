package com.example.tarea1firebase;

import androidx.appcompat.app.AppCompatActivity;

import android.net.Uri;
import android.os.Bundle;
import android.widget.ImageView;

import java.io.IOException;

import pl.droidsonroids.gif.GifDrawable;

public class PruebaFondo extends AppCompatActivity {
    private ImageView videoMarco;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_prueba_fondo);

        videoMarco = findViewById(R.id.imVideo);
        String uriPath = "android.resource://com.example.tarea1firebase/" + R.raw.humobackground;
        Uri uri = Uri.parse(uriPath);
        try {
            GifDrawable gifDrawable = new GifDrawable(getResources(), R.raw.humobackground);
            videoMarco.setImageDrawable(gifDrawable);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}