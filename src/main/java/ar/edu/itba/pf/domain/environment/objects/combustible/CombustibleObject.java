package ar.edu.itba.pf.domain.environment.objects.combustible;

import ar.edu.itba.pf.domain.Helper;
import ar.edu.itba.pf.domain.environment.exceptions.ConsumedByFireException;
import ar.edu.itba.pf.domain.environment.impl.Cell;
import ar.edu.itba.pf.domain.environment.objects.EnvironmentObject;

public abstract class CombustibleObject implements EnvironmentObject {

    private double z;
    private double mass;
    private double combustionPerTime;
    private boolean onFire;
    private Cell cell;

    public CombustibleObject(double z, double mass, double combustionPerTime) {
        this.z = z;
        this.mass = mass;
        this.combustionPerTime = combustionPerTime;
    }

    @Override
    public double getZ() {
        return z;
    }

    public boolean isOnFire() {
        return onFire;
    }

    public void setOnFire(){
        onFire = true;
    }

    /**
     * LABEL: HEAT PROPAGATION
     */
    public double consume(){
        double heat = 0;
        if(isOnFire()){
            heat = generateHeat();
            if(mass > combustionPerTime){
                mass -= combustionPerTime;
            }else {
                throw new ConsumedByFireException();
            }
        }
        return heat;
    }

    /**
     * TODO: setear el valor como expresion o constante
     * LABEL: HEAT PROPAGATION
     */
    private double generateHeat() {
        return 50;
    }

    @Override
    public double evolve() {
        try{
            return consume();
        }catch (ConsumedByFireException e){
            cell.removeObject(this);
        }
        return 0;
    }

    @Override
    public void setCell(Cell cell) {
        Helper.checkNotNull(cell);
        this.cell = cell;
    }
}
