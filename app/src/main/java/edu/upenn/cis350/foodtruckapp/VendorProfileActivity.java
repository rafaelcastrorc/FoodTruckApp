package edu.upenn.cis350.foodtruckapp;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

public class VendorProfileActivity extends AppCompatActivity {

    private Spinner openWeekdayTimeSpinner;
    private Spinner openWeekdayPeriodSpinner;
    private Spinner closeWeekdayTimeSpinner;
    private Spinner closeWeekdayPeriodSpinner;
    private Spinner openWeekendTimeSpinner;
    private Spinner openWeekendPeriodSpinner;
    private Spinner closeWeekendTimeSpinner;
    private Spinner closeWeekendPeriodSpinner;
    ArrayList<Integer> itemIds;
    ArrayList<Integer> priceIds;
    ArrayList<Integer> spinnerIds;
    ArrayList<Integer> allIds;
    private String selection;
    private Toolbar toolbar;
    private EditText itemPrice;
    private EditText itemTwoPrice;
    private EditText itemThreePrice;
    private EditText itemFourPrice;
    private EditText itemFivePrice;
    private EditText itemSixPrice;
    private EditText itemSevenPrice;
    private EditText itemEightPrice;
    private EditText itemNinePrice;
    private Button profilePic;
    public static int PICK_PROFILE_PIC = 1;
    private FirebaseDatabase database;
    private FirebaseAuth mAuth;
    private DatabaseReference databaseRef;
    private FirebaseStorage storage;
    private StorageReference storageRef;
    private StorageReference imagesRef;
    private String vendorName;
    private StorageReference vendorRef;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vendor_profile); // switch content view

        profilePic = (Button) findViewById(R.id.vendor_profile_image);
        profilePic.setOnClickListener(new AdapterView.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                Log.d("HELLO", "HELLO");
                startActivityForResult(intent, PICK_PROFILE_PIC);
            }
        });

        itemIds = new ArrayList<Integer>();
        itemIds.add(R.id.vendor_item_one);
        itemIds.add(R.id.vendor_item_two);
        itemIds.add(R.id.vendor_item_three);
        itemIds.add(R.id.vendor_item_four);
        itemIds.add(R.id.vendor_item_five);
        itemIds.add(R.id.vendor_item_six);
        itemIds.add(R.id.vendor_item_seven);
        itemIds.add(R.id.vendor_item_eight);
        itemIds.add(R.id.vendor_item_nine);

        priceIds = new ArrayList<Integer>();
        priceIds.add(R.id.vendor_price_item_one);
        priceIds.add(R.id.vendor_price_item_two);
        priceIds.add(R.id.vendor_price_item_three);
        priceIds.add(R.id.vendor_price_item_four);
        priceIds.add(R.id.vendor_price_item_five);
        priceIds.add(R.id.vendor_price_item_six);
        priceIds.add(R.id.vendor_price_item_seven);
        priceIds.add(R.id.vendor_price_item_eight);
        priceIds.add(R.id.vendor_price_item_nine);

        spinnerIds = new ArrayList<Integer>();
        spinnerIds.add(R.id.spinner_vendor_open_weekday_time);
        spinnerIds.add(R.id.spinner_vendor_open_weekday_period);
        spinnerIds.add(R.id.spinner_vendor_close_weekday_time);
        spinnerIds.add(R.id.spinner_vendor_close_weekday_period);
        spinnerIds.add(R.id.spinner_vendor_open_weekend_time);
        spinnerIds.add(R.id.spinner_vendor_open_weekend_period);
        spinnerIds.add(R.id.spinner_vendor_close_weekend_time);
        spinnerIds.add(R.id.spinner_vendor_close_weekend_period);

        allIds = new ArrayList<Integer>();
        allIds.addAll(itemIds);
        allIds.addAll(priceIds);
        allIds.addAll(spinnerIds);
        allIds.add(R.id.vendor_profile_image);

        for (int i = 0; i < allIds.size(); i++) {
            View view = findViewById(allIds.get(i));
            view.setEnabled(false);
            appendDollarSigns();
        }



        databaseRef = FirebaseDatabase.getInstance().getReference("Users");
        mAuth = FirebaseAuth.getInstance();
        String uniqueUID = mAuth.getCurrentUser().getUid();
        DatabaseReference foodtruck = databaseRef.child(uniqueUID).child("Name Of Food Truck");
        foodtruck.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                vendorName = dataSnapshot.getValue().toString();
                getSupportActionBar().setTitle(vendorName);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle(vendorName);
    }

    // used to create menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_vendor_profile, menu);
        return true;
    }

    // used for handling mouseclick in menu
   @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getTitle().equals("Edit")) {
            for (int i = 0; i < allIds.size(); i++) {
                View view = findViewById(allIds.get(i));
                view.setEnabled(true);
            }
        }
       else if (item.getTitle().equals("Save")) {
           for (int i = 0; i < allIds.size(); i++) {
               View view = findViewById(allIds.get(i));
               view.setEnabled(false);
               appendDollarSigns();
           }
       }
       return true;
    }

    // used for appending dollar signs to text in price fields once 'Save' is clicked
    void appendDollarSigns() {
        for (int i = 0; i < priceIds.size(); i++) {
            EditText priceField = (EditText) findViewById(priceIds.get(i));
            String price = priceField.getText().toString();
            if (!price.trim().startsWith("$") && !price.trim().isEmpty()) {
                priceField.setText("$" + price);
            }
            priceField.setEnabled(false);
        }

    }

    // used for receiving image from user & changing background of button to that image
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != RESULT_OK) {
            Log.d("bye", "bye");
            return;
        }
        if (requestCode == 1) {
            Log.d("whoa", "whoa");
            final Uri extras = data.getData();

            InputStream imageStream = null;
            try {
                imageStream = getContentResolver().openInputStream(extras);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            Bitmap selectedImage = BitmapFactory.decodeStream(imageStream);         // get pic selected
            BitmapDrawable drawableBitmap = new BitmapDrawable(
                    getApplicationContext().getResources(), selectedImage);
            profilePic.setBackground(drawableBitmap);
            profilePic.setText("");

            ByteArrayOutputStream baos = new ByteArrayOutputStream();               // upload pic to database
            selectedImage.compress(Bitmap.CompressFormat.JPEG, 100, baos);
            byte[] byteData = baos.toByteArray();
            storage = FirebaseStorage.getInstance();
            storageRef = storage.getReference();
            imagesRef = storageRef.child("images");
            vendorRef = imagesRef.child(vendorName);
            UploadTask uploadTask = vendorRef.putBytes(byteData);
            uploadTask.addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    Toast.makeText(VendorProfileActivity.this, "The picture you selected could" +
                            "not be uploaded.", Toast.LENGTH_LONG).show();
                }
            }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Uri downloadUrl = taskSnapshot.getDownloadUrl();
                }
            });
        }
    }

    void populateVendorFields() {
        storage = FirebaseStorage.getInstance();
        storageRef = storage.getReference();
        imagesRef = storageRef.child("images");
        vendorRef = imagesRef.child(vendorName);

        final long ONE_MEGABYTE = 1024 * 1024;
        vendorRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                try {
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(getApplicationContext().getContentResolver(), uri);
                    BitmapDrawable drawableBitmap = new BitmapDrawable(
                            getApplicationContext().getResources(), bitmap);
                    profilePic.setBackground(drawableBitmap);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle any errors
            }
        });


    }

}
