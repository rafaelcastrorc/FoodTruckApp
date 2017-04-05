package edu.upenn.cis350.foodtruckapp;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
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

import static edu.upenn.cis350.foodtruckapp.VendorProfileActivity.setListViewHeightBasedOnChildren;

public class VendorProfileForCustomer extends AppCompatActivity {

    private FirebaseDatabase database;
    private FirebaseAuth mAuth;
    private DatabaseReference databaseRef;
    private DatabaseReference vendorRef;
    private DatabaseReference menuRef;
    private ArrayList<MyMenuItem> menu;
    private ListView menuListView;
    private String vendorUniqueID;
    protected ArrayList<Order> orders = new ArrayList<>();




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
                Intent i = new Intent(VendorProfileForCustomer.this, Cart.class);
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

        Intent i = getIntent();
        vendorUniqueID = i.getStringExtra("vendorUniqueID");
        databaseRef = FirebaseDatabase.getInstance().getReference("Users");
        vendorRef = databaseRef.child(vendorUniqueID);

        DatabaseReference foodtruck = vendorRef.child("Name Of Food Truck");

        foodtruck.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String name = dataSnapshot.getValue().toString();
                getSupportActionBar().setTitle(name);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });

        menu = new ArrayList<MyMenuItem>();
        final MyCustomAdapter myAdapter = new MyCustomAdapter(this);

        menuListView = (ListView) findViewById(R.id.cust_menu);
        menuListView.setAdapter(myAdapter);

        menuRef = vendorRef.child("Menu");
        menuRef.addChildEventListener(new ChildEventListener() {
            String item = "";
            String price = "";
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String prevChildKey) {
                MyMenuItem menuItem = new MyMenuItem(dataSnapshot.getKey(),
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

        DatabaseReference hoursRef = vendorRef.child("Hours");
        hoursRef.child("OpenWeekdayTime").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                TextView textView = (TextView) findViewById(R.id.customer_vendor_open_weekday_time);
                textView.setText((String) dataSnapshot.getValue());
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
                textView.setText((String) dataSnapshot.getValue());
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
                textView.setText((String) dataSnapshot.getValue());
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
                textView.setText((String) dataSnapshot.getValue());
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


        //////////////////////////////////////////////////////////

        //Handle total bar that is displayed
        mAuth = FirebaseAuth.getInstance();
        DatabaseReference myOrdersRef = databaseRef.child(mAuth.getCurrentUser().getUid()).child("MyOrders");
        myOrdersRef.addChildEventListener(new ChildEventListener() {
            public String vendorUniqueID = "";
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
                TextView item = (TextView) view.findViewById(R.id.menu_item);
                MyMenuItem menuItem = menu.get(position);
                item.setText(menuItem.getItem());

                TextView price = (TextView) view.findViewById(R.id.menu_item_price);
                price.setText(menuItem.getPrice());

                final TextView itemCount = (TextView) view.findViewById(R.id.menu_item_quantity);

                //Handle buttons and add onClickListeners
                Button deleteButton = (Button)view.findViewById(R.id.delete_button);
                Button addButton = (Button) view.findViewById(R.id.add_button);

                deleteButton.setOnClickListener(new View.OnClickListener(){
                    @Override
                    public void onClick(View v) {
                        if (quantity == 0) {
                            return;
                        }
                        quantity--;
                        itemCount.setText(Integer.toString(quantity));
                        notifyDataSetChanged();
                    }
                });
                addButton.setOnClickListener(new View.OnClickListener(){
                    @Override
                    public void onClick(View v) {
                        quantity++;
                        itemCount.setText(Integer.toString(quantity));
                        notifyDataSetChanged();
                    }
                });
            }

            return view;
        }
    }


    private void updateTotal(){
        TextView total = (TextView)findViewById(R.id.total_shopping_cart);
        total.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(VendorProfileForCustomer.this, Cart.class);
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

//    private class ViewHolder {
//        private TextView menuItem;
//        private TextView menuItemQuantity;
//
//        public ViewHolder() {
//           // this.menuItem = menuItem;
//            //this.menuItemQuantity = menuItemQuantity;
//        }
//
//        public TextView getMenuItem() {
//            return menuItem;
//        }
//        public TextView getMenuItemQuantity() {
//            return menuItemQuantity;
//        }
//    }


    //Todo: Add the + to all orders.
    public void sendOrderToVendor_onClick(View v) {
        CustomerOrderMGM customerOrderMGM = new CustomerOrderMGM();
        customerOrderMGM.setVendorUniqueID(vendorUniqueID);
        Button currButton = (Button) findViewById(R.id.sendOrderToVendor);
        //Add here the order, the name of food truck, and the cost of the item
        customerOrderMGM.sendOrderToCart("Chocolates", "Insert the name of the food truck here", 10.50);

    }
}
