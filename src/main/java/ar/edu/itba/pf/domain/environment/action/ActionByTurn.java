package ar.edu.itba.pf.domain.environment.action;

public class ActionByTurn {

    private int turn;
    private ActionType action;
    private Double value;//es opcional

    public ActionByTurn(int turn, ActionType action) {
        this.turn = turn;
        this.action = action;
    }

    public ActionByTurn(int turn, ActionType action, Double value) {
        this.turn = turn;
        this.action = action;
        this.value = value;
    }

    public boolean isTurn(int aTurn){
        return aTurn == turn;
    }

    public int getTurn() {
        return turn;
    }

    public ActionType getAction() {
        return action;
    }

    public Double getValue() {
        return value;
    }
}
