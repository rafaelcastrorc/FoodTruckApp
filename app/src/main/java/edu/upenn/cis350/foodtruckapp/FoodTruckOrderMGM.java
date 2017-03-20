package edu.upenn.cis350.foodtruckapp;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by rafaelcastro on 3/14/17.
 */

public class FoodTruckOrderMGM extends AppCompatActivity  {

    private FirebaseDatabase database;



    //TODO: Create functionality to let user order food
    //Todo: Constantly update view of queue to show ned orders

    protected String getUser() {
       // TODO: Get the latest version of the User Id from the database
        return"";
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.food_truck_order_mgm_layout);



        //for testing purposes we are going to send the notification to the same device we are testing
        String username = FirebaseInstanceId.getInstance().getId();
         FirebaseMessaging.getInstance().subscribeToTopic("user_"+username);

         sendNotificationToUser(username, "Eureka");
    }



    public void order_done_onClick(View v) {
        String user = getUser();
        user = "";
        sendNotificationToUser(user, "Your order from " + getFoodTruckName() + " is ready!");
    }

    public void accept_order_onClick(View v) {
        String user = getUser();
        user = "";
        sendNotificationToUser(user, "Your order is being prepared! ");
    }

    public void decline_order_onClick(View v) {

    }

    //This sends a request to the queue, then the Node script handles everything in our other server,
    //and sends notification to user. DO NOT MODIFY ANYTHING IN THIS FILE!
    public void sendNotificationToUser(String user, final String message) {
        //Access the queue part of the database
        database = FirebaseDatabase.getInstance();
        final DatabaseReference notifications = database.getReference("notificationRequests");
        Map notification = new HashMap<>();
        notification.put("username", user);
        notification.put("message", message);

        notifications.push().setValue(notification);

    }


    private String getFoodTruckName() {

        //Todo: Connect to database
        String foodTruckName = "";
        return foodTruckName;
    }
}