package edu.upenn.cis350.foodtruckapp;


import android.util.Log;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import static edu.upenn.cis350.foodtruckapp.VendorProfileActivity.setListViewHeightBasedOnChildren;

public class VendorProfileForCustomerActivity extends AppCompatActivity {
    private DatabaseReference databaseRef;
    private DatabaseReference vendorRef;
    private DatabaseReference menuRef;
    private ArrayList<MyMenuItem> menu;
    private ListView menuListView;
    private String vendorUniqueID;
    private String customerUniqueID;
    private String foodtruckName;
    private CustomerOrderMGM customerOrderMGM;
    ArrayList<Order> orders = new ArrayList<>();


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
                Intent i = new Intent(VendorProfileForCustomerActivity.this, Cart.class);
                startActivity(i);
                return true;
            case R.id.home_button:
                Intent j = new Intent(VendorProfileForCustomerActivity.this, CustomerMainMenuActivity.class);
                startActivity(j);
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vendor_profile_for_customer);
        customerOrderMGM = new CustomerOrderMGM();
        customerUniqueID = customerOrderMGM.getUniqueID();

        Intent i = getIntent();
        vendorUniqueID = i.getStringExtra("vendorUniqueID");
        databaseRef = FirebaseDatabase.getInstance().getReference("Users");
        vendorRef = databaseRef.child(vendorUniqueID);
        Log.d("ID of MyTruck", vendorUniqueID);

        DatabaseReference foodtruck = vendorRef.child("Name Of Food Truck");
        foodtruck.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                foodtruckName = dataSnapshot.getValue().toString();
                getSupportActionBar().setTitle(foodtruckName);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
        populateMenu();

        // get "Hours" data for vendor
        DatabaseReference hoursRef = vendorRef.child("Hours");
        hoursRef.child("OpenWeekdayTime").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                TextView textView = (TextView) findViewById(R.id.customer_vendor_open_weekday_time);
                textView.setText(dataSnapshot.getValue().toString());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });

        hoursRef.child("OpenWeekdayPeriod").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                TextView textView = (TextView) findViewById(R.id.customer_vendor_open_weekday_period);
                textView.setText((String) dataSnapshot.getValue());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });

        hoursRef.child("CloseWeekdayTime").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                TextView textView = (TextView) findViewById(R.id.customer_vendor_close_weekday_time);
                textView.setText(dataSnapshot.getValue().toString());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });

        hoursRef.child("CloseWeekdayPeriod").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                TextView textView = (TextView) findViewById(R.id.customer_vendor_close_weekday_period);
                textView.setText((String) dataSnapshot.getValue());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });

        hoursRef.child("OpenWeekendTime").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                TextView textView = (TextView) findViewById(R.id.customer_vendor_open_weekend_time);
                textView.setText(dataSnapshot.getValue().toString());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });

        hoursRef.child("OpenWeekendPeriod").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                TextView textView = (TextView) findViewById(R.id.customer_vendor_open_weekend_period);
                textView.setText((String) dataSnapshot.getValue());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });

        hoursRef.child("CloseWeekendTime").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                TextView textView = (TextView) findViewById(R.id.customer_vendor_close_weekend_time);
                textView.setText(dataSnapshot.getValue().toString());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });

        hoursRef.child("CloseWeekendPeriod").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                TextView textView = (TextView) findViewById(R.id.customer_vendor_close_weekend_period);
                textView.setText((String) dataSnapshot.getValue());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });

        // update current quantities
        final DatabaseReference customerRef = databaseRef.child(customerOrderMGM.getUniqueID());
        DatabaseReference cartRef = customerRef.child("MyOrders").child(vendorUniqueID).child("Order");
        cartRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String order = (String) dataSnapshot.getValue();
                TreeMap<String, Integer> currentItemsInCart = customerOrderMGM.ordersParser(order);
                for (Map.Entry<String, Integer> entry : currentItemsInCart.entrySet()) {
                    setQuantityByName(entry.getKey(), entry.getValue());
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });

        // populate initial quantities
        customerRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String prevChildKey) {
                if (dataSnapshot.getKey().equals("MyOrders")) {
                    Map<String, Object> orderInfo = (Map<String, Object>) dataSnapshot.getValue();
                    for (Map.Entry<String, Object> entry : orderInfo.entrySet()) {
                        if (entry.getKey().equals("Order")) {
                            String order = (String) entry.getValue();
                            TreeMap<String, Integer> currentItemsInCart = customerOrderMGM.ordersParser(order);
                            for (Map.Entry<String, Integer> item : currentItemsInCart.entrySet()) {
                                setQuantityByName(item.getKey(), item.getValue());
                            }
                        }
                    }
                }
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

        populateVendorPicture();


        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        DatabaseReference myOrdersRef = databaseRef.child(mAuth.getCurrentUser().getUid()).child("MyOrders");

        myOrdersRef.addChildEventListener(new ChildEventListener() {

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
                HashMap<String, Object> values = (HashMap<String, Object>) dataSnapshot.getValue();
                for (String type : values.keySet()) {
                    if (type.equals("CustomerInstanceId")) {
                        this.instanceId = (String) values.get(type);
                    } else if (type.equals("Order")) {
                        this.order = (String) values.get(type);
                    } else if (type.equals("CustomerName")) {
                        this.customerName = (String) values.get(type);
                    } else if (type.equals("PushId")) {
                        this.pushId = (String) values.get(type);
                    } else if (type.equals("vendorUniqueID")) {
                        this.vendorUniqueID = (String) values.get(type);
                    } else if (type.equals("FoodTruckName")) {
                        this.foodTruckName = (String) values.get(type);
                    } else if (type.equals("Price")) {
                        try {
                            this.price = (Double) values.get(type);
                        }
                        catch (ClassCastException e) {
                            Long l = new Long((Long) values.get(type));
                            this.price= l.doubleValue();

                        }
                    } else if (type.equals("Submitted")) {
                        String choice = (String) values.get(type);
                        if (choice.equals("true")) {
                            status = true;
                        }
                    }
                }
                Order customerOrder = new Order(instanceId, order, customerName, pushId, vendorUniqueID);
                customerOrder.setStatus(status);
                customerOrder.setFoodTruckName(foodTruckName);
                customerOrder.setPrice(price);
                orders.add(customerOrder);
                updateTotal();

            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String prevChildKey) {
                boolean status = false;
                HashMap<String, Object> values = (HashMap<String, Object>) dataSnapshot.getValue();
                for (String type : values.keySet()) {

                    if (type.equals("CustomerInstanceId")) {
                        this.instanceId = (String) values.get(type);

                    } else if (type.equals("Order")) {
                        this.order = (String) values.get(type);
                    } else if (type.equals("CustomerName")) {
                        this.customerName = (String) values.get(type);
                    } else if (type.equals("PushId")) {
                        this.pushId = (String) values.get(type);
                    } else if (type.equals("vendorUniqueID")) {
                        this.vendorUniqueID = (String) values.get(type);
                    } else if (type.equals("FoodTruckName")) {
                        this.foodTruckName = (String) values.get(type);
                    } else if (type.equals("Price")) {
                        try {
                            this.price = (Double) values.get(type);
                        }
                        catch (ClassCastException e) {
                            Long l = new Long((Long) values.get(type));
                            this.price= l.doubleValue();

                        }
                    } else if (type.equals("Submitted")) {
                        String choice = (String) values.get(type);
                        if (choice.equals("true")) {
                            status = true;
                        }
                    }

                }
                Order customerOrder = new Order(instanceId, order, customerName, pushId, vendorUniqueID);

                //deletes old order
                orders.remove(customerOrder);

                //adds new order at end of queue

                customerOrder.setStatus(status);
                customerOrder.setFoodTruckName(foodTruckName);
                customerOrder.setPrice(price);

                orders.add(customerOrder);
                updateTotal();

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

                HashMap<String, Object> values = (HashMap<String, Object>) dataSnapshot.getValue();
                for (String type : values.keySet()) {

                    if (type.equals("CustomerInstanceId")) {
                        this.instanceId = (String) values.get(type);

                    } else if (type.equals("Order")) {
                        this.order = (String) values.get(type);
                    } else if (type.equals("CustomerName")) {
                        this.customerName = (String) values.get(type);
                    } else if (type.equals("PushId")) {
                        this.pushId = (String) values.get(type);
                    } else if (type.equals("vendorUniqueID")) {
                        this.vendorUniqueID = (String) values.get(type);
                    }

                }
                Order customerOrder = new Order(instanceId, order, customerName, pushId, vendorUniqueID);
                orders.remove(customerOrder);
                updateTotal();

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String prevChildKey) {
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });

    }

    void setQuantityByName(String name, int quantity) {
        for (MyMenuItem item : menu) {
            if (item.getItem().equals(name)) {
                item.setQuantity(quantity);
            }
        }
    }

    // get vendor picture
    void populateVendorPicture() {
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReference();
        StorageReference imagesRef = storageRef.child("images");
        StorageReference vendorStorageRef = imagesRef.child(vendorUniqueID);
        final long ONE_MEGABYTE = 1024 * 1024;
        vendorStorageRef.getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
            @Override
            public void onSuccess(byte[] bytes) {
                Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                BitmapDrawable drawableBitmap = new BitmapDrawable(
                        getApplicationContext().getResources(), bitmap);
                ImageView vendorImage = (ImageView) findViewById(R.id.cust_vendor_profile_image);
                vendorImage.setBackground(drawableBitmap);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                Log.d("hey", "hey");
            }
        });
    }

    // custom adapter for menu listview
    public class MyCustomAdapter extends BaseAdapter implements ListAdapter {
        private Context context;
        int quantity = 0;

        public MyCustomAdapter(Context context) {
            this.context = context;
        }

        @Override
        public int getCount() {
            return menu.size();
        }

        @Override
        public Object getItem(int pos) {
            return menu.get(pos);
        }

        @Override
        public long getItemId(int pos) {
            return 0;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            View view = convertView;
            if (view == null) {
                LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                view = inflater.inflate(R.layout.customer_menu_item_style, null);

                // fill textviews w/ data
                final MyMenuItem menuItem = menu.get(position);
                final TextView item = (TextView) view.findViewById(R.id.menu_item);
                item.setText(menuItem.getItem());

                final TextView price = (TextView) view.findViewById(R.id.menu_item_price);
                price.setText(menuItem.getPrice());

                final TextView itemCount = (TextView) view.findViewById(R.id.menu_item_quantity);
                itemCount.setText(Integer.toString(menuItem.getQuantity()));

                // reduce quantity of item & notify cart
                Button deleteButton = (Button) view.findViewById(R.id.delete_button);
                Button addButton = (Button) view.findViewById(R.id.add_button);
                deleteButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        int quantity = menuItem.getQuantity();
                        if (quantity == 0) {
                            return;
                        }
                        CustomerOrderMGM customerOrderMGM = new CustomerOrderMGM();
                        customerOrderMGM.setVendorUniqueID(vendorUniqueID);
                        customerOrderMGM.setContext(getApplicationContext());
                        customerOrderMGM.removeOrderFromCart(item.getText().toString(), foodtruckName,
                                Double.parseDouble(price.getText().toString()));

                        menuItem.setQuantity(quantity - 1);
                        itemCount.setText(Integer.toString(menuItem.getQuantity()));
                        notifyDataSetChanged();
                    }
                });
                // increase quantity of item & notify cart
                addButton.setOnClickListener(new View.OnClickListener(){
                    @Override
                    public void onClick(View v) {
                        int quantity = menuItem.getQuantity();
                        if (quantity == 9) {
                            return;
                        }
                        CustomerOrderMGM customerOrderMGM = new CustomerOrderMGM();
                        customerOrderMGM.setVendorUniqueID(vendorUniqueID);
                        customerOrderMGM.setContext(getApplicationContext());
                        customerOrderMGM.addOrderToCart(item.getText().toString(), foodtruckName,
                                Double.parseDouble(price.getText().toString()));

                        menuItem.setQuantity(quantity + 1);
                        itemCount.setText(Integer.toString(menuItem.getQuantity()));
                        notifyDataSetChanged();
                    }
                });
            }
            else {
                final TextView item = (TextView) view.findViewById(R.id.menu_item);
                final MyMenuItem menuItem = menu.get(position);
                item.setText(menuItem.getItem());

                final TextView price = (TextView) view.findViewById(R.id.menu_item_price);
                price.setText(menuItem.getPrice());

                final TextView itemCount = (TextView) view.findViewById(R.id.menu_item_quantity);
                itemCount.setText(Integer.toString(menuItem.getQuantity()));
            }
            return view;
        }
    }

    private void populateMenu() {
        menu = new ArrayList<MyMenuItem>();
        final MyCustomAdapter myAdapter = new MyCustomAdapter(this);
        menuListView = (ListView) findViewById(R.id.cust_menu);
        menuListView.setAdapter(myAdapter);

        menuRef = vendorRef.child("Menu");
        menuRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String prevChildKey) {
                final MyMenuItem menuItem = new MyMenuItem(dataSnapshot.getKey(),
                        (String) dataSnapshot.getValue());

                if (!menu.contains(menuItem)) {
                    menu.add(menuItem);
                }
                myAdapter.notifyDataSetChanged();
                setListViewHeightBasedOnChildren(menuListView);
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String prevChildKey) {
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                MyMenuItem menuItem = new MyMenuItem(
                        (String) dataSnapshot.getKey(), (String) dataSnapshot.getValue());
                menu.remove(menuItem);
                myAdapter.notifyDataSetChanged();
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String prevChildKey) {
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

    private void updateTotal() {
        TextView total = (TextView) findViewById(R.id.total_shopping_cart);
        total.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(VendorProfileForCustomerActivity.this, Cart.class);
                startActivity(i);
            }
        });
        double result = 0.0;
        for (Order order : orders) {
            result = result + order.getPrice();
        }
        NumberFormat formatter = new DecimalFormat("#0.00");

        total.setText("$" + formatter.format(result));
    }


    
    //Todo: For Desmond
//To add item to cart
       // CustomerOrderMGM customerOrderMGM = new CustomerOrderMGM();
       // customerOrderMGM.setVendorUniqueID(vendorUniqueID);
//        customerOrderMGM.addOrderToCart("Candies", "Insert the name of the food truck here", 10.50);

 //To remove item
       // CustomerOrderMGM customerOrderMGM = new CustomerOrderMGM();
       // customerOrderMGM.setVendorUniqueID(vendorUniqueID);
        //customerOrderMGM.removeOrderFromCart("Candies", "Insert the name of the food truck here", 10.50);

//To parse the order String
// CustomerOrderMGM customerOrderMGM = new CustomerOrderMGM();
// customerOrderMGM.setVendorUniqueID(vendorUniqueID);
//customerOrderMGM.ordersParser("[1] Chocolate. \n");


    public void submitReview(View v){
        DatabaseReference reviewRef = vendorRef.child("Reviews");

        EditText reviewText = (EditText) findViewById(R.id.writeReview);
        String review = reviewText.getText().toString();

        reviewRef.child(customerUniqueID).setValue(review);

          //customerUniqueID needs to be set somewhere
          //How to notify vendor of new update?
    }

    public void addRatingOf1(View v){
        addRating(1);
    }

    public void addRatingOf2(View v){
        addRating(2);
    }

    public void addRatingOf3(View v){
        addRating(3);
    }

    public void addRatingOf4(View v){
        addRating(4);
    }



   private void addRating(Integer rating){
       Log.d("MyTruck", "in this bitch");



       final DatabaseReference avgRatingRef = vendorRef.child("Average Rating");
       Log.d("MyTruck", "bitch1");
       final DatabaseReference totalRatingsRef = vendorRef.child("Total Ratings");
       Log.d("MyTruck", "bitch2");


       final Integer userRating = rating;

        final Double[] avgRating = new Double[1];
       Log.d("MyTruck", "bitch3");
        final Integer[] totalRatings = new Integer[1];
       Log.d("MyTruck", "bitch4");

        avgRatingRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                try {
                    Log.d("MyTruck", "bitch5");
                    avgRating[0] = (Double) dataSnapshot.getValue();
                    Log.d("MyTruckAvgRating", ""+avgRating[0]);
                }
                catch (ClassCastException e){
                    Log.d("MyTruck", "bitch6");
                    Long temp = (Long) dataSnapshot.getValue();
                    avgRating[0] = temp.doubleValue();
                    Log.d("MyTruckAvgRating", ""+avgRating[0]);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        totalRatingsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                try {
                    Log.d("MyTruck", "bitch7");
                    totalRatings[0] = (Integer) dataSnapshot.getValue();
                    Log.d("MyTruckTotalRatings", ""+ totalRatings[0]);
                }
                catch (ClassCastException e){
                    Log.d("MyTruck", "bitch8");
                    Long temp = (Long) dataSnapshot.getValue();
                    totalRatings[0] = temp.intValue();
                    Log.d("MyTruckTotalRatings", ""+ totalRatings[0]);
                }

                Double newRating = (avgRating[0] * totalRatings[0] + userRating)/(totalRatings[0] + 1);
                Integer newTotalRatings = totalRatings[0] + 1;

                Log.d("MyTrucknewRating", ""+ newRating);
                Log.d("MyTrucknewTotalRatings", ""+ newTotalRatings);

                avgRatingRef.setValue(newRating);
                totalRatingsRef.setValue(newTotalRatings);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }


}
