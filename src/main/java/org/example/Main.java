package org.example;

import org.example.task2.PropertiesFile;

import java.io.FileNotFoundException;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.example.task1.InputOutputFile.readAndParse;
import static org.example.task2.PropertiesFile.loadFromProperties;


public class Main {
    public static void main(String[] args) throws FileNotFoundException{

        long time = System.currentTimeMillis();
        readAndParse();
        System.out.println(System.currentTimeMillis() - time);

        Path testFilePath = Paths.get("src/main/resources/config.properties");
        PropertiesFile.Class cls = new  PropertiesFile.Class<>();
        loadFromProperties(  cls, testFilePath);

    }
}