package edu.upenn.cis350.foodtruckapp;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.FirebaseMessaging;

import org.joda.time.DateTime;
import org.joda.time.Period;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.PeriodFormatter;
import org.joda.time.format.PeriodFormatterBuilder;

import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.TreeMap;

/**
 * Created by rafaelcastro on 3/17/17.
 * Handles how the customer orders are sent from the user to the vendor.
 */

public class CustomerOrderMGM {
    private FirebaseAuth mAuth;
    private DatabaseReference databaseRef;
    private String id;
    private String vendorUniqueID;
    private String customerOrder;
    private String foodTruckName;
    private double price;
    private Context context;


    protected CustomerOrderMGM() {
        databaseRef = FirebaseDatabase.getInstance().getReference("Users");
        mAuth = FirebaseAuth.getInstance();
        id = FirebaseInstanceId.getInstance().getId();
    }

    protected void setVendorUniqueID(String newId) {
        vendorUniqueID = newId;
    }

    protected String getUniqueID() { return mAuth.getCurrentUser().getUid(); }

    /**
     * Adds an order to the cart
     * @param customerOrder - String representing the order of the customer.
     * @param foodTruckName - Name of the food truck the user ordered from
     * @param price - Price of the item the user ordered
     * @return void
     */
    protected void addOrderToCart(String customerOrder, String foodTruckName, double price) {
        this.foodTruckName = foodTruckName;
        this.price = price;
        this.customerOrder = customerOrder;
        //Check if there is already an order for a given vendor, if not add a new one.
        updateOrder(customerOrder, price, false);
    }

    /**
     * Removes an order from the cart
     * @param customerOrder - String representing the order of the customer.
     * @param foodTruckName - Name of the food truck the user ordered from
     * @param price - Price of the item the user ordered
     * @return void
     */
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


    /**
     * Updates a current order in the cart
     * There is a time limit to update the order
     * @param newOrder - The new item that you are adding to the order.
     * @param newPrice - The price of the item you are adding to the cart
     * @param remove - True if you are removing the current item, false otherwise.
     * @return void
     */
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
                    Log.d("fuck order:", newOrder);
                    TreeMap<String, Integer> orderToQuantity;

                    //Gets the current order
                    String prevOrder = (String) currOrder.get("Order");

                    //Parses the order string
                    orderToQuantity = ordersParser(prevOrder);


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
                        //If the order for the current  vendor is empty, i remove it
                        if (orderToQuantity.keySet().size() == 0) {
                            DatabaseReference currUserOrder = databaseRef.child(mAuth.getCurrentUser().getUid()).child("MyOrders").child(vendorUniqueID);
                            currUserOrder.removeValue();
                            Log.d("fuck", "here");
                            return;
                        }
                        //If cart does not contain the order, return
                        else if (orderToQuantity.get(newOrder) == null) {
                            return;
                        }
                        //If quantity for current item is 0, do not remove more, but remove itemo
                        else if (orderToQuantity.get(newOrder) == 0) {
                            orderToQuantity.remove(newOrder);
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
                    for(String orderToPut: orderToQuantity.keySet()) {
                        sb.append("["+orderToQuantity.get(orderToPut)+"] ");
                        sb.append(orderToPut);
                        sb.append(".\n");
                    }
                    String newOrder = sb.toString();
                    currOrder.put("Order", newOrder);


                    String submitted = (String) currOrder.get("Submitted");

                    String pushId = (String) currOrder.get("PushId");


                    boolean isValidTime = true;

                    if (submitted.equals("true")) {
                        //Get time of order. Can only change to false if the order has already being submitted
                        DateTimeFormatter formatter = DateTimeFormat.forPattern("dd/MM/yyyy HH:mm:ss");
                        DateTime prevDT = formatter.parseDateTime((String)currOrder.get("Time"));
                        //Time limit of 1 minut
                        isValidTime = time(prevDT, 1);

                        if (isValidTime) {
                            //Update the order on the vendor side as long as time is valid
                            vendorOrdersRef.child(pushId).setValue(currOrder);
                            //get the time of the order
                        }
                    }

                    //For customer side, update the order
                    DatabaseReference customerRef = databaseRef.child(mAuth.getCurrentUser().getUid());
                    if (isValidTime) {
                        customerRef.child("MyOrders").child(vendorUniqueID).setValue(currOrder);
                    }
                    else {

                        Toast toast = Toast.makeText(context, "Your have exceeded the time limit to modify your order", Toast.LENGTH_SHORT);
                        toast.show();
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


    }

    /**
     * Cancels an order, notifies the vendor
     * There is a time limit to cancel the order
     * @param submitted - Has the order been submitted to the vendor or not.
     * @param pushID - pushId of the user
     * @return void
     */
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
                String orderToPut = customerOrder;
                if (!submitted) {
                    orderToPut = "[1] "+customerOrder+".\n";
                }
                orderInfo.put("Order",  orderToPut);
                orderInfo.put("CustomerName", nameOfCustomer);
                orderInfo.put("PushId", pushId);
                orderInfo.put("customerUniqueID", uID);
                orderInfo.put("vendorUniqueID", vendorUniqueID);
                orderInfo.put("FoodTruckName", foodTruckName);
                orderInfo.put("Price", price);
                DateTime now = new DateTime();
                DateTimeFormatter fmt = DateTimeFormat.forPattern("dd/MM/yyyy HH:mm:ss");
                String dtStr = fmt.print(now);
                orderInfo.put("Time", dtStr);

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


    /**
     * Sends the order to the vendor, adds it to the database
     * @param order - String representing the order of the customer.
     * @return void
     */
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

    /**
     * Subscribes a user to receieve notifications on his device
     */
    private void subscribe() {
        //Subscribe user to topic so that he can get the notification
        FirebaseMessaging.getInstance().subscribeToTopic("user_" + id);
    }

    protected TreeMap<String, Integer> ordersParser(String prevOrder) {
        if (prevOrder == null || prevOrder.isEmpty()) {
            return new TreeMap<String, Integer>();
        }
        TreeMap<String, Integer> orderToQuantity = new TreeMap<>();
        //Goes through each line of the order
        Scanner scanner = new Scanner(prevOrder);
        while (scanner.hasNextLine()) {
            StringBuilder order = new StringBuilder();
            StringBuilder quantity = new StringBuilder();

            String line = scanner.nextLine();
            Log.d("fuck", "ordersParser: "+line  );

            boolean isQuantity = false;
            //Format fo the order [n] Name Of food.\n
            for (char c: line.toCharArray()) {
                //If there is an space, it may be a word or it can be a quantity

                if (c == '[') {

                    isQuantity = true;
                    continue;
                }
                else if (isQuantity) {
                    if (c == ' ') {
                        isQuantity = false;
                        continue;
                    }
                    if (c== ']') {
                        continue;
                    }
                    quantity.append(c);
                }


                else {
                    if (c == '.') {
                        break;
                    }
                    order.append(c);
                }
            }
            orderToQuantity.put(order.toString(), Integer.parseInt(quantity.toString()));
        }
        scanner.close();
        return orderToQuantity;
    }

    /**
     * Adds a time limite to perform a certain operation
     * @param date - date the order was sent
     * @param timeLimit - Amount of minutes user has before he is unable to perform a function
     * @return boolean
     */

    protected boolean time(DateTime date, int timeLimit) {
        //Allow to change order up to 5 minuts
        DateTime now = new DateTime();
        Period period = new Period(date, now);
        if (period.getYears() == 0 && period.getMonths() == 0 && period.getWeeks() ==0 && period.getDays() ==0) {
            if (period.getMinutes() < timeLimit) {
                return true;
            }
        }
        return false;

    }
    protected void setContext(Context context) {
        this.context = context;
    }







}
