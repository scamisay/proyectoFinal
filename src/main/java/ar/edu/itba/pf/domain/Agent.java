package ar.edu.itba.pf.domain;

import java.util.List;

public abstract class Agent {

    private List<Target> targets;
    private List<Action> actions;
    private List<Agent> brothers;
    private World world;

    public void step(){
        observe();

        for(Action action : actions){
            if(action.decide()){
                double result = action.apply();
                learn(result, action.getId());
            }
        }

        transmit();
    }

    public abstract void learn(double result, int id);

    public abstract void observe();

    public abstract void transmit();
}
