package edu.upenn.cis350.foodtruckapp;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

/**
 * Created by saume on 4/7/2017.
 */

public class CustomTruckListAdapter extends ArrayAdapter<String> {

    private HashMap<String, String> vendors = new HashMap<String, String>();

    public CustomTruckListAdapter(Context context, String[] trucks){
        super(context, R.layout.list_item_style, trucks);
    }

    public View getView(int position, View convertView, ViewGroup parent){
        LayoutInflater truckInflater = LayoutInflater.from(getContext());
        View customView = truckInflater.inflate(R.layout.list_item_style, parent, false);

        final String singleTruckItem = getItem(position);
        TextView textView = (TextView) customView.findViewById(R.id.truckName);
        Button button = (Button) customView.findViewById(R.id.fav);



        button.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                DatabaseReference databaseRef = FirebaseDatabase.getInstance().getReference("Users");
                FirebaseAuth mAuth;
                mAuth = FirebaseAuth.getInstance();
                databaseRef.child(mAuth.getCurrentUser().getUid()).child("Favorites").child(singleTruckItem).setValue(singleTruckItem);
            }
        });

        textView.setText(singleTruckItem);
        return customView;
    }
}
