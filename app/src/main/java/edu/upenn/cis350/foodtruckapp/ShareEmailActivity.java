package edu.upenn.cis350.foodtruckapp;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class ShareEmailActivity extends AppCompatActivity {
    private EditText email;

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

                Intent i = new Intent(ShareEmailActivity.this, Cart.class);
                startActivity(i);
                return true;
            case R.id.home_button:
                Intent j = new Intent(ShareEmailActivity.this, CustomerMainMenuActivity.class);
                startActivity(j);
                return true;
            case R.id.search_button_menu:
                Intent x = new Intent(ShareEmailActivity.this, SearchFoodActivity.class);
                startActivity(x);

            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_share_email);
        email = (EditText) findViewById(R.id.input_email_share);
    }

    /**
     * open local email app with TO, Subject fields, and content
     * @param v
     */
    public void sendEmailOnClick(View v) {
        String address = email.getText().toString();
        String subject = getResources().getString(R.string.email_subject);
        String content = getResources().getString(R.string.email_content);

        Intent i = new Intent(Intent.ACTION_SEND);
        i.setType("message/rfc822");
        i.putExtra(Intent.EXTRA_EMAIL  , new String[]{address});
        i.putExtra(Intent.EXTRA_SUBJECT, subject);
        i.putExtra(Intent.EXTRA_TEXT   , content);
        try {
            startActivity(Intent.createChooser(i, "Send mail..."));
        } catch (android.content.ActivityNotFoundException ex) {
            Toast.makeText(ShareEmailActivity.this, "There are no email clients installed.", Toast.LENGTH_SHORT).show();
        }
    }


}
