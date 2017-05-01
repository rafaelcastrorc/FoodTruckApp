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


    /**
     * add shopping cart to menu bar
     * @param menu
     * @return true if selected activity valid
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.shopping_cart, menu);
        return true;
    }

    /**
     * start activities clicked in menu bar
     * @param item
     * @return
     */
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

    /**
     * usual onCreate plus gets vendor information from db
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_top_food_trucks);

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
    }

    /**
     * Adapter to show trucks in a View
     */
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

}
