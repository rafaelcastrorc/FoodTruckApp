package edu.upenn.cis350.foodtruckapp;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class VendorProfileForCustomer extends AppCompatActivity {

    private FirebaseDatabase database;
    private FirebaseAuth mAuth;
    private DatabaseReference databaseRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vendor_profile_for_customer);

        Intent i = getIntent();
        String vendorID = i.getStringExtra("vendorInstanceID");
        Log.d("ID", vendorID);
        databaseRef = FirebaseDatabase.getInstance().getReference("Users");
        DatabaseReference foodtruck = databaseRef.child(vendorID).child("Name Of Food Truck");
        Log.d("ID", foodtruck.toString());


        foodtruck.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.d("KEY", dataSnapshot.getKey());
                String name = dataSnapshot.getValue().toString();
                Log.d("NAME", name);

                getSupportActionBar().setTitle(name);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });

        //generate list
        ArrayList<String> list = new ArrayList<String>();
        list.add("a bunch of texttdtfsdffsdf\nsfsfdsfdfdfsdf");
        list.add("item2");

        //instantiate custom adapter
        MyCustomAdapter adapter = new MyCustomAdapter(list, this);

        //handle listview and assign adapter
        ListView menu = (ListView) findViewById(R.id.menu_listview);
        menu.setAdapter(adapter);
    }

    public class MyCustomAdapter extends BaseAdapter implements ListAdapter {
        private ArrayList<String> list = new ArrayList<String>();
        private Context context;
        int quantity = 0;

        public MyCustomAdapter(ArrayList<String> list, Context context) {
            this.list = list;
            this.context = context;
        }

        @Override
        public int getCount() {
            return list.size();
        }

        @Override
        public Object getItem(int pos) {
            return list.get(pos);
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
                view = inflater.inflate(R.layout.menu_item_style, null);
            }

            //Handle TextViews and display string from your list
            TextView listItem = (TextView) view.findViewById(R.id.menu_item);
            listItem.setText(list.get(position));

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

            return view;
        }
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
}
