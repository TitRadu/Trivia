package com.example.triviaapp;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class FirebaseHelper {
    private static FirebaseHelper firebaseHelper;
    public static DatabaseReference userDatabaseReference;
    public static DatabaseReference rankingDatabaseReference;

    private FirebaseHelper(){

    }

    public static FirebaseHelper getInstance(){
        if(firebaseHelper == null){
            firebaseHelper = new FirebaseHelper();
            FirebaseDatabase.getInstance().setPersistenceEnabled(true);
            userDatabaseReference = FirebaseDatabase.getInstance().getReference().child("Users");
            rankingDatabaseReference = FirebaseDatabase.getInstance().getReference().child("Ranking");

        }

        return firebaseHelper;
    }

}
