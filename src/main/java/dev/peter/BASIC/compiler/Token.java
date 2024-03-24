package dev.peter.BASIC.compiler;

public record Token(String tokenText, TokenType tokenType) {
    public static TokenType checkIfKeyword(String text) {
        for (TokenType type : TokenType.values()) {
//            Relies on all keyword enum values being 1XX
            if (type.name().equals(text) && type.getValue() >= 100 && type.getValue() < 200) {
                return type;
            }
        }
        return null;
    }
}
