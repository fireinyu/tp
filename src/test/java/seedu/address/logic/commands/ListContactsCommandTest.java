package seedu.address.logic.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.logic.commands.CommandTestUtil.assertCommandFailure;
import static seedu.address.logic.commands.CommandTestUtil.assertCommandSuccess;
import static seedu.address.logic.commands.CommandTestUtil.showContactAtIndex;
import static seedu.address.testutil.Assert.assertThrows;
import static seedu.address.testutil.TypicalContacts.getTypicalAddressBook;
import static seedu.address.testutil.TypicalIndexes.INDEX_FIRST_CONTACT;
import static seedu.address.testutil.TypicalIndexes.INDEX_FIRST_STORY;
import static seedu.address.testutil.TypicalIndexes.INDEX_SECOND_STORY;
import static seedu.address.testutil.TypicalStories.STORY_A;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import seedu.address.commons.core.index.Index;
import seedu.address.logic.Messages;
import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.model.UserPrefs;
import seedu.address.model.contact.Contact;
import seedu.address.model.contact.ContactAssignedToStoryPredicate;
import seedu.address.model.story.Story;
import seedu.address.model.story.StoryName;
import seedu.address.testutil.ContactBuilder;

/**
 * Contains integration tests (interaction with the Model) and unit tests for ListContactsCommand.
 */
public class ListContactsCommandTest {

    private Model model;
    private Model expectedModel;

    @BeforeEach
    public void setUp() {
        model = new ModelManager(getTypicalAddressBook(), new UserPrefs());
        model.addStory(STORY_A);
        expectedModel = new ModelManager(model.getAddressBook(), new UserPrefs());
    }

    @Test
    public void execute_listIsNotFiltered_showsSameList() {
        String expectedMessage = String.format(ListContactsCommand.MESSAGE_SUCCESS,
                expectedModel.getFilteredContactList().size());
        assertCommandSuccess(new ListContactsCommand(), model, expectedMessage, expectedModel);
    }

    @Test
    public void execute_listIsFiltered_showsEverything() {
        showContactAtIndex(model, INDEX_FIRST_CONTACT);
        String expectedMessage = String.format(ListContactsCommand.MESSAGE_SUCCESS,
                expectedModel.getFilteredContactList().size());
        assertCommandSuccess(new ListContactsCommand(), model, expectedMessage, expectedModel);
    }

    @Test
    public void execute_validStoryIndex_showsAssignedContacts() {
        Contact contact = model.getFilteredContactList().get(INDEX_FIRST_CONTACT.getZeroBased());
        Story story = model.getFilteredStoryList().get(INDEX_FIRST_STORY.getZeroBased());

        model.assignStory(contact, story);

        Story assignedStory = model.getFilteredStoryList().get(INDEX_FIRST_STORY.getZeroBased());

        expectedModel = new ModelManager(model.getAddressBook(), new UserPrefs());
        expectedModel.updateFilteredContactList(new ContactAssignedToStoryPredicate(assignedStory));

        String expectedMessage = String.format(ListContactsCommand.MESSAGE_LIST_ASSIGNED_CONTACTS_SUCCESS,
                1, assignedStory.getName());

        assertCommandSuccess(new ListContactsCommand(INDEX_FIRST_STORY), model, expectedMessage, expectedModel);

        assertEquals(1, model.getFilteredContactList().size());
        assertEquals(contact.getName(), model.getFilteredContactList().get(0).getName());
    }

    @Test
    public void execute_validStoryIndexNoAssignments_showsEmptyList() {
        Story story = model.getFilteredStoryList().get(INDEX_FIRST_STORY.getZeroBased());

        expectedModel.updateFilteredContactList(new ContactAssignedToStoryPredicate(story));

        String expectedMessage = String.format(ListContactsCommand.MESSAGE_LIST_ASSIGNED_CONTACTS_SUCCESS,
                0, story.getName());

        assertCommandSuccess(new ListContactsCommand(INDEX_FIRST_STORY), model, expectedMessage, expectedModel);

        assertTrue(model.getFilteredContactList().isEmpty());
    }

    @Test
    public void execute_editContactAfterListContacts_contactRemainsInFilteredList() throws Exception {
        Contact contact = model.getFilteredContactList().get(INDEX_FIRST_CONTACT.getZeroBased());
        Story story = model.getFilteredStoryList().get(INDEX_FIRST_STORY.getZeroBased());
        model.assignStory(contact, story);

        ListContactsCommand listContactsCommand = new ListContactsCommand(INDEX_FIRST_STORY);
        listContactsCommand.execute(model);
        assertEquals(1, model.getFilteredContactList().size());

        // Edit the contact's phone while under listcontacts filter
        Contact contactToEdit = model.getFilteredContactList().get(0);
        Contact editedContact = new ContactBuilder(contactToEdit).withPhone("98765432").build();
        model.setContact(contactToEdit, editedContact);

        // The edited contact must still be present in the filtered contact list!
        assertEquals(1, model.getFilteredContactList().size());
        assertEquals(editedContact.getName(), model.getFilteredContactList().get(0).getName());
    }

    @Test
    public void execute_editContactNameAfterListContacts_contactRemainsInFilteredList() throws Exception {
        Contact contact = model.getFilteredContactList().get(INDEX_FIRST_CONTACT.getZeroBased());
        Story story = model.getFilteredStoryList().get(INDEX_FIRST_STORY.getZeroBased());
        model.assignStory(contact, story);

        ListContactsCommand listContactsCommand = new ListContactsCommand(INDEX_FIRST_STORY);
        listContactsCommand.execute(model);
        assertEquals(1, model.getFilteredContactList().size());

        // Edit the contact's name while under listcontacts filter
        Contact contactToEdit = model.getFilteredContactList().get(0);
        Contact editedContact = new ContactBuilder(contactToEdit).withName("New Contact Name").build();
        model.setContact(contactToEdit, editedContact);

        // The edited contact must still be present in the filtered contact list!
        assertEquals(1, model.getFilteredContactList().size());
        assertEquals(editedContact.getName(), model.getFilteredContactList().get(0).getName());
    }

    @Test
    public void execute_editStoryNameAfterListContacts_updatesFilterAndAssignedContactsRemain() throws Exception {
        Contact contact = model.getFilteredContactList().get(INDEX_FIRST_CONTACT.getZeroBased());
        Story story = model.getFilteredStoryList().get(INDEX_FIRST_STORY.getZeroBased());
        model.assignStory(contact, story);

        ListContactsCommand listContactsCommand = new ListContactsCommand(INDEX_FIRST_STORY);
        listContactsCommand.execute(model);
        assertEquals(1, model.getFilteredContactList().size());

        // Edit the story's name
        Story storyToEdit = model.getFilteredStoryList().get(INDEX_FIRST_STORY.getZeroBased());
        Story editedStory = new Story(new StoryName("Renamed Story"), storyToEdit.getTags(), storyToEdit.getContacts());
        model.setStory(storyToEdit, editedStory);

        // Filter status and filtered contacts should be updated
        String expectedStatus = new ContactAssignedToStoryPredicate(editedStory).toString();
        assertEquals(expectedStatus, model.getContactFilterStatusProperty().getValue());
        assertEquals(1, model.getFilteredContactList().size());
        assertEquals(contact.getName(), model.getFilteredContactList().get(0).getName());
    }

    @Test
    public void execute_invalidStoryIndex_throwsCommandException() {
        Index invalidIndex = Index.fromOneBased(model.getFilteredStoryList().size() + 1);

        assertCommandFailure(
                new ListContactsCommand(invalidIndex),
                model,
                Messages.MESSAGE_INVALID_STORY_DISPLAYED_INDEX);
    }

    @Test
    public void equals() {
        ListContactsCommand listContacts = new ListContactsCommand();
        ListContactsCommand listContactsWithIndex = new ListContactsCommand(INDEX_FIRST_STORY);

        // same object -> returns true
        assertTrue(listContacts.equals(listContacts));

        // same values -> returns true
        assertTrue(listContacts.equals(new ListContactsCommand()));
        assertTrue(listContactsWithIndex.equals(new ListContactsCommand(INDEX_FIRST_STORY)));

        // different types -> returns false
        assertFalse(listContacts.equals(1));

        // null -> returns false
        assertFalse(listContacts.equals(null));

        // different indices -> returns false
        assertFalse(listContactsWithIndex.equals(new ListContactsCommand(INDEX_SECOND_STORY)));
        assertFalse(listContacts.equals(listContactsWithIndex));
    }

    @Test
    public void constructor_nullIndex_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> new ListContactsCommand(null));
    }

    @Test
    public void execute_nullModel_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> new ListContactsCommand().execute(null));
        assertThrows(NullPointerException.class, () -> new ListContactsCommand(INDEX_FIRST_STORY).execute(null));
    }

    @Test
    public void hashCodeMethod() {
        ListContactsCommand listContacts1 = new ListContactsCommand();
        ListContactsCommand listContacts2 = new ListContactsCommand();
        assertEquals(listContacts1.hashCode(), listContacts2.hashCode());

        ListContactsCommand listContactsWithIndex1 = new ListContactsCommand(INDEX_FIRST_STORY);
        ListContactsCommand listContactsWithIndex2 = new ListContactsCommand(INDEX_FIRST_STORY);
        assertEquals(listContactsWithIndex1.hashCode(), listContactsWithIndex2.hashCode());
    }

    @Test
    public void toStringMethod() {
        ListContactsCommand listContacts = new ListContactsCommand(INDEX_FIRST_STORY);
        String expected = ListContactsCommand.class.getCanonicalName()
                + "{storyIndex=Optional[" + INDEX_FIRST_STORY + "]}";
        assertEquals(expected, listContacts.toString());
    }
}
