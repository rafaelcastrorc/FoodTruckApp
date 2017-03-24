package edu.upenn.cis350.foodtruckapp;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.RatingBar;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;


public class TopFoodTrucksActivity extends AppCompatActivity {
    //Pull food trucks from database
    //Get their unique ids
    //uniqueId.getChild("Name Of Food Truck")
    //Todo: Start counter with 0 rating and 0
    //ProcessingRatings pr = new ProcessingRatings();
    //pr.pushRatingToDatabase("K1Z7QIYsM9QtVDMI2hMUnLIGcIy2", 4);

    private FirebaseAuth mAuth;
    private DatabaseReference databaseRef;
    private DatabaseReference rating;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_top_food_trucks);
        //Pull food trucks from database
        //Get their unique ids
        //uniqueId.getChild("Name Of Food Truck")
        //ProcessingRatings pr = new ProcessingRatings();
        //pr.pushRatingToDatabase("K1Z7QIYsM9QtVDMI2hMUnLIGcIy2", 4);

        TextView firstVendorName = (TextView) findViewById(R.id.top);
        TextView secondVendorName = (TextView) findViewById(R.id.top_trucks_vendor_two);
        TextView thirdVendorName = (TextView) findViewById(R.id.top_trucks_vendor_three);
        TextView fourthVendorName = (TextView) findViewById(R.id.top_trucks_vendor_four);
        TextView fifthVendorName = (TextView) findViewById(R.id.top_trucks_vendor_five);
        TextView sixthVendorName = (TextView) findViewById(R.id.top_trucks_vendor_six);
        TextView seventhVendorName = (TextView) findViewById(R.id.top_trucks_vendor_seven);
        TextView eighthVendorName = (TextView) findViewById(R.id.top_trucks_vendor_eight);

        TextView[] topTrucks = new TextView[8];
        topTrucks[0] = firstVendorName;
        topTrucks[1] = secondVendorName;
        topTrucks[2] = thirdVendorName;
        topTrucks[3] = fourthVendorName;
        topTrucks[4] = fifthVendorName;
        topTrucks[5] = sixthVendorName;
        topTrucks[6] = seventhVendorName;
        topTrucks[7] = eighthVendorName;

        RatingBar firstVendorRating = (RatingBar) findViewById(R.id.top_trucks_vendor_one_rating);
        RatingBar secondVendorRating = (RatingBar) findViewById(R.id.top_trucks_vendor_two_rating);
        RatingBar thirdVendorRating = (RatingBar) findViewById(R.id.top_trucks_vendor_three_rating);
        RatingBar fourthVendorRating = (RatingBar) findViewById(R.id.top_trucks_vendor_four_rating);
        RatingBar fifthVendorRating = (RatingBar) findViewById(R.id.top_trucks_vendor_five_rating);
        RatingBar sixthVendorRating = (RatingBar) findViewById(R.id.top_trucks_vendor_six_rating);
        RatingBar seventhVendorRating = (RatingBar) findViewById(R.id.top_trucks_vendor_seven_rating);
        RatingBar eighthVendorRating = (RatingBar) findViewById(R.id.top_trucks_vendor_eight_rating);

        RatingBar[] topTrucksRating = new RatingBar[8];
        topTrucksRating[0] = firstVendorRating;
        topTrucksRating[1] = secondVendorRating;
        topTrucksRating[2] = thirdVendorRating;
        topTrucksRating[3] = fourthVendorRating;
        topTrucksRating[4] = fifthVendorRating;
        topTrucksRating[5] = sixthVendorRating;
        topTrucksRating[6] = seventhVendorRating;
        topTrucksRating[7] = eighthVendorRating;

        // Todo: Populate TextViews to have name of vendors

//        dinosaursRef.orderByChild("height").addChildEventListener(new ChildEventListener() {
//            @Override
//            public void onChildAdded(DataSnapshot dataSnapshot, String prevChildKey) {
//                Dinosaur dinosaur = dataSnapshot.getValue(Dinosaur.class);
//                System.out.println(dataSnapshot.getKey() + " was " + dinosaur.height + " meters tall.");
//            }
//
//            // ...
//        });
//        ArrayList<>
//        databaseRef = FirebaseDatabase.getInstance().getReference("Ratings");
//        databaseRef.addChildEventListener(new ChildEventListener() {
//
//            @Override
//            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
//                HashMap<String, Object> values =  (HashMap<String, Object>) dataSnapshot.getValue();
//                for (String type: values.keySet()) {
//
//            }
//
//            @Override
//            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
//
//            }
//
//            @Override
//            public void onChildRemoved(DataSnapshot dataSnapshot) {
//
//            }
//
//            @Override
//            public void onChildMoved(DataSnapshot dataSnapshot, String s) {
//
//            }
//
//            @Override
//            public void onCancelled(DatabaseError databaseError) {
//
//            }
//        })
//        HashMap<String, Object> foodTrucks = databaseRef.get
//
//        databaseRef.orderByChild("AverageRating").addChildEventListener(new ChildEventListener() {
//
//            @Override
//            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
//                HashMap<String, Object> values =  (HashMap<String, Object>) dataSnapshot.getValue();
//                for (String type: values.keySet()) {
//
//                    if (type.equals("AverageRating")) {
//                        this.instanceId = (String) values.get(type);
//
//                    }
//                    else if (type.equals("Order")) {
//                        this.order = (String) values.get(type);
//                    }
//                    else if (type.equals("CustomerName")){
//                        this.customerName = (String) values.get(type);
//                    }
//                    else if (type.equals("PushId")){
//                        this.pushId = (String) values.get(type);
//                    }
//                }
//            }
//
//            @Override
//            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
//
//            }
//
//            @Override
//            public void onChildRemoved(DataSnapshot dataSnapshot) {
//
//            }
//
//            @Override
//            public void onChildMoved(DataSnapshot dataSnapshot, String s) {
//
//            }
//
//            @Override
//            public void onCancelled(DatabaseError databaseError) {
//
//            }
//        })
//        mAuth = FirebaseAuth.getInstance();
//        String currVendor = mAuth.getCurrentUser().getUid();
//
//        for (int i = 0; i < 8; i++) {
//            rating = databaseRef.child(currVendor).child("Orders");
//            topTrucks[i].setText("SET ME TO A VENDOR NAME");
//        }
//
//        // Todo: Populate RatingBars to have ratings of vendors
//
//        for (int i = 0; i < 8; i++) {
//           // topTrucksRating[i].setRating(4);
//        }

    }


    //Get all the vendors names

}
