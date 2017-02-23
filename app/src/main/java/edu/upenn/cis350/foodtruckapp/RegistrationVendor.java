package edu.upenn.cis350.foodtruckapp;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class RegistrationVendor extends AppCompatActivity {

    private static final int IMAGE_GALLERY = 10;

    private Button uploadButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration_vendor);
        uploadButton = (Button) findViewById(R.id.truckPhoto);
    }

    protected void btnUploadTruckPhoto(View view){
        Intent choosePhoto = new Intent(Intent.ACTION_PICK);
        //intent to pick a picture from gallery

       String path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).getPath();
        //get path where images are stored

        Uri pictureDirectory = Uri.parse(path);
        //converts the string of the path to a Uniform Resource Identifier which is used to access directory

        choosePhoto.setDataAndType(pictureDirectory,"image/*");
        //show images of all types in the directory

        startActivityForResult(choosePhoto,IMAGE_GALLERY);
        //start the image gallery activity. It will return with value IMAGE_GALLERY and invoke onActivityResult()

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == IMAGE_GALLERY){
            Uri photoPath = data.getData();
            //get path of selected image

            uploadButton.setBackgroundColor(Color.GREEN);
            uploadButton.invalidate();

        }
    }
}
