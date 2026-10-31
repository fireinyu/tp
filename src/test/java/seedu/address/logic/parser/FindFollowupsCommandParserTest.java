package seedu.address.logic.parser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static seedu.address.logic.Messages.MESSAGE_FOLLOWUPS_LISTED_OVERVIEW;
import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseFailure;

import java.util.List;

import org.junit.jupiter.api.Test;

import seedu.address.logic.commands.Command;
import seedu.address.logic.commands.CommandResult;
import seedu.address.logic.commands.FindFollowupsCommand;
import seedu.address.model.AddressBook;
import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.model.UserPrefs;
import seedu.address.model.contact.Name;
import seedu.address.model.followup.Followup;
import seedu.address.model.followup.FollowupDescription;

public class FindFollowupsCommandParserTest {

    private final FindFollowupsCommandParser parser =
            new FindFollowupsCommandParser();

    private final Name author = new Name("Alice");
    private final Followup interviewFollowup =
            new Followup(new FollowupDescription("interview"), author);
    private final Followup meetingFollowup =
            new Followup(new FollowupDescription("meeting"), author);
    private final Followup callFollowup =
            new Followup(new FollowupDescription("call"), author);
    private final Followup symbolFollowup =
            new Followup(new FollowupDescription("10am urgent! hospital#2"), author);
    private final Followup otherFollowup =
            new Followup(new FollowupDescription("unrelated"), author);

    private Model createTestModel() {
        AddressBook ab = new AddressBook();
        ab.addFollowup(interviewFollowup);
        ab.addFollowup(meetingFollowup);
        ab.addFollowup(callFollowup);
        ab.addFollowup(symbolFollowup);
        ab.addFollowup(otherFollowup);
        return new ModelManager(ab, new UserPrefs());
    }

    private void assertParseExecutesTo(String userInput, List<Followup> expectedFollowups) throws Exception {
        Command command = parser.parse(userInput);
        Model model = createTestModel();
        CommandResult result = command.execute(model);
        assertEquals(String.format(MESSAGE_FOLLOWUPS_LISTED_OVERVIEW, expectedFollowups.size()),
                result.getFeedbackToUser());
        assertEquals(expectedFollowups, model.getFilteredFollowupList());
    }

    @Test
    public void parse_emptyArg_throwsParseException() {
        assertParseFailure(parser, "     ",
                String.format(MESSAGE_INVALID_COMMAND_FORMAT,
                        FindFollowupsCommand.MESSAGE_USAGE));

        assertParseFailure(parser, "",
                String.format(MESSAGE_INVALID_COMMAND_FORMAT,
                        FindFollowupsCommand.MESSAGE_USAGE));

        assertParseFailure(parser, "\t \r \n",
                String.format(MESSAGE_INVALID_COMMAND_FORMAT,
                        FindFollowupsCommand.MESSAGE_USAGE));
    }

    @Test
    public void parse_validArgs_executesExpectedFiltering() throws Exception {
        // Normal input
        assertParseExecutesTo("interview meeting call",
                List.of(interviewFollowup, meetingFollowup, callFollowup));

        // Extra whitespace (tabs, newlines, carriage returns)
        assertParseExecutesTo(" \n interview \n \t meeting \r call \t",
                List.of(interviewFollowup, meetingFollowup, callFollowup));

        // Symbols and numbers
        assertParseExecutesTo("10am urgent! hospital#2", List.of(symbolFollowup));
    }

    @Test
    public void parse_singleKeyword_executesExpectedFiltering() throws Exception {
        assertParseExecutesTo("interview", List.of(interviewFollowup));
        assertParseExecutesTo("   interview   ", List.of(interviewFollowup));
    }
}
