package LR;

import java.util.ArrayList;

public class State {

    private int StateNum;
    private ArrayList<Production> productions = new ArrayList<>();
    private boolean accepting = false;
    private boolean initial = false;


    public State(int id) {
        this.StateNum = id;
    }

    public void addProduction(Production p) {
        productions.add(p);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof State)) {
            return false;
        }
        State other = (State) obj;

        return productions.equals(other.productions);
    }


    @Override
    public String toString() {
        String ret = "";
        ret += " --I_" + StateNum + "-- ";
        for (Production p: productions) {
            ret += "\n" + p.toString();
        }
        return ret;
    }


    /* Getters and setters */

    public void setProductions(ArrayList<Production> productions) {
        this.productions = productions;
    }

    public void setAccepting() {
        this.accepting = true;
    }

    public void setInitial() {
        this.initial = true;
    }

    public boolean isAccepting() {
        return accepting;
    }

    public boolean isInitial() {
        return initial;
    }

    public int getStateNum() {
        return StateNum;
    }

    public ArrayList<Production> getProductions() {
        return productions;
    }
    
}
