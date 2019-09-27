package ar.edu.itba.pf.domain;

import java.util.Collection;

public class Helper {

    public static void checkNotNull(Object ... args){
        for(Object andArg : args){
            if(andArg == null){
                throw new RuntimeException("Todos los paramentros son obligatorios");
            }
        }
    }

    public static void checkNotEmpty(String ... args){
        for(String andArg : args){
            if(andArg == null || andArg.isEmpty()){
                throw new RuntimeException("Todos los paramentros son obligatorios");
            }
        }
    }

    public static void checkNotEmpty(Collection... args){
        for(Collection aCollection : args){
            if(aCollection == null || aCollection.isEmpty()){
                throw new RuntimeException("Todos los paramentros son obligatorios");
            }
        }
    }

    public static void checkPositive(Number ... numbers){
        for(Number aNumber: numbers){
            if(aNumber == null || aNumber.intValue() < 0){
                throw new RuntimeException("La cantidad debe ser positiva");
            }
        }
    }

    public static void checkGreaterThan(Integer minimum, Number ... numbers){
        for(Number aNumber: numbers){
            if(aNumber == null || aNumber.intValue() < minimum){
                throw new RuntimeException("La cantidad debe ser mayor o igual a "+minimum);
            }
        }
    }
}
