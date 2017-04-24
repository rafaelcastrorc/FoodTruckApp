package edu.upenn.cis350.foodtruckapp;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
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
import android.widget.TwoLineListItem;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;

import org.joda.time.DateTime;
import org.joda.time.Period;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.w3c.dom.Text;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;

//Handles the GUI of the vendor analytics page
public class VendorAnalyticsActivity extends AppCompatActivity {

    protected ListView statList;
    protected ArrayList<Stat> stats = new ArrayList<>();
    private boolean isOrderSelected = false;
    private TwoLineListItem previousChildSelected = null;
    private Order selectedOrder;

    private FirebaseAuth mAuth;
    private DatabaseReference databaseRef;
    private String id;
    private HashMap<Integer, Order> orderHistoryMap = new HashMap<>();
    protected LinkedList<Order> orderList = new LinkedList<>();
    VendorAnalyticsActivity.MyAdapter arrayAdapter;
    boolean first = true;


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
                Intent j = new Intent(VendorAnalyticsActivity.this, VendorMainMenuActivity.class);
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
        setContentView(R.layout.activity_vendor_analytics);
        statList = (ListView) findViewById(R.id.list_view_analaytics);
        arrayAdapter = new VendorAnalyticsActivity.MyAdapter(this, stats);
        statList.setAdapter(arrayAdapter);

        databaseRef = FirebaseDatabase.getInstance().getReference("Users");
        mAuth = FirebaseAuth.getInstance();
        String currVendor = mAuth.getCurrentUser().getUid();
        DatabaseReference currentOrders = databaseRef.child(currVendor).child("OrderHistory");

        //handles the updated text field
        DateTime initial = new DateTime();
        TextView updated = (TextView) findViewById(R.id.updated_last);
        DateTimeFormatter fmt = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss");
        String dtStr = fmt.print(initial);
        updated.setText("Last updated: " + dtStr);

        updated.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                overridePendingTransition( 0, 0);
                startActivity(getIntent());
                overridePendingTransition( 0, 0);

            }
        });

        //Tracking all the orders

        currentOrders.addChildEventListener(new ChildEventListener() {
            Double price = 0.0;
            String time = "";
            String vendorUniqueID = "";
            String instanceId = "";
            String order = "";
            String customerName = "";
            String pushId = "";
            String customerUniqueID = "";



            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String prevChildKey) {
                HashMap<String, Object> values =  (HashMap<String, Object>) dataSnapshot.getValue();
                for (String type: values.keySet()) {

                    if (type.equals("customerInstanceId")) {
                        this.instanceId = (String) values.get(type);

                    }
                    else if (type.equals("customerOrder")) {
                        this.order = (String) values.get(type);
                    }
                    else if (type.equals("customerName")){
                        this.customerName = (String) values.get(type);
                    }
                    else if (type.equals("pushId")){
                        this.pushId = (String) values.get(type);
                    }
                    else if (type.equals("customerUniqueID")){
                        this.customerUniqueID = (String) values.get(type);
                    }
                    else if (type.equals("time")) {
                        this.time = (String) values.get(type);
                    }
                    else if (type.equals("price")) {
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

                    Order customerOrder = new Order(instanceId, order, customerName, pushId, vendorUniqueID);
                    customerOrder.setCustomerUniqueID(customerUniqueID);
                    customerOrder.setTime(time);
                    customerOrder.setPrice(price);
                    addOrder(customerOrder);

            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String prevChildKey) {
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String prevChildKey) {
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });




    }



    private void addStats(HashMap<Integer, Order> orderHistoryMap) {

        VendorAnalytics va = new VendorAnalytics(orderHistoryMap);

        //Show all sales
        NumberFormat formatter = new DecimalFormat("#0.00");
        //Past hour sales
        double result = va.getSales("Hour");
        Stat stat1 = new Stat("Sales in the last hour", "$"+ formatter.format(result));
        //Past hour sales
        result = va.getSales("Hour");
        Stat stat2 = new Stat("Sales in the last day", "$"+formatter.format(result) );
        //Past week sales
        result = va.getSales("Week");
        Stat stat3 = new Stat("Sales in the last week", "$"+formatter.format(result) );
        //Past month sales
        result = va.getSales("Month");
        Stat stat4 = new Stat("Sales in the last month", "$"+formatter.format(result) );
        //Past year sales
        result = va.getSales("Year");
        Stat stat5 = new Stat("Sales in the last year", "$"+formatter.format(result) );
        //All time sales
        result = va.getSales("All time");
        Stat stat6 = new Stat("All time sales", "$"+formatter.format(result) );

        //Show most popular times
        //Most popular hour
        String result2 = va.getMostPopularTime("Hour");
        Stat stat7 = new Stat("Most popular hour", result2 );
        //Most popular day
        result2 = va.getMostPopularTime("Day");
        Stat stat8 = new Stat("Most popular day", result2 );


        //Most popular product
        String result3 = va.getBestSellingProduct();
        Stat stat9 = new Stat("Most ordered product", result3);
        if (!first) {
            stats.remove(stat1);
            stats.remove(stat2);
            stats.remove(stat3);
            stats.remove(stat4);
            stats.remove(stat5);
            stats.remove(stat6);
            stats.remove(stat7);
            stats.remove(stat8);
            stats.remove(stat9);



            first = true;
        }
        if (first) {
            stats.add(stat1);
            stats.add(stat2);
            stats.add(stat3);
            stats.add(stat4);
            stats.add(stat5);
            stats.add(stat6);
            stats.add(stat7);
            stats.add(stat8);
            stats.add(stat9);

            this.first = false;
        }

        arrayAdapter.notifyDataSetChanged();
        //Show graph
        //Week to number of orders graph
        //Hours to number of orders graph
        //Month to profit or month to number of orders for 12 months


    }




    public void update_Analytics_onClick(View v) {
        finish();
        overridePendingTransition( 0, 0);
        startActivity(getIntent());
        overridePendingTransition( 0, 0);    }



    /**
     * Adds an order to the vendor and modifies the stats displayed on the screen
     * @param order - order you want to add
     * @return void
     */
    protected void addOrder(Order order) {
        orderList.add(order);
        //Map of num of the order to an Order object
        orderHistoryMap.put(orderList.size(), order);
        addStats(orderHistoryMap);
    }
    /**
     * Remove an order from the vendor history and modifies the stats displayed on the screen
     * @param order - order you want to remove
     * @return void
     */
    protected void removeOrder(Order order) {
        orderList.remove(order);
        //Map of num of the order to an Order object
        int j =0;
        for (int i : orderHistoryMap.keySet()) {
            if (orderHistoryMap.get(i).equals(order)) {
                j = i;
            }
        }
        orderHistoryMap.remove(j);
    }


    /**
     * Custom adapter class that handles how the stats are displayed in the activity
     */

    class MyAdapter extends BaseAdapter {

        private Context context;
        private ArrayList<Stat> stats;

        public MyAdapter(Context context, ArrayList<Stat> stats) {
            this.context = context;
            this.stats = stats;
        }

        @Override
        public int getCount() {
            return stats.size();
        }

        @Override
        public Object getItem(int position) {
            return stats.get(position);
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
            text1.setText(stats.get(position).getTitle());
            text2.setText(stats.get(position).getValue());
            return twoLineListItem;
        }
    }

    class Stat {
        private final String value;
        private final String title;


        Stat(String title, String value) {
            this.title = title;
            this.value = value;
        }

        public String getValue() {
            return value;
        }

        public String getTitle() {
            return title;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            Stat stat = (Stat) o;

            return title.equals(stat.title);

        }

        @Override
        public int hashCode() {
            return title.hashCode();
        }
    }


}