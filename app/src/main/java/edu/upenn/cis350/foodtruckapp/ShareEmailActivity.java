package edu.upenn.cis350.foodtruckapp;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class ShareEmailActivity extends AppCompatActivity {
    private EditText email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_share_email);
        email = (EditText) findViewById(R.id.input_email_share);
    }

    public void sendEmailOnClick(View v) {
        String address = email.getText().toString();
        String subject = getResources().getString(R.string.email_subject);
        String content = getResources().getString(R.string.email_content);

        Intent i = new Intent(Intent.ACTION_SEND);
        i.setType("message/rfc/822");
        i.putExtra(Intent.EXTRA_EMAIL  , address);
        i.putExtra(Intent.EXTRA_SUBJECT, subject);
        i.putExtra(Intent.EXTRA_TEXT   , content);
        try {
            startActivity(Intent.createChooser(i, "Send mail..."));
        } catch (android.content.ActivityNotFoundException ex) {
            Toast.makeText(ShareEmailActivity.this, "There are no email clients installed.", Toast.LENGTH_SHORT).show();
        }
    }


}
