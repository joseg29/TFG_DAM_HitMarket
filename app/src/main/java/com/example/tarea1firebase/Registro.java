package com.example.tarea1firebase;

import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.tarea1firebase.SpinnerMultiGeneros.AdapatadorSpinnerMultiGeneros;
import com.example.tarea1firebase.SpinnerMultiGeneros.ControladorSpinnerMultiGeneros;
import com.example.tarea1firebase.entidades.Usuario;
import com.github.ybq.android.spinkit.sprite.Sprite;
import com.github.ybq.android.spinkit.style.FadingCircle;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import pl.droidsonroids.gif.GifDrawable;

public class Registro extends AppCompatActivity {
    private FirebaseFirestore db;
    //Este será el nombre de la colección que daremos en la BBDD de Firebase
    public final static String COLECCION = "Usuarios";
    private StorageReference storageRef;
    private FirebaseStorage storage;
    private EditText etEmail, etNombre, etContrasena, etConfirmPassword;
    private ImageButton btnMostrarContrasena;
    private boolean mostrarContrasena = false;
    private TextView tvIniciarSesion, lblContrasena, lblConfirmarContrasena;
    private Usuario user;
    private Button btnRegistrar;
    private FirebaseAuth mAuth;
    private Bundle googleAccount;
    private String nombre, email, id;
    private ProgressBar progressBar;
    private ImageView videoMarco;
    private Spinner mySpinner, mySpinnerGenero;
    private List<String> selectedGeneros;
    private List<ControladorSpinnerMultiGeneros> listVOs;
    private AdapatadorSpinnerMultiGeneros myAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.registro);

        db = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();
        storageRef = storage.getReference();
        storageRef = FirebaseStorage.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();

        inicializarVistas();

        inicializarListenerBotones();


    }

    /**
     * Este método inicializa los listeners de los botones
     */
    private void inicializarListenerBotones() {
        /**
         * Botón que te envía a la activity de login
         */
        tvIniciarSesion.setOnClickListener(v -> {
            //Cambio a activity de login
            Intent intent = new Intent(Registro.this, Login.class);
            startActivity(intent);
            finish();
        });

        /**
         * Listener para el botón btnRegistrar.
         */
        btnRegistrar.setOnClickListener(v -> {
            crearUsuario();
        });

        /**
         *Botón para ocultar/mostrar contraseña
         */
        btnMostrarContrasena.setOnClickListener(view -> {
            mostrarContrasena = !mostrarContrasena;
            int cursorPosition = etContrasena.getSelectionStart();
            if (mostrarContrasena) {
                // Si se permite mostrar la contraseña, se cambia el inputType del EditText
                etContrasena.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                btnMostrarContrasena.setImageResource(R.drawable.ojo_cerrado);
            } else {
                etContrasena.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                btnMostrarContrasena.setImageResource(R.drawable.ojo_abierto);
            }
            etContrasena.setSelection(cursorPosition);
        });

        /**
         * Inicialización del spinner mySpinner.
         */
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.autonomous_communities, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mySpinner.setAdapter(adapter);
    }


    private void inicializarVistas() {
        /**
         Fondo animado
         */
        videoMarco = findViewById(R.id.imVideo);
        try {
            GifDrawable gifDrawable = new GifDrawable(getResources(), R.raw.humobackground);
            videoMarco.setImageDrawable(gifDrawable);
        } catch (IOException e) {
            e.printStackTrace();
        }

        //Icono de carga
        progressBar = findViewById(R.id.spin_kit);
        Sprite doubleBounce = new FadingCircle();
        progressBar.setIndeterminateDrawable(doubleBounce);
        progressBar.setVisibility(View.GONE);

        tvIniciarSesion = findViewById(R.id.linkIniciaSesion);
        btnRegistrar = findViewById(R.id.btnRegistrate);
        btnMostrarContrasena = findViewById(R.id.btnMostrarContrasena);
        etContrasena = findViewById(R.id.etPassword);
        etConfirmPassword = findViewById(R.id.etConfirmarPassword);
        lblContrasena = findViewById(R.id.lblPassword);
        mySpinner = findViewById(R.id.spinnerOpciones);
        mySpinnerGenero = findViewById(R.id.spinnerOpcionesGenero);
        lblConfirmarContrasena = findViewById(R.id.lblConfirmarPassword);
        etNombre = findViewById(R.id.etNombre);
        etEmail = findViewById(R.id.etEmail);
        etContrasena = findViewById(R.id.etPassword);

        /**
         * Verificación de inicio de sesión con Google.
         */
        googleAccount = null;

        try {
            /**
             * Obtención del objeto Bundle CUENTAGOOGLE del Intent, para luego comprobar si existe o no.
             */
            googleAccount = getIntent().getBundleExtra("CUENTAGOOGLE");

            /**
             Verificamos si se ha obtenido la cuenta de google. En caso de que no sea null, es porque el usuario ha elegido iniciar sesión con google.
             */
            if (googleAccount != null) {
                /**
                 * Obtención de los valores NOMBRE, EMAIL e ID del objeto Bundle.
                 */
                nombre = googleAccount.getString("NOMBRE");
                email = googleAccount.getString("EMAIL");
                id = googleAccount.getString("ID");

                /**
                 * Establecimiento de los valores de los campos etEmail y etNombre.
                 */
                etEmail.setText(email);
                etNombre.setText(nombre);

                /**
                 * Deshabilitación de la edición de los campos etEmail y etNombre.
                 */
                etEmail.setFocusable(false);
                etNombre.setFocusable(false);
                etNombre.setCursorVisible(false);
                etEmail.setCursorVisible(false);

                /**
                 * Ocultamos de los campos relacionados con la contraseña.
                 */
                etContrasena.setVisibility(View.GONE);
                lblContrasena.setVisibility(View.GONE);
                btnMostrarContrasena.setVisibility(View.GONE);
                etConfirmPassword.setVisibility(View.GONE);
                lblConfirmarContrasena.setVisibility(View.GONE);
            }
        } catch (Exception e) {
        }


        /**
         Lista de géneros seleccionables
         */
        final String[] select_qualification = {"Seleccione Generos", "#Clasica", "#Country", "#Electro", "#Flamenco", "#Folk", "#Jazz", "#Kpop", "#Metal", "#Pop", "#Rap", "#Rock", "#Trap", "#Drill"};


        listVOs = new ArrayList<>();

        for (int i = 0; i < select_qualification.length; i++) {
            ControladorSpinnerMultiGeneros stateVO = new ControladorSpinnerMultiGeneros();
            stateVO.setTitle(select_qualification[i]);
            stateVO.setSelected(false);
            listVOs.add(stateVO);
        }

        selectedGeneros = new ArrayList<>();

        myAdapter = new AdapatadorSpinnerMultiGeneros(Registro.this, 0, listVOs, selectedGeneros);
        mySpinnerGenero.setAdapter(myAdapter);

    }


    /**
     * Este método crea un nuevo usuario en la aplicación.
     */
    public void crearUsuario() {
        /**
         * Obtención de los valores en los campos de texto ingresados por el usuario
         * */
        String nombreUsuario = etNombre.getText().toString();
        String emailUsuario = etEmail.getText().toString();
        String contrasenaUsuario = etContrasena.getText().toString();
        String confirmarContrasenaUsuario = etConfirmPassword.getText().toString();
        String ciudad = mySpinner.getSelectedItem().toString();

        /** Listener para el spinner mySpinnerGenero*/

        mySpinnerGenero.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                ControladorSpinnerMultiGeneros selectedState = (ControladorSpinnerMultiGeneros) parent.getSelectedItem();
                String selectedGenre = selectedState.getTitle();

                /** Si el género seleccionado no es "Seleccione Generos"*/
                if (!selectedGenre.equals("Seleccione Generos")) {
                    /** Si el género está seleccionado, se agrega a la lista de géneros seleccionados*/
                    if (selectedState.isSelected()) {
                        selectedGeneros.add(selectedGenre);
                    } else {
                        /**Si el género no está seleccionado, se elimina de la lista de géneros seleccionados*/
                        selectedGeneros.remove(selectedGenre);
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        // Mostrar la barra de progreso
        progressBar.setVisibility(View.VISIBLE);

        /**
         * Si el usuario ha elegido el inicio de sesión con google, se obtienen solo los campos necesarios.
         */
        if (googleAccount != null) {
            registrarConGoogle(emailUsuario, nombreUsuario);
        } else {

            /**Verificación de campos vacíos en caso de que no se inicie sesión con google, sino de manera normal*/
            if (contrasenaUsuario.isEmpty() || confirmarContrasenaUsuario.isEmpty()) {
                Toast.makeText(this, "Debe completar ambos campos de contraseña", Toast.LENGTH_SHORT).show();
                return;
            }

            /** Verificación de coincidencia entre la contraseña y su confirmación*/
            if (!contrasenaUsuario.equals(confirmarContrasenaUsuario)) {
                Toast.makeText(this, "La contraseña y su confirmación no coinciden", Toast.LENGTH_SHORT).show();
                return;
            }

            /** Verificación de campo de email vacío*/
            if (!emailUsuario.isEmpty()) {
                /**
                 Si todos los campos están correctos, se hace la llamada al authenticator para que registre el usuario
                 */
                mAuth.createUserWithEmailAndPassword(emailUsuario, contrasenaUsuario).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        /**
                         * Si NO se ha podido crear el usuario en authenticator
                         */
                        if (!task.isSuccessful()) {
                            try {
                                throw task.getException();
                            }
                            /**
                             * Contraseña debil
                             */ catch (FirebaseAuthWeakPasswordException e) {
                                Toast.makeText(Registro.this, "Clave débil. Mínimo 6 caracteres", Toast.LENGTH_LONG).show();
                            }
                            /**
                             *Formato de email invalido
                             */ catch (FirebaseAuthInvalidCredentialsException e) {
                                Toast.makeText(Registro.this, "Formato de email inválido.", Toast.LENGTH_LONG).show();
                            }
                            /**
                             *Correo en uso
                             */ catch (FirebaseAuthUserCollisionException e) {
                                Toast.makeText(Registro.this, "Este correo ya está en uso.", Toast.LENGTH_LONG).show();
                            }
                            /**
                             *Otras excepciones
                             */ catch (Exception e) {
                                Toast.makeText(Registro.this, e.getMessage(), Toast.LENGTH_LONG).show();
                            }
                        }
                        /**
                         * En caso de que la creación del usuario en authenticator haya sido exitosa
                         */
                        else {
                            // Creación del usuario en Firebase Authentication y Firestore
                            String idUsuario = task.getResult().getUser().getUid();
                            user = new Usuario(idUsuario, emailUsuario, nombreUsuario, null, ciudad, Arrays.asList(), "", "", "", "", "", Arrays.asList(), "", Arrays.asList(), Arrays.asList(), Arrays.asList(), selectedGeneros, Arrays.asList());
                            db.collection(COLECCION).document(idUsuario).set(user).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Intent intent = new Intent(Registro.this, MarcoMenu.class);
                                    intent.putExtra("UidUsuario", user.getId());
                                    startActivity(intent);
                                    finish();
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                }
                            });
                        }
                    }

                });
            }
        }

        // Ocultar la barra de progreso
        progressBar.setVisibility(View.GONE);
    }


    /**
     * Este método registra un nuevo usuario en la aplicación utilizando una cuenta de Google.
     *
     * @param emailUsuario  El email del usuario.
     * @param nombreUsuario El nombre del usuario.
     */
    private void registrarConGoogle(String emailUsuario, String nombreUsuario) {
        /** Obtención del ID de usuario y la ciudad seleccionada*/
        String idUsuario = id;
        String ciudad = mySpinner.getSelectedItem().toString();

        /** Creación del objeto Usuario*/
        user = new Usuario(idUsuario, emailUsuario, nombreUsuario, null, ciudad, Arrays.asList(), "", "", "", "", "", Arrays.asList(), "", Arrays.asList(), Arrays.asList(), Arrays.asList(), selectedGeneros, Arrays.asList());

        /** Creación del usuario en Firestore*/
        db.collection(COLECCION).document(idUsuario).set(user).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Toast.makeText(Registro.this, "User creado", Toast.LENGTH_LONG).show();
                Intent intent = new Intent(Registro.this, MarcoMenu.class);
                intent.putExtra("UidUsuario", id);
                startActivity(intent);
                finish();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
            }
        });
    }


}