/**
 * Class Name: MainActivity
 * Version Information: Version 1.0
 * Create Date: Oct 25th, 2022
 * Last Edit Date: Nov 3rd, 2022
 * Author: Shihao Liu
 * Copyright Notice:
 */

package com.example.prepear;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

/**
 * Initialize the opening page of the app
 * Prompts the user to select a section of app by clicking the corresponding button for each section,
 * and direct the user to the user-selected section
*/
public class MainActivity extends AppCompatActivity {
    // On below part:
    private TextView userNameTextView;
    private TextView userEmailTextView;
    private TextView userPhoneNumberTextView;
    private TextView userRequiredVerificationTextView;
    private Button emailVerificationActionButton;
    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore db;
    String userUID;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // On below: set the customized MainActivity's content up front after launching the app
        setContentView(R.layout.activity_main);
        // On below: grab the Button for (after clicking)launching Ingredient Storage Section of the app
        final Button ingredientStorageButton = findViewById(R.id.ingredient_storage_button);
        directToViewIngredientStorage(ingredientStorageButton);
        // On below: grab the Button for (after clicking)launching Recipe Folder Section of the app
        final Button recipeFolderButton = findViewById(R.id.recipe_folder_button);
        directToViewRecipeFolder(recipeFolderButton);
        // On below part:
        final Button shoppingListButton = findViewById(R.id.shopping_list_button);
        // On below Part:
        final Button mealPlanButton = findViewById(R.id.meal_planner_button);

        firebaseAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        final FirebaseUser currentUser = firebaseAuth.getCurrentUser();

        userNameTextView = findViewById(R.id.userName_text);
        userEmailTextView = findViewById(R.id.userEmail_text);
        userPhoneNumberTextView = findViewById(R.id.user_phoneNumber_text);
        userRequiredVerificationTextView = findViewById(R.id.email_verification_text);
        emailVerificationActionButton = findViewById(R.id.email_verification_action_button);
        if (currentUser.isEmailVerified()) {
            ingredientStorageButton.setEnabled(true);
            recipeFolderButton.setEnabled(true);
            mealPlanButton.setEnabled(true);
            shoppingListButton.setEnabled(true);
            userRequiredVerificationTextView.setVisibility(View.INVISIBLE);
            emailVerificationActionButton.setVisibility(View.INVISIBLE);
        }
        if (! currentUser.isEmailVerified()) { // if the user has not complete email verification link
            userRequiredVerificationTextView.setVisibility(View.VISIBLE);
            emailVerificationActionButton.setVisibility(View.VISIBLE);
            userNameTextView.setVisibility(View.INVISIBLE);
            userEmailTextView.setVisibility(View.INVISIBLE);
            userPhoneNumberTextView.setVisibility(View.INVISIBLE);
            ingredientStorageButton.setEnabled(false);
            recipeFolderButton.setEnabled(false);
            mealPlanButton.setEnabled(false);
            shoppingListButton.setEnabled(false);
            emailVerificationActionButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // On below part: send the verification link to user's email
                    currentUser.sendEmailVerification().addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            Toast.makeText(v.getContext(), "Verification Email has been sent.",
                                    Toast.LENGTH_LONG).show();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.d(currentUser.getUid(), "On Failure: Email not sent" + e.getMessage());
                        }
                    });
                }
            });
        }


        userUID = firebaseAuth.getCurrentUser().getUid();
        DocumentReference currentUserDocRef = db.collection("Users").document(userUID);
        currentUserDocRef.addSnapshotListener(this, new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                userNameTextView.setText(value.getString("UserName"));
                userEmailTextView.setText(value.getString("UserEmail"));
                userPhoneNumberTextView.setText(value.getString("UserPhoneNumber"));
            }
        });
    }

    /**
     * Direct to ViewIngredientStorage Activity after clicking the "INGREDIENT STORAGE" button on MainActivity
     * @param clickedButton clickedButton an button for the user to click,
     * and be directed to the corresponding activity
     */
    public void directToViewIngredientStorage(Button clickedButton) {
        clickedButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // On below: initialize a intent for launching the Ingredient Storage Activity
                Intent intentForViewIngredientStorageActivity = new Intent(MainActivity.this,
                        ViewIngredientStorageActivity.class);
                // On below: start launching
                startActivity(intentForViewIngredientStorageActivity);
            }
        });
    }

    /**
     * Direct to ViewRecipeList Activity after clicking the "RECIPE FOLDER" button on MainActivity
     * @param clickedButton clickedButton an button for the user to click,
     * and be directed to the corresponding activity
     */
    public void directToViewRecipeFolder(Button clickedButton){
        clickedButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // On below: initialize a intent for launching the Recipe Folder Activity
                Intent intentForViewRecipeListActivity = new Intent(MainActivity.this,
                        ViewRecipeListActivity.class);
                // On below: After clicking the button, start launching
                startActivity(intentForViewRecipeListActivity);
            }
        });
    }

    /**
     * Log out from PrePear and will be re-directed to Login Activity
     * @param view
     */
    public void userLogout(View view){
        FirebaseAuth.getInstance().signOut();
        startActivity(new Intent(getApplicationContext(), LoginActivity.class));
        finish();
    }

}