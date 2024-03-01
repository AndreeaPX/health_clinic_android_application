package com.example.msl.Instance;

public class Rating {
    private int idAppointment;
    private float rating;
    private String comment;

    public Rating(int idAppointment, float rating, String comment) {
        this.idAppointment = idAppointment;
        this.rating = rating;
        this.comment = comment;
    }

    public int getIdAppointment() {
        return idAppointment;
    }

    public float getRating() {
        return rating;
    }

    public String getComment() {
        return comment;
    }
}
