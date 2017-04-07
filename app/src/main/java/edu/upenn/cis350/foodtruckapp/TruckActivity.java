package edu.upenn.cis350.foodtruckapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;

public class TruckActivity extends AppCompatActivity {

    private DatabaseReference mRootReference = FirebaseDatabase.getInstance().getReference();
    private DatabaseReference ratingReference = mRootReference.child("Ratings");
    private Double avgRating=0.0;
    private Integer countValue=0;
    private boolean isCorrectVendor = false;
    private String vendorId = "";

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.shopping_cart, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.shopping_cart_button:

                Intent i = new Intent(TruckActivity.this, Cart.class);
                startActivity(i);
                return true;
            case R.id.home_button:
                Intent j = new Intent(TruckActivity.this, CustomerMainMenuActivity.class);
                startActivity(j);
                return true;

            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_truck);
        Intent intent = getIntent();
        final String TruckName = intent.getExtras().getString("truckName");
        TextView ratingText = (TextView) findViewById(R.id.ratingText);
        ratingText.setText("Give " + TruckName + " a Rating!");

        Log.d("hey", "hey");
        ratingReference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                HashMap<String, Object> values = (HashMap<String, Object>) dataSnapshot.getValue();

                String name = null;
                Log.d("hey", "hey");

                for (Map.Entry<String, Object> entry : values.entrySet()) {
                    Log.d("yo", "yo");
                    if (entry.getKey().equals("NameOfFoodTruck")) {
                        name = (String) entry.getValue();
                        Log.d("HERE", "HRERE");
                        if (name.equals(TruckName)) {
                            isCorrectVendor = true;
                            Log.d("hello", "hello");
                        }
                    } else if (entry.getKey().equals("UniqueUserID") && isCorrectVendor) {
                        vendorId = (String) entry.getValue();
                        getValues();
                        return;
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
//        DatabaseReference truckReference = ratingReference.child(vendorId);
//        final DatabaseReference averageRatingReference = truckReference.child ("AverageRating");
//        final DatabaseReference count = truckReference.child("Count");
//
//
//        averageRatingReference.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot dataSnapshot) {
//                avgRating = dataSnapshot.getValue(Double.class);
//                Toast.makeText(TruckActivity.this, "Current Average Rating is " + avgRating, Toast.LENGTH_SHORT ).show();
//            }
//
//            @Override
//            public void onCancelled(DatabaseError databaseError) {
//
//            }
//        });
//
//        count.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot dataSnapshot) {
//                countValue = dataSnapshot.getValue(Integer.class);
//            }
//
//            @Override
//            public void onCancelled(DatabaseError databaseError) {
//
//            }
//        });
//
//        final Button button1 = (Button) findViewById(R.id.rating1);
//        button1.setOnClickListener(new View.OnClickListener() {
//            public void onClick(View v) {
//                Integer newCountValue = countValue + 1;
//                Double newRating = (avgRating*(countValue) + 1)/newCountValue;
//                Log.d("rating", ""+ newRating);
//
//                count.setValue(newCountValue);
//                averageRatingReference.setValue(newRating);
//
//                String message = "You're rating has been recorded";
//
//                Toast.makeText(TruckActivity.this, message, Toast.LENGTH_SHORT ).show();
//            }
//        });
//
//        final Button button2= (Button) findViewById(R.id.rating2);
//        button2.setOnClickListener(new View.OnClickListener() {
//            public void onClick(View v) {
//                Integer newCountValue = countValue + 1;
//                Double newRating = (avgRating*(countValue) + 2)/newCountValue;
//                Log.d("rating", ""+ newRating);
//                averageRatingReference.setValue(newRating);
//                count.setValue(newCountValue);
//
//                String message = "You're rating has been recorded";
//
//                Toast.makeText(TruckActivity.this, message, Toast.LENGTH_SHORT ).show();
//            }
//        });
//
//        final Button button3 = (Button) findViewById(R.id.rating3);
//        button3.setOnClickListener(new View.OnClickListener() {
//            public void onClick(View v) {
//                Integer newCountValue = countValue + 1;
//                Double newRating = (avgRating*(countValue) + 3)/newCountValue;
//                Log.d("rating", ""+ newRating);
//                averageRatingReference.setValue(newRating);
//                count.setValue(newCountValue);
//
//                String message = "You're rating has been recorded";
//
//                Toast.makeText(TruckActivity.this, message, Toast.LENGTH_SHORT ).show();
//            }
//        });
//
//        final Button button4 = (Button) findViewById(R.id.rating4);
//        button4.setOnClickListener(new View.OnClickListener() {
//            public void onClick(View v) {
//                Integer newCountValue = countValue + 1;
//                Double newRating = (avgRating*(countValue) + 4)/newCountValue;
//                Log.d("rating", ""+ newRating);
//                averageRatingReference.setValue(newRating);
//                count.setValue(newCountValue);
//
//                String message = "You're rating has been recorded";
//
//                Toast.makeText(TruckActivity.this, message, Toast.LENGTH_SHORT ).show();
//            }
//        });

    }

    void getValues() {
        DatabaseReference truckReference = ratingReference.child(vendorId);
        final DatabaseReference averageRatingReference = truckReference.child ("AverageRating");
        final DatabaseReference count = truckReference.child("Count");


        averageRatingReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                avgRating = dataSnapshot.getValue(Double.class);
                String df = new DecimalFormat("#.#").format(avgRating);
                Toast.makeText(TruckActivity.this, "Current Average Rating is " + df, Toast.LENGTH_SHORT ).show();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        count.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                countValue = dataSnapshot.getValue(Integer.class);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        final Button button1 = (Button) findViewById(R.id.rating1);
        button1.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Integer newCountValue = countValue + 1;
                Double newRating = (avgRating*(countValue) + 1)/newCountValue;
                Log.d("rating", ""+ newRating);

                count.setValue(newCountValue);
                averageRatingReference.setValue(newRating);

                String message = "You're rating has been recorded";

                Toast.makeText(TruckActivity.this, message, Toast.LENGTH_SHORT ).show();
            }
        });

        final Button button2= (Button) findViewById(R.id.rating2);
        button2.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Integer newCountValue = countValue + 1;
                Double newRating = (avgRating*(countValue) + 2)/newCountValue;
                Log.d("rating", ""+ newRating);
                averageRatingReference.setValue(newRating);
                count.setValue(newCountValue);

                String message = "You're rating has been recorded";

                Toast.makeText(TruckActivity.this, message, Toast.LENGTH_SHORT ).show();
            }
        });

        final Button button3 = (Button) findViewById(R.id.rating3);
        button3.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Integer newCountValue = countValue + 1;
                Double newRating = (avgRating*(countValue) + 3)/newCountValue;
                Log.d("rating", ""+ newRating);
                averageRatingReference.setValue(newRating);
                count.setValue(newCountValue);

                String message = "You're rating has been recorded";

                Toast.makeText(TruckActivity.this, message, Toast.LENGTH_SHORT ).show();
            }
        });

        final Button button4 = (Button) findViewById(R.id.rating4);
        button4.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Integer newCountValue = countValue + 1;
                Double newRating = (avgRating*(countValue) + 4)/newCountValue;
                Log.d("rating", ""+ newRating);
                averageRatingReference.setValue(newRating);
                count.setValue(newCountValue);

                String message = "You're rating has been recorded";

                Toast.makeText(TruckActivity.this, message, Toast.LENGTH_SHORT ).show();
            }
        });
    }
}
