package seedu.address.logic.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.logic.commands.CommandTestUtil.assertCommandFailure;
import static seedu.address.testutil.Assert.assertThrows;
import static seedu.address.testutil.TypicalContacts.ALICE;
import static seedu.address.testutil.TypicalContacts.BENSON;

import org.junit.jupiter.api.Test;

import seedu.address.commons.core.index.Index;
import seedu.address.logic.Messages;
import seedu.address.logic.commands.CommandTestUtil.ContactEqualsPredicate;
import seedu.address.logic.commands.EditFollowupCommand.EditFollowupDescriptor;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.logic.parser.AddressBookParser;
import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.model.followup.Followup;
import seedu.address.model.followup.FollowupDescription;
import seedu.address.model.followup.FollowupIsForContactPredicate;

public class EditFollowupCommandTest {
    private final Model model = new ModelManager();
    private final Followup original = new Followup(new FollowupDescription("Call"), ALICE.getName());

    public EditFollowupCommandTest() {
        model.addContact(ALICE);
        model.addContact(BENSON);
        model.addFollowup(original);
    }

    @Test
    public void execute_descriptionOnly_preservesContact() throws Exception {
        new AddressBookParser().parseCommand("editfollowup 1 d/Email").execute(model);
        assertEquals(new Followup(new FollowupDescription("Email"), ALICE.getName()),
                model.getFilteredFollowupList().get(0));
    }

    @Test
    public void execute_contactOnly_usesFilteredContactIndex() throws Exception {
        model.updateFilteredContactList(new ContactEqualsPredicate(BENSON));
        new AddressBookParser().parseCommand("editfollowup 1 ci/1").execute(model);
        assertEquals(new Followup(original.getDescription(), BENSON.getName()),
                model.getFilteredFollowupList().get(0));
    }

    @Test
    public void execute_bothFields_usesFilteredFollowupIndexAndPreservesFilter() throws Exception {
        Followup second = new Followup(new FollowupDescription("Meet"), ALICE.getName());
        model.addFollowup(second);
        model.updateFilteredFollowupList(new FollowupIsForContactPredicate(ALICE));
        assertEquals(2, model.getFilteredFollowupList().size());

        new AddressBookParser().parseCommand("editfollowup 2 d/Email ci/2").execute(model);
        assertEquals(1, model.getFilteredFollowupList().size());
        assertEquals(original, model.getFilteredFollowupList().get(0));

        Followup edited = new Followup(new FollowupDescription("Email"), BENSON.getName());
        assertTrue(model.getAddressBook().getFollowupList().contains(edited));
    }

    @Test
    public void execute_duplicateContactChange_failure() {
        model.addFollowup(new Followup(original.getDescription(), BENSON.getName()));
        EditFollowupDescriptor descriptor = new EditFollowupDescriptor();
        descriptor.setContactIndex(Index.fromOneBased(2));
        EditFollowupCommand command = new EditFollowupCommand(Index.fromOneBased(1), descriptor);
        assertThrows(CommandException.class,
                EditFollowupCommand.MESSAGE_DUPLICATE_FOLLOWUP, () -> command.execute(model));
        assertEquals(original, model.getFilteredFollowupList().get(0));
    }

    @Test
    public void execute_invalidIndexes_failure() {
        EditFollowupDescriptor descriptor = new EditFollowupDescriptor();
        descriptor.setContactIndex(Index.fromOneBased(3));
        assertCommandFailure(new EditFollowupCommand(Index.fromOneBased(2), descriptor), model,
                Messages.MESSAGE_INVALID_FOLLOWUP_DISPLAYED_INDEX);
        assertCommandFailure(new EditFollowupCommand(Index.fromOneBased(1), descriptor), model,
                Messages.MESSAGE_INVALID_CONTACT_DISPLAYED_INDEX);

        // Boundary max integer index for followup
        assertCommandFailure(new EditFollowupCommand(Index.fromOneBased(Integer.MAX_VALUE), descriptor), model,
                Messages.MESSAGE_INVALID_FOLLOWUP_DISPLAYED_INDEX);

        // Boundary max integer index for contact
        EditFollowupDescriptor maxContactDescriptor = new EditFollowupDescriptor();
        maxContactDescriptor.setContactIndex(Index.fromOneBased(Integer.MAX_VALUE));
        assertCommandFailure(new EditFollowupCommand(Index.fromOneBased(1), maxContactDescriptor), model,
                Messages.MESSAGE_INVALID_CONTACT_DISPLAYED_INDEX);
    }

    @Test
    public void execute_unchangedValues_success() throws Exception {
        new AddressBookParser().parseCommand("editfollowup 1 d/Call ci/1").execute(model);
        assertEquals(original, model.getFilteredFollowupList().get(0));
    }

    @Test
    public void execute_nullModel_throwsNullPointerException() {
        EditFollowupDescriptor descriptor = new EditFollowupDescriptor();
        EditFollowupCommand command = new EditFollowupCommand(Index.fromOneBased(1), descriptor);
        assertThrows(NullPointerException.class, () -> command.execute(null));
    }

    @Test
    public void equals() {
        EditFollowupDescriptor descriptor = new EditFollowupDescriptor();
        descriptor.setDescription(new FollowupDescription("Call"));
        EditFollowupCommand command = new EditFollowupCommand(Index.fromOneBased(1), descriptor);

        // same object -> returns true
        assertTrue(command.equals(command));

        // same values -> returns true
        assertTrue(command.equals(new EditFollowupCommand(Index.fromOneBased(1), descriptor)));

        // null -> returns false
        assertFalse(command.equals(null));

        // different types -> returns false
        assertFalse(command.equals(5));

        // different index -> returns false
        assertFalse(command.equals(new EditFollowupCommand(Index.fromOneBased(2), descriptor)));

        // different descriptor -> returns false
        EditFollowupDescriptor differentDescriptor = new EditFollowupDescriptor();
        differentDescriptor.setDescription(new FollowupDescription("Email"));
        assertFalse(command.equals(new EditFollowupCommand(Index.fromOneBased(1), differentDescriptor)));
    }

    @Test
    public void toStringMethod() {
        EditFollowupDescriptor descriptor = new EditFollowupDescriptor();
        descriptor.setDescription(new FollowupDescription("Call"));
        EditFollowupCommand command = new EditFollowupCommand(Index.fromOneBased(1), descriptor);
        String expected = EditFollowupCommand.class.getCanonicalName()
                + "{index=" + Index.fromOneBased(1) + ", editFollowupDescriptor=" + descriptor + "}";
        assertEquals(expected, command.toString());
    }
}
