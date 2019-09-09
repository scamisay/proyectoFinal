package ar.edu.itba.pf.domain.environment.objects;

import ar.edu.itba.pf.domain.environment.exceptions.ConsumedByFireException;

public abstract class CombustibleObject implements EnvironmentObject {

    private double z;
    private double mass;
    private double combustionPerTime;
    private boolean onFire;

    @Override
    public double getZ() {
        return z;
    }

    public boolean isOnFire() {
        return onFire;
    }

    public double consume(){
        if(isOnFire()){
            if(mass > combustionPerTime){
                mass -= combustionPerTime;
            }else {
                throw new ConsumedByFireException();
            }
        }
        return 0;
    }

}
