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
    private String type;

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

    //Register as Vendor
    public void btnRegistrationVendor_Click(View v) {
        type = "Vendor";
        userRegister(v);

    }

    //Register as Costumer
    public void btnRegistrationCostumer_Click(View v) {
        type = "Costumer";
        userRegister(v);
    }



    private void userRegister(View v){
        // email cannot be empty
        if (TextUtils.isEmpty(email.getText().toString())) {
            email.setError("This field cannot be empty");
            return;
        }

        //password cannot be empty.
        if (TextUtils.isEmpty(password.getText().toString())) {
            password.setError("This field cannot be empty");
            return;
        }

        //password cannot be empty.
        if (TextUtils.isEmpty(name.getText().toString())) {
            name.setError("This field cannot be empty");
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
                            writeNewUser(name.getText().toString(), uniqueID, type, email.getText().toString());

                            //Destination where the user should go once register successfully
                            //Depends on the type of user
                            //TODO: Vendor Main Menu page 
                            Intent i = new Intent(RegistrationActivity.this, CustomerMainMenuActivity.class);
                            startActivity(i);

                        } else {
                            Log.e("There is an error", task.getException().toString());
                            Toast.makeText(RegistrationActivity.this, task.getException().getMessage(), Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }

    private void writeNewUser(String name, String uniqueName, String type, String email) {
        User user = new User(name, uniqueName, type, email);

        databaseRef.child(user.dUniqueName).child("UniqueID").setValue(user.dUniqueName);
        databaseRef.child(user.dUniqueName).child("Name").setValue(user.dName);
        databaseRef.child(user.dUniqueName).child("Type").setValue(user.dType);
        databaseRef.child(user.dUniqueName).child("Email").setValue(user.dEmail);
    }

    @IgnoreExtraProperties
    public class User {

        private String dName;
        private String dUniqueName;
        private String dType;
        private String dEmail;



        public User() {
            // Default constructor required for calls to DataSnapshot.getValue(User.class)
        }

        private User(String name, String uniqueName, String typeOfUser, String email) {
            this.dName = name;
            this.dUniqueName = uniqueName;
            this.dType = typeOfUser;
            this.dEmail = email;

        }

    }
}



