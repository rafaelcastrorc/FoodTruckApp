package edu.upenn.cis350.foodtruckapp;

/**
 * Created by rafaelcastro on 4/3/17.
 * Order object that s
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
    private String firstLine;
    private String secondLine;

    Order(String customerInstanceID, String order, String name, String pushId, String vendorUniqueID) {
        this.customerInstanceID = customerInstanceID;
        this.order = order;
        this.customerName = name;
        this.pushId = pushId;
        this.vendorUniqueID = vendorUniqueID;
    }

    /**
     *
     * @return customerInstanceID
     */
    public String getCustomerInstanceID() {
        return customerInstanceID;
    }

    /**
     *
     * @return current customer order
     */
    public String getCustomerOrder() {
        return order;
    }

    /**
     *
     * @return current push ID
     */
    public String getPushId() {
        return pushId;
    }

    /**
     *
     * @return customer name
     */
    public String getCustomerName() {
        return customerName;
    }

    /**
     *
     * @return vendor's unique id
     */
    public String getVendorUniqueID() {
        return vendorUniqueID;
    }

    /**
     *  set whether order has been submitted
     * @param status: if order has been submitted yet
     */
    public void setStatus(boolean status) {
        submitted = status;
    }

    /**
     *
     * @return true if submitted, false otherwise
     */
    public boolean getStatus() {
        return submitted;
    }

    /**
     *
     * @param name vendor name
     */
    public void setFoodTruckName(String name) {
        vendorName = name;
    }

    /**
     *
     * @return vendorName
     */
    public String getFoodTruckName() {
        return vendorName;
    }

    /**
     * set price
     * @param price
     */
    public void setPrice(double price) {
        this.price = price;
    }

    /**
     *
     * @return get price
     */
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

    /**
     * set customer id
     * @param customerUniqueID
     */
    protected void setCustomerUniqueID(String customerUniqueID) {
        this.customerUniqueID = customerUniqueID;
    }

    /**
     *
     * @return customerUniqueID
     */
    public String getCustomerUniqueID() {
        return customerUniqueID;
    }

    /**
     * set time of order
     * @param time
     */
    public void setTime(String time) {
        this.time = time;
    }

    /**
     *
     * @return get time of order
     */
    public String getTime() {
        return time;
    }

    /**
     * format strings to be displayed in list
     * @param firstLine
     * @param secondLine
     */
    void setFormatStrings(String firstLine, String secondLine) {
        this.firstLine = firstLine;
        this.secondLine = secondLine;
    }

    /**
     *
     * @return first line
     */
    String getFirstLine() {
        return firstLine;
    }

    /**
     *
     * @return second line
     */
    String getSecondLine() {
        return secondLine;
    }
}

