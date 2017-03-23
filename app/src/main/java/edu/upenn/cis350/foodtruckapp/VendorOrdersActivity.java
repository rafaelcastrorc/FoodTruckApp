package edu.upenn.cis350.foodtruckapp;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;

import java.util.ArrayList;
import java.util.HashMap;


public class VendorOrdersActivity extends AppCompatActivity {

    private FirebaseDatabase database;
    private FirebaseAuth mAuth;
    private DatabaseReference databaseRef;
    DatabaseReference currentOrders;

    private ListView orderList;
    private ArrayList<Order> orders = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.food_truck_order_mgm_layout); //Todo: Create a layout for this activity
        databaseRef = FirebaseDatabase.getInstance().getReference("Users");
        mAuth = FirebaseAuth.getInstance();
        String currVendor = mAuth.getCurrentUser().getUid();
        currentOrders = databaseRef.child(currVendor).child("Orders");
        orderList = (ListView) findViewById(R.id.order_list);
        final ArrayAdapter<Order> arrayAdapter = new ArrayAdapter<Order>(this, android.R.layout.simple_list_item_1, orders);
        orderList.setAdapter(arrayAdapter);

        //Orders are represented in the database by a push id, and each push id has a instanceID and a message
        // as children

        currentOrders.addChildEventListener(new ChildEventListener() {

            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String prevChildKey) {
                HashMap<String, Object> values =  (HashMap<String, Object>) dataSnapshot.getValue();
                for (String type: values.keySet()) {
                    String instanceId = "";
                    String order = "";
                    String costumerName = "";
                    if (type.equals("CostumerInstanceId")) {
                        instanceId = (String) values.get(type);

                    }
                    else if (type.equals("Order")) {
                        order = (String) values.get(type);
                    }
                    else if (type.equals("CostumerName")){
                        costumerName = (String) values.get(type);
                        Order costumerOrder = new Order(instanceId, order, costumerName);
                        orders.add(costumerOrder);
                    }

                }
                arrayAdapter.notifyDataSetChanged();




            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String prevChildKey) {
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                HashMap<String, Object> values =  (HashMap<String, Object>) dataSnapshot.getValue();
                for (String type: values.keySet()) {
                    String instanceId = "";
                    String order = "";
                    String costumerName = "";
                    if (type.equals("CostumerInstanceId")) {
                        instanceId = (String) values.get(type);

                    } else if (type.equals("Order")) {
                        order = (String) values.get(type);

                    }
                    else if (type.equals("CostumerName")) {
                        costumerName = (String) values.get(type);
                        Order costumerOrder = new Order(instanceId, order, costumerName);
                        orders.remove(costumerOrder);
                    }

                }
                    arrayAdapter.notifyDataSetChanged();

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String prevChildKey) {
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });

    }



    //Todo: Handle user cancels order, vendor rej order, vendor accepts order, vendor finish order

    //Button associated to a given user
    public void order_done_onClick(View v) {

        String idOfCostumer = "the id of the user who sent the order";
        idOfCostumer = FirebaseInstanceId.getInstance().getId(); // for testing purposes
        FoodTruckOrderMGM notifications = new FoodTruckOrderMGM(idOfCostumer);
        notifications.orderDone();
    }

    public class Order {
        protected  String costumerInstanceID;
        protected  String order;
        protected String costumerName;

        Order(String costumerInstanceID, String order, String name ) {
            this.costumerInstanceID = costumerInstanceID;
            this.order = order;
            this.costumerName = name;
        }

        String getCostumerInstanceID() {
            return costumerInstanceID;
        }
        String getCostumerOrder() {
            return order;
        }
        String getCostumername() {return costumerName;}

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            Order order1 = (Order) o;

            if (costumerInstanceID != null ? !costumerInstanceID.equals(order1.costumerInstanceID) : order1.costumerInstanceID != null)
                return false;
            return order != null ? order.equals(order1.order) : order1.order == null;

        }

        @Override
        public int hashCode() {
            int result = costumerInstanceID != null ? costumerInstanceID.hashCode() : 0;
            result = 31 * result + (order != null ? order.hashCode() : 0);
            return result;
        }
    }

}