package edu.upenn.cis350.foodtruckapp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class RegistrationVendor extends AppCompatActivity {

    private static final int IMAGE_GALLERY = 10;

    private Button uploadButton;
    private DatabaseReference databaseRef;
    private FirebaseDatabase database;
    private String userID;
    EditText typeOfFood;
    EditText nameOfFoodTruck;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration_vendor);
        uploadButton = (Button) findViewById(R.id.truckPhoto);
        database = FirebaseDatabase.getInstance();
        databaseRef = database.getReference("Users");
        Bundle b = getIntent().getExtras();
        String user = ""; // or other values
        if(b != null)
            userID = b.getString("UserId");
        typeOfFood = (EditText) findViewById(R.id.foodTypeField);
        nameOfFoodTruck = (EditText) findViewById(R.id.truckNameField);
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

    //Once regiter button is pressed
    public void btnRegistrationVendor2_Click(View v) {

        // typeOfFood cannot be empty
        if (TextUtils.isEmpty(typeOfFood.getText().toString())) {
            typeOfFood.setError("This field cannot be empty");
            return;
        }

        // typeOfFood cannot be empty
        if (TextUtils.isEmpty(nameOfFoodTruck.getText().toString())) {
            nameOfFoodTruck.setError("This field cannot be empty");
            return;
        }
        String tof = typeOfFood.getText().toString();
        String noft = nameOfFoodTruck.getText().toString();

        //Adds this info to the user database
        databaseRef.child(userID).child("Type Of Food").setValue(tof);
        databaseRef.child(userID).child("Name Of Food Truck").setValue(noft);

        //Todo: Vendor page
        Intent i = new Intent(RegistrationVendor.this, CustomerMainMenuActivity.class);
        startActivity(i);
    }

}
