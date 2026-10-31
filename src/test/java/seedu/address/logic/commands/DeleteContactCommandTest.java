package seedu.address.logic.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.logic.commands.CommandTestUtil.assertCommandFailure;
import static seedu.address.logic.commands.CommandTestUtil.assertCommandSuccess;
import static seedu.address.logic.commands.CommandTestUtil.showContactAtIndex;
import static seedu.address.testutil.TypicalContacts.getTypicalAddressBook;
import static seedu.address.testutil.TypicalIndexes.INDEX_FIRST_CONTACT;
import static seedu.address.testutil.TypicalIndexes.INDEX_SECOND_CONTACT;

import org.junit.jupiter.api.Test;

import seedu.address.commons.core.index.Index;
import seedu.address.logic.Messages;
import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.model.UserPrefs;
import seedu.address.model.contact.Contact;

/**
 * Contains integration tests (interaction with the Model) and unit tests for
 * {@code DeleteContactCommand}.
 */
public class DeleteContactCommandTest {

    private Model model = new ModelManager(getTypicalAddressBook(), new UserPrefs());

    @Test
    public void execute_validIndexUnfilteredList_success() {
        Contact contactToDelete = model.getFilteredContactList().get(INDEX_FIRST_CONTACT.getZeroBased());
        DeleteContactCommand deleteContactCommand = new DeleteContactCommand(INDEX_FIRST_CONTACT);

        String expectedMessage = String.format(DeleteContactCommand.MESSAGE_DELETE_CONTACT_SUCCESS,
                Messages.format(contactToDelete));

        ModelManager expectedModel = new ModelManager(model.getAddressBook(), new UserPrefs());
        expectedModel.deleteContact(contactToDelete);

        assertCommandSuccess(deleteContactCommand, model, expectedMessage, expectedModel);
    }

    @Test
    public void execute_invalidIndexUnfilteredList_throwsCommandException() {
        Index outOfBoundIndex = Index.fromOneBased(model.getFilteredContactList().size() + 1);
        DeleteContactCommand deleteContactCommand = new DeleteContactCommand(outOfBoundIndex);

        assertCommandFailure(deleteContactCommand, model, Messages.MESSAGE_INVALID_CONTACT_DISPLAYED_INDEX);
    }

    @Test
    public void execute_validIndexFilteredList_success() {
        showContactAtIndex(model, INDEX_FIRST_CONTACT);

        Contact contactToDelete = model.getFilteredContactList().get(INDEX_FIRST_CONTACT.getZeroBased());
        DeleteContactCommand deleteContactCommand = new DeleteContactCommand(INDEX_FIRST_CONTACT);

        String expectedMessage = String.format(DeleteContactCommand.MESSAGE_DELETE_CONTACT_SUCCESS,
                Messages.format(contactToDelete));

        Model expectedModel = new ModelManager(model.getAddressBook(), new UserPrefs());
        showContactAtIndex(expectedModel, INDEX_FIRST_CONTACT);
        expectedModel.deleteContact(contactToDelete);

        assertCommandSuccess(deleteContactCommand, model, expectedMessage, expectedModel);
    }

    @Test
    public void execute_invalidIndexFilteredList_throwsCommandException() {
        showContactAtIndex(model, INDEX_FIRST_CONTACT);

        Index outOfBoundIndex = INDEX_SECOND_CONTACT;
        // ensures that outOfBoundIndex is still in bounds of address book list
        assertTrue(outOfBoundIndex.getZeroBased() < model.getAddressBook().getContactList().size());

        DeleteContactCommand deleteContactCommand = new DeleteContactCommand(outOfBoundIndex);

        assertCommandFailure(deleteContactCommand, model, Messages.MESSAGE_INVALID_CONTACT_DISPLAYED_INDEX);
    }

    @Test
    public void equals() {
        DeleteContactCommand deleteFirstCommand = new DeleteContactCommand(INDEX_FIRST_CONTACT);
        DeleteContactCommand deleteSecondCommand = new DeleteContactCommand(INDEX_SECOND_CONTACT);

        // same object -> returns true
        assertTrue(deleteFirstCommand.equals(deleteFirstCommand));

        // same values -> returns true
        DeleteContactCommand deleteFirstCommandCopy = new DeleteContactCommand(INDEX_FIRST_CONTACT);
        assertTrue(deleteFirstCommand.equals(deleteFirstCommandCopy));

        // different types -> returns false
        assertFalse(deleteFirstCommand.equals(1));

        // null -> returns false
        assertFalse(deleteFirstCommand.equals(null));

        // different contact -> returns false
        assertFalse(deleteFirstCommand.equals(deleteSecondCommand));
    }

    @Test
    public void toStringMethod() {
        Index targetIndex = Index.fromOneBased(1);
        DeleteContactCommand deleteContactCommand = new DeleteContactCommand(targetIndex);
        String expected = DeleteContactCommand.class.getCanonicalName() + "{targetIndex=" + targetIndex + "}";
        assertEquals(expected, deleteContactCommand.toString());
    }

}
