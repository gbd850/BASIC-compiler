package dev.peter.BASIC.compiler;

public class Main {
    public static void main(String[] args) {
        String source = "IF+-123 foo*THEN/";
        Lexer lexer = new Lexer(source);

        Token token = lexer.getToken();
        while (token.tokenType() != TokenType.EOF) {
            System.out.println(token.tokenType());
            token = lexer.getToken();
        }
    }
}
