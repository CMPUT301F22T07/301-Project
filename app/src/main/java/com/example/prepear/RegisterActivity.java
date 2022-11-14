/**
 * Class Name: RegisterActivity
 * Version Information: Version 1.0
 * Create Date: Nov 8th, 2022
 * Author: Shihao Liu
 * Copyright Notice:
 */

package com.example.prepear;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;


/**
 * This class is used by the user to sign up an account for PrePear app
 * After user's successful register, the user will be re-directed to MainActivity (Home Page)
 *
 */
public class RegisterActivity extends AppCompatActivity {
    // on below part: initialize class variables
    private EditText userNameInput; // EditText for user name input
    private EditText userEmailInput; // EditText for user email input
    private EditText userFirstPasswordInput; // EditText for user initial password input
    private EditText userSecondPasswordInput; // EditText for user password confirmation input by re-typing
    private EditText userPhoneNumberInput; // EditText for user phone number input
    private ProgressBar userProgressStatus; // will be shown after user completes successful inputs
    private Button signUpButton; // Button for submitting user input info for registering
    private TextView loginText; // for user to log in with an existing account
    private String userUID; // store the unique User UID
    // On below part: essential instances of Firebase to connect the FireStore cloud db and Authentication
    FirebaseAuth firebaseAuth;
    FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        userNameInput = findViewById(R.id.username_input);
        userEmailInput = findViewById(R.id.email_input);
        userFirstPasswordInput = findViewById(R.id.password_first_input);
        userSecondPasswordInput = findViewById(R.id.password_second_input);
        userPhoneNumberInput = findViewById(R.id.user_phoneNumber_input);
        userProgressStatus = findViewById(R.id.fill_progressBar);
        userProgressStatus.setVisibility(View.INVISIBLE);
        signUpButton = (Button) findViewById(R.id.register_confirm_button);
        loginText = findViewById(R.id.already_registered_text);

        firebaseAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        if (firebaseAuth.getCurrentUser() != null) { // Check if the user is already registered
            startActivity(new Intent(getApplicationContext(), MainActivity.class));
            finish();
        }

        // On below part: for the user to login with existing account and password
        loginText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent loginActivity = new Intent( RegisterActivity.this,
                        LoginActivity.class);
                startActivity(loginActivity);
            }

        });

        signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String userName = userNameInput.getText().toString();
                String userEmail = userEmailInput.getText().toString().trim();
                String userPasswordFirstAttempt = userFirstPasswordInput.getText().toString().trim();
                String userPasswordSecondAttempt = userSecondPasswordInput.getText().toString().trim();
                String userPhoneNumber = userPhoneNumberInput.getText().toString().trim();

                if (TextUtils.isEmpty(userName)){ // if username field is empty when registering
                    userNameInput.setError("Please enter your username!");
                    return;
                }
                if (TextUtils.isEmpty(userEmail)) { //
                    userEmailInput.setError("Please enter your email!");
                    return;
                }
                if (TextUtils.isEmpty(userPasswordFirstAttempt)) { //
                    userFirstPasswordInput.setError("Please enter your password!");
                    return;
                }
                if (TextUtils.isEmpty(userPasswordSecondAttempt)) { //
                    userSecondPasswordInput.setError("Please enter your password!");
                    return;
                }
                if (userName.length() < 6 || userName.length() > 50) { //
                    userNameInput.setError("Username enter range is 6 - 100");
                    return;
                }

                if (userPasswordFirstAttempt.length() < 6) { //
                    userFirstPasswordInput.setError("User password minimum length is 6.");
                }

                if (! userPasswordFirstAttempt.equals(userPasswordSecondAttempt)) { //
                    userSecondPasswordInput.setError("Your password does not match!");
                    return;
                }

                userProgressStatus.setVisibility(View.VISIBLE);

                /* Register the user in FireBase */
                firebaseAuth.createUserWithEmailAndPassword(userEmail, userPasswordFirstAttempt)
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) { // if user creates the account successfully
                            // On below part: send the verification link to user's email
                            FirebaseUser currentUser = firebaseAuth.getCurrentUser();
                            currentUser.sendEmailVerification().addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void unused) {
                                    Toast.makeText(RegisterActivity.this, "Verification Email has been sent. ",
                                            Toast.LENGTH_LONG).show();
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Log.d(currentUser.getUid(), "On Failure: Email not sent" + e.getMessage());
                                }
                            });

                            Toast.makeText(RegisterActivity.this,"Successful registered!",
                                    Toast.LENGTH_LONG).show(); //
                            userUID = firebaseAuth.getCurrentUser().getUid();
                            DocumentReference userDocumentReference = db.collection("Users")
                                    .document(userUID);
                            Map<String,Object> userInfo = new HashMap<>();
                            userInfo.put("UserName", userName);
                            userInfo.put("UserEmail",userEmail);
                            userInfo.put("UserPhoneNumber", userPhoneNumber);
                            userDocumentReference.set(userInfo)
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void unused) {
                                    Log.d(userName, "registered information is stored successfully");
                                }
                            }) .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Log.d(userName, "info adding failure on:" + e.toString());
                                        }
                                    });
                            startActivity(new Intent(getApplicationContext(), MainActivity.class));

                        } else {
                            Toast.makeText(RegisterActivity.this, task.getException().getMessage(),
                                    Toast.LENGTH_LONG).show();
                            userProgressStatus.setVisibility(View.GONE);
                        }
                    }
                });
            }
        });


    }
}