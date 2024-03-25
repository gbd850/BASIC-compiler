package dev.peter.BASIC.compiler;

import java.util.HashSet;
import java.util.Set;

public class Parser {

    private final Lexer lexer;

    private final Emitter emitter;

    private Token curToken;

    private Token peekToken;

    private Set<String> symbols;

    private Set<String> labelsDeclared;

    private Set<String> labelsGotoed;

    public Parser(Lexer lexer, Emitter emitter) {
        this.lexer = lexer;
        this.emitter = emitter;

        this.symbols = new HashSet<>();
        this.labelsDeclared = new HashSet<>();
        this.labelsGotoed = new HashSet<>();

        this.curToken = null;
        this.peekToken = null;
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

//    program ::= {statement}
    public void program() {
        System.err.println("PROGRAM");

        this.emitter.headerLine("#include <stdio.h>");
        this.emitter.headerLine("#include <stdlib.h>");
        this.emitter.headerLine("int main(void) {");

//        Since some newlines are required in our grammar, need to skip the excess.
        while (this.checkToken(TokenType.NEWLINE)) {
            this.nextToken();
        }

//        Parse all the statements in the program.
        while (!this.checkToken(TokenType.EOF)) {
            this.statement();
        }

//        Wrap things up.
        this.emitter.emitLine("system ( \"PAUSE\" );");
        this.emitter.emitLine("return 0;");
        this.emitter.emitLine("}");

//        Check that each label referenced in a GOTO is declared.
        for (String label : this.labelsGotoed) {
            if (!this.labelsDeclared.contains(label)) {
                this.abort("Attemtping to GOTO to undeclared label: " + label);
            }
        }
    }

    private void statement() {
//        "PRINT" (expression | string)
        if (this.checkToken(TokenType.PRINT)) {
            System.err.println("STATEMENT - PRINT");
            this.nextToken();

            if (this.checkToken(TokenType.STRING)) {
                this.emitter.emitLine("printf(\"" + this.curToken.tokenText() + "\\n\");");
                this.nextToken();
            } else {
                this.emitter.emit("printf(\"%.2f\\n\", (float) (");
                this.expression();
                this.emitter.emitLine("));");
            }
        }
//        "IF" comparison "THEN" {statement} "ENDIF"
        else if (this.checkToken(TokenType.IF)) {
            System.err.println("STATEMENT - IF");

            this.nextToken();
            this.emitter.emit("if (");
            this.comparison();

            this.match(TokenType.THEN);
            this.nl();
            this.emitter.emitLine(") {");

            while (!this.checkToken(TokenType.ENDIF)) {
                this.statement();
            }
            this.match(TokenType.ENDIF);
            this.emitter.emitLine("}");
        }
//        "WHILE" comparison "REPEAT" {statement} "ENDWHILE"
        else if (this.checkToken(TokenType.WHILE)) {
            System.err.println("STATEMENT - WHILE");

            this.nextToken();
            this.emitter.emit("while (");
            this.comparison();

            this.match(TokenType.REPEAT);
            this.nl();
            this.emitter.emitLine(") {");

            while (!this.checkToken(TokenType.ENDWHILE)) {
                this.statement();
            }
            this.match(TokenType.ENDWHILE);
            this.emitter.emitLine("}");
        }
//        "LABEL" ident
        else if (this.checkToken(TokenType.LABEL)) {
            System.err.println("STATEMENT - LABEL");

            this.nextToken();

            if (this.labelsDeclared.contains(this.curToken.tokenText())) {
                this.abort("Label already exists: " + this.curToken.tokenText());
            }
            this.labelsDeclared.add(this.curToken.tokenText());

            this.emitter.emitLine(this.curToken.tokenText() + ":");
            this.match(TokenType.IDENT);
        }
//        "GOTO" ident
        else if (this.checkToken(TokenType.GOTO)) {
            System.err.println("STATEMENT - GOTO");

            this.nextToken();
            this.labelsGotoed.add(this.curToken.tokenText());
            this.emitter.emitLine("goto " + this.curToken.tokenText() + ";");
            this.match(TokenType.IDENT);
        }
//        "LET" ident "=" expression
        else if (this.checkToken(TokenType.LET)) {
            System.err.println("STATEMENT - LET");

            this.nextToken();

            if (!this.symbols.contains(this.curToken.tokenText())) {
                this.symbols.add(this.curToken.tokenText());
                this.emitter.headerLine("float " + this.curToken.tokenText() + ";");
            }

            this.emitter.emit(this.curToken.tokenText() + " = ");
            this.match(TokenType.IDENT);
            this.match(TokenType.EQ);

            this.expression();
            this.emitter.emitLine(";");
        }
//        "INPUT" ident
        else if (this.checkToken(TokenType.INPUT)) {
            System.err.println("STATEMENT - INPUT");

            this.nextToken();

            if (!this.symbols.contains(this.curToken.tokenText())) {
                this.symbols.add(this.curToken.tokenText());
                this.emitter.headerLine("float " + this.curToken.tokenText() + ";");
            }

//            Emit scanf but also validate the input. If invalid, set the variable to 0 and clear the input.
            this.emitter.emitLine("if (0 == scanf(\"%f\", &" + this.curToken.tokenText() + ")) {");
            this.emitter.emitLine(this.curToken.tokenText() + " = 0;");
            this.emitter.emitLine("scanf(\"%*s\");");
            this.emitter.emitLine("}");
            this.match(TokenType.IDENT);
        }
        else {
            this.abort("Invalid statement at " + this.curToken.tokenText() + " (" + this.curToken.tokenType().name() + ")");
        }

        this.nl();
    }

//    comparison ::= expression (("==" | "!=" | ">" | ">=" | "<" | "<=") expression)+
    private void comparison() {
        System.err.println("COMPARISON");

        this.expression();

        if (this.isComparisonOperator()) {
            this.emitter.emit(this.curToken.tokenText());
            this.nextToken();
            this.expression();
        }
        else {
            this.abort("Expected comparison operator at: " + this.curToken.tokenText());
        }

        while (this.isComparisonOperator()) {
            this.emitter.emit(this.curToken.tokenText());
            this.nextToken();
            this.expression();
        }
    }

    private boolean isComparisonOperator() {
        return this.checkToken(TokenType.GT) ||
                this.checkToken(TokenType.GTEQ) ||
                this.checkToken(TokenType.LT) ||
                this.checkToken(TokenType.LTEQ) ||
                this.checkToken(TokenType.EQEQ) ||
                this.checkToken(TokenType.NOTEQ);
    }

//    expression ::= term {( "-" | "+" ) term}
    private void expression() {
        System.err.println("EXPRESSION");

        this.term();

        while (this.checkToken(TokenType.PLUS) || this.checkToken(TokenType.MINUS)) {
            this.emitter.emit(this.curToken.tokenText());
            this.nextToken();
            this.term();
        }
    }

//    term ::= unary {( "/" | "*" ) unary}
    private void term() {
        System.err.println("TERM");

        this.unary();
        while (this.checkToken(TokenType.SLASH) || this.checkToken(TokenType.ASTERISK)) {
            this.emitter.emit(this.curToken.tokenText());
            this.nextToken();
            this.unary();
        }
    }

//    unary ::= ["+" | "-"] primary
    private void unary() {
        System.err.println("UNARY");

        if (this.checkToken(TokenType.PLUS) || this.checkToken(TokenType.MINUS)) {
            this.emitter.emit(this.curToken.tokenText());
            this.nextToken();
        }
        this.primary();
    }

//    primary ::= number | ident
    private void primary() {
        System.err.println("PRIMARY (" + this.curToken.tokenText() + ")");

        if (this.checkToken(TokenType.NUMBER)) {
            this.emitter.emit(this.curToken.tokenText());
            this.nextToken();
        } else if (this.checkToken(TokenType.IDENT)) {
            if (!this.symbols.contains(this.curToken.tokenText())) {
                this.abort("Referencing variable before assignment: " + this.curToken.tokenText());
            }
            this.emitter.emit(this.curToken.tokenText());
            this.nextToken();
        }
        else {
            this.abort("Unexpected token at " + this.curToken.tokenText());
        }
    }

    private void nl() {
        System.err.println("NEWLINE");

        this.match(TokenType.NEWLINE);
        while (this.checkToken(TokenType.NEWLINE)) {
            this.nextToken();
        }
    }
}
