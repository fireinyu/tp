package seedu.address.logic.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.logic.commands.CommandTestUtil.assertCommandFailure;
import static seedu.address.logic.commands.CommandTestUtil.assertCommandSuccess;
import static seedu.address.logic.commands.CommandTestUtil.showContactAtIndex;
import static seedu.address.testutil.Assert.assertThrows;
import static seedu.address.testutil.TypicalContacts.getTypicalAddressBook;
import static seedu.address.testutil.TypicalFollowups.FOLLOWUP_A;
import static seedu.address.testutil.TypicalFollowups.FOLLOWUP_B;
import static seedu.address.testutil.TypicalIndexes.INDEX_FIRST_CONTACT;
import static seedu.address.testutil.TypicalIndexes.INDEX_SECOND_CONTACT;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import seedu.address.commons.core.index.Index;
import seedu.address.logic.Messages;
import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.model.UserPrefs;
import seedu.address.model.contact.Contact;

public class AddFollowupCommandTest {
    private Model model;

    @BeforeEach
    public void setUp() {
        model = new ModelManager(getTypicalAddressBook(), new UserPrefs());
    }

    @Test
    public void constructor_nullFollowupDescription_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> new AddFollowupCommand(null, INDEX_FIRST_CONTACT));
    }

    @Test
    public void constructor_nullContactIndex_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> new AddFollowupCommand(FOLLOWUP_A.getDescription(), null));
    }

    @Test
    public void execute_validIndexUnfilteredList_success() {
        Contact target = model.getFilteredContactList().get(INDEX_FIRST_CONTACT.getZeroBased());
        AddFollowupCommand addFollowupCommand =
                new AddFollowupCommand(FOLLOWUP_A.getDescription(), INDEX_FIRST_CONTACT);

        String expectedMessage = String.format(AddFollowupCommand.MESSAGE_SUCCESS,
                FOLLOWUP_A.getDescription(), target.getName());

        ModelManager expectedModel = new ModelManager(model.getAddressBook(), new UserPrefs());
        expectedModel.addFollowup(FOLLOWUP_A);

        assertCommandSuccess(addFollowupCommand, model, expectedMessage, expectedModel);
    }

    @Test
    public void execute_invalidIndexUnfilteredList_throwsCommandException() {
        Index outOfBoundIndex = Index.fromOneBased(model.getFilteredContactList().size() + 1);
        AddFollowupCommand addFollowupCommand = new AddFollowupCommand(FOLLOWUP_A.getDescription(), outOfBoundIndex);

        assertCommandFailure(addFollowupCommand, model, Messages.MESSAGE_INVALID_CONTACT_DISPLAYED_INDEX);
    }

    @Test
    public void execute_validIndexFilteredList_success() {
        showContactAtIndex(model, INDEX_FIRST_CONTACT);

        Contact target = model.getFilteredContactList().get(INDEX_FIRST_CONTACT.getZeroBased());
        AddFollowupCommand addFollowupCommand =
                new AddFollowupCommand(FOLLOWUP_A.getDescription(), INDEX_FIRST_CONTACT);

        String expectedMessage = String.format(AddFollowupCommand.MESSAGE_SUCCESS,
                FOLLOWUP_A.getDescription(), target.getName());

        Model expectedModel = new ModelManager(model.getAddressBook(), new UserPrefs());
        expectedModel.addFollowup(FOLLOWUP_A);
        showContactAtIndex(expectedModel, INDEX_FIRST_CONTACT);
        assertCommandSuccess(addFollowupCommand, model, expectedMessage, expectedModel);
    }

    @Test
    public void execute_invalidIndexFilteredList_throwsCommandException() {
        showContactAtIndex(model, INDEX_FIRST_CONTACT);

        Index outOfBoundIndex = INDEX_SECOND_CONTACT;
        // ensures that outOfBoundIndex is still in bounds of address book list
        assertTrue(outOfBoundIndex.getZeroBased() < model.getAddressBook().getContactList().size());

        AddFollowupCommand addFollowupCommand = new AddFollowupCommand(FOLLOWUP_A.getDescription(), outOfBoundIndex);

        assertCommandFailure(addFollowupCommand, model, Messages.MESSAGE_INVALID_CONTACT_DISPLAYED_INDEX);
    }

    @Test
    public void execute_newFollowup_success() {
        Contact target = model.getFilteredContactList().get(INDEX_FIRST_CONTACT.getZeroBased());

        Model expectedModel = new ModelManager(model.getAddressBook(), new UserPrefs());
        expectedModel.addFollowup(FOLLOWUP_A);

        assertCommandSuccess(new AddFollowupCommand(FOLLOWUP_A.getDescription(), INDEX_FIRST_CONTACT), model,
                String.format(AddFollowupCommand.MESSAGE_SUCCESS, FOLLOWUP_A.getDescription(), target.getName()),
                expectedModel);
    }

    @Test
    public void execute_duplicateFollowup_throwsCommandException() {
        model.addFollowup(FOLLOWUP_A);
        assertCommandFailure(new AddFollowupCommand(FOLLOWUP_A.getDescription(), INDEX_FIRST_CONTACT), model,
                String.format(AddFollowupCommand.MESSAGE_DUPLICATE_FOLLOWUP, FOLLOWUP_A.getContact()));
    }

    @Test
    public void equals() {
        AddFollowupCommand addFollowupACommand = new AddFollowupCommand(FOLLOWUP_A.getDescription(),
                INDEX_FIRST_CONTACT);
        AddFollowupCommand addFollowupBCommand = new AddFollowupCommand(FOLLOWUP_B.getDescription(),
                INDEX_SECOND_CONTACT);


        // same object -> returns true
        assertTrue(addFollowupACommand.equals(addFollowupACommand));

        // same values -> returns true
        AddFollowupCommand addFollowupACommandCopy = new AddFollowupCommand(FOLLOWUP_A.getDescription(),
                INDEX_FIRST_CONTACT);
        assertTrue(addFollowupACommand.equals(addFollowupACommandCopy));

        // different types -> returns false
        assertFalse(addFollowupACommand.equals(1));

        // null -> returns false
        assertFalse(addFollowupACommand.equals(null));

        // different contact -> returns false
        assertFalse(addFollowupACommand.equals(addFollowupBCommand));

        // different contact index -> returns false
        AddFollowupCommand addFollowupACommandDifferentContact = new AddFollowupCommand(FOLLOWUP_A.getDescription(),
                INDEX_SECOND_CONTACT);
        assertFalse(addFollowupACommand.equals(addFollowupACommandDifferentContact));
    }

    @Test
    public void toStringMethod() {
        AddFollowupCommand addFollowupCommand =
                new AddFollowupCommand(FOLLOWUP_A.getDescription(), INDEX_FIRST_CONTACT);
        String expected = AddFollowupCommand.class.getCanonicalName() + "{followupDescription="
                + FOLLOWUP_A.getDescription()
                + ", contactIndex=" + INDEX_FIRST_CONTACT + "}";
        assertEquals(expected, addFollowupCommand.toString());
    }


}
