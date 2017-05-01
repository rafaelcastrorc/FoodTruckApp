package edu.upenn.cis350.foodtruckapp;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.SearchView;
import android.view.View;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ShareFavoritesActivity extends AppCompatActivity {

    private DatabaseReference databaseRef;
    final Map<CheckBox, String> userCheckboxesAndEmails = new HashMap<>();

    String MyFavorites;

    /**
     * create menu bar
     * @param menu
     * @return
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.shopping_cart, menu);
        return true;
    }

    /**
     * start activities selected from menu bar
     * @param item
     * @return
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.shopping_cart_button:

                Intent i = new Intent(ShareFavoritesActivity.this, Cart.class);
                startActivity(i);
                return true;
            case R.id.home_button:
                Intent j = new Intent(ShareFavoritesActivity.this, CustomerMainMenuActivity.class);
                startActivity(j);
                return true;
            case R.id.search_button_menu:
                Intent x = new Intent(ShareFavoritesActivity.this, SearchFoodActivity.class);
                startActivity(x);

            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);
        }
    }

    /**
     * usual onCreate and also handles email sharing
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_share_favorites);
        MyFavorites =  getIntent().getExtras().getString("MyFavs");

        databaseRef = FirebaseDatabase.getInstance().getReference("Users");
        final LinearLayout usersLayout = (LinearLayout) findViewById(R.id.users);

        SearchView sv = (SearchView) findViewById(R.id.searchForUsers);

        sv.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {

                final String queryUsable = query;
                databaseRef.addValueEventListener(new ValueEventListener() {


                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for (CheckBox c:userCheckboxesAndEmails.keySet()){
                            usersLayout.removeView(c);
                        }
                        userCheckboxesAndEmails.clear();
                        Iterable<DataSnapshot> users = dataSnapshot.getChildren();
                        for (DataSnapshot user : users) {
                            DataSnapshot name = user.child("Name");
                            DataSnapshot email = user.child("Email");
                            String userName = name.getValue(String.class);
                            String userEmail = email.getValue(String.class);

                            if (userName.equals(queryUsable)){
                                CheckBox userToEmail = new CheckBox(getApplicationContext());
                                userToEmail.setText(userName);
                                userToEmail.setTextColor(getResources().getColor(R.color.BLACK));
                                int[][] states =  {{android.R.attr.state_checked},{-android.R.attr.state_checked}};
                                int color[] = {getResources().getColor(R.color.BLACK),getResources().getColor(R.color.BLACK)};
                                userToEmail.setButtonTintList(new ColorStateList(states,color));
                                userCheckboxesAndEmails.put(userToEmail, userEmail );
                                usersLayout.addView(userToEmail);

                            }

                        }

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }


                });
                return true; // is it okay to return true on default?
            }

            @Override
            public boolean onQueryTextChange(String newText) {

                final String queryUsable = newText;
                databaseRef.addValueEventListener(new ValueEventListener() {


                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for (CheckBox c:userCheckboxesAndEmails.keySet()){
                            usersLayout.removeView(c);
                        }
                        userCheckboxesAndEmails.clear();
                        Iterable<DataSnapshot> users = dataSnapshot.getChildren();
                        for (DataSnapshot user : users) {
                            DataSnapshot name = user.child("Name");
                            DataSnapshot email = user.child("Email");
                            String userName = name.getValue(String.class);
                            String userEmail = email.getValue(String.class);

                            if (userName.equals(queryUsable)){
                                CheckBox userToEmail = new CheckBox(getApplicationContext());
                                userToEmail.setText(userName);
                                userToEmail.setTextColor(getResources().getColor(R.color.BLACK));
                                int[][] states =  {{android.R.attr.state_checked},{-android.R.attr.state_checked}};
                                int color[] = {getResources().getColor(R.color.BLACK),getResources().getColor(R.color.BLACK)};
                                userToEmail.setButtonTintList(new ColorStateList(states,color));
                                userCheckboxesAndEmails.put(userToEmail, userEmail);
                                usersLayout.addView(userToEmail);

                            }
                        }

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }


                });
                return true; // is it okay to return true on default?
            }

        });

    }

    /**
     * sends email to selected user with favorites
     * @param v
     */
    public void shareFavorites(View v){
            for (Map.Entry<CheckBox,String> c: userCheckboxesAndEmails.entrySet()){
                if (c.getKey().isChecked()){
                    String email = c.getValue();

                    Intent i = new Intent(Intent.ACTION_SEND);
                    i.setType("message/rfc822");
                    i.putExtra(Intent.EXTRA_EMAIL  , new String[]{email});
                    i.putExtra(Intent.EXTRA_SUBJECT, "My Favorite Food Trucks");
                    i.putExtra(Intent.EXTRA_TEXT   , MyFavorites);
                    try {
                        startActivity(Intent.createChooser(i, "Send mail..."));
                    } catch (android.content.ActivityNotFoundException ex) {
                        Toast.makeText(ShareFavoritesActivity.this, "There are no email clients installed.", Toast.LENGTH_SHORT).show();
                    }
                }

            }

    }



}
