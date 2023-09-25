package tw.cn.cap.gtb.todo;

import com.google.common.base.Joiner;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Arrays;

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

        if ("add".equals(args[0])) {
            final var taskName = Joiner.on(" ").join(Arrays.stream(args).skip(1).toArray(String[]::new));
            Files.write(Paths.get(fileNameWithPath), taskName.concat("\n").getBytes(), StandardOpenOption.APPEND);
        }
    }
}
