package edu.upenn.cis350.foodtruckapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

public class NearMeActivity extends AppCompatActivity {
    protected ArrayList<Order> orders = new ArrayList<>();

    private DatabaseReference databaseRef;
    private FirebaseDatabase database;

    HashMap<String, String> nameIDMap;

    private final String TAG = "NearMeActivity";

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

                Intent i = new Intent(NearMeActivity.this, Cart.class);
                startActivity(i);
                return true;
            case R.id.home_button:
                Intent j = new Intent(NearMeActivity.this, CustomerMainMenuActivity.class);
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
        database = FirebaseDatabase.getInstance();
        databaseRef = database.getReference("Users");
        setContentView(R.layout.activity_ftnear_me);
        nameIDMap = new HashMap<String, String>();
        setListView();
        getNameIDMap();
        setListView();
    }


    private void setListView() {
        final ListView list = (ListView) findViewById(R.id.nearMeList);

        Object[] trucksNearMeObj = nameIDMap.keySet().toArray();
        String[] trucksNearMe = new String[trucksNearMeObj.length];

        for (int i = 0; i < trucksNearMeObj.length; i++) {
            trucksNearMe[i] = (String) trucksNearMeObj[i];
        }

//        for (int i = 0; i < trucksNearMe.length; i++) {
//            Log.d(TAG, "in for loop "+trucksNearMe[i]);
//        }
//        String[] trucksNearMe = {"Hemo's", "Real Leann's", "Halal Guys", "Magic Carpet"};

        Arrays.sort(trucksNearMe);

        ListAdapter truckAdapter = new CustomTruckListAdapter(this, trucksNearMe);
        list.setAdapter(truckAdapter);

        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, final View view,
                                    int position, long id) {
                String truckName = String.valueOf(parent.getItemAtPosition(position));
                Toast.makeText(NearMeActivity.this,truckName, Toast.LENGTH_LONG).show();
                Intent i = new Intent(NearMeActivity.this,VendorProfileForCustomerActivity.class);
                i.putExtra("vendorUniqueID", nameIDMap.get(truckName));
                i.putExtra("truckName", truckName);
                startActivity(i);
            }

        });

        list.setDividerHeight(10);
    }


    public void onMapButtonClick(View view) {
        Intent i = new Intent(NearMeActivity.this, MapsActivity.class);
        startActivity(i);
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
                    setListView();
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w(TAG, "Failed to read value.", error.toException());
            }
        });
    }



}
