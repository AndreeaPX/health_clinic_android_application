package com.example.msl.Instance;

public class Doctor {

    private int id;
    private String firtName;
    private String lastName;
    private int idSpecialization;

    public Doctor(int id, String firtName, String lastName,int idSpecialization) {
        this.id = id;
        this.firtName = firtName;
        this.lastName = lastName;
        this.idSpecialization=idSpecialization;
    }


    public int getId() {
        return id;
    }

    public int getIdSpecialization() {
        return idSpecialization;
    }

    @Override
    public String toString() {
        return firtName + " " + lastName;
    }
}
