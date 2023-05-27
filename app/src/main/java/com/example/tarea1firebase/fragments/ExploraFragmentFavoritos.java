package com.example.tarea1firebase.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.SearchView;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tarea1firebase.R;
import com.example.tarea1firebase.adaptadores.AdaptadorUsuariosFavoritos;
import com.example.tarea1firebase.adaptadores.AdaptadorUsuariosRecycler;
import com.example.tarea1firebase.entidades.Usuario;
import com.example.tarea1firebase.gestor.GestorFirestore;
import com.github.ybq.android.spinkit.sprite.Sprite;
import com.github.ybq.android.spinkit.style.FadingCircle;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
/**
 * La clase ExploraFragment es una subclase de la clase Fragment que representa un fragmento de la interfaz de usuario
 * utilizado para mostrar la funcionalidad de exploración de una aplicación.
 */
public class ExploraFragmentFavoritos extends Fragment {

    private RecyclerView recyclerViewUsu;
    private AdaptadorUsuariosFavoritos adaptadorUsuariosFavoritos;
    private Usuario user;
    private ArrayList<Usuario> listaUsuarios;
    private SearchView barraBusqueda;
    private ProgressBar progressBar;
    private GestorFirestore gestorFirebase;
    private FirebaseAuth mAuth;
    private ImageView imgFavsVacios;
    private TextView lblFavsVacios;
    /**
     * Constructor público sin argumentos requerido por la documentación de Fragment.
     */
    public ExploraFragmentFavoritos() {
        // Required empty public constructor
    }
    /**
     * Método de ciclo de vida del Fragment, llamado al crear el Fragment.
     * @param savedInstanceState Bundle que contiene el estado anteriormente guardado del Fragment, si lo hay.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    /**
     * Método de ciclo de vida del Fragment, llamado para crear y devolver la vista asociada con el Fragment.
     * @param inflater LayoutInflater utilizado para inflar la vista.
     * @param container ViewGroup al que se adjuntará la vista.
     * @param savedInstanceState Bundle que contiene el estado anteriormente guardado del Fragment, si lo hay.
     * @return View asociada con el Fragment.
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.explora_fragment_favoritos, container, false);
    }
    /**
     * Método de ciclo de vida del Fragment, llamado después de que la vista haya sido creada y se haya agregado al Fragment.
     * Inicializa las vistas y los adaptadores, configura el oyente de la barra de búsqueda y carga los datos de Firebase.
     * @param view La vista que ha sido creada.
     * @param savedInstanceState Bundle que contiene el estado anteriormente guardado del Fragment, si lo hay.
     */
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        inicializarVistas(view);
        mAuth = FirebaseAuth.getInstance();
        gestorFirebase = new GestorFirestore();
        imgFavsVacios = view.findViewById(R.id.imagenRecyclerVacioFavsVacio);
        lblFavsVacios = view.findViewById(R.id.lblRecyclerVacioFavsVacio);
        lblFavsVacios.setText("No hay usuarios disponibles");
        setListenerBarraBusqueda();
        /*
         * Llamada al método "obtenerTodosLosUsuarios" del objeto "gestorFirebase" para obtener todos los usuarios.
         */
        gestorFirebase.obtenerTodosLosUsuarios(new GestorFirestore.Callback<ArrayList<Usuario>>() {
            /**
             * Método que se ejecuta cuando se obtiene un resultado exitoso en la operación.
             *
             * @param result El ArrayList de objetos Usuario que representa el resultado exitoso.
             */
            @Override
            public void onSuccess(ArrayList<Usuario> result) {
                /*
                 * Eliminar el usuario actual de la lista de resultados, si coincide con el usuario autenticado.
                 */
                for (int i = 0; i < result.size(); i++) {
                    if (result.get(i).getId().equals(mAuth.getCurrentUser().getUid())) {
                        result.remove(result.get(i));
                    }
                }
                /*
                 * Crear un nuevo adaptador de usuarios utilizando la lista actualizada.
                 */
                adaptadorUsuariosFavoritos = new AdaptadorUsuariosFavoritos(result);
                /*
                 * Establecer el adaptador en el RecyclerView correspondiente.
                 */
                recyclerViewUsu.setAdapter(adaptadorUsuariosFavoritos);
                /*
                 * Verificar si el adaptador contiene elementos.
                 */
                if (adaptadorUsuariosFavoritos.getItemCount() > 0) {
                    /*
                     * Ocultar la imagen y el texto que indican que no hay usuarios favoritos vacíos.
                     */
                    imgFavsVacios.setVisibility(View.GONE);
                    lblFavsVacios.setVisibility(View.GONE);
                } else {
                    /*
                     * Mostrar la imagen y el texto que indican que no hay usuarios favoritos vacíos.
                     */
                    imgFavsVacios.setVisibility(View.VISIBLE);
                    lblFavsVacios.setVisibility(View.VISIBLE);
                }
            }
        });


    }
    /**
     * Inicializa las vistas del fragmento.
     * @param view La vista raíz del fragmento.
     */
    private void inicializarVistas(View view) {
        recyclerViewUsu = view.findViewById(R.id.recyclerUsuarios);
        recyclerViewUsu.setHasFixedSize(true);
        recyclerViewUsu.setLayoutManager(new LinearLayoutManager(getActivity()));

        progressBar = view.findViewById(R.id.spin_kit);
        Sprite doubleBounce = new FadingCircle();
        progressBar.setIndeterminateDrawable(doubleBounce);
        progressBar.setVisibility(View.GONE);
        barraBusqueda = view.findViewById(R.id.barraBusqueda);
    }

    /**
     * Configura el oyente de la barra de búsqueda.
     */
    private void setListenerBarraBusqueda() {
        /*
         * Establece un listener de texto de consulta en la barra de búsqueda, que se ejecuta cuando se ingresan o modifican consultas de texto
         */
        barraBusqueda.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            /**
             * Método que se ejecuta cuando se envía una consulta de texto.
             *
             * @param query El texto de la consulta enviado.
             * @return Devuelve un valor booleano que indica si la consulta de texto ha sido procesada.
             */
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }
            /**
             * Método que se ejecuta cuando cambia el texto de la consulta.
             *
             * @param newText El nuevo texto de la consulta.
             * @return Devuelve un valor booleano que indica si el cambio de texto de la consulta ha sido procesado.
             */
            @Override
            public boolean onQueryTextChange(String newText) {
                adaptadorUsuariosFavoritos.filter(newText, progressBar);
                return false;
            }
        });
    }
}

