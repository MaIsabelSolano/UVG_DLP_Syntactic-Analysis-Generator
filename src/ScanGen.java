package src;

import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.ArrayList;

public class ScanGen {
    
    public ScanGen() {}

    public void genScanFile(ArrayList<Token> tokens, AFD afd) {

        // store the automata 
        System.out.println("\nAutomata Store: \n");
        String fileNameAutomata = "output/Automata.txt";
        String automataInfo = "";
        // Symbols
        for (Symbol sym: afd.getAlphabet()) {
            if (sym instanceof Terminator) {
                Terminator temp = (Terminator) sym;
                automataInfo += "$," + temp.getAssociatedToken();
            }
            if (sym.c_id == ' ') {
                automataInfo += "\\s~";
            } else if (sym.c_id == '\n') {
                automataInfo += "\\n~";
            } else if (sym.c_id == '\t') {
                automataInfo += "\\t~";
            } else {
                automataInfo += sym.c_id + "~";
            }
        }
        automataInfo += "\n";

        // States
        for (State s: afd.getStates()) {
            automataInfo += s.id+","+s.type.toString()+"~";
        }
        automataInfo += "\n";

        // Initial State
        automataInfo += Integer.toString(afd.getInitialState().id) +"\n";

        // Transition
        for (Transition t: afd.getTransitions()) {
            if (t.getSymbol() instanceof Terminator) {
                Terminator temp = (Terminator) t.getSymbol();
                automataInfo += t.getOriginState() + "," + t.getFinalState().id + "," + temp.id + "," + temp.getAssociatedToken() + "~";

            } else{
                automataInfo += t.getOriginState() + "," + t.getFinalState().id + "," + t.getSymbol().id + "~";
            }
            
        }
        automataInfo += "\n";

        // FinalStates
        for (State s: afd.getStates()) {
            if (s.isFinal()) {
                automataInfo += s.id+"~";
            }
        }
        
        try {
            FileWriter fileWriter = new FileWriter(fileNameAutomata);
            fileWriter.write(automataInfo);
            fileWriter.close();
            System.out.println("Automata File created and written successfully.");
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Storing the Tokens
        System.out.println("\nAutomata Store: \n");
        String fileNameTokens = "output/AFD_Tokens.txt";
        String tokensInfo = "";

        for (Token t: tokens) {
            tokensInfo += "\n¬\n";
            tokensInfo += "name: " + t.getLexeme() + "\n";
            tokensInfo += "value: ";
            for (Token v: t.getValue()) {
                tokensInfo += "~"+v.getLexeme();
            }
            tokensInfo += "\nfunction: "+t.getFunction();
            tokensInfo += "\nterminal: "+t.isTerminal();
            tokensInfo += "\noperator: "+t.isOperator();
        }
        try {
            FileWriter fileWriter = new FileWriter(fileNameTokens);
            fileWriter.write(tokensInfo);
            fileWriter.close();
            System.out.println("Tokens' File created and written successfully.");
        } catch (IOException e) {
            e.printStackTrace();
        }     
        
        
        // serialization
        try {
            FileOutputStream fileOut = new FileOutputStream("output/TokensSerialized.ser");
            ObjectOutputStream output = new ObjectOutputStream(fileOut);
            output.writeObject(tokens);
            output.close();
            
        } catch (IOException e) {
            e.printStackTrace();

        }



    }
}
