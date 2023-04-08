package com.example.tarea1firebase;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.tarea1firebase.Fragments.ExploraFragment;
import com.example.tarea1firebase.Fragments.ChatsRecientesFragment;
import com.example.tarea1firebase.Fragments.NoticiasFragment;
import com.example.tarea1firebase.Fragments.PerfilFragment;
import com.example.tarea1firebase.databinding.ActivityMarcoMenuBinding;


public class MarcoMenu extends AppCompatActivity {

    ActivityMarcoMenuBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMarcoMenuBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        // Verificar si se debe abrir el fragmento de perfil
        boolean abrirPerfil = getIntent().getBooleanExtra("abrir_perfil", false);
        if (abrirPerfil) {
            replaceFragment(new PerfilFragment());
        } else {
            replaceFragment(new NoticiasFragment());
        }
        binding.bottomNavigationView.setOnItemSelectedListener(item -> {

            switch (item.getItemId()) {
                case R.id.menu_noticias:
                    replaceFragment(new NoticiasFragment());
                    break;
                case R.id.menu_explora:
                    replaceFragment(new ExploraFragment());
                    break;
                case R.id.menu_mensajes:
                    replaceFragment(new ChatsRecientesFragment());
                    break;
                case R.id.menu_perfil:
                    replaceFragment(new PerfilFragment());
                    break;

            }

            return true;
        });


    }

    private void replaceFragment(Fragment fragment) {

        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frame_menu, fragment);
        //fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();

    }
}