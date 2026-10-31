package seedu.address.logic.commands;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.logic.commands.CommandTestUtil.PREDICATE_SHOW_NO_FOLLOWUPS;
import static seedu.address.logic.commands.CommandTestUtil.PREDICATE_SHOW_NO_STORIES;
import static seedu.address.logic.commands.CommandTestUtil.assertCommandSuccess;
import static seedu.address.logic.commands.CommandTestUtil.showContactAtIndex;
import static seedu.address.testutil.TypicalContacts.getTypicalAddressBook;
import static seedu.address.testutil.TypicalFollowups.FOLLOWUP_A;
import static seedu.address.testutil.TypicalIndexes.INDEX_FIRST_CONTACT;
import static seedu.address.testutil.TypicalStories.STORY_A;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.model.UserPrefs;

/**
 * Contains integration tests (interaction with the Model) and unit tests for ListCommand.
 */
public class ListCommandTest {

    private Model model;
    private Model expectedModel;

    @BeforeEach
    public void setUp() {
        model = new ModelManager(getTypicalAddressBook(), new UserPrefs());
        model.addStory(STORY_A);
        model.addFollowup(FOLLOWUP_A);
        expectedModel = new ModelManager(model.getAddressBook(), new UserPrefs());
    }

    @Test
    public void execute_listIsNotFiltered_showsSameList() {
        String expectedMessage = String.format(ListCommand.MESSAGE_SUCCESS,
                expectedModel.getFilteredContactList().size(),
                expectedModel.getFilteredStoryList().size(),
                expectedModel.getFilteredFollowupList().size()
        );
        assertCommandSuccess(new ListCommand(), model, expectedMessage, expectedModel);
    }

    @Test
    public void execute_listIsFiltered_showsEverything() {
        showContactAtIndex(model, INDEX_FIRST_CONTACT);
        model.updateFilteredStoryList(PREDICATE_SHOW_NO_STORIES);
        model.updateFilteredFollowupList(PREDICATE_SHOW_NO_FOLLOWUPS);
        String expectedMessage = String.format(ListCommand.MESSAGE_SUCCESS,
                expectedModel.getFilteredContactList().size(),
                expectedModel.getFilteredStoryList().size(),
                expectedModel.getFilteredFollowupList().size()
        );
        assertCommandSuccess(new ListCommand(), model, expectedMessage, expectedModel);
    }

    @Test
    public void equals() {
        ListCommand listCommand = new ListCommand();

        // same object -> returns true
        assertTrue(listCommand.equals(listCommand));

        // same values -> returns true
        assertTrue(listCommand.equals(new ListCommand()));

        // different types -> returns false
        assertFalse(listCommand.equals(1));

        // null -> returns false
        assertFalse(listCommand.equals(null));
    }
}
