package edu.upenn.cis350.foodtruckapp;

import android.support.v7.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by rafaelcastro on 3/14/17.
 */


//NOTE TO EVERYONE: DO NOT MODIFY THIS FILE, THANKS
public class FoodTruckOrderMGM extends AppCompatActivity  {

    private String username;
    private FirebaseDatabase database;
    private FirebaseAuth mAuth;
    private DatabaseReference databaseRef;

    public FoodTruckOrderMGM(String username) {
        this.username  = username;
    }

    /**
     * Process order operation
     * @param i 1 if order ready, 2 if order cancelled
     */
    protected void orderDone(int i) {
        databaseRef = FirebaseDatabase.getInstance().getReference("Users");
        mAuth = FirebaseAuth.getInstance();
        if (i == 2) {
            sendOrder2();
        }
        else {
            sendOrder();
        }
    }

    /**
     * Send notification that order is ready
     */
    private void sendOrder() {
        String uniqueUID = mAuth.getCurrentUser().getUid();
        DatabaseReference nameofft = databaseRef.child(uniqueUID).child("Name Of Food Truck");

        nameofft.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String foodTruckName= dataSnapshot.getValue().toString();
                sendNotificationToUser(username, "Your order at " + foodTruckName + " is ready!");
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                System.out.println("The read failed: " + databaseError.getCode());
            }
        });
    }

    /**
     * Send notification that order was cancelled
     */
    private void sendOrder2() {
        String uniqueUID = mAuth.getCurrentUser().getUid();
        DatabaseReference nameofft = databaseRef.child(uniqueUID).child("Name Of Food Truck");

        nameofft.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String foodTruckName= dataSnapshot.getValue().toString();
                sendNotificationToUser(username, "Your order at " + foodTruckName + " was cancelled");
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                System.out.println("The read failed: " + databaseError.getCode());
            }
        });
    }

    /**
     * This sends a request to the queue, then the Node script handles everything in our other server,
     * send notif to user
     * @param user
     * @param message
     */
    private void sendNotificationToUser(String user, final String message) {
        //Access the queue part of the database
        database = FirebaseDatabase.getInstance();
        final DatabaseReference notifications = database.getReference("notificationRequests");
        Map notification = new HashMap<>();
        notification.put("username", user);
        notification.put("message", message);
        notifications.push().setValue(notification);
    }
}
