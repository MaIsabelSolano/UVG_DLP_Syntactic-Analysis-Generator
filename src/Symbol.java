/*
 * @author: Ma. Isabel Solano
 * @version 3, 03/04/23
 * 
 * Symbol class that stores the char and int value of each symbol
 * 
 */

package src;
public class Symbol {
    
    public int id; //ASCII
    public char c_id;
    private boolean isOperator = false;

    /**
     * Symbol class constructor
     * 
     * @param a Symbol 
     */
    public Symbol(char a){

        this.c_id = a;
        this.id = a;
    }

    /* Getters and setters */

    public boolean isOperator() {
        return isOperator;
    }

    public void setOperator(boolean isOperator) {
        this.isOperator = isOperator;
    }

    @Override
    public String toString() {
        return "" + c_id;
    }
}
