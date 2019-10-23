package ar.edu.itba.pf.domain.environment.impl;


import ar.edu.itba.pf.domain.environment.CellularAutomaton;
import ar.edu.itba.pf.domain.environment.PairDouble;
import ar.edu.itba.pf.domain.environment.action.ActionByTurn;
import ar.edu.itba.pf.domain.environment.action.ActionType;
import ar.edu.itba.pf.domain.environment.exceptions.cellexception.MoreThanOneCombustionObject;
import ar.edu.itba.pf.domain.environment.objects.EnvironmentObject;
import ar.edu.itba.pf.domain.environment.objects.combustible.CombustibleObject;
import ar.edu.itba.pf.domain.helper.ValidatorHelper;

import java.util.*;

import static ar.edu.itba.pf.domain.helper.VectorHelper.angleBetweenVectors;
import static java.lang.StrictMath.cos;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toSet;

public class Cell {

    private int x;
    private int y;
    private CellularAutomaton cellularAutomaton;
    private double temperature = NORMAL_TEMERATURE;
    private PairDouble wind;
    private List<ActionByTurn> actionsByTurn = new ArrayList<>();

    /**
     * Vecinos validos para esta celula
     */
    private Set<NeighbourOrientation> neighbourOrientations = new HashSet<>();

    /**
     * temperatura generada por los vecinos. En el valor del mapa hay un ActionByTurn que tiene el valor
     * de temperatura y el turno cuando aplicarlo
     */
    private Map<NeighbourOrientation, Double> temperatures = new HashMap<>();

    private Map<NeighbourOrientation, Double> nextTurntemperatures = new HashMap<>();

    private List<EnvironmentObject> objects = new LinkedList<>();


    /**
     * constants
     */
    public static final double RADIATION_PROPOTION_PROPAGATION_PER_CELL = .5;
    public static final double NORMAL_TEMERATURE = 0;
    public static final double TEMPERATURE_FOR_A_METER_BY_TIME = 50;
    public static final double MAX_WIND_SPEED_TOLERANCE_FOR_BURNING = 200;
    public static final double MAX_WIND_SPEED_TOLERANCE_FOR_HEATING = 100;

    public Cell(int x, int y, CellularAutomaton cellularAutomaton) {
        ValidatorHelper.checkPositive(x,y);
        ValidatorHelper.checkNotNull(cellularAutomaton);
        this.x = x;
        this.y = y;
        this.cellularAutomaton = cellularAutomaton;
        initializeNeighbours();
        initializeTemperatures();
        wind = PairDouble.ZERO;
    }

    public void initializeTemperatures() {
        //temperatures = new HashMap<>();
        for(NeighbourOrientation neighbourOrientation : neighbourOrientations){
                temperatures.put(neighbourOrientation, 0.);
                nextTurntemperatures.put(neighbourOrientation, 0.);
        }
    }

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
     */
    public void removeObject(EnvironmentObject object) {
        objects.removeIf(o -> o.equals(object));
    }

    public void evolve() {
        //System.out.println(String.format("(%d,%d)",x,y));
        List<ActionType> actions = getActionsForThisTurn();

        temperature = updateTemperatureByRadiation() + updateTemperatureByWind();

        /**
         * evoluciono los no combustibles
         */
        getObjects().stream()
                .filter(o -> !(o instanceof CombustibleObject))
                .forEach( object -> object.evolve());

        /**
         * transmito el calor que ha llegado de celulas vecinas
         */
        transmitHeatReceived();

        /**
         * evoluciono el elemento que genera calor
         */
        if(hasCombustibleObject()){
            /**
             * Si aun no hay fuego lo enciendo
             */
            if(actions.contains(ActionType.START_A_FIRE)){
                startFire();
            }

            CombustibleObject combustibleObject = getCombustibleObjects().get(0);
            if(combustibleObject.isOnFire()){
                spreadFire();
            }

            double temperatureGenerated = combustibleObject.evolve();
            spreadHeat(temperatureGenerated);
        }

        //initializeTemperatures();
    }

    private int getCurrentTurn(){
        return cellularAutomaton.getTime();
    }

    private List<ActionType> getActionsForThisTurn() {
        List<ActionType> actions = new ArrayList<>();
        Iterator<ActionByTurn> it = actionsByTurn.iterator();
        if(it.hasNext()){
            ActionByTurn actionByTurn = it.next();
            if(actionByTurn.getTurn() == getCurrentTurn()){
                actions.add(actionByTurn.getAction());
                it.remove();
            }
        }
        return actions;
    }

    public boolean hasCombustibleObject(){
        return !getCombustibleObjects().isEmpty();
    }

    public void spreadHeat(double irradiatedTemperature){
        temperature += irradiatedTemperature;
        double irradiatedTemperatureToNeighbour = irradiatedTemperature;
        if(irradiatedTemperatureToNeighbour <= 0){
            return;
        }

        neighbourOrientations.forEach( orientation -> transferHeatToNeighbourCell(orientation, irradiatedTemperatureToNeighbour ) );

    }

    public void spreadFire(){
        getNeighbours()
                .stream()
                .filter(Cell::hasCombustibleObject)
                .forEach(Cell::startFireInNextTurn);
    }

    public void startFireInNextTurn(){
        int nextTurn = cellularAutomaton.getTime()+1;
        addActionByTurn(nextTurn, ActionType.START_A_FIRE);
    }

    public void addActionByTurn(int turn, ActionType actionType){
        actionsByTurn.add(new ActionByTurn(turn, actionType));
    }

    private void startFire() {
        getCombustibleObjects()
                .stream()
                .findFirst()
                .ifPresent(CombustibleObject::setOnFire);
    }

    private void transferHeatToNeighbourCell(NeighbourOrientation neighbourOrientation, double heat){
        try{
            Cell neighbour = getCellFromOrientation(neighbourOrientation);
            neighbour.updateNeighbourTemperature(heat * RADIATION_PROPOTION_PROPAGATION_PER_CELL, neighbourOrientation);
        }catch (Exception e){
            /**
             * no hay hacia donde transferir
             */
        }
    }

    public void transmitHeatReceived() {
        neighbourOrientations.forEach( orientation -> {
            /**
             * busco la temperatura que me fue irradiada en esta direccion. Si supera el umbral la transmito
             */
            double irradiatedTemperatureToNeighbour = temperatures.get(orientation);
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

    private double updateTemperatureByRadiation() {
        return neighbourOrientations.stream()
                .mapToDouble(orientation -> temperatures.get(orientation))
                .sum();
    }

    private double updateTemperatureByWind(){
        if(getWind().equals(PairDouble.ZERO)){
            return 0;
        }
        return neighbourOrientations.stream()
                .filter(neighbourRadiation -> temperatures.get(neighbourRadiation) > 0)
                .mapToDouble(neighbourRadiation ->
                        calculateTemperatureDistributedByWind(temperatures.get(neighbourRadiation),neighbourRadiation.getPair(), getWind()))
                .sum();
    }

    private double calculateTemperatureDistributedByWind(double radiatedTemperature, PairDouble radiation, PairDouble wind){
        return radiatedTemperature * cos(angleBetweenVectors(radiation, wind)) * influenceOfWind(wind.getModule());
    }

    private double influenceOfWind(double x) {
        if(x <= MAX_WIND_SPEED_TOLERANCE_FOR_HEATING){
            return x/MAX_WIND_SPEED_TOLERANCE_FOR_HEATING;
        }else {
            return 1;
        }
    }

    public void updateNeighbourTemperature(double temperature, NeighbourOrientation myOrientation){
        NeighbourOrientation neighbourOrientation = myOrientation.invert();
        //double oldTemperature = temperatures.get(neighbourOrientation);
        //temperatures.put(neighbourOrientation, oldTemperature + temperature);

        //int turn = getCurrentTurn()+1;
        //ActionByTurn actionByTurn = new ActionByTurn(turn, ActionType.SPREAD_HEAT, temperature);
        //if(temperatures.get(neighbourOrientation) == null){
            nextTurntemperatures.put(neighbourOrientation, temperature);
        //}
    }

    public void updateWind(double windX, double windY){
        this.wind = new PairDouble(windX, windY);
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public PairDouble getWind() {
        return wind;
    }

    public double getTemperature() {
        return temperature;
    }

    public List<EnvironmentObject> getObjects() {
        return objects;
    }

    public List<CombustibleObject> getCombustibleObjects(){
        return objects.stream()
                .filter(o -> o instanceof CombustibleObject)
                .map( o -> (CombustibleObject)o)
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

    public void writeStructuresForNextTurn() {
        for(NeighbourOrientation neighbourOrientation : nextTurntemperatures.keySet()){
            temperatures.put(neighbourOrientation, nextTurntemperatures.get(neighbourOrientation));
            nextTurntemperatures.put(neighbourOrientation, 0.);
        }
    }
}
