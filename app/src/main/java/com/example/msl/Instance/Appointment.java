package com.example.msl.Instance;

import java.sql.Time;
import java.util.Date;
import java.util.Objects;

public class Appointment {

    private Date appointmentDate;
    private String appointmentTime;
    private String details;
    private int idDoctor;
    private int idClinica;
    private int id;
    private String doctor;
    private String clinic;
    private String address;
    private String specialization;

    public Appointment(int id,Date appointmentDate, String appointmentTime, String details, String doctor, String clinic, String address, String specialization) {
        this.id = id;
        this.appointmentDate = appointmentDate;
        this.appointmentTime = appointmentTime;
        this.details = details;
        this.doctor = doctor;
        this.clinic = clinic;
        this.address = address;
        this.specialization = specialization;
    }

    public Appointment(Date appointmentDate, String appointmentTime, String details, int idDoctor, int idClinica) {
        this.appointmentDate = appointmentDate;
        this.appointmentTime = appointmentTime;
        this.details = details;
        this.idDoctor = idDoctor;
        this.idClinica = idClinica;
    }

    public String getDoctor() {
        return doctor;
    }

    public String getClinic() {
        return clinic;
    }

    public String getAddress() {
        return address;
    }

    public String getSpecialization() {
        return specialization;
    }

    public Date getAppointmentDate() {
        return appointmentDate;
    }

    public String getAppointmentTime() {
        return appointmentTime;
    }

    public String getDetails() {
        return details;
    }

    public int getIdDoctor() {
        return idDoctor;
    }

    public int getIdClinica() {
        return idClinica;
    }

    public int getId() {
        return id;
    }

    @Override
    public String toString() {
        return "Appointment{" +
                "appointmentDate=" + appointmentDate +
                ", appointmentTime=" + appointmentTime +
                ", details='" + details + '\'' +
                ", idDoctor=" + idDoctor +
                ", idClinica=" + idClinica +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Appointment)) return false;
        Appointment that = (Appointment) o;
        return getAppointmentDate().equals(that.getAppointmentDate()) && getAppointmentTime().equals(that.getAppointmentTime()) && getDoctor().equals(that.getDoctor()) && getClinic().equals(that.getClinic()) && getSpecialization().equals(that.getSpecialization());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getAppointmentDate(), getAppointmentTime(), getDoctor(), getClinic(), getSpecialization());
    }
}
