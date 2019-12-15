package ar.edu.itba.pf.domain.environment.objects.combustible;

import ar.edu.itba.pf.domain.helper.ValidatorHelper;
import ar.edu.itba.pf.domain.environment.exceptions.ConsumedByFireException;
import ar.edu.itba.pf.domain.environment.impl.Cell;
import ar.edu.itba.pf.domain.environment.objects.EnvironmentObject;

import static ar.edu.itba.pf.domain.environment.impl.Cell.TEMPERATURE_FOR_A_METER_BY_TIME;
import static ar.edu.itba.pf.domain.environment.impl.Cell.MAX_WIND_SPEED_TOLERANCE_FOR_BURNING;

public abstract class CombustibleObject implements EnvironmentObject {

    private int z;
    private double mass;
    private double combustionPerTime;
    private boolean onFire;
    private double height;
    private Cell cell;

    public CombustibleObject(int z, double mass, double combustionByAreaPerTime, double height) {
        this.z = z;
        this.mass = mass;
        this.height = height;
        this.combustionPerTime = combustionByAreaPerTime * height;
    }

    @Override
    public int getZ() {
        return z;
    }

    public boolean isOnFire() {
        return onFire;
    }

    public void setFireOn(){
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

        calculateMoistureLevel();
        if(moistureLevel <= 0){
            setFireOff();
        }

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

    private static final double DRY_LEVEL = 100;
    private double moistureLevel = DRY_LEVEL;
    protected void calculateMoistureLevel(){
        moistureLevel = moistureLevel + generatedDryByHeat() - cell.getAccumulatedWater();
        if(moistureLevel > DRY_LEVEL ){
            moistureLevel = DRY_LEVEL;
        }else if(moistureLevel < 0){
            moistureLevel = 0;
        }
    }

    public double getMoistureLevel() {
        return moistureLevel;
    }

    //me quedo con el 1% del calor generado
    private double generatedDryByHeat(){
        if(cell.getTemperature() > 0){
            return cell.getTemperature() * .01;
        }else {
            return generateHeat() * .005;
        }
    }

    private double getMoistureLevelIndex(){
        return moistureLevel/DRY_LEVEL;
    }

    private void setFireOff(){
        onFire = false;
    }
    /**
     * LABEL: HEAT PROPAGATION
     */
    private double generateHeat() {
        return TEMPERATURE_FOR_A_METER_BY_TIME * height * getMoistureLevelIndex();
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

    /*@Override
    public String getCharacterRepresentarion() {
        return isOnFire()?"F":"";
    }*/
}
