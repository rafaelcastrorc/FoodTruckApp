package edu.upenn.cis350.foodtruckapp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
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
import com.google.firebase.database.IgnoreExtraProperties;
import com.google.firebase.database.ValueEventListener;

public class RegistrationActivity extends AppCompatActivity {

    private EditText email;
    private EditText password;
    private EditText name;

    private FirebaseAuth mAuth;
    private DatabaseReference databaseRef;
    private FirebaseDatabase database;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        mAuth = FirebaseAuth.getInstance();
        email = (EditText) findViewById(R.id.emailField);
        password = (EditText) findViewById(R.id.passwordField);
        name = (EditText) findViewById(R.id.nameField);

        //Authentication
        mAuth = FirebaseAuth.getInstance();
        //Database
        database = FirebaseDatabase.getInstance();
        databaseRef = database.getReference("Users");



    }

    public void btnRegistrationUser_Click(View v) {
        // email cannot be empty
        if (TextUtils.isEmpty(email.getText().toString())) {
            email.setError("This field cannot be empty");
            return;
        }

        //password cannot be empty
        if (TextUtils.isEmpty(password.getText().toString())) {
            password.setError("This field cannot be empty");
            return;
        }
        final ProgressDialog progressDialog = ProgressDialog.show(RegistrationActivity.this, "Please wait", "Processing", true);
        (mAuth.createUserWithEmailAndPassword(email.getText().toString(), password.getText().toString()))
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        progressDialog.dismiss();

                        if (task.isSuccessful()) {

                            //If user is able to register
                            Toast.makeText(RegistrationActivity.this, "Registration successful", Toast.LENGTH_LONG).show();

                            // Write the user info to the database
                            FirebaseUser user = task.getResult().getUser();
                            //To represent users without clons, we use the UID
                            String uniqueID = user.getUid();
                            //Creates a new user
                            writeNewUser(name.getText().toString(), uniqueID, "Costumer");

                            //Destination where the user should go once register successfully
                            Intent i = new Intent(RegistrationActivity.this, CustomerMainMenuActivity.class);
                            startActivity(i);

                        } else {
                            Log.e("There is an error", task.getException().toString());
                            Toast.makeText(RegistrationActivity.this, task.getException().getMessage(), Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }


    private boolean isValidPassword() {
        return false;
    }

    private void writeNewUser(String name, String uniqueName, String type) {
        User user = new User(name, uniqueName, type);

        databaseRef.child(user.dUniqueName).child("UniqueID").setValue(user.dUniqueName);
        databaseRef.child(user.dUniqueName).child("Name").setValue(user.dName);
        databaseRef.child(user.dUniqueName).child("Type").setValue(user.dType);
        databaseRef.child(user.dUniqueName).child("Email").setValue(email.getText().toString());
    }

    @IgnoreExtraProperties
    public class User {

        public String dName;
        public String dUniqueName;
        public String dType;


        public User() {
            // Default constructor required for calls to DataSnapshot.getValue(User.class)
        }

        public User(String name, String uniqueName, String typeOfUser ) {
            this.dName = name;
            this.dUniqueName = uniqueName;
            this.dType = typeOfUser;

        }

    }
}



