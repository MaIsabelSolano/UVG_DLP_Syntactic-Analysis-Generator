package src;

public class Terminator extends Symbol {

    private Token relatedToken; 
    private String associatedToken;

    public Terminator(Token relToken) {
        super('$');

        this.relatedToken = relToken;
        this.associatedToken = relToken.getLexeme();
    }

    public Terminator(String relToken) {
        super('$');

        this.associatedToken = relToken;
    }

    public Token getRelatedToken() {
        return relatedToken;

    }

    public String getAssociatedToken() {
        return associatedToken;
    }
}
