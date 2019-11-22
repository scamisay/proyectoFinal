package ar.edu.itba.pf.domain.environment.impl;

import ar.edu.itba.pf.domain.engine.Evolver;
import ar.edu.itba.pf.domain.engine.impl.EvolverImpl;
import ar.edu.itba.pf.domain.environment.CellularAutomaton;
import ar.edu.itba.pf.domain.environment.Pair;
import ar.edu.itba.pf.domain.environment.PairDouble;
import ar.edu.itba.pf.domain.environment.objects.GroundObject;
import ar.edu.itba.pf.domain.environment.objects.combustible.CombustibleObject;
import ar.edu.itba.pf.domain.environment.objects.combustible.GrassCO;
import ar.edu.itba.pf.domain.environment.objects.combustible.TreeCO;
import ar.edu.itba.pf.domain.environment.windengine.impl.PolarWind;
import ar.edu.itba.pf.domain.helper.VectorHelper;
import ar.edu.itba.pf.domain.helper.worldcreator.WorldPart;
import org.junit.Assert;
import org.junit.Test;

import java.util.*;
import java.util.stream.IntStream;

import static ar.edu.itba.pf.domain.environment.AshesDistributor.calculateDistribution;
import static ar.edu.itba.pf.domain.environment.impl.Cell.NORMAL_TEMPERATURE;
import static ar.edu.itba.pf.domain.environment.impl.NeighbourOrientation.*;
import static ar.edu.itba.pf.domain.helper.VectorHelper.angleBetweenVectors;
import static java.lang.Math.PI;
import static java.util.stream.Collectors.toList;

public class CellTest {

    static final int TREE_HEIGHT = 5;

    CellularAutomaton createCellularAutomaton(int width, int height){
        return new CellularAutomatonImpl(width,height);
    }

    @Test
    public void createEightNeighbours(){
        Cell cell = new Cell(1,1, createCellularAutomaton(4,4));
        Set<NeighbourOrientation> orientationsCalculated = cell.getNeighbourOrientations();
        Assert.assertTrue(orientationsCalculated.containsAll(Arrays.asList(NeighbourOrientation.values())));
    }

    @Test
    public void createTopLeftCell(){
        Cell cell = new Cell(0,3, createCellularAutomaton(4,4));
        Set<NeighbourOrientation> orientationsCalculated = cell.getNeighbourOrientations();
        Set<NeighbourOrientation> expected = new HashSet<>(Arrays.asList(EAST, SE, SOUTH));
        Assert.assertTrue(expected.equals(orientationsCalculated));
    }

    @Test
    public void createBottomRightCell(){
        Cell cell = new Cell(3,0, createCellularAutomaton(4,4));
        Set<NeighbourOrientation> orientationsCalculated = cell.getNeighbourOrientations();
        Set<NeighbourOrientation> expected = new HashSet<>(Arrays.asList(WEST, NW, NORTH));
        Assert.assertTrue(expected.equals(orientationsCalculated));
    }

    @Test
    public void createSmallWorld(){
        int width = 4;
        int height = 3;
        CellularAutomaton cellularAutomaton = createScenario(width, height);
        Assert.assertEquals(cellularAutomaton.getHeight(), height);
    }

    @Test
    public void testRadiationHorizontal(){
        CellularAutomaton a = createOneTreeOnFireAndGrass(7, 1, 0,0, TREE_HEIGHT);
        Evolver e = new EvolverImpl(a, c -> c.getTime() > 20);
        e.start();

        List<Double> temperatures = IntStream.range(0,a.getWidth()).boxed()
                .map( i -> a.getCell(i,0).getTemperature())
                .collect(toList());

        Assert.assertTrue(isSortedDesc(temperatures));
    }

    @Test
    public void testRadiationVertical(){
        CellularAutomaton a = createOneTreeOnFireAndGrass(1, 7, 0,0, TREE_HEIGHT);
        Evolver e = new EvolverImpl(a, c -> c.getTime() > 20);
        e.start();

        List<Double> temperatures = IntStream.range(0,a.getHeight()).boxed()
                .map( i -> a.getCell(0,i).getTemperature())
                .collect(toList());

        Assert.assertTrue(isSortedDesc(temperatures));
    }

    @Test
    public void testRadiationCircularATreeInAGrassField(){
        int x=5;
        int y=x;
        int size = x*2+1;
        CellularAutomaton a = createManyTreesOnFireAndGrass(size, size, Arrays.asList(new Pair(x,y)), TREE_HEIGHT);
        Evolver e = new EvolverImpl(a, c -> c.getTime() > 10);
        //Evolver e = new EvolverImpl(a, c -> !c.isOnFire());
        e.start();
        e.continueEvolving(x);
        Assert.assertTrue(a.getCell(1,1).getTemperature() == NORMAL_TEMPERATURE);
    }

    @Test
    public void testRadiationCircularATreeInADeadField(){
        int x=6;
        int y=x;
        int size = x*2+1;
        CellularAutomaton a = createOneTree(size, size, x, y, TREE_HEIGHT);
        Evolver e = new EvolverImpl(a, c -> c.getTime() > 10);
        e.start();

        Cell north = a.getCell(x,y-1);
        Cell south = a.getCell( x, y+1);
        Cell west = a.getCell(x-1,y);
        Cell east = a.getCell(x-1,y);

        Assert.assertTrue(
                areEqualDouble(north.getTemperature(), south.getTemperature())
                        &&
                        areEqualDouble(west.getTemperature(), east.getTemperature() )
        );
    }

    @Test
    public void testAshesAngle(){
        Map<NeighbourOrientation, Double> proportions = new HashMap<>();
        PairDouble wind = NORTH.getPair();
        for(NeighbourOrientation orientation : NeighbourOrientation.values()){
            double angle = angleBetweenVectors(orientation.getPair(), wind);
            double proportionDistributionForNeighbour = calculateDistribution(angle);
            proportions.put(orientation, proportionDistributionForNeighbour);
        }
        double epsilon = 1e-5;
        Assert.assertTrue(proportions.entrySet().stream().filter(e->e.getValue() >= epsilon).count() == 3);
    }

    //cambiado
    @Test
    public void testRadiationCircularATreeInADeadFieldWithWind(){
        int x=5;
        int y=x;
        int size = x*2+1;
        //CellularAutomaton a = createOneTree(size, size, x,y, TREE_HEIGHT);
        CellularAutomaton a = createManyTreesOnFireAndGrass(size, size, Arrays.asList(new Pair(x,y)), TREE_HEIGHT);

        Evolver e = new EvolverImpl(a, c -> c.getTime() > 10);

        //viento hacia el norte
        double angle = (1./2)*PI;
        a.addWindStrategy(new PolarWind(50, angle));
        e.start();

        Cell north = a.getCell(x,y+1);
        Cell south = a.getCell( x, y-1);
        Cell west = a.getCell(x-1,y);
        Cell east = a.getCell(x+1,y);

        Assert.assertTrue(
                (north.getTemperature() > south.getTemperature())
                            &&
                        areEqualDouble(west.getTemperature(), east.getTemperature() )
        );
    }

    @Test
    public void testCircularRadiationWithWind(){
        int x=5;
        int y=x;
        int size = x*2+1;
        CellularAutomaton a = createOneTreeOnFireAndGrass(size, size, x,y, TREE_HEIGHT);
        Evolver e = new EvolverImpl(a, c -> c.getTime() > 6);
        double angle = (1./2)*PI;
        a.addWindStrategy(new PolarWind(100, angle));
        e.start();

        Cell north = a.getCell(x,y+1);
        Cell south = a.getCell( x, y-1);
        Cell west = a.getCell(x-1,y);
        Cell east = a.getCell(x+1,y);

        Assert.assertTrue(
                (north.getTemperature() > south.getTemperature())
                        &&
                        areEqualDouble(west.getTemperature(), east.getTemperature() )
        );
    }

    @Test
    public void testFireByAshes(){
        int x=5;
        int size = x*2+1;
        CellularAutomaton a = createWorldFromParts(size, size,

                new WorldPart(0, x, size, x, b -> new GrassCO(1,5)),
                new WorldPart(x, 0, 1,1, b -> {
                    CombustibleObject combustibleObject = new TreeCO(10,100, TREE_HEIGHT);
                    combustibleObject.setOnFire();
                    return combustibleObject;
                }));
        Evolver e = new EvolverImpl(a, c -> c.getTime() > 7);
        double angle = (1./2)*PI;
        a.addWindStrategy(new PolarWind(100, angle));
        e.start();

        Assert.assertTrue(a.getCombustionableObjects().size() > 1);
    }

    @Test
    public void testWindOrientationByAngle(){
        CellularAutomaton a = null;
        for(int i =0; i<8; i++){
            a = createOneTreeOnFireAndGrass(11, 11, 5,5, TREE_HEIGHT);
            Evolver e = new EvolverImpl(a, c -> c.getTime() > 6);
            double angle = (1./4)*PI*i;
            a.addWindStrategy(new PolarWind(100, angle));
            e.start();
            System.out.println(String.format("angle = %dxPI/4 --- wind = %s",i,a.getCell(0,0).getWind().toString()));

        }
        Assert.assertTrue(a!=null);
    }


    @Test
    public void testRadiationCircularTwoFires(){
        CellularAutomaton a = createManyTreesOnFireAndGrass(10, 10, Arrays.asList(new Pair(0,0), new Pair(7,7)), TREE_HEIGHT);
        //a.addWindStrategy(new PolarWind(2,PI/4));
        Evolver e = new EvolverImpl(a, c -> c.getTime() > 6);
        e.start();
        a.printTemperatures();
        Assert.assertTrue(a!=null);
    }

   @Test
   public void testParallelVectors(){
       PairDouble v1 = new PairDouble(1,1);
       PairDouble v2 = new PairDouble(4,4);
       double angle = VectorHelper.angleBetweenVectors(v1,v2);
       Assert.assertTrue(areEqualDouble(angle,0));
   }

    @Test
    public void testPerpendicular(){
        PairDouble v1 = new PairDouble(1,0);
        PairDouble v2 = new PairDouble(0,1);
        double angle = VectorHelper.angleBetweenVectors(v1,v2);
        Assert.assertTrue(areEqualDouble(angle,PI/2));
    }

    public static boolean isSortedDesc(List<Double> list) {
        return isSortedDesc(list, list.size());
    }

    public static boolean isSortedDesc(List<Double> listOfStrings, int index) {
        if (index < 2) {
            return true;
        } else if (listOfStrings.get(index - 1).compareTo(listOfStrings.get(index - 2)) > 0) {
            return false;
        } else {
            return isSortedDesc(listOfStrings, index - 1);
        }
    }

    public static boolean areEqualDouble(double a, double b) {
        int precision = 5;
        return Math.abs(a - b) <= Math.pow(10, -precision);
    }

    @Test
    public void consumingATree(){
        int width = 1;
        int height = 1;
        CellularAutomaton cellularAutomaton = createOneTreeOnFireAndGrass(width, height, 0,0, TREE_HEIGHT);
        Evolver evolver = new EvolverImpl(cellularAutomaton, c -> c.getCombustionableObjects().isEmpty());
        evolver.start();
        Assert.assertTrue(cellularAutomaton.getCombustionableObjects().isEmpty());
    }

    public void consumeTree(){
        CellularAutomaton a = createOneTreeOnFireAndGrass(3, 3, 1,1, TREE_HEIGHT);
        Evolver e = new EvolverImpl(a, c -> c.getTime() > 3000);
        e.start();
    }

    private CellularAutomaton createScenario(int width, int height){
        CellularAutomaton cellularAutomaton = createCellularAutomaton(width,height);
        for(int x = 0; x<width; x++){
            for(int y = 0; y < height; y++){
                cellularAutomaton.addElement(x,y,new GroundObject(2));
            }
        }
        return cellularAutomaton;
    }

    private CellularAutomaton createOneTreeOnFireAndGrass(int width, int height, int treeX, int treeY, int treeHeight){
        CellularAutomaton cellularAutomaton = createScenario(width, height);
        cellularAutomaton.iterate().forEach( pair -> {
            if(pair.x == treeX && pair.y==treeY){
                CombustibleObject combustibleObject = new TreeCO(10,100, treeHeight);
                combustibleObject.setOnFire();
                cellularAutomaton.addElement(pair, combustibleObject);
            }else {
                cellularAutomaton.addElement(pair, new GrassCO(1,5));
            }
        });
        return cellularAutomaton;
    }

    private CellularAutomaton createWorldFromParts(int width, int height, WorldPart ... parts){
        CellularAutomaton cellularAutomaton = createScenario(width, height);
        for(WorldPart part : parts){
            part.createPart(cellularAutomaton);
        }
        return cellularAutomaton;
    }

    private CellularAutomaton createOneTree(int width, int height, int treeX, int treeY, int treeHeight){
        CellularAutomaton cellularAutomaton = createScenario(width, height);
        cellularAutomaton.iterate().forEach( pair -> {
            if(pair.x == treeX && pair.y==treeY){
                CombustibleObject combustibleObject = new TreeCO(10,100, treeHeight);
                combustibleObject.setOnFire();
                cellularAutomaton.addElement(pair, combustibleObject);
            }
        });
        return cellularAutomaton;
    }

    private CellularAutomaton createManyTreesOnFireAndGrass(int width, int height, List<Pair> treesPositions, int treeHeight){
        CellularAutomaton cellularAutomaton = createScenario(width, height);
        cellularAutomaton.iterate().forEach( pair -> {
            if(treesPositions.contains(pair)){
                CombustibleObject combustibleObject = new TreeCO(10,100, treeHeight);
                combustibleObject.setOnFire();
                cellularAutomaton.addElement(pair, combustibleObject);
            }else {
                cellularAutomaton.addElement(pair, new GrassCO(1,5));
            }
        });
        return cellularAutomaton;
    }

}