package edu.upenn.cis350.foodtruckapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class TruckActivity extends AppCompatActivity {

    DatabaseReference mRootReference = FirebaseDatabase.getInstance().getReference();
    DatabaseReference ratingReference = mRootReference.child("Ratings");

    Double avgRating=0.0;
    Integer countValue=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_truck);
        Intent intent = getIntent();
        String TruckName = intent.getExtras().getString("truckName");
        TextView ratingText = (TextView) findViewById(R.id.ratingText);
        ratingText.setText("Give " + TruckName + " a Rating!");



        DatabaseReference truckReference = ratingReference.child(TruckName);
        final DatabaseReference averageRatingReference = truckReference.child ("AverageRating");
        final DatabaseReference count = truckReference.child("Count");


        averageRatingReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                avgRating = dataSnapshot.getValue(Double.class);
                Toast.makeText(TruckActivity.this, "Current Average Rating is " + avgRating, Toast.LENGTH_SHORT ).show();
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

        final Button button5 = (Button) findViewById(R.id.rating5);
        button5.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Integer newCountValue = countValue + 1;
                Double newRating = (avgRating*(countValue) + 5)/newCountValue;
                Log.d("rating", ""+ newRating);
                averageRatingReference.setValue(newRating);
                count.setValue(newCountValue);

                String message = "You're rating has been recorded";

                Toast.makeText(TruckActivity.this, message, Toast.LENGTH_SHORT ).show();
            }
        });
    }
}
