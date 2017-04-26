package edu.upenn.cis350.foodtruckapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.RatingBar;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.TreeSet;


public class TopFoodTrucksActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private DatabaseReference databaseRef;
    private DatabaseReference rating;
    private ArrayList<Vendor> vendors = new ArrayList<Vendor>();
    private TextView[] topTrucks = null;
    private RatingBar[] topTrucksRating = null;


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
                Intent i = new Intent(TopFoodTrucksActivity.this, Cart.class);
                startActivity(i);
                return true;
            case R.id.home_button:
                Intent j = new Intent(TopFoodTrucksActivity.this, CustomerMainMenuActivity.class);
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
        setContentView(R.layout.activity_top_food_trucks);

        TextView firstVendorName = (TextView) findViewById(R.id.top_trucks_vendor_one);
        TextView secondVendorName = (TextView) findViewById(R.id.top_trucks_vendor_two);
        TextView thirdVendorName = (TextView) findViewById(R.id.top_trucks_vendor_three);
        TextView fourthVendorName = (TextView) findViewById(R.id.top_trucks_vendor_four);
        TextView fifthVendorName = (TextView) findViewById(R.id.top_trucks_vendor_five);
        TextView sixthVendorName = (TextView) findViewById(R.id.top_trucks_vendor_six);
        TextView seventhVendorName = (TextView) findViewById(R.id.top_trucks_vendor_seven);
        TextView eighthVendorName = (TextView) findViewById(R.id.top_trucks_vendor_eight);

        topTrucks = new TextView[8];
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

        topTrucksRating = new RatingBar[8];
        topTrucksRating[0] = firstVendorRating;
        topTrucksRating[1] = secondVendorRating;
        topTrucksRating[2] = thirdVendorRating;
        topTrucksRating[3] = fourthVendorRating;
        topTrucksRating[4] = fifthVendorRating;
        topTrucksRating[5] = sixthVendorRating;
        topTrucksRating[6] = seventhVendorRating;
        topTrucksRating[7] = eighthVendorRating;

        // Todo: Populate TextViews to have name of vendors
        // I put the names of the TextViews in an array because I think that Firebase will return
        // an ArrayList of the Vendors & it'll be an easy conversion
        databaseRef = FirebaseDatabase.getInstance().getReference().child("Users");
        vendors = new ArrayList<Vendor>();
        populateTextFields();
        databaseRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                HashMap<String, Object> values = (HashMap<String, Object>) dataSnapshot.getValue();
                String name = (String) values.get("Name Of Food Truck");
                if (name == null) {
                    return;
                }
                Vendor vendor = null;
                try {
                    Double rating = (Double) values.get("Average Rating");
                    if (rating != null) {
                        vendor = new Vendor(name, rating);
                    }
                }
                catch (ClassCastException e) {
                    Long rating = (Long) values.get("Average Rating");
                    if (rating != null) {
                        vendor = new Vendor(name, rating.doubleValue());
                    }
                }
                if (vendor != null) {
                    vendors.add(vendor);
                }
                populateTextFields();
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
        populateTextFields();

//        list.setAdapter(adapter);
//        list.setOnItemClickListener(new OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> parent, View view, int position,
//                                    long id) {
//                Intent intent = new Intent(FavoritesActivity.this, VendorProfileForCustomerActivity.class);
//                final TextView selectedChild = (TextView) parent.getChildAt(position);
//
//                String selectedVendorID = (String) selectedChild.getText();
//                Log.d(selectedVendorID, selectedVendorID);
//                intent.putExtra("vendorUniqueID", vendors.get(selectedVendorID));
//                startActivity(intent);
//            }
//        });
//        list.setDividerHeight(10);
    }

    void populateTextFields() {
        TreeSet<Vendor> sortedVendors = new TreeSet<Vendor>();
        sortedVendors.addAll(vendors);
        Log.d("sorted vendors", sortedVendors.toString());
        ArrayList<Vendor> listOfSortedVendors = new ArrayList<Vendor>();
        listOfSortedVendors.addAll(sortedVendors);

        for (int i = 0; i < Math.min(8, sortedVendors.size()); i++) {           // populate either top 8 or size of sortedVendors
            Vendor vendor = listOfSortedVendors.get(i);
            topTrucks[i].setText(vendor.getName());
            topTrucksRating[i].setRating(vendor.getRating().floatValue());
            //topTrucksRating[i].setEnabled(false);

        }
    }
}
