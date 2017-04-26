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
                            DataSnapshot name = user.child("Name");
                            DataSnapshot type = user.child("Type");


                            if (type.getValue(String.class).equals("Vendor")) {
                                DataSnapshot TOF = user.child("Type Of Food");
                                DataSnapshot uniqueID = user.child("UniqueID");
                                if (((String)TOF.getValue()).equalsIgnoreCase(queryUsable)){
                                    foodList.put(name.getValue(String.class),uniqueID.getValue(String.class));
                                }
                                else {
                                    DataSnapshot menuItems = user.child("Menu");
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
                            DataSnapshot name = user.child("Name");
                            DataSnapshot type = user.child("Type");


                            if (type.getValue(String.class).equals("Vendor")) {
                                DataSnapshot TOF = user.child("Type Of Food");
                                DataSnapshot uniqueID = user.child("UniqueID");
                                if (((String)TOF.getValue()).equalsIgnoreCase(queryUsable)) {
                                    foodList.put(name.getValue(String.class), uniqueID.getValue(String.class));
                                } else {
                                    DataSnapshot menuItems = user.child("Menu");
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
                return true; // is it okay to return true on default?
            }

        });




    }

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

        /*
        //Handle total bar
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        DatabaseReference databaseRef = FirebaseDatabase.getInstance().getReference("Users");

        DatabaseReference myOrdersRef = databaseRef.child(mAuth.getCurrentUser().getUid()).child("MyOrders");
        myOrdersRef.addChildEventListener(new ChildEventListener() {
            public String vendorUniqueID = "";
            String instanceId = "";
            String order = "";
            String customerName = "";
            String pushId = "";
            String foodTruckName = "";
            double price = 0.0;



            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String prevChildKey) {
                boolean status = false;
                HashMap<String, Object> values =  (HashMap<String, Object>) dataSnapshot.getValue();
                for (String type: values.keySet()) {

                    if (type.equals("CustomerInstanceId")) {
                        this.instanceId = (String) values.get(type);
                    }
                    else if (type.equals("Order")) {
                        this.order = (String) values.get(type);
                    }
                    else if (type.equals("CustomerName")){
                        this.customerName = (String) values.get(type);
                    }
                    else if (type.equals("PushId")){
                        this.pushId = (String) values.get(type);
                    }
                    else if (type.equals("vendorUniqueID")){
                        this.vendorUniqueID = (String) values.get(type);
                    }
                    else if (type.equals("FoodTruckName")){
                        this.foodTruckName = (String) values.get(type);
                    }
                    else if (type.equals("Price")){
                        try {
                            this.price = (Double) values.get(type);
                        }
                        catch (ClassCastException e) {
                            Long l = new Long((Long) values.get(type));
                            this.price = l.doubleValue();
                        }
                    }

                    else if(type.equals("Submitted")) {
                        String choice  = (String) values.get(type);
                        if (choice.equals("true")) {
                            status = true;
                        }
                    }

                }
                Order customerOrder = new Order(instanceId, order, customerName, pushId, vendorUniqueID);
                customerOrder.setStatus(status);
                customerOrder.setFoodTruckName(foodTruckName);
                customerOrder.setPrice(price);
                orders.add(customerOrder);
                updateTotal();


            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String prevChildKey) {
                boolean status = false;
                HashMap<String, Object> values =  (HashMap<String, Object>) dataSnapshot.getValue();
                for (String type: values.keySet()) {

                    if (type.equals("CustomerInstanceId")) {
                        this.instanceId = (String) values.get(type);

                    }
                    else if (type.equals("Order")) {
                        this.order = (String) values.get(type);
                    }
                    else if (type.equals("CustomerName")){
                        this.customerName = (String) values.get(type);
                    }
                    else if (type.equals("PushId")){
                        this.pushId = (String) values.get(type);
                    }
                    else if (type.equals("vendorUniqueID")){
                        this.vendorUniqueID = (String) values.get(type);
                    }
                    else if (type.equals("FoodTruckName")){
                        this.foodTruckName = (String) values.get(type);
                    }
                    else if (type.equals("Price")){
                        try {
                            this.price = (Double) values.get(type);
                        }
                        catch (ClassCastException e) {
                            Long l = new Long((Long) values.get(type));
                            this.price = l.doubleValue();

                        }
                    }

                    else if(type.equals("Submitted")) {
                        String choice  = (String) values.get(type);
                        if (choice.equals("true")) {
                            status = true;
                        }
                    }

                }
                Order customerOrder = new Order(instanceId, order, customerName, pushId, vendorUniqueID);

                //deletes old order
                orders.remove(customerOrder);
                //adds new order at end of queue

                customerOrder.setStatus(status);
                customerOrder.setFoodTruckName(foodTruckName);
                customerOrder.setPrice(price);

                orders.add(customerOrder);
                updateTotal();

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

                HashMap<String, Object> values =  (HashMap<String, Object>) dataSnapshot.getValue();
                for (String type: values.keySet()) {

                    if (type.equals("CustomerInstanceId")) {
                        this.instanceId = (String) values.get(type);

                    }
                    else if (type.equals("Order")) {
                        this.order = (String) values.get(type);
                    }
                    else if (type.equals("CustomerName")){
                        this.customerName = (String) values.get(type);
                    }
                    else if (type.equals("PushId")){
                        this.pushId = (String) values.get(type);
                    }
                    else if (type.equals("vendorUniqueID")){
                        this.vendorUniqueID = (String) values.get(type);
                    }

                }
                Order customerOrder = new Order(instanceId, order, customerName, pushId, vendorUniqueID);
                orders.remove(customerOrder);

                updateTotal();

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String prevChildKey) {
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
        Dont know what to do with this for now */

    }


}
