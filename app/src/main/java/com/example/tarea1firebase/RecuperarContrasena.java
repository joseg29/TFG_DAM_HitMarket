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

import java.util.List;

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
        /*
         * Listener para ejecutar el envio de recuperar contraseña
         * */
        btnRecuperarContrasena.setOnClickListener(v -> {
            progressBar.setVisibility(View.GONE);
            /*
             * Llama al método validarCorreo() para verificar la validez del correo electrónico ingresado.
             * */
            validarCorreo();
        });
    }

    /**
     * Valida la dirección de correo electrónico ingresada y realiza una acción si es válida.
     * Si la dirección de correo electrónico es inválida, muestra un mensaje de error en el campo de correo electrónico.
     */
    private void validarCorreo() {
        String eMail = etEmail.getText().toString().trim();
        /*
         * Verifica si el campo de correo electrónico está vacío o si no coincide con el patrón
         * de dirección de correo electrónico
         * */
        if (eMail.isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(eMail).matches()) {
            progressBar.setVisibility(View.GONE);
            etEmail.setError("Correo inválido");
            return;
        }
        envia(eMail);
    }

    /**
     * Envía un correo electrónico de restablecimiento de contraseña a la dirección proporcionada.
     * Muestra mensajes de éxito o error según el resultado del envío del correo electrónico.
     *
     * @param eMail la dirección de correo electrónico a la cual se enviará el correo de restablecimiento de contraseña
     */
    private void envia(String eMail) {
        mAuth = FirebaseAuth.getInstance();

        // Verificar si el correo electrónico está registrado
        mAuth.fetchSignInMethodsForEmail(eMail).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                List<String> signInMethods = task.getResult().getSignInMethods();
                if (signInMethods != null && !signInMethods.isEmpty()) {
                    // El correo electrónico está registrado, enviar correo de restablecimiento de contraseña
                    mAuth.sendPasswordResetEmail(eMail)
                            .addOnCompleteListener(sendEmailTask -> {
                                if (sendEmailTask.isSuccessful()) {
                                    progressBar.setVisibility(View.VISIBLE);
                                    Toast.makeText(RecuperarContrasena.this, "Correo Enviado", Toast.LENGTH_SHORT).show();

                                    // Resto del código para mostrar AlertDialog y redirigir al usuario
                                    View view = getLayoutInflater().inflate(R.layout.correo_enviado, null);
                                    /*
                                     * Muestra un AlertDialog personalizado para indicar que se ha enviado el correo
                                     * */
                                    AlertDialog.Builder builder = new AlertDialog.Builder(RecuperarContrasena.this);
                                    builder.setView(view);
                                    builder.setCancelable(false);
                                    AlertDialog alertDialog = builder.create();
                                    alertDialog.show();

                                    /*
                                     * Cierra el AlertDialog después de un cierto tiempo y redirige al usuario a
                                     *  la pantalla de inicio de sesión
                                     * */
                                    new Handler().postDelayed(() -> {
                                        alertDialog.dismiss();
                                        Intent intent = new Intent(RecuperarContrasena.this, Login.class);
                                        startActivity(intent);
                                        finish();
                                    }, 3000);
                                } else {
                                    progressBar.setVisibility(View.GONE);
                                    Toast.makeText(RecuperarContrasena.this, "No se pudo enviar el correo", Toast.LENGTH_SHORT).show();
                                }
                            });
                } else {
                    // El correo electrónico no está registrado
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(RecuperarContrasena.this, "Correo inválido", Toast.LENGTH_SHORT).show();
                }
            } else {
                // Error al verificar el correo electrónico
                progressBar.setVisibility(View.GONE);
                Toast.makeText(RecuperarContrasena.this, "Error al verificar el correo electrónico", Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * Maneja el evento de presionar el botón de retroceso (Back) en el dispositivo.
     * Sobrescribe el método predeterminado para redirigir al usuario a la pantalla de inicio de sesión.
     * Al presionar el botón de retroceso, se inicia una nueva actividad de inicio de sesión y se finaliza la actividad actual.
     */
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(RecuperarContrasena.this, Login.class);
        startActivity(intent);
        finish();
    }
}