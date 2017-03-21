package edu.upenn.cis350.foodtruckapp;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

/**
 * Created by desmondhoward on 3/13/17.
 */

public class VendorOrdersActivity extends AppCompatActivity {
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
        list.setDividerHeight(10);
    }

}
