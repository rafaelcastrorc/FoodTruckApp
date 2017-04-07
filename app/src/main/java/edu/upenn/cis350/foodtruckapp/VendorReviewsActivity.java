package edu.upenn.cis350.foodtruckapp;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Arrays;

public class VendorReviewsActivity extends AppCompatActivity {
    private DatabaseReference vendorReviewsRef;
    private ArrayAdapter<String> adapter;
    private ArrayList<String> reviews;
    private String vendorUniqueID; //where do I get this from

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vendor_reviews);
        vendorUniqueID = getIntent().getExtras().getString("UniqueID");
        vendorReviewsRef = FirebaseDatabase.getInstance().getReference("Users").child(vendorUniqueID).child("Reviews");


        final ListView list = (ListView) findViewById(R.id.reviews_list);
        reviews = new ArrayList<String>();

        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, reviews);

        list.setAdapter(adapter);

        vendorReviewsRef.addValueEventListener(new ValueEventListener() {


            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Iterable<DataSnapshot> children = dataSnapshot.getChildren();
                reviews.clear();
                for (DataSnapshot child : children) {
                    String review = child.getValue(String.class);
                    reviews.add(review);
                }
                adapter.notifyDataSetChanged();

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }


        });

    }

}
