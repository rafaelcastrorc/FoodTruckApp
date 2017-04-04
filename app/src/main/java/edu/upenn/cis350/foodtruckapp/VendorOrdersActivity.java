package edu.upenn.cis350.foodtruckapp;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
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
import com.google.firebase.iid.FirebaseInstanceId;

import java.util.ArrayList;
import java.util.HashMap;


public class VendorOrdersActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private DatabaseReference databaseRef;
    DatabaseReference currentOrders;

    protected ListView orderList;
    protected ArrayList<Order> orders = new ArrayList<>();
    private boolean isOrderSelected = false;
    private TwoLineListItem previousChildSelected = null;
    private Order selectedOrder;


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
            public String vendorUniqueID = "";
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
                    else if (type.equals("vendorUniqueID")){
                        this.vendorUniqueID = (String) values.get(type);
                    }

                }
                Order customerOrder = new Order(instanceId, order, customerName, pushId, vendorUniqueID);
                customerOrder.setCustomerUniqueID(customerUniqueID);
                orders.add(customerOrder);

                arrayAdapter.notifyDataSetChanged();


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
                    else if (type.equals("customerUniqueID")){
                        this.customerUniqueID = (String) values.get(type);
                    }
                    else if (type.equals("vendorUniqueID")){
                        this.vendorUniqueID = (String) values.get(type);
                    }

                }
                Order customerOrder = new Order(instanceId, order, customerName, pushId, vendorUniqueID);
                //deletes old order
                orders.remove(customerOrder);

                customerOrder.setCustomerUniqueID(customerUniqueID);

                //adds new order at end of queue
                orders.add(customerOrder);

                arrayAdapter.notifyDataSetChanged();

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



    // Order is Done
    public void orderDone_onClick(View v) {
        if (selectedOrder == null) {            // button clicked but no order selected
            Toast.makeText(VendorOrdersActivity.this, "You must select an order first", Toast.LENGTH_LONG).show();
            return;
        }

        // setup Complete Order popup
        AlertDialog.Builder confirmPopupBuilder = new AlertDialog.Builder(this);
        confirmPopupBuilder.setTitle("This order has been completes");
        confirmPopupBuilder.setMessage("Are you sure you want to mark this order as completed: " + selectedOrder.getCustomerName().toString()
                + "'s order?");

        confirmPopupBuilder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int which) {

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



    // Send notification to customer that their order is ready
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

    // Send notification to customer that their order was cancelled
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

            text1.setText(orders.get(position).getCustomerName());
            text2.setText(orders.get(position).getCustomerOrder());
            return twoLineListItem;
        }
    }

}