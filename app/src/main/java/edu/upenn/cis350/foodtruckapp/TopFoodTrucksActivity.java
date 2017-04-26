package edu.upenn.cis350.foodtruckapp;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;

public class TopFoodTrucksActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private DatabaseReference databaseRef;
    private DatabaseReference rating;
    private ArrayList<Vendor> vendors = new ArrayList<Vendor>();
    private TextView[] topTrucks = null;
    private RatingBar[] topTrucksRating = null;
    private HashMap<String, String> nameIDMap;



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
            case R.id.search_button_menu:
                Intent x = new Intent(TopFoodTrucksActivity.this, SearchFoodActivity.class);
                startActivity(x);

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
//
//        TextView firstVendorName = (TextView) findViewById(R.id.top_trucks_vendor_one);
//        TextView secondVendorName = (TextView) findViewById(R.id.top_trucks_vendor_two);
//        TextView thirdVendorName = (TextView) findViewById(R.id.top_trucks_vendor_three);
//        TextView fourthVendorName = (TextView) findViewById(R.id.top_trucks_vendor_four);
//        TextView fifthVendorName = (TextView) findViewById(R.id.top_trucks_vendor_five);
//        TextView sixthVendorName = (TextView) findViewById(R.id.top_trucks_vendor_six);
//        TextView seventhVendorName = (TextView) findViewById(R.id.top_trucks_vendor_seven);
//        TextView eighthVendorName = (TextView) findViewById(R.id.top_trucks_vendor_eight);
//
//        topTrucks = new TextView[8];
//        topTrucks[0] = firstVendorName;
//        topTrucks[1] = secondVendorName;
//        topTrucks[2] = thirdVendorName;
//        topTrucks[3] = fourthVendorName;
//        topTrucks[4] = fifthVendorName;
//        topTrucks[5] = sixthVendorName;
//        topTrucks[6] = seventhVendorName;
//        topTrucks[7] = eighthVendorName;
//
//        RatingBar firstVendorRating = (RatingBar) findViewById(R.id.top_trucks_vendor_one_rating);
//        RatingBar secondVendorRating = (RatingBar) findViewById(R.id.top_trucks_vendor_two_rating);
//        RatingBar thirdVendorRating = (RatingBar) findViewById(R.id.top_trucks_vendor_three_rating);
//        RatingBar fourthVendorRating = (RatingBar) findViewById(R.id.top_trucks_vendor_four_rating);
//        RatingBar fifthVendorRating = (RatingBar) findViewById(R.id.top_trucks_vendor_five_rating);
//        RatingBar sixthVendorRating = (RatingBar) findViewById(R.id.top_trucks_vendor_six_rating);
//        RatingBar seventhVendorRating = (RatingBar) findViewById(R.id.top_trucks_vendor_seven_rating);
//        RatingBar eighthVendorRating = (RatingBar) findViewById(R.id.top_trucks_vendor_eight_rating);
//
//        topTrucksRating = new RatingBar[8];
//        topTrucksRating[0] = firstVendorRating;
//        topTrucksRating[1] = secondVendorRating;
//        topTrucksRating[2] = thirdVendorRating;
//        topTrucksRating[3] = fourthVendorRating;
//        topTrucksRating[4] = fifthVendorRating;
//        topTrucksRating[5] = sixthVendorRating;
//        topTrucksRating[6] = seventhVendorRating;
//        topTrucksRating[7] = eighthVendorRating;

        ListView list = (ListView) findViewById(R.id.top_food_trucks_list);
        final TopTrucksAdapter adapter = new TopTrucksAdapter(this);
        list.setAdapter(adapter);
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position,
                                    long id) {
                Intent intent = new Intent(TopFoodTrucksActivity.this, VendorProfileForCustomerActivity.class);
                final TextView selectedChild = (TextView) parent.getChildAt(position);
                String selectedVendorID = (String) selectedChild.getText();

                intent.putExtra("vendorUniqueID", nameIDMap.get(selectedVendorID));
                startActivity(intent);
            }
        });
        list.setDividerHeight(10);

        // I put the names of the TextViews in an array because I think that Firebase will return
        // an ArrayList of the Vendors & it'll be an easy conversion
        databaseRef = FirebaseDatabase.getInstance().getReference().child("Users");
        vendors = new ArrayList<Vendor>();
        //populateTextFields();
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
               // populateTextFields();
                adapter.notifyDataSetChanged();

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
       // populateTextFields();
    }

//    void populateTextFields() {
//        TreeSet<Vendor> sortedVendors = new TreeSet<Vendor>();
//        sortedVendors.addAll(vendors);
//        Log.d("sorted vendors", sortedVendors.toString());
//        ArrayList<Vendor> listOfSortedVendors = new ArrayList<Vendor>();
//        listOfSortedVendors.addAll(sortedVendors);
//
//        for (int i = 0; i < Math.min(8, sortedVendors.size()); i++) {           // populate either top 8 or size of sortedVendors
//            Vendor vendor = listOfSortedVendors.get(i);
//            topTrucks[i].setText(vendor.getName());
//            topTrucksRating[i].setRating(vendor.getRating().floatValue());
//            topTrucksRating[i].setEnabled(false);
//
//        }
//    }

    public class TopTrucksAdapter extends BaseAdapter {
        private Context context;

        public TopTrucksAdapter(Context context) {
            this.context = context;
        }

        @Override
        public int getCount() {
            return vendors.size();
        }

        @Override
        public Object getItem(int position) {
            return vendors.get(position);
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }


        public View getView(int position, View convertView, ViewGroup parent){
            View customView;

            if (convertView == null) {
                LayoutInflater inflater = (LayoutInflater) context
                        .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                customView = inflater.inflate(
                        R.layout.top_trucks_item_style, null);
            } else {
                customView =convertView;
            }

            Vendor vendor = (Vendor) getItem(position);
            TextView textView = (TextView) customView.findViewById(R.id.top_trucks_name);
            RatingBar ratingBar = (RatingBar) customView.findViewById(R.id.top_trucks_rating);
            ratingBar.setRating(vendor.getRating().floatValue());
            textView.setText(vendor.getName());
            return customView;
        }
    }

    private void getNameIDMap() {
        //read from database
        databaseRef.addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                HashMap<String, Object> values = (HashMap<String, Object>) dataSnapshot.getValue();
                for (String id : values.keySet()) {
                    HashMap<String, Object> userInfo = (HashMap<String, Object>) values.get(id);

                    if (userInfo.get("Type").equals("Vendor")
                            && userInfo.containsKey("Location")
                            && userInfo.containsKey("Name Of Food Truck")
                            && userInfo.containsKey("Type Of Food")
                            && userInfo.containsKey("Active")
                            && userInfo.containsKey("Menu")) {
                        String vendorName = (String) userInfo.get("Name Of Food Truck");
                        String uniqueID = (String) userInfo.get("UniqueID");
                        nameIDMap.put(vendorName, uniqueID);
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
            }
        });
    }
}
