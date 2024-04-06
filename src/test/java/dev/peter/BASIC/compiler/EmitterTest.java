package dev.peter.BASIC.compiler;

import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

class EmitterTest {

    private Emitter emitter;

    @Test
    void shouldEmit() {
        String code = "Hello";
        emitter = new Emitter(null);

        emitter.emit(code);

        try {
            Field codeField = Emitter.class.getDeclaredField("code");

            codeField.setAccessible(true);

            assertEquals("Hello", codeField.get(emitter));
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void shouldEmitLine() {
        String code = "Hello";
        emitter = new Emitter(null);

        emitter.emitLine(code);

        try {
            Field codeField = Emitter.class.getDeclaredField("code");

            codeField.setAccessible(true);

            assertEquals("Hello\n", codeField.get(emitter));
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void shouldAddHeaderLine() {
        String code = "Header";
        emitter = new Emitter(null);

        emitter.headerLine(code);

        try {
            Field headerField = Emitter.class.getDeclaredField("header");

            headerField.setAccessible(true);

            assertEquals("Header\n", headerField.get(emitter));
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void shouldWriteToFile() {
        String code = "Hello";
        String filePath = "test.c";
        emitter = new Emitter(filePath);

        emitter.emit(code);

        emitter.writeFile();

        File file = new File(filePath);

        assertTrue(file.exists());
        try {
            assertEquals("Hello", Files.readString(Path.of(filePath)));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void shouldThrowExceptionWhenWriteToFile() {
        String code = "Hello";
        String filePath = null;
        emitter = new Emitter(filePath);

        emitter.emit(code);

        assertThrows(RuntimeException.class, () -> emitter.writeFile());
    }
}