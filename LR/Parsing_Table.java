package LR;

import java.util.ArrayList;
import java.util.Arrays;


public class Parsing_Table {

    public Parsing_Table(Automata automata) {

        // Get symbols and productions from the automata
        ArrayList<String> nter = new ArrayList<>();
        ArrayList<String> ter = new ArrayList<>();

        for (Production p: automata.getProductions()) {
            if (p.isTerminal()) ter.add(p.getName());
            else nter.add(p.getName());
        }

        String[] nonTerminals = nter.toArray(new String[0]);
        String[] terminals = ter.toArray(new String[0]);

        // Get states numbers
        ArrayList<String> st = new ArrayList<>();
        for (State s: automata.getStates()) {
            st.add(Integer.toString(s.getStateNum()));
        }
        String[] states = st.toArray(new String[0]);

        System.out.println(Arrays.toString(terminals));
        System.out.println(Arrays.toString(nonTerminals));
        System.out.println(Arrays.toString(states));
        
        String[][] TABLA = new String[states.length][nonTerminals.length + terminals.length];

        System.out.println();

        System.out.printf("|%-12s|", "");
        for (int i = 0; i < terminals.length; i ++){
            System.out.printf("%-12s|", terminals[i]);
        }

        
        for (int i = 0; i < nonTerminals.length; i ++){
            System.out.printf("%-12s|", nonTerminals[i]);
        }

        System.out.println();
        
        for (int i = 0; i < TABLA.length; i ++) {        
            System.out.printf("|%-12s|", states[i]);    
            for (int j = 0; j < TABLA[0].length; j++) {
                System.out.printf("%-12s|", TABLA[i][j]);
            }
            System.out.println();
        }

    }

    @Override
    public String toString() {
        // TODO Auto-generated method stub
        return super.toString();
    }
    
}
