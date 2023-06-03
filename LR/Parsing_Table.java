package LR;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Stack;


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
                    
                    // let's find it´s corresponding production
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

            String symbol = "";

            if (terminals[i].toLowerCase().equals("number")) symbol = "n";
            if (terminals[i].toLowerCase().equals("plus")) symbol = "+";
            if (terminals[i].toLowerCase().equals("times")) symbol = "*";
            if (terminals[i].toLowerCase().equals("lparen")) symbol = "(";
            if (terminals[i].toLowerCase().equals("rparen")) symbol = ")";
            if (terminals[i].toLowerCase().equals("id")) symbol = "id";
            if (terminals[i].toLowerCase().equals("minus")) symbol = "-";
            if (terminals[i].toLowerCase().equals("div")) symbol = "/";
            if (terminals[i].toLowerCase().equals("semicolon")) symbol = ";";
            if (terminals[i].toLowerCase().equals("eq")) symbol = "=";
            if (terminals[i].toLowerCase().equals("assignop")) symbol = ":=";
            if (terminals[i].toLowerCase().equals("lt")) symbol = "<";
            if (terminals[i].toLowerCase().equals("$")) symbol = "$";

            System.out.printf("%-5s|", symbol);
            // System.out.printf("%-5s|", terminals[i]);
        }

        
        for (int i = 0; i < nonTerminals.length; i ++){
            String initial = "" + nonTerminals[i].charAt(0);
            System.out.printf("%-5s|", initial);
            // System.out.printf("%-5s|", nonTerminals[i]);

        }

        System.out.println();
        
        for (int i = 0; i < TABLE.length; i ++) {        
            System.out.printf("|%-4s|", states[i]);    
            for (int j = 0; j < TABLE[0].length; j++) {
                if (TABLE[i][j] == null) System.out.printf("%-5s|", "-");
                else System.out.printf("%-5s|", TABLE[i][j]);
            }
            System.out.println();
        }


        // SIMULATION
        
        // Reading found tokens from Tokens.txt
        // Tokens Stack
        Stack<String> input = new Stack<>();


        String filePath = "output/Tokens.txt";

        try {

            List<String> lines = Files.readAllLines(Paths.get(filePath));

            // starts at 1 to ignore the name of the column
            for (int i = 1; i < lines.size(); i ++) {
                String[] tuple = lines.get(i).split("->");

                String left = tuple[0].replaceAll("\\s+", "");
                String right = tuple[1].replaceAll("\\s+", "");

                // ignore WS
                if (!left.equals("\\n") && !left.equals("\\s") && !right.equals("ws")) {
                    input.add(0, right); // add at the beggining
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        // Add end token at the beggining
        input.add(0, "$"); 

        System.out.println("Tokens found");
        System.out.println(input.toString());

        // States Stack
        Stack<Integer> statesStack = new Stack<>();
        statesStack.add(automata.getStates().get(0).getStateNum());

        // Symbols Stack
        Stack<String> symbolStack = new Stack<>();
        symbolStack.add("$");

        


        boolean done = false;
        int num = 1; 
        
        String currentSymbol = input.peek();
        int posSymbol = getPosition(currentSymbol, terminals, nonTerminals);
        int currentState = 0;
        String nextAction = TABLE[currentState][posSymbol];

        System.out.println("__________________________________________________");
        System.out.println("__________________________________________________");
        System.out.println(" " + num + ": ");
        System.out.println("Stack: " + statesStack.toString());
        System.out.println("Symbols: " + symbolStack.toString());
        System.out.println("Current symbol: " + currentSymbol);
        System.out.println("Current state: " + currentState);
        System.out.println("Input: " + input.toString());
        System.out.println("Action: " + nextAction);

        while (!done && num < 100) {
            num ++;

            if (nextAction == null) {

                System.out.println("\nError");

                ArrayList<String> expectedTokens = new ArrayList<>();

                for (int x = 0; x < terminals.length; x ++) {
                    if (TABLE[currentState][x] != null) expectedTokens.add(terminals[x]);
                }

                System.out.println("Expected " + expectedTokens.toString() + " but found " + currentSymbol);

                break;
            }

            if (nextAction.equals("acc") || nextAction.equals("r0")) {
                // Accept!
                System.out.println("\nAccept!");

                // End loop
                break;

            } else if (nextAction.charAt(0) == 's') {
                // shift

                // state 
                currentState = Integer.parseInt(nextAction.substring(1));
                statesStack.push(currentState);

                symbolStack.push(currentSymbol);

                // get new symbols
                input.pop();
                currentSymbol = input.peek();


                posSymbol = getPosition(currentSymbol, terminals, nonTerminals);
                nextAction = TABLE[statesStack.peek()][posSymbol];
                
                

                // // Add current symbols to symbol stack 
                // symbolStack.add(currentSymbol);

                // // Add to stateStack


            } else if (nextAction.charAt(0) == 'r') {
                // reduce

                

                int reduceTo = Integer.parseInt(nextAction.substring(1));

                Production reduceToP = automata.getProductions().get(reduceTo);
                System.out.println("reduce to " + reduceToP.toString());

                for (String s: reduceToP.getProduce()) statesStack.pop();


                int posProduction = getPosition(reduceToP.getName(), terminals, nonTerminals);
                

                String stateString = TABLE[statesStack.peek()][posProduction];

                statesStack.push(Integer.parseInt(stateString));

                // get next action
                posSymbol = getPosition(currentSymbol, terminals, nonTerminals);
                nextAction = TABLE[statesStack.peek()][posSymbol];
                

            }

            else {

                System.out.println("\nError");

                ArrayList<String> expectedTokens = new ArrayList<>();

                for (int x = 0; x < terminals.length; x ++) {
                    if (TABLE[currentState][x] != null) expectedTokens.add(terminals[x]);
                }

                System.out.println("Expected " + expectedTokens.toString() + " but found " + currentSymbol);

                break;
            }
            

            // printing results
            System.out.println("__________________________________________________");
            System.out.println("__________________________________________________");
            System.out.println(" " + num + ": ");
            System.out.println("Stack: " + statesStack.toString());
            System.out.println("Symbols: " + symbolStack.toString());
            System.out.println("Current symbol: " + currentSymbol);
            System.out.println("Current state: " + currentState);
            System.out.println("Input: " + input.toString());
            System.out.println("Action: " + nextAction);

            // Avoids getting stuck in infinite loops
            
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
