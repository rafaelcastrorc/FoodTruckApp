package edu.upenn.cis350.foodtruckapp;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

public class TopFoodTrucksActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_top_food_trucks);
        ListView list = (ListView) findViewById(R.id.top_food_trucks_list);

        //Pull food trucks from database
        //Get their unique ids
        //uniqueId.getChild("Name Of Food Truck")
        //Todo: Start counter with 0 rating and 0
        //ProcessingRatings pr = new ProcessingRatings();
        //pr.pushRatingToDatabase("K1Z7QIYsM9QtVDMI2hMUnLIGcIy2", 4);
        String[] user_fav_trucks = {"Halal @ 38th & Walnut                             4" +
                "", "Hemo's                                                      5",
                "Magic Carpet                                            3",
                "Yuh Kee's                                                  5",
                "Mexicali                                                     2",
                "Bubble Tea                                               5",
                "The Real Le Ann                                      4",
                "Bento Box                                                3",
                "Casablanca                                             2",
                "Bui's                                                          4"}; // Todo: data to be pulled from Firebase
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
