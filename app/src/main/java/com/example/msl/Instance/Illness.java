package com.example.msl.Instance;

public class Illness implements Comparable<Illness> {
    private int id;
    private String name;
    private String category;
    private String isRare;
    private int risk;



    public Illness(int id, String name, String category, String isRare, int risk) {
        this.id = id;
        this.name = name;
        this.category = category;
        this.isRare = isRare;
        this.risk = risk;
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String isRare() {
        return isRare;
    }

    public void setRare(String rare) {
        isRare = rare;
    }

    public int getRisk() {
        return risk;
    }

    public void setRisk(int risk) {
        this.risk = risk;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Override
    public boolean equals(Object obj) {
        if(!(obj instanceof Illness)) {
            return false;
        }
        Illness illness = (Illness) obj;
        return this.id == illness.getId() && this.name.equals(illness.getName());
    }

    @Override
    public String toString() {
       return this.name+"\n - Category: "+this.category+"\n - Is Rare: "+this.isRare+"\n - Risk: "+this.risk;
    }

    @Override
    public int compareTo(Illness o) {
        return this.name.compareToIgnoreCase(o.name);
    }
}
