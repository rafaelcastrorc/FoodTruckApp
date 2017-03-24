package edu.upenn.cis350.foodtruckapp;

import android.util.Log;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.LinkedList;

/**
 * Created by rafaelcastro on 3/23/17.
 */

public class ProcessingRatings {
    private FirebaseDatabase database;
    private DatabaseReference databaseRef;

    ProcessingRatings() {
        database = FirebaseDatabase.getInstance();
        databaseRef = database.getReference("Users");

    }

    //uniqueUserId of the vendor you are rating
    void pushRatingToDatabase(String uniqueUserId, final int rating) {
        //For testing purposes, we can use the following uniqueUserId: T7CYz2Xq0fVlj5JnHqZXQYxg1hL2
        DatabaseReference vendor = databaseRef.child(uniqueUserId);
        final DatabaseReference ratingsOfVendor = vendor.child("Rating");

        //gets current rating
        ratingsOfVendor.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                HashMap<String, Object> values =  (HashMap<String, Object>) dataSnapshot.getValue();
                long currRating = 0;
                long counter = 0;
                for (String type: values.keySet()) {
                    if (type.equals("CurrentRating")) {
                        currRating = (long) values.get(type);
                    }
                    else if (type.equals("Counter")) {
                        counter = (long) values.get(type);

                    }
                }

                long newRating = (((currRating * counter) + rating)/(counter + 1));

                ratingsOfVendor.child("RatingHolder").child("CurrentRating").setValue(newRating);
                ratingsOfVendor.child("RatingHolder").child("Counter").setValue(counter + 1);


            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    //uniqueUserId of the vendor you want the average from
    long getAverage(String uniqueUserId) {
        DatabaseReference vendor = databaseRef.child(uniqueUserId);
        final long[] currRating = new long[1];
        vendor.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                HashMap<String, Object> values =  (HashMap<String, Object>) dataSnapshot.getValue();
                for (String type: values.keySet()) {
                    if (type.equals("CurrentRating")) {
                        currRating[0] = (long) values.get(type);
                    }

                }

            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }

        });

        return currRating[0];


    }
}
