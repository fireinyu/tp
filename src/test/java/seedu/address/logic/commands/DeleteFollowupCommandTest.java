package seedu.address.logic.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.logic.commands.CommandTestUtil.PREDICATE_SHOW_NO_FOLLOWUPS;
import static seedu.address.logic.commands.CommandTestUtil.assertCommandFailure;
import static seedu.address.logic.commands.CommandTestUtil.assertCommandSuccess;
import static seedu.address.logic.commands.CommandTestUtil.showFollowupAtIndex;
import static seedu.address.testutil.TypicalFollowups.getTypicalAddressBook;
import static seedu.address.testutil.TypicalIndexes.INDEX_FIRST_FOLLOWUP;
import static seedu.address.testutil.TypicalIndexes.INDEX_SECOND_FOLLOWUP;

import org.junit.jupiter.api.Test;

import seedu.address.commons.core.index.Index;
import seedu.address.logic.Messages;
import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.model.UserPrefs;
import seedu.address.model.followup.Followup;

/**
 * Contains integration tests (interaction with the Model) and unit tests for
 * {@code DeleteFollowupCommand}.
 */
public class DeleteFollowupCommandTest {

    private Model model = new ModelManager(getTypicalAddressBook(), new UserPrefs());

    @Test
    public void execute_validIndexUnfilteredList_success() {
        Followup followupToDelete = model.getFilteredFollowupList().get(INDEX_FIRST_FOLLOWUP.getZeroBased());
        DeleteFollowupCommand deleteFollowupCommand = new DeleteFollowupCommand(INDEX_FIRST_FOLLOWUP);

        String expectedMessage = String.format(DeleteFollowupCommand.MESSAGE_DELETE_FOLLOWUP_SUCCESS,
                Messages.format(followupToDelete));

        ModelManager expectedModel = new ModelManager(model.getAddressBook(), new UserPrefs());
        expectedModel.deleteFollowup(followupToDelete);

        assertCommandSuccess(deleteFollowupCommand, model, expectedMessage, expectedModel);
    }

    @Test
    public void execute_invalidIndexUnfilteredList_throwsCommandException() {
        Index outOfBoundIndex = Index.fromOneBased(model.getFilteredFollowupList().size() + 1);
        DeleteFollowupCommand deleteFollowupCommand = new DeleteFollowupCommand(outOfBoundIndex);

        assertCommandFailure(deleteFollowupCommand, model, Messages.MESSAGE_INVALID_FOLLOWUP_DISPLAYED_INDEX);
    }

    @Test
    public void execute_validIndexFilteredList_success() {
        showFollowupAtIndex(model, INDEX_FIRST_FOLLOWUP);

        Followup followupToDelete = model.getFilteredFollowupList().get(INDEX_FIRST_FOLLOWUP.getZeroBased());
        DeleteFollowupCommand deleteFollowupCommand = new DeleteFollowupCommand(INDEX_FIRST_FOLLOWUP);

        String expectedMessage = String.format(DeleteFollowupCommand.MESSAGE_DELETE_FOLLOWUP_SUCCESS,
                Messages.format(followupToDelete));

        Model expectedModel = new ModelManager(model.getAddressBook(), new UserPrefs());
        showFollowupAtIndex(expectedModel, INDEX_FIRST_FOLLOWUP);
        expectedModel.deleteFollowup(followupToDelete);

        assertCommandSuccess(deleteFollowupCommand, model, expectedMessage, expectedModel);
    }

    @Test
    public void execute_invalidIndexFilteredList_throwsCommandException() {
        showFollowupAtIndex(model, INDEX_FIRST_FOLLOWUP);

        Index outOfBoundIndex = INDEX_SECOND_FOLLOWUP;
        // ensures that outOfBoundIndex is still in bounds of address book list
        assertTrue(outOfBoundIndex.getZeroBased() < model.getAddressBook().getFollowupList().size());

        DeleteFollowupCommand deleteFollowupCommand = new DeleteFollowupCommand(outOfBoundIndex);

        assertCommandFailure(deleteFollowupCommand, model, Messages.MESSAGE_INVALID_FOLLOWUP_DISPLAYED_INDEX);
    }

    @Test
    public void equals() {
        DeleteFollowupCommand deleteFollowupFirstCommand = new DeleteFollowupCommand(INDEX_FIRST_FOLLOWUP);
        DeleteFollowupCommand deleteFollowupSecondCommand = new DeleteFollowupCommand(INDEX_SECOND_FOLLOWUP);

        // same object -> returns true
        assertTrue(deleteFollowupFirstCommand.equals(deleteFollowupFirstCommand));

        // same values -> returns true
        DeleteFollowupCommand deleteFollowupFirstCommandCopy = new DeleteFollowupCommand(INDEX_FIRST_FOLLOWUP);
        assertTrue(deleteFollowupFirstCommand.equals(deleteFollowupFirstCommandCopy));

        // different types -> returns false
        assertFalse(deleteFollowupFirstCommand.equals(1));

        // null -> returns false
        assertFalse(deleteFollowupFirstCommand.equals(null));

        // different followup -> returns false
        assertFalse(deleteFollowupFirstCommand.equals(deleteFollowupSecondCommand));
    }

    @Test
    public void toStringMethod() {
        Index targetIndex = Index.fromOneBased(1);
        DeleteFollowupCommand deleteFollowupCommand = new DeleteFollowupCommand(targetIndex);
        String expected = DeleteFollowupCommand.class.getCanonicalName() + "{targetIndex=" + targetIndex + "}";
        assertEquals(expected, deleteFollowupCommand.toString());
    }

    /**
     * Updates {@code model}'s filtered list to show no followups.
     */
    private void showNoFollowup(Model model) {
        model.updateFilteredFollowupList(PREDICATE_SHOW_NO_FOLLOWUPS);

        assertTrue(model.getFilteredFollowupList().isEmpty());
    }
}
