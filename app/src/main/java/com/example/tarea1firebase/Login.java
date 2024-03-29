package com.example.tarea1firebase;

import static com.example.tarea1firebase.Registro.COLECCION;

import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.tarea1firebase.entidades.Usuario;
import com.example.tarea1firebase.gestor.GestorFirestore;
import com.github.ybq.android.spinkit.sprite.Sprite;
import com.github.ybq.android.spinkit.style.FadingCircle;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.IOException;

import pl.droidsonroids.gif.GifDrawable;

public class Login extends AppCompatActivity {
    private Button btnIniciarSesion, btnGoogleLogin;
    private EditText etEmail, etPassword;
    private TextView tvRegistrar, tvForgetPassword;
    private Usuario user;
    private ImageView videoMarco;
    private ImageButton btnMostrarContrasena;
    private boolean mostrarContrasena = false;
    private static final int RC_SIGN_IN = 9001;
    private GoogleSignInClient mGoogleSignInClient;
    private FirebaseAuth mAuth;
    private GoogleSignInAccount googleAccount;
    private ProgressBar progressBar;
    private GestorFirestore gestorFirestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);

        gestorFirestore = new GestorFirestore();
        mAuth = FirebaseAuth.getInstance();

        inicializarVistas();
        inicializarListenersBotones();

        /** Configurar Google Sign-In
         * @see https://firebase.google.com/docs/auth/android/google-signin?hl=es-419
         * */
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestIdToken(getString(R.string.default_web_client_id)).requestEmail().build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

    }

    private void inicializarListenersBotones() {
        /**
         * Botón que te envía a la activity de registro
         */
        tvRegistrar.setOnClickListener(v -> {
            //Cambio a activity de registro
            Intent intent = new Intent(Login.this, Registro.class);
            startActivity(intent);
            finish();
        });


        btnIniciarSesion.setOnClickListener(v -> {
            validarLogin();
        });

        //Boton de mostrar u ocultar contraseña
        btnMostrarContrasena.setOnClickListener(view -> {
            mostrarContrasena = !mostrarContrasena;
            int cursorPosition = etPassword.getSelectionStart();
            if (mostrarContrasena) {
                // Si se permite mostrar la contraseña, se cambia el inputType del EditText
                etPassword.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                btnMostrarContrasena.setImageResource(R.drawable.ojo_cerrado);
            } else {
                etPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                btnMostrarContrasena.setImageResource(R.drawable.ojo_abierto);
            }
            etPassword.setSelection(cursorPosition);
        });

        /**
         * Botón que abre la api de google para iniciar sesión.
         */
        btnGoogleLogin.setOnClickListener(v -> {
            Intent signInIntent = mGoogleSignInClient.getSignInIntent();
            startActivityForResult(signInIntent, RC_SIGN_IN);
        });

        /**
         * Botón para proceso de cambio de clave
         */
        tvForgetPassword.setOnClickListener(v -> {
            Intent intent = new Intent(Login.this, RecuperarContrasena.class);
            startActivity(intent);
            finish();
        });
    }

    private void inicializarVistas() {
        /**Icono de carga*/
        progressBar = findViewById(R.id.spin_kit);
        Sprite doubleBounce = new FadingCircle();
        progressBar.setIndeterminateDrawable(doubleBounce);
        progressBar.setVisibility(View.GONE);

        /**Fondo animado*/
        videoMarco = findViewById(R.id.imVideo);
        try {
            GifDrawable gifDrawable = new GifDrawable(getResources(), R.raw.humobackground);
            videoMarco.setImageDrawable(gifDrawable);
        } catch (IOException e) {
            e.printStackTrace();
        }

        tvForgetPassword = findViewById(R.id.tvForgetPassword);
        btnMostrarContrasena = findViewById(R.id.btnMostrarContrasena);
        etPassword = findViewById(R.id.etPassword);
        btnIniciarSesion = findViewById(R.id.btnIniciarSesion);
        btnGoogleLogin = findViewById(R.id.btnGoogle);
        tvRegistrar = findViewById(R.id.linkRegistrate);
        btnGoogleLogin = findViewById(R.id.btnGoogle);
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
    }

    /**
     * Este método se llama cuando una actividad lanza un intent y devuelve un resultado.
     *
     * @param requestCode El código de solicitud originalmente suministrado a startActivityForResult(),
     *                    lo que permite identificar de quién proviene este resultado.
     * @param resultCode  El código de resultado devuelto por la actividad secundaria a través de su setResult().
     * @param data        Un Intent, que puede devolver datos de resultados a la persona que llama.
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        /** Verificación del código de solicitud*/
        if (requestCode == RC_SIGN_IN) {
            /** Obtención del resultado del inicio de sesión con Google*/
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                /** Inicio de sesión con Firebase Authentication
                 * @see https://firebase.google.com/docs/auth/android/google-signin?hl=es-419*/

                googleAccount = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(googleAccount.getIdToken());
            } catch (ApiException e) {
                /** Manejo de errores de inicio de sesión*/
            }
        }
    }


    /**
     * Este método inicia sesión en Firebase Authentication utilizando una cuenta de Google.
     *
     * @param idToken El token de ID de la cuenta de Google.
     */
    private void firebaseAuthWithGoogle(String idToken) {
        /** Creación de credenciales de inicio de sesión con Google*/
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);

        /** Inicio de sesión con Firebase Authentication*/
        mAuth.signInWithCredential(credential).addOnCompleteListener(this, task -> {
            if (task.isSuccessful()) {
                /** Validación de sesión exitosa*/
                progressBar.setVisibility(View.VISIBLE);

                /**Se llama al método que da acceso a la app*/
                iniciarSesion();
            } else {
                /** Error en inicio de sesión*/
                progressBar.setVisibility(View.GONE);
                Toast.makeText(Login.this, "Error al iniciar sesión con Google", Toast.LENGTH_LONG).show();
            }
        });
    }


    /**
     * Este método valida el inicio de sesión del usuario en la aplicación.
     */
    public void validarLogin() {
        /** Obtención de los valores ingresados por el usuario*/
        String emailUsuario = etEmail.getText().toString();
        String claveUsuario = etPassword.getText().toString();

        /** Verificación de campos vacíos*/
        if (!emailUsuario.isEmpty() && !claveUsuario.isEmpty()) {
            /**Intento de inicio de sesión*/
            mAuth.signInWithEmailAndPassword(emailUsuario, claveUsuario).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    /**Ha habido algún error*/
                    if (!task.isSuccessful()) {
                        try {
                            throw task.getException();
                        } catch (FirebaseAuthInvalidUserException e) {
                            /** El usuario no existe*/
                            progressBar.setVisibility(View.GONE);
                            Toast.makeText(Login.this, "Email o contraseña incorrecta", Toast.LENGTH_LONG).show();
                        } catch (FirebaseAuthInvalidCredentialsException e) {
                            /** Credenciales inválidas (correo electrónico incorrecto o contraseña incorrecta)*/
                            progressBar.setVisibility(View.GONE);
                            Toast.makeText(Login.this, "Email o contraseña incorrecta", Toast.LENGTH_LONG).show();
                        } catch (Exception e) {
                            /** Otros problemas*/
                            Toast.makeText(Login.this, "No se ha podido iniciar sesión.", Toast.LENGTH_SHORT).show();
                        }
                    }
                    /** Si no ha habido ningún problema con los campos, se procede a iniciar sesión*/
                    else {
                        progressBar.setVisibility(View.VISIBLE);
                        iniciarSesion();
                    }
                }
            });
        }
        /**
         * Hay algún campo vacío
         */
        else {
            progressBar.setVisibility(View.GONE);
            Toast.makeText(Login.this, "Hay algún campo vacío.", Toast.LENGTH_LONG).show();
        }
    }


    public void iniciarSesion() {
        String idDocumento = mAuth.getCurrentUser().getUid();

        /**Verificamos si se ha iniciado sesión con una cuenta de google. De ser así, 'googleAccount' no será null.*/
        if (googleAccount != null) {
            gestorFirestore.verificarSiUsuarioYaExisteEnFirestore(idDocumento, new GestorFirestore.Callback<Boolean>() {
                @Override
                public void onSuccess(Boolean existe) {
                    Intent intent;
                    /**Si el usuario ya existe en firestore, se envía directamente al inicio.*/
                    if (existe) {
                        intent = new Intent(Login.this, MarcoMenu.class);
                        intent.putExtra("UidUsuario", idDocumento);
                    }
                    /**Si el usuario solo se ha registrado en authenticator con google pero no en firestore, se envía a registro para que rellene los campos que faltan*/
                    else {
                        intent = new Intent(Login.this, Registro.class);

                        /**
                         * Lo enviamos a registro para que rellene los datos que faltan, pero le enviamos los que ya tenemos, como el correo, el nombre y el id
                         */
                        Bundle userBundle = new Bundle();
                        userBundle.putString("NOMBRE", googleAccount.getDisplayName());
                        userBundle.putString("EMAIL", googleAccount.getEmail());
                        userBundle.putString("ID", mAuth.getCurrentUser().getUid());
                        intent.putExtra("CUENTAGOOGLE", userBundle);
                    }
                    startActivity(intent);
                    finish();
                }
            });
        }
        /**Si 'googleAccount' es null, quiere decir que inicio sesíon con usuario y contraseña, y se envía a la siguiente activity.*/
        else {
            Intent intent = new Intent(Login.this, MarcoMenu.class);
            intent.putExtra("UidUsuario", idDocumento);
            startActivity(intent);
            finish();
        }
    }

}

