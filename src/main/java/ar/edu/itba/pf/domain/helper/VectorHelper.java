package ar.edu.itba.pf.domain.helper;

import ar.edu.itba.pf.domain.environment.PairDouble;

import static java.lang.Math.acos;

public class VectorHelper {

    public static double angleBetweenVectors(PairDouble pair1, PairDouble pair2){
        double arg = (pair1.x * pair2.x + pair1.y * pair2.y)/(pair1.getModule()*pair2.getModule());
        return acos(arg);
    }
}
