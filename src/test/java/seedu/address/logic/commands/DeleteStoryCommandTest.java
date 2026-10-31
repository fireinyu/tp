package seedu.address.logic.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.logic.commands.CommandTestUtil.PREDICATE_SHOW_NO_STORIES;
import static seedu.address.logic.commands.CommandTestUtil.assertCommandFailure;
import static seedu.address.logic.commands.CommandTestUtil.assertCommandSuccess;
import static seedu.address.logic.commands.CommandTestUtil.showStoryAtIndex;
import static seedu.address.testutil.TypicalIndexes.INDEX_FIRST_STORY;
import static seedu.address.testutil.TypicalIndexes.INDEX_SECOND_STORY;
import static seedu.address.testutil.TypicalStories.getTypicalAddressBook;

import org.junit.jupiter.api.Test;

import seedu.address.commons.core.index.Index;
import seedu.address.logic.Messages;
import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.model.UserPrefs;
import seedu.address.model.story.Story;

/**
 * Contains integration tests (interaction with the Model) and unit tests for
 * {@code DeleteStoryCommand}.
 */
public class DeleteStoryCommandTest {

    private Model model = new ModelManager(getTypicalAddressBook(), new UserPrefs());

    @Test
    public void execute_validIndexUnfilteredList_success() {
        Story storyToDelete = model.getFilteredStoryList().get(INDEX_FIRST_STORY.getZeroBased());
        DeleteStoryCommand deleteStoryCommand = new DeleteStoryCommand(INDEX_FIRST_STORY);

        String expectedMessage = String.format(DeleteStoryCommand.MESSAGE_DELETE_STORY_SUCCESS,
                Messages.format(storyToDelete));

        ModelManager expectedModel = new ModelManager(model.getAddressBook(), new UserPrefs());
        expectedModel.deleteStory(storyToDelete);

        assertCommandSuccess(deleteStoryCommand, model, expectedMessage, expectedModel);
    }

    @Test
    public void execute_invalidIndexUnfilteredList_throwsCommandException() {
        Index outOfBoundIndex = Index.fromOneBased(model.getFilteredStoryList().size() + 1);
        DeleteStoryCommand deleteStoryCommand = new DeleteStoryCommand(outOfBoundIndex);

        assertCommandFailure(deleteStoryCommand, model, Messages.MESSAGE_INVALID_STORY_DISPLAYED_INDEX);
    }

    @Test
    public void execute_validIndexFilteredList_success() {
        showStoryAtIndex(model, INDEX_FIRST_STORY);

        Story storyToDelete = model.getFilteredStoryList().get(INDEX_FIRST_STORY.getZeroBased());
        DeleteStoryCommand deleteStoryCommand = new DeleteStoryCommand(INDEX_FIRST_STORY);

        String expectedMessage = String.format(DeleteStoryCommand.MESSAGE_DELETE_STORY_SUCCESS,
                Messages.format(storyToDelete));

        Model expectedModel = new ModelManager(model.getAddressBook(), new UserPrefs());
        showStoryAtIndex(expectedModel, INDEX_FIRST_STORY);
        expectedModel.deleteStory(storyToDelete);

        assertCommandSuccess(deleteStoryCommand, model, expectedMessage, expectedModel);
    }

    @Test
    public void execute_invalidIndexFilteredList_throwsCommandException() {
        showStoryAtIndex(model, INDEX_FIRST_STORY);

        Index outOfBoundIndex = INDEX_SECOND_STORY;
        // ensures that outOfBoundIndex is still in bounds of address book list
        assertTrue(outOfBoundIndex.getZeroBased() < model.getAddressBook().getStoryList().size());

        DeleteStoryCommand deleteStoryCommand = new DeleteStoryCommand(outOfBoundIndex);

        assertCommandFailure(deleteStoryCommand, model, Messages.MESSAGE_INVALID_STORY_DISPLAYED_INDEX);
    }

    @Test
    public void equals() {
        DeleteStoryCommand deleteFirstCommand = new DeleteStoryCommand(INDEX_FIRST_STORY);
        DeleteStoryCommand deleteSecondCommand = new DeleteStoryCommand(INDEX_SECOND_STORY);

        // same object -> returns true
        assertTrue(deleteFirstCommand.equals(deleteFirstCommand));

        // same values -> returns true
        DeleteStoryCommand deleteFirstCommandCopy = new DeleteStoryCommand(INDEX_FIRST_STORY);
        assertTrue(deleteFirstCommand.equals(deleteFirstCommandCopy));

        // different types -> returns false
        assertFalse(deleteFirstCommand.equals(1));

        // null -> returns false
        assertFalse(deleteFirstCommand.equals(null));

        // different story -> returns false
        assertFalse(deleteFirstCommand.equals(deleteSecondCommand));
    }

    @Test
    public void toStringMethod() {
        Index targetIndex = Index.fromOneBased(1);
        DeleteStoryCommand deleteStoryCommand = new DeleteStoryCommand(targetIndex);
        String expected = DeleteStoryCommand.class.getCanonicalName() + "{targetIndex=" + targetIndex + "}";
        assertEquals(expected, deleteStoryCommand.toString());
    }

    /**
     * Updates {@code model}'s filtered list to show no stories.
     */
    private void showNoStory(Model model) {
        model.updateFilteredStoryList(PREDICATE_SHOW_NO_STORIES);

        assertTrue(model.getFilteredStoryList().isEmpty());
    }
}
