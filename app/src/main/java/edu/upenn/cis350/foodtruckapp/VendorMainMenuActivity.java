package edu.upenn.cis350.foodtruckapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;

import com.google.firebase.auth.FirebaseAuth;

/**
 * Created by desmondhoward on 3/13/17.
 */

public class VendorMainMenuActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vendor_main_menu);
        mAuth = FirebaseAuth.getInstance();


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




    }

    public void sign_out_Vendor_onClick(View v) {
        mAuth.signOut();
        Intent i = new Intent(VendorMainMenuActivity.this, LoginActivity.class);
        startActivity(i);
        finish();
    }
}