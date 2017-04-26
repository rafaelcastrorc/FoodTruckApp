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
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;

public class CustomerMainMenuActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private DatabaseReference databaseRef;
    private String currentUserID;
    ArrayList<Order> orders = new ArrayList<>();


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

                Intent i = new Intent(CustomerMainMenuActivity.this, Cart.class);
                startActivity(i);
                return true;
            case R.id.home_button:
                Intent j = new Intent(CustomerMainMenuActivity.this, CustomerMainMenuActivity.class);
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
        setContentView(R.layout.activity_customer_main_menu);
        mAuth = FirebaseAuth.getInstance();
        databaseRef = FirebaseDatabase.getInstance().getReference("Users");
        currentUserID = mAuth.getCurrentUser().getUid();



        // add click listener to Food Trucks near me button
        Button nearMeButton = (Button) findViewById(R.id.button_near_me);
        nearMeButton.setOnClickListener(new AdapterView.OnClickListener() {

            public void onClick(View view) {
                Intent i = new Intent(CustomerMainMenuActivity.this, NearMeActivity.class);
                startActivity(i);
            }
        });

        // add click listener to search food button
        Button searchFoodButton = (Button) findViewById(R.id.button_search_food);
        searchFoodButton.setOnClickListener(new AdapterView.OnClickListener() {

            public void onClick(View view) {
                Intent i = new Intent(CustomerMainMenuActivity.this, SearchFoodActivity.class);
                startActivity(i);
            }
        });

        // add click listener to favorites button
        Button favsButton = (Button) findViewById(R.id.button_favs);
        favsButton.setOnClickListener(new AdapterView.OnClickListener() {

            public void onClick(View view) {
                Intent i = new Intent(CustomerMainMenuActivity.this, FavoritesActivity.class);
                startActivity(i);
            }
        });

        // add click listener to top food trucks button
        Button topTrucksButton = (Button) findViewById(R.id.button_top_trucks);
        topTrucksButton.setOnClickListener(new AdapterView.OnClickListener() {

            public void onClick(View view) {
                Intent i = new Intent(CustomerMainMenuActivity.this, TopFoodTrucksActivity.class);
                startActivity(i);
            }
        });

        DatabaseReference myOrdersRef = databaseRef.child(currentUserID).child("MyOrders");

        myOrdersRef.addChildEventListener(new ChildEventListener() {

            String vendorUniqueID = "";
            String instanceId = "";
            String order = "";
            String customerName = "";
            String pushId = "";
            String foodTruckName = "";
            double price = 0.0;

            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String prevChildKey) {
                boolean status = false;

                HashMap<String, Object> values = (HashMap<String, Object>) dataSnapshot.getValue();
                for (String type : values.keySet()) {

                    if (type.equals("CustomerInstanceId")) {
                        this.instanceId = (String) values.get(type);
                    } else if (type.equals("Order")) {
                        this.order = (String) values.get(type);
                    } else if (type.equals("CustomerName")) {
                        this.customerName = (String) values.get(type);
                    } else if (type.equals("PushId")) {
                        this.pushId = (String) values.get(type);
                    } else if (type.equals("vendorUniqueID")) {
                        this.vendorUniqueID = (String) values.get(type);
                    } else if (type.equals("FoodTruckName")) {
                        this.foodTruckName = (String) values.get(type);
                    } else if (type.equals("Price")) {
                        try {
                            this.price = (Double) values.get(type);
                        }
                        catch (ClassCastException e) {
                            Long l = new Long((Long) values.get(type));
                            this.price= l.doubleValue();

                        }

                    } else if (type.equals("Submitted")) {
                        String choice = (String) values.get(type);
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
                HashMap<String, Object> values = (HashMap<String, Object>) dataSnapshot.getValue();
                for (String type : values.keySet()) {

                    if (type.equals("CustomerInstanceId")) {
                        this.instanceId = (String) values.get(type);

                    } else if (type.equals("Order")) {
                        this.order = (String) values.get(type);
                    } else if (type.equals("CustomerName")) {
                        this.customerName = (String) values.get(type);
                    } else if (type.equals("PushId")) {
                        this.pushId = (String) values.get(type);
                    } else if (type.equals("vendorUniqueID")) {
                        this.vendorUniqueID = (String) values.get(type);
                    } else if (type.equals("FoodTruckName")) {
                        this.foodTruckName = (String) values.get(type);
                    } else if (type.equals("Price")) {
                        try {
                            this.price = (Double) values.get(type);
                        }
                        catch (ClassCastException e) {
                            Long l = new Long((Long) values.get(type));
                            this.price= l.doubleValue();

                        }
                    } else if (type.equals("Submitted")) {
                        String choice = (String) values.get(type);
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

                HashMap<String, Object> values = (HashMap<String, Object>) dataSnapshot.getValue();
                for (String type : values.keySet()) {

                    if (type.equals("CustomerInstanceId")) {
                        this.instanceId = (String) values.get(type);

                    } else if (type.equals("Order")) {
                        this.order = (String) values.get(type);
                    } else if (type.equals("CustomerName")) {
                        this.customerName = (String) values.get(type);
                    } else if (type.equals("PushId")) {
                        this.pushId = (String) values.get(type);
                    } else if (type.equals("vendorUniqueID")) {
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

        // if the customer is still banned then go back to log in,
        // else reset No Show counters and ban time
        databaseRef.child(currentUserID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChild("Ban Time")) {
                    String timeInSeconds = dataSnapshot.child("Ban Time").getValue().toString();
                    int banTime = Integer.parseInt(timeInSeconds);
                    int currentTime = (int)Math.round(System.currentTimeMillis() / 1000.0);

                    if (currentTime - banTime < 864000) {
                        Toast.makeText(CustomerMainMenuActivity.this,
                                "You have been banned for not picking up your orders, come back tomorrow!",
                                Toast.LENGTH_LONG).show();

                        mAuth.signOut();
                        Intent i = new Intent(CustomerMainMenuActivity.this, LoginActivity.class);
                        startActivity(i);
                        finish();
                    } else {
                        resetBanTime(currentUserID);
                    }

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    private void resetBanTime(String userId) {
        databaseRef.child(userId).child("Ban Time").removeValue();
        databaseRef.child(userId).child("No Show").setValue(0);
    }


    public void shareOnClick(View v) {
        Intent i = new Intent(CustomerMainMenuActivity.this, ShareEmailActivity.class);
        startActivity(i);
    }

    public void signOutOnClick(View v) {
        mAuth.signOut();
        Intent i = new Intent(CustomerMainMenuActivity.this, LoginActivity.class);
        startActivity(i);
        finish();
    }


    private void updateTotal(){
        TextView total = (TextView)findViewById(R.id.total_shopping_cart);
        total.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(CustomerMainMenuActivity.this, Cart.class);
                startActivity(i);
            }
        });
        double result = 0.0;
        for (Order order: orders) {
            result = result + order.getPrice();
        }
        NumberFormat formatter = new DecimalFormat("#0.00");

        total.setText("$"+ formatter.format(result));
    }

}
