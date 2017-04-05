package edu.upenn.cis350.foodtruckapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

public class CustomerOrderMGMDemo extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_order_mgmdemo);
    }

    //You need the UID of the vendor, his food truck name, the order written as a string separated by \n and the price.

    public void sendOrderToVendor_onClick(View v) {
            CustomerOrderMGM customerOrderMGM = new CustomerOrderMGM();
            customerOrderMGM.setVendorUniqueID("zXpAaZitvEUgEPgpsIDQweuPmtt2");
            Button currButton = (Button) findViewById(R.id.addOrderToCart);
        //For testing purposes, I am hardcoding the unique user id of the vendor@gmail.com - zXpAaZitvEUgEPgpsIDQweuPmtt2
            customerOrderMGM.addOrderToCart("Chocolates", "Halal Food Truck", 10.50);

    }



    public void gotoPage_onClick(View v) {
        Intent i = new Intent(CustomerOrderMGMDemo.this, Cart.class);
        startActivity(i);

    }




}
