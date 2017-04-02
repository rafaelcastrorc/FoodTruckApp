package edu.upenn.cis350.foodtruckapp;

import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

/**
 * Created by desmondhoward on 3/13/17.
 */

public class VendorMainMenuActivity extends AppCompatActivity implements LocationListener{

    private FirebaseAuth mAuth;
    private DatabaseReference databaseRef;
    private FirebaseDatabase database;

    protected LocationManager locationManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vendor_main_menu);
        mAuth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        databaseRef = database.getReference("Users");

        // add click listener to 'My Orders' button
        Button nearMeButton = (Button) findViewById(R.id.button_vendor_orders);
        nearMeButton.setOnClickListener(new AdapterView.OnClickListener() {

            public void onClick(View view) {
                Intent i = new Intent(VendorMainMenuActivity.this, VendorOrdersActivity.class);
                startActivity(i);
            }
        });

        // add click listener to 'My Profile' button
        Button favsButton = (Button) findViewById(R.id.button_vendor_profile);
        favsButton.setOnClickListener(new AdapterView.OnClickListener() {

            public void onClick(View view) {
                Intent i = new Intent(VendorMainMenuActivity.this, VendorProfileActivity.class);
                startActivity(i);
            }
        });

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        locationManager.requestLocationUpdates(locationManager.GPS_PROVIDER, 0, 0, this);

    }

    public void sign_out_Vendor_onClick(View v) {
        mAuth.signOut();
        Intent i = new Intent(VendorMainMenuActivity.this, LoginActivity.class);
        startActivity(i);
        finish();
    }

    @Override
    public void onLocationChanged(Location location) {
        String coords = location.getLatitude() + ", " + location.getLongitude();
        addLocation(coords);
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }

    //TODO use this for vendor
    public void addLocation(String coords){
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        String userId  = mAuth.getCurrentUser().getUid();
        databaseRef.child(userId).child("Location").setValue(coords);
    }
}