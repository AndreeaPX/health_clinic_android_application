package com.example.msl.Instance;

import java.util.Date;

public class Clinic {
    private int id;
    private String name;
    private String email;
    private Date startingDate;
    private String address;
    private String city;

    public Clinic(int id, String name,String email, Date startingDate, String address, String city) {
        this.id = id;
        this.name = name;
        this.email=email;
        this.startingDate = startingDate;
        this.address = address;
        this.city = city;
    }

    public Clinic(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Date getStartingDate() {
        return startingDate;
    }

    public String getAddress() {
        return address;
    }

    public String getCity() {
        return city;
    }

    public String getEmail() {
        return email;
    }

    @Override
    public String toString() {
        return name;
    }
}
