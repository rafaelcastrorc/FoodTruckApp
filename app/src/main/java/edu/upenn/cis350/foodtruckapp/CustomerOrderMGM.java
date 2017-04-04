package edu.upenn.cis350.foodtruckapp;

import android.util.Log;
import android.util.StringBuilderPrinter;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by rafaelcastro on 3/17/17.
 */

public class CustomerOrderMGM {
    private FirebaseAuth mAuth;
    private DatabaseReference databaseRef;
    private String id;
    private String vendorUniqueID;
    private String customerOrder;
    private String foodTruckName;
    private double price;

    protected CustomerOrderMGM() {
        databaseRef = FirebaseDatabase.getInstance().getReference("Users");
        mAuth = FirebaseAuth.getInstance();
        id = FirebaseInstanceId.getInstance().getId();
    }

    protected void setVendorUniqueID(String newId) {
        vendorUniqueID = newId;
    }

    //Call from vendor profile on customer side
    protected void sendOrderToCart(String customerOrder, String foodTruckName, double price) {
        this.foodTruckName = foodTruckName;
        this.price = price;
        this.customerOrder = customerOrder;
        //Check if there is already an order for a given vendor, if not add a new one.
        updateOrder(customerOrder, price);
    }




    protected void updateOrder(final String newOrder, final double newPrice) {
        //On customer side, find the current order by using the vendor
        DatabaseReference currUser = databaseRef.child(mAuth.getCurrentUser().getUid()).child("MyOrders").child(vendorUniqueID);

        DatabaseReference vendorRef = databaseRef.child(vendorUniqueID);
        final DatabaseReference vendorOrdersRef = vendorRef.child("Orders");

        currUser.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() == null) {
                   //This will only happen if there is no order asccoiated with the vendor.
                    //So we create new order and send order to cart
                    pushOrderToFirebase(vendorOrdersRef, false);

                }
                else {
                    //If there already exist an order, do the following

                    HashMap<String, Object> currOrder = (HashMap<String, Object>) dataSnapshot.getValue();

                    //Get the prev order and append the new order.
                    String prevOrder = (String) currOrder.get("Order");
                    StringBuilder sb = new StringBuilder();
                    sb.append(prevOrder);
                    sb.append("\n");
                    sb.append(newOrder);

                    String newOrder = sb.toString();

                    //Get the prev price
                    Double prevPrice = 0.0;
                    try {
                        prevPrice = (Double) currOrder.get("Price");
                    }
                    catch (ClassCastException e) {
                        Long l = new Long((Long) currOrder.get("Price"));
                        prevPrice= l.doubleValue();

                    }

                    //Mofify the order
                    currOrder.put("Order", newOrder);
                    currOrder.put("Price", newPrice + prevPrice);
                    String submitted = (String) currOrder.get("Submitted");

                    String pushId = (String) currOrder.get("PushId");


                    if (submitted.equals("true")) {
                        //Update the order on the vendor side
                        vendorOrdersRef.child(pushId).setValue(currOrder);
                    }

                    //For customer side, update the order
                    DatabaseReference customerRef = databaseRef.child(mAuth.getCurrentUser().getUid());
                    customerRef.child("MyOrders").child(vendorUniqueID).setValue(currOrder);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


    }

    protected void cancelOrder(String pushID, boolean submitted) {
        DatabaseReference currUserOrder = databaseRef.child(mAuth.getCurrentUser().getUid()).child("MyOrders").child(vendorUniqueID);
        currUserOrder.removeValue();
        //If order has already been submitted, remove from vendor queue
        if (submitted) {
            databaseRef.child(vendorUniqueID).child("Orders").child(pushID).removeValue();
        }
    }


    private void pushOrderToFirebase(final DatabaseReference vendorOrdersRef, final boolean submitted) {
        String uID = mAuth.getCurrentUser().getUid();
        DatabaseReference currUser = databaseRef.child(uID).child("Name");

        currUser.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String nameOfCustomer = dataSnapshot.getValue().toString();

                //Get push key
                String pushId = vendorOrdersRef.push().getKey();

                Map orderInfo = new HashMap<>();
                orderInfo.put("CustomerInstanceId", id);
                orderInfo.put("Order", customerOrder);
                orderInfo.put("CustomerName", nameOfCustomer);
                orderInfo.put("PushId", pushId);
                orderInfo.put("vendorUniqueID", vendorUniqueID);
                orderInfo.put("FoodTruckName", foodTruckName);
                orderInfo.put("Price", price);


                String boolString = "false";
                if (submitted) {
                    boolString = "true";
                }
                orderInfo.put("Submitted", boolString);


                if (submitted) {
                    //Adds it to vendor side only if order has been submitted
                    vendorOrdersRef.child(pushId).setValue(orderInfo);
                }

                //For customer side
                DatabaseReference customerRef = databaseRef.child(mAuth.getCurrentUser().getUid());
                //Each user will have a myOrders children, and each order will map to an specific vendor
                customerRef.child("MyOrders").child(vendorUniqueID).setValue(orderInfo);


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }



    //Call from the cart
    protected void sendOrderToVendor(Order order) {
        this.customerOrder = order.getCustomerOrder();
        this.foodTruckName = order.getFoodTruckName();
        this.price = order.getPrice();


        //For vendor side
        DatabaseReference vendorRef = databaseRef.child(vendorUniqueID);
        DatabaseReference vendorOrdersRef = vendorRef.child("Orders");
        subscribe();
        pushOrderToFirebase(vendorOrdersRef, true);
    }


    private void subscribe() {
        //Subscribe user to topic so that he can get the notification
        FirebaseMessaging.getInstance().subscribeToTopic("user_" + id);
    }





  /*  protected double getTotal() {
        final ArrayList<Order> orders = new ArrayList<>();
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
                        this.price = (Double) values.get(type);
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
                updateTotal(orders);


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
                        this.price = (Double) values.get(type);
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
                updateTotal(orders);

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
                updateTotal(orders);

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String prevChildKey) {
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });

        return updateTotal(orders);
    }

    private double updateTotal(ArrayList<Order> orders) {
        double result = 0.0;
        for(Order order :orders)
        {
            result = result + order.getPrice();
        }
        return result;
    }



*/

}
