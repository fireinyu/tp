package seedu.address.logic.parser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.Messages.MESSAGE_STORIES_LISTED_OVERVIEW;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseFailure;

import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.Test;

import seedu.address.logic.commands.Command;
import seedu.address.logic.commands.CommandResult;
import seedu.address.logic.commands.FindStoriesCommand;
import seedu.address.model.AddressBook;
import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.model.UserPrefs;
import seedu.address.model.story.Story;
import seedu.address.model.story.StoryName;

public class FindStoriesCommandParserTest {

    private final FindStoryCommandParser parser = new FindStoryCommandParser();

    private final Story housingStory = new Story(new StoryName("Housing Project"), Collections.emptySet());
    private final Story investigationStory =
            new Story(new StoryName("Investigation Log"), Collections.emptySet());
    private final Story scandalStory = new Story(new StoryName("Scandal Report"), Collections.emptySet());
    private final Story symbolStory =
            new Story(new StoryName("COVID-19 2026 Budget!"), Collections.emptySet());
    private final Story otherStory = new Story(new StoryName("Education Review"), Collections.emptySet());

    private Model createTestModel() {
        AddressBook ab = new AddressBook();
        ab.addStory(housingStory);
        ab.addStory(investigationStory);
        ab.addStory(scandalStory);
        ab.addStory(symbolStory);
        ab.addStory(otherStory);
        return new ModelManager(ab, new UserPrefs());
    }

    private void assertParseExecutesTo(String userInput, List<Story> expectedStories) throws Exception {
        Command command = parser.parse(userInput);
        Model model = createTestModel();
        CommandResult result = command.execute(model);
        assertEquals(String.format(MESSAGE_STORIES_LISTED_OVERVIEW, expectedStories.size()),
                result.getFeedbackToUser());
        assertEquals(expectedStories, model.getFilteredStoryList());
    }

    @Test
    public void parse_emptyArg_throwsParseException() {
        String expectedMessage = String.format(MESSAGE_INVALID_COMMAND_FORMAT, FindStoriesCommand.MESSAGE_USAGE);
        assertParseFailure(parser, "     ", expectedMessage);
        assertParseFailure(parser, "", expectedMessage);
        assertParseFailure(parser, "\t \r \n", expectedMessage);
    }

    @Test
    public void parse_validArgs_executesExpectedFiltering() throws Exception {
        // Single keyword
        assertParseExecutesTo("Housing", List.of(housingStory));
        assertParseExecutesTo("   Housing   ", List.of(housingStory));

        // Multiple keywords
        assertParseExecutesTo("Housing Investigation Scandal",
                List.of(housingStory, investigationStory, scandalStory));

        // Whitespace variations (newlines, tabs)
        assertParseExecutesTo(" \n Housing \n \t Investigation \r Scandal \t",
                List.of(housingStory, investigationStory, scandalStory));

        // Keywords with symbols or numbers
        assertParseExecutesTo("2026 Budget! COVID-19", List.of(symbolStory));
    }
}
