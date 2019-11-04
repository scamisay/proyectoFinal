package ar.edu.itba.pf.domain.environment;

import static java.lang.Math.PI;
import static java.lang.StrictMath.pow;

public class AshesDistributor {

    private static final double BASE = PI/256;
    private static final double EPSILON = 1e-6;

    public static double calculateDistribution(double angle){
        if( 0 <= angle && angle <= (PI/4 + EPSILON)){
            return pow(BASE, angle);
        }else if((PI*(7/4)- EPSILON) <= angle && angle <= PI*2){
            return pow(BASE, -(angle-2*PI));
        }else{
            return 0;
        }
    }
}
