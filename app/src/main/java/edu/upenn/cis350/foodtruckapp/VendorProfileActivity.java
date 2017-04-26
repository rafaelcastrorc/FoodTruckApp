package edu.upenn.cis350.foodtruckapp;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridLayout.LayoutParams;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.NumberPicker;
import android.widget.RatingBar;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
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
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;

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
    ArrayList<Integer> pickerIds;
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
    private String uniqueID;
    private StorageReference vendorRef;
    private ArrayList<MyMenuItem> menu;
    private ListView menuListView;
    private HashMap<String, String> textValues = new HashMap<String, String>();
    private int openWeekdayTime = 0;
    private String openWeekdayPeriod = "";
    private int closeWeekdayTime = 0;
    private String closeWeekdayPeriod = "";
    private int openWeekendTime = 0;
    private String openWeekendPeriod = "";
    private int closeWeekendTime = 0;
    private String closeWeekendPeriod = "";


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


        Button reviews = (Button) findViewById(R.id.seeReviews);


        pickerIds = new ArrayList<Integer>();
        pickerIds.add(R.id.picker_vendor_open_weekday_time);
        pickerIds.add(R.id.picker_vendor_open_weekday_period);
        pickerIds.add(R.id.picker_vendor_close_weekday_time);
        pickerIds.add(R.id.picker_vendor_close_weekday_period);
        pickerIds.add(R.id.picker_vendor_open_weekend_time);
        pickerIds.add(R.id.picker_vendor_open_weekend_period);
        pickerIds.add(R.id.picker_vendor_close_weekend_time);
        pickerIds.add(R.id.picker_vendor_close_weekend_period);

        final NumberPicker npOpenWeekdayTime = (NumberPicker) findViewById(R.id.picker_vendor_open_weekday_time);
        npOpenWeekdayTime.setWrapSelectorWheel(true);
        npOpenWeekdayTime.setMinValue(6);
        npOpenWeekdayTime.setMaxValue(12);
        npOpenWeekdayTime.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                openWeekdayTime = npOpenWeekdayTime.getValue();
            }
        });

        String periods[] = new String[]{"AM", "PM"};
        final NumberPicker npOpenWeekdayPeriod = (NumberPicker) findViewById(R.id.picker_vendor_open_weekday_period);
        npOpenWeekdayPeriod.setWrapSelectorWheel(true);
        npOpenWeekdayPeriod.setDisplayedValues(periods);
        npOpenWeekdayPeriod.setMinValue(0);
        npOpenWeekdayPeriod.setMaxValue(1);
        npOpenWeekdayPeriod.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                if (npOpenWeekdayPeriod.getValue() == 0) {
                    openWeekdayPeriod = "AM";
                }
                else {
                    openWeekdayPeriod = "PM";

                }
            }
        });

        final NumberPicker npCloseWeekdayTime = (NumberPicker) findViewById(R.id.picker_vendor_close_weekday_time);
        npCloseWeekdayTime.setWrapSelectorWheel(true);
        npCloseWeekdayTime.setMinValue(6);
        npCloseWeekdayTime.setMaxValue(12);
        npCloseWeekdayTime.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                closeWeekdayTime = npCloseWeekdayTime.getValue();
            }
        });

        final NumberPicker npCloseWeekdayPeriod = (NumberPicker) findViewById(R.id.picker_vendor_close_weekday_period);
        npCloseWeekdayPeriod.setWrapSelectorWheel(true);
        npCloseWeekdayPeriod.setDisplayedValues(periods);
        npCloseWeekdayPeriod.setMinValue(0);
        npCloseWeekdayPeriod.setMaxValue(1);
        npCloseWeekdayPeriod.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                if (npCloseWeekdayPeriod.getValue() == 0) {
                    closeWeekdayPeriod = "AM";
                }
                else {
                    closeWeekdayPeriod = "PM";

                }
            }
        });



        final NumberPicker npOpenWeekendTime = (NumberPicker) findViewById(R.id.picker_vendor_open_weekend_time);
        npOpenWeekendTime.setWrapSelectorWheel(true);
        npOpenWeekendTime.setMinValue(6);
        npOpenWeekendTime.setMaxValue(12);
        npOpenWeekendTime.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                openWeekendTime = npOpenWeekendTime.getValue();

            }
        });

        final NumberPicker npOpenWeekendPeriod = (NumberPicker) findViewById(R.id.picker_vendor_open_weekend_period);
        npOpenWeekendPeriod.setWrapSelectorWheel(true);
        npOpenWeekendPeriod.setDisplayedValues(periods);
        npOpenWeekendPeriod.setMinValue(0);
        npOpenWeekendPeriod.setMaxValue(1);
        npOpenWeekendPeriod.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                if (npOpenWeekendPeriod.getValue() == 0) {
                    openWeekendPeriod = "AM";
                }
                else {
                    openWeekendPeriod = "PM";

                }
            }
        });

        final NumberPicker npCloseWeekendTime = (NumberPicker) findViewById(R.id.picker_vendor_close_weekend_time);
        npCloseWeekendTime.setWrapSelectorWheel(true);
        npCloseWeekendTime.setMinValue(6);
        npCloseWeekendTime.setMaxValue(12);
        npCloseWeekendTime.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                closeWeekendTime = npCloseWeekendTime.getValue();
            }
        });

        final NumberPicker npCloseWeekendPeriod = (NumberPicker) findViewById(R.id.picker_vendor_close_weekend_period);
        npCloseWeekendPeriod.setWrapSelectorWheel(true);
        npCloseWeekendPeriod.setDisplayedValues(periods);
        npCloseWeekendPeriod.setMinValue(0);
        npCloseWeekendPeriod.setMaxValue(1);
        npCloseWeekendPeriod.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                if (npOpenWeekendPeriod.getValue() == 0) {
                    openWeekendPeriod = "AM";
                }
                else {
                    openWeekendPeriod = "PM";

                }
            }
        });


        databaseRef = FirebaseDatabase.getInstance().getReference("Users");
        mAuth = FirebaseAuth.getInstance();
        uniqueID = mAuth.getCurrentUser().getUid();
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        DatabaseReference foodtruck = databaseRef.child(uniqueID).child("Name Of Food Truck");
        foodtruck.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() != null) {
                    vendorName = dataSnapshot.getValue().toString();
                    try {
                        getSupportActionBar().setTitle(vendorName);
                    }
                    catch (NullPointerException e) {
                        getSupportActionBar().setTitle("No name found");
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });

        DatabaseReference rating = databaseRef.child(uniqueID).child("Average Rating");
        rating.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() != null) {
                    try {
                        Double rating = (Double) dataSnapshot.getValue();
                        RatingBar bar = (RatingBar) findViewById(R.id.ratingBarForVendor);
                        bar.setRating(rating.floatValue());
                    }
                    catch (ClassCastException e) {
                        Long rating = (Long) dataSnapshot.getValue();
                        RatingBar bar = (RatingBar) findViewById(R.id.ratingBarForVendor);
                        bar.setRating(rating.floatValue());
                    }
                    getSupportActionBar().setTitle(vendorName);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // menu array adapter
        menuListView = (ListView) findViewById(R.id.menu);
        menu = new ArrayList<MyMenuItem>();
        final MyAdapter arrayAdapter = new VendorProfileActivity.MyAdapter(this);
        menuListView.setAdapter(arrayAdapter);

        databaseRef = FirebaseDatabase.getInstance().getReference("Users");
        mAuth = FirebaseAuth.getInstance();
        String currVendor = mAuth.getCurrentUser().getUid();
        DatabaseReference menuRef = databaseRef.child(currVendor).child("Menu");
        menuRef.addChildEventListener(new ChildEventListener() {
              String item = "";
              String price = "";
              @Override
              public void onChildAdded(DataSnapshot dataSnapshot, String prevChildKey) {
                  MyMenuItem menuItem = new MyMenuItem(
                          dataSnapshot.getKey(), (String) dataSnapshot.getValue());
                  if (!menu.contains(menuItem)) {
                      menu.add(menuItem);
                  }
                  arrayAdapter.notifyDataSetChanged();
                  setListViewHeightBasedOnChildren(menuListView);
              }

              @Override
              public void onChildChanged(DataSnapshot dataSnapshot, String prevChildKey) {
                  setListViewHeightBasedOnChildren(menuListView);
              }

              @Override
              public void onChildRemoved(DataSnapshot dataSnapshot) {
                  MyMenuItem menuItem = new MyMenuItem(
                          (String) dataSnapshot.getKey(), (String) dataSnapshot.getValue());
                  menu.remove(menuItem);
                  arrayAdapter.notifyDataSetChanged();
                  setListViewHeightBasedOnChildren(menuListView);

              }

              @Override
              public void onChildMoved(DataSnapshot dataSnapshot, String prevChildKey) {
                  setListViewHeightBasedOnChildren(menuListView);

              }

              @Override
              public void onCancelled(DatabaseError databaseError) {
              }
        });

        populateVendorPicture();

        Button addMenuItemBtn = (Button) findViewById(R.id.vendor_add_menu_item_button);
        addMenuItemBtn.setOnClickListener(new AdapterView.OnClickListener() {
            @Override
            public void onClick(View v) {
                MyMenuItem newItem = new MyMenuItem("", "");
                if (!menu.contains(newItem)) {
                    menu.add(newItem);
                }
                arrayAdapter.notifyDataSetChanged();
                Log.d("MENU", arrayAdapter.getMenu().toString());
                setListViewHeightBasedOnChildren(menuListView);
            }
        });

        makeFieldsStatic();
    }

    void makeChildrenStatic(View view) {
        if (!(view instanceof ViewGroup)) {
            return;
        }
        else {
            ViewGroup vg = (ViewGroup) view;
            for (int i = 0; i < vg.getChildCount(); i++) {
                View child = vg.getChildAt(i);
                makeChildrenStatic(child);
                child.setEnabled(false);
            }
        }
    }

    void makeChildrenEditable(View view) {
        if (!(view instanceof ViewGroup) || view instanceof RatingBar) {
            return;
        }
        else {
            ViewGroup vg = (ViewGroup) view;
            for (int i = 0; i < vg.getChildCount(); i++) {
                View child = vg.getChildAt(i);
                makeChildrenEditable(child);
                child.setEnabled(true);
            }
        }
    }

    void makeFieldsStatic() {
        LinearLayout overallLayout = (LinearLayout) findViewById(R.id.vendor_profile_overall_layout);
        makeChildrenStatic(overallLayout);
        menuListView.setEnabled(false);
    }

    void makeFieldsEditable() {
        LinearLayout overallLayout = (LinearLayout) findViewById(R.id.vendor_profile_overall_layout);
        makeChildrenEditable(overallLayout);
        menuListView.setEnabled(false);
    }

    public static void setListViewHeightBasedOnChildren(ListView listView) {
        ListAdapter arrayAdapter = listView.getAdapter();
        int desiredWidth = View.MeasureSpec.makeMeasureSpec(
                listView.getWidth(), View.MeasureSpec.UNSPECIFIED);
        int totalHeight = 0;

        View view = null;

        for (int i = 0; i < arrayAdapter.getCount(); i++) {
            view = arrayAdapter.getView(i, view, listView);

            if (i == 0) {
                view.setLayoutParams(new ViewGroup.LayoutParams(desiredWidth, LayoutParams.WRAP_CONTENT));
            }
            view.measure(desiredWidth, View.MeasureSpec.UNSPECIFIED);
            totalHeight += view.getMeasuredHeight();
        }
        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight + (listView.getDividerHeight() * (arrayAdapter.getCount() - 1));
        listView.setLayoutParams(params);
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
            makeFieldsEditable();
        } else if (item.getTitle().equals("Save")) {
            uploadVendorPicToServer();
            makeFieldsStatic();
            notifyDatabaseOfChange();
        }
        else if (item.getTitle().equals("My stats")) {
            Intent j = new Intent(VendorProfileActivity.this, VendorAnalyticsActivity.class);
            startActivity(j);
        }
        else if (item.getTitle().equals("Home")) {
            Intent j = new Intent(VendorProfileActivity.this, VendorMainMenuActivity.class);
            startActivity(j);
        }
        return true;
    }

    void uploadVendorPicToServer() {
        Button imageBtn = (Button) findViewById(R.id.vendor_profile_image);
        Drawable drawable = imageBtn.getBackground();
        Bitmap bitmap;
        try {
            bitmap = ((BitmapDrawable) drawable).getBitmap();
        }
        catch (ClassCastException e) {
            return;
        }
        ByteArrayOutputStream baos = new ByteArrayOutputStream();               // upload pic to database
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] byteData = baos.toByteArray();
        storage = FirebaseStorage.getInstance();
        storageRef = storage.getReferenceFromUrl("gs://food-truck-f6065.appspot.com");
        imagesRef = storageRef.child("images/" + uniqueID);
        UploadTask uploadTask = imagesRef.putBytes(byteData);
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                Toast.makeText(VendorProfileActivity.this, "The picture you selected could" +
                        "not be uploaded.", Toast.LENGTH_LONG).show();
                Log.d("exception", exception.getCause().toString());
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
            }
        });
    }

    // used for receiving image from user & changing background of button to that image
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != RESULT_OK) {
            return;
        }
        if (requestCode == 1) {
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

        }
    }

    void populateVendorPicture() {
        storage = FirebaseStorage.getInstance();
        storageRef = storage.getReference();
        imagesRef = storageRef.child("images");
        vendorRef = imagesRef.child(uniqueID);
        final long ONE_MEGABYTE = 1024 * 1024;
        vendorRef.getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
            @Override
            public void onSuccess(byte[] bytes) {
                Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                BitmapDrawable drawableBitmap = new BitmapDrawable(
                        getApplicationContext().getResources(), bitmap);
                profilePic.setBackground(drawableBitmap);
                profilePic.setText("");
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
            }
        });
    }

    void notifyDatabaseOfChange() {
        databaseRef = FirebaseDatabase.getInstance().getReference("Users");
        mAuth = FirebaseAuth.getInstance();
        uniqueID = mAuth.getCurrentUser().getUid();
        DatabaseReference hoursRef = databaseRef.child(uniqueID).child("Hours");
        if (openWeekdayTime != 0) {
            hoursRef.child("OpenWeekdayTime").setValue(openWeekdayTime);
        }
        if (!openWeekdayPeriod.isEmpty()) {
            hoursRef.child("OpenWeekdayPeriod").setValue(openWeekdayPeriod);
        }
        if (closeWeekdayTime != 0) {
            hoursRef.child("CloseWeekdayTime").setValue(closeWeekdayTime);
        }
        if (!closeWeekdayPeriod.isEmpty()) {
            hoursRef.child("CloseWeekdayPeriod").setValue(closeWeekdayPeriod);
        }
        if (openWeekendTime != 0) {
            hoursRef.child("OpenWeekendTime").setValue(openWeekendTime);
        }
        if (!openWeekendPeriod.isEmpty()) {
            hoursRef.child("OpenWeekendPeriod").setValue(openWeekendPeriod);
        }
        if (closeWeekendTime != 0) {
            hoursRef.child("CloseWeekendTime").setValue(closeWeekendTime);
        }
        if (!closeWeekdayPeriod.isEmpty()) {
            hoursRef.child("CloseWeekendPeriod").setValue(closeWeekendPeriod);
        }

        ((MyAdapter) menuListView.getAdapter()).notifyDataSetChanged();
        setListViewHeightBasedOnChildren(menuListView);
        for (MyMenuItem item : menu) {
            if (item.getPrice().isEmpty() || item.getItem().isEmpty()) {
                continue;
            }
            databaseRef.child(uniqueID).child("Menu").child(item.getItem()).setValue(item.getPrice());
        }
    }


    class MyAdapter extends BaseAdapter {

        private Context context;

        public MyAdapter(Context context) {
            this.context = context;
        }

        @Override
        public int getCount() {
            return menu.size();
        }

        @Override
        public Object getItem(int position) {
            return menu.get(position);
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        ArrayList<MyMenuItem> getMenu() {
            return menu;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {

            if (convertView == null) {
                LayoutInflater inflater = (LayoutInflater) context
                        .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = inflater.inflate(R.layout.vendor_menu_item_style, parent, false);

                final EditText item = (EditText) convertView.findViewById(R.id.vendor_menu_item);
                item.setText(((MyMenuItem) getItem(position)).getItem());
                final EditText price = (EditText) convertView.findViewById(R.id.vendor_menu_item_price);
                price.setText(((MyMenuItem) getItem(position)).getPrice());

                item.addTextChangedListener(new TextWatcher() {

                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                    }

                    @Override
                    public void afterTextChanged(Editable s) {
                        item.removeTextChangedListener(this);
                        String itemString = ((MyMenuItem) getItem(position)).getItem();
                        if (itemString.isEmpty()) {
                            item.setText(s);
                        } else {
                            String input = s.toString();
//                            if (input.isEmpty()) {
//                                item.setText(itemString);
//                            } else {
                                item.setText(input);
                            //}
                    }
                        item.addTextChangedListener(this);
                        item.setSelection(item.getText().length());
                    }

                });
                price.addTextChangedListener(new TextWatcher() {

                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                    }

                    @Override
                    public void afterTextChanged(Editable s) {
                        price.removeTextChangedListener(this);
                        String itemPrice = ((MyMenuItem) getItem(position)).getPrice();
                        if (itemPrice.isEmpty()) {
                            price.setText(s);
                        } else {
                            String input = s.toString();
//                            if (input.isEmpty()) {
//                                price.setText(itemPrice);
//                            } else {
                                price.setText(input);
                            //}
                        }
                        price.addTextChangedListener(this);
                        price.setSelection(price.getText().length());
                    }
                });

                item.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                    @Override
                    public void onFocusChange(View v, boolean hasFocus) {
                        if (position >= menu.size()) {
                            return;
                        }
                        if (!hasFocus) {
                            ((MyMenuItem) getItem(position)).setItem(item.getText().toString());
                        }
                    }
                });

                price.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                    @Override
                    public void onFocusChange(View v, boolean hasFocus) {
                        if (position >= menu.size()) {
                            return;
                        }
                        if (!hasFocus) {
                            ((MyMenuItem) getItem(position)).setPrice((price.getText().toString()));
                        }
                    }
                });
            }
            return convertView;
        }
    }

    public void seeReviews(View v){
        Intent i = new Intent(VendorProfileActivity.this, VendorReviewsActivity.class);
        i.putExtra("UniqueID", uniqueID);
        startActivity(i);
    }
}
