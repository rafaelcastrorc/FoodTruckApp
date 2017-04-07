package edu.upenn.cis350.foodtruckapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

public class NearMeActivity extends AppCompatActivity {
    protected ArrayList<Order> orders = new ArrayList<>();

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
        setContentView(R.layout.activity_ftnear_me);

        final ListView list = (ListView) findViewById(R.id.favs_list);

        // data to be pulled from Firebase
        String[] user_near_trucks = { "Mc Fries", "Yasmin", "Hemo's", "Magic Carpet", "Yuh Kee's", "Mexicali",
                "Magic Carpet", "Real Lee An's", "Lee An's"};
        Arrays.sort(user_near_trucks);

        list.setAdapter(new ArrayAdapter<String>(this, R.layout.list_item_style,
                user_near_trucks) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View view = super.getView(position, convertView, parent);
                TextView textView = ((TextView) view.findViewById(android.R.id.text1));
                textView.setHeight(200);
                return view;
            }
        });


        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, final View view,
                                    int position, long id) {
                Intent truckIntent = new Intent(NearMeActivity.this, VendorProfileForCustomerActivity.class);
                TextView textView = (TextView) list.getChildAt(position);
                String truckName = textView.getText().toString();
                truckIntent.putExtra("truckName", truckName);
                startActivity(truckIntent);
            }

        });

        list.setDividerHeight(10);

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

    }
    private void updateTotal(){
        TextView total = (TextView)findViewById(R.id.total_shopping_cart);
        total.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(NearMeActivity.this, Cart.class);
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
