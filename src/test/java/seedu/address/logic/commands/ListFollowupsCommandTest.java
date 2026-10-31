package seedu.address.logic.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.logic.commands.CommandTestUtil.PREDICATE_SHOW_NO_FOLLOWUPS;
import static seedu.address.logic.commands.CommandTestUtil.assertCommandFailure;
import static seedu.address.logic.commands.CommandTestUtil.assertCommandSuccess;
import static seedu.address.logic.commands.CommandTestUtil.showContactAtIndex;
import static seedu.address.testutil.Assert.assertThrows;
import static seedu.address.testutil.TypicalContacts.ALICE;
import static seedu.address.testutil.TypicalContacts.BENSON;
import static seedu.address.testutil.TypicalContacts.DANIEL;
import static seedu.address.testutil.TypicalFollowups.FOLLOWUP_A;
import static seedu.address.testutil.TypicalFollowups.FOLLOWUP_B;
import static seedu.address.testutil.TypicalIndexes.INDEX_FIRST_CONTACT;
import static seedu.address.testutil.TypicalIndexes.INDEX_SECOND_CONTACT;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import seedu.address.commons.core.index.Index;
import seedu.address.logic.Messages;
import seedu.address.model.AddressBook;
import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.model.UserPrefs;
import seedu.address.model.contact.Contact;
import seedu.address.model.followup.FollowupIsForContactPredicate;
import seedu.address.testutil.ContactBuilder;

/**
 * Contains integration tests (interaction with the Model) and unit tests for ListFollowupsCommand.
 */
public class ListFollowupsCommandTest {

    private Model model;
    private Model expectedModel;

    @BeforeEach
    public void setUp() {
        AddressBook addressBook = new AddressBook();
        addressBook.addContact(ALICE);
        addressBook.addContact(BENSON);
        addressBook.addContact(DANIEL);
        addressBook.addFollowup(FOLLOWUP_A);
        addressBook.addFollowup(FOLLOWUP_B);

        model = new ModelManager(addressBook, new UserPrefs());
        expectedModel = new ModelManager(addressBook, new UserPrefs());
    }

    @Test
    public void constructor_nullIndex_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> new ListFollowupsCommand(null));
    }

    @Test
    public void execute_nullModel_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> new ListFollowupsCommand().execute(null));
        assertThrows(NullPointerException.class, () -> new ListFollowupsCommand(INDEX_FIRST_CONTACT).execute(null));
    }

    @Test
    public void execute_listIsNotFiltered_showsSameList() {
        String expectedMessage = String.format(ListFollowupsCommand.MESSAGE_SUCCESS,
                expectedModel.getFilteredFollowupList().size());
        assertCommandSuccess(new ListFollowupsCommand(), model, expectedMessage, expectedModel);
    }

    @Test
    public void execute_listIsFiltered_showsEverything() {
        model.updateFilteredFollowupList(PREDICATE_SHOW_NO_FOLLOWUPS);
        String expectedMessage = String.format(ListFollowupsCommand.MESSAGE_SUCCESS,
                expectedModel.getFilteredFollowupList().size());
        assertCommandSuccess(new ListFollowupsCommand(), model, expectedMessage, expectedModel);
    }

    @Test
    public void execute_validContactIndex_showsAssignedFollowups() {
        Contact contact = model.getFilteredContactList().get(INDEX_FIRST_CONTACT.getZeroBased());
        expectedModel.updateFilteredFollowupList(new FollowupIsForContactPredicate(contact));

        String expectedMessage = String.format(ListFollowupsCommand.MESSAGE_LIST_ASSIGNED_FOLLOWUPS_SUCCESS,
                1, contact.getName());

        assertCommandSuccess(new ListFollowupsCommand(INDEX_FIRST_CONTACT), model, expectedMessage, expectedModel);
        assertEquals(1, model.getFilteredFollowupList().size());
        assertEquals(FOLLOWUP_A, model.getFilteredFollowupList().get(0));
    }

    @Test
    public void execute_validContactIndexNoAssignments_showsEmptyList() {
        // DANIEL is the 3rd contact and has no follow-ups
        Index danielIndex = Index.fromOneBased(3);
        Contact contact = model.getFilteredContactList().get(danielIndex.getZeroBased());
        expectedModel.updateFilteredFollowupList(new FollowupIsForContactPredicate(contact));

        String expectedMessage = String.format(ListFollowupsCommand.MESSAGE_LIST_ASSIGNED_FOLLOWUPS_SUCCESS,
                0, contact.getName());

        assertCommandSuccess(new ListFollowupsCommand(danielIndex), model, expectedMessage, expectedModel);
        assertTrue(model.getFilteredFollowupList().isEmpty());
    }

    @Test
    public void execute_editContactNameAfterListFollowups_updatesFilterAndAssignedFollowupsRemain() throws Exception {
        ListFollowupsCommand listFollowupsCommand = new ListFollowupsCommand(INDEX_FIRST_CONTACT);
        listFollowupsCommand.execute(model);
        assertEquals(1, model.getFilteredFollowupList().size());

        // Edit the contact's name
        Contact contactToEdit = model.getFilteredContactList().get(INDEX_FIRST_CONTACT.getZeroBased());
        Contact editedContact = new ContactBuilder(contactToEdit).withName("Renamed Contact").build();
        model.setContact(contactToEdit, editedContact);

        // Filter status and filtered followups should be updated
        String expectedStatus = new FollowupIsForContactPredicate(editedContact).toString();
        assertEquals(expectedStatus, model.getFollowupFilterStatusProperty().getValue());
        assertEquals(1, model.getFilteredFollowupList().size());
        assertEquals(editedContact.getName(), model.getFilteredFollowupList().get(0).getContact());
    }

    @Test
    public void execute_invalidContactIndex_throwsCommandException() {
        Index invalidIndex = Index.fromOneBased(model.getFilteredContactList().size() + 1);

        assertCommandFailure(
                new ListFollowupsCommand(invalidIndex),
                model,
                Messages.MESSAGE_INVALID_CONTACT_DISPLAYED_INDEX);
    }

    @Test
    public void execute_invalidContactIndexFilteredList_throwsCommandException() {
        showContactAtIndex(model, INDEX_FIRST_CONTACT);
        Index outOfBoundIndex = INDEX_SECOND_CONTACT;
        // ensures outOfBoundIndex is valid in address book, but out of bounds in filtered contact list
        assertTrue(outOfBoundIndex.getZeroBased() < model.getAddressBook().getContactList().size());

        assertCommandFailure(
                new ListFollowupsCommand(outOfBoundIndex),
                model,
                Messages.MESSAGE_INVALID_CONTACT_DISPLAYED_INDEX);
    }

    @Test
    public void equals() {
        ListFollowupsCommand listFollowups = new ListFollowupsCommand();
        ListFollowupsCommand listFollowupsWithIndex = new ListFollowupsCommand(INDEX_FIRST_CONTACT);

        // same object -> returns true
        assertTrue(listFollowups.equals(listFollowups));

        // same values -> returns true
        assertTrue(listFollowups.equals(new ListFollowupsCommand()));
        assertTrue(listFollowupsWithIndex.equals(new ListFollowupsCommand(INDEX_FIRST_CONTACT)));

        // different types -> returns false
        assertFalse(listFollowups.equals(1));

        // null -> returns false
        assertFalse(listFollowups.equals(null));

        // different indices -> returns false
        assertFalse(listFollowupsWithIndex.equals(new ListFollowupsCommand(INDEX_SECOND_CONTACT)));
        assertFalse(listFollowups.equals(listFollowupsWithIndex));
    }

    @Test
    public void hashCodeMethod() {
        ListFollowupsCommand listFollowups1 = new ListFollowupsCommand();
        ListFollowupsCommand listFollowups2 = new ListFollowupsCommand();
        assertEquals(listFollowups1.hashCode(), listFollowups2.hashCode());

        ListFollowupsCommand listFollowupsWithIndex1 = new ListFollowupsCommand(INDEX_FIRST_CONTACT);
        ListFollowupsCommand listFollowupsWithIndex2 = new ListFollowupsCommand(INDEX_FIRST_CONTACT);
        assertEquals(listFollowupsWithIndex1.hashCode(), listFollowupsWithIndex2.hashCode());
    }

    @Test
    public void toStringMethod() {
        ListFollowupsCommand listFollowups = new ListFollowupsCommand(INDEX_FIRST_CONTACT);
        String expected = ListFollowupsCommand.class.getCanonicalName()
                + "{contactIndex=Optional[" + INDEX_FIRST_CONTACT + "]}";
        assertEquals(expected, listFollowups.toString());
    }
}
