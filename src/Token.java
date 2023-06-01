/*
 * @author: Ma. Isabel Solano
 * @version 2, 03/04/2023
 * 
 * Class to define a token
 * 
 */

package src;

import java.io.Serializable;
import java.util.ArrayList;

public class Token implements Serializable{

    private String lexeme;
    private ArrayList<Token> value = new ArrayList<>();
    private String function; 
    private boolean terminal;
    private boolean isOperator = false;

    /**
     * Simple constructor
     * 
     * @param lexeme    Name of the lexeme
     * @param terminal  If it's a terminal token or not
     */
    public Token(String lexeme, Boolean terminal) {
        this.lexeme = lexeme;
        this.terminal = terminal;
    }

    /**
     * Simple constructor that includes the value
     * 
     * @param lexeme    Name of lexeme
     * @param value     Value of lexeme
     * @param terminal  If it's a terminal token or not
     */
    public Token(String lexeme, ArrayList<Token> value, boolean terminal) {
        this.lexeme = lexeme;
        this.value = value;
        this.terminal = terminal;
    }

    /**
     * If the last toiken in the production is an or,
     * delet it
     */
    public void deleteLastOr() {
        if (value.get(value.size()-1).lexeme.equals("|")) {
            value.remove(value.size()-1);
        }
    }

    /* Getters and setters */
    public String getLexeme() {
        return lexeme;
    }

    public String getFunction() {
        return function;
    }

    public ArrayList<Token> getValue() {
        return value;
    }

    public boolean isOperator() {
        return isOperator;
    }

    public boolean isTerminal() {
        return terminal;
    }

    public void setFunction(String function) {
        this.function = function;
    }

    public void setValue(ArrayList<Token> value) {
        this.value = value;
    }

    public void setAsOperator() {
        this.isOperator = true;
    }

    public void setOperator(boolean isOperator) {
        this.isOperator = isOperator;
    }

    public void addValueToken(Token t) {
        value.add(t);
    }

    public void addValueToken(Token t, int pos) {
        value.add(pos, t);
    }

    @Override
    public String toString() {
        String print = "";
        print += "{";
        print += "\n\tlexeme: \"" + lexeme+ "\""; 
        print += "\n\tvalue: [";
        for (Token t: value) {
            print += t.getLexeme() + " ";
        }
        print += "]";
        print += "\n\tfunction: {" + function +"}";
        print += "\n}";
        return print;
    }
    
}
