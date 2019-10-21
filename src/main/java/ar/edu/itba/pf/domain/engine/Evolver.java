package ar.edu.itba.pf.domain.engine;

public interface Evolver {

    boolean stopCondition();

    void start();

    void continueEvolving(int times);
}
