package com.example.tarea1firebase;

import static com.example.tarea1firebase.Registro.COLECCION;

import android.app.ProgressDialog;
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

import com.github.ybq.android.spinkit.SpinKitView;
import com.github.ybq.android.spinkit.sprite.Sprite;
import com.github.ybq.android.spinkit.style.DoubleBounce;
import com.github.ybq.android.spinkit.style.FadingCircle;
import com.github.ybq.android.spinkit.style.Wave;
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
    private FirebaseFirestore db;
    private StorageReference storageRef;
    private FirebaseStorage storage;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);

        //Icono de carga
        progressBar = findViewById(R.id.spin_kit);
        Sprite doubleBounce = new FadingCircle();
        progressBar.setIndeterminateDrawable(doubleBounce);
        progressBar.setVisibility(View.GONE);

        //Fondo animado
        videoMarco = findViewById(R.id.imVideo);
        try {
            GifDrawable gifDrawable = new GifDrawable(getResources(), R.raw.humobackground);
            videoMarco.setImageDrawable(gifDrawable);
        } catch (IOException e) {
            e.printStackTrace();
        }

        db = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();
        storageRef = storage.getReference();

        btnIniciarSesion = findViewById(R.id.btnIniciarSesion);

        btnGoogleLogin = findViewById(R.id.btnGoogle);

        tvRegistrar = findViewById(R.id.linkRegistrate);

        tvRegistrar.setOnClickListener(v -> {
            //Cambio a activity de registro
            Intent intent = new Intent(Login.this, Registro.class);
            startActivity(intent);
            finish();
        });

        btnIniciarSesion.setOnClickListener(v -> {
            validarLogin();
        });


        btnMostrarContrasena = findViewById(R.id.btnMostrarContrasena);
        etPassword = findViewById(R.id.etPassword);

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

        mAuth = FirebaseAuth.getInstance();

        // Configurar Google Sign-In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestIdToken(getString(R.string.default_web_client_id)).requestEmail().build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
        btnGoogleLogin = findViewById(R.id.btnGoogle);
        btnGoogleLogin.setOnClickListener(v -> {
            Intent signInIntent = mGoogleSignInClient.getSignInIntent();
            startActivityForResult(signInIntent, RC_SIGN_IN);
        });

        tvForgetPassword = findViewById(R.id.tvForgetPassword);
        tvForgetPassword.setOnClickListener(v -> {
            Intent intent = new Intent(Login.this, RecuperarContrasena.class);
            startActivity(intent);
            finish();
        });
    }

    //Este método se ejecuta una vez termina el intentForResult
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Resultado devuelto por Google Sign-In
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Iniciar sesión con Firebase Authentication
                googleAccount = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(googleAccount.getIdToken());
            } catch (ApiException e) {
                // Manejar errores de inicio de sesión
            }
        }
    }

    private void firebaseAuthWithGoogle(String idToken) {
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        mAuth.signInWithCredential(credential).addOnCompleteListener(this, task -> {
            if (task.isSuccessful()) {
                // Inicio de sesión exitoso
                progressBar.setVisibility(View.VISIBLE);
                iniciarSesion();
            } else {
                // Error en inicio de sesión
                progressBar.setVisibility(View.GONE);
                Toast.makeText(Login.this, "Error al iniciar sesión con Google", Toast.LENGTH_LONG).show();
            }
        });
    }

    public void validarLogin() {
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);


        String emailUsuario = etEmail.getText().toString();
        String claveUsuario = etPassword.getText().toString();


        if (!emailUsuario.isEmpty() && !claveUsuario.isEmpty()) {
            mAuth.signInWithEmailAndPassword(emailUsuario, claveUsuario).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (!task.isSuccessful()) {
                        try {
                            throw task.getException();
                        } catch (FirebaseAuthInvalidUserException e) {
                            progressBar.setVisibility(View.GONE);
                            // El usuario no existe
                            Toast.makeText(Login.this, "Email o contraseña incorrecta", Toast.LENGTH_LONG).show();
                        } catch (FirebaseAuthInvalidCredentialsException e) {
                            progressBar.setVisibility(View.GONE);
                            // Credenciales inválidas (correo electrónico incorrecto o contraseña incorrecta)
                            Toast.makeText(Login.this, "Email o contraseña incorrecta", Toast.LENGTH_LONG).show();
                        } catch (Exception e) {
                            Toast.makeText(Login.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        progressBar.setVisibility(View.VISIBLE);
                        iniciarSesion();
                    }
                }
            });
        } else {
            progressBar.setVisibility(View.GONE);
            Toast.makeText(Login.this, "Hay algún campo vacío.", Toast.LENGTH_LONG).show();
        }
    }

    public void iniciarSesion() {
        String idDocumento = mAuth.getCurrentUser().getUid();

        //Se hace un get de firestore database con el id como referencia de documento, para obtener el objeto Usuario y pasarlo a la siguiente activity
        db.collection(COLECCION).document(idDocumento).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                //Verificamos si se ha iniciado sesión con una cuenta de google. De ser así, 'googleAccount' no será null.
                if (googleAccount != null) {
                    Bundle userBundle = new Bundle();

                    userBundle.putString("NOMBRE", googleAccount.getDisplayName());
                    userBundle.putString("EMAIL", googleAccount.getEmail());
                    userBundle.putString("ID", mAuth.getCurrentUser().getUid());
                    db.collection(COLECCION).document(idDocumento).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            DocumentSnapshot document = task.getResult();
                            //Si el documento existe, es porque ya el usuario se ha registrado anteriormente con los campos que faltaban.
                            if (document.exists()) {
                                Intent intent = new Intent(Login.this, VistaExplora.class);
                                intent.putExtra("UidUsuario", idDocumento);
                                startActivity(intent);
                                finish();
                            }
                            //Si no existe, se envía a registro para que cree la cuenta por primera vez (Se pasan los campos como email y nombre).
                            else {
                                Intent intent = new Intent(Login.this, Registro.class);
                                intent.putExtra("CUENTAGOOGLE", userBundle);
                                startActivity(intent);
                                finish();
                            }
                        }
                    });
                }
                //Si 'googleAccount' es null, quiere decir que inicio sesíon con usuario y contraseña, y se envía a la siguiente activity.
                else {
                    Intent intent = new Intent(Login.this, VistaExplora.class);
                    intent.putExtra("UidUsuario", idDocumento);
                    startActivity(intent);
                    finish();
                }
            }
        });
    }
}

