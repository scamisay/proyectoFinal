package ar.edu.itba.pf.domain.drone;


import ar.edu.itba.pf.domain.environment.objects.DroneObject;

public class Drone implements DroneObject {

    private int id;
    private double mass;
    private Tuple3D position;
    private Tuple3D velocity;
    private double water;
    private double energy;

    private static final double HEIGHT = 1;

    public double getZ0() {
        return position.getZ();
    }

    @Override
    public double getX() {
        return 0;
    }

    @Override
    public double getY() {
        return 0;
    }

    @Override
    public double getZ() {
        return 0;
    }

    @Override
    public double getRadio() {
        return 0;
    }
}
