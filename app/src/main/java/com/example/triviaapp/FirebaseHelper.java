package com.example.triviaapp;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class FirebaseHelper {
    private static FirebaseHelper firebaseHelper;
    public static DatabaseReference userDatabaseReference;
    public static DatabaseReference questionDatabaseReference;
    public static DatabaseReference answerDatabaseReference;
    public static DatabaseReference connectedRef;

    private FirebaseHelper(){

    }

    public static FirebaseHelper getInstance(){
        if(firebaseHelper == null){
            firebaseHelper = new FirebaseHelper();
            FirebaseDatabase.getInstance().setPersistenceEnabled(true);
            connectedRef = FirebaseDatabase.getInstance().getReference(".info/connected");
            userDatabaseReference = FirebaseDatabase.getInstance().getReference().child("Users");
            questionDatabaseReference = FirebaseDatabase.getInstance().getReference().child("Questions");
            answerDatabaseReference = FirebaseDatabase.getInstance().getReference().child("Answers");

        }

        return firebaseHelper;
    }

}
