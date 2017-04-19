package edu.upenn.cis350.foodtruckapp;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.TwoLineListItem;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by rafaelcastro on 4/02/17.
 * Handles all the functionality of the customer cart
 */

public class Cart extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private DatabaseReference databaseRef;
    DatabaseReference myOrdersRef;

    protected ListView orderList;
    protected ArrayList<Order> orders = new ArrayList<>();
    private boolean isOrderSelected = false;
    private TwoLineListItem previousChildSelected = null;
    private Order selectedOrder;

    /**
     * Inflates the menu with the shopping cart icon
     */
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

                Intent i = new Intent(Cart.this, Cart.class);
                startActivity(i);
                return true;
            case R.id.home_button:
                Intent j = new Intent(Cart.this, CustomerMainMenuActivity.class);
                startActivity(j);
                return true;

            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);
        }
    }



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);


        databaseRef = FirebaseDatabase.getInstance().getReference("Users");
        mAuth = FirebaseAuth.getInstance();
        myOrdersRef = databaseRef.child(mAuth.getCurrentUser().getUid()).child("MyOrders");

        orderList = (ListView) findViewById(R.id.orders_list_shopping_cart);
        final Cart.MyAdapter arrayAdapter = new Cart.MyAdapter(this, orders);
        orderList.setAdapter(arrayAdapter);

        // When an order is clicked by the customer make it bold & italic
        ListView listView = (ListView) findViewById(R.id.orders_list_shopping_cart);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, final View view,
                                    int position, long id) {
                if (previousChildSelected != null) {                // set previously selected item back to normal
                    previousChildSelected.getText1().setTypeface(null, Typeface.NORMAL);
                    previousChildSelected.getText2().setTypeface(null, Typeface.NORMAL);
                }
                isOrderSelected = true;
                // get selected order
                final TwoLineListItem selectedChild = (TwoLineListItem) parent.getChildAt(position);
                previousChildSelected = selectedChild;

                try {
                    selectedChild.getText1().setTypeface(null, Typeface.BOLD_ITALIC);
                    selectedChild.getText2().setTypeface(null, Typeface.BOLD_ITALIC);
                }
                catch (NullPointerException e) {

                }
                selectedOrder = (Order) parent.getItemAtPosition(position);
            }

        });

        myOrdersRef.addChildEventListener(new ChildEventListener() {
            String time;
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
                    else if (type.equals("Time")){
                        this.time = (String) values.get(type);
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
                //Add as long as is not empty
                if (!order.equals("")) {

                    Order customerOrder = new Order(instanceId, order, customerName, pushId, vendorUniqueID);
                    customerOrder.setStatus(status);
                    customerOrder.setFoodTruckName(foodTruckName);
                    customerOrder.setPrice(price);
                    customerOrder.setTime(time);
                    orders.add(customerOrder);

                    arrayAdapter.notifyDataSetChanged();
                    updateTotal();
                }
                else {
                    //Since order is empty delete it
                    DatabaseReference currUserOrder = databaseRef.child(mAuth.getCurrentUser().getUid()).child("MyOrders").child(vendorUniqueID);
                    currUserOrder.removeValue();
                }



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
                    else if (type.equals("Time")){
                        this.time = (String) values.get(type);
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
                if (!order.equals("")) {

                    Order customerOrder = new Order(instanceId, order, customerName, pushId, vendorUniqueID);

                    //deletes old order
                    orders.remove(customerOrder);
                    //adds new order at end of queue

                    customerOrder.setStatus(status);
                    customerOrder.setFoodTruckName(foodTruckName);
                    customerOrder.setPrice(price);
                    customerOrder.setTime(time);


                    orders.add(customerOrder);

                    arrayAdapter.notifyDataSetChanged();
                    updateTotal();
                }
                else {
                    //Delete the order.
                    DatabaseReference currUserOrder = databaseRef.child(mAuth.getCurrentUser().getUid()).child("MyOrders").child(vendorUniqueID);
                    currUserOrder.removeValue();
                }

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

                arrayAdapter.notifyDataSetChanged();
                updateTotal();

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String prevChildKey) {
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });;
    }



    /**
     * Handles the code for the Submit button
     */    public void submitOrder_onClick(View v) {
        if (selectedOrder == null) {            // button clicked but no order selected
            Toast.makeText(Cart.this, "You must select an order first", Toast.LENGTH_LONG).show();
            return;
        }
        //Does not allow to resubmit order
        if (selectedOrder.getStatus()) {
            Toast.makeText(Cart.this, "You already submitted this order!", Toast.LENGTH_LONG).show();
            return;
        }

        // setup Complete Order popup
        AlertDialog.Builder confirmPopupBuilder = new AlertDialog.Builder(this);
        confirmPopupBuilder.setTitle("Your order is going to be sent to the food truck. You will have 3 minutes to cancel it and 1 minute to modify it.");
        confirmPopupBuilder.setMessage("Are you sure you want this order: \n" + selectedOrder.getCustomerOrder().toString());

        confirmPopupBuilder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int which) {

                //Send order to vendor
                CustomerOrderMGM customerOrderMGM = new CustomerOrderMGM();
                customerOrderMGM.setVendorUniqueID(selectedOrder.vendorUniqueID);
                customerOrderMGM.sendOrderToVendor(selectedOrder);

                // make text normal
                previousChildSelected.getText1().setTypeface(null, Typeface.NORMAL);
                previousChildSelected.getText2().setTypeface(null, Typeface.NORMAL);
                previousChildSelected = null;
                isOrderSelected = false;


                dialog.dismiss();
            }
        });

        confirmPopupBuilder.setNegativeButton("No", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {

                dialog.dismiss();
            }
        });

        // show Complete Order popup
        AlertDialog alert = confirmPopupBuilder.create();
        alert.show();
    }



    /**
     * Handles the code for the Update Order button
     */
    public void updateOrder_OnClick(View v) {
        if (selectedOrder == null) {            // button clicked but no order selected
            Toast.makeText(Cart.this, "You must select an order first", Toast.LENGTH_LONG).show();
            return;
        }
        //Allows to change order once submitted
        if (selectedOrder.getStatus()) {
            AlertDialog.Builder confirmPopupBuilder = new AlertDialog.Builder(this);
            confirmPopupBuilder.setTitle("The order you selected has already been sent to the vendor");
            confirmPopupBuilder.setMessage("Are you sure you want to modify this order: \n" + selectedOrder.getCustomerOrder().toString() + "?");

            confirmPopupBuilder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {

                public void onClick(DialogInterface dialog, int which) {

                    //Go to the vendor profile page
                    selectedOrder.getVendorUniqueID();
                    CustomerOrderMGM customerOrderMGM = new CustomerOrderMGM();
                    customerOrderMGM.setVendorUniqueID(selectedOrder.vendorUniqueID);
                    //Gives user 1 minutes to modify order
                    DateTimeFormatter formatter = DateTimeFormat.forPattern("dd/MM/yyyy HH:mm:ss");
                    DateTime prevDT = formatter.parseDateTime((String)selectedOrder.getTime());
                    boolean isValidTime = customerOrderMGM.time(prevDT, 1);
                    if (isValidTime) {
                        Intent i = new Intent(Cart.this, VendorProfileForCustomerActivity.class);
                        i.putExtra("vendorUniqueID", selectedOrder.getVendorUniqueID());
                        startActivity(i);

                    }
                    else {
                        Toast.makeText(Cart.this, "You have exceeded the time limit to modify your order.", Toast.LENGTH_SHORT).show();
                    }


                }
            });

            confirmPopupBuilder.setNegativeButton("No", new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface dialog, int which) {

                    dialog.dismiss();
                }
            });

            // show Complete Order popup
            AlertDialog alert = confirmPopupBuilder.create();
            alert.show();


        } else {


            AlertDialog.Builder confirmPopupBuilder = new AlertDialog.Builder(this);
            confirmPopupBuilder.setTitle("You are about to modify your oder");
            confirmPopupBuilder.setMessage("Are you sure you want to modify this order: \n" + selectedOrder.getCustomerOrder().toString()+ "?") ;
            confirmPopupBuilder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {

                public void onClick(DialogInterface dialog, int which) {
                    //Go to the vendor profile page
                    selectedOrder.getVendorUniqueID();
                    //Todo: Go to specific vendor profile
                    Intent i = new Intent(Cart.this, VendorProfileForCustomerActivity.class);
                    i.putExtra("vendorUniqueID", selectedOrder.getVendorUniqueID());
                    startActivity(i);
                }
            });

            confirmPopupBuilder.setNegativeButton("No", new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });

            // show Complete Order popup
            AlertDialog alert = confirmPopupBuilder.create();
            alert.show();
        }
    }


    // submit order
    public void cartOrderCancelled_onClick(View v) {
        if (selectedOrder == null) {            // button clicked but no order selected
            Toast.makeText(Cart.this, "You must select an order first", Toast.LENGTH_LONG).show();
            return;
        }

        //Allows to cancel order once submitted
        if (selectedOrder.getStatus()) {
            AlertDialog.Builder confirmPopupBuilder = new AlertDialog.Builder(this);
            confirmPopupBuilder.setTitle("The order you selected has already been sent to the vendor");
            confirmPopupBuilder.setMessage("Are you sure you want to cancel this order: \n" + selectedOrder.getCustomerOrder().toString() + "?");

            confirmPopupBuilder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {

                public void onClick(DialogInterface dialog, int which) {

                    CustomerOrderMGM customerOrderMGM = new CustomerOrderMGM();
                    customerOrderMGM.setVendorUniqueID(selectedOrder.vendorUniqueID);
                    //Gives user 3 minuts to cancel order
                    DateTimeFormatter formatter = DateTimeFormat.forPattern("dd/MM/yyyy HH:mm:ss");
                    DateTime prevDT = formatter.parseDateTime((String)selectedOrder.getTime());
                    boolean isValidTime = customerOrderMGM.time(prevDT, 3);
                    if (isValidTime) {
                        customerOrderMGM.cancelOrder(selectedOrder.pushId, selectedOrder.getStatus());
                    }
                    else {
                        Toast.makeText(Cart.this, "You have exceeded the time limit to cancel your order.", Toast.LENGTH_SHORT).show();
                    }

                }
            });

            confirmPopupBuilder.setNegativeButton("No", new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface dialog, int which) {

                    dialog.dismiss();
                }
            });

            // show Complete Order popup
            AlertDialog alert = confirmPopupBuilder.create();
            alert.show();


        } else {


            AlertDialog.Builder confirmPopupBuilder = new AlertDialog.Builder(this);
            confirmPopupBuilder.setTitle("You are about to remove this oder");
            confirmPopupBuilder.setMessage("Are you sure you want to remove this order: " + selectedOrder.getCustomerOrder().toString()+ "?") ;

            confirmPopupBuilder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {

                public void onClick(DialogInterface dialog, int which) {

                    CustomerOrderMGM customerOrderMGM = new CustomerOrderMGM();
                    customerOrderMGM.setVendorUniqueID(selectedOrder.vendorUniqueID);
                    customerOrderMGM.cancelOrder(selectedOrder.pushId, selectedOrder.getStatus());

                }
            });

            confirmPopupBuilder.setNegativeButton("No", new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface dialog, int which) {

                    dialog.dismiss();
                }
            });

            // show Complete Order popup
            AlertDialog alert = confirmPopupBuilder.create();
            alert.show();
        }
    }


    /**
     * Custom adapter class that handles how the items are displayed in the cart
     */

    class MyAdapter extends BaseAdapter {

        private Context context;
        private ArrayList<Order> orders;

        public MyAdapter(Context context, ArrayList<Order> orders) {
            this.context = context;
            this.orders = orders;
        }

        @Override
        public int getCount() {
            return orders.size();
        }

        @Override
        public Object getItem(int position) {
            return orders.get(position);
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            TwoLineListItem twoLineListItem;

            if (convertView == null) {
                LayoutInflater inflater = (LayoutInflater) context
                        .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                twoLineListItem = (TwoLineListItem) inflater.inflate(
                        android.R.layout.simple_list_item_2, null);
            } else {
                twoLineListItem = (TwoLineListItem) convertView;
            }

            TextView text1 = twoLineListItem.getText1();
            text1.setTextSize(20);
            TextView text2 = twoLineListItem.getText2();
            NumberFormat formatter = new DecimalFormat("#0.00");
            text1.setText(orders.get(position).getFoodTruckName() + " - $" + formatter.format(orders.get(position).getPrice()));
            text2.setText(orders.get(position).getCustomerOrder());
            return twoLineListItem;
        }
    }

    /**
     * Handles the bar that displays the total. It is constantly updated.
     */

    private void updateTotal(){
        TextView total = (TextView)findViewById(R.id.total_shopping_cart);
        double result = 0.0;
        for (Order order: orders) {
            result = result + order.getPrice();
        }
        NumberFormat formatter = new DecimalFormat("#0.00");

        total.setText("$"+ formatter.format(result));
    }


}

