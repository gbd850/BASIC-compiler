package dev.peter.BASIC.compiler;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TokenTest {

    @Test
    void shouldReturnCheckedKeyword() {
        String tokenType = "PRINT";

        assertEquals(TokenType.PRINT, Token.checkIfKeyword(tokenType));
    }

    @Test
    void shouldReturnNullForNotAKeyword() {
        String tokenType = "STRING";

        assertNull(Token.checkIfKeyword(tokenType));
    }
}