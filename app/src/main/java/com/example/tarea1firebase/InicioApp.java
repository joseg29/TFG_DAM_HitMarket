package com.example.tarea1firebase;

import static com.example.tarea1firebase.Registro.COLECCION;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.tarea1firebase.entidades.Usuario;
import com.example.tarea1firebase.gestor.GestorFirestore;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class InicioApp extends AppCompatActivity {
    private Usuario user;
    private GestorFirestore gestorFirebase;


    /**
     * Método llamado cuando se crea la actividad.
     *
     * @param savedInstanceState Objeto Bundle que contiene el estado anteriormente guardado de la actividad.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.inicio_app);
        gestorFirebase = new GestorFirestore();
    }

    /**
     * Método llamado cuando la actividad se vuelve visible para el usuario.
     * Se inicia una pausa de 3 segundos y luego se ejecuta el método iniciarSesion().
     */
    @Override
    protected void onStart() {
        super.onStart();

        // Se crea un objeto Handler y se usa su método postDelayed para agregar una pausa de 3 segundos
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                // Código que se ejecutará después de 3 segundos
                iniciarSesion();
            }
        }, 3000);
    }


    /**
     * Verifica si hay una sesión iniciada y redirige a la siguiente actividad.
     * Si hay una sesión iniciada, obtiene el objeto Usuario de Firestore y lo pasa a la siguiente actividad.
     * Si no hay una sesión iniciada, redirige a la actividad de inicio de sesión.
     */
    public void iniciarSesion() {
        // Se revisa si hay alguna sesión abierta (currentUser / usuarioActual).

        // Si hay alguna sesión iniciada, se envía directamente a la ventana siguiente.
        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            String idDocumento = FirebaseAuth.getInstance().getCurrentUser().getUid();

            // Se hace un get de Firestore a partir del uid del currentUser. Su id se pasa como referencia de documento, para obtener el objeto Usuario y pasarlo a la siguiente activity.
            gestorFirebase.obtenerUsuarioPorId(idDocumento, new GestorFirestore.Callback<Usuario>() {
                @Override
                public void onSuccess(Usuario result) {
                    user = result;
                    Toast.makeText(InicioApp.this, "Sesión iniciada", Toast.LENGTH_LONG).show();
                    Intent intent = new Intent(InicioApp.this, MarcoMenu.class);
                    intent.putExtra("USUARIO", user.getId());
                    startActivity(intent);
                    finish();
                }
            }, Usuario.class);
        }
        // Si no hay ninguna sesión iniciada, se envía a la ventana de Login.
        else {
            Intent intent = new Intent(InicioApp.this, Login.class);
            startActivity(intent);
            finish();
        }
    }
}
