package com.stylelogic.predictionengine;

class GroupRatings {
    long rating;
    boolean valid = true;
    boolean modified = false;
    short weight;
    short[] ratings = {0, 0, 0, 0, 0, 0, 0};

    public GroupRatings() {
        rating = 0;
        weight = 0;
    }

    public GroupRatings(int _rating, short _weight) {
        rating = _rating;
        weight = _weight;
    }

    public GroupRatings(int _rating, short _weight, short _ratings[]) {
        rating = _rating;
        weight = _weight;
        ratings = _ratings;
    }
}
