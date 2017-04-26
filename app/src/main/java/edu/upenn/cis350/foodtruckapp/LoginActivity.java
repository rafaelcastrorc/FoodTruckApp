package edu.upenn.cis350.foodtruckapp;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
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
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

/**
 * Created by rafaelcastro on 2/20/17.
 */

public class LoginActivity extends Activity implements View.OnClickListener {


    private FirebaseAuth firebaseAuth;
    private EditText emailLogin;
    private EditText pswd;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private DatabaseReference databaseRef;
    private FirebaseDatabase database;
    private GoogleApiClient googleApiClient;
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
                    // User is signed out
                    //Log.d("User is signed out", "onAuthStateChanged:signed_out");
                    firebaseAuth.signOut();
                    // Sign out Google user if they were signed in
                    if (googleApiClient.isConnected() && !once) {
                        //signOut();
                        once = true;
                    }
                }
                updateUI(user);
            }
        };

        findViewById(R.id.google_sign_in_button).setOnClickListener(this);

    }

    // method to sign in Google user
    private void signIn() {
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(googleApiClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    // method to sign out Google user
    void signOut() {
        googleApiClient.connect();
        firebaseAuth.signOut();
        Log.d("SIGN", "SIGN");
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

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching th
        // e Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            GoogleSignInAccount account = result.getSignInAccount();
            //firebaseAuthWithGoogle(account);
            handleSignInResult(result);
        }
    }

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
                                // Sign in success, update UI with the signed-in user's information
                                signInGoogleUser(acct);

                                Log.d("Activity", "signInWithCredential:success");
                                signInGoogleUser(acct);
                            } else {
                                // If sign in fails, display a message to the user.
                                Log.d("Activity", "signInWithCredential:failure" + task.getException());
                                Toast.makeText(LoginActivity.this, "Authentication failed.",
                                        Toast.LENGTH_SHORT).show();
                                updateUI(null);
                            }

                        }
                    });
        } else {

        }
    }

    public void signInGoogleUser(GoogleSignInAccount acct) {
        FirebaseUser user = firebaseAuth.getCurrentUser();
        if (user == null) {

        }
        databaseRef = FirebaseDatabase.getInstance().getReference().child("Users");
        DatabaseReference userRef = databaseRef.child(user.getUid());
        userRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Object type = (Object) dataSnapshot.getValue();
                if (type.equals("Customer")) {
                    Intent i = new Intent(LoginActivity.this, CustomerMainMenuActivity.class);
                    i.putExtra("Email", firebaseAuth.getCurrentUser().getEmail());
                    sawType = true;
                    startActivityForResult(i, 1);
                    Log.d("custttttttt", "fdfsfdsf?");
                    return;
                }
                else if (type.equals("Vendor")) {
                    Intent i = new Intent(LoginActivity.this, VendorMainMenuActivity.class);
                    i.putExtra("Email", firebaseAuth.getCurrentUser().getEmail());
                    startActivity(i);
                    sawType = true;
                }
                else {
                    Log.d("TYPE", type.toString());
                }
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }

        });

        if (!sawType) {
            registerGoogleUser(acct.getEmail(), acct.getDisplayName());
        }
    }

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
            }
        });
        // show User Type popup
        AlertDialog alert = confirmPopupBuilder.create();
        alert.show();
    }

    // [START on_start_add_listener]
    @Override
    public void onStart() {
        firebaseAuth.addAuthStateListener(mAuthListener);
//        gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
//                .requestEmail()
//                .requestIdToken("906039078080-2cmjii5bqjti72ftpn5ff8if80725brj.apps.googleusercontent.com")
//                .build();
//        googleApiClient = new GoogleApiClient.Builder(this)
//                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
//                .build();
//        googleApiClient.connect();
        FirebaseUser currentUser = firebaseAuth.getCurrentUser();
        updateUI(currentUser);
        super.onStart();
    }

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
                                Intent i = new Intent(LoginActivity.this, CustomerMainMenuActivity.class);
                                i.putExtra("Email", firebaseAuth.getCurrentUser().getEmail());
                                startActivity(i);
                            } else {
                                Log.e("ERROR", task.getException().toString());
                                //linkWithGoogle();
                                firebaseAuth.getCurrentUser().linkWithCredential(credential)
                                        .addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                                            @Override
                                            public void onComplete(@NonNull Task<AuthResult> task) {
                                                if (task.isSuccessful()) {
                                                    Log.d("HEY", "linkWithCredential:success");
                                                    FirebaseUser user = task.getResult().getUser();
                                                    updateUI(user);
                                                } else {
                                                    Log.w("HEY", "linkWithCredential:failure", task.getException());
                                                    Toast.makeText(LoginActivity.this, "Authentication failed.",
                                                            Toast.LENGTH_SHORT).show();
                                                    updateUI(null);
                                                }

                                                // ...
                                            }
                                        });
                                Toast.makeText(LoginActivity.this, task.getException().getMessage(), Toast.LENGTH_LONG).show();

                            }
                        }
                    });
    }

    void linkWithGoogle() {
        firebaseAuth.getCurrentUser().linkWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Log.d("HEY", "linkWithCredential:success");
                            FirebaseUser user = task.getResult().getUser();
                            updateUI(user);
                        } else {
                            Log.w("HEY", "linkWithCredential:failure", task.getException());
                            Toast.makeText(LoginActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                            updateUI(null);
                        }

                        // ...
                    }
                });
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


