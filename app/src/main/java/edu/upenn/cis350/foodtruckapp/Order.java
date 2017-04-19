package edu.upenn.cis350.foodtruckapp;

/**
 * Created by rafaelcastro on 4/3/17.
 * Order object that stores all information that a user order has
 */

public class Order {
    protected String customerInstanceID;
    protected String order;
    protected String customerName;
    protected String pushId;
    protected String vendorUniqueID;
    protected boolean submitted;
    protected String vendorName;
    private double price;
    private String customerUniqueID;
    private String time;

    Order(String customerInstanceID, String order, String name, String pushId, String vendorUniqueID) {
        this.customerInstanceID = customerInstanceID;
        this.order = order;
        this.customerName = name;
        this.pushId = pushId;
        this.vendorUniqueID = vendorUniqueID;
    }

    public String getCustomerInstanceID() {
        return customerInstanceID;
    }

    public String getCustomerOrder() {
        return order;
    }

    public String getPushId() {
        return pushId;
    }


    public String getCustomerName() {
        return customerName;
    }

    public String getVendorUniqueID() {
        return vendorUniqueID;
    }

    public void setStatus(boolean status) {
        submitted = status;
    }

    public boolean getStatus() {
        return submitted;
    }


    public void setFoodTruckName(String name) {
        vendorName = name;
    }


    public String getFoodTruckName() {
        return vendorName;
    }

    public void setPrice(double price) {
        this.price = price;
    }


    public double getPrice() {
        return price;
    }


    @Override
    public String toString() {
        String formattedOrder = "";
        formattedOrder = order + "\n" + customerName;
        return formattedOrder;

    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Order order = (Order) o;

        if (customerInstanceID != null ? !customerInstanceID.equals(order.customerInstanceID) : order.customerInstanceID != null)
            return false;
        if (customerName != null ? !customerName.equals(order.customerName) : order.customerName != null)
            return false;
        return vendorUniqueID != null ? vendorUniqueID.equals(order.vendorUniqueID) : order.vendorUniqueID == null;

    }

    @Override
    public int hashCode() {
        int result = customerInstanceID != null ? customerInstanceID.hashCode() : 0;
        result = 31 * result + (order != null ? order.hashCode() : 0);
        result = 31 * result + (customerName != null ? customerName.hashCode() : 0);
        result = 31 * result + (pushId != null ? pushId.hashCode() : 0);
        return result;
    }

    protected void setCustomerUniqueID(String customerUniqueID) {
        this.customerUniqueID = customerUniqueID;
    }

    public String getCustomerUniqueID() {
        return customerUniqueID;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getTime() {
        return time;
    }
}
