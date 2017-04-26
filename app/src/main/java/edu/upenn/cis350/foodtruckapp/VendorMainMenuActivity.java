package edu.upenn.cis350.foodtruckapp;

import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.plus.People;
import com.google.android.gms.plus.Plus;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

/**
 * Created by desmondhoward on 3/13/17.
 */

public class VendorMainMenuActivity extends AppCompatActivity implements LocationListener, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener,
        ResultCallback<People.LoadPeopleResult>, View.OnClickListener {

    private FirebaseAuth mAuth;
    private DatabaseReference databaseRef;
    private FirebaseDatabase database;

    protected LocationManager locationManager;
    protected LocationListener locationListener;

    ToggleButton toggleButton;
    Button setLocationButton;
    Button nearMeButton;
    Button favsButton;

    Location location;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_for_vendor, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            case R.id.home_button:
                Intent j = new Intent(VendorMainMenuActivity.this, VendorMainMenuActivity.class);
                startActivity(j);
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vendor_main_menu);
        mAuth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        databaseRef = database.getReference("Users");

        // add click listener to 'My Orders' button
        nearMeButton = (Button) findViewById(R.id.button_vendor_orders);
        nearMeButton.setOnClickListener(new AdapterView.OnClickListener() {

            public void onClick(View view) {
                Intent i = new Intent(VendorMainMenuActivity.this, VendorOrdersActivity.class);
                startActivity(i);
            }
        });


        // add click listener to 'My Profile' button
        favsButton = (Button) findViewById(R.id.button_vendor_profile);
        favsButton.setOnClickListener(new AdapterView.OnClickListener() {

            public void onClick(View view) {
                Intent i = new Intent(VendorMainMenuActivity.this, VendorProfileActivity.class);
                startActivity(i);
            }
        });

        // add click listener to Active toggle button
        toggleButton = (ToggleButton) findViewById(R.id.active_toggleButton);
        toggleButton.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {
                boolean isActive = toggleButton.isChecked();
                setActiveStatus(isActive);
            }
        });

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        locationManager.requestLocationUpdates(locationManager.GPS_PROVIDER, 0, 0, this);

        setLocationButton = (Button) findViewById(R.id.set_location);
        setLocationButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                setLocation(location);
            }
        });

    }

    public void sign_out_Vendor_onClick(View v) {
        setActiveStatus(false);
        mAuth.signOut();
        final GoogleApiClient googleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this).addApi(Plus.API)
                .addScope(Plus.SCOPE_PLUS_LOGIN).build();

        Log.d("LOGOUT", "TEST");
        if (googleApiClient.isConnected()) {
            Plus.AccountApi.clearDefaultAccount(googleApiClient);
            googleApiClient.disconnect();
            googleApiClient.connect();
        }

        Intent i = new Intent(VendorMainMenuActivity.this, LoginActivity.class);
        //startActivity(i);
        startActivityForResult(i, 1);
        finish();
    }


    @Override
    public void onLocationChanged(Location location) {
        this.location = location;
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

    public void setLocation(Location location){
        String userId  = mAuth.getCurrentUser().getUid();
        String lat = Double.toString(location.getLatitude());
        String lng = Double.toString(location.getLongitude());
        databaseRef.child(userId).child("Location").setValue(lat+", "+lng);
        Toast.makeText(getApplicationContext(), "You have correctly set your location!", Toast.LENGTH_SHORT).show();
    }

    public void setActiveStatus(boolean status) {
        String userId  = mAuth.getCurrentUser().getUid();
        databaseRef.child(userId).child("Active").setValue(status);
    }


    @Override
    public void onConnected(@Nullable Bundle bundle) {

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onClick(View v) {

    }

    @Override
    public void onResult(@NonNull People.LoadPeopleResult loadPeopleResult) {

    }
}