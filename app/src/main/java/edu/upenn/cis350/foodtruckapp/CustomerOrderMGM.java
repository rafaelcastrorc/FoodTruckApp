package edu.upenn.cis350.foodtruckapp;

import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.FirebaseMessaging;

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



    //When called, get the vendor uniqueId
    protected CustomerOrderMGM(String vendorUniqueID) {
        this.vendorUniqueID = vendorUniqueID;
        databaseRef = FirebaseDatabase.getInstance().getReference("Users");
        mAuth = FirebaseAuth.getInstance();
        id = FirebaseInstanceId.getInstance().getId();
    }

    //Call from vendor profile on customer side
    //customerOrder needs to be separated by /n
    protected void sendOrderToCart(String customerOrder, String foodTruckName, double price) {
        this.foodTruckName = foodTruckName;
        this.price = price;
        this.customerOrder = customerOrder;
        DatabaseReference vendorRef = databaseRef.child(vendorUniqueID);
        DatabaseReference vendorOrdersRef = vendorRef.child("Orders");
        pushOrderToFirebase(vendorOrdersRef, false);
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


    protected void updateOrder(final String newOrder, final double newPrice) {
        //On customer side, find the current order by using the vendor
        DatabaseReference currUser = databaseRef.child(mAuth.getCurrentUser().getUid()).child("MyOrders").child(vendorUniqueID);

        DatabaseReference vendorRef = databaseRef.child(vendorUniqueID);
        final DatabaseReference vendorOrdersRef = vendorRef.child("Orders");

        currUser.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                HashMap<String, Object> currOrder = (HashMap<String, Object>) dataSnapshot.getValue();
                //Mofify the order
                currOrder.put("Order", newOrder);
                currOrder.put("Price", newPrice);
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


    protected void setVendorUniqueID(String newId) {
        vendorUniqueID = newId;
    }


    private void subscribe () {
        //Subscribe user to topic so that he can get the notification
        FirebaseMessaging.getInstance().subscribeToTopic("user_"+id);
    }





    private void pushOrderToFirebase(final DatabaseReference vendorOrdersRef, final boolean submitted) {
        String uID = mAuth.getCurrentUser().getUid();
        DatabaseReference currUser = databaseRef.child(uID).child("Name");

        currUser.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String nameOfCustomer= dataSnapshot.getValue().toString();

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





}
