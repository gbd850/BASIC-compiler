package dev.peter.BASIC.compiler;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class LexerTest {

    private Lexer lexer;

    @Test
    void shouldReturnCurrentCharacter() {
        String content = "PRINT \"Hello World\"";
        lexer = new Lexer(content);

        Character character = lexer.getCurChar();

        assertEquals('P', character);
    }

    @Test
    void shouldMoveToTheNextCharacter() {
        String content = "PRINT \"Hello World\"";
        lexer = new Lexer(content);

        lexer.nextChar();
        Character character = lexer.getCurChar();

        assertEquals('R', character);
    }

    @Test
    void shouldSetEOFCharacter() {
        String content = "";
        lexer = new Lexer(content);

        lexer.nextChar();
        Character character = lexer.getCurChar();

        assertEquals('\0', character);
    }

    @Test
    void shouldPeekNextCharacter() {
        String content = "PRINT \"Hello World\"";
        lexer = new Lexer(content);

        Character character = lexer.peek();

        assertEquals('R', character);
    }

    @Test
    void shouldPeekEOFCharacter() {
        String content = "";
        lexer = new Lexer(content);

        Character character = lexer.peek();

        assertEquals('\0', character);
    }

    @Test
    void shouldSkipWhitespace() {
        String content = "  \r \tHello World";
        lexer = new Lexer(content);

        lexer.skipWhitespace();
        Character character = lexer.getCurChar();

        assertEquals('H', character);
    }

    @Test
    void shouldSkipComment() {
        String content = "# aaaaaaa\nHello World";
        lexer = new Lexer(content);

        lexer.skipComment();
        Character character = lexer.getCurChar();

        assertEquals('\n', character);
    }

    @Test
    void shouldGetNumberToken() {
        String content = "123";
        lexer = new Lexer(content);

        Token token = lexer.getToken();

        assertEquals(TokenType.NUMBER, token.tokenType());
        assertEquals("123", token.tokenText());
    }

    @Test
    void shouldThrowExceptionForIllegalNumber() {
        String content = "123.";
        lexer = new Lexer(content);

        assertThrows(RuntimeException.class, () -> lexer.getToken());
    }

    @Test
    void shouldGetKeywordToken() {
        String content = "PRINT";
        lexer = new Lexer(content);

        Token token = lexer.getToken();

        assertEquals(TokenType.PRINT, token.tokenType());
        assertEquals("PRINT", token.tokenText());
    }

    @Test
    void shouldGetIdentifierToken() {
        String content = "a";
        lexer = new Lexer(content);

        Token token = lexer.getToken();

        assertEquals(TokenType.IDENT, token.tokenType());
        assertEquals("a", token.tokenText());
    }

    @Test
    void shouldGetPlusOperatorToken() {
        String content = "+";
        lexer = new Lexer(content);

        Token token = lexer.getToken();

        assertEquals(TokenType.PLUS, token.tokenType());
        assertEquals("+", token.tokenText());
    }

    @Test
    void shouldGetMinusOperatorToken() {
        String content = "-";
        lexer = new Lexer(content);

        Token token = lexer.getToken();

        assertEquals(TokenType.MINUS, token.tokenType());
        assertEquals("-", token.tokenText());
    }

    @Test
    void shouldGetMultiplyOperatorToken() {
        String content = "*";
        lexer = new Lexer(content);

        Token token = lexer.getToken();

        assertEquals(TokenType.ASTERISK, token.tokenType());
        assertEquals("*", token.tokenText());
    }

    @Test
    void shouldGetDivisionOperatorToken() {
        String content = "/";
        lexer = new Lexer(content);

        Token token = lexer.getToken();

        assertEquals(TokenType.SLASH, token.tokenType());
        assertEquals("/", token.tokenText());
    }

    @Test
    void shouldGetEqualsOperatorToken() {
        String content = "=";
        lexer = new Lexer(content);

        Token token = lexer.getToken();

        assertEquals(TokenType.EQ, token.tokenType());
        assertEquals("=", token.tokenText());
    }

    @Test
    void shouldGetDoubleEqualsOperatorToken() {
        String content = "==";
        lexer = new Lexer(content);

        Token token = lexer.getToken();

        assertEquals(TokenType.EQEQ, token.tokenType());
        assertEquals("==", token.tokenText());
    }

    @Test
    void shouldGetGreaterThanOperatorToken() {
        String content = ">";
        lexer = new Lexer(content);

        Token token = lexer.getToken();

        assertEquals(TokenType.GT, token.tokenType());
        assertEquals(">", token.tokenText());
    }

    @Test
    void shouldGetGreaterThanOrEqualsOperatorToken() {
        String content = ">=";
        lexer = new Lexer(content);

        Token token = lexer.getToken();

        assertEquals(TokenType.GTEQ, token.tokenType());
        assertEquals(">=", token.tokenText());
    }

    @Test
    void shouldGetLessThanOperatorToken() {
        String content = "<";
        lexer = new Lexer(content);

        Token token = lexer.getToken();

        assertEquals(TokenType.LT, token.tokenType());
        assertEquals("<", token.tokenText());
    }

    @Test
    void shouldGetLessThanOrEqualsOperatorToken() {
        String content = "<=";
        lexer = new Lexer(content);

        Token token = lexer.getToken();

        assertEquals(TokenType.LTEQ, token.tokenType());
        assertEquals("<=", token.tokenText());
    }

    @Test
    void shouldGetNotEqualOperatorToken() {
        String content = "!=";
        lexer = new Lexer(content);

        Token token = lexer.getToken();

        assertEquals(TokenType.NOTEQ, token.tokenType());
        assertEquals("!=", token.tokenText());
    }

    @Test
    void shouldThrowExceptionForIllegalNotEqualOperator() {
        String content = "!>";
        lexer = new Lexer(content);

        assertThrows(RuntimeException.class, () -> lexer.getToken());
    }

    @Test
    void shouldThrowExceptionForIllegalCharacterInString() {
        String content = "\"%";
        lexer = new Lexer(content);

        assertThrows(RuntimeException.class, () -> lexer.getToken());
    }

    @Test
    void shouldGetStringToken() {
        String content = "\"Hello\"";
        lexer = new Lexer(content);

        Token token = lexer.getToken();

        assertEquals(TokenType.STRING, token.tokenType());
        assertEquals("Hello", token.tokenText());
    }

    @Test
    void shouldGetNewLineToken() {
        String content = "\n";
        lexer = new Lexer(content);

        Token token = lexer.getToken();

        assertEquals(TokenType.NEWLINE, token.tokenType());
        assertEquals("\n", token.tokenText());
    }

    @Test
    void shouldGetEOFToken() {
        String content = "\0";
        lexer = new Lexer(content);

        Token token = lexer.getToken();

        assertEquals(TokenType.EOF, token.tokenType());
        assertEquals("\0", token.tokenText());
    }

    @Test
    void shouldThrowExceptionForUnknownToken() {
        String content = "%";
        lexer = new Lexer(content);

        assertThrows(RuntimeException.class, () -> lexer.getToken());
    }
}