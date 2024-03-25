package dev.peter.BASIC.compiler;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class Emitter {

    private String filePath;

    private String header;

    private String code;

    public Emitter(String filePath) {
        this.filePath = filePath;
        this.header = "";
        this.code = "";
    }

    public void emit(String code) {
        this.code += code;
    }

    public void emitLine(String code) {
        this.code += code + '\n';
    }

    public void headerLine(String code) {
        this.header = code + '\n';
    }

    public void writeFile() {
        Path filePath = Path.of(this.filePath);
        try {
            Files.writeString(filePath, this.header + this.code);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
