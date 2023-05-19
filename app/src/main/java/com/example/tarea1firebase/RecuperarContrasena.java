package com.example.tarea1firebase;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.github.ybq.android.spinkit.sprite.Sprite;
import com.github.ybq.android.spinkit.style.FadingCircle;
import com.google.firebase.auth.FirebaseAuth;

public class RecuperarContrasena extends AppCompatActivity {

    private Button btnRecuperarContrasena;
    private EditText etEmail;
    private FirebaseAuth mAuth;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recuperar_contrasena);

        progressBar = findViewById(R.id.spin_kit_2);
        Sprite doubleBounce = new FadingCircle();
        progressBar.setIndeterminateDrawable(doubleBounce);
        progressBar.setVisibility(View.GONE);

        btnRecuperarContrasena = findViewById(R.id.btnRecuperar);
        etEmail = findViewById(R.id.etEmailRecuperar);
        btnRecuperarContrasena.setOnClickListener(v -> {
            progressBar.setVisibility(View.GONE);
            validarCorreo();
        });
    }

    private void validarCorreo() {
        String eMail = etEmail.getText().toString().trim();

        if (eMail.isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(eMail).matches()) {
            progressBar.setVisibility(View.GONE);
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
                        progressBar.setVisibility(View.VISIBLE);
                        Toast.makeText(RecuperarContrasena.this, "Correo Enviado", Toast.LENGTH_SHORT).show();
                        //Falta hacer un xml para que nos avise de quetodo se ha realizado de forma correct
                        View view = getLayoutInflater().inflate(R.layout.correo_enviado, null);

                        AlertDialog.Builder builder = new AlertDialog.Builder(RecuperarContrasena.this);
                        builder.setView(view);
                        builder.setCancelable(false);
                        AlertDialog alertDialog = builder.create();
                        alertDialog.show();

                        // Cerrar el AlertDialog después de un tiempo
                        new Handler().postDelayed(() -> {
                            alertDialog.dismiss();
                            Intent intent = new Intent(RecuperarContrasena.this, Login.class);
                            startActivity(intent);
                            finish();
                        }, 3000);
                    } else {
                        progressBar.setVisibility(View.GONE);
                        Toast.makeText(RecuperarContrasena.this, "Correo inválido", Toast.LENGTH_SHORT).show();
                    }
                });
    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(RecuperarContrasena.this, Login.class);
        startActivity(intent);
        finish();
    }
}