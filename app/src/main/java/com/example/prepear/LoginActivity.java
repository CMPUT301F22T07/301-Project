/**
 * Class Name: LoginActivity
 * Version Information: Version 1.0
 * Create Date: Nov 8th, 2022
 * Author: Shihao Liu
 * Copyright Notice:
 */

package com.example.prepear;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;


/**
 * This class is used by the user to sign in with his/her account and password for accessing and using PrePear app
 */
public class LoginActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
    }
}