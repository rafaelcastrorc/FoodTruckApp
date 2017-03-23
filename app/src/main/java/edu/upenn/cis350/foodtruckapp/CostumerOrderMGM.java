package edu.upenn.cis350.foodtruckapp;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.FirebaseMessaging;

/**
 * Created by rafaelcastro on 3/17/17.
 */

public class CostumerOrderMGM {
    private String id;
    private void linkId() {
        //Todo: Call server

        //Gets the id
        id = FirebaseInstanceId.getInstance().getToken();

        //Todo: Add new field to hold id token
    }
    private void subscribe () {
        FirebaseMessaging.getInstance().subscribeToTopic("user_"+id);
    }



}
