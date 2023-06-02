package LR;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Queue;

public class Automata {

    // Automata parts
    private ArrayList<State> states = new ArrayList<>();
    private ArrayList<Transition> transitions = new ArrayList<>();
    private ArrayList<String> expressions = new ArrayList<>(); 
    private HashMap<String, ArrayList<String>> first = new HashMap<>();
    // private HashMap<String, ArrayList<String>> follow = new HashMap<>();
    private State finalState;
    private State initialState; 

    private int stateID = 0;

    // SL[0] ksk
    private ArrayList<Production> productions = new ArrayList<>();
    private ArrayList<Production> productionsDot = new ArrayList<>();
    private HashSet<String> visited = new HashSet<>();

    /**
     * Empty automata constructor
     */
    public Automata() {}

    public Automata(ArrayList<Production> productions, ArrayList<Production> productionsDot, ArrayList<String> expressions) {
        this.productions = productions;
        this.productionsDot = productionsDot;
        this.expressions = expressions;

        construction();

        // set initial and final state
        states.get(0).setInitial();
        states.get(1).setAccepting();

    }

    private void construction() {

        // Creation and CLOSURE on initial Production
        ArrayList<Production> firstProduction = new ArrayList<>();
        firstProduction.add(productionsDot.get(0));
        State initialSTATE = CLOUSURE(firstProduction);

        states.add(initialSTATE);


        Queue<State> queue = new LinkedList<>();
        queue.add(initialSTATE);

        System.out.println("\nConstruction ");

        int emergency = 0;
        while( !queue.isEmpty() && emergency < 50) {
            State currentState = queue.peek();

            System.out.println("current");
            System.out.println(currentState.toString());

            for (String expression: expressions) {

                State nextState = CLOUSURE(GOTO(currentState, expression));

                if (nextState == null) continue;

                System.out.println("State: I_" + currentState.getStateNum() + " Element: " + expression + " Queue size: " + queue.size());

                Transition newTransition = new Transition(currentState, expression, nextState);
                if (!transitions.contains(newTransition)) transitions.add(newTransition);
                
                //System.out.println(nextState);

                if (!queue.contains(nextState)) queue.add(nextState);
                if (!states.contains(nextState)) states.add(nextState);

            }

            emergency ++;

            queue.remove();
        }


        // Printing states that we got at the end
        System.out.println("\nStates");
        for (State s: states) {
            System.out.println(s);
        }

    }

    public State CLOUSURE(ArrayList<Production> prodArr) {

        ArrayList<Production> newProdArr = new ArrayList<>(prodArr);
        
        for (Production p: prodArr) {
            for (int i = 0; i < p.getProduce().size(); i ++) {
                if (p.getProduce().get(i).equals(".")) {
                    // found the dot
                    if (i + 1 < p.getProduce().size()) {
                        // There other expresions after
                        String nextProduction = p.getProduce().get(i + 1);
                        
                        for (String f: FIRST(nextProduction)) {
                            for (Production fp: productionsDot) {
                                if (fp.getName().equals(f)) {
                                    if (!fp.isTerminal()){
                                        if (!newProdArr.contains(fp)) newProdArr.add(fp);
                                    }
                                }
                            }
                        }
                        
                    } else {
                        // No more productions, so do nothing
                    }
                }
            }
        }

        if (newProdArr.size() <= 0) return null;
        
        State result = new State(stateID);
        result.setProductions(newProdArr);

        for (State s: states) {
            if (result.equals(s)) {
                return s;
            }
        }

        stateID ++;
        return result; 
    }

    public ArrayList<Production> GOTO(State currentState, String expression) {
        ArrayList<Production> result = new ArrayList<>(); 

        for (Production p: currentState.getProductions()) {
            int indexDot = p.getProduce().indexOf(".");
            if (indexDot + 1 < p.getProduce().size()) {
                // Still inside of the list
                if (p.getProduce().get(indexDot + 1).equals(expression)) {
                    // We have found . expression
                    
                    // Swap places expression .
                    ArrayList<String> temp = new ArrayList<>(p.getProduce());
                    Collections.swap(temp, indexDot, indexDot + 1);

                    // Create new produce
                    Production tempProduction = new Production(p);
                    tempProduction.setProduce(temp);

                    // Add to reult
                    result.add(tempProduction);
                }
                // else end of Production 
            }
        }

        return result; 
    }


    public ArrayList<String> FIRST(String X) {
        ArrayList<String> result = new ArrayList<>(); 
        
        for (Production p: productions) {
            if (p.getName().equals(X)) {
                // Match name to production

                // rule 1
                if (p.isTerminal()) {
                    result.add(X);
                    // return without iterating again
                    first.put(X, result);
                    return result;
                }

                // rule 2
            else {
                String firstSymbol = p.getProduce().get(0);
                
                if (!result.contains(firstSymbol)) result.add(firstSymbol);

                if (!first.containsKey(firstSymbol)) {
                    // Add a placeholder for now to avoid infinite recursion
                    first.put(firstSymbol, new ArrayList<>());
                    
                    // Recursively add FIRST set of first symbol
                    ArrayList<String> recursive = FIRST(firstSymbol);
                    for (String sr : recursive) {
                        if (!result.contains(sr)) result.add(sr);
                    }
                    result.addAll(recursive);
                } else {
                    // FIRST set of first symbol already calculated
                    ArrayList<String> recursive = first.get(firstSymbol);
                    for (String sr : recursive) {
                        if (!result.contains(sr)) result.add(sr);
                    }
                }
            }

                // rule 3
            }
        }

        first.put(X, result);
        return result; 
    }

    public ArrayList<String> FIRST2(String X) {
        ArrayList<String> result = new ArrayList<>(); 

        for (Production p: productions) {
            // Match name to production
            if (p.getName().equals(X)) {

                // rule 1
                if (p.isTerminal()) {
                    result.add(X);
                    return result;
                }

                // rule 2
                String firstSymbol = p.getProduce().get(0);

                // Avoid infinite recursion
                if (firstSymbol.equals(X)) continue;

                else {
                    ArrayList<String> recursiveCall = FIRST2(firstSymbol);

                    for (String s: recursiveCall) {
                        if (!result.contains(s)) {
                            result.add(s);
                        }
                    }
                }
                

            }
        }

        return result; 
    }

    public ArrayList<String> FOLLOW(String X) {
        ArrayList<String> result = new ArrayList<>(); 

        // Rule 1
        if (X.equals(productions.get(0).getName())) {
            result.add("$");
            return result;
        }

        // Avoid infinite recursion
        if (visited.contains(X)) {
            return result;
        }
        visited.add(X);

        for (Production p: productions) {
            if (p.getProduce().contains(X)) {
                int index = p.getProduce().indexOf(X);
                // rule 2
                if (index < p.getProduce().size() - 1) {
                    ArrayList<String> res = FIRST(p.getProduce().get(index + 1));
                    for (String s: res) {
                        if (!result.contains(s) && !s.equals("ε")) result.add(s);
                    }
                }
                // rule 3
                else {
                    ArrayList<String> res = FOLLOW(p.getName());
                    for (String s: res) {
                        if (!result.contains(s)) result.add(s);
                    }
                }
            }
        }


        return result; 
    }

    public ArrayList<String> FOLLOW2(String X) {
        ArrayList<String> result = new ArrayList<>(); 

        // Rule 1
        if (X.equals(productions.get(0).getName())) {
            result.add("$");
            return result;
        }

        //  Other rules
        for (Production p: productions) {

            // Match name to production
            if (p.getProduce().contains(X)) {
                int index = p.getProduce().indexOf(X);
                // rule 2
                if (index < p.getProduce().size() - 1) {
                    String first = p.getProduce().get(index + 1);

                    ArrayList<String> res = FIRST2(first);
                    for (String s: res) {
                        if (!result.contains(s) && !s.equals("ε")) result.add(s);
                    }
                }
                // rule 3
                else {
                    String follow = p.getName();

                    // Avoid infinite recursion
                    if (X.equals(follow)) continue;

                    ArrayList<String> res = FOLLOW2(follow);
                    for (String s: res) {
                        if (!result.contains(s)) result.add(s);
                    }
                }
            }

        }

        return result; 
    }

    @Override
    public String toString() {
        String ret = "";
        ret += "Automata";
        ret += "\nStates : [";
        for (State s: states) ret += s.getStateNum() + ", ";
        ret += "]\nSymbols: " + expressions.toString();
        ret += "\nTransitions: " + transitions.toString();
        ret += "initial state:\n" + states.get(0);
        ret += "initial final:\n" + states.get(0);
        return ret;
    }

    /* Getters */
    public ArrayList<State> getStates() {
        return states;
    }

    public ArrayList<Transition> getTransitions() {
        return transitions;
    }

    public ArrayList<String> getExpresions() {
        return expressions;
    }

    public State getFinalState() {
        return finalState;
    }

    public State getInitialState() {
        return initialState;
    }

    public ArrayList<Production> getProductions() {
        return productions;
    }

    
}
