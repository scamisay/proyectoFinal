package ar.edu.itba.pf.domain.environment.impl;

import ar.edu.itba.pf.domain.drone.Drone;
import ar.edu.itba.pf.domain.environment.CellularAutomaton;
import ar.edu.itba.pf.domain.environment.Pair;
import ar.edu.itba.pf.domain.environment.exceptions.BoundaryException;
import ar.edu.itba.pf.domain.environment.objects.EnvironmentObject;
import ar.edu.itba.pf.domain.environment.objects.combustible.CombustibleObject;
import ar.edu.itba.pf.domain.environment.windengine.WindStrategy;

import java.util.*;
import java.util.function.Function;

import static java.lang.Math.sqrt;
import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toList;

public class CellularAutomatonImpl implements CellularAutomaton {

    private int width;
    private int height;
    private long t;
    private Cell[][] cells;
    private List<Drone> drones = new ArrayList<>();
    private WindStrategy windStrategy;
    private Random randomGenerator = new Random();

    public CellularAutomatonImpl(int width, int height) {
        init(width, height);
    }

    @Override
    public void setSeed(long seed) {
        randomGenerator = new Random(seed);
    }

    @Override
    public void init(int width, int height) {
        this.width = width;
        this.height = height;
        t = 0;
        cells = new Cell[width][height];
        initializeCells();
    }

    @Override
    public Cell getCell(int x, int y) {
        return cells[x][y];
    }

    private void initializeCells() {
        for(int x = 0; x < width; x++){
            for(int y = 0; y < height ; y++){
                cells[x][y] = new Cell(x,y, this);
            }
        }
    }

    @Override
    public void addElement(int x, int y, EnvironmentObject object) {
        checkBoundaries(x,y);
        cells[x][y].addObject(object);
    }

    @Override
    public void addElement(Pair pair, EnvironmentObject object) {
        addElement(pair.x, pair.y, object);
        object.setCell(cells[pair.x][pair.y]);
    }

    @Override
    public void addWindStrategy(WindStrategy windStrategy) {
        this.windStrategy = windStrategy;
    }

    private void checkBoundaries(int x, int y){
        if(x < 0 || x >= width || y < 0 || y >= height){
            throw new BoundaryException(width, height, x, y);
        }
    }

    @Override
    public void evolve() {
        t++;
        for(int x = 0; x < width; x++){
            for (int y = 0; y < height; y++){
                Cell cell = cells[x][y];

                /**
                 * actualizo el vector viento en la celula de haber una estrategia para el mismo
                 */
                if(windStrategy != null){
                    double windX = windStrategy.getX(x,y,t);
                    double windY = windStrategy.getY(x,y,t);
                    cell.updateWind(windX, windY);
                }

                /**
                 * evoluciono el estado de la celula
                 */
                cell.evolve();
            }
        }

        for(int x = 0; x < width; x++){
            for (int y = 0; y < height; y++){
                Cell cell = cells[x][y];

                /**
                 * evoluciono el estado de la celula
                 */
                cell.writeStructuresForNextTurn();
            }
        }

        //todo: compatibilizar el t del ambiente con el dt de los drones
        for(Drone drone : drones){
            drone.evolve();
        }
    }

    @Override
    public int getWidth() {
        return width;
    }

    @Override
    public int getHeight() {
        return height;
    }

    @Override
    public long getTime() {
        return t;
    }

    @Override
    public List<Pair> iterate() {
        List<Pair> pairs = new ArrayList<>();
        for(int x = 0; x<width; x++){
            for(int y = 0; y < height; y++){
                pairs.add(new Pair(x,y));
            }
        }
        return pairs;
    }

    private String printer(Function<Cell, String> printInCell){
        StringBuffer sb = new StringBuffer();
        for(int y = height-1; y >= 0; y--){
            for(int x = 0; x<width; x++){
                sb.append(String.format("[ %s ] ",printInCell.apply(cells[x][y])));
            }
            sb.append("\n");
        }
        return sb.toString();
    }

    @Override
    public <T> Map<Integer, Map<Integer, T>> buildMatrix(Function<Cell, T> transformation) {
        Map<Integer, Map<Integer, T>> matrix = new HashMap<>();
        for(int y = height-1; y >= 0; y--){
            Map<Integer, T> subMatrix = new HashMap<>();
            for(int x = 0; x<width; x++){
                T element = transformation.apply(cells[x][y]);
                if(element != null){
                    subMatrix.put(x, element);
                }
            }
            if(!subMatrix.isEmpty()){
                matrix.put(y, subMatrix);
            }
        }
        return matrix;
    }

    @Override
    public void addDrone(Drone drone) {
        //todo: controlar que no se repita el id
        if(drone != null){
            drones.add(drone);
            drone.setAutomaton(this);
        }
    }

    @Override
    public Cell getCell(double x, double y) {
        return getCell((int)Math.floor(x), (int)Math.floor(y));
    }

    @Override
    public String printObjects() {
        return printer(cell ->
                cell.getObjects()
                .stream()
                .map(EnvironmentObject::getStringRepresentarion)
                .filter(s -> !s.isEmpty())
                .collect(joining(" "))
        );
    }

    @Override
    public String printTemperatures() {
        return printer(cell -> String.format("%7.2f",cell.getTemperature()));
    }

    @Override
    public String printCardinals() {
        return printer(cell -> String.format("%d,%d",cell.getX(), cell.getY()));
    }

    @Override
    public String printAshes() {
        return printer(cell -> String.format("%7.2f",cell.getAshes()));
    }


    @Override
    public String printWind() {
        return printer(cell -> {
            double x = cell.getWind().x;
            double y = cell.getWind().y;
            double modulus = sqrt(x*x + y*y);
            double angle =  Math.atan2(y, x);
            return String.format("(%5.2f,%5.2f)",modulus, angle);
        });
    }

    @Override
    public List<CombustibleObject> getCombustionableObjects() {
        return iterate().stream()
                .map(pair -> getCell(pair))
                .map(cell -> cell.getCombustibleObjects())
                .flatMap(Collection::stream)
                .collect(toList());
    }

    @Override
    public boolean isOnFire() {
        return getCombustionableObjects().stream()
                .filter(CombustibleObject::isOnFire)
                .findFirst().isPresent();
    }

    @Override
    public double generateRandomDouble() {
        return randomGenerator.nextDouble();
    }

    @Override
    public List<Drone> getDrones() {
        return drones;
    }

    private Cell getCell(Pair pair) {
        return cells[pair.x][pair.y];
    }
}
