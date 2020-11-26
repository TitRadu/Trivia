package com.example.triviaapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {
    private FirebaseAuth firebaseAuth;

    private EditText emailInput, passwordInput;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initializeViews();

    }

    private void initializeViews(){
        emailInput = findViewById(R.id.emailLogInput);
        passwordInput = findViewById(R.id.passwordLogInput);
        firebaseAuth = FirebaseAuth.getInstance();

    }

    private boolean inputCheck(String email, String password){
        if(email.isEmpty()){
            Toast.makeText(this,"Introduce a email!",Toast.LENGTH_SHORT).show();
            return false;

        }

        if(password.isEmpty()){
            Toast.makeText(this,"Introduce a password!",Toast.LENGTH_SHORT).show();
            return false;

        }

        return true;

    }


    public void  registerActivity(View view){
        Intent intent = new Intent(this, RegisterActivity.class);
        startActivity(intent);

    }

    public void  logInActivity(View view){
        final String email = emailInput.getText().toString();
        final String password = passwordInput.getText().toString();

        if(!inputCheck(email,password)){
            return;

        }
            FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                // Sign in success, update UI with the signed-in user's information
                                Toast.makeText(getBaseContext(), "Succes!", Toast.LENGTH_SHORT).show();
                                updateUI();
                            } else {
                                // If sign in fails, display a message to the user.
                                Toast.makeText(getBaseContext(), "Email or password incorrect!", Toast.LENGTH_SHORT).show();

                            }

                        }
                    });

        }

        private void updateUI(){
            Intent intent = new Intent(this, GameActivity.class);
            startActivity(intent);

        }

    public void  exitApp(View view){
        finishAndRemoveTask();

    }

}