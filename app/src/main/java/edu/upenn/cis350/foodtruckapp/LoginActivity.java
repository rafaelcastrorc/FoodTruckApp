package edu.upenn.cis350.foodtruckapp;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

/**
 * Created by rafaelcastro on 2/20/17.
 */

public class LoginActivity extends Activity implements View.OnClickListener {


    private static FirebaseAuth firebaseAuth;
    private EditText emailLogin;
    private EditText pswd;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private DatabaseReference databaseRef;
    private FirebaseDatabase database;
    private static GoogleApiClient googleApiClient;
    private static final int RC_SIGN_IN = 9001;
    private GoogleSignInOptions gso;
    private boolean sawType = false;
    private boolean once = false;
    private AuthCredential credential;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_page);
        emailLogin = (EditText) findViewById(R.id.input_email);
        pswd = (EditText) findViewById(R.id.input_password);

        firebaseAuth = FirebaseAuth.getInstance();

        database = FirebaseDatabase.getInstance();
        databaseRef = database.getReference("Users");

        // Necessary setup for Google login
        gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .requestIdToken("906039078080-2cmjii5bqjti72ftpn5ff8if80725brj.apps.googleusercontent.com")
                .build();

        googleApiClient = new GoogleApiClient.Builder(this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();
        googleApiClient.connect();


        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull final FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    // User is signed in
                    Log.d("User is signed in", user.getUid());
                    updateUI(user);
                } else {

                    if (googleApiClient.isConnected() && !once) {
                        signOut();
                        firebaseAuth.signOut();
                        once = true;
                    }
                }
                updateUI(user);
            }
        };

        findViewById(R.id.google_sign_in_button).setOnClickListener(this);
        SignInButton googleSignInBtn = (SignInButton) findViewById(R.id.google_sign_in_button);
        googleSignInBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signIn();
            }
        });

    }

    /**
     * sing in Google user
     */
    private void signIn() {
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(googleApiClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    // method to sign out Google user
    static void signOut() {
        googleApiClient.connect();
        firebaseAuth.signOut();
        Auth.GoogleSignInApi.signOut(googleApiClient).setResultCallback(
                new ResultCallback<Status>() {
                    @Override
                    public void onResult(Status status) {
                        // ...
                    }
                });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.google_sign_in_button:
                signIn();
                break;
        }
    }

    // handle activity result from Google login popup
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            handleSignInResult(result);
        }
    }

    // handle sign in result from Google login popup
    private void handleSignInResult(GoogleSignInResult result) {
        if (result.isSuccess()) {
            // sign in successful, login Google user to Firebase
            final GoogleSignInAccount acct = result.getSignInAccount();
            credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
            firebaseAuth.signInWithCredential(credential)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                firebaseAuth.getCurrentUser().linkWithCredential(credential)
                                        .addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                                            @Override
                                            public void onComplete(@NonNull Task<AuthResult> task) {
                                                if (task.isSuccessful()) {
                                                    Log.d("HEY", "linkWithCredential:success");
                                                    FirebaseUser user = task.getResult().getUser();
                                                    updateUI(user);
                                                } else {
                                                    registerGoogleUser(firebaseAuth.getCurrentUser().getEmail(), firebaseAuth.getCurrentUser().getDisplayName());
                                                }
                                            }
                                        });

                            } else {
                                // If sign in fails, display a message to the user.

//                                updateUI(null);
                            }

                        }
                    });
        }
    }


    // display user type popup for Google registration
    public void registerGoogleUser(final String email, final String name) {
        // ask user which type of user they want to register as
        AlertDialog.Builder confirmPopupBuilder = new AlertDialog.Builder(this);
        confirmPopupBuilder.setTitle("User Type");
        confirmPopupBuilder.setMessage("You do not yet have an account. Which type of user would you like to register as?");

        confirmPopupBuilder.setPositiveButton("Customer", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                Intent i = new Intent(LoginActivity.this, RegistrationActivity.class);
                Bundle b = new Bundle();
                b.putString("type", "Customer");
                b.putString("name", name);
                b.putString("email", email);
                i.putExtras(b);
                startActivity(i);
            }
        });
        confirmPopupBuilder.setNegativeButton("Vendor", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent i = new Intent(LoginActivity.this, RegistrationActivity.class);
                Bundle b = new Bundle();
                b.putString("type", "Vendor");
                b.putString("name", name);
                b.putString("email", email);
                i.putExtras(b);
                startActivity(i);
                Log.d("STARTED THIS????", "WHOA");
            }
        });
        // show User Type popup
        if (!sawType) {
            AlertDialog alert = confirmPopupBuilder.create();
            alert.show();
        }
    }

    // [START on_start_add_listener]
    @Override
    public void onStart() {
        firebaseAuth.addAuthStateListener(mAuthListener);

        FirebaseUser currentUser = firebaseAuth.getCurrentUser();
        updateUI(currentUser);
        super.onStart();
    }

    /**
     * disconnect fireAuth
     */
    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            firebaseAuth.removeAuthStateListener(mAuthListener);
        }
    }

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
                                updateUI(firebaseAuth.getCurrentUser());
                                FirebaseUser user = firebaseAuth.getCurrentUser();
                                updateUI(user);
                            } else {

                                Toast.makeText(LoginActivity.this, task.getException().getMessage(), Toast.LENGTH_LONG).show();

                            }
                        }
                    });
        updateUI(firebaseAuth.getCurrentUser());
    }

    public void main_register_button_click(View v) {
        Intent i = new Intent(LoginActivity.this, RegisterChoiceActivity.class);
        startActivity(i);
        finish();
    }

    private void updateUI(final FirebaseUser user) {
        if (user != null) {
            //Checks for the type of user to log in in correct page

            DatabaseReference userType = databaseRef.child(user.getUid()).child("Type");
            userType.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    String type = dataSnapshot.getValue(String.class);
                    if (type == null) {         // Google user doesn't have profile yet
                        //registerGoogleUser(user.getEmail(), user.getDisplayName());
                    }
                    else if (type.equals("Vendor")) {

                        Intent i = new Intent(LoginActivity.this, VendorMainMenuActivity.class);
                        i.putExtra("Email", firebaseAuth.getCurrentUser().getEmail());
                        startActivity(i);
                    }
                    else if (type.equals("Customer")) {
                        finish();
                        overridePendingTransition(0, 0);
                        startActivity(getIntent());
                        overridePendingTransition(0, 0);
                        Intent i = new Intent(LoginActivity.this, CustomerMainMenuActivity.class);
                        i.putExtra("Email", firebaseAuth.getCurrentUser().getEmail());
                        startActivity(i);
                    }
                }

                @Override

                public void onCancelled(DatabaseError databaseError) {

                }
            });

        }
        else {
            Log.d("user was null", "NULL");
        }
    }

}


