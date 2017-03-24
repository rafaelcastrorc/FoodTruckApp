package edu.upenn.cis350.foodtruckapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.Arrays;

public class NearMeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ftnear_me);

        final ListView list = (ListView) findViewById(R.id.favs_list);

        // data to be pulled from Firebase
        String[] user_near_trucks = {"Bui's", "Yasmin", "Hemo's", "Magic Carpet", "Yuh Kee's", "Mexicali",
                "Magic Carpet", "Real Lee An's", "Lee An's"};
        Arrays.sort(user_near_trucks);

        list.setAdapter(new ArrayAdapter<String>(this, R.layout.list_item_style,
                user_near_trucks) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View view = super.getView(position, convertView, parent);
                TextView textView = ((TextView) view.findViewById(android.R.id.text1));
                textView.setHeight(200);
                return view;
            }
        });


        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, final View view,
                                    int position, long id) {
                Intent truckIntent = new Intent(NearMeActivity.this, TruckActivity.class);
                TextView textView = (TextView) list.getChildAt(position);
                String truckName = textView.getText().toString();
                truckIntent.putExtra("truckName", truckName);
                startActivity(truckIntent);
            }

        });

        list.setDividerHeight(10);
    }
}
