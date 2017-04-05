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
import java.util.Scanner;

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
    protected void addOrderToCart(String customerOrder, String foodTruckName, double price) {
        this.foodTruckName = foodTruckName;
        this.price = price;
        this.customerOrder = customerOrder;
        //Check if there is already an order for a given vendor, if not add a new one.
        updateOrder(customerOrder, price, false);
    }


    protected void removeOrderFromCart(String customerOrder, String foodTruckName, double price) {
        //Check if there is no item.
        //Remove order completly if order is empty
        this.foodTruckName = foodTruckName;
        //Make price negative
        this.price = price;
        this.customerOrder = customerOrder;
        //Check if there is already an order for a given vendor, if not add a new one.
        updateOrder(customerOrder, price, true);
    }




    protected void updateOrder(final String newOrder, final double newPrice, final boolean remove) {
        //On customer side, find the current order by using the vendor
        DatabaseReference currUser = databaseRef.child(mAuth.getCurrentUser().getUid()).child("MyOrders").child(vendorUniqueID);

        DatabaseReference vendorRef = databaseRef.child(vendorUniqueID);
        final DatabaseReference vendorOrdersRef = vendorRef.child("Orders");

        currUser.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() == null) {
                   //This will only happen if there is no order asccoiated with the vendor.
                    if(remove) {
                        //If we are removing and there is no order, we just return
                        return;
                    }
                    else {
                        //If we are adding and there is no order, create a new order
                        pushOrderToFirebase(vendorOrdersRef, false);
                    }

                }
                else {
                    //If there already exist an order, do the following

                    HashMap<String, Object> currOrder = (HashMap<String, Object>) dataSnapshot.getValue();
                    HashMap<String, Integer> orderToQuantity = new   HashMap<String, Integer>();

                    //Gets the current order
                    String prevOrder = (String) currOrder.get("Order");

                    //Gets all items of the order and adds them to the map

                    //Goes through each line of the order
                    Scanner scanner = new Scanner(prevOrder);
                    while (scanner.hasNextLine()) {
                        StringBuilder order = new StringBuilder();
                        StringBuilder quantity = new StringBuilder();

                        String line = scanner.nextLine();
                        boolean isQuantity = false;
                        for (char c: line.toCharArray()) {
                            //If there is an space, it may be a word or it can be a quantity
                            if (c == '(') {
                                isQuantity = true;
                                continue;
                            }
                            else if (isQuantity) {
                                try  {
                                    if (Character.isDigit(c)) {
                                        quantity.append(c);
                                    }
                                }
                                catch (NumberFormatException e) {
                                    break;
                                }
                            }
                            else {
                                order.append(c);
                            }

                        }
                        orderToQuantity.put(order.toString(), Integer.parseInt(quantity.toString()));
                    }


                    scanner.close();

                    //If we are adding an element to the cart
                    if (!remove) {
                        if (orderToQuantity.get(newOrder) == null) {
                            orderToQuantity.put(newOrder, 1);
                        }
                        else {
                            Integer currQuantity = orderToQuantity.get(newOrder);
                            orderToQuantity.put(newOrder, currQuantity + 1);
                        }

                    }

                    //If remove is true
                    else {
                        //If cart does not contain the order, return
                        if (orderToQuantity.get(newOrder) == null) {
                            return;
                        }
                        //If quantity for current item is 0, do not remove more.
                        else if (orderToQuantity.get(newOrder) == 0) {
                            return;
                        }
                        //If quantity is greater than 0
                        else {
                            Integer currQuantity = orderToQuantity.get(newOrder);
                            orderToQuantity.put(newOrder, currQuantity - 1);
                           //If the quantity is now 0, remove it
                            if (currQuantity-1 ==0) {
                                orderToQuantity.remove(newOrder);
                            }
                        }
                    }

                    //Get the prev price
                    Double prevPrice = 0.0;
                    try {
                        prevPrice = (Double) currOrder.get("Price");
                    }
                    catch (ClassCastException e) {
                        Long l = new Long((Long) currOrder.get("Price"));
                        prevPrice= l.doubleValue();

                    }
                    if (!remove) {
                        currOrder.put("Price", prevPrice + newPrice);
                    }
                    else {
                        currOrder.put("Price", prevPrice - newPrice);

                    }


                    //Construct new order string
                    StringBuilder sb = new StringBuilder();
                    Log.d("fuck orderToQuantity", orderToQuantity.keySet().toString());
                    for(String orderToPut: orderToQuantity.keySet()) {
                        sb.append(orderToPut);
                        sb.append("("+orderToQuantity.get(orderToPut)+")");
                        sb.append("\n");
                    }
                    String newOrder = sb.toString();
                    currOrder.put("Order", newOrder);


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
        final String uID = mAuth.getCurrentUser().getUid();
        DatabaseReference currUser = databaseRef.child(uID).child("Name");

        currUser.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String nameOfCustomer = dataSnapshot.getValue().toString();

                //Get push key
                String pushId = vendorOrdersRef.push().getKey();

                Map orderInfo = new HashMap<>();
                orderInfo.put("CustomerInstanceId", id);
                orderInfo.put("Order", customerOrder + "(1)" + "\n");
                orderInfo.put("CustomerName", nameOfCustomer);
                orderInfo.put("PushId", pushId);
                orderInfo.put("customerUniqueID", uID);
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





}
