package edu.upenn.cis350.foodtruckapp;

public class MyMenuItem {

        private String item;
        private String price;

        public MyMenuItem(String item, String price) {
            this.item = item;
            this.price = price;
        }

        String getItem() {
            return item;
        }

        String getPrice() {
            return price;
        }

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
            return "item: " + item + " price: " + price;
        }

    }