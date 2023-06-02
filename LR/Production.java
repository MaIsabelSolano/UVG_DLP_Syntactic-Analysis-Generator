package LR;

import java.util.ArrayList;

import src.Token;

public class Production {

    private String name;
    private ArrayList<String> produce = new ArrayList<>();
    private boolean ignore = false;
    private boolean terminal = false;
    private Token matchingToken;

    public Production(String name, boolean ignore, boolean terminal) {
        this.name= name;
        this.ignore = ignore;
        this.terminal = terminal;
    };

    public Production(String name, boolean ignore, boolean terminal, Token matchingToken) {
        this.name= name;
        this.ignore = ignore;
        this.terminal = terminal;
        this.matchingToken = matchingToken;
    };

    public Production(Production p) {
        name = p.getName();
        produce = new ArrayList<>(p.getProduce());
        ignore = p.isIgnore();
        terminal = p.isTerminal();
        matchingToken = p.getMatchingToken();
    }
    
    public void addProduce(String p) {
        produce.add(p);
    }

    public void addProduce(String p, int pos) {
        produce.add(pos, p);
    }

    public void setIgnorable() {
        this.ignore = true;
    }

    public void setProduce(ArrayList<String> produce) {
        this.produce = produce;
    }

    public String getName() {
        return name;
    }

    public Token getMatchingToken() {
        return matchingToken;
    }

    public ArrayList<String> getProduce() {
        return produce;
    }

    public boolean isIgnore() {
        return ignore;
    }

    public boolean isTerminal() {
        return terminal;
    }

    @Override
    public boolean equals(Object obj) {
        
        Production o = (Production) obj;

        if (o.getProduce().size() != produce.size()) return false;

        // for (String s: produce) {

        //     if (!o.getProduce().contains(s)) return false;
        // }

        for (int i = 0; i < produce.size(); i++ ) {
            if (!produce.get(i).equals(o.getProduce().get(i))) return false;
        }

        return true; 
    }

    @Override
    public String toString() {
        String ret = "";
        if (!terminal) {
            String prod = "";
            for (String p: produce) {
                prod += p + " ";
            }
            ret = name + " -> " + prod;
        } else {
            ret = name + " -> ";
        }
        return ret;
    }
    
}
