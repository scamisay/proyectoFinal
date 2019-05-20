package ar.edu.itba.pf.domain;

import java.util.List;

public abstract class Action {

    private int id;
    private double probability;
    private List<Parameter> parameters;

    public abstract double apply();

    public abstract boolean decide();

    public int getId() {
        return id;
    }

    public double getProbability() {
        return probability;
    }
}
