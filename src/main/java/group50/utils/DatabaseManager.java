package group50.utils;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.*;
import group50.model.Obstacle;
import group50.network.Firebase;
import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.Query;
import com.google.cloud.firestore.QueryDocumentSnapshot;
import com.google.cloud.firestore.QuerySnapshot;

import java.io.File;
import java.util.*;
import java.util.concurrent.ExecutionException;

import java.sql.*;
import java.util.List;

public class DatabaseManager {

    private static final String URL = "jdbc:sqlite:database.db";




    private static boolean userExists(String username) {
        Firestore db = Firebase.getFirestore();

        Query query = db.collection("users").whereEqualTo("username", username);

        ApiFuture<QuerySnapshot> future = query.get();

        try {
            List<QueryDocumentSnapshot> documents = future.get().getDocuments();

            if (documents.isEmpty()) {
                System.out.println("No users found with username: " + username);
                return false;
            } else {
                for (QueryDocumentSnapshot document : documents) {
                    System.out.println("User found:");
                    System.out.println("Document ID: " + document.getId());
                    System.out.println("Data: " + document.getData());

                }
                return true;
            }
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static boolean addUser(String username, String password, String role) {
        if(username.isEmpty()||role==null||password.isEmpty()){
            return false;
        }
        Map<String, Object> userData = new HashMap<>();
        userData.put("username",username);
        userData.put("password",password);
        userData.put("role",role);
        Firebase.writeData("users",username,userData);
        return true;
    }

    public static boolean validateUser(String username, String password) {
        Firestore db = Firebase.getFirestore();

        Query query = db.collection("users").whereEqualTo("username", username).whereEqualTo("password",password); // Construct the query.

        ApiFuture<QuerySnapshot> future = query.get();

        try {
            List<QueryDocumentSnapshot> documents = future.get().getDocuments();

            if (documents.isEmpty()) {
                System.out.println("No users found with username: " + username);
                return false;
            } else {
                for (QueryDocumentSnapshot document : documents) {
                    System.out.println("User found:");
                    System.out.println("Document ID: " + document.getId());
                    System.out.println("Data: " + document.getData());

                }
                return true;
            }
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static String getUserRole(String username) {
        Firestore db = Firebase.getFirestore();

        Query query = db.collection("users").whereEqualTo("username", username);

        ApiFuture<QuerySnapshot> future = query.get();

        try {
            List<QueryDocumentSnapshot> documents = future.get().getDocuments();

            if (documents.isEmpty()) {
                System.out.println("No users found with username: " + username);
                return "";
            } else {
                for (QueryDocumentSnapshot document : documents) {
                    System.out.println("User found:");
                    System.out.println("Document ID: " + document.getId());
                    System.out.println("Data: " + document.getData());
                    return document.getData().get("role").toString();

                }
                return "";
            }
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
            return "";
        }
    }
}
