package com.example.tarea1firebase.gestor;

import static com.example.tarea1firebase.fragments.PerfilFragment.COLECCION;

import android.net.Uri;

import androidx.annotation.NonNull;

import com.example.tarea1firebase.Registro;
import com.example.tarea1firebase.entidades.Publicacion;
import com.example.tarea1firebase.entidades.Resena;
import com.example.tarea1firebase.entidades.Usuario;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

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
     * Este método obtiene una lista de todos los usuarios en la base de datos y devuelve el resultado a través de un objeto Callback.
     *
     * @param callback Un objeto Callback que se utilizará para manejar el resultado de la operación.
     */
    public void obtenerTodosLosUsuarios(Callback<ArrayList<Usuario>> callback) {
        // Obtener todos los documentos de la colección COLECCION
        db.collection(Registro.COLECCION).get().addOnSuccessListener(documentSnapshots -> {
            // Crear una lista vacía para almacenar los objetos Usuario
            ArrayList<Usuario> listaUsuarios = new ArrayList<>();
            // Recorrer todos los documentos obtenidos
            for (DocumentSnapshot documentSnapshot : documentSnapshots.getDocuments()) {
                // Convertir cada documento en un objeto Usuario
                Usuario saludo = documentSnapshot.toObject(Usuario.class);
                // Agregar el objeto Usuario a la lista
                listaUsuarios.add(saludo);
            }
            // Llamar al método onSuccess del objeto Callback proporcionado y pasarle como parámetro la lista de usuarios obtenida
            callback.onSuccess(listaUsuarios);
        }).addOnFailureListener(e -> {
            // Llamar al método onFailure del objeto Callback proporcionado si ocurre algún error durante la operación
        });
    }


    /**
     * Este método verifica si un usuario ya existe en Firestore.
     *
     * @param id       El ID del usuario a verificar.
     * @param callback Un objeto Callback que se llama cuando el gestor completa la verificación.
     */
    public void verificarSiUsuarioYaExisteEnFirestore(String id, Callback<Boolean> callback) {
        db.collection(Registro.COLECCION).document(id).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                DocumentSnapshot document = task.getResult();
                /** Si el documento existe, es porque ya el usuario se ha registrado anteriormente con los campos que faltaban.*/
                if (document.exists()) {
                    callback.onSuccess(true);
                }
                /** Si no existe, se envía a registro para que cree la cuenta por primera vez (Se pasan los campos como email y nombre).*/
                else {
                    callback.onSuccess(false);
                }
            }
        });
    }


    /**
     * Este método recupera un objeto de usuario de tipo T de la base de datos por su ID.
     *
     * @param id       El ID del usuario que se quiere recuperar.
     * @param callback Un objeto Callback que se utilizará para manejar el resultado de la operación.
     * @param clase    La clase del objeto que se quiere recuperar.
     */
    public <T> void obtenerUsuarioPorId(String id, Callback<T> callback, Class<T> clase) {
        DocumentReference docRef = db.collection(COLECCION).document(id);
        /**
         * Intenta hacer un get de firestore con la referencia de documento que le hemos pasado
         */
        docRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                /**
                 * Si ha habido alguna coincidencia es porque existe el usuario.
                 */
                if (document.exists()) {
                    T result = document.toObject(clase);
                    callback.onSuccess(result);
                }
            }
        });
    }


    /**
     * Este método sube un archivo de audio a la base de datos y actualiza el documento del usuario con la URL del archivo subido.
     *
     * @param uri       La URI del archivo de audio que se quiere subir.
     * @param idUsuario El ID del usuario cuyo documento se quiere actualizar.
     * @param callback  Un objeto Callback que se utilizará para manejar el resultado de la operación.
     */
    public void subirAudio(Uri uri, String idUsuario, Callback<String> callback) {
        /** Crear una referencia al storage en la base de datos en la ruta "audios" y asignarle el nombre del archivo de audio*/
        StorageReference storagePath = storageRef.child("audios").child(uri.getLastPathSegment() + Timestamp.now());

        /**Subir el archivo a la base de datos (Storage)*/
        storagePath.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                /** Obtener la URL del archivo subido*/
                Task<Uri> firebaseUri = taskSnapshot.getStorage().getDownloadUrl();
                firebaseUri.addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        String url = uri.toString();
                        /** Actualizar el documento del usuario con la URL del archivo subido*/
                        db.collection(COLECCION).document(idUsuario).update("arrayCanciones", FieldValue.arrayUnion(url)).addOnSuccessListener(documentReference -> {
                            // Llamar al método onSuccess del objeto Callback proporcionado y pasarle como parámetro la URL del archivo subido
                            callback.onSuccess(url);
                        });
                    }
                });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                // Llamar al método onFailure del objeto Callback proporcionado si ocurre algún error durante la operación
            }
        });
    }


    /**
     * Este método agrega un nuevo valor a un campo de tipo array en el documento de un usuario en la base de datos.
     *
     * @param idUsuario        El ID del usuario cuyo documento se quiere actualizar.
     * @param campoAActualizar El nombre del campo que se quiere actualizar.
     * @param nuevoValor       El valor que se quiere agregar al array.
     * @param callback         Un objeto Callback que se utilizará para manejar el resultado de la operación.
     */
    public void anadirValorArray(String idUsuario, String campoAActualizar, Object nuevoValor, Callback<String> callback) {
        // Actualizar el documento del usuario en la base de datos
        db.collection(COLECCION).document(idUsuario).update(campoAActualizar, FieldValue.arrayUnion(nuevoValor)).addOnSuccessListener(documentReference -> {
            // Llamar al método onSuccess del objeto Callback proporcionado y pasarle como parámetro el mensaje "Actualizado"
            callback.onSuccess("Actualizado");
        });
    }

    /**
     * Este método elimina un valor de un campo de tipo array en el documento de un usuario en la base de datos.
     *
     * @param idUsuario        El ID del usuario cuyo documento se quiere actualizar.
     * @param campoAActualizar El nombre del campo que se quiere actualizar.
     * @param valorABorrar     El valor que se quiere eliminar del array.
     * @param callback         Un objeto Callback que se utilizará para manejar el resultado de la operación.
     */
    public void borrarValorArray(String idUsuario, String campoAActualizar, String valorABorrar, Callback<String> callback) {
        // Actualizar el documento del usuario en la base de datos
        db.collection(COLECCION).document(idUsuario).update(campoAActualizar, FieldValue.arrayRemove(valorABorrar)).addOnSuccessListener(documentReference -> {
            // Llamar al método onSuccess del objeto Callback proporcionado y pasarle como parámetro el mensaje "Borrado"
            callback.onSuccess("Borrado");
        });
    }

    /**
     * Este método actualiza un valor en un campo de tipo array en el documento de un usuario en la base de datos. Primero elimina un valor y luego agrega uno nuevo.
     *
     * @param idUsuario        El ID del usuario cuyo documento se quiere actualizar.
     * @param campoAActualizar El nombre del campo que se quiere actualizar.
     * @param valorABorrar     El valor que se quiere eliminar del array.
     * @param valorNuevo       El valor que se quiere agregar al array.
     * @param callback         Un objeto Callback que se utilizará para manejar el resultado de la operación.
     */
    public void actualizarValorArray(String idUsuario, String campoAActualizar, Object valorABorrar, Object valorNuevo, Callback<String> callback) {
        // Eliminar el valor especificado del campo en el documento del usuario
        db.collection(COLECCION).document(idUsuario).update(campoAActualizar, FieldValue.arrayRemove(valorABorrar)).addOnSuccessListener(documentReference -> {
            // Llamar al método onSuccess del objeto Callback proporcionado y pasarle como parámetro el mensaje "Borrado"
            callback.onSuccess("Borrado");
        });

        // Agregar el nuevo valor al campo en el documento del usuario
        db.collection(COLECCION).document(idUsuario).update(campoAActualizar, FieldValue.arrayUnion(valorNuevo)).addOnSuccessListener(documentReference -> {
            // Llamar al método onSuccess del objeto Callback proporcionado y pasarle como parámetro el mensaje "Añadido"
            callback.onSuccess("Añadido");
        });
    }


    /**
     * Este método calcula la media de las valoraciones de un usuario en la base de datos y devuelve el resultado a través de un objeto Callback.
     *
     * @param id       El ID del usuario cuyo documento se quiere consultar.
     * @param callback Un objeto Callback que se utilizará para manejar el resultado de la operación.
     */
    public void obtenerMediaResenas(String id, Callback<String> callback) {
        // Crear una referencia al documento del usuario en la base de datos
        DocumentReference docRef = db.collection(COLECCION).document(id);
        // Obtener el documento del usuario
        docRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document.exists()) {
                    // Inicializar la variable media en 0.0
                    Double media = 0.0;

                    // Convertir el documento en un objeto Usuario
                    Usuario usuario = document.toObject(Usuario.class);
                    // Obtener la lista de valoraciones del usuario
                    List<Resena> listaValoraciones = usuario.getListaResenas();

                    // Si la lista de valoraciones no está vacía
                    if (listaValoraciones.size() > 0) {
                        // Recorrer la lista de valoraciones y sumarlas
                        for (int i = 0; i < listaValoraciones.size(); i++) {
                            media += listaValoraciones.get(i).getValoracion();
                        }
                        // Calcular la media dividiendo la suma entre el número de valoraciones
                        media = media / listaValoraciones.size();
                    }
                    // Llamar al método onSuccess del objeto Callback proporcionado y pasarle como parámetro la media calculada convertida en una cadena
                    callback.onSuccess(String.valueOf(media));

                }
            }
        });
    }


    /**
     * Este método agrega el ID de un usuario a la lista de visitas al perfil de otro usuario en la base de datos.
     *
     * @param idUsuarioQueEsVisitado El ID del usuario cuyo perfil ha sido visitado.
     * @param idUsuarioQueVisita     El ID del usuario que ha visitado el perfil.
     */
    public void anadirVisitaAlPerfil(String idUsuarioQueEsVisitado, String idUsuarioQueVisita) {
        // Actualizar el documento del usuario cuyo perfil ha sido visitado en la base de datos
        db.collection(COLECCION).document(idUsuarioQueEsVisitado).update("visitasAlPerfil", FieldValue.arrayUnion(idUsuarioQueVisita)).addOnSuccessListener(documentReference -> {
            // No se hace nada si la operación es exitosa
        });
    }


    /**
     * Esta es una interfaz genérica para manejar el resultado de una operación asíncrona.
     *
     * @param <T> El tipo de dato del resultado de la operación.
     */
    public interface Callback<T> {
        /**
         * Este método se llama cuando la operación asíncrona es exitosa.
         *
         * @param result El resultado de la operación.
         */
        void onSuccess(T result);
    }


}
