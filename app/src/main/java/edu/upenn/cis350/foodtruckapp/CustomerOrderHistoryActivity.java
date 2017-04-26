package edu.upenn.cis350.foodtruckapp;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
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
import java.util.Stack;
import java.util.TreeMap;

public class CustomerOrderHistoryActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private DatabaseReference databaseRef;
    DatabaseReference myOrdersHistoryRef;

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

                Intent i = new Intent(CustomerOrderHistoryActivity.this, Cart.class);
                startActivity(i);
                return true;
            case R.id.home_button:
                Intent j = new Intent(CustomerOrderHistoryActivity.this, CustomerMainMenuActivity.class);
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
        setContentView(R.layout.activity_customer_order_history);

        databaseRef = FirebaseDatabase.getInstance().getReference("Users");
        mAuth = FirebaseAuth.getInstance();
        myOrdersHistoryRef = databaseRef.child(mAuth.getCurrentUser().getUid()).child("History");

        orderList = (ListView) findViewById(R.id.list_view_order_history);
        final CustomerOrderHistoryActivity.MyAdapter arrayAdapter = new CustomerOrderHistoryActivity.MyAdapter(this, orders);
        orderList.setAdapter(arrayAdapter);

        // When an order is clicked by the customer make it bold & italic
        ListView listView = (ListView) findViewById(R.id.list_view_order_history);
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

        myOrdersHistoryRef.addChildEventListener(new ChildEventListener() {
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

                    if (type.equals("customerInstanceId")) {
                        this.instanceId = (String) values.get(type);
                    }
                    else if (type.equals("order")) {
                        this.order = (String) values.get(type);
                    }
                    else if (type.equals("customerName")){
                        this.customerName = (String) values.get(type);
                    }
                    else if (type.equals("pushId")){
                        this.pushId = (String) values.get(type);
                    }
                    else if (type.equals("vendorUniqueID")){
                        this.vendorUniqueID = (String) values.get(type);
                    }
                    else if (type.equals("foodTruckName")){
                        this.foodTruckName = (String) values.get(type);
                    }
                    else if (type.equals("time")){
                        this.time = (String) values.get(type);
                    }
                    else if (type.equals("price")){
                        try {
                            this.price = (Double) values.get(type);
                        }
                        catch (ClassCastException e) {
                            Long l = (Long) values.get(type);
                            this.price = l.doubleValue();
                        }
                    }

                    else if(type.equals("submitted")) {
                        Boolean choice  = (Boolean) values.get(type);

                    }

                }
                //Add as long as is not empty
                if (!order.equals("")) {

                    Order customerOrder = new Order(instanceId, order, customerName, pushId, vendorUniqueID);
                    customerOrder.setStatus(status);
                    customerOrder.setFoodTruckName(foodTruckName);
                    customerOrder.setPrice(price);
                    customerOrder.setTime(time);
                    orders.add(0, customerOrder);

                    arrayAdapter.notifyDataSetChanged();
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

                    if (type.equals("customerInstanceId")) {
                        this.instanceId = (String) values.get(type);

                    }
                    else if (type.equals("order")) {
                        this.order = (String) values.get(type);
                    }
                    else if (type.equals("customerName")){
                        this.customerName = (String) values.get(type);
                    }
                    else if (type.equals("pushId")){
                        this.pushId = (String) values.get(type);
                    }
                    else if (type.equals("vendorUniqueID")){
                        this.vendorUniqueID = (String) values.get(type);
                    }
                    else if (type.equals("time")){
                        this.time = (String) values.get(type);
                    }
                    else if (type.equals("foodTruckName")){
                        this.foodTruckName = (String) values.get(type);
                    }
                    else if (type.equals("price")){
                        try {
                            this.price = (Double) values.get(type);
                        }
                        catch (ClassCastException e) {
                            Long l = new Long((Long) values.get(type));
                            this.price = l.doubleValue();

                        }
                    }

                    else if(type.equals("submitted")) {
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

                    orders.add(0, customerOrder);

                    arrayAdapter.notifyDataSetChanged();
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

                    if (type.equals("customerInstanceId")) {
                        this.instanceId = (String) values.get(type);

                    }
                    else if (type.equals("order")) {
                        this.order = (String) values.get(type);
                    }
                    else if (type.equals("customerName")){
                        this.customerName = (String) values.get(type);
                    }
                    else if (type.equals("pushId")){
                        this.pushId = (String) values.get(type);
                    }
                    else if (type.equals("vendorUniqueID")){
                        this.vendorUniqueID = (String) values.get(type);
                    }

                }
                Order customerOrder = new Order(instanceId, order, customerName, pushId, vendorUniqueID);
                orders.remove(customerOrder);

                arrayAdapter.notifyDataSetChanged();

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
     * Custom adapter class that handles how the items are displayed in the history
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
            DateTimeFormatter timeFormatter = DateTimeFormat.forPattern("dd/MM/yyyy HH:mm:ss");
            DateTime time = timeFormatter.parseDateTime(orders.get(position).getTime());
            String hour = String.valueOf(time.getHourOfDay()),
                    minute = String.valueOf(time.getMinuteOfHour()),
                    second = String.valueOf(time.getSecondOfMinute());
            if (time.getHourOfDay() < 10) {
                hour = "0" +time.getHourOfDay();
            }
            if (time.getMinuteOfHour() < 10) {
                minute = "0" +time.getMinuteOfHour();
            }
            if (time.getSecondOfMinute() < 10) {
                second = "0" +time.getSecondOfMinute();
            }
            String timeToDisplay = "Ordered: " +time.getMonthOfYear()+"/"+ time.getDayOfMonth()+"/"+
                    time.getYear() +" " + hour + ":" + minute+ ":" + second;

            text1.setText(orders.get(position).getFoodTruckName() + " - $" + formatter.format(orders.get(position).getPrice()));
            text2.setText(timeToDisplay +"\n" +  orders.get(position).getCustomerOrder());
            return twoLineListItem;
        }
    }

    /**
     * Handles the code for the order again button
     */
    public void order_again_onClick(View v) {
        if (selectedOrder == null) {            // button clicked but no order selected
            Toast.makeText(CustomerOrderHistoryActivity.this, "You must select an order first", Toast.LENGTH_LONG).show();
            return;
        }
        AlertDialog.Builder confirmPopupBuilder = new AlertDialog.Builder(this);
        confirmPopupBuilder.setTitle("You are about to submit this order");
        confirmPopupBuilder.setMessage("Are you sure you want this order: \n" + selectedOrder.getCustomerOrder().toString());

        confirmPopupBuilder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int which) {

                //Send order to vendor
                CustomerOrderMGM customerOrderMGM = new CustomerOrderMGM();
                customerOrderMGM.setVendorUniqueID(selectedOrder.vendorUniqueID);
                customerOrderMGM.setContext(getApplicationContext());

                customerOrderMGM.addOrderFromHistory(selectedOrder.getCustomerOrder(), selectedOrder.getFoodTruckName(), selectedOrder.getPrice());

                try {
                    // make text normal
                    previousChildSelected.getText1().setTypeface(null, Typeface.NORMAL);
                    previousChildSelected.getText2().setTypeface(null, Typeface.NORMAL);
                    previousChildSelected = null;
                    isOrderSelected = false;
                }
                catch (NullPointerException e){

                }


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

}
