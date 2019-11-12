package ar.edu.itba.pf.domain.helper.worldcreator;

import ar.edu.itba.pf.domain.environment.CellularAutomaton;
import ar.edu.itba.pf.domain.environment.Pair;
import ar.edu.itba.pf.domain.environment.objects.combustible.CombustibleObject;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public class WorldPart{
    int x;
    int y;
    int width;
    int height;
    Function<CellularAutomaton, CombustibleObject> objectCreator;

    public WorldPart(int x, int y, int width, int height, Function<CellularAutomaton, CombustibleObject> objectCreator) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.objectCreator = objectCreator;
    }

    public void createPart(CellularAutomaton automaton){
        iterate().forEach( pair -> automaton.addElement(pair, objectCreator.apply(automaton)));
    }

    private List<Pair> iterate() {
        List<Pair> pairs = new ArrayList<>();
        int yLimit = height+this.y;
        int xLimit = width+this.x;
        for(int x = this.x; x< xLimit; x++){
            for(int y = this.y; y < yLimit; y++){
                pairs.add(new Pair(x,y));
            }
        }
        return pairs;
    }

}