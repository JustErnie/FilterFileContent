package app;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Main {
    private static String prefix = "";
    private static String customPath = "";
    private static final String CURRENT_DIRECTORY = Paths.get("").toAbsolutePath().toString();
    private static boolean fullInfo = false;
    private static boolean shortInfo = false;
    private static boolean addToExistingFile = false;
    private static boolean isAnyLineSkipped = false;
    private static final ArrayList<File> files = new ArrayList<>();
    private static final ArrayList<String> floatArray = new ArrayList<>();
    private static final ArrayList<String> intArray = new ArrayList<>();
    private static final ArrayList<String> strArray = new ArrayList<>();

    public static void main(String[] args) {
        try {
            parseArgs(args);
        }
        catch (IllegalArgumentException e) {
            System.out.println(e.getMessage());
            System.exit(0);
        }

        for (File file : files) {
            try (BufferedReader reader = new BufferedReader(new FileReader(file, StandardCharsets.UTF_8))) {
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

        if (floatArray.isEmpty() & intArray.isEmpty() & strArray.isEmpty()) {
            System.out.println("В файле/файлах нет подходящих строк");
            System.exit(0);
        }

        if (isAnyLineSkipped) {
            System.out.println("Одна или несколько строк были пропущены из-за несоответствия ни одной категории");
        }

        String pathToSave = CURRENT_DIRECTORY + customPath;
        new File(pathToSave).mkdirs();
        try {
            if (addToExistingFile) {
                if (!floatArray.isEmpty()) {
                    Files.write(Paths.get(pathToSave + "\\" + prefix + "floats.txt"), floatArray,
                            StandardOpenOption.WRITE, StandardOpenOption.CREATE, StandardOpenOption.APPEND);
                }
                if(!intArray.isEmpty()) {
                    Files.write(Paths.get(pathToSave + "\\" + prefix + "integers.txt"), intArray,
                            StandardOpenOption.WRITE, StandardOpenOption.CREATE, StandardOpenOption.APPEND);
                }
                if (!strArray.isEmpty()) {
                    Files.write(Paths.get(pathToSave + "\\" + prefix + "strings.txt"), strArray,
                            StandardOpenOption.WRITE, StandardOpenOption.CREATE, StandardOpenOption.APPEND);
                }
            } else {
                if (!floatArray.isEmpty()) {
                    Files.write(Paths.get(pathToSave + "\\" + prefix + "floats.txt"), floatArray);
                }
                if (!intArray.isEmpty()) {
                    Files.write(Paths.get(pathToSave + "\\" + prefix + "integers.txt"), intArray);
                }
                if (!strArray.isEmpty()) {
                    Files.write(Paths.get(pathToSave + "\\" + prefix + "strings.txt"), strArray);
                }
            }
        } catch (IOException e) {
            System.out.println("Ошибка записи файла");
            System.exit(0);
        }

    }

    private static void parseLine(String line) {
        Pattern floatPattern = Pattern.compile("^-?\\d+\\.\\d+(E-?\\d+)?$");
        Pattern intPattern = Pattern.compile("^-?\\d+$");
        Pattern strPattern = Pattern.compile("^[A-Za-zА-яЁё ]+$");

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
                    customPath = validateAndTransformPath(args[i]);
                    break;
                case "-p":
                    i++;
                    prefix = args[i];
                    break;
                default:
                    if (args[i].endsWith(".txt")) {
                        files.add(new File(CURRENT_DIRECTORY + "\\" + args[i]));
                    } else throw new IllegalArgumentException("Неверный формат файла или неверные аргументы");
            }
        }
    }

    private static String validateAndTransformPath(String path) throws IllegalArgumentException {
        Pattern pattern = Pattern.compile("(([\\\\/])[^\"\\\\/:|<>*?\\n]+)+");
        Matcher matcher = pattern.matcher(path);
        if (!matcher.matches()) throw new IllegalArgumentException("Неверный формат пути");
        return path.replace('/', '\\');
    }
}

//java -jar util.jar -s -f -a -o /some/path -p sample- in1.txt in2.txt
//java -jar FilterFileContent.jar -s -a -p sample- in1.txt in2.txt

// *.txt must be in UTF-8

