/**
 * Class Name: LoginActivity
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
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;


/**
 * This class is used by the user to sign in with his/her account and password for accessing and using PrePear app
 */
public class LoginActivity extends AppCompatActivity {
    // on below: initialize class variables
    private TextInputEditText userEmailInput;
    private TextInputEditText userPasswordInput;
    private TextView registerText;
    private TextView forgetPasswordText;
    private Button loginButton;
    private CheckBox loginKeeperCheckBox;
    ProgressBar loginStatusProgressBar;
    FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        userEmailInput = findViewById(R.id.userEmail_input_text);
        userPasswordInput = findViewById(R.id.password_input);
        registerText = findViewById(R.id.new_user_register_text);
        forgetPasswordText = findViewById(R.id.forget_password_text);
        loginButton = findViewById(R.id.inputs_confirm_button);
        loginKeeperCheckBox = findViewById(R.id.keep_signed_in_checkBox);
        loginStatusProgressBar = findViewById(R.id.user_login_status);
        loginStatusProgressBar.setVisibility(View.INVISIBLE);

        firebaseAuth = FirebaseAuth.getInstance();

        // On below part:
        registerText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), RegisterActivity.class));
            }
        });

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String userEmail = userEmailInput.getText().toString().trim();
                String userPassword = userPasswordInput.getText().toString().trim();

                if (TextUtils.isEmpty(userEmail)) { //
                    userEmailInput.setError("Please enter your email!");
                    return;
                }
                if (TextUtils.isEmpty(userPassword)) { //
                    userPasswordInput.setError("Please enter your password!");
                    return;
                }
                if (userPassword.length() < 6) { //
                    userPasswordInput.setError("User password minimum length is 6.");
                    return;
                }

                loginStatusProgressBar.setVisibility(View.VISIBLE);

                // On below part: authenticate the user
                firebaseAuth.signInWithEmailAndPassword(userEmail,userPassword)
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) { //
                            Toast.makeText(LoginActivity.this,"Successful login, Welcome back!",
                                    Toast.LENGTH_LONG).show();
                            startActivity(new Intent(getApplicationContext(), MainActivity.class));
                        } else {
                            Toast.makeText(LoginActivity.this, task.getException().getMessage(),
                                    Toast.LENGTH_LONG).show();
                            loginStatusProgressBar.setVisibility(View.GONE);
                        }
                    }
                });
            }
        });
    }
}