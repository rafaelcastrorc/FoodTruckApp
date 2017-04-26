package edu.upenn.cis350.foodtruckapp;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
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
import com.google.firebase.database.ValueEventListener;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;



//Handles the vendor queue interface
public class VendorOrdersActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private DatabaseReference databaseRef;
    private DatabaseReference currentOrders;

    protected ListView orderList;
    protected ArrayList<Order> orders = new ArrayList<>();
    private boolean isOrderSelected = false;
    private TwoLineListItem previousChildSelected = null;
    private Order selectedOrder;
    private String truckName = "";

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_for_vendor, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            case R.id.home_button:
                Intent j = new Intent(VendorOrdersActivity.this, VendorMainMenuActivity.class);
                startActivity(j);
                return true;
            case R.id.stats_button:
                j = new Intent(VendorOrdersActivity.this, VendorAnalyticsActivity.class);
                startActivity(j);
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vendor_orders);

        databaseRef = FirebaseDatabase.getInstance().getReference("Users");
        mAuth = FirebaseAuth.getInstance();
        String currVendor = mAuth.getCurrentUser().getUid();
        currentOrders = databaseRef.child(currVendor).child("Orders");


        orderList = (ListView) findViewById(R.id.order_list);
        final MyAdapter arrayAdapter = new MyAdapter(this, orders);
        orderList.setAdapter(arrayAdapter);

        // When an order is clicked by the vendor make it bold & italic
        ListView listView = (ListView) findViewById(R.id.order_list);
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

                selectedChild.getText1().setTypeface(null, Typeface.BOLD_ITALIC);
                selectedChild.getText2().setTypeface(null, Typeface.BOLD_ITALIC);

                selectedOrder = (Order) parent.getItemAtPosition(position);

            }

        });

        currentOrders.addChildEventListener(new ChildEventListener() {
            String foodTruckName;
            Double price = 0.0;
            String time = "";
            String vendorUniqueID = "";
            String instanceId = "";
            String order = "";
            String customerName = "";
            String pushId = "";
            String customerUniqueID = "";
            int counter = 0;



            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String prevChildKey) {
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
                    else if (type.equals("customerUniqueID")){
                        this.customerUniqueID = (String) values.get(type);
                    }
                    else if (type.equals("FoodTruckName")){
                        this.foodTruckName = (String) values.get(type);
                        truckName = foodTruckName;
                    }
                    else if (type.equals("Time")) {
                        this.time = (String) values.get(type);
                    }
                    else if (type.equals("Price")) {
                        try {
                            this.price = (Double) values.get(type);
                        } catch (ClassCastException e) {
                            Long l = new Long((Long) values.get(type));
                            this.price = l.doubleValue();
                        }
                    }
                    else if (type.equals("vendorUniqueID")){
                        this.vendorUniqueID = (String) values.get(type);
                    }

                }
                if (!order.isEmpty()) {
                    Order customerOrder = new Order(instanceId, order, customerName, pushId, vendorUniqueID);
                    customerOrder.setCustomerUniqueID(customerUniqueID);
                    customerOrder.setTime(time);
                    customerOrder.setPrice(price);
                    customerOrder.setFoodTruckName(foodTruckName);
                    orders.add(customerOrder);

                    arrayAdapter.notifyDataSetChanged();
                }
                else {
                        CustomerOrderMGM customerOrderMGM = new CustomerOrderMGM();
                        customerOrderMGM.setVendorUniqueID(vendorUniqueID);
                        customerOrderMGM.cancelOrder(pushId, true);
                }


            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String prevChildKey) {
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
                    else if (type.equals("FoodTruckName")){
                        this.foodTruckName = (String) values.get(type);
                        truckName = foodTruckName;
                    }
                    else if (type.equals("customerUniqueID")){
                        this.customerUniqueID = (String) values.get(type);
                    }
                    else if (type.equals("Time")) {
                        this.time = (String) values.get(type);
                    }
                    else if (type.equals("Price")) {
                        try {
                            this.price = (Double) values.get(type);
                        } catch (ClassCastException e) {
                            Long l = new Long((Long) values.get(type));
                            this.price = l.doubleValue();
                        }
                    }
                    else if (type.equals("vendorUniqueID")){
                        this.vendorUniqueID = (String) values.get(type);
                    }

                }
                if (!order.isEmpty()) {
                    Order customerOrder = new Order(instanceId, order, customerName, pushId, vendorUniqueID);
                    //deletes old order
                    orders.remove(customerOrder);

                    customerOrder.setCustomerUniqueID(customerUniqueID);
                    customerOrder.setTime(time);
                    customerOrder.setPrice(price);
                    customerOrder.setFoodTruckName(foodTruckName);


                    //adds new order at end of queue
                    orders.add(customerOrder);

                    arrayAdapter.notifyDataSetChanged();
                }
                else {
                    CustomerOrderMGM customerOrderMGM = new CustomerOrderMGM();
                    customerOrderMGM.setVendorUniqueID(vendorUniqueID);
                    customerOrderMGM.cancelOrder(pushId, true);
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

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String prevChildKey) {
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }



    /*Handles the order done button*/
    public void orderDone_OnClick(View v) {
        if (selectedOrder == null) {            // button clicked but no order selected
            Toast.makeText(VendorOrdersActivity.this, "You must select an order first", Toast.LENGTH_LONG).show();
            return;
        }

        // setup Complete Order popup
        AlertDialog.Builder confirmPopupBuilder = new AlertDialog.Builder(this);
        confirmPopupBuilder.setTitle("This order has been completed");
        confirmPopupBuilder.setMessage("Are you sure you want to mark this order as completed: " + selectedOrder.getCustomerName().toString()
                + "'s order?");

        confirmPopupBuilder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int which) {

                //Add order to the order history field in the database
                VendorAnalytics va = new VendorAnalytics();
                va.pushOrderToDataBase(selectedOrder);

                //Remove all instances of the order
                currentOrders.child(selectedOrder.getPushId()).removeValue();
                databaseRef.child(selectedOrder.getCustomerUniqueID()).child("MyOrders").child(selectedOrder.getVendorUniqueID()).removeValue();

                //Add order to user history
                selectedOrder.setFoodTruckName(truckName);
                databaseRef.child(selectedOrder.getCustomerUniqueID()).child("History").push().setValue(selectedOrder);



                // make text normal
                try {
                    previousChildSelected.getText1().setTypeface(null, Typeface.NORMAL);
                    previousChildSelected.getText2().setTypeface(null, Typeface.NORMAL);
                }
                catch (NullPointerException e) {

                }

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
     * Handles the Order is Ready button.
     * Sends notification to customer
     */
    public void OrderReadyOnClick(View v) {
        if (selectedOrder == null) {            // button clicked but no order selected
            Toast.makeText(VendorOrdersActivity.this, "You must select an order first", Toast.LENGTH_LONG).show();
            return;
        }

        // setup order ready popup
        AlertDialog.Builder confirmPopupBuilder = new AlertDialog.Builder(this);
        confirmPopupBuilder.setTitle("Complete Order");
        confirmPopupBuilder.setMessage("Are you sure you want to complete " + selectedOrder.getCustomerName().toString()
                + "'s order?");

        confirmPopupBuilder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int which) {
                Toast.makeText(VendorOrdersActivity.this, selectedOrder.getCustomerName() + " has been" +
                        " notified of his or her order!", Toast.LENGTH_LONG).show();

                String customerInstanceId = selectedOrder.getCustomerInstanceID();
                FoodTruckOrderMGM notifications = new FoodTruckOrderMGM(customerInstanceId);
                notifications.orderDone(1);

                // make text normal
                try {
                    previousChildSelected.getText1().setTypeface(null, Typeface.NORMAL);
                    previousChildSelected.getText2().setTypeface(null, Typeface.NORMAL);
                    previousChildSelected = null;
                    isOrderSelected = false;
                }
                catch (NullPointerException e) {

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

    /**
     * Handles the Cancel Order button.
     * Sends notification to customer
     */
    public void OrderCancelledOnClick(View v) {

        if (selectedOrder == null) {            // button clicked but no order selected
            Toast.makeText(VendorOrdersActivity.this, "You must select an order first", Toast.LENGTH_LONG).show();
            return;
        }
        // setup Cancelled popup
        AlertDialog.Builder cancelledPopupBuilder = new AlertDialog.Builder(this);
        cancelledPopupBuilder.setTitle("Cancel Order");
        cancelledPopupBuilder.setMessage("Are you sure you want to cancel " + selectedOrder.getCustomerName().toString()
                + "'s order?");

        cancelledPopupBuilder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int which) {
                Toast.makeText(VendorOrdersActivity.this, selectedOrder.getCustomerName() + "'s " +
                        " order has been cancelled", Toast.LENGTH_LONG).show();
                String customerInstanceId = selectedOrder.getCustomerInstanceID();
              //  customerInstanceId = FirebaseInstanceId.getInstance().getId();          // for testing purposes
                FoodTruckOrderMGM notifications = new FoodTruckOrderMGM(customerInstanceId);
                notifications.orderDone(2);
                currentOrders.child(selectedOrder.getPushId()).removeValue();
                databaseRef.child(selectedOrder.getCustomerUniqueID()).child("MyOrders").child(selectedOrder.getVendorUniqueID()).removeValue();

                // make text normal
                previousChildSelected.getText1().setTypeface(null, Typeface.NORMAL);
                previousChildSelected.getText2().setTypeface(null, Typeface.NORMAL);
                previousChildSelected = null;
                isOrderSelected = false;

                dialog.dismiss();
            }
        });

        cancelledPopupBuilder.setNegativeButton("No", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {

                dialog.dismiss();
            }
        });

        // show Complete Order popup

        AlertDialog alert = cancelledPopupBuilder.create();
        alert.show();
    }


    public void OrderNoShowOnClick(View v) {
        // button clicked but no order selected
        if (selectedOrder == null) {
            Toast.makeText(VendorOrdersActivity.this, "You must select an order first", Toast.LENGTH_LONG).show();
            return;
        }

        // setup noshow popup
        AlertDialog.Builder cancelledPopupBuilder = new AlertDialog.Builder(this);
        cancelledPopupBuilder.setTitle("No Show");
        cancelledPopupBuilder.setMessage("Are you sure " + selectedOrder.getCustomerName().toString()
                + " did not show up?");

        cancelledPopupBuilder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int which) {
                Toast.makeText(VendorOrdersActivity.this, selectedOrder.getCustomerName() +
                        " has been flagged.", Toast.LENGTH_LONG).show();

                String customerInstanceId = selectedOrder.getCustomerInstanceID();
                //  customerInstanceId = FirebaseInstanceId.getInstance().getId();          // for testing purposes

                currentOrders.child(selectedOrder.getPushId()).removeValue();
                databaseRef.child(selectedOrder.getCustomerUniqueID()).child("MyOrders").child(selectedOrder.getVendorUniqueID()).removeValue();

                // update noshow count
                updateCustomerNoShow(selectedOrder.getCustomerUniqueID());

                // make text normal
                previousChildSelected.getText1().setTypeface(null, Typeface.NORMAL);
                previousChildSelected.getText2().setTypeface(null, Typeface.NORMAL);
                previousChildSelected = null;
                isOrderSelected = false;

                dialog.dismiss();
            }
        });

        cancelledPopupBuilder.setNegativeButton("No", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {

                dialog.dismiss();
            }
        });

        // show Complete Order popup

        AlertDialog alert = cancelledPopupBuilder.create();
        alert.show();
    }

    // add 1 to the customer's no-show count
    public void updateCustomerNoShow(String customerID) {
        final String id = customerID;

        databaseRef.child(customerID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (!dataSnapshot.hasChild("No Show")) {
                    databaseRef.child(id).child("No Show").setValue("1");
                }
                else {
                    int noShowCounter = Integer.parseInt(dataSnapshot.child("No Show")
                            .getValue().toString());
                    databaseRef.child(id).child("No Show").setValue(++noShowCounter);

                    if (noShowCounter == 3) {
                        Log.d("noshow test", "counter is 3");
                        databaseRef.child(id).child("Ban Time").setValue(
                                Math.round(System.currentTimeMillis() / 1000.0));
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

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
            TextView text2 = twoLineListItem.getText2();
            text1.setTextSize(24);
            text2.setTextSize(15);

            NumberFormat formatter = new DecimalFormat("#0.00");
            text1.setText(orders.get(position).getCustomerName() + " $"+ formatter.format(orders.get(position).getPrice()));
            text2.setText(orders.get(position).getCustomerOrder());
            return twoLineListItem;
        }
    }

}