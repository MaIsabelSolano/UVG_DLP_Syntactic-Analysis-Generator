package LR;

import java.util.ArrayList;

import src.GraphToFile;
import src.TerminalCommand;

// import src.Token;

public class Controller {

    public static void main(String[] Args) {

        // recieve the number of the yalp
        String selectedDoc = Args[0];

        // YAPAL reading
        Yapal_reader yReader = new Yapal_reader("input/slr-"+selectedDoc+".yalp");
        // ArrayList<Token> tokens = yReader.getTokens();
        ArrayList<Production> productions = yReader.getProductions();
        ArrayList<Production> productionsDot = new ArrayList<>();
        ArrayList<String> productionList = yReader.getProductionNames();
        
        System.out.println("\nProductions");
        for (int i = 0; i < productions.size(); i++) {

            Production newP = new Production(productions.get(i));

            if (!newP.isTerminal()) newP.addProduce(".", 0);
            productionsDot.add(newP);

            System.out.println(productions.get(i));
            // System.out.println("D: " + productionsDot.get(i));
            
        }

        Automata LR_0 = new Automata(productions, productionsDot, productionList);

        System.out.println(LR_0);

        System.out.println("\nFIRST(n)");
        for (String s: productionList) {
            System.out.println(s + ":" + LR_0.FIRST(s).toString());
        }

        System.out.println("\nFOLLOW(n)");
        for (String s: productionList) {
            System.out.println(s + ":" + LR_0.FOLLOW(s).toString());
        }

        System.out.println();

        GraphToFile gtf = new GraphToFile();
        gtf.generateFile("output/LR_0-"+selectedDoc+".txt", LR_0);
        TerminalCommand tc = new TerminalCommand();
        tc.GraphAFN("output/LR_0-"+selectedDoc+".txt", "output/LR_0-"+selectedDoc+".jpg");

        
    }
    
}
