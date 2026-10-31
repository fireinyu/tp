package seedu.address.logic.parser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static seedu.address.logic.Messages.MESSAGE_CONTACTS_LISTED_OVERVIEW;
import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseFailure;

import java.util.List;

import org.junit.jupiter.api.Test;

import seedu.address.logic.commands.Command;
import seedu.address.logic.commands.CommandResult;
import seedu.address.logic.commands.FindContactsCommand;
import seedu.address.model.AddressBook;
import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.model.UserPrefs;
import seedu.address.model.contact.Contact;
import seedu.address.testutil.ContactBuilder;

public class FindContactsCommandParserTest {

    private final FindContactsCommandParser parser = new FindContactsCommandParser();

    private final Contact alice = new ContactBuilder().withName("Alice").build();
    private final Contact bob = new ContactBuilder().withName("Bob").build();
    private final Contact carol = new ContactBuilder().withName("Carol").build();
    private final Contact symbolContact = new ContactBuilder().withName("Alice-Bob Dr. 123").build();
    private final Contact other = new ContactBuilder().withName("David").build();

    private Model createTestModel() {
        AddressBook ab = new AddressBook();
        ab.addContact(alice);
        ab.addContact(bob);
        ab.addContact(carol);
        ab.addContact(symbolContact);
        ab.addContact(other);
        return new ModelManager(ab, new UserPrefs());
    }

    private void assertParseExecutesTo(String userInput, List<Contact> expectedContacts) throws Exception {
        Command command = parser.parse(userInput);
        Model model = createTestModel();
        CommandResult result = command.execute(model);
        assertEquals(String.format(MESSAGE_CONTACTS_LISTED_OVERVIEW, expectedContacts.size()),
                result.getFeedbackToUser());
        assertEquals(expectedContacts, model.getFilteredContactList());
    }

    @Test
    public void parse_emptyArg_throwsParseException() {
        String expectedMessage = String.format(MESSAGE_INVALID_COMMAND_FORMAT,
                FindContactsCommand.MESSAGE_USAGE);
        assertParseFailure(parser, "     ", expectedMessage);
        assertParseFailure(parser, "", expectedMessage);
        assertParseFailure(parser, "\t \n \r", expectedMessage);
    }

    @Test
    public void parse_validArgs_executesExpectedFiltering() throws Exception {
        // Single keyword
        assertParseExecutesTo("Alice", List.of(alice));
        assertParseExecutesTo("  Alice  ", List.of(alice));

        // Multiple keywords without leading and trailing whitespaces
        assertParseExecutesTo("Alice Bob Carol", List.of(alice, bob, carol));

        // Multiple whitespaces between keywords (tabs, newlines)
        assertParseExecutesTo(" \n Alice \n \t Bob \r Carol \t", List.of(alice, bob, carol));

        // Keywords with symbols or numbers
        assertParseExecutesTo("Alice-Bob Dr. 123", List.of(symbolContact));
    }

}
