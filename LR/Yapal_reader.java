package LR;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.ArrayList;

import src.Token;


public class Yapal_reader {

    private String filename;

    private BufferedReader reader;

    private ArrayList<String> info = new ArrayList<>();
    private ArrayList<String> tokenString = new ArrayList<>();
    private ArrayList<String> productionNames = new ArrayList<>();
    private ArrayList<Production> terminalProductions = new ArrayList<>();
    private ArrayList<Production> productions = new ArrayList<>();

    private String producctionsStings = "";
    
    private ArrayList<Token> tokens = new ArrayList<>();

    
    public Yapal_reader(String filename) {
    
        this.filename = filename;

        TokensDeSerialization();
        fileToInfo();
        separateGroups();
        terminalTokenRecognition();
        productionGeneration();
        augmentProductions();
        genList();
 
    }

    /**
     * From TokensSerialized.ser to a Token Arraylist
     */
    private void TokensDeSerialization() {
        try {
            FileInputStream fileIn = new FileInputStream("output/TokensSerialized.ser");
            ObjectInputStream input = new ObjectInputStream(fileIn);

            ArrayList<?> rawTokens = (ArrayList<?>) input.readObject();

            tokens = new ArrayList<>();
            for (Object rawToken : rawTokens) {
                tokens.add((Token) rawToken);
            }
            input.close();

        } catch (IOException e) {
            e.printStackTrace();
            return;
        } catch (ClassNotFoundException c) {
            c.printStackTrace();
            return;
        }
    }

    /**
     * Removes comments
     */
    private void fileToInfo() {
        try {
            reader = new BufferedReader(new FileReader(filename));
            String line;
            boolean inComment = false;
            while ((line = reader.readLine()) != null) {
                if (!line.trim().isEmpty()) {
                    // if it's not empty
                    String temp_line = "";
                    char[] characters = line.toCharArray();
                    // search and eliminate comments
                    for (int i = 0; i < characters.length; i++ ) {
                        if (!inComment) {
                            // check for comment
                            if (characters[i] == '/') {
                                if (characters[i + 1] == '*') {
                                    inComment = true;

                                    // skip *
                                    i++;

                                } else {
                                    // copy into info without comments
                                    temp_line += characters[i];
                                }
                            } else {
                                // copy into info without comments
                                temp_line += characters[i];
                            }
                        } else {
                            // check if comment section has ended
                            if (characters[i] == '*') {
                                if (characters[i + 1] == '/') {
                                    inComment = false;

                                    // skip /
                                    i++;
                                }
                            }
                        }
                    }
                    // Add line to info
                    if (!temp_line.trim().isEmpty()) info.add(temp_line);
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private void separateGroups() {

        int pos = 0;
        for (int i = 0; i < info.size(); i++) {
            String configuration = info.get(i).trim().split(" ")[0].toLowerCase();

            // check for %token's or %ignore's
            if (configuration.equals("%token") || configuration.equals("%ignore")) {
                tokenString.add(info.get(i).trim());

            } else {
                // %token or %ignore stopped appearing, now it's jus producctions
                pos = i;
                break;
            }
        }

        for (int i = pos; i < info.size(); i ++){
            producctionsStings += info.get(i);
        }

    }

    private void terminalTokenRecognition() {
        for (String s: tokenString) {
            String temp[]  = s.split(" ");
            if (temp[0].equals("%token")) {
                Token tempToken = getSpecificToken(temp[1]);
                if (tempToken != null) {
                    // the token exists
                    Production tempProd = new Production(temp[1], false, true, tempToken);
                    terminalProductions.add(tempProd);

                } else {
                    // check for return value if not found
                    Token tempToken2 = getTokenByReturn(temp[1]);
                    if (tempToken2 != null) {
                        Production tempProd = new Production(temp[1], false, true, tempToken2);
                        terminalProductions.add(tempProd);

                    } else {
                        // Error
                        // TODO error handling
                    }
                    
                }
            } else if (temp[0].equals("%ignore")) {

            }
        }
    }

    private void productionGeneration() {
        String separatedProductions[] = producctionsStings.split(";");
        for (String s: separatedProductions) {
            s = s.replaceAll("\\s+", " ");

            String separated[] = s.split(":");
   

            String products[] = separated[1].trim().split("\\|");

            for (String p: products) {
                // create production
                Production currentProduction = new Production(separated[0], false, false);

                String elements[] = p.split(" ");

                for (String e: elements) {
                    // check for empty spaces
                    if (e.length() > 0) currentProduction.addProduce(e);
                }

                productions.add(currentProduction);
            }
        }

        // Add the terminal transitions
        for (Production p: terminalProductions) {
            productions.add(p);
        }
    }

    private void augmentProductions() {
        String initialProduction = productions.get(0).getName();
        String newInitialP = initialProduction + "'";

        Production newInitialProduction = new Production(newInitialP, false, false, null);
        newInitialProduction.addProduce(initialProduction);

        productions.add(0, newInitialProduction);
    }

    private void genList() {
        for (Production p: productions) {
            if (!productionNames.contains(p.getName())) productionNames.add(p.getName());
        }

        // Delete the augmented production
        productionNames.remove(0);
    }

    public ArrayList<Token> getTokens() {
        return tokens;
    }

    public ArrayList<String> getProductionNames() {
        return productionNames;
    }

    public ArrayList<Production> getProductions() {
        return productions;
    }

    private Token getSpecificToken(String name) {
        for (Token t: tokens) {
            String upperLexeme = t.getLexeme().toUpperCase();
            if (upperLexeme.equals(name.toUpperCase())) {
                return t;
            }
        }
        return null;
    }

    private Token getTokenByReturn(String ret) {
        for (Token t: tokens) {
            if (t.getFunction() != null) {
                if (t.getFunction().length() > 0) {
                    if (t.getFunction().startsWith("return")) {
                        String parts[] = t.getFunction().split(" ");
                        if (parts[1].equals(ret)) {
                            return t;
                        }
                    }
                }
            }
        }
        return null;
    }



}
