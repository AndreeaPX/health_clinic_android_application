package com.example.msl.Instance;

public class RDoctor {
    private int idDoctor;
    private int idClinic;

    public RDoctor(int idDoctor, int idClinic) {
        this.idDoctor = idDoctor;
        this.idClinic = idClinic;
    }

    public int getIdDoctor() {
        return idDoctor;
    }

    public int getIdClinic() {
        return idClinic;
    }

    @Override
    public String toString() {
        return "RDoctor{" +
                "idDoctor=" + idDoctor +
                ", idClinic=" + idClinic +
                '}';
    }
}
