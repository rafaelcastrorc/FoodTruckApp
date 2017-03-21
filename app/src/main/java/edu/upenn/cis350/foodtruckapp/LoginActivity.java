package edu.upenn.cis350.foodtruckapp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

/**
 * Created by rafaelcastro on 2/20/17.
 */

public class LoginActivity extends AppCompatActivity {


    private FirebaseAuth firebaseAuth;
    private EditText emailLogin;
    private EditText pswd;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private DatabaseReference databaseRef;
    private FirebaseDatabase database;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_page);
        emailLogin = (EditText) findViewById(R.id.email_login_field);
        pswd = (EditText) findViewById(R.id.password_field_login);
        firebaseAuth = FirebaseAuth.getInstance();

        database = FirebaseDatabase.getInstance();
        databaseRef = database.getReference("Users");



        // [START auth_state_listener]
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    // User is signed in
                    Log.d("User is signed in", user.getUid());
                } else {
                    // User is signed out
                    Log.d("User is signed out", "onAuthStateChanged:signed_out");
                }
                updateUI(user);
            }
        };
    }

    // [START on_start_add_listener]
    @Override
    public void onStart() {
        super.onStart();
        firebaseAuth.addAuthStateListener(mAuthListener);
    }
    // [END on_start_add_listener]

    // [START on_stop_remove_listener]
    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            firebaseAuth.removeAuthStateListener(mAuthListener);
        }
    }
    // [END on_stop_remove_listener]


    public void main_login_button_click(View v) {

        //password cannot be empty.
        if (TextUtils.isEmpty(pswd.getText().toString())) {
            pswd.setError("This field cannot be empty");
            return;
        }
        //password cannot be empty.
        if (TextUtils.isEmpty(emailLogin.getText().toString())) {
            emailLogin.setError("This field cannot be empty");
            return;
        }

        final ProgressDialog progressDialog = ProgressDialog.show(LoginActivity.this, "Please wait", "Proccessing...", true);
        (firebaseAuth.signInWithEmailAndPassword(emailLogin.getText().toString(), pswd.getText().toString()))
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        progressDialog.dismiss();

                        if (task.isSuccessful()) {
                            Toast.makeText(LoginActivity.this, "Login successful", Toast.LENGTH_LONG).show();
                            Intent i = new Intent(LoginActivity.this, CustomerMainMenuActivity.class);
                            i.putExtra("Email", firebaseAuth.getCurrentUser().getEmail());
                            startActivity(i);
                        } else {
                            Log.e("ERROR", task.getException().toString());
                            Toast.makeText(LoginActivity.this, task.getException().getMessage(), Toast.LENGTH_LONG).show();

                        }
                    }
                });
    }

    public void main_register_button_click(View v) {
        Intent i = new Intent(LoginActivity.this, RegisterChoiceActivity.class);
        startActivity(i);
        finish();
    }

    private void updateUI(FirebaseUser user) {
        if (user != null) {
            //Checks for the type of user to log in in correct page

            DatabaseReference userType = databaseRef.child(user.getUid()).child("Type");

            userType.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    String type = dataSnapshot.getValue(String.class);
                    if (type.equals("Vendor")) {
                        Intent i = new Intent(LoginActivity.this, VendorMainMenuActivity.class);
                        i.putExtra("Email", firebaseAuth.getCurrentUser().getEmail());
                        startActivity(i);
                        finish();
                    }
                    else {
                        Intent i = new Intent(LoginActivity.this, CustomerMainMenuActivity.class);
                        i.putExtra("Email", firebaseAuth.getCurrentUser().getEmail());
                        startActivity(i);
                        finish();
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });




        }
    }


}


