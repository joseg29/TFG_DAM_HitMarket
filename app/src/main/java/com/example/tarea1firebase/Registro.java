package com.example.tarea1firebase;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

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
    private Usuario user;
    private Button btnLogin, btnRegistrar;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.registro);

        db = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();
        storageRef = storage.getReference();
        storageRef = FirebaseStorage.getInstance().getReference();
        btnLogin = findViewById(R.id.btnLogin);
        btnRegistrar = findViewById(R.id.btnRegistrar);

        btnLogin.setOnClickListener(v -> {
            //Cambio a activity de login
            Intent intent = new Intent(Registro.this, Login.class);
            startActivity(intent);
            finish();
        });

        btnRegistrar.setOnClickListener(v -> {
            crearUsuario();
        });


        mAuth = FirebaseAuth.getInstance();

    }


    public void crearUsuario() {
        etNombre = findViewById(R.id.etNombre);
        etEmail = findViewById(R.id.etID);
        etContrasena = findViewById(R.id.etContrasena);

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
                            Toast.makeText(Registro.this, "Este email ya está registrado.", Toast.LENGTH_LONG).show();
                        } catch (Exception e) {
                            Toast.makeText(Registro.this, e.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    } else {
                        // Se crea el usuario en collections (FIRESTORE), y se le pasa el id de authenticator como referencia de documento
                        String idUsuario = task.getResult().getUser().getUid();
                        user = new Usuario(idUsuario, emailUsuario, nombreUsuario, "MuchoTexto descr", Arrays.asList(), contrasenaUsuario, "joseg29_", "joseg29", "elrincondegiorgio");
                        db.collection(COLECCION).document(idUsuario).set(user).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Toast.makeText(Registro.this, "User creado", Toast.LENGTH_LONG).show();
                                Intent intent = new Intent(Registro.this, PerfilUsuario.class);
                                intent.putExtra("USUARIO", user);
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

        } else {
            Toast.makeText(Registro.this, "Hay algún campo vacío.", Toast.LENGTH_LONG).show();
        }
    }

}