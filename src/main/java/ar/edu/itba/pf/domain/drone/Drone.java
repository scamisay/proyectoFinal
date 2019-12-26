package ar.edu.itba.pf.domain.drone;


import ar.edu.itba.pf.domain.environment.CellularAutomaton;
import ar.edu.itba.pf.domain.environment.impl.Cell;
import ar.edu.itba.pf.domain.environment.impl.NeighbourOrientation;
import ar.edu.itba.pf.domain.environment.objects.DroneObject;
import ar.edu.itba.pf.domain.helper.ValidatorHelper;

import java.util.HashMap;
import java.util.Map;

import static ar.edu.itba.pf.domain.drone.DroneAction.*;
import static ar.edu.itba.pf.domain.drone.FlyingState.*;

public class Drone implements DroneObject {

    private static final double WATER_CAṔACITY = 15;
    private static final double WATER_PER_TIME = .5;
    private static final long MAX_DRONES_PER_CELL = 4;

    private int id;
    private double mass;

    //recursos
    private double water;
    private double energy;

    //valores internos
    private Cell cell;
    private Cell target; //celda vecina
    private int z;
    private CellularAutomaton automaton;

    private static final double RADIUS = .5;

    private FiniteStateAutomata fsm;
    private FlyingState state;

    private double temperatureSelector;
    private FightProgress fightProgress;
    private long maxDronesPerCell = MAX_DRONES_PER_CELL;

    public Drone(int id, double mass, double water, double energy, int height) {
        this.id = id;
        this.mass = mass;
        this.water = water;
        this.energy = energy;
        this.z = height;

        state = LANDED;
        fsm = new FiniteStateAutomata();
        fsm.addTransition(LANDED, LAND, LANDED );
        fsm.addTransition(LANDED, SEARCH_TARGET, TO_TAGET );
        fsm.addTransition(TO_TAGET, SEARCH_TARGET, TO_TAGET );
        fsm.addTransition(TO_TAGET, STOP, OVER_TARGET );
        fsm.addTransition(TO_TAGET, LAND, LANDED );
        fsm.addTransition(TO_TAGET, FLY, OVER_TARGET );
        fsm.addTransition(OVER_TARGET, SEARCH_TARGET, TO_TAGET );
        fsm.addTransition(OVER_TARGET, HOLD, OVER_TARGET );
        fsm.addTransition(OVER_TARGET, LAND, LANDED );
        fsm.addTransition(OVER_TARGET, ESCAPE, ON_ESCAPE );
        fsm.addTransition(ON_ESCAPE, ESCAPE, ON_ESCAPE );
        fsm.addTransition(ON_ESCAPE, SEARCH_TARGET, TO_TAGET );


        this.temperatureSelector = MAX_SELECTOR_VALUE;
    }

    private static final double MAX_SELECTOR_VALUE = 1;
    private static final double MIN_SELECTOR_VALUE = 0;

    public double getTemperatureSelector() {
        return temperatureSelector;
    }

    public void setTemperatureSelector(double temperatureSelector) {
        if(temperatureSelector < MIN_SELECTOR_VALUE || temperatureSelector > MAX_SELECTOR_VALUE){
            throw new RuntimeException("Selector de temperatura invalido" );
        }
        this.temperatureSelector = temperatureSelector;
    }

    public void setMaxDronesPerCell(long maxDronesPerCell) {
        ValidatorHelper.checkPositive(maxDronesPerCell);
        this.maxDronesPerCell = maxDronesPerCell;
    }

    private void land(){

    }

    /**
     * busqueda por calor que produce un movimiento por paso temporal
     */
    private boolean searchTarget(){
        Cell possibleNextMove = findClosestTarget();
        if(possibleNextMove != null){
            cell = possibleNextMove;
            return true;
        }else {
            return false;
        }
    }

    private double findMaxTemperatureInNeighbours(){
        return cell.getNeighbours().stream()
                .mapToDouble(Cell::getTemperature)
                .max().getAsDouble();
    }

    private double findAvgTemperatureInNeighbours(){
        return cell.getNeighbours().stream()
                .mapToDouble(Cell::getTemperature)
                .average().getAsDouble();
    }

    private double findSelectedValue(double selection){
        double max = findMaxTemperatureInNeighbours();
        double avg = findAvgTemperatureInNeighbours();
        return (max-avg)*selection+avg;
    }

    private Cell findClosestTarget() {
        double lowerBound = findSelectedValue(temperatureSelector);
        return cell.getNeighbours().stream()
                .filter(c -> c.getTemperature() > 0)
                .filter(c -> c.getTemperature() >= lowerBound)
                .filter(c -> c.getNumberOfDronesFlying() < maxDronesPerCell)
                .findAny().orElse(null);
    }

    private Cell findNeighbourCellOnFire() {
        double lowerBound = findSelectedValue(temperatureSelector);
        return cell.getNeighbours().stream()
                .filter(Cell::isOnfire)
                .filter(c -> c.getTemperature() > 0)
                .filter(c -> c.getTemperature() >= lowerBound)
                .filter(c -> c.getNumberOfDronesFighting() < maxDronesPerCell)
                .filter(Cell::isTemperatureLocalMax)
                .findAny().orElse(null);
    }

    private void stop(){

    }

    private void fly(){

    }

    @Override
    public double evolve() {
        DroneAction action = null;
        switch (state){
            case LANDED:
                action = searchTarget() ? SEARCH_TARGET : LAND;
                break;
            case TO_TAGET:
                Cell neighbourOnFire = findNeighbourCellOnFire();
                if(neighbourOnFire != null){
                    target = neighbourOnFire;
                    stop();
                    action = STOP;
                }else {
                    action = searchTarget() ? SEARCH_TARGET : LAND;
                }
                break;
            case OVER_TARGET:
                //todo: comparar calificacion: target.isOnfire()
                if(target.isTemperatureLocalMax()){
                    if(continueFigthing()){
                        target.receiveWater(rain());
                        evaluateFightProgress(target.getMoistureLevel());
                        action = HOLD;
                    }else{
                        action = ESCAPE;
                    }
                }else {
                    action = SEARCH_TARGET;
                }
                break;
            case ON_ESCAPE:
                if(enoughDistanceFromTarget()){
                    action = SEARCH_TARGET;
                }else {
                    action = walkAwayFromTarget() ? ESCAPE : SEARCH_TARGET;
                }
                break;
        }

        state = fsm.makeTransition(state, action);
        return 0;
    }

    public FlyingState getState() {
        return state;
    }

    private int minDistanceFromTarget = 4;
    private boolean enoughDistanceFromTarget() {
        return cell.distanceTo(target) >= minDistanceFromTarget;
    }

    private NeighbourOrientation escapeOrientation;

    /**
     * Se aleja en orientacion al objetivo siempre que haya espacio en el ambiente para hacerlo
     * @return devuelve si logro moverse
     */
    private boolean walkAwayFromTarget() {
        if(escapeOrientation == null){
            escapeOrientation = NeighbourOrientation.fromTo(cell, target);
        }
        Cell nextPos = cell.moveInOrientation(escapeOrientation);
        if(nextPos != null){
            cell = nextPos;
            return true;
        }else{
            return false;
        }
    }

    private double winningMinTrend = 8;
    private double probToGiveUpOnAFight = .15;

    private boolean continueFigthing() {
        double probGiveUp = automaton.generateRandomDouble();
        return !(fightProgress != null &&
                fightProgress.isWinning() &&
                fightProgress.getTrend() >= winningMinTrend &&
                probToGiveUpOnAFight >= probGiveUp);
    }

    private void evaluateFightProgress(double moistureLevel) {
        if(fightProgress == null){
            fightProgress = new FightProgress(moistureLevel);
        }else {
            fightProgress.evaluate(moistureLevel);
        }
    }

    private double rain(){
        if(water - WATER_PER_TIME >= 0){
            water -= WATER_PER_TIME;
        }
        return WATER_PER_TIME;
    }

    @Override
    public int getX() {
        return cell.getX();
    }

    @Override
    public int getY() {
        return cell.getY();
    }

    @Override
    public int getZ() {
        return z;
    }

    @Override
    public String getStringRepresentarion() {
        return "D";
    }

    @Override
    public void setCell(Cell cell) {
        ValidatorHelper.checkNotNull(cell);
        this.cell = cell;

        /**
         * evito que se inicializen dos drones en la misma posicion
         */
        int maxZ = automaton.getDrones().stream()
                .filter(drone -> drone.getCell().equals(cell))
                .filter(drone -> drone.getId() != id)
                .mapToInt(Drone::getZ).max().orElse(0);
        if(maxZ >= z){
            z = maxZ + 1;
        }
    }

    public void setAutomaton(CellularAutomaton automaton) {
        this.automaton = automaton;
    }

    @Override
    public double getRadio() {
        return 0;
    }

    public int getId() {
        return id;
    }

    public double getMass() {
        return mass;
    }

    public double getWater() {
        return water;
    }

    public double getEnergy() {
        return energy;
    }

    public Cell getCell() {
        return cell;
    }

    class FiniteStateAutomata{
        private Map<FlyingState, Map<DroneAction, FlyingState>> states = new HashMap<>();

        public void addTransition(FlyingState inState, DroneAction action, FlyingState outState){
            if( !states.containsKey(inState) ) {
                Map<DroneAction, FlyingState> map = new HashMap<>();
                map.put(action, outState);
                states.put(inState, map);
            }else{
                states.get(inState).put(action, outState);
            }
        }

        public FlyingState makeTransition(FlyingState inState, DroneAction action){
            if(states.containsKey(inState) && states.get(inState).containsKey(action)){
                return states.get(inState).get(action);
            }else {
                throw new RuntimeException("Invalid Drone Transition");
            }
        }
    }

    public void setMinDistanceFromTarget(int minDistanceFromTarget) {
        this.minDistanceFromTarget = minDistanceFromTarget;
    }

    public void setWinningMinTrend(double winningMinTrend) {
        this.winningMinTrend = winningMinTrend;
    }

    public void setProbToGiveUpOnAFight(double probToGiveUpOnAFight) {
        this.probToGiveUpOnAFight = probToGiveUpOnAFight;
    }

    class FightProgress{
        private int trend;
        private boolean winning;
        private double previousValue;

        public FightProgress(double moistureLevel) {
            previousValue = moistureLevel;
        }

        public int getTrend() {
            return trend;
        }

        public boolean isWinning() {
            return winning;
        }

        void evaluate(double currentValue){
            boolean result = currentValue < previousValue;
            previousValue = currentValue;
            if(result != winning){
                winning = result;
                trend = 1;
            }else {
                trend++;
            }
        }
    }

}
