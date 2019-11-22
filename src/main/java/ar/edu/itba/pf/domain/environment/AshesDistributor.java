package ar.edu.itba.pf.domain.environment;

import static java.lang.Math.PI;
import static java.lang.StrictMath.pow;

public class AshesDistributor {

    private static final double BASE = PI/256;
    private static final double EPSILON = 1e-6;
    private static final double PROPORTION_FOR_TRANSITION = .8;//se transfiere el 80%

    public static double calculateDistribution(double angle){
        double exp;
        if( 0 <= angle && angle <= (PI/4 + EPSILON)){
            exp = angle;
        }else if((PI*(7./4)- EPSILON) <= angle && angle <= PI*2){
            exp = -(angle-2*PI);
        }else{
            return 0;
        }
        return pow(BASE, exp) * PROPORTION_FOR_TRANSITION;
    }
}
