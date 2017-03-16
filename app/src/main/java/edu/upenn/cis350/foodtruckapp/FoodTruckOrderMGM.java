package edu.upenn.cis350.foodtruckapp;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by rafaelcastro on 3/14/17.
 */

public class FoodTruckOrderMGM extends AppCompatActivity  {

    private DatabaseReference notifications;
    private FirebaseDatabase ref;

    protected String getUser() {
       // TODO:
        return"";
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.food_truck_order_mgm_layout);
        sendNotificationToUser("Pancho", "Trial");
    }


    //The code necessary for push notifications
    //can be put in another class
    //

    public void order_done_onClick(View v) {

    }

    public void accept_order_onClick(View v) {
        String user = getUser();
        user = "";
        sendNotificationToUser(user, "Your order is being prepared! " + user);
    }

    public void decline_order_onClick(View v) {

    }

    public void sendNotificationToUser(String user, final String message) {
        //Access the queue part of the database
        ref = FirebaseDatabase.getInstance();
        notifications = ref.getReference("notificationRequests");

        //Adds the current user to the database queue
        Map notification = new HashMap<>();
        notification.put("UniqueID", user);
        notification.put("Message", message);

        notifications.push().setValue(notification);




    }

}