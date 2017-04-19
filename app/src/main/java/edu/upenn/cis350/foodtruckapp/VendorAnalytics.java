package edu.upenn.cis350.foodtruckapp;

import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;

import org.joda.time.DateTime;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;

/**
 * Created by rafaelcastro on 4/19/17.
 * Handles the functionality of the VendorAnalyticsActivity
 */

class VendorAnalytics {
    private FirebaseAuth mAuth;
    private DatabaseReference databaseRef;
    private String id;
    private HashMap<Integer, Order> orderHistoryMap;
    private LinkedList<Order> orderList;

    /**
     * No args constructor
     */
    VendorAnalytics() {
        databaseRef = FirebaseDatabase.getInstance().getReference("Users");
        mAuth = FirebaseAuth.getInstance();
        id = FirebaseInstanceId.getInstance().getId();
    }

    /**
     * Gets the sales for a given time period
     *
     * @param date - Time period to get sales from
     * @return void
     */
    protected double getSales(DateTime date) {
        return 0.0;
    }


    /**
     * Pushes an order to the OrderHistory child of the vendor in firebase
     *
     * @param order - order that needs to be added
     * @return void
     */
    protected void pushOrderToDataBase(Order order) {
        DatabaseReference orderHistory = databaseRef.child(mAuth.getCurrentUser().getUid()).child("OrderHistory");
        orderHistory.push().setValue(order);
    }


    /**
     * Gets all the orders that this vendor has
     *
     * @return void
     */
    private void getOrderHistory() {
        orderList = new LinkedList<>();
        DatabaseReference orderHistory = databaseRef.child(mAuth.getCurrentUser().getUid()).child("OrderHistory");
        orderHistory.addChildEventListener(new ChildEventListener() {
            public String vendorUniqueID = "";
            String instanceId = "";
            String order = "";
            String customerName = "";
            String pushId = "";
            String foodTruckName = "";
            String time = "";

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
                    } else if (type.equals("Time")) {
                        this.time = (String) values.get(type);
                    } else if (type.equals("Price")) {
                        try {
                            this.price = (Double) values.get(type);
                        } catch (ClassCastException e) {
                            Long l = new Long((Long) values.get(type));
                            this.price = l.doubleValue();
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
                customerOrder.setTime(time);
                customerOrder.setFoodTruckName(foodTruckName);
                customerOrder.setPrice(price);
                orderList.add(customerOrder);
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
                    } else if (type.equals("Time")) {
                        this.time = (String) values.get(type);
                    } else if (type.equals("FoodTruckName")) {
                        this.foodTruckName = (String) values.get(type);
                    } else if (type.equals("Price")) {
                        try {
                            this.price = (Double) values.get(type);
                        } catch (ClassCastException e) {
                            Long l = new Long((Long) values.get(type));
                            this.price = l.doubleValue();

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
                orderList.remove(customerOrder);
                //adds new order at end of queue

                customerOrder.setStatus(status);
                customerOrder.setFoodTruckName(foodTruckName);
                customerOrder.setPrice(price);
                customerOrder.setTime(time);

                orderList.add(customerOrder);

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
                orderList.remove(customerOrder);
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String prevChildKey) {
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });

        int i = 0;
        for (Order order : orderList) {
            orderHistoryMap.put(i, order);
        }

    }
}
