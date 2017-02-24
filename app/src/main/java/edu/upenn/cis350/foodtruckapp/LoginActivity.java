package edu.upenn.cis350.foodtruckapp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

/**
 * Created by rafaelcastro on 2/20/17.
 */

public class LoginActivity extends AppCompatActivity {

    private EditText txtEmailLogin;
    private EditText txtPwd;
   //private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_page);
       // emailLogin = (EditText) findViewById(R.id.email_field_login);
       // pswd = (EditText) findViewById(R.id.password_field_login);
      //  firebaseAuth = FirebaseAuth.getInstance();
    }

    public void main_login_button_click (View v) {
        //final ProgressDialog progressDialog = ProgressDialog.show(LoginActivity.this, "Please wait...", "Proccessing...", true);

        //(firebaseAuth.signInWithEmailAndPassword(txtEmailLogin.getText().toString(), txtPwd.getText().toString()))
        //       .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
        //          @Override
        //          public void onComplete(@NonNull Task<AuthResult> task) {
        //             progressDialog.dismiss();
////
        //             if (task.isSuccessful()) {
        //                //Toast.makeText(LoginActivity.this, "Login successful", Toast.LENGTH_LONG).show();
        //Intent i = new Intent(LoginActivity.this, ProfileActivity.class);
        //                 //i.putExtra("Email", firebaseAuth.getCurrentUser().getEmail());
        // startActivity(i);
        //             } else {
        //                Log.e("ERROR", task.getException().toString());
        //                Toast.makeText(LoginActivity.this, task.getException().getMessage(), Toast.LENGTH_LONG).show();
//
        //             }
        //         }
        //   });
        //  }//
        Intent i = new Intent(LoginActivity.this, CustomerMainMenuActivity.class);
        startActivity(i);
        finish();

    }
    public void main_register_button_click(View v){
        Intent i = new Intent(LoginActivity.this, RegisterChoiceActivity.class);
        startActivity(i);
        finish();
    }
}
