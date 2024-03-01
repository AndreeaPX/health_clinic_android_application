package com.example.msl.Instance;

import java.util.Date;
import java.util.Objects;

public class Medication implements Comparable<Medication> {
    private int id;
    private String name;
    private Date startingDate;
    private float dose;
    private String timesADay;
    private String interval;

    public Medication(int id, String name, Date startingDate, float dose, String timesADay, String interval) {
        this.id=id;
        this.name = name;
        this.startingDate = startingDate;
        this.dose = dose;
        this.timesADay = timesADay;
        this.interval = interval;
    }

    public String getName() {
        return name;
    }


    public Date getStartingDate() {
        return startingDate;
    }

    public void setStartingDate(Date startingDate) {
        this.startingDate = startingDate;
    }

    public float getDose() {
        return dose;
    }

    public void setDose(float dose) {
        this.dose = dose;
    }

    public String getTimesADay() {
        return timesADay;
    }

    public void setTimesADay(String timesADay) {
        this.timesADay = timesADay;
    }

    public String getInterval() {
        return interval;
    }

    public void setInterval(String interval) {
        this.interval = interval;
    }

    public int getId() {
        return id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Medication)) return false;
        Medication medicine = (Medication) o;
        return getId() == medicine.getId() && getName().equals(medicine.getName());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId(), getName());
    }

    @Override
    public String toString() {
        return this.name+"\n - Dose (mg/day) : "+this.dose+"\n - I take the medicine: "+this.timesADay+"\n - When : "+this.interval;
    }

    @Override
    public int compareTo(Medication o) {
        return this.name.compareToIgnoreCase(o.name);
    }
}
