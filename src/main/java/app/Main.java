package app;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Comparator;
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
    private static boolean isFileNotFound = false;
    private static final ArrayList<File> files = new ArrayList<>();
    private static final ArrayList<String> floatArray = new ArrayList<>();
    private static final ArrayList<String> intArray = new ArrayList<>();
    private static final ArrayList<String> strArray = new ArrayList<>();

    public static void main(String[] args) {
        parseArgs(args);

        if (files.isEmpty()) {
            System.out.println("Не было передано ни одного файла в формате *.txt");
            System.exit(0);
        }

        for (File file : files) {
            try (BufferedReader reader = new BufferedReader(new FileReader(file, StandardCharsets.UTF_8))) {
                while (reader.ready()) {
                    parseLine(reader.readLine());
                }
            } catch (FileNotFoundException e) {
                isFileNotFound = true;
            } catch (IOException e) {
                System.out.println("Ошибка чтения файла");
            }
        }

        if (isFileNotFound) {
            System.out.println("Один или несколько файлов не удалось найти");
        }
        if (floatArray.isEmpty() & intArray.isEmpty() & strArray.isEmpty()) {
            System.out.println("В файле/файлах нет подходящих строк");
            System.exit(0);
        }

        if (isAnyLineSkipped) {
            System.out.println("Одна или несколько строк были пропущены из-за несоответствия ни одной категории");
        }

        String pathToSave = CURRENT_DIRECTORY + customPath;
        try {
            new File(pathToSave).mkdirs();
        } catch (Exception e) {
            System.out.println("Результат будет сохранён в текущей директории, так как создать новую не удалось");
            pathToSave = CURRENT_DIRECTORY;
        }

        System.out.println();

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

        if (fullInfo) {
            printFullInfo();
        } else if (shortInfo) {
            printSortInfo();
        }
    }


    private static void parseArgs(String[] args) {
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
                    try {
                        customPath = validateAndTransformPath(args[i]);
                    } catch (IllegalArgumentException ignored) {}
                    break;
                case "-p":
                    i++;
                    prefix = args[i];
                    break;
                default:
                    if (args[i].endsWith(".txt")) {
                        files.add(new File(CURRENT_DIRECTORY + "\\" + args[i]));
                    }
            }
        }
    }

    private static String validateAndTransformPath(String path) throws IllegalArgumentException {
        Pattern pattern = Pattern.compile("(([\\\\/])[^\"\\\\/:|<>*?\\n]+)+");
        Matcher matcher = pattern.matcher(path);
        if (matcher.matches()) {
            return path.replace('/', '\\');
        } else {
            throw new IllegalArgumentException();
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

    private static void printSortInfo() {
        if (!floatArray.isEmpty()) {
            printShortFloatInfo();
        }
        if (!intArray.isEmpty()) {
            printShortIntInfo();
        }
        if (!strArray.isEmpty()) {
            printShortStrInfo();
        }
    }

    private static void printFullInfo() {
        if (!floatArray.isEmpty()) {
            ArrayList<Float> actualFloats = new ArrayList<>();
            floatArray.forEach(n -> actualFloats.add(Float.parseFloat(n)));

            float floatMax = actualFloats.stream().max(Comparator.naturalOrder()).get();
            float floatMin = actualFloats.stream().min(Comparator.naturalOrder()).get();
            double floatAvg = actualFloats.stream().mapToDouble(Float::doubleValue).average().getAsDouble();
            float floatSum = actualFloats.stream().reduce(0f, Float::sum);

            printShortFloatInfo();
            System.out.println("Максимальное число: " + floatMax);
            System.out.println("Минимальное число: " + floatMin);
            System.out.println("Среднее значение: " + floatAvg);
            System.out.println("Сумма чисел: " + floatSum);
            System.out.println();
        }
        if (!intArray.isEmpty()) {
            ArrayList<Long> actualIntegers = new ArrayList<>();
            intArray.forEach(n -> actualIntegers.add(Long.parseLong(n)));

            long intMax = actualIntegers.stream().max(Comparator.naturalOrder()).get();
            long intMin = actualIntegers.stream().min(Comparator.naturalOrder()).get();
            double intAvg = actualIntegers.stream().mapToDouble(Long::doubleValue).average().getAsDouble();
            long intSum = actualIntegers.stream().reduce(0L, Long::sum);

            printShortIntInfo();
            System.out.println("Максимальное число: " + intMax);
            System.out.println("Минимальное число: " + intMin);
            System.out.println("Среднее значение: " + intAvg);
            System.out.println("Сумма чисел: " + intSum);
            System.out.println();
        }
        if (!strArray.isEmpty()) {
            Comparator<String> comparator = Comparator.comparingInt(String::length);
            String longestStr = strArray.stream().max(comparator).get();
            String shortestStr = strArray.stream().min(comparator).get();

            printShortStrInfo();
            System.out.println("Самая длинная строка: " + longestStr);
            System.out.println("Самая короткая строка: " + shortestStr);
        }
    }

    private static void printShortStrInfo() {
        System.out.printf("%s было сохранено в %s\n",
                fixNoun(strArray.size(), "строка", "строки", "строк"),
                prefix + "strings.txt");
    }

    private static void printShortIntInfo() {
        System.out.printf("%s было сохранено в %s\n",
                fixNoun(intArray.size(), "целое число", "целых числа", "целых чисел"),
                prefix + "integers.txt");
    }

    private static void printShortFloatInfo() {
        System.out.printf("%s с плавающей запятой было сохранено в %s\n",
                fixNoun(floatArray.size(), "число", "числа", "чисел"),
                prefix + "floats.txt");
    }

    private static String fixNoun(int quantity, String formFor1, String formFor2, String formFor5) {
        String result;
        int num100 = quantity % 100;
        if(num100 > 4 && num100 < 21) result = formFor5;
        else {
            int num10 = num100 % 10;
            if (num10 == 1) result = formFor1;
            else if (num10 > 1 && num10 < 5) result = formFor2;
            else result = formFor5;
        }
        return quantity + " " + result;
    }
}

/*
Передаваемые файлы должны быть в фомате *.txt и использовать кодировку UTF-8
Путь к новой папке должен быть без пробелов
Целые числа и числа с плавающей запятой не должны выходить за диапазон значений Long и Float соответственно
 */

