package edu.upenn.cis350.foodtruckapp;

/**
 * Created by desmondhoward on 3/24/17.
 */

public class Vendor implements Comparable<Vendor> {

    private String name;
    private Double rating;
    private Long count;

    public Vendor(String name, Double rating, Long count) {
        this.name = name;
        this.rating = rating;
        this.count = count;
    }

    String getName() {
        return name;
    }

    Double getRating() {
        return rating;
    }

    Long getCount() {return count;}

    @Override
    public int compareTo(Vendor o) {
        Double oRating = o.getRating();
        if (this.rating > oRating) {
            return 1;
        }
        else if (this.rating < oRating) {
            return -1;
        }
        else {
            return 0;
        }
    }
}
