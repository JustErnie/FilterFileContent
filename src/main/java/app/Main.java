package app;

import java.io.File;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Main {
    private static String prefix = null;
    private static String customPath = null;
    private static boolean fullInfo = false;
    private static boolean shortInfo = false;
    private static boolean addToExistingFile = false;
    private static ArrayList<File> files = new ArrayList<>();

    public static void main(String[] args) {
        try {
            parseArgs(args);
        }
        catch (IllegalArgumentException e) {
            System.out.println(e.getMessage());
            System.exit(0);
        }
        System.out.println("Done");


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