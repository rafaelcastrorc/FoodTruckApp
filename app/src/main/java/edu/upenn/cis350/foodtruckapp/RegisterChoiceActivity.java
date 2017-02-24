package edu.upenn.cis350.foodtruckapp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class RegisterChoiceActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_choice);
    }

    public void registerVendor(View view) {
        Intent i = new Intent(this, RegistrationVendor.class);
        startActivity(i);
    }

    public void registerCustomer(View view) {
        Intent i = new Intent(this, RegistrationActivity.class);
        startActivity(i);
    }
}
