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

    /**
     * register vendor
     * @param v
     */
    public void button_vendor_click(View v) {
        Intent i = new Intent(RegisterChoiceActivity.this, RegistrationActivity.class);
        Bundle b = new Bundle();
        b.putString("type", "Vendor");
        i.putExtras(b);
        startActivity(i);
        finish();
    }

    /**
     * register customer
     * @param v
     */
    public void button_customer_click(View v) {
        Intent i = new Intent(RegisterChoiceActivity.this, RegistrationActivity.class);
        Bundle b = new Bundle();
        b.putString("type", "Customer");
        i.putExtras(b);
        startActivity(i);
        finish();
    }

}
