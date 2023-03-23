package com.example.tarea1firebase;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;

public class RecuperarContrasena extends AppCompatActivity {

    private Button btnRecuperarContrasena;
    private EditText etEmail;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recuperar_contrasena);

        btnRecuperarContrasena = findViewById(R.id.btnRecuperar);
        etEmail = findViewById(R.id.etEmailRecuperar);
        btnRecuperarContrasena.setOnClickListener(v -> {
            validarCorreo();
        });
    }

    private void validarCorreo() {
        String eMail = etEmail.getText().toString().trim();

        if (eMail.isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(eMail).matches()) {
            etEmail.setError("Correo inválido");
            return;
        }

        envia(eMail);
    }

    private void envia(String eMail) {
        mAuth = FirebaseAuth.getInstance();
        mAuth.sendPasswordResetEmail(eMail)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(RecuperarContrasena.this, "Correo Enviado", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(RecuperarContrasena.this, Login.class);
                        startActivity(intent);
                        finish();
                    } else {
                        Toast.makeText(RecuperarContrasena.this, "Correo inválido", Toast.LENGTH_SHORT).show();
                    }
                });
    }
    //Con este método lo que conseguimos es que el usuario si no quiere recuperar contraseña
    //puede interactuar para volver con el botón de android (flechita) hacia el Login.
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(RecuperarContrasena.this, Login.class);
        startActivity(intent);
        finish();
    }
}