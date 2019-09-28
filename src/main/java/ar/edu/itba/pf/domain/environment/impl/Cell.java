package ar.edu.itba.pf.domain.environment.impl;


import ar.edu.itba.pf.domain.Helper;
import ar.edu.itba.pf.domain.environment.CellularAutomaton;
import ar.edu.itba.pf.domain.environment.exceptions.cellexception.MoreThanOneCombustionObject;
import ar.edu.itba.pf.domain.environment.objects.EnvironmentObject;
import ar.edu.itba.pf.domain.environment.objects.combustible.CombustibleObject;

import java.util.*;

import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toSet;

public class Cell {

    private int x;
    private int y;
    private CellularAutomaton cellularAutomaton;
    private double temperature = NORMAL_TEMERATURE;

    /**
     * constants
     */
    public static final double LOOSE_RADIATION_PER_CELL = 10;
    public static final double NORMAL_TEMERATURE = 0;

    public Cell(int x, int y, CellularAutomaton cellularAutomaton) {
        Helper.checkPositive(x,y);
        Helper.checkNotNull(cellularAutomaton);
        this.x = x;
        this.y = y;
        this.cellularAutomaton = cellularAutomaton;
        initializeNeighbours();
        initializeTemperatures();
    }

    public void initializeTemperatures() {
        temperatures = new HashMap<>();
        for(NeighbourOrientation neighbourOrientation : neighbourOrientations){
            temperatures.put(neighbourOrientation, new Double(0));
        }
    }

    /**
     * Vecinos validos para esta celula
     */
    private Set<NeighbourOrientation> neighbourOrientations = new HashSet<>();

    /**
     * temperatura generada por los vecinos
     */
    private Map<NeighbourOrientation, Double> temperatures;

    private List<EnvironmentObject> objects = new LinkedList<>();

    private void initializeNeighbours(){
        for(int xGen = this.x-1; xGen <= this.x+1 ; xGen++ ){
            if(xGen < 0 || xGen >= cellularAutomaton.getWidth()){
                continue;
            }
            for(int yGen = this.y-1; yGen <= this.y+1 ; yGen++ ){
                if(yGen < 0 || yGen >= cellularAutomaton.getHeight()){
                    continue;
                }
                if(xGen == this.x && yGen == this.y){
                    continue;
                }

                //valores normalizados para coincidir con las orientaciones de vecinos
                int xNormalized = xGen - this.x;
                int yNormalized = yGen - this.y;
                NeighbourOrientation orientation = NeighbourOrientation.findByPosition(xNormalized, yNormalized);
                neighbourOrientations.add(orientation);
            }
        }
    }

    //todo: controlar superposiciones
    public void addObject(EnvironmentObject object) {
        if(object instanceof CombustibleObject && !getCombustibleObjects().isEmpty()){
            throw new MoreThanOneCombustionObject(this);
        }
        objects.add(object);
    }

    /**
     * LABEL: HEAT PROPAGATION
     * TODO: PENDING TEST - al consumirse un objeto debe desaparecer de la celula
     */
    public void removeObject(EnvironmentObject object) {
        objects.removeIf(o -> o.equals(object));
    }

    public void evolve() {
        updateTemperatureByRadiation();

        /**
         * evoluciono los no combustibles
         */
        getObjects().stream()
                .filter(o -> !(o instanceof CombustibleObject))
                .forEach( object -> object.evolve());

        /**
         * evoluciono el elemento que genera calor
         */
        if(!getCombustibleObjects().isEmpty()){
            double temperatureGenerated = getCombustibleObjects().get(0).evolve();
            spreadHeat(temperatureGenerated);
        }

        /**
         * transmito el calor que ha llegado de celulas vecinas
         */
        transmitHeatReceived();

        initializeTemperatures();
    }


    public void spreadHeat(double irradiatedTemperature){
        temperature += irradiatedTemperature;
        double irradiatedTemperatureToNeighbour = irradiatedTemperature - LOOSE_RADIATION_PER_CELL;
        if(irradiatedTemperatureToNeighbour <= 0){
            return;
        }
        neighbourOrientations.forEach( orientation -> transferHeatToNeighbourCell(orientation, irradiatedTemperatureToNeighbour) );

    }

    private void transferHeatToNeighbourCell(NeighbourOrientation neighbourOrientation, double heat){
        try{
            Cell neighbour = getCellFromOrientation(neighbourOrientation);
            neighbour.updateNeighbourTemperature(heat, neighbourOrientation);
        }catch (Exception e){
            System.out.println(1);
        }
    }

    public void transmitHeatReceived() {
        neighbourOrientations.forEach( orientation -> {
            /**
             * busco la temperatura que me fue irradiada en esta direccion. Si supera el umbral la transmito
             */
            double irradiatedTemperatureToNeighbour = temperatures.get(orientation) - LOOSE_RADIATION_PER_CELL;
            if(irradiatedTemperatureToNeighbour <= 0){
                return;
            }

            NeighbourOrientation mainTargetOrientation = orientation.invert();
            List<NeighbourOrientation> targetOrientations = new ArrayList<>();
            targetOrientations.add(mainTargetOrientation);
            if(orientation.isDiagonal()){
                targetOrientations.add(NeighbourOrientation.findByPosition(0, mainTargetOrientation.y));
                targetOrientations.add(NeighbourOrientation.findByPosition( mainTargetOrientation.x,0));
            }

            targetOrientations.forEach(targetOrientation -> transferHeatToNeighbourCell(targetOrientation, irradiatedTemperatureToNeighbour));
        });
    }

    private void updateTemperatureByRadiation() {
        temperature = neighbourOrientations.stream()
                .map( orientation -> temperatures.get(orientation))
                //.filter(neighbourRadiation -> neighbourRadiation > LOOSE_RADIATION_PER_CELL)
                .mapToDouble(neighbourRadiation -> neighbourRadiation)
                .sum();
    }

    public void updateNeighbourTemperature(double temperature, NeighbourOrientation myOrientation){
        NeighbourOrientation neighbourOrientation = myOrientation.invert();
        double oldTemperature = temperatures.get(neighbourOrientation);
        temperatures.put(neighbourOrientation, oldTemperature + temperature);
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public double getTemperature() {
        return temperature;
    }

    public List<EnvironmentObject> getObjects() {
        return objects;
    }

    public List<EnvironmentObject> getCombustibleObjects(){
        return objects.stream()
                .filter(o -> o instanceof CombustibleObject)
                .collect(toList());
    }

    private Set<Cell> getNeighbours(){
        return neighbourOrientations.stream()
                .map(orientation -> getCellFromOrientation(orientation))
                .collect(toSet());
    }

    private Cell getCellFromOrientation(NeighbourOrientation orientation) {
        return cellularAutomaton.getCell(x + orientation.x, y + orientation.y);
    }

    public Set<NeighbourOrientation> getNeighbourOrientations() {
        return neighbourOrientations;
    }
}
