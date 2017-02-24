package edu.upenn.cis350.foodtruckapp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
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

public class RegistrationVendor extends AppCompatActivity {

    private static final int IMAGE_GALLERY = 10;

    private Button uploadButton;
    private FirebaseAuth mAuth;
    private EditText email;
    private EditText password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration_vendor);
        uploadButton = (Button) findViewById(R.id.truckPhoto);
        mAuth = FirebaseAuth.getInstance();
        email = (EditText) findViewById(R.id.emailField);
        password = (EditText) findViewById(R.id.passwordField);
    }

    protected void btnUploadTruckPhoto(View view){
        Intent choosePhoto = new Intent(Intent.ACTION_PICK);
        //intent to pick a picture from gallery

       String path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).getPath();
        //get path where images are stored

        Uri pictureDirectory = Uri.parse(path);
        //converts the string of the path to a Uniform Resource Identifier which is used to access directory

        choosePhoto.setDataAndType(pictureDirectory,"image/*");
        //show images of all types in the directory

        startActivityForResult(choosePhoto,IMAGE_GALLERY);
        //start the image gallery activity. It will return with value IMAGE_GALLERY and invoke onActivityResult()

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == IMAGE_GALLERY){
            Uri photoPath = data.getData();
            //get path of selected image

            uploadButton.setBackgroundColor(Color.GREEN);
            uploadButton.invalidate();

        }
    }

    protected void btnRegistrationUser_Click(View view){
        final ProgressDialog progressDialog = ProgressDialog.show(RegistrationVendor.this, "Please wait", "Processing", true);
        (mAuth.createUserWithEmailAndPassword(email.getText().toString(), password.getText().toString()))
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        progressDialog.dismiss();

                        if (task.isSuccessful()) {
                            Toast.makeText(RegistrationVendor.this, "Registration successful", Toast.LENGTH_LONG).show();
                            //The activity it is supposed to go. Depends on the person who is working on this
                            //Intent i = new Intent(RegistrationActivity.this, LoginActivity.class);
                            // startActivity(i);
                        }
                        else
                        {
                            Log.e("There is an error", task.getException().toString());
                            Toast.makeText(RegistrationVendor.this, task.getException().getMessage(), Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }
}
