package dev.peter.BASIC.compiler;

import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;

import static org.junit.jupiter.api.Assertions.*;

class ParserTest {

    private Parser parser;
    private Lexer lexer;
    private Emitter emitter;

    @Test
    void shouldCorrectlyCheckMatchingToken() {
        String content = "PRINT \"Hello\"";
        lexer = new Lexer(content);
        parser = new Parser(lexer, emitter);

        assertTrue(parser.checkToken(TokenType.PRINT));
    }

    @Test
    void shouldCorrectlyCheckMismatchingToken() {
        String content = "STRING";
        lexer = new Lexer(content);
        parser = new Parser(lexer, emitter);

        assertFalse(parser.checkToken(TokenType.PRINT));
    }

    @Test
    void shouldCheckMatchingPeek() {
        String content = "PRINT \"Hello\"";
        lexer = new Lexer(content);
        parser = new Parser(lexer, emitter);

        assertTrue(parser.checkPeek(TokenType.STRING));
    }

    @Test
    void shouldCheckMismatchingPeek() {
        String content = "PRINT \"Hello\"";
        lexer = new Lexer(content);
        parser = new Parser(lexer, emitter);

        assertFalse(parser.checkPeek(TokenType.NUMBER));
    }

    @Test
    void shouldMatch() throws Exception {
        String content = "PRINT \"Hello\"";
        lexer = new Lexer(content);
        parser = new Parser(lexer, emitter);

        parser.match(TokenType.PRINT);

        Field curTokenField = Parser.class.getDeclaredField("curToken");
        curTokenField.setAccessible(true);

        Token matched = (Token) curTokenField.get(parser);

        assertEquals(TokenType.STRING, matched.tokenType());
        assertEquals("Hello", matched.tokenText());
    }

    @Test
    void shouldThrowExceptionForMismatch() throws Exception {
        String content = "PRINT \"Hello\"";
        lexer = new Lexer(content);
        parser = new Parser(lexer, emitter);

        assertThrows(RuntimeException.class, () -> parser.match(TokenType.STRING), "Expected STRING, got PRINT");
    }

    @Test
    void shouldSetNextToken() throws Exception {
        String content = "PRINT \"Hello\"";
        lexer = new Lexer(content);
        parser = new Parser(lexer, emitter);

        parser.nextToken();

        Field curTokenField = Parser.class.getDeclaredField("curToken");
        Field peekTokenField = Parser.class.getDeclaredField("peekToken");
        curTokenField.setAccessible(true);
        peekTokenField.setAccessible(true);

        Token next = (Token) curTokenField.get(parser);
        Token peek = (Token) peekTokenField.get(parser);

        assertEquals(TokenType.STRING, next.tokenType());
        assertEquals("Hello", next.tokenText());
        assertEquals(TokenType.NEWLINE, peek.tokenType());
        assertEquals("\n", peek.tokenText());
    }

    @Test
    void shouldCorrectlyProgram() throws Exception {
        String content = "PRINT \"Hello\"";
        lexer = new Lexer(content);
        emitter = new Emitter(null);
        parser = new Parser(lexer, emitter);

        parser.program();

        Field codeField = Emitter.class.getDeclaredField("code");
        Field headerField = Emitter.class.getDeclaredField("header");
        codeField.setAccessible(true);
        headerField.setAccessible(true);

        assertEquals("""
                printf("Hello\\n");
                system ( "PAUSE" );
                return 0;
                }
                """, codeField.get(emitter));
        assertEquals("""
                #include <stdio.h>
                #include <stdlib.h>
                int main(void) {
                """, headerField.get(emitter));


    }

    @Test
    void shouldThrowExceptionForInvalidGOTO() {
        String content = "GOTO a";
        lexer = new Lexer(content);
        emitter = new Emitter(null);
        parser = new Parser(lexer, emitter);

        assertThrows(RuntimeException.class, () -> parser.program(), "Attemtping to GOTO to undeclared label: a");
    }

    @Test
    void shouldThrowExceptionForInvalidLabel() {
        String content = "LABEL a\nLABEL a";
        lexer = new Lexer(content);
        emitter = new Emitter(null);
        parser = new Parser(lexer, emitter);

        assertThrows(RuntimeException.class, () -> parser.program(), "Label already exists: a");
    }

    @Test
    void shouldThrowExceptionForInvalidStatement() {
        String content = "\"Hello\"";
        lexer = new Lexer(content);
        emitter = new Emitter(null);
        parser = new Parser(lexer, emitter);

        assertThrows(RuntimeException.class, () -> parser.program(), "Invalid statement at Hello (STRING)");
    }

    @Test
    void shouldThrowExceptionForInvalidComparison() {
        String content = "IF 5 >< 3";
        lexer = new Lexer(content);
        emitter = new Emitter(null);
        parser = new Parser(lexer, emitter);

        assertThrows(RuntimeException.class, () -> parser.program(), "Expected comparison operator at: +");
    }

    @Test
    void shouldThrowExceptionForInvalidVariableReference() {
        String content = "IF a > 5";
        lexer = new Lexer(content);
        emitter = new Emitter(null);
        parser = new Parser(lexer, emitter);

        assertThrows(RuntimeException.class, () -> parser.program(), "Referencing variable before assignment: a");
    }

    @Test
    void shouldThrowExceptionForUnexpectedToken() {
        String content = "PIRNT \"Hello\"";
        lexer = new Lexer(content);
        emitter = new Emitter(null);
        parser = new Parser(lexer, emitter);

        assertThrows(RuntimeException.class, () -> parser.program(), "Unexpected token at PIRNT");
    }
}