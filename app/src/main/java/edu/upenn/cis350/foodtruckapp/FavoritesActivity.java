package edu.upenn.cis350.foodtruckapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

public class FavoritesActivity extends AppCompatActivity {

    private FirebaseDatabase database;
    private FirebaseAuth mAuth;
    private DatabaseReference databaseRef;
    private HashMap<String, String> vendors = new HashMap<String, String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorites);
        ListView list = (ListView) findViewById(R.id.favs_list);
        String[] user_fav_trucks = {"Bui's", "Hemo's", "Magic Carpet", "Yuh Kee's", "Mexicali"}; // data to be pulled from Firebase
        list.setAdapter(new ArrayAdapter<String>(this, R.layout.list_item_style,
                user_fav_trucks) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View view = super.getView(position, convertView, parent);
                TextView textView = ((TextView) view.findViewById(android.R.id.text1));
                textView.setHeight(200);
                return view;
            }
        });
        databaseRef = FirebaseDatabase.getInstance().getReference("Users");
        databaseRef.addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        getAllVendors((Map<String,Object>) dataSnapshot.getValue());
                    }
                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                    }
                });

        list.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position,
                                    long id) {
                Intent intent = new Intent(FavoritesActivity.this, VendorProfileForCustomer.class);
                final TextView selectedChild = (TextView) parent.getChildAt(position);

                String selectedVendorID = (String) selectedChild.getText();
                intent.putExtra("vendorInstanceID", vendors.get(selectedVendorID));
                startActivity(intent);
            }
        });
        list.setDividerHeight(10);
    }

    void getAllVendors(Map<String,Object> users) {
        for (Map.Entry<String, Object> entry : users.entrySet()){
            Map user = (Map) entry.getValue();
            vendors.put((String) user.get("Name Of Food Truck"), (String) user.get("UniqueID"));
        }
    }
}
