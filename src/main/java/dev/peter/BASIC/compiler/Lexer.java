package dev.peter.BASIC.compiler;

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

    }

    public void skipWhitespace() {

    }

    public void skipComment() {

    }

    public char getToken() {
        return 0;
    }
}
