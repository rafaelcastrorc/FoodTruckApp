package edu.upenn.cis350.foodtruckapp;

import android.content.Intent;
import android.support.annotation.MainThread;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.View;

/**
 * Created by rafaelcastro on 2/20/17.
 */

public class WelcomePage extends AppCompatActivity{

    //Right now it directed to the register choose page
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        Intent i = new Intent(WelcomePage.this, RegisterChoiceActivity.class);
        startActivity(i);
        return true;
    }


    //Handles database  login and register

    //TODO: WE NEED A MAIN PAGE!!!
    //If the button Register is pressed.


    //TODO: Add a button
    private void buttonRegisterPressed(View v) {
        Intent i = new Intent(WelcomePage.this, RegistrationActivity.class);
        startActivity(i);

    }




    //If the button Login is pressed.
    //TODO: Add a button
    private void buttonLoginPressed(View v) {
        Intent i = new Intent(WelcomePage.this, LoginActivity.class);
        startActivity(i);
    }
}
