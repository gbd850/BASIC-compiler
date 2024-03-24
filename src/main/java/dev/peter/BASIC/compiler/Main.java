package dev.peter.BASIC.compiler;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class Main {
    public static void main(String[] args) {

        if (args.length == 0) {
            throw new RuntimeException("Error: Compile needs source file as argument");
        }
        Path filePath = Path.of(args[0]);
        String content;
        try {
            content = Files.readString(filePath);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        Lexer lexer = new Lexer(content);
        Parser parser = new Parser(lexer);

        parser.program();
        System.out.println("Parsing completed");
    }
}
