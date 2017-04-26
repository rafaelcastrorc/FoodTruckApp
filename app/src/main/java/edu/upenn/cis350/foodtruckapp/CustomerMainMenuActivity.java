package edu.upenn.cis350.foodtruckapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.plus.People;
import com.google.android.gms.plus.Plus;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;

public class CustomerMainMenuActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener,
        ResultCallback<People.LoadPeopleResult>, View.OnClickListener {
    private FirebaseAuth mAuth;
    ArrayList<Order> orders = new ArrayList<>();
    private GoogleApiClient googleApiClient;

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
        DatabaseReference databaseRef = FirebaseDatabase.getInstance().getReference("Users");

        googleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this).addApi(Plus.API)
                .addScope(Plus.SCOPE_PLUS_LOGIN).build();


        // add click listener to Food Trucks near me button
        Button nearMeButton = (Button) findViewById(R.id.button_near_me);
        nearMeButton.setOnClickListener(new AdapterView.OnClickListener() {

            public void onClick(View view) {
                Intent i = new Intent(CustomerMainMenuActivity.this, NearMeActivity.class);
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

        // add click listener to social feed button
        Button socialFeedButton = (Button) findViewById(R.id.button_social_feed);
        socialFeedButton.setOnClickListener(new AdapterView.OnClickListener() {

            public void onClick(View view) {
                Intent i = new Intent(CustomerMainMenuActivity.this, SocialFeedActivity.class);
                startActivity(i);
            }
        });

        DatabaseReference myOrdersRef = databaseRef.child(mAuth.getCurrentUser().getUid()).child("MyOrders");
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


    }

    public void sign_out_onClick(View v) {
        mAuth.signOut();

        Intent i = new Intent(CustomerMainMenuActivity.this, LoginActivity.class);
        setResult(1, i);
        i.putExtra("google", "1");
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

    @Override
    public void onClick(View v) {

    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        Plus.PeopleApi.loadVisible(googleApiClient, null).setResultCallback(
                this);
    }

    @Override
    public void onConnectionSuspended(int i) {
        googleApiClient.connect();

    }

    protected void onStart() {
        super.onStart();
        googleApiClient.connect();
    }

    protected void onStop() {
        super.onStop();
        if (googleApiClient.isConnected()) {
            googleApiClient.disconnect();
        }
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onResult(@NonNull People.LoadPeopleResult loadPeopleResult) {

    }
}
