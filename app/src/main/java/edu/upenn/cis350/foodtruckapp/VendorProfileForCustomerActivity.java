package edu.upenn.cis350.foodtruckapp;

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
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
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

import static edu.upenn.cis350.foodtruckapp.VendorProfileActivity.setListViewHeightBasedOnChildren;

public class VendorProfileForCustomerActivity extends AppCompatActivity {

    private DatabaseReference databaseRef;
    private DatabaseReference vendorRef;
    private DatabaseReference menuRef;
    private ArrayList<MyMenuItem> menu;
    private ListView menuListView;
    private String vendorUniqueID;
    private String customerUniqueID;
    protected ArrayList<Order> orders = new ArrayList<>();
    private String foodtruckName;
    private CustomerOrderMGM customerOrderMGM;

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

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vendor_profile_for_customer);
        customerOrderMGM = new CustomerOrderMGM();

        Intent i = getIntent();
        vendorUniqueID = i.getStringExtra("vendorUniqueID");
        databaseRef = FirebaseDatabase.getInstance().getReference("Users");
        vendorRef = databaseRef.child(vendorUniqueID);

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
        // populate cart w/ pre-existing data



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

//        final DatabaseReference customerRef = databaseRef.child(customerOrderMGM.getUniqueID());
//        DatabaseReference cartRef = customerRef.child("MyOrders").child(vendorUniqueID);
//        cartRef.addChildEventListener(new ChildEventListener() {
//            String item = "";
//            String price = "";
//            @Override
//            public void onChildAdded(DataSnapshot dataSnapshot, String prevChildKey) {
//            }
//
//            @Override
//            public void onChildChanged(DataSnapshot dataSnapshot, String prevChildKey) {
//                if (dataSnapshot.getKey().equals("Order")) {
//                    String order = (String) dataSnapshot.getValue();
//                    StringBuilder sb = new StringBuilder();
//
//                    for (int i = 0; i < order.length(); i++) {
//                        char c = order.charAt(i);
//                        if (i == order.length() - 1) {
//                            sb.append(c);
//                            final String item = sb.toString();
//                            decrMenuItemQuantityByName(item);
//                        }
//                        else if (c != '\n') {
//                            sb.append(c);
//                        }
//                        else {
//                            String item = sb.toString();
//                            decrMenuItemQuantityByName(item);
//                            sb = new StringBuilder();
//                        }
//
//                    }
//                }
//                setListViewHeightBasedOnChildren(menuListView);
//            }
//
//            @Override
//            public void onChildRemoved(DataSnapshot dataSnapshot) {
//                Log.d("key", dataSnapshot.getKey());
//            }
//
//            @Override
//            public void onChildMoved(DataSnapshot dataSnapshot, String prevChildKey) {
//                setListViewHeightBasedOnChildren(menuListView);
//
//            }
//
//            @Override
//            public void onCancelled(DatabaseError databaseError) {
//            }
//        });

        // check to see if an item is in the customer's cart
        final DatabaseReference customerRef = databaseRef.child(customerOrderMGM.getUniqueID());
        DatabaseReference cartRef = customerRef.child("MyOrders").child(vendorUniqueID).child("Order");
        cartRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String order = (String) dataSnapshot.getValue();
                StringBuilder sb = new StringBuilder();
                if (order == null) {
                    return;
                }
                String[] items = order.split("\\r?\\n");
                Log.d("orders", orders.toString());
                for (int i = 0; i < order.length(); i++) {
                    String item = items[i];
                    MyMenuItem menuItem = getItemByName(item);

                }
//                        for (int i = 0; i < order.length(); i++) {
//                            char c = order.charAt(i);
//                            if (i == order.length() - 1) {
//                                sb.append(c);
//                                final String item = sb.toString();
//                                if (item.equals(menuItem.getItem())) {
//                                    menuItem.incrQuantity();
//                                    Log.d("here", "hereerere");
//                                    Log.d("order", order);
//
//                                    myAdapter.notifyDataSetChanged();
//                                }
//                            }
//                            else if (c != '\n') {
//                                sb.append(c);
//                            }
//                            else {
//                                String item = sb.toString();
//                                if (item.equals(menuItem.getItem())) {
//                                    //menuItem.incrQuantity();
//                                    Log.d("else", "elsessdadsa");
//
//                                }
//                                sb = new StringBuilder();
//                            }
//                        }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
        populateVendorPicture();
    }

    MyMenuItem getItemByName(String name) {
        for (MyMenuItem item : menu) {
            if (item.getItem().equals(name)) {
                return item;
            }
        }
        return null;
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

                //Handle TextViews and display string from your list
                final TextView item = (TextView) view.findViewById(R.id.menu_item);
                final MyMenuItem menuItem = menu.get(position);
                item.setText(menuItem.getItem());

                final TextView price = (TextView) view.findViewById(R.id.menu_item_price);
                price.setText(menuItem.getPrice());

                final TextView itemCount = (TextView) view.findViewById(R.id.menu_item_quantity);
                itemCount.setText(Integer.toString(menuItem.getQuantity()));

                //Handle buttons and add onClickListeners
                Button deleteButton = (Button)view.findViewById(R.id.delete_button);
                Button addButton = (Button) view.findViewById(R.id.add_button);

                deleteButton.setOnClickListener(new View.OnClickListener(){
                    @Override
                    public void onClick(View v) {
                        if (menuItem.getQuantity() == 0) {
                            return;
                        }
                        menuItem.decrQuantity();
                        itemCount.setText(Integer.toString(menuItem.getQuantity()));
                        notifyDataSetChanged();
                    }
                });
                addButton.setOnClickListener(new View.OnClickListener(){
                    @Override
                    public void onClick(View v) {
                        //menuItem.incrQuantity();
                        //itemCount.setText(Integer.toString(menuItem.getQuantity()));
                        final DatabaseReference customerRef = databaseRef.child(customerOrderMGM.getUniqueID());
                        CustomerOrderMGM customerOrderMGM = new CustomerOrderMGM();
                        customerOrderMGM.setVendorUniqueID(vendorUniqueID);
                        customerOrderMGM.sendOrderToCart(item.getText().toString(), foodtruckName,
                                Double.parseDouble(price.getText().toString()));
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
        // set arrayadapter for menu list view
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

//                // check to see if an item is in the customer's cart
//                final DatabaseReference customerRef = databaseRef.child(customerOrderMGM.getUniqueID());
//                DatabaseReference cartRef = customerRef.child("MyOrders").child(vendorUniqueID).child("Order");
//                cartRef.addValueEventListener(new ValueEventListener() {
//                    @Override
//                    public void onDataChange(DataSnapshot dataSnapshot) {
//                        String order = (String) dataSnapshot.getValue();
//                        StringBuilder sb = new StringBuilder();
//                        if (order == null) {
//                            return;
//                        }
//                        String[] orders = order.split("\\r?\\n");
//                        Log.d("orders", orders.toString());
//                        for (int i = 0; i < order.length(); i++) {
//
//                        }
////                        for (int i = 0; i < order.length(); i++) {
////                            char c = order.charAt(i);
////                            if (i == order.length() - 1) {
////                                sb.append(c);
////                                final String item = sb.toString();
////                                if (item.equals(menuItem.getItem())) {
////                                    menuItem.incrQuantity();
////                                    Log.d("here", "hereerere");
////                                    Log.d("order", order);
////
////                                    myAdapter.notifyDataSetChanged();
////                                }
////                            }
////                            else if (c != '\n') {
////                                sb.append(c);
////                            }
////                            else {
////                                String item = sb.toString();
////                                if (item.equals(menuItem.getItem())) {
////                                    //menuItem.incrQuantity();
////                                    Log.d("else", "elsessdadsa");
////
////                                }
////                                sb = new StringBuilder();
////                            }
////                        }
//                    }
//
//                    @Override
//                    public void onCancelled(DatabaseError databaseError) {
//                    }
//                });
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

//        final DatabaseReference customerRef = databaseRef.child(customerOrderMGM.getUniqueID());
//        DatabaseReference cartRef = customerRef.child("MyOrders").child(vendorUniqueID).child("Order");
//        cartRef.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot dataSnapshot) {
//                String order = (String) dataSnapshot.getValue();
//                StringBuilder sb = new StringBuilder();
//                if (order == null) {
//                    return;
//                }
//                for (int i = 0; i < order.length(); i++) {
//                    char c = order.charAt(i);
//                    if (i == order.length() - 1) {
//                        sb.append(c);
//                        final String item = sb.toString();
//                        if (item.equals(menuItem.getItem())) {
//                            menuItem.incrQuantity();
//                            myAdapter.notifyDataSetChanged();
//                        }
//                    } else if (c != '\n') {
//                        sb.append(c);
//                    } else {
//                        String item = sb.toString();
//                        if (item.equals(menuItem.getItem())) {
//                            menuItem.incrQuantity();
//                        }
//                        sb = new StringBuilder();
//                    }
//                }
//            }
//        });
    }

    private void updateTotal(){
        TextView total = (TextView)findViewById(R.id.total_shopping_cart);
        total.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(VendorProfileForCustomerActivity.this, Cart.class);
                startActivity(i);
            }
        });
        double result = 0.0;
        for (Order order: orders) {
            result = result + order.getPrice();
        }
        NumberFormat formatter = new DecimalFormat("#0.00");

        total.setText("$"+ formatter.format(result));
    }

    public void sendOrderToVendor_onClick(View v) {
        customerOrderMGM.setVendorUniqueID(vendorUniqueID);
        Button currButton = (Button) findViewById(R.id.sendOrderToVendor);
        //Add here the order, the name of food truck, and the cost of the item
        customerOrderMGM.sendOrderToCart("Chocolates", "Insert the name of the food truck here", 10.50);
    }
}
