package dev.peter.BASIC.compiler;

import java.util.Objects;

import static java.lang.Character.*;

public class Lexer {

    private final String source;

    private char curChar;

    private int curPos;

    public Lexer(String source) {
        this.source = source + "\n";
        this.curChar = 0;
        this.curPos = -1;
        this.nextChar();
    }

    public char getCurChar() {
        return curChar;
    }

    public void nextChar() {
        this.curPos++;
        if (this.curPos >= source.length()) {
            this.curChar = '\0'; //EOF
        }
        else {
            this.curChar = this.source.charAt(this.curPos);
        }
    }

    public char peek() {
        if (this.curPos + 1 >= this.source.length()) {
            return '\0';
        }
        return this.source.charAt(this.curPos + 1);
    }

    public void abort(String message) {
        throw new RuntimeException("Lexing error: " + message);
    }

    public void skipWhitespace() {
        while (this.curChar == ' ' || this.curChar == '\t' || this.curChar == '\r') {
            this.nextChar();
        }
    }

    public void skipComment() {
        if (this.curChar == '#') {
            while (this.curChar != '\n') {
                this.nextChar();
            }
        }
    }

    public Token getToken() {
        this.skipWhitespace();
        this.skipComment();
        Token token = null;

        if (isDigit(this.curChar)) {
            int startPos = this.curPos;
            while (isDigit(this.peek())) {
                this.nextChar();
            }
            if (this.peek() == '.') {
                this.nextChar();
//                Must have at least one digit after decimal.
                if (!isDigit(this.peek())) {
                    this.abort("Illegal character in number");
                }
                while (isDigit(this.peek())) {
                    this.nextChar();
                }
            }
            String number = this.source.substring(startPos, this.curPos + 1);
            token = new Token(number, TokenType.NUMBER);
        }
        else if (isAlphabetic(this.curChar)) {
            int startPos = this.curPos;
            while (isLetterOrDigit(this.peek())) {
                this.nextChar();
            }
            String text = this.source.substring(startPos, this.curPos + 1);
            TokenType keyword = Token.checkIfKeyword(text);
            token = new Token(text, Objects.requireNonNullElse(keyword, TokenType.IDENT));
        }
        else {
            switch (this.curChar) {
                case '+' -> token = new Token(Character.toString(this.curChar), TokenType.PLUS);
                case '-' -> token = new Token(Character.toString(this.curChar), TokenType.MINUS);
                case '*' -> token = new Token(Character.toString(this.curChar), TokenType.ASTERISK);
                case '/' -> token = new Token(Character.toString(this.curChar), TokenType.SLASH);
                case '=' -> {
                    if (this.peek() == '=') {
                        char lastChar = this.curChar;
                        this.nextChar();
                        token = new Token(String.valueOf(lastChar) + this.curChar, TokenType.EQEQ);
                    } else {
                        token = new Token(Character.toString(this.curChar), TokenType.EQ);
                    }
                }
                case '>' -> {
                    if (this.peek() == '=') {
                        char lastChar = this.curChar;
                        this.nextChar();
                        token = new Token(String.valueOf(lastChar) + this.curChar, TokenType.GTEQ);
                    } else {
                        token = new Token(Character.toString(this.curChar), TokenType.GT);
                    }
                }
                case '<' -> {
                    if (this.peek() == '=') {
                        char lastChar = this.curChar;
                        this.nextChar();
                        token = new Token(String.valueOf(lastChar) + this.curChar, TokenType.LTEQ);
                    } else {
                        token = new Token(Character.toString(this.curChar), TokenType.LT);
                    }
                }
                case '!' -> {
                    if (this.peek() == '=') {
                        char lastChar = this.curChar;
                        this.nextChar();
                        token = new Token(String.valueOf(lastChar) + this.curChar, TokenType.NOTEQ);
                    } else {
                        this.abort("Expected !=, got !" + this.peek());
                    }
                }
                case '\"' -> {
                    this.nextChar();
                    int startPos = this.curPos;

                    while (this.curChar != '\"') {
                        if (this.curChar == '\r' || this.curChar == '\n' || this.curChar == '\t' || this.curChar == '\\' || this.curChar == '%') {
                            this.abort("Illegal character in string");
                        }
                        this.nextChar();
                    }
                    String text = this.source.substring(startPos, this.curPos);
                    token = new Token(text, TokenType.STRING);
                }
                case '\n' -> token = new Token(Character.toString(this.curChar), TokenType.NEWLINE);
                case '\0' -> token = new Token("\0", TokenType.EOF);
                default -> this.abort("Unknown token: " + this.curChar);
            }
        }
        this.nextChar();
        return token;
    }
}
