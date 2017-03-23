package edu.upenn.cis350.foodtruckapp;

import android.content.Context;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
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

    private FirebaseDatabase database;
    private FirebaseAuth mAuth;
    private DatabaseReference databaseRef;
    DatabaseReference currentOrders;

    private ListView orderList;
    private ArrayList<Order> orders = new ArrayList<Order>();
    private boolean isOrderSelected = false;
    private TwoLineListItem previousChildSelected = null;
    private Order selectedOrder = null;


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

        // Orders are represented in the database by a push id, and each push id has a instanceID and a message
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


    // Send notification to customer that their order is ready
    public void OrderDoneOnClick(View v) {

        if (selectedOrder == null) {            // button clicked but no order selected
            Toast.makeText(VendorOrdersActivity.this, "No order selected", Toast.LENGTH_LONG).show();
            return;
        }

        Toast.makeText(VendorOrdersActivity.this, selectedOrder.getCostumerName() + " has been" +
                " notified of their order!", Toast.LENGTH_LONG).show();
        String customerInstanceId = selectedOrder.getCostumerInstanceID();
        customerInstanceId = FirebaseInstanceId.getInstance().getId(); // for testing purposes
        FoodTruckOrderMGM notifications = new FoodTruckOrderMGM(customerInstanceId);
        notifications.orderDone();

        // make text normal
        previousChildSelected.getText1().setTypeface(null, Typeface.NORMAL);
        previousChildSelected.getText2().setTypeface(null, Typeface.NORMAL);
        previousChildSelected = null;
        isOrderSelected = false;
    }

    // Todo: Setup functionality for when a vendor cancels order
    // Send notification to customer that their order was cancelled
    public void OrderCancelledOnClick(View v) {

        if (selectedOrder == null) {            // button clicked but no order selected
            return;
        }
        Toast.makeText(VendorOrdersActivity.this, selectedOrder.getCostumerName() + "'s order" +
                " has been cancelled!", Toast.LENGTH_LONG).show();
        String customerInstanceId = selectedOrder.getCostumerInstanceID();
        //customerInstanceId = FirebaseInstanceId.getInstance().getId(); // for testing purposes
        FoodTruckOrderMGM notifications = new FoodTruckOrderMGM(customerInstanceId);
        notifications.orderDone();

        // make text normal
        previousChildSelected.getText1().setTypeface(null, Typeface.NORMAL);
        previousChildSelected.getText2().setTypeface(null, Typeface.NORMAL);
        previousChildSelected = null;
        isOrderSelected = false;
    }

    public class Order {
        protected String costumerInstanceID;
        protected String order;
        protected String costumerName;

        Order(String costumerInstanceID, String order, String name ) {
            this.costumerInstanceID = costumerInstanceID;
            this.order = order;
            this.costumerName = name;
        }

        public String getCostumerInstanceID() {
            return costumerInstanceID;
        }
        public String getCostumerOrder() {
            return order;
        }
        public String getCostumerName() {return costumerName;}

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

        @Override
        public String toString() {
            String formattedOrder = "";
            formattedOrder = order + "\n" + costumerName;
            return formattedOrder;

        }
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
            text1.setText(orders.get(position).getCostumerName());
            text2.setText("Halal");
            return twoLineListItem;
        }
    }

}