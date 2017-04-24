package edu.upenn.cis350.foodtruckapp;

import android.util.Log;
import android.view.View;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;

import org.joda.time.DateTime;
import org.joda.time.Period;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.TreeMap;

/**
 * Created by rafaelcastro on 4/19/17.
 * Handles the functionality of the VendorAnalyticsActivity
 */

class VendorAnalytics {
    private FirebaseAuth mAuth;
    private DatabaseReference databaseRef;
    private String id;
    private HashMap<Integer, Order> orderHistoryMap;
    private LinkedList<Order> orderList;

    /**
     * No args constructor
     */
    VendorAnalytics(HashMap<Integer, Order> orderHistoryMap) {
        databaseRef = FirebaseDatabase.getInstance().getReference("Users");
        mAuth = FirebaseAuth.getInstance();
        id = FirebaseInstanceId.getInstance().getId();
        this.orderHistoryMap = orderHistoryMap;

    }

    VendorAnalytics() {
        databaseRef = FirebaseDatabase.getInstance().getReference("Users");
        mAuth = FirebaseAuth.getInstance();
        id = FirebaseInstanceId.getInstance().getId();
    }

    /**
     * Gets the sales for a given time period
     *
     * @param period - Time period to get sales from. Choose from Hour, Day, Week, Month, Year, All time
     * @return void
     */
    protected double getSales(String period) {
        Double totalSales = 0.0;
        boolean isValidTime = false;
        for(Integer i : orderHistoryMap.keySet()) {
            DateTimeFormatter formatter = DateTimeFormat.forPattern("dd/MM/yyyy HH:mm:ss");
            DateTime prevDT = formatter.parseDateTime(orderHistoryMap.get(i).getTime());
            isValidTime = time(prevDT, period);
            if (isValidTime) {
                totalSales = totalSales + orderHistoryMap.get(i).getPrice();
            }
        }
        return totalSales;
    }


    /**
     * Pushes an order to the OrderHistory child of the vendor in firebase
     *
     * @param order - order that needs to be added
     * @return void
     */
    protected void pushOrderToDataBase(Order order) {
        DatabaseReference orderHistory = databaseRef.child(mAuth.getCurrentUser().getUid()).child("OrderHistory");
        orderHistory.push().setValue(order);
    }


    /**
     * Gets the time of the day when the food truck receives the most order
     * @param period - Time period to calculate the info
     * @return string with the most popular hour or day to order
     */
    protected String getMostPopularTime(String period) {
        String result = "";
        HashMap<Integer, String> periodToNumOfOrders = new HashMap<>();
        int mostOrders = 0;

        if (period.equals("Hour")) {
            HashMap<Integer, ArrayList<Order>> timeToOrder = mapTimeToOrder(period);
            for (int hour : timeToOrder.keySet()) {
                //Gets the number of orders ina  given hour
                if (timeToOrder.get(hour).size() > mostOrders) {
                   mostOrders =  timeToOrder.get(hour).size();
                    result = String.valueOf(hour) + ":00";
                }
            }
        }
        else {
            //Calculate the most popular day
            HashMap<Integer, ArrayList<Order>> timeToOrder = mapTimeToOrder(period);
            for (int day : timeToOrder.keySet()) {
                //Gets the number of orders ina  given hour
                if (timeToOrder.get(day).size() > mostOrders) {
                    mostOrders =  timeToOrder.get(day).size();
                    result = String.valueOf(day);
                }
            }

            //Get the day formatted correctly
            if (result.equals("1")) {
                result = "Monday";
            }
            else if (result.equals("2")) {
                result = "Tuesday";
            }
            else if (result.equals("3")) {
                result = "Wednesday";
            }
            else if (result.equals("4")) {
                result = "Thursday";
            }
            else if (result.equals("5")) {
                result = "Friday";
            }
            else if (result.equals("6")) {
                result = "Saturday";
            }
            else {
                result = "Sunday";
            }
        }

        return result;
    }
    /**
     * Maps a time period to a list of orders that occur during that specific period
     * @param time - Time period to calculate the info
     * @return map from time period to a list of orders
     */
    private HashMap<Integer, ArrayList<Order>> mapTimeToOrder(String time) {
        HashMap<Integer, ArrayList<Order>> periodToNumOfOrders = new HashMap<>();

        for(Integer i : orderHistoryMap.keySet()) {

            DateTimeFormatter formatter = DateTimeFormat.forPattern("dd/MM/yyyy HH:mm:ss");
            DateTime prevDT = formatter.parseDateTime(orderHistoryMap.get(i).getTime());

            if (time.equals("Hour")) {
                if (periodToNumOfOrders.get(prevDT.getHourOfDay()) == null) {
                    ArrayList<Order> newOrder = new ArrayList<>();
                    newOrder.add(orderHistoryMap.get(i));
                    periodToNumOfOrders.put(prevDT.getHourOfDay(), newOrder);
                }
                else {
                    ArrayList<Order> prev = periodToNumOfOrders.get(prevDT.getHourOfDay());
                    prev.add(orderHistoryMap.get(i));
                    periodToNumOfOrders.put(prevDT.getHourOfDay(), prev);

                }
            }

            else if (time.equals("Day")) {
                if (periodToNumOfOrders.get(prevDT.getDayOfWeek()) == null) {
                    ArrayList<Order> newOrder = new ArrayList<>();
                    newOrder.add(orderHistoryMap.get(i));
                    periodToNumOfOrders.put(prevDT.getDayOfWeek(), newOrder);
                }
                else {
                    ArrayList<Order> prev = periodToNumOfOrders.get(prevDT.getDayOfWeek());
                    prev.add(orderHistoryMap.get(i));
                    periodToNumOfOrders.put(prevDT.getDayOfWeek(), prev);

                }
            }
        }
        return periodToNumOfOrders;
    }


    /**
     * Gets the best selling product
     * @return String - product
     */
    protected String getBestSellingProduct() {
        CustomerOrderMGM mgm  = new CustomerOrderMGM();
        TreeMap<String, Integer> orders = new TreeMap<>();
        //Gets all the orders by item and frequency
        for (int i: orderHistoryMap.keySet()) {
            Order curr = orderHistoryMap.get(i);
            TreeMap<String, Integer> temp = mgm.ordersParser(curr.getCustomerOrder());

            //goes through each item of the order and adds it to the map
            for (String item : temp.keySet()) {
                if (!orders.containsKey(item)) {
                    orders.put(item, temp.get(item));
                }
                else {
                    int prevQuant = orders.get(item);
                    orders.put(item, prevQuant + temp.get(item));
                }

            }
        }
        String result = "";
        int largest = 0;
        for (String order : orders.keySet()) {
            if (orders.get(order) > largest) {
                largest = orders.get(order);
                result = order;
            }
        }
        return result;

    }



    /**
     * Adds a time limite to perform a certain operation
     * @param date - date the order was sent
     * @param limitPeriod - Limit for the allowed valid time, for instance 1 hour, 1 day, 1 week, etc...
     * @return boolean - is the time valid?
     */
    protected boolean time(DateTime date, String limitPeriod) {
        DateTime now = new DateTime();
        Period period = new Period(date, now);
        if (limitPeriod.equals("Hour")) {
            if (period.getYears() == 0 && period.getMonths() == 0 && period.getWeeks() ==0 && period.getDays() ==0 && period.getHours() <= 1) {
                    return true;
                }
        }
        else if (limitPeriod.equals("Day")) {
            if (period.getYears() == 0 && period.getMonths() == 0 && period.getWeeks() ==0 && period.getDays() <= 1) {
                return true;
            }
        }
        else if (limitPeriod.equals("Week")) {
            if (period.getYears() == 0 && period.getMonths() == 0 && period.getWeeks() <= 1) {
                return true;
            }
        }
        else if (limitPeriod.equals("Month"))  {
            if (period.getYears() == 0 && period.getMonths() <= 1) {
                return true;
            }
        }
        else if (limitPeriod.equals("Year")){
            if (period.getYears() <= 1) {
                return true;
            }
        }
        else {
            return true;
        }
        return false;

    }
}
