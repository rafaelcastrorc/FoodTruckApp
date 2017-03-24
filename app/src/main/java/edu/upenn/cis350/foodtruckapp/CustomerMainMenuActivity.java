package edu.upenn.cis350.foodtruckapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;

import com.google.firebase.auth.FirebaseAuth;

public class CustomerMainMenuActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_main_menu);
        mAuth = FirebaseAuth.getInstance();


        // add click listener to Food Trucks near me button
        Button nearMeButton = (Button) findViewById(R.id.button_near_me);
        nearMeButton.setOnClickListener(new AdapterView.OnClickListener() {

            public void onClick(View view) {
                Intent i = new Intent(CustomerMainMenuActivity.this, NearMeActivity.class);
                startActivity(i);
            }
        });

        // add click listener to favorites button
        Button favsButton = (Button) findViewById(R.id.button_favs);
        favsButton.setOnClickListener(new AdapterView.OnClickListener() {

            public void onClick(View view) {
                Intent i = new Intent(CustomerMainMenuActivity.this, FavoritesActivity.class);
                startActivity(i);
            }
        });

        // add click listener to top food trucks button
        Button topTrucksButton = (Button) findViewById(R.id.button_top_trucks);
        topTrucksButton.setOnClickListener(new AdapterView.OnClickListener() {

            public void onClick(View view) {
                Intent i = new Intent(CustomerMainMenuActivity.this, TopFoodTrucksActivity.class);
                startActivity(i);
            }
        });

    }

    public void sign_out_onClick(View v) {
        mAuth.signOut();
        Intent i = new Intent(CustomerMainMenuActivity.this, LoginActivity.class);
        startActivity(i);
        finish();
    }


}
