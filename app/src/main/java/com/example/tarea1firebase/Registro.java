package com.example.tarea1firebase;

import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
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

import java.util.Arrays;

public class Registro extends AppCompatActivity {
    private FirebaseFirestore db;
    //Este será el nombre de la colección que daremos en la BBDD de Firebase
    public final static String COLECCION = "Usuarios";
    private StorageReference storageRef;
    private FirebaseStorage storage;
    private EditText etEmail, etNombre, etContrasena;
    private ImageButton btnMostrarContrasena;
    private boolean mostrarContrasena = false;
    private TextView tvIniciarSesion;
    private Usuario user;
    private Button btnRegistrar;
    private FirebaseAuth mAuth;
    private Bundle googleAccount;
    private String nombre, email, id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.registro);

        db = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();
        storageRef = storage.getReference();
        storageRef = FirebaseStorage.getInstance().getReference();
        tvIniciarSesion = findViewById(R.id.linkIniciaSesion);
        btnRegistrar = findViewById(R.id.btnRegistrate);
        btnMostrarContrasena = findViewById(R.id.btnMostrarContrasena);
        etContrasena = findViewById(R.id.etPassword);

        tvIniciarSesion.setOnClickListener(v -> {
            //Cambio a activity de login
            Intent intent = new Intent(Registro.this, Login.class);
            startActivity(intent);
            finish();
        });

        btnRegistrar.setOnClickListener(v -> {
            crearUsuario();
        });


        mAuth = FirebaseAuth.getInstance();

        etNombre = findViewById(R.id.etNombre);
        etEmail = findViewById(R.id.etEmail);
        etContrasena = findViewById(R.id.etPassword);

        googleAccount = null;
        try {
            googleAccount = getIntent().getBundleExtra("CUENTAGOOGLE");
            if (googleAccount != null) {
                nombre = googleAccount.getString("NOMBRE");
                email = googleAccount.getString("EMAIL");
                id = googleAccount.getString("ID");

                etEmail.setText(email);
                etNombre.setText(nombre);
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

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
    }


    public void crearUsuario() {
        String nombreUsuario = etNombre.getText().toString();
        String emailUsuario = etEmail.getText().toString();
        String contrasenaUsuario = etContrasena.getText().toString();

        if (!emailUsuario.isEmpty() && !contrasenaUsuario.isEmpty()) {
            //Se crea el usuario en el authenticator de firebase
            mAuth.createUserWithEmailAndPassword(emailUsuario, contrasenaUsuario).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (!task.isSuccessful()) {
                        try {
                            throw task.getException();
                        } catch (FirebaseAuthWeakPasswordException e) {
                            Toast.makeText(Registro.this, "Clave débil. Mínimo 6 caracteres", Toast.LENGTH_LONG).show();
                        } catch (FirebaseAuthInvalidCredentialsException e) {
                            Toast.makeText(Registro.this, "Formato de email inválido.", Toast.LENGTH_LONG).show();
                        } catch (FirebaseAuthUserCollisionException e) {
                            //Ya existe el email
                            String idUsuario = id;
                            user = new Usuario(idUsuario, emailUsuario, nombreUsuario, "MuchoTexto descr", Arrays.asList(), contrasenaUsuario, "joseg29_", "joseg29", "elrincondegiorgio");
                            db.collection(COLECCION).document(idUsuario).set(user).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Toast.makeText(Registro.this, "User creado", Toast.LENGTH_LONG).show();
                                    Intent intent = new Intent(Registro.this, PerfilUsuario.class);
                                    intent.putExtra("UidUsuario", id);
                                    startActivity(intent);
                                    finish();
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    System.out.println("s<moomom");
                                }
                            });
                        } catch (Exception e) {
                            Toast.makeText(Registro.this, e.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    } else {
                        System.out.println("mlmosa");
                        // Se crea el usuario en collections (FIRESTORE), y se le pasa el id de authenticator como referencia de documento
                        String idUsuario = task.getResult().getUser().getUid();
                        System.out.println(idUsuario);
                        user = new Usuario(idUsuario, emailUsuario, nombreUsuario, "MuchoTexto descr", Arrays.asList(), contrasenaUsuario, "joseg29_", "joseg29", "elrincondegiorgio");
                        db.collection(COLECCION).document(idUsuario).set(user).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Toast.makeText(Registro.this, "User creado", Toast.LENGTH_LONG).show();
                                Intent intent = new Intent(Registro.this, PerfilUsuario.class);
                                intent.putExtra("UidUsuario", user.getId());
                                startActivity(intent);
                                finish();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                System.out.println("soasaom");
                            }
                        });
                    }
                }

            });
        }
    }

}