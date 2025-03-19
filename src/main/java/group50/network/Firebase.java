package group50.network;

import com.google.api.core.ApiFuture;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.WriteResult;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.cloud.FirestoreClient;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ExecutionException;

public class Firebase {
    private static final String SERVICE_ACCOUNT_JSON = "/Users/jordan/Documents/runway-c8831-firebase-adminsdk-fbsvc-82b0a7918b.json";

    public static boolean initialize() {
        try {
            FileInputStream serviceAccount = new FileInputStream(SERVICE_ACCOUNT_JSON);

            FirebaseOptions options = new FirebaseOptions.Builder()
                    .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                    .setProjectId("runway-c8831")
                    .build();
            FirebaseApp.initializeApp(options);
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static Firestore getFirestore() {
        return FirestoreClient.getFirestore();
    }
    public static void writeData(String collection, String document, Map<String, Object> data) {
        Firestore db = Firebase.getFirestore();
        DocumentReference docRef = db.collection(collection).document(document);
        ApiFuture<WriteResult> result = docRef.set(data);

        try {
            System.out.println("Update time : " + result.get().getUpdateTime());
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
    }
    public static Map<String, Object> readDocument(String collection, String document) {
        Firestore db = Firebase.getFirestore();
        DocumentReference docRef = db.collection(collection).document(document);
        ApiFuture<DocumentSnapshot> future = docRef.get();

        try {
            DocumentSnapshot documentSnapshot = future.get();
            if (documentSnapshot.exists()) {
                return documentSnapshot.getData();
            } else {
                System.out.println("Document not found.");
                return null;
            }
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
            return null;
        }
    }
    public static boolean isInternetAvailable() {
        Map<String, Object>val= readDocument("connectionTest","pinger");
        return val != null;
    }



}