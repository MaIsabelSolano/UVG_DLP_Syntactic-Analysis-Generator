
/*
 * @author: Ma. Isabel Solano
 * @version 4, 24/04/23
 * 
 * AFN class as a Automata should be defined.
 * 
 */

package src;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Stack;

public class AFD {

    private ArrayList<State> States = new ArrayList<>();
    private ArrayList<Symbol> alphabet = new ArrayList<>();
    private State initialState;
    private ArrayList<Transition> trans = new ArrayList<>();
    private ArrayList<State> finalStates = new ArrayList<>();
    
    /**
     * 
     * Simple test constructor
     * 
     * @param Symbols
     */
    public AFD(ArrayList<Symbol> Symbols){
        this.alphabet = Symbols;
    }

    /**
     * Given a String with the Automata information, creates
     * a new one 
     * 
     * @param txt
     */
    public AFD(String txt) {

        String components[] = txt.split("\n");
        for (int i = 0; i < components.length; i++) {

            if (i == 0) {
                // States
                String sym[] = components[0].split("~");

                for (String s: sym) {
                    if (s.charAt(0) == '\\') {
                        // special symbol
                        if (s.charAt(1) == 's') {
                            // space
                            Symbol tempSymbol = new Symbol(' ');
                            alphabet.add(tempSymbol);
                        } else if (s.charAt(1) == 't') {
                            // tab
                            Symbol tempSymbol = new Symbol('\t');
                            alphabet.add(tempSymbol);
                        } else if (s.charAt(1) == 't') {
                            // new line
                            Symbol tempSymbol = new Symbol('\n');
                            alphabet.add(tempSymbol);
                        }

                    } else if (s.charAt(0) == '$') {
                        // Terminal Symbol
                        String parts[] = s.split(",");
                        Terminator tempSymbol = new Terminator(parts[1]);
                        alphabet.add(tempSymbol);

                    } else {
                        // normal symbol
                        Symbol tempSymbol = new Symbol(s.charAt(i));
                        alphabet.add(tempSymbol);
                    }
                }

            } else if (i == 1) {
                // States
                String sts[] = components[1].split("~");

                for (String s: sts) {
                    String st[] = s.split(",");

                    int type = 0;
                    if (st[1].equals("Inicial")) type = 1;
                    if (st[1].equals("Trans")) type = 2;
                    if (st[1].equals("Final")) type = 3;

                    State tempState = new State(Integer.parseInt(st[0]), type);
                    States.add(tempState);
                }

            } else if (i == 2) {
                // Initial State
                int id = Integer.parseInt(components[2]);

                for (State s: States) {
                    if (s.id == id) {
                        initialState = s;
                    }
                }

            } else if (i == 3) {
                // Transitions

                String transtxt[] = components[3].split("~");

                for (String t: transtxt) {
                    String parts[] = t.split(",");

                    int ot = Integer.parseInt(parts[0]);
                    int dt = Integer.parseInt(parts[1]);

                    State originTem = new State(0, 1);
                    State destTemp = new State(0, 1);
                    Symbol symTemp = new Symbol(' ');

                    for (State s: States) {
                        if (s.id == ot) originTem = s;
                        if (s.id == dt) destTemp = s;
                    }

                    if (parts.length == 3) {
                        int symID = Integer.parseInt(parts[2]);

                        for (Symbol j: alphabet) {
                            if (j.id == symID) symTemp = j;
                        }

                    } else if (parts.length == 4) {
                        // terminator Symbol
                        int symID = Integer.parseInt(parts[2]);
                        String assoToken = parts[3];

                        for (Symbol j: alphabet) {
                            if (j.id == symID) {
                                if (j instanceof Terminator) {
                                    Terminator tempT = (Terminator) j;
                                    if (tempT.getAssociatedToken().equals(assoToken + "$")) {
                                        
                                        symTemp = tempT;
                                    }
                                }
                            }
                        }
                    }

                    Transition tempTransition = new Transition(originTem, symTemp, destTemp);
                    trans.add(tempTransition);
                    
                }

            } else if (i == 4) {
                // Final states

            }

        }

    }

    /**
     * Subset construction from NFA to DNA
     * 
     * @param alphabet
     * @param afn
     */
    public AFD(ArrayList<Symbol> sym, AFN afn) {

        // Delete epsilon from Symbol dictionary
        Symbol epsilon = new Symbol('ε');
        sym.remove(epsilon.id);

        this.alphabet = sym;

        // Array list that 
        ArrayList<ArrayList<State>> C_states = new ArrayList<>();
        Stack<ArrayList<State>> unverified_States = new Stack<>();

        ArrayList<State> currentStates = new ArrayList<>(); 
        currentStates.add(afn.getInitialState()); // Adds the inital state to de current states
        currentStates = eClosure(afn, currentStates); // Get first state
        unverified_States.add(currentStates); 
        C_states.add(currentStates);

        // generate initial state
        State initState;
        if (currentStates.contains(afn.getFinalState())){
            initState = new State(0, 3);
            this.finalStates.add(initState);
        } else {
            initState = new State(0, 1);
        }
        this.initialState = initState;
        this.States.add(initialState);


        boolean verifier = true; // Indicates if there are still states to mark
        while (verifier) {

            // Verify if there are still unverified states    
            if (!unverified_States.isEmpty()) {
                // if yes, pop one from unverified_States
                currentStates = unverified_States.pop();

            } else {
                // if not, stop
                verifier = false;
                // The algorithm is done
            }

            // perform for each symbol in the alphabet
            for (Symbol currentSymbol: alphabet) {

                ArrayList<State> moveRes = move(afn, currentStates, currentSymbol);

                // e-clousure
                moveRes = eClosure(afn, moveRes);

                // rearange
                moveRes.sort(Comparator.naturalOrder());

                // verify if we already have this state
                if (!C_states.contains(moveRes)) {
                    // verify that the array is not empty
                    if (!moveRes.isEmpty()) {
                        C_states.add(moveRes);
                        unverified_States.add(moveRes);

                        
                        State endState;
                        if (moveRes.contains(afn.getFinalState())){
                            endState = new State(C_states.indexOf(moveRes), 3);
                            this.finalStates.add(endState);
                        } else {
                            endState = new State(C_states.indexOf(moveRes), 2);
                        }

                        // gen state and gen transition
                        
                        this.States.add(endState); // add new State

                        Transition tempTransition = new Transition(
                            States.get(C_states.indexOf(currentStates)), 
                            currentSymbol,
                            States.get(C_states.indexOf(moveRes)));

                        this.trans.add(tempTransition);
                    }
                    

                } else {

                    // gen state and gen transition
                    Transition tempTransition = new Transition(
                        States.get(C_states.indexOf(currentStates)), 
                        currentSymbol,
                        States.get(C_states.indexOf(moveRes))
                    );

                    // Check if the transitions was not added before
                    // check for repeated transition
                    int add = 0;
                    for (Transition t: trans) {
                        if (tempTransition.equals(t)) add += 1;
                    }

                    // Add to the transitions
                    if (add == 0) trans.add(tempTransition);
                }


            }

        }

        // System.out.println("\nGenerated states");
        // for (int i = 0; i < C_states.size(); i++) {
        //     String temp = "{";
        //     for (State s : C_states.get(i)) temp += s.toString();
        //     temp += "}";
        //     System.out.println(temp);
        // }

    }

    /**
     * Direct DFA construction constructor. 
     * 
     * @param sym   The Languages alphabet
     * @param tree  The Syntactic tree with the regex information
     */
    public AFD(ArrayList<Symbol> sym, SintacticTree tree) {

        // Delete epsilon from Symbol dictionary
        Symbol epsilon = new Symbol('ε');
        sym.remove(epsilon);

        this.alphabet = sym;

        ArrayList<ArrayList<Integer>> baseStates = new ArrayList<>();
        Stack<ArrayList<Integer>> unverifiedStates = new Stack<>();
        
        // Start with the root's firtpos
        ArrayList<Integer> currentStates = new ArrayList<>();
        
        ArrayList<Integer> initial = tree.getRoot().getFirstpos();
        State newState;
        // check if it's a valid end state
        if (initial.contains(tree.getTerminalpos())) newState = new State(0, 3);
        else newState = new State(0, 1);
        this.initialState = newState;
        States.add(newState);
        

        // add to stack
        baseStates.add(initial);
        unverifiedStates.push(initial);

        ArrayList<Integer> nextState;

        boolean keepGoing = true;
        while (keepGoing) {

            // verify if there is still anything to get transitions from 
            if (!unverifiedStates.isEmpty()) {
                // if so, pos the latest unverified State
                currentStates = unverifiedStates.pop();

            } else {
                // The algorithm is over
                keepGoing = false;
            }

            // check transtitions from each symbol in the alphabet
            for (Symbol i: alphabet) {
                nextState = new ArrayList<>(); // create empty

                // check in all of the positions of the currentStates
                for (int pos: currentStates) {

                    // check if the current symbol is represented in the current position
                    if (tree.getPosSymbol().get(pos).c_id == i.c_id) {

                        //check for special terminator symbols
                        if (i instanceof Terminator && tree.getPosSymbol().get(pos) instanceof Terminator) {
                            Terminator treeTempSym = (Terminator) tree.getPosSymbol().get(pos);
                            Terminator curTempSym = (Terminator) i;

                            if (treeTempSym.getAssociatedToken().equals(curTempSym.getAssociatedToken())) {
                                // get the follopos and add it to currentStes
                                for (int addPos: tree.getFollowpos(pos)) {
                                    if (!nextState.contains(addPos)) nextState.add(addPos); 
                                }
                            }
                        }

                        else {

                            // get the follopos and add it to currentStes
                            for (int addPos: tree.getFollowpos(pos)) {
                                if (!nextState.contains(addPos)) nextState.add(addPos); 
                            }
                        }
                    }
                }

                if (!nextState.isEmpty()) {
                    // check it this state exists
                    if (baseStates.contains(nextState)) {
                        // get indexes
                        int originId = baseStates.indexOf(currentStates);
                        int destinyId = baseStates.indexOf(nextState);

                        Transition newTrans = new Transition(
                            States.get(originId), 
                            i, 
                            States.get(destinyId)
                        );

                        // check for repeated transition
                        int add = 0;
                        for (Transition t: trans) {
                            if (newTrans.equals(t)) add += 1;
                        }
                        // Add to the transitions
                        if (add == 0) trans.add(newTrans);

                    } else {
                        // generate new state and transitions

                        // add to the array
                        baseStates.add(nextState);
                        unverifiedStates.push(nextState);
                        
                        // get indexes
                        int originId = baseStates.indexOf(currentStates);
                        int destinyId = baseStates.indexOf(nextState);

                        // check if it's final state
                        if (nextState.contains(tree.getTerminalpos())){ newState = new State(destinyId, 3);}
                        else newState = new State(destinyId, 2);

                        States.add(newState);

                        //Add transition to transitions arrray
                        Transition newTrans = new Transition(
                            States.get(originId), 
                            i, 
                            newState
                        );

                        // check for repeated transition
                        int add = 0;
                        for (Transition t: trans) {
                            if (newTrans.equals(t)) add += 1;
                        }
                        // Add to the transitions
                        if (add == 0) trans.add(newTrans);
                    }
                }
                
                
            }
        }

        // System.out.println("\nGenerated states");
        // for (int i = 0; i < baseStates.size(); i++) {
        //     String temp = "{";
        //     for (int s : baseStates.get(i)) temp += Integer.toString(s);
        //     temp += "}";
        //     System.out.println(temp);
        // }
    }

    /**
     * Given a String, using the method 'moveState' it simulates goes through
     * all of the element of the string and to their respective transitions till
     * finishing the string and checking if the last reached states was a 
     * valid end state
     * 
     * @param r String to simulates
     * @return  True of false according to if the string was valid or not.
     */
    public boolean Simulate(String r) {

        State currentState = initialState;
        for (int c = 0; c < r.length(); c++) {
            State nextState = moveState(currentState, r.charAt(c));
            if (nextState == null) return false;
            else currentState = nextState;
        }

        if (currentState.type == Type.Final) return true;
        else return false;

    }

    public Symbol simulateGetSymbol(String r) {

        State currentState = initialState;
        Symbol lastSymbol = null;
        for (int c = 0; c < r.length(); c++) {
            State nextState = moveState(currentState, r.charAt(c));
            lastSymbol = moveState_getSym(currentState, r.charAt(c));
            if (nextState == null) return null;
            else currentState = nextState;
        }

        return lastSymbol;

    }

    /**
     * Calculates all of the available states that can e reached using
     * epsilon
     * 
     * @param afn       NFA that wants to be converted to DFA
     * @param current   Current set of states
     * @return          Next set of states 
     */
    private ArrayList<State> eClosure(AFN afn, ArrayList<State> current) {

        ArrayList<State> E_states = new ArrayList<>();
        for (State s: current) E_states.add(s); // copy first symbols

        boolean keepGoing = true;
        while (keepGoing) {
            for (int i = 0; i < current.size(); i++) {

                // Check the epsilon transitions for each the current set of states
                State temp = current.get(i);
                ArrayList<Transition> transitions = afn.getTransitions();
    
                for (int j = 0; j < transitions.size() ; j++ ) {
                    if (transitions.get(j).getOriginState().id == temp.id) {
                        // check for all transitions that start begin with that state
                        if (transitions.get(j).symbol.c_id == 'ε') {
                            // if it has an epsilon transition, it ads the final state to Estates
    
                            // but first it checks if it was already in the list so it won't repeat states
                            if (!E_states.contains(transitions.get(j).getFinalState())) E_states.add(transitions.get(j).getFinalState());
                        }
                    }
                }
    
            }

            // Determine weather we should keep going or not
            if (E_states.equals(current)) {
                // There are no other epsilon transitions to be reached
                keepGoing = false;
            } else {
                // Rewrite current
                current.clear();
                for (State s: E_states) current.add(s);
            }

        }


        return E_states;
    }

    /**
     * Given an AFN, a current list of states, and a given Symbol, it
     * calculates all of the next staes. 
     * 
     * @param afn       AFN that is in process on being changed to DFA
     * @param current   Current set of states
     * @param s         Symbol to use for moving.
     * @return          A set of states with the possible next states. 
     */
    private ArrayList<State> move(AFN afn, ArrayList<State> current, Symbol s) {

        ArrayList<State> S_states = new ArrayList<>();

        for (int i = 0; i < current.size(); i++) {
            // Check the given symbol transitions for each the current set of states

            State temp = current.get(i);
            ArrayList<Transition> transitions = afn.getTransitions();

            for (int j = 0; j < transitions.size() ; j++ ) {
                if (transitions.get(j).getOriginState().id == temp.id) {
                    // check for all transitions that start begin with that state
                    if (transitions.get(j).symbol.c_id == s.c_id) {
                        // if it has an epsilon transition, it ads the final state to Estates

                        // but first it checks if it was already in the list so it won't repeat states
                        if (!S_states.contains(transitions.get(j).getFinalState())) S_states.add(transitions.get(j).getFinalState());
                    }
                }
            }

        }

        return S_states;
    }

    /**
     * Used during simulation, given a state and a symbol (char)
     * returns the State that follows according to the given String
     * 
     * @param state Current state
     * @param s     Symbols to use during the moving
     * @return      Next state
     */
    public State moveState(State state, char s) {

        for (Transition t: trans) {
            if (t.getOriginState().id == state.id) {
                if (t.getSymbol().c_id == s) {
                    return t.getFinalState();
                }
            }
        }

        return null;
    }

    public Symbol moveState_getSym(State state, char s) {

        for (Transition t: trans) {
            if (t.getOriginState().id == state.id) {
                if (t.getSymbol().c_id == s) {
                    return t.getSymbol();
                }
            }
        }

        return null;
    }

    /* Getters */
    public ArrayList<State> getFinalStates() {
        return finalStates;
    }

    public State getInitialState() {
        return initialState;
    }

    public ArrayList<State> getStates() {
        return States;
    }

    public ArrayList<Transition> getTransitions() {
        return trans;
    }

    public ArrayList<Symbol> getAlphabet() {
        return alphabet;
    }

    /* Setters */
    public void setAlphabet(ArrayList<Symbol> alphabet) {
        this.alphabet = alphabet;
    }

    public void setInitialState(State initialState) {
        this.initialState = initialState;
    }

    public void setFinalStates(ArrayList<State> finalStates) {
        this.finalStates = finalStates;
    }

    public void setTransitions(ArrayList<Transition> trans) {
        this.trans = trans;
    }

    public void addState(State newState, boolean isFinal) {
        // Check for null states, if null, don't add
        if (newState != null) {
            if (!this.States.contains(newState)) States.add(newState);
            if (isFinal) this.finalStates.add(newState);
        }
    }

    public void addTransition(State origin, Symbol s, State whereTo) {
        // Check for null informations, if null, don't add that transition
        if (origin != null && s != null && whereTo != null) {
            Transition temp = new Transition(origin, s, whereTo);
            if (!trans.contains(temp)) trans.add(temp);
        }
    }

    @Override
    public String toString() {
        String info = "";
        info += "\nSymbols: " + alphabet.toString();

        info += "\nStates: " + States.toString();

        info += "\nTransitions: " + trans.toString();

        info += "\nInitial state: "; 
        
        info += (this.initialState == null) ? "None" : this.initialState.toString();

        info += "\nAccepting states: " + finalStates.toString(); 

        return info;
    }

}
