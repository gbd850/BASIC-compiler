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
        System.err.println("PROGRAM");

        while (this.checkToken(TokenType.NEWLINE)) {
            this.nextToken();
        }

        while (!this.checkToken(TokenType.EOF)) {
            this.statement();
        }
    }

    private void statement() {
//        "PRINT" (expression | string)
        if (this.checkToken(TokenType.PRINT)) {
            System.err.println("STATEMENT - PRINT");
            this.nextToken();

            if (this.checkToken(TokenType.STRING)) {
                this.nextToken();
            } else {
                this.expression();
            }
        }
//        "IF" comparison "THEN" {statement} "ENDIF"
        else if (this.checkToken(TokenType.IF)) {
            System.err.println("STATEMENT - IF");

            this.nextToken();
            this.comparison();

            this.match(TokenType.THEN);
            this.nl();

            while (!this.checkToken(TokenType.ENDIF)) {
                this.statement();
            }
            this.match(TokenType.ENDIF);
        }
//        "WHILE" comparison "REPEAT" {statement} "ENDWHILE"
        else if (this.checkToken(TokenType.WHILE)) {
            System.err.println("STATEMENT - WHILE");

            this.nextToken();
            this.comparison();

            this.match(TokenType.REPEAT);
            this.nl();

            while (!this.checkToken(TokenType.ENDWHILE)) {
                this.statement();
            }
            this.match(TokenType.ENDWHILE);
        }
//        "LABEL" ident
        else if (this.checkToken(TokenType.LABEL)) {
            System.err.println("STATEMENT - LABEL");

            this.nextToken();
            this.match(TokenType.IDENT);
        }
//        "GOTO" ident
        else if (this.checkToken(TokenType.GOTO)) {
            System.err.println("STATEMENT - GOTO");

            this.nextToken();
            this.match(TokenType.IDENT);
        }
//        "LET" ident "=" expression
        else if (this.checkToken(TokenType.LET)) {
            System.err.println("STATEMENT - LET");

            this.nextToken();
            this.match(TokenType.IDENT);
            this.match(TokenType.EQ);
            this.expression();
        }
//        "INPUT" ident
        else if (this.checkToken(TokenType.INPUT)) {
            System.err.println("STATEMENT - INPUT");

            this.nextToken();
            this.match(TokenType.IDENT);
        }
        else {
            this.abort("Invalid statement at " + this.curToken.tokenText() + " (" + this.curToken.tokenType().name() + ")");
        }

        this.nl();
    }

    private void comparison() {

    }

    private void expression() {

    }

    private void nl() {
        System.err.println("NEWLINE");

        this.match(TokenType.NEWLINE);
        while (this.checkToken(TokenType.NEWLINE)) {
            this.nextToken();
        }
    }
}
