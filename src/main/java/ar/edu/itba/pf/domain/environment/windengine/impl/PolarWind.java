package ar.edu.itba.pf.domain.environment.windengine.impl;

import ar.edu.itba.pf.domain.environment.windengine.WindStrategy;

import static java.lang.Math.PI;
import static java.lang.Math.cos;
import static java.lang.Math.sin;

public class PolarWind implements WindStrategy {

    private double modulus;
    private double angle;

    public PolarWind(double modulus, double angle) {
        this.modulus = modulus;
        this.angle = angle;
    }

    @Override
    public double getX(int x, int y, int t) {
        return modulus*cos(angle);
    }

    @Override
    public double getY(int x, int y, int t) {
        return modulus*sin(angle);
    }
}
