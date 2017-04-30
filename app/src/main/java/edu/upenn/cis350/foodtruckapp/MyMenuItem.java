package edu.upenn.cis350.foodtruckapp;

/**
 * basic Menu Item class with getters and setters for item price, and quantity
 */
public class MyMenuItem {

        private String item;
        private String price;
        private int quantity;

        public MyMenuItem(String item, String price) {
            this.item = item;
            this.price = price;
            quantity = 0;
        }

        String getItem() {
            return item;
        }

        String getPrice() {
            return price;
        }

        int getQuantity() { return quantity; }

        void setQuantity(int quantity) {this.quantity = quantity; }

        void setPrice(String price) {
            this.price = price;
        }

        void setItem(String item) {
            this.item = item;
        }


        @Override
        public boolean equals(Object o) {
            MyMenuItem otherItem = (MyMenuItem) o;
            if (otherItem.getItem().equals(item) && otherItem.getPrice().equals(price)) {
                return true;
            }
            return false;
        }

        @Override
        public String toString() {
            return "item: " + item + " quantity: " + quantity;
        }

    }