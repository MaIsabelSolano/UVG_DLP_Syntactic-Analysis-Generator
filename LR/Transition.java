package LR;

public class Transition {

    private State initialState;
    private String expresion;
    private State finalState;

    public Transition (State iState, String ex, State fState) {
        this.initialState = iState;
        this.expresion = ex;
        this.finalState = fState;
    }

    @Override
    public boolean equals(Object obj) {
        
        Transition o = (Transition) obj;
        if (o.getInitialState().equals(initialState)) {
            if (o.getExpresion().equals(expresion)) {
                if (o.getFinalState().equals(finalState)) {
                    return true;
                }
            }
        }

        return false;
    }

    @Override
    public String toString() {
        return "["+ initialState.getStateNum() + " -" + expresion + "-> " + finalState.getStateNum() +"]";
    }


    /* Getters and setters */

    public String getExpresion() {
        return expresion;
    }

    public State getInitialState() {
        return initialState;
    }

    public State getFinalState() {
        return finalState;
    }
    
}
