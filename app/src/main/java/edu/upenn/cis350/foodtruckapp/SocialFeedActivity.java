package edu.upenn.cis350.foodtruckapp;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.TwoLineListItem;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.util.ArrayList;
import java.util.HashMap;

public class SocialFeedActivity extends AppCompatActivity {

    private ArrayList<Order> orders;
    private DatabaseReference databaseRef;
    private ListView orderList;

    /**
     * add shopping cart to menu bar
     * @param menu
     * @return true if selected activity valid
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.shopping_cart, menu);
        return true;
    }

    /**
     * start activities clicked in menu bar
     * @param item
     * @return
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.shopping_cart_button:

                Intent i = new Intent(SocialFeedActivity.this, Cart.class);
                startActivity(i);
                return true;
            case R.id.home_button:
                Intent j = new Intent(SocialFeedActivity.this, CustomerMainMenuActivity.class);
                startActivity(j);
                return true;
            case R.id.search_button_menu:
                Intent x = new Intent(SocialFeedActivity.this, SearchFoodActivity.class);
                startActivity(x);

            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_social_feed);
        orders = new ArrayList<Order>();
        final SocialFeedActivity.MyCustomAdapter arrayAdapter = new SocialFeedActivity.MyCustomAdapter(getApplicationContext());
        orderList = (ListView) findViewById(R.id.social_feed);
        orderList.setAdapter(arrayAdapter);

        databaseRef = FirebaseDatabase.getInstance().getReference().child("Users");
        databaseRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                HashMap<String, Object> values = (HashMap<String, Object>) dataSnapshot.getValue();
                String UID = (String) values.get("UniqueID");
                DatabaseReference myOrdersRef = databaseRef.child(UID).child("MyOrders");
                myOrdersRef.addChildEventListener(new ChildEventListener() {
                    String customerName;
                    String order;
                    String foodTruckName;
                    String time;
                    String vendorUID;
                    String customerUID;

                    @Override
                    public void onChildAdded(DataSnapshot dataSnapshot, String prevChildKey) {
                        HashMap<String, Object> values = (HashMap<String, Object>) dataSnapshot.getValue();
                        for (String type : values.keySet()) {
                            if (type.equals("Order")) {
                                this.order = (String) values.get(type);
                            } else if (type.equals("CustomerName")) {
                                this.customerName = (String) values.get(type);
                            } else if (type.equals("FoodTruckName")) {
                                this.foodTruckName = (String) values.get(type);
                            } else if (type.equals("Time")) {
                                this.time = (String) values.get(type);
                            }
                            else if (type.equals("vendorUniqueID")) {
                                this.vendorUID = (String) values.get(type);
                            }
                            else if (type.equals("customerUniqueID")) {
                                this.customerUID = (String) values.get(type);
                            }
                        }
                        //Add as long as is not empty
                        if (!order.equals("")) {
                            Order customerOrder = new Order(customerUID, order, customerName, null, vendorUID);
                            customerOrder.setFoodTruckName(foodTruckName);
                            customerOrder.setTime(time);
                            // eqaulity isn't based on the order itself
                            if (orders.contains(customerOrder)) {
                                orders.remove(customerOrder);
                                orders.add(customerOrder);
                            }
                            else {
                                orders.add(customerOrder);
                            }
                            arrayAdapter.notifyDataSetChanged();
                        }
                    }

                    @Override
                    public void onChildChanged(DataSnapshot dataSnapshot, String prevChildKey) {
//                        HashMap<String, Object> values = (HashMap<String, Object>) dataSnapshot.getValue();
//                        for (String type : values.keySet()) {
//                            if (type.equals("Order")) {
//                                this.order = (String) values.get(type);
//                            } else if (type.equals("CustomerName")) {
//                                this.customerName = (String) values.get(type);
//                            } else if (type.equals("FoodTruckName")) {
//                                this.foodTruckName = (String) values.get(type);
//                            } else if (type.equals("Time")) {
//                                this.time = (String) values.get(type);
//                            }
//                            else if (type.equals("vendorUniqueID")) {
//                                this.vendorUID = (String) values.get(type);
//                            }
//                            else if (type.equals("customerUniqueID")) {
//                                this.customerUID = (String) values.get(type);
//                            }
//                        }
//                        //Add as long as is not empty
//                        if (!order.equals("")) {
//                            Order customerOrder = new Order(customerUID, order, customerName, null, vendorUID);
//                            customerOrder.setFoodTruckName(foodTruckName);
//                            customerOrder.setTime(time);
//                            orders.remove(customerOrder);
//                            arrayAdapter.notifyDataSetChanged();
//                            orders.add(customerOrder);
//                            arrayAdapter.notifyDataSetChanged();
//                        }
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

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String prevChildKey) {
//                HashMap<String, Object> values = (HashMap<String, Object>) dataSnapshot.getValue();
//                String UID = (String) values.get("UniqueID");
//                DatabaseReference myOrdersRef = databaseRef.child(UID).child("MyOrders");
//                myOrdersRef.addChildEventListener(new ChildEventListener() {
//                    String customerName;
//                    String order;
//                    String foodTruckName;
//                    String time;
//
//                    @Override
//                    public void onChildAdded(DataSnapshot dataSnapshot, String prevChildKey) {
//                        HashMap<String, Object> values = (HashMap<String, Object>) dataSnapshot.getValue();
//                        for (String type : values.keySet()) {
//                            if (type.equals("Order")) {
//                                this.order = (String) values.get(type);
//                            } else if (type.equals("CustomerName")) {
//                                this.customerName = (String) values.get(type);
//                            } else if (type.equals("FoodTruckName")) {
//                                this.foodTruckName = (String) values.get(type);
//                            } else if (type.equals("Time")) {
//                                this.time = (String) values.get(type);
//                            }
//                        }
//                        //Add as long as is not empty
//                        if (!order.equals("")) {
//                            Order customerOrder = new Order(null, order, customerName, null, null);
//                            customerOrder.setFoodTruckName(foodTruckName);
//                            customerOrder.setTime(time);
//                            orders.add(customerOrder);
//                            arrayAdapter.notifyDataSetChanged();
//                        }
//                    }
//
//                    @Override
//                    public void onChildChanged(DataSnapshot dataSnapshot, String s) {
//
//                    }
//
//                    @Override
//                    public void onChildRemoved(DataSnapshot dataSnapshot) {
//
//                    }
//
//                    @Override
//                    public void onChildMoved(DataSnapshot dataSnapshot, String s) {
//
//                    }
//
//                    @Override
//                    public void onCancelled(DatabaseError databaseError) {
//
//                    }
//                });
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

    /**
     * custom adapter for menu listview
     */
    public class MyCustomAdapter extends BaseAdapter implements ListAdapter {
        private Context context;

        public MyCustomAdapter(Context context) {
            this.context = context;
        }

        @Override
        public int getCount() {
            return orders.size();
        }

        @Override
        public Object getItem(int pos) {
            return orders.get(pos);
        }

        @Override
        public long getItemId(int pos) {
            return pos;
        }

        /**
         * set content of each feed view item
         * @param position
         * @param convertView
         * @param parent
         * @return
         */
        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {

            TwoLineListItem twoLineListItem;

            if (convertView == null) {
                LayoutInflater inflater = (LayoutInflater) context
                        .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                twoLineListItem = (TwoLineListItem) inflater.inflate(
                        android.R.layout.simple_list_item_2, null);
            } else {
                twoLineListItem = (TwoLineListItem) convertView;
            }

            final Order order = orders.get(position);
            TextView text1 = twoLineListItem.getText1();
            TextView text2 = twoLineListItem.getText2();
            text1.setTextColor(Color.BLACK);
            text2.setTextColor(Color.BLACK);
            text1.setTextSize(24);
            text2.setTextSize(15);

            // put each item on new line
            String customerOrder = order.getCustomerOrder();
            StringBuilder sb = new StringBuilder();

            for (int i = 0; i < customerOrder.length(); i++) {
                if (customerOrder.charAt(i) == '[') {
                    boolean firstSpace = false;
                    boolean secondSpace = false;
                    int j = i + 1;
                    while (j < customerOrder.length() && customerOrder.charAt(j) != '[') {
                        j++;
                    }
                    if (i == 0) {
                        sb.append(customerOrder.substring(i, j));
                    }
                    else {
                        sb.append("<br>" + customerOrder.substring(i, j));
                    }
                }
            }

            // make time look nice ... "dd/MM/yyyy HH:mm:ss"
            String time = order.getTime();
            DateTimeFormatter f = DateTimeFormat.forPattern("dd/MM/yyyy HH:mm:ss");
            DateTime dateTime = f.parseDateTime(order.getTime());
            dateTime.toLocalDateTime();

            order.setFormatStrings("<i><font color=\"#0EBDE8\">" + order.getCustomerName() + "</i>\n ordered: ",
                    sb.toString() + "<br>from <i><font color=\"#0EBDE8\">" + order.getFoodTruckName() + "</i> at " +  order.getTime());

            // use HTML format to make text look nice
            text1.setText(Html.fromHtml(order.getFirstLine()));
            text2.setText(Html.fromHtml(order.getSecondLine()));

            return twoLineListItem;
        }
    }
}
