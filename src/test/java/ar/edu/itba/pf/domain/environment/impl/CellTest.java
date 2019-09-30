package ar.edu.itba.pf.domain.environment.impl;

import ar.edu.itba.pf.domain.engine.Evolver;
import ar.edu.itba.pf.domain.engine.impl.EvolverImpl;
import ar.edu.itba.pf.domain.environment.CellularAutomaton;
import ar.edu.itba.pf.domain.environment.Pair;
import ar.edu.itba.pf.domain.environment.objects.GroundObject;
import ar.edu.itba.pf.domain.environment.objects.combustible.CombustibleObject;
import ar.edu.itba.pf.domain.environment.objects.combustible.GrassCO;
import ar.edu.itba.pf.domain.environment.objects.combustible.TreeCO;
import ar.edu.itba.pf.domain.environment.windengine.impl.PolarWind;
import org.junit.Assert;
import org.junit.Test;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static ar.edu.itba.pf.domain.environment.impl.NeighbourOrientation.*;
import static java.lang.Math.PI;

public class CellTest {

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
        Cell cell = new Cell(0,0, createCellularAutomaton(4,4));
        Set<NeighbourOrientation> orientationsCalculated = cell.getNeighbourOrientations();
        Set<NeighbourOrientation> expected = new HashSet<>(Arrays.asList(EAST, SE, SOUTH));
        Assert.assertTrue(expected.equals(orientationsCalculated));
    }

    @Test
    public void createBottomRightCell(){
        Cell cell = new Cell(3,3, createCellularAutomaton(4,4));
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
        CellularAutomaton a = createOneTreeOnFireAndGrass(7, 1, 0,0);
        Evolver e = new EvolverImpl(a, c -> c.getTime() > 20);
        e.start();
        a.printTemperatures();
        Assert.assertTrue(a!=null);
    }

    @Test
    public void testRadiationVertical(){
        CellularAutomaton a = createOneTreeOnFireAndGrass(1, 7, 0,0);
        Evolver e = new EvolverImpl(a, c -> c.getTime() > 20);
        e.start();
        a.printTemperatures();
        Assert.assertTrue(a!=null);
    }

    @Test
    public void testRadiationCircular(){
        CellularAutomaton a = createOneTreeOnFireAndGrass(6, 6, 0,0);
        Evolver e = new EvolverImpl(a, c -> c.getTime() > 1);
        e.start();
        a.printTemperatures();
        Assert.assertTrue(a!=null);
    }

    @Test
    public void testRadiationCircularTwoFires(){
        CellularAutomaton a = createManyTreesOnFireAndGrass(10, 10, Arrays.asList(new Pair(0,0), new Pair(7,7)));
        a.addWindStrategy(new PolarWind(2,PI/4));
        Evolver e = new EvolverImpl(a, c -> c.getTime() > 6);
        e.start();
        a.printTemperatures();
        Assert.assertTrue(a!=null);
    }

   /* @Test
    public void testRadiationCircularTwoFires(){
        CellularAutomaton a = createManyTreesOnFireAndGrass(10, 10, Arrays.asList( new Pair(5,5)));
        Evolver e = new EvolverImpl(a, c -> c.getTime() > 7);
        e.start();
        a.printTemperatures();
        Assert.assertTrue(a!=null);
    }*/

    @Test
    public void consumingATree(){
        int width = 1;
        int height = 1;
        CellularAutomaton cellularAutomaton = createOneTreeOnFireAndGrass(width, height, 0,0);
        Evolver evolver = new EvolverImpl(cellularAutomaton, c -> c.getCombustionableObjects().isEmpty());
        evolver.start();
        Assert.assertTrue(cellularAutomaton.getCombustionableObjects().isEmpty());
    }

    public void consumeTree(){
        CellularAutomaton a = createOneTreeOnFireAndGrass(3, 3, 1,1);
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

    private CellularAutomaton createOneTreeOnFireAndGrass(int width, int height, int treeX, int treeY){
        CellularAutomaton cellularAutomaton = createScenario(width, height);
        cellularAutomaton.iterate().forEach( pair -> {
            if(pair.x == treeX && pair.y==treeY){
                CombustibleObject combustibleObject = new TreeCO(10,100);
                combustibleObject.setOnFire();
                cellularAutomaton.addElement(pair, combustibleObject);
            }else {
                cellularAutomaton.addElement(pair, new GrassCO(1,5));
            }
        });
        return cellularAutomaton;
    }

    private CellularAutomaton createManyTreesOnFireAndGrass(int width, int height, List<Pair> treesPositions){
        CellularAutomaton cellularAutomaton = createScenario(width, height);
        cellularAutomaton.iterate().forEach( pair -> {
            if(treesPositions.contains(pair)){
                CombustibleObject combustibleObject = new TreeCO(10,100);
                combustibleObject.setOnFire();
                cellularAutomaton.addElement(pair, combustibleObject);
            }else {
                cellularAutomaton.addElement(pair, new GrassCO(1,5));
            }
        });
        return cellularAutomaton;
    }

}