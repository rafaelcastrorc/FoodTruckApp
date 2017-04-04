package edu.upenn.cis350.foodtruckapp;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;

public class CustomerOrderMGMDemo extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_order_mgmdemo);


    }

    public void sendOrderToVendor_onClick(View v) {
        CustomerOrderMGM customerOrderMGM = new CustomerOrderMGM("zXpAaZitvEUgEPgpsIDQweuPmtt2");
        Button currButton = (Button) findViewById(R.id.sendOrderToVendor);

        currButton.setClickable(false);

        //For testing purposes, I am hardcoding the unique user id of the vendor@gmail.com - zXpAaZitvEUgEPgpsIDQweuPmtt2
        customerOrderMGM.sendOrderToCart("Candies \nCookies \nChocolates", "Halal Food Truck", 10.50);

    }


    public void updateOrder_onClick(View v) {
        CustomerOrderMGM customerOrderMGM = new CustomerOrderMGM("zXpAaZitvEUgEPgpsIDQweuPmtt2");
        customerOrderMGM.updateOrder("Candies \nCookies", 20.5);

    }

    public void cancelOrder_onClick(View v) {
        CustomerOrderMGM customerOrderMGM = new CustomerOrderMGM("zXpAaZitvEUgEPgpsIDQweuPmtt2");

    }


}
