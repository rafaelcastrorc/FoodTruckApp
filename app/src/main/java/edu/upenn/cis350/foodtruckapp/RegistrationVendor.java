package edu.upenn.cis350.foodtruckapp;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

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
    boolean thereIsAName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration_vendor);
        database = FirebaseDatabase.getInstance();
        databaseRef = database.getReference("Users");

        Bundle b = getIntent().getExtras();
        String user = ""; // or other values
        if(b != null) {
            userID = b.getString("UserId");
        }
        typeOfFood = (EditText) findViewById(R.id.foodTypeField);
        nameOfFoodTruck = (EditText) findViewById(R.id.truckNameField);
        thereIsAName = false;
    }

    /**
     * sets image background
     * @param requestCode
     * @param resultCode
     * @param data
     */
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

    /**
     * register vendor
     * @param v
     */
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

        thereIsAName = true;
        String tof = typeOfFood.getText().toString();
        String noft = nameOfFoodTruck.getText().toString();

        //Adds this info to the the current vendor
        DatabaseReference currentVendorReference = databaseRef.child(userID);

        //Create these fields for the current vendor
        currentVendorReference.child("Type Of Food").setValue(tof);
        currentVendorReference.child("Name Of Food Truck").setValue(noft);
        currentVendorReference.child("Menu");
        currentVendorReference.child("Average Rating");
        currentVendorReference.child("Total Ratings");



        Double avgRating = 0.00;
        Integer totalRatings = 0;

        currentVendorReference.child("Average Rating").setValue(avgRating); //Initialize the vendor's Average Rating to 0.00
        currentVendorReference.child("Total Ratings").setValue(totalRatings); //Initialize the vendor's Total Ratings to 0
        currentVendorReference.child("UniqueUserID").setValue(userID);
        currentVendorReference.child("NameOfFoodTruck").setValue(nameOfFoodTruck.getText().toString()); //WHY????!!!
        currentVendorReference.child("Menu");

        DatabaseReference hoursRef = currentVendorReference.child("Hours");
        hoursRef.child("OpenWeekdayTime");
        hoursRef.child("OpenWeekdayPeriod");
        hoursRef.child("CloseWeekdayTime");
        hoursRef.child("OpenWeekdayPeriod");
        hoursRef.child("OpenWeekendTime");
        hoursRef.child("OpenWeekendPeriod");
        hoursRef.child("CloseWeekendTime");
        hoursRef.child("CloseWeekendPeriod");

        if (thereIsAName) {
            Intent i = new Intent(RegistrationVendor.this, VendorMainMenuActivity.class);
            startActivity(i);
        }
    }

}
