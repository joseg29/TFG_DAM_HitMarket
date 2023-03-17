package com.example.tarea1firebase;

import static com.example.tarea1firebase.Registro.COLECCION;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.InputType;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

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
    private TextView tvRegistrar;
    private Usuario user;
    private ImageView videoMarco;
    private ImageButton btnMostrarContrasena;
    private boolean mostrarContrasena = false;
    private static final int RC_SIGN_IN = 9001;
    private GoogleSignInClient mGoogleSignInClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);

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

        videoMarco = findViewById(R.id.imVideo);
        String uriPath = "android.resource://com.example.tarea1firebase/" + R.raw.humobackground;
        Uri uri = Uri.parse(uriPath);
        try {
            GifDrawable gifDrawable = new GifDrawable(getResources(), R.raw.humobackground);
            videoMarco.setImageDrawable(gifDrawable);
        } catch (IOException e) {
            e.printStackTrace();
        }
        btnMostrarContrasena = findViewById(R.id.btnMostrarContrasena);
        etPassword = findViewById(R.id.etPassword);

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

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.web_cliente_id))
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        btnGoogleLogin = findViewById(R.id.btnGoogle);
        btnGoogleLogin.setOnClickListener(v -> {
            Intent signInIntent = mGoogleSignInClient.getSignInIntent();
            startActivityForResult(signInIntent, RC_SIGN_IN);
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Resultado del inicio de sesión con Google
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Autenticación con Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account.getIdToken());
            } catch (ApiException e) {
                Toast.makeText(Login.this, "Error al iniciar sesión con Google", Toast.LENGTH_LONG).show();
            }
        }
    }

    private void firebaseAuthWithGoogle(String idToken) {
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        FirebaseAuth.getInstance().signInWithCredential(credential)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        // Inicio de sesión exitoso
                        iniciarSesion();
                    } else {
                        // Error en inicio de sesión
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
            FirebaseAuth.getInstance().signInWithEmailAndPassword(emailUsuario, claveUsuario).addOnCompleteListener(new OnCompleteListener<AuthResult>() {

                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (!task.isSuccessful()) {
                        try {
                            throw task.getException();
                        } catch (FirebaseAuthInvalidUserException e) {
                            // El usuario no existe
                            Toast.makeText(Login.this, "Email o contraseña incorrecta", Toast.LENGTH_LONG).show();
                        } catch (FirebaseAuthInvalidCredentialsException e) {
                            // Credenciales inválidas (correo electrónico incorrecto o contraseña incorrecta)
                            Toast.makeText(Login.this, "Email o contraseña incorrecta", Toast.LENGTH_LONG).show();
                        } catch (Exception e) {
                            Toast.makeText(Login.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        iniciarSesion();
                    }
                }
            });
        } else {
            Toast.makeText(Login.this, "Hay algún campo vacío.", Toast.LENGTH_LONG).show();
        }
    }

    public void iniciarSesion() {
        String idDocumento = FirebaseAuth.getInstance().getCurrentUser().getUid();

        //Se hace un get de firestore database con el id como referencia de documento, para obtener el objeto Usuario y pasarlo a la siguiente activity
        db.collection(COLECCION).document(idDocumento).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        user = document.toObject(Usuario.class);
                        Toast.makeText(Login.this, "Sesión iniciada", Toast.LENGTH_LONG).show();
                        Intent intent = new Intent(Login.this, PerfilUsuario.class);
                        intent.putExtra("USUARIO", user);
                        intent.putExtra("UidUsuario", user.getId());
                        startActivity(intent);
                        finish();
                    }
                }
            }
        });
    }
}

