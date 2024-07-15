package app;

import java.io.*;
import java.net.URL;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Main {
    private static String prefix = null;
    private static String customPath = null;
    private static boolean fullInfo = false;
    private static boolean shortInfo = false;
    private static boolean isAnyLineSkipped = false;
    private static boolean addToExistingFile = false;
    private static ArrayList<File> files = new ArrayList<>();
    private static ArrayList<String> floatArray = new ArrayList<>();
    private static ArrayList<String> intArray = new ArrayList<>();
    private static ArrayList<String> strArray = new ArrayList<>();

    public static void main(String[] args) {
        try {
            parseArgs(args);
        }
        catch (IllegalArgumentException e) {
            System.out.println(e.getMessage());
            System.exit(0);
        }

        for (File file : files) {
            try (InputStream inputStream = Main.class.getResourceAsStream("/" + file.toString());
                 BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, "Cp1251"))) {
                while (reader.ready()) {
                    parseLine(reader.readLine());
                }
            } catch (FileNotFoundException e) {
                System.out.println("Файл не найден");
                System.exit(0);
            } catch (IOException e) {
                System.out.println("Ошибка чтения файла");
                System.exit(0);
            }
        }

        if (isAnyLineSkipped) {
            System.out.println("Одна или несколько строк были пропущены из-за несоответствия ни одной категории");
        }

        System.out.println(floatArray);
        System.out.println(intArray);
        System.out.println(strArray);


    }

    private static void parseLine(String line) {
        Pattern floatPattern = Pattern.compile("^-?\\d+\\.\\d+(E-?\\d+)?$");
        Pattern intPattern = Pattern.compile("^-?\\d+$");
        Pattern strPattern = Pattern.compile("^[A-Za-zА-я ]+$");

        Matcher floatMatcher = floatPattern.matcher(line);
        Matcher intMatcher = intPattern.matcher(line);
        Matcher strMatcher = strPattern.matcher(line);

        if (floatMatcher.matches()) {
            floatArray.add(line);
        } else if (intMatcher.matches()) {
            intArray.add(line);
        } else if (strMatcher.matches()) {
            strArray.add(line);
        } else isAnyLineSkipped = true;
    }

    private static void parseArgs(String[] args) throws IllegalArgumentException {
        for (int i = 0; i < args.length; i++) {
            switch (args[i]) {
                case "-s":
                    shortInfo = true;
                    break;
                case "-f":
                    fullInfo = true;
                    break;
                case "-a":
                    addToExistingFile = true;
                    break;
                case "-o":
                    i++;
                    validatePath(args[i]);
                    customPath = args[i];
                    break;
                case "-p":
                    i++;
                    prefix = args[i];
                    break;
                default:
                    if (args[i].endsWith(".txt")) {
                        files.add(new File(args[i]));
                    } else throw new IllegalArgumentException("Неверный формат файла или неверные аргументы");
            }
        }
    }

    private static void validatePath(String path) throws IllegalArgumentException {
        Pattern pattern = Pattern.compile("(/\\w+)+");
        Matcher matcher = pattern.matcher(path);
        if (!matcher.matches()) throw new IllegalArgumentException("Неверный формат пути");
    }
}

//java -jar util.jar -s -f -a -o /some/path -p sample- in1.txt in2.txt
//java -jar FilterFileContent.jar -s -a -p sample- in1.txt in2.txt