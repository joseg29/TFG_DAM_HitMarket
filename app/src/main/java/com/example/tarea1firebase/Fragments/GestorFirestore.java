package com.example.tarea1firebase.Fragments;

import static com.example.tarea1firebase.Fragments.PerfilFragment.COLECCION;

import android.net.Uri;
import android.os.Handler;

import androidx.annotation.NonNull;

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

public class GestorFirestore {
    private StorageReference storageRef;
    private FirebaseStorage storage;
    private FirebaseFirestore db;

    public GestorFirestore() {
        db = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();
        storageRef = storage.getReference();
    }

    /**
     * public void imprimir(Callback<String> callback) {
     * System.out.println("buscando...");
     * //Este callback solo se ejecutará cuando se llegue a esta línea del código, y se devovlerá el resultado
     * //Luego en el otro lado donde se llama al método, el resultado se obtendrá en el onSuccess, despues de haber dado la orden de callback.
     * callback.onSuccess("Listo");
     * }
     */

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


    public interface Callback<T> {
        void onSuccess(T result);
    }


}
