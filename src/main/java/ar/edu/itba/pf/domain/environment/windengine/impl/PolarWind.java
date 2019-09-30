package ar.edu.itba.pf.domain.environment.windengine.impl;

import ar.edu.itba.pf.domain.environment.windengine.WindStrategy;

public class PolarWind implements WindStrategy {

    private double modulus;
    private double angle;

    public PolarWind(double modulus, double angle) {
        this.modulus = modulus;
        this.angle = angle;
    }

    @Override
    public double getX(int x, int y, int t) {
        return Math.abs(modulus)*Math.cos(angle);
    }

    @Override
    public double getY(int x, int y, int t) {
        return Math.abs(modulus)*Math.sin(angle);
    }
}
