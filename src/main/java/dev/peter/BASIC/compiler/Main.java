package dev.peter.BASIC.compiler;

public class Main {
    public static void main(String[] args) {
        String source = "LET foobar = 123";
        Lexer lexer = new Lexer(source);

        while (lexer.peek() != '\0') {
            System.out.println(lexer.getCurChar());
            lexer.nextChar();
        }
    }
}
