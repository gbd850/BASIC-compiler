package dev.peter.BASIC.compiler;

public class Parser {

    private final Lexer lexer;

    private Token curToken;

    private Token peekToken;

    public Parser(Lexer lexer) {
        this.curToken = null;
        this.peekToken = null;
        this.lexer = lexer;

        this.nextToken();
        this.nextToken(); // Call this twice to initialize current and peek.
    }

    public boolean checkToken(TokenType type) {
        return type == this.curToken.tokenType();
    }

    public boolean checkPeek(TokenType type) {
        return type == this.peekToken.tokenType();
    }

    public void match(TokenType type) {
        if (!this.checkToken(type)) {
            this.abort("Expected " + type.name() + ", got " + this.curToken.tokenType().name());
        }
        this.nextToken();
    }

    public void nextToken() {
        this.curToken = this.peekToken;
        this.peekToken = this.lexer.getToken();
    }

    private void abort(String message) {
        throw new RuntimeException("Parsing error: " + message);
    }

    public void program() {
    }
}
