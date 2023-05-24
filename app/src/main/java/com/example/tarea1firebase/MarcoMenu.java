package com.example.tarea1firebase;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.tarea1firebase.fragments.ExploraFragment;
import com.example.tarea1firebase.fragments.ChatsRecientesFragment;
import com.example.tarea1firebase.fragments.MuroFragment;
import com.example.tarea1firebase.fragments.FavoritosFragment;
import com.example.tarea1firebase.fragments.PerfilFragment;
import com.example.tarea1firebase.databinding.ActivityMarcoMenuBinding;


public class MarcoMenu extends AppCompatActivity {

    ActivityMarcoMenuBinding binding;

    /**
     * Método llamado cuando se crea la actividad.
     *
     * @param savedInstanceState Objeto Bundle que contiene el estado anteriormente guardado de la actividad.
     */
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
            replaceFragment(new MuroFragment());
        }
        binding.bottomNavigationView.setOnItemSelectedListener(item -> {

            switch (item.getItemId()) {
                case R.id.menu_noticias:
                    replaceFragment(new FavoritosFragment());
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
                case R.id.menu_publicaciones:
                    replaceFragment(new MuroFragment());
                    break;
            }

            return true;
        });
    }

    /**
     * Reemplaza el fragmento actual por el fragmento especificado.
     *
     * @param fragment Fragmento a reemplazar.
     */
    private void replaceFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frame_menu, fragment);
        fragmentTransaction.commit();
    }
}
