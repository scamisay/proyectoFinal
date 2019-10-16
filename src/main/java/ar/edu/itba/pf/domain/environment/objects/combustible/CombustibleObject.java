package ar.edu.itba.pf.domain.environment.objects.combustible;

import ar.edu.itba.pf.domain.helper.ValidatorHelper;
import ar.edu.itba.pf.domain.environment.exceptions.ConsumedByFireException;
import ar.edu.itba.pf.domain.environment.impl.Cell;
import ar.edu.itba.pf.domain.environment.objects.EnvironmentObject;

import static ar.edu.itba.pf.domain.environment.impl.Cell.COMBUSTIBLE_CONSUMED_BY_TIME;
import static ar.edu.itba.pf.domain.environment.impl.Cell.MAX_WIND_SPEED_TOLERANCE_FOR_BURNING;

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
     *
     * Si el viento supera MAX_WIND_SPEED_TOLERANCE_FOR_BURNING el fuego se apaga
     *
     * Al consumirse toda la masa del objeto se lanza una ConsumedByFireException
     */
    public double consume(){
        double heat = 0;

        if(cell.getWind().getModule() > MAX_WIND_SPEED_TOLERANCE_FOR_BURNING){
            setFireOff();
        }

        if(isOnFire()){
            heat = generateHeat();
            if(mass > combustionPerTime){
                mass -= combustionPerTime;
            }else {
                setFireOff();
                throw new ConsumedByFireException();
            }
        }
        return heat;
    }

    private void setFireOff(){
        onFire = false;
    }
    /**
     * LABEL: HEAT PROPAGATION
     */
    private double generateHeat() {
        return COMBUSTIBLE_CONSUMED_BY_TIME;
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
        ValidatorHelper.checkNotNull(cell);
        this.cell = cell;
    }

    @Override
    public String getCharacterRepresentarion() {
        return isOnFire()?"F":"";
    }
}
