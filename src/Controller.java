/*
 * @author: Ma. Isabel Solano
 * @version 3, 03/04/23
 * 
 * Main class
 */

package src;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Stack;

public class Controller {
    public static void main(String[] Args) {

        // selected document
        String selectedDoc = Args[0];

        // instances
        InfixToPostfix itp = new InfixToPostfix();
        GraphToFile gtf = new GraphToFile();
        TerminalCommand tc = new TerminalCommand();

        Yalex_reader yr = new Yalex_reader("input/slr-"+selectedDoc+".yal");
        ArrayList<Symbol> regex = yr.read();

        ArrayList<Token> tokens = yr.getTokens();

        System.out.println("\n_______Regex_______");
        System.out.println();
        for (Symbol s: regex) {
            System.out.print(s);
        }
        System.out.println();


        // System.out.println("\n___Is operator per symbol___");
        // for (Symbol s: regex) {
        //     System.out.println(s.c_id + " is operator " + (s.isOperator() ? "yes" : " no"));
        // }
        
        System.out.println("\n______Concat_______");
        
        Stack<Symbol> postfix = itp.convert(regex);

        HashMap<Integer, Symbol> alphabet = itp.getDic();
        ArrayList<Symbol> alphabet2 = itp.getDict2();

        System.out.println("\n_____Alphabet 2______\n");
        for (Symbol s: alphabet2) {
            System.out.print(s + ", ");
        }
        System.out.println();

        // Adding #. to the end of the regex
        Symbol end = new Symbol('#');
        Symbol concat = new Symbol('.');
        concat.setOperator(true);
        postfix.add(end);
        postfix.add(concat);

        String alph = "";
        for (Symbol s: alphabet.values()) {
            alph += String.valueOf(s.c_id);
        }
        System.out.println("\n______Alphabet_______");
        System.out.println(alph);

        String test = "";
        for (Symbol s: postfix) {
            test += String.valueOf(s.c_id);
        }

        // System.out.println("\n___Is operator per symbol___");
        // for (Symbol s: postfix) {
        //     System.out.println(s.c_id + " is operator " + (s.isOperator() ? "yes" : " no"));
        // }

        System.out.println("\n______Regex Postfix_______");
        System.out.println(test);

        System.out.println("\nSyntactic Tree");
        SintacticTree sintacticTree = new SintacticTree(postfix);
        // sintacticTree.printTree(sintacticTree.getRoot());
        // sintacticTree.TreePrinter(sintacticTree.getRoot(), "", true);
        AFD automata = new AFD(alphabet2, sintacticTree);
        System.out.println(automata);

        // AFD_direct graph
        String graphTxtFileName = "output/AFD_direct-"+selectedDoc+".txt";
        String graphJpgFileName = "output/AFD_direct-"+selectedDoc+".jpg";
        gtf.generateFile(graphTxtFileName, automata);
        tc.GraphAFN(graphTxtFileName, graphJpgFileName);

        // Generate Scanner
        ScanGen scanGen = new ScanGen();
        scanGen.genScanFile(tokens, automata);
    }

}