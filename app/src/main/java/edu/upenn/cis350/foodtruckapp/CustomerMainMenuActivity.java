package edu.upenn.cis350.foodtruckapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;

public class CustomerMainMenuActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_main_menu);

        Button nearMeButton = (Button) findViewById(R.id.button_near_me);
        nearMeButton.setOnClickListener(new AdapterView.OnClickListener() {

            public void onClick(View view) {
                Intent i = new Intent(CustomerMainMenuActivity.this, NearMeActivity.class);
                startActivity(i);
            }
        });

        Button favsButton = (Button) findViewById(R.id.button_favs);
        favsButton.setOnClickListener(new AdapterView.OnClickListener() {

            public void onClick(View view) {
                Intent i = new Intent(CustomerMainMenuActivity.this, FavoritesActivity.class);
                startActivity(i);
            }
        });

        Button topTrucksButton = (Button) findViewById(R.id.button_top_trucks);
        topTrucksButton.setOnClickListener(new AdapterView.OnClickListener() {

            public void onClick(View view) {
                Intent i = new Intent(CustomerMainMenuActivity.this, TopFoodTrucksActivity.class);
                startActivity(i);
            }
        });

    }
}
