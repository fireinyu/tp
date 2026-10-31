package seedu.address;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/**
 * Contains unit and integration tests for {@code CliTestRunner}.
 */
public class CliTestRunnerTest {

    @TempDir
    public Path temporaryFolder;

    @Test
    public void run_missingArguments_returnsErrorCodeAndPrintsUsage() {
        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        ByteArrayOutputStream errContent = new ByteArrayOutputStream();
        PrintStream out = new PrintStream(outContent);
        PrintStream err = new PrintStream(errContent);

        int exitCode = CliTestRunner.run(new String[0], out, err);

        assertEquals(CliTestRunner.EXIT_CODE_ERROR, exitCode);
        assertTrue(errContent.toString().contains("Usage:"));
    }

    @Test
    public void run_helpOption_returnsSuccessAndPrintsHelp() {
        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        ByteArrayOutputStream errContent = new ByteArrayOutputStream();
        PrintStream out = new PrintStream(outContent);
        PrintStream err = new PrintStream(errContent);

        int exitCode = CliTestRunner.run(new String[] {"--help"}, out, err);

        assertEquals(CliTestRunner.EXIT_CODE_SUCCESS, exitCode);
        assertTrue(outContent.toString().contains("PressMemo Headless CLI Testing Tool"));
    }

    @Test
    public void run_unknownOption_returnsErrorCodeAndPrintsError() {
        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        ByteArrayOutputStream errContent = new ByteArrayOutputStream();
        PrintStream out = new PrintStream(outContent);
        PrintStream err = new PrintStream(errContent);

        int exitCode = CliTestRunner.run(new String[] {"--invalid-flag", "in.txt", "out.txt"}, out, err);

        assertEquals(CliTestRunner.EXIT_CODE_ERROR, exitCode);
        assertTrue(errContent.toString().contains("Unknown option: --invalid-flag"));
    }

    @Test
    public void run_nonExistentInputFile_returnsErrorCode() {
        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        ByteArrayOutputStream errContent = new ByteArrayOutputStream();
        PrintStream out = new PrintStream(outContent);
        PrintStream err = new PrintStream(errContent);

        Path nonExistentInput = temporaryFolder.resolve("does-not-exist.txt");
        Path output = temporaryFolder.resolve("output.txt");

        int exitCode = CliTestRunner.run(new String[] {
            nonExistentInput.toString(), output.toString()
        }, out, err);

        assertEquals(CliTestRunner.EXIT_CODE_ERROR, exitCode);
        assertTrue(errContent.toString().contains("Input file does not exist or cannot be read"));
    }

    @Test
    public void run_validCommandsFile_executesCommandsAndWritesDetailedOutput() throws IOException {
        Path input = temporaryFolder.resolve("commands.txt");
        Path output = temporaryFolder.resolve("results.txt");
        Path data = temporaryFolder.resolve("addressbook.json");
        Path prefs = temporaryFolder.resolve("preferences.json");

        List<String> commandLines = List.of(
                "# Initial setup commands",
                "",
                "help",
                "list",
                "",
                "# Invalid command test",
                "invalidcommandwordxyz",
                "deletecontact 9999"
        );
        Files.write(input, commandLines, StandardCharsets.UTF_8);

        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        ByteArrayOutputStream errContent = new ByteArrayOutputStream();
        PrintStream out = new PrintStream(outContent);
        PrintStream err = new PrintStream(errContent);

        int exitCode = CliTestRunner.run(new String[] {
            "--data", data.toString(),
            "--prefs", prefs.toString(),
            input.toString(),
            output.toString()
        }, out, err);

        assertEquals(CliTestRunner.EXIT_CODE_SUCCESS, exitCode);
        assertTrue(Files.exists(output));

        String outputContent = Files.readString(output, StandardCharsets.UTF_8);
        assertTrue(outputContent.contains("# Initial setup commands"));
        assertTrue(outputContent.contains("[SUCCESS] help"));
        assertTrue(outputContent.contains("[SUCCESS] list"));
        assertTrue(outputContent.contains("[ERROR] invalidcommandwordxyz"));
        assertTrue(outputContent.contains("Unknown command."));
        assertTrue(outputContent.contains("[ERROR] deletecontact 9999"));
        assertTrue(outputContent.contains("The contact index provided is invalid."));
        assertTrue(outputContent.contains("Contacts Panel:"));
        assertTrue(outputContent.contains("Stories Panel:"));
        assertTrue(outputContent.contains("Followups Panel:"));
        assertTrue(outputContent.contains("Alex Yeoh"));

        assertTrue(outContent.toString().contains("Total commands executed: 4"));
        assertTrue(outContent.toString().contains("Succeeded: 2"));
        assertTrue(outContent.toString().contains("Failed   : 2"));
    }

    @Test
    public void run_clearCommand_displaysEmptyPanels() throws IOException {
        Path input = temporaryFolder.resolve("commands.txt");
        Path output = temporaryFolder.resolve("results.txt");
        Path data = temporaryFolder.resolve("addressbook.json");
        Path prefs = temporaryFolder.resolve("preferences.json");

        List<String> commandLines = List.of("clear");
        Files.write(input, commandLines, StandardCharsets.UTF_8);

        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        ByteArrayOutputStream errContent = new ByteArrayOutputStream();
        PrintStream out = new PrintStream(outContent);
        PrintStream err = new PrintStream(errContent);

        int exitCode = CliTestRunner.run(new String[] {
            "--data", data.toString(),
            "--prefs", prefs.toString(),
            input.toString(),
            output.toString()
        }, out, err);

        assertEquals(CliTestRunner.EXIT_CODE_SUCCESS, exitCode);
        String outputContent = Files.readString(output, StandardCharsets.UTF_8);
        assertTrue(outputContent.contains("[SUCCESS] clear"));
        String lineSep = System.lineSeparator();
        assertTrue(outputContent.contains("Contacts Panel:" + lineSep + "(empty)"));
        assertTrue(outputContent.contains("Stories Panel:" + lineSep + "(empty)"));
        assertTrue(outputContent.contains("Followups Panel:" + lineSep + "(empty)"));
    }

    @Test
    public void run_stopOnExit_stopsExecutingSubsequentCommands() throws IOException {
        Path input = temporaryFolder.resolve("commands.txt");
        Path output = temporaryFolder.resolve("results.txt");
        Path data = temporaryFolder.resolve("addressbook.json");
        Path prefs = temporaryFolder.resolve("preferences.json");

        List<String> commandLines = List.of(
                "help",
                "exit",
                "list"
        );
        Files.write(input, commandLines, StandardCharsets.UTF_8);

        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        ByteArrayOutputStream errContent = new ByteArrayOutputStream();
        PrintStream out = new PrintStream(outContent);
        PrintStream err = new PrintStream(errContent);

        int exitCode = CliTestRunner.run(new String[] {
            "--stop-on-exit",
            "--data", data.toString(),
            "--prefs", prefs.toString(),
            input.toString(),
            output.toString()
        }, out, err);

        assertEquals(CliTestRunner.EXIT_CODE_SUCCESS, exitCode);
        String outputContent = Files.readString(output, StandardCharsets.UTF_8);
        assertTrue(outputContent.contains("[SUCCESS] help"));
        assertTrue(outputContent.contains("[SUCCESS] exit"));
        assertFalse(outputContent.contains("[SUCCESS] list"));
    }

    @Test
    public void run_failOnCommandError_returnsCommandFailureCode() throws IOException {
        Path input = temporaryFolder.resolve("commands.txt");
        Path output = temporaryFolder.resolve("results.txt");
        Path data = temporaryFolder.resolve("addressbook.json");
        Path prefs = temporaryFolder.resolve("preferences.json");

        List<String> commandLines = List.of("invalidcommandwordxyz");
        Files.write(input, commandLines, StandardCharsets.UTF_8);

        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        ByteArrayOutputStream errContent = new ByteArrayOutputStream();
        PrintStream out = new PrintStream(outContent);
        PrintStream err = new PrintStream(errContent);

        int exitCode = CliTestRunner.run(new String[] {
            "--fail-on-command-error",
            "--data", data.toString(),
            "--prefs", prefs.toString(),
            input.toString(),
            output.toString()
        }, out, err);

        assertEquals(CliTestRunner.EXIT_CODE_COMMAND_FAILURE, exitCode);
    }
}
