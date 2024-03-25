package dev.peter.BASIC.compiler;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class Main {
    public static void main(String[] args) {

        if (args.length < 2) {
            throw new RuntimeException("Error: Missing source file to compile and/or name of the output file");
        }
        Path filePath = Path.of(args[0]);
        String content;
        try {
            content = Files.readString(filePath);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        Lexer lexer = new Lexer(content);
        Emitter emitter = new Emitter(args[1]);
        Parser parser = new Parser(lexer, emitter);

        parser.program();
        emitter.writeFile();
        System.out.println("Compiling completed");

        try {
            Thread.sleep(200);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

//        Compiling C code and running the program
        Runtime sys = Runtime.getRuntime();
        try {
            String commandGcc = "gcc " + args[1] + " -o output.exe";
            Process process = sys.exec(commandGcc);
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            Thread.sleep(200);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        try {
            String commandRun = "cmd.exe /c start .\\output.exe";
            Process process = sys.exec(commandRun, null, new File("."));
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
