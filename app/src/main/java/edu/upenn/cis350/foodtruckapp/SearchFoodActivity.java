package edu.upenn.cis350.foodtruckapp;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SearchFoodActivity extends AppCompatActivity {

    private DatabaseReference databaseRef;
    Map<String,String> foodList;

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

                Intent i = new Intent(SearchFoodActivity.this, Cart.class);
                startActivity(i);
                return true;
            case R.id.home_button:
                Intent j = new Intent(SearchFoodActivity.this, CustomerMainMenuActivity.class);
                startActivity(j);
                return true;

            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);
        }
    }

    /**
     * creates and handles search logic
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_food);
        databaseRef =FirebaseDatabase.getInstance().getReference("Users");
        foodList = new HashMap<>();

        SearchView sv = (SearchView) findViewById(R.id.searchForFood);

        sv.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {

                final String queryUsable = query;

                databaseRef.addValueEventListener(new ValueEventListener() {


                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        foodList.clear();
                        Iterable<DataSnapshot> users = dataSnapshot.getChildren();
                        for (DataSnapshot user : users) {
                            DataSnapshot name = user.child("Name of Food Truck");
                            DataSnapshot type = user.child("Type");


                            if (type.getValue(String.class).equals("Vendor")) {
                                DataSnapshot TOF = user.child("Type Of Food");
                                if (TOF.getValue() == null){
                                    break;
                                }
                                DataSnapshot uniqueID = user.child("UniqueID");
                                if (uniqueID.getValue() == null){
                                    break;
                                }
                                if (((String)TOF.getValue()).equalsIgnoreCase(queryUsable)){
                                    foodList.put(name.getValue(String.class),uniqueID.getValue(String.class));
                                }
                                else {
                                    DataSnapshot menuItems = user.child("Menu");
                                    if (menuItems.getValue() == null){
                                        break;
                                    }
                                    HashMap<String, Object> values = (HashMap<String, Object>) menuItems.getValue();
                                    if (values != null) {
                                        for (String item : values.keySet()) {
                                            if (item.equalsIgnoreCase(queryUsable)) {
                                                foodList.put(name.getValue(String.class),uniqueID.getValue(String.class));
                                            }
                                        }
                                    }
                                }
                            }
                        }

                        setListView();
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }


                });
                return true; // is it okay to return true on default?
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                final String queryUsable = newText;

                databaseRef.addValueEventListener(new ValueEventListener() {


                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        foodList.clear();
                        Iterable<DataSnapshot> users = dataSnapshot.getChildren();
                        for (DataSnapshot user : users) {
                          DataSnapshot name = user.child("Name Of Food Truck");
                            DataSnapshot type = user.child("Type");


                            if (type.getValue(String.class).equals("Vendor")) {
                                DataSnapshot TOF = user.child("Type Of Food");
                                if (TOF.getValue() == null){
                                    break;
                                }
                                DataSnapshot uniqueID = user.child("UniqueID");
                                if (uniqueID.getValue() == null){
                                    break;
                                }
                                if (((String)TOF.getValue()).equalsIgnoreCase(queryUsable)) {
                                    foodList.put(name.getValue(String.class), uniqueID.getValue(String.class));
                                } else {
                                    DataSnapshot menuItems = user.child("Menu");
                                    if (menuItems.getValue() == null){
                                        break;
                                    }
                                    HashMap<String, Object> values = (HashMap<String, Object>) menuItems.getValue();
                                    if (values != null) {
                                        for (String item : values.keySet()) {
                                            if (item.equalsIgnoreCase(queryUsable)) {
                                                foodList.put(name.getValue(String.class), uniqueID.getValue(String.class));
                                            }
                                        }
                                    }
                                }
                            }
                        }

                        setListView();
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }


                });
                return true;
            }

        });




    }

    /**
     * set list view of food trucks found
     */
    private void setListView() {
        final ListView list = (ListView) findViewById(R.id.foodTruckList);

        list.setAdapter( new CustomTruckListAdapter(this, new String[0]));

        String[] trucksNearMe = new String[foodList.size()] ;
        foodList.keySet().toArray(trucksNearMe);


        Arrays.sort(trucksNearMe);

        ListAdapter truckAdapter = new CustomTruckListAdapter(this, trucksNearMe);
        list.setAdapter(truckAdapter);

        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, final View view,
                                    int position, long id) {
                String truckName = String.valueOf(parent.getItemAtPosition(position));
                Toast.makeText(SearchFoodActivity.this, truckName, Toast.LENGTH_LONG).show();
                Intent i = new Intent(SearchFoodActivity.this, VendorProfileForCustomerActivity.class);
                i.putExtra("vendorUniqueID", foodList.get(truckName));
                i.putExtra("truckName", truckName);
                startActivity(i);
            }

        });

        list.setDividerHeight(10);


    }


}
