package tw.cn.cap.gtb.todo;

import com.google.common.base.Joiner;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Arrays;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class App {
    public String getGreeting() {
        return "Hello World!";
    }

    public static void main(String[] args) throws IOException {
        if (args.length == 0) {
            System.out.println("usage: todo <init|list|add|edit|remove|mark|unmark|reset> [args]");
            return;
        }

        final var homePath = System.getProperty("user.home");
        final var filePath = Path.of(homePath + File.separator + ".todo");
        final var fileNameWithPath = filePath + File.separator + "tasks";

        if (!Files.exists(filePath) && !"init".equals(args[0])) {
            System.out.println(String.format("Please run 'init' before running '%s' command.", args[0]));
            return;
        }

        if ("init".equals(args[0])) {
            if (!Files.exists(filePath)) {
                Files.createDirectory(filePath);
            }
            final var file = new File(fileNameWithPath);
            if (!file.exists()) {
                file.createNewFile();
            }
            System.out.println("Initialized successfully.");
            return;
        }

        long lineCount = 0;
        Path fileName = Path.of(fileNameWithPath);
        try {
            lineCount = Files.lines(fileName).count();
        } catch (IOException e) {
            e.printStackTrace();
        }

        if ("add".equals(args[0])) {
            final var taskName = Joiner.on(" ").join(Arrays.stream(args).skip(1).toArray(String[]::new));
            Files.write(fileName, String.format("- %d ", (lineCount + 1)).concat(taskName.concat(System.lineSeparator())).getBytes(), StandardOpenOption.APPEND);
        }

        if ("list".equals(args[0])) {
            try {
                System.out.println("# To be done");
                String[] toBeDoneList = Files.lines(fileName)
                        .filter(line -> line.startsWith("-"))
                        .map(line -> line.replaceAll("- ", ""))
                        .toArray(String[]::new);

                if (toBeDoneList.length != 0) {
                    Arrays.stream(toBeDoneList).forEach(System.out::println);
                } else {
                    System.out.println("Empty");
                }

                System.out.println("# Completed");
                String[] completedList = Files.lines(fileName)
                        .filter(line -> line.startsWith("x"))
                        .map(line -> line.replaceAll("x ", ""))
                        .toArray(String[]::new);

                if (completedList.length != 0) {
                    Arrays.stream(completedList).forEach(System.out::println);
                } else {
                    System.out.println("Empty");
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        if ("remark".equals(args[0])) {
            final var taskId = Arrays.stream(args).skip(1).toArray(String[]::new);

            try (Stream<String> lines = Files.lines(fileName);
                 Stream<String> modifiedLines = lines.map(line -> {
                     if (Arrays.stream(taskId)
                             .anyMatch(line::contains) && line.startsWith("-")) {
                         return line.replace("-", "x");
                     }
                     return line;
                 })) {
                Files.write(fileName, modifiedLines.collect(Collectors.toList()),
                        StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return;
        }

        if ("remove".equals(args[0])) {
            final var taskId = Arrays.stream(args).skip(1).toArray(String[]::new);
            try (Stream<String> lines = Files.lines(fileName);
                 Stream<String> modifiedLines = lines.map(line -> {
                     String regex = "^(x|-).*"; // 以"x"或"-"开头的字符串的正则表达式
                     if (Arrays.stream(taskId)
                             .anyMatch(line::contains) && line.matches(regex)) {
                         return line.replaceAll(regex, "*");
                     }
                     return line;
                 })) {
                Files.write(fileName, modifiedLines.collect(Collectors.toList()),
                        StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return;
        }
    }
}
