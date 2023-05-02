package com.example.tarea1firebase.gestor;

import static com.example.tarea1firebase.fragments.PerfilFragment.COLECCION;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.telecom.Call;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.tarea1firebase.Login;
import com.example.tarea1firebase.MarcoMenu;
import com.example.tarea1firebase.Registro;
import com.example.tarea1firebase.adaptadores.AdaptadorUsuariosRecycler;
import com.example.tarea1firebase.entidades.Resena;
import com.example.tarea1firebase.entidades.Usuario;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.List;

public class GestorFirestore {
    private StorageReference storageRef;
    private FirebaseStorage storage;
    private FirebaseFirestore db;

    public GestorFirestore() {
        db = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();
        storageRef = storage.getReference();
    }

    public void obtenerTodosLosUsuarios(Callback<ArrayList<Usuario>> callback) {
        db.collection(Registro.COLECCION).get().addOnSuccessListener(documentSnapshots -> {
            ArrayList<Usuario> listaUsuarios = new ArrayList<>();
            for (DocumentSnapshot documentSnapshot : documentSnapshots.getDocuments()) {
                Usuario saludo = documentSnapshot.toObject(Usuario.class);
                listaUsuarios.add(saludo);
            }
            callback.onSuccess(listaUsuarios);
        }).addOnFailureListener(e -> {
        });
    }

    public void verificarSiUsuarioYaExisteEnFirestore(String id, Callback<Boolean> callback) {
        db.collection(Registro.COLECCION).document(id).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                DocumentSnapshot document = task.getResult();
                //Si el documento existe, es porque ya el usuario se ha registrado anteriormente con los campos que faltaban.
                if (document.exists()) {
                    callback.onSuccess(true);
                }
                //Si no existe, se envía a registro para que cree la cuenta por primera vez (Se pasan los campos como email y nombre).
                else {
                    callback.onSuccess(false);
                }
            }
        });
    }


    public <T> void obtenerUsuarioPorId(String id, Callback<T> callback, Class<T> clase) {
        DocumentReference docRef = db.collection(COLECCION).document(id);
        docRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document.exists()) {
                    T result = document.toObject(clase);
                    callback.onSuccess(result);
                } else {
                    System.out.println("No existe el usuario");
                }
            } else {
                System.out.println("Ha fallado");
            }
        });
    }

    public void subirAudio(Uri uri, String idUsuario, Callback<String> callback) {
        StorageReference storagePath = storageRef.child("audios").child(uri.getLastPathSegment());

        storagePath.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Task<Uri> firebaseUri = taskSnapshot.getStorage().getDownloadUrl();
                firebaseUri.addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        String url = uri.toString();
                        db.collection(COLECCION).document(idUsuario).update("arrayCanciones", FieldValue.arrayUnion(url)).addOnSuccessListener(documentReference -> {
                            callback.onSuccess(url);
                        });
                    }
                });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
            }
        });
    }

    public void actualiazarCampoUsuario(String idUsuario, String campoAActualizar, Object nuevoValor, Callback<String> callback) {
        db.collection(COLECCION).document(idUsuario).update(campoAActualizar, FieldValue.arrayUnion(nuevoValor)).addOnSuccessListener(documentReference -> {
            callback.onSuccess("Actualizado");
        });
    }

    public void obtenerMediaResenas(String id, Callback<Double> callback) {
        DocumentReference docRef = db.collection(COLECCION).document(id);
        docRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document.exists()) {
                    double media = 0;

                    Usuario usuario = document.toObject(Usuario.class);
                    List<Resena> listaValoraciones = usuario.getListaResenas();

                    for (int i = 0; i < listaValoraciones.size(); i++) {
                        media += listaValoraciones.get(i).getValoracion();
                    }
                    media = media / listaValoraciones.size();

                    callback.onSuccess(media);

                } else {
                    System.out.println("No existe el usuario");
                }
            }
        });
    }


    public interface Callback<T> {
        void onSuccess(T result);
    }


}
