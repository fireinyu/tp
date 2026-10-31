package seedu.address;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.logging.ConsoleHandler;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;

import seedu.address.commons.core.LogsCenter;
import seedu.address.commons.exceptions.DataLoadingException;
import seedu.address.commons.util.StringUtil;
import seedu.address.logic.Logic;
import seedu.address.logic.LogicManager;
import seedu.address.logic.commands.CommandResult;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.AddressBook;
import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.model.ReadOnlyAddressBook;
import seedu.address.model.ReadOnlyUserPrefs;
import seedu.address.model.UserPrefs;
import seedu.address.model.util.SampleDataUtil;
import seedu.address.storage.JsonAddressBookStorage;
import seedu.address.storage.JsonUserPrefsStorage;
import seedu.address.storage.Storage;
import seedu.address.storage.StorageManager;

/**
 * Headless command-line testing tool for PressMemo.
 *
 * Reads a list of commands from a text file, executes each command against the application logic,
 * and writes the formatted results (status and feedback) to an output text file.
 */
public class CliTestRunner {

    public static final String DEFAULT_ADDRESS_BOOK_FILE = "data/addressbook.json";
    public static final String DEFAULT_USER_PREFS_FILE = "preferences.json";

    public static final int EXIT_CODE_SUCCESS = 0;
    public static final int EXIT_CODE_ERROR = 1;
    public static final int EXIT_CODE_COMMAND_FAILURE = 2;

    private static final Logger logger = LogsCenter.getLogger(CliTestRunner.class);

    /**
     * Main entry point for standalone CLI test executions.
     *
     * @param args Command-line arguments.
     */
    public static void main(String[] args) {
        int exitCode = run(args, System.out, System.err);
        if (exitCode != 0) {
            System.exit(exitCode);
        }
    }

    /**
     * Runs the CLI test suite with the specified arguments using standard streams.
     *
     * @param args Command-line arguments.
     * @return Exit code (0 for success, non-zero for error).
     */
    public static int run(String[] args) {
        return run(args, System.out, System.err);
    }

    /**
     * Runs the CLI test suite with the specified arguments and output streams.
     *
     * @param args Command-line arguments.
     * @param out Output stream for normal messages.
     * @param err Output stream for error messages.
     * @return Exit code (0 for success, non-zero for error).
     */
    public static int run(String[] args, PrintStream out, PrintStream err) {
        Path dataPath = Paths.get(DEFAULT_ADDRESS_BOOK_FILE);
        Path prefsPath = Paths.get(DEFAULT_USER_PREFS_FILE);
        boolean stopOnExit = false;
        boolean failOnCommandError = false;
        List<String> positionalArgs = new ArrayList<>();

        for (int i = 0; i < args.length; i++) {
            String arg = args[i];
            if (arg.equals("-h") || arg.equalsIgnoreCase("--help")) {
                printHelp(out);
                return EXIT_CODE_SUCCESS;
            } else if (arg.equalsIgnoreCase("--stop-on-exit")) {
                stopOnExit = true;
            } else if (arg.equalsIgnoreCase("--fail-on-command-error")) {
                failOnCommandError = true;
            } else if (arg.equalsIgnoreCase("--data") && i + 1 < args.length) {
                dataPath = Paths.get(args[++i]);
            } else if (arg.equalsIgnoreCase("--prefs") && i + 1 < args.length) {
                prefsPath = Paths.get(args[++i]);
            } else if (arg.startsWith("-")) {
                err.println("Unknown option: " + arg);
                printUsage(err);
                return EXIT_CODE_ERROR;
            } else {
                positionalArgs.add(arg);
            }
        }

        if (positionalArgs.size() != 2) {
            printUsage(err);
            return EXIT_CODE_ERROR;
        }

        Path inputPath = Paths.get(positionalArgs.get(0));
        Path outputPath = Paths.get(positionalArgs.get(1));

        if (!Files.exists(inputPath) || !Files.isReadable(inputPath)) {
            err.println("Error: Input file does not exist or cannot be read: " + inputPath);
            return EXIT_CODE_ERROR;
        }

        List<String> rawLines;
        try {
            rawLines = Files.readAllLines(inputPath, StandardCharsets.UTF_8);
        } catch (IOException e) {
            err.println("Error reading input file: " + e.getMessage());
            return EXIT_CODE_ERROR;
        }

        int totalCommands = 0;
        for (String rawLine : rawLines) {
            String trimmed = rawLine.trim();
            if (!trimmed.isEmpty() && !trimmed.startsWith("#") && !trimmed.startsWith("//")) {
                totalCommands++;
            }
        }

        silenceConsoleLogger();

        Storage storage;
        Model model;
        Logic logic;
        try {
            JsonUserPrefsStorage userPrefsStorage = new JsonUserPrefsStorage(prefsPath);
            UserPrefs userPrefs = initPrefs(userPrefsStorage);
            JsonAddressBookStorage addressBookStorage = new JsonAddressBookStorage(dataPath);
            storage = new StorageManager(addressBookStorage, userPrefsStorage);
            model = initModelManager(storage, userPrefs);
            logic = new LogicManager(model, storage);
        } catch (Exception e) {
            err.println("Error initializing application logic: " + e.getMessage());
            return EXIT_CODE_ERROR;
        }

        if (outputPath.getParent() != null) {
            try {
                Files.createDirectories(outputPath.getParent());
            } catch (IOException e) {
                err.println("Error creating parent directories for output file: " + e.getMessage());
                return EXIT_CODE_ERROR;
            }
        }

        out.println("Executing CLI tests...");
        out.println("  Input file : " + inputPath.toAbsolutePath());
        out.println("  Output file: " + outputPath.toAbsolutePath());
        out.println("  Data file  : " + dataPath.toAbsolutePath());
        out.println("  Preferences: " + prefsPath.toAbsolutePath());
        out.printf("  Total test commands: %d%n%n", totalCommands);

        int executedCount = 0;
        int successCount = 0;
        int errorCount = 0;

        try (BufferedWriter writer = Files.newBufferedWriter(outputPath, StandardCharsets.UTF_8)) {
            for (String rawLine : rawLines) {
                String trimmed = rawLine.trim();

                if (trimmed.isEmpty()) {
                    continue;
                }

                if (trimmed.startsWith("#") || trimmed.startsWith("//")) {
                    writer.write(trimmed);
                    writer.newLine();
                    writer.newLine();
                    continue;
                }

                executedCount++;
                String status;
                String message;
                boolean shouldExit = false;

                try {
                    CommandResult result = logic.execute(trimmed);
                    status = "SUCCESS";
                    message = result.getFeedbackToUser();
                    successCount++;
                    if (result.isExit()) {
                        shouldExit = true;
                    }
                } catch (CommandException | ParseException e) {
                    status = "ERROR";
                    message = e.getMessage();
                    errorCount++;
                } catch (Exception e) {
                    status = "ERROR";
                    message = "Unexpected error: " + e.getMessage();
                    errorCount++;
                }

                out.printf("[%d/%d] [%s] %s%n", executedCount, totalCommands, status, trimmed);

                writer.write(String.format("[%s] %s%n", status, trimmed));
                writer.write(message);
                writer.newLine();
                writer.newLine();

                if (shouldExit && stopOnExit) {
                    out.println("Application exit requested. Halting further command execution.");
                    break;
                }
            }
        } catch (IOException e) {
            err.println("Error writing to output file: " + e.getMessage());
            return EXIT_CODE_ERROR;
        }

        try {
            storage.saveUserPrefs(model.getUserPrefs());
        } catch (IOException e) {
            logger.warning("Failed to save preferences: " + StringUtil.getDetails(e));
        }

        out.println();
        out.println("=== CLI Test Execution Summary ===");
        out.printf("Total commands executed: %d%n", executedCount);
        out.printf("Succeeded: %d%n", successCount);
        out.printf("Failed   : %d%n", errorCount);
        out.printf("Output written to: %s%n", outputPath.toAbsolutePath());

        if (failOnCommandError && errorCount > 0) {
            return EXIT_CODE_COMMAND_FAILURE;
        }

        return EXIT_CODE_SUCCESS;
    }

    /**
     * Suppresses verbose console logging so it does not interfere with CLI output.
     */
    protected static void silenceConsoleLogger() {
        Logger baseLogger = Logger.getLogger("ab3");
        for (Handler handler : baseLogger.getHandlers()) {
            if (handler instanceof ConsoleHandler) {
                handler.setLevel(Level.WARNING);
            }
        }
    }

    /**
     * Initializes the user preferences.
     *
     * @param storage The user prefs storage component.
     * @return The initialized {@code UserPrefs}.
     */
    protected static UserPrefs initPrefs(JsonUserPrefsStorage storage) {
        Path prefsFilePath = storage.getUserPrefsFilePath();
        UserPrefs initializedPrefs;
        try {
            Optional<UserPrefs> prefsOptional = storage.readUserPrefs();
            initializedPrefs = prefsOptional.orElse(new UserPrefs());
        } catch (DataLoadingException e) {
            logger.warning(String.format("Preference file at %s could not be loaded. Using default preferences.",
                    prefsFilePath));
            return new UserPrefs();
        }

        try {
            storage.saveUserPrefs(initializedPrefs);
        } catch (IOException e) {
            logger.warning("Failed to save preference file : " + StringUtil.getDetails(e));
        }

        return initializedPrefs;
    }

    /**
     * Initializes the model manager with data from storage.
     *
     * @param storage The storage component.
     * @param userPrefs The user preferences.
     * @return The initialized {@code Model}.
     */
    protected static Model initModelManager(Storage storage, ReadOnlyUserPrefs userPrefs) {
        Optional<ReadOnlyAddressBook> addressBookOptional;
        ReadOnlyAddressBook initialData;
        try {
            addressBookOptional = storage.readAddressBook();
            initialData = addressBookOptional.orElseGet(SampleDataUtil::getSampleAddressBook);
        } catch (DataLoadingException e) {
            logger.warning(String.format("Data file at %s could not be loaded. Starting with an empty address book.",
                    storage.getAddressBookFilePath()));
            initialData = new AddressBook();
        }

        return new ModelManager(initialData, userPrefs);
    }

    /**
     * Prints the command usage information.
     *
     * @param out Stream to write usage to.
     */
    public static void printUsage(PrintStream out) {
        out.println("Usage: java -jar pressmemo.jar --cli [options] <input-file> <output-file>");
        out.println("   or: ./gradlew runCliTest --args=\"[options] <input-file> <output-file>\"");
        out.println("Run with '--help' to display available options.");
    }

    /**
     * Prints detailed help information.
     *
     * @param out Stream to write help to.
     */
    public static void printHelp(PrintStream out) {
        out.println("PressMemo Headless CLI Testing Tool");
        out.println();
        out.println("Usage:");
        out.println("  java -jar pressmemo.jar --cli [options] <input-file> <output-file>");
        out.println("  ./gradlew runCliTest --args=\"[options] <input-file> <output-file>\"");
        out.println();
        out.println("Arguments:");
        out.println("  <input-file>   Path to the text file containing commands to execute (one per line).");
        out.println("  <output-file>  Path to the text file where command results will be written.");
        out.println();
        out.println("Options:");
        out.println("  --data <path>             Path to custom addressbook JSON data file.");
        out.println("                            Default: " + DEFAULT_ADDRESS_BOOK_FILE);
        out.println("  --prefs <path>            Path to custom preferences JSON file.");
        out.println("                            Default: " + DEFAULT_USER_PREFS_FILE);
        out.println("  --stop-on-exit            Stop executing further commands if 'exit' is encountered.");
        out.println("  --fail-on-command-error   Exit with non-zero status if any command returns an ERROR.");
        out.println("  -h, --help                Display this help message.");
    }
}
