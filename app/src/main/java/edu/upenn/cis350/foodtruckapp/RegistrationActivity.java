package edu.upenn.cis350.foodtruckapp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.IgnoreExtraProperties;
import com.google.firebase.iid.FirebaseInstanceId;

public class RegistrationActivity extends AppCompatActivity {

    private EditText email;
    private EditText password;
    private EditText name;
    private String type;
    private FirebaseAuth mAuth;
    private DatabaseReference databaseRef;
    private FirebaseDatabase database;
    boolean isSecure = false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration_customer);

        mAuth = FirebaseAuth.getInstance();
        email = (EditText) findViewById(R.id.emailField);
        password = (EditText) findViewById(R.id.passwordField);
        name = (EditText) findViewById(R.id.nameField);

        //Authentication
        mAuth = FirebaseAuth.getInstance();
        //Database
        database = FirebaseDatabase.getInstance();
        databaseRef = database.getReference("Users");
        Bundle b = getIntent().getExtras();
        String typeSelected = "";
        if(b != null)
            typeSelected = b.getString("type");
        if (typeSelected.equals("Customer")) {
            type = "Customer";
        }
        else {
            type = "Vendor";
        }

        // user is logged in with Google
        String email = b.getString("email");
        String name = b.getString("name");
        if (email != null) {
            EditText emailField = (EditText) findViewById(R.id.emailField);
            emailField.setText(email);
        }
        if (name != null) {
            EditText emailField = (EditText) findViewById(R.id.nameField);
            emailField.setText(name);
        }
    }

    /**
     * start registration from user
     * @param v
     */
    public void btnRegistrationCustomer_Click(View v) {
        userRegister(v);
    }

    /**
     * handle with user registration, make sure input parameters of email/pw are respected
     * @param v
     */
    private void userRegister(View v) {
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


        //password needs to contain one number
        if (!isSecure(password)) {
            password.setError("You need at least one number in your password");

        }

        if (isSecure) {
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
                                if (type.equals("Customer")) {
                                    Intent i = new Intent(RegistrationActivity.this, CustomerMainMenuActivity.class);
                                    startActivity(i);
                                    finish();
                                }
                                else {
                                    //If it is a vendor, it goes here
                                    Intent i = new Intent(RegistrationActivity.this, RegistrationVendor.class);
                                    Bundle b = new Bundle();
                                    b.putString("UserId", uniqueID); //Vendor user id to next
                                    i.putExtras(b); //Put your id to your next Intent
                                    startActivity(i);
                                    finish();
                                }

                            } else {
                                if (mAuth.getCurrentUser() == null) {
                                    Toast.makeText(RegistrationActivity.this, task.getException().getMessage(), Toast.LENGTH_LONG).show();
                                    return;
                                }
                                writeNewUser(name.getText().toString(), mAuth.getCurrentUser().getUid(), type, email.getText().toString());
                                if (type.equals("Customer")) {
                                    Intent i = new Intent(RegistrationActivity.this, CustomerMainMenuActivity.class);
                                    startActivity(i);
                                    finish();
                                }
                                else {
                                    //If it is a vendor, it goes here
                                    Intent i = new Intent(RegistrationActivity.this, RegistrationVendor.class);
                                    Bundle b = new Bundle();
                                    b.putString("UserId", mAuth.getCurrentUser().getUid()); //Vendor user id to next
                                    i.putExtras(b); //Put your id to your next Intent
                                    startActivity(i);
                                    finish();
                                }
                            }
                        }
                    });
        }
    }

    /**
     * check if pw is secure
     * @param password
     * @return true if pw is secure false otherwise
     */
    private boolean isSecure(EditText password) {
        String currPassword = password.getText().toString();
        for (char c : currPassword.toCharArray()) {
            if (Character.isDigit(c)) {
                isSecure = true;
                return true;
            }
        }
        isSecure = false;
        return false;
    }

    /**
     * set new user in database
     * @param name
     * @param uniqueName
     * @param type
     * @param email
     */
    private void writeNewUser(String name, String uniqueName, String type, String email) {
        User user = new User(name, uniqueName, type, email);

        databaseRef.child(user.dUniqueName).child("UniqueID").setValue(user.dUniqueName);
        databaseRef.child(user.dUniqueName).child("Name").setValue(user.dName);
        databaseRef.child(user.dUniqueName).child("Type").setValue(user.dType);
        databaseRef.child(user.dUniqueName).child("Email").setValue(user.dEmail);
        databaseRef.child(user.dUniqueName).child("InstanceID").setValue(FirebaseInstanceId.getInstance().getId());

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



