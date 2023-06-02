package LR;

import java.util.ArrayList;
import java.util.Arrays;


public class Parsing_Table {

    public Parsing_Table(Automata automata) {

        // Get symbols and productions from the automata
        ArrayList<String> nter = new ArrayList<>();
        ArrayList<String> ter = new ArrayList<>();


        for (Production p: automata.getProductions()) {
            if (p.isTerminal()) {
                if (!ter.contains(p.getName())) ter.add(p.getName());
            }
            else {
                // skip the augmented grammar
                if (!p.getName().equals(automata.getProductions().get(0).getName())){
                    if (!nter.contains(p.getName())) nter.add(p.getName());
                }
            }
        }

        // Add ens symbol
        ter.add("$");

        String[] nonTerminals = nter.toArray(new String[0]);
        String[] terminals = ter.toArray(new String[0]);

        // Get states numbers
        ArrayList<String> st = new ArrayList<>();
        for (State s: automata.getStates()) {
            st.add(Integer.toString(s.getStateNum()));
        }
        String[] states = st.toArray(new String[0]);

        System.out.println("\nterminals: ");
        System.out.println(Arrays.toString(terminals));
        System.out.println("\nnon-terminals");
        System.out.println(Arrays.toString(nonTerminals));
        System.out.println("\nState numbers");
        System.out.println(Arrays.toString(states));
        
        String[][] TABLE = new String[states.length][nonTerminals.length + terminals.length];


        // Shifts
        for (Transition t: automata.getTransitions()) {

            // get Symbol or Production position
            int pos = getPosition(t.getExpresion(), terminals, nonTerminals);

            // determine if its a terminal or not
            boolean isTerminal = (pos < terminals.length)? true: false;

            // replace in the table
            if (isTerminal) TABLE[t.getInitialState().getStateNum()][pos] = "s" + Integer.toString(t.getFinalState().getStateNum());
            else TABLE[t.getInitialState().getStateNum()][pos] = Integer.toString(t.getFinalState().getStateNum());

        }

        // Reduce
        for (State s: automata.getStates()) {
            for (Production p: s.getProductions()) {

                if (p.getProduce().get(p.getProduce().size() - 1).equals(".")) {
                    // . is the last thing it that production
                    System.out.println(Integer.toString(s.getStateNum()) + ": " + p);
                    
                    // let's find it´s corresponding productino
                    int Ipos = getEqualProduction(p, automata.getProductions());

                    // get follow of the left side of the production
                    for (String X: automata.FOLLOW2(p.getName())) {
                        int Spos = getPosition(X, terminals, nonTerminals);

                        
                        // Add to the table
                        TABLE[s.getStateNum()][Spos] = "r" + Integer.toString(Ipos);
                        

                    }
                } 
            }
        }





        // Table printing

        System.out.println();

        System.out.printf("|%-4s|", "");
        for (int i = 0; i < terminals.length; i ++){
            System.out.printf("%-12s|", terminals[i]);
        }

        
        for (int i = 0; i < nonTerminals.length; i ++){
            System.out.printf("%-12s|", nonTerminals[i]);
        }

        System.out.println();
        
        for (int i = 0; i < TABLE.length; i ++) {        
            System.out.printf("|%-4s|", states[i]);    
            for (int j = 0; j < TABLE[0].length; j++) {
                if (TABLE[i][j] == null) System.out.printf("%-12s|", "-");
                else System.out.printf("%-12s|", TABLE[i][j]);
            }
            System.out.println();
        }

    }

    private int getPosition(String X, String[] terminals, String[] nonTerminals) {
        int pos = 0;

        // Checks first the terminals
        for (int i = 0; i < terminals.length; i++) {

            if (X.equals(terminals[i])) return pos; // Break when found
            else pos ++;
        }

        // Checks non-Terminals, continues adding to pos
        for (int i = 0; i < nonTerminals.length; i++) {
            if (X == nonTerminals[i]) return pos; // Breaks when found
            else pos ++;
        }

        return pos;
    }

    /**
     * 
     * @param pa    Augemnted production
     * @param po    Original production
     * @return
     */
    private boolean equalProductions(ArrayList<String> pa, ArrayList<String> po) {

        pa.remove(".");

        if (pa.size() != po.size()) return false;

        for (int i = 0; i < po.size(); i++ ) {
            if (!pa.get(i).equals(po.get(i))) {
                return false;
            }
        }
        
        return true;
    }

    private int getEqualProduction(Production p, ArrayList<Production> productions) {


        for (int i = 0; i < productions.size(); i++) {
            if (equalProductions(p.getProduce(), productions.get(i).getProduce())) return i;
            
        }

        return -1;
    }
    
}
