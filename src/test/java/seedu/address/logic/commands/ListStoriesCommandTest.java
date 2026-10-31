package seedu.address.logic.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.logic.commands.CommandTestUtil.PREDICATE_SHOW_NO_STORIES;
import static seedu.address.logic.commands.CommandTestUtil.assertCommandFailure;
import static seedu.address.logic.commands.CommandTestUtil.assertCommandSuccess;
import static seedu.address.testutil.Assert.assertThrows;
import static seedu.address.testutil.TypicalContacts.getTypicalAddressBook;
import static seedu.address.testutil.TypicalIndexes.INDEX_FIRST_CONTACT;
import static seedu.address.testutil.TypicalIndexes.INDEX_FIRST_STORY;
import static seedu.address.testutil.TypicalIndexes.INDEX_SECOND_CONTACT;
import static seedu.address.testutil.TypicalStories.STORY_A;
import static seedu.address.testutil.TypicalStories.STORY_B;

import java.util.Collections;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import seedu.address.commons.core.index.Index;
import seedu.address.logic.Messages;
import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.model.UserPrefs;
import seedu.address.model.contact.Contact;
import seedu.address.model.story.Story;
import seedu.address.model.story.StoryAssignedToContactPredicate;
import seedu.address.testutil.ContactBuilder;

/**
 * Contains integration tests (interaction with the Model) and unit tests for ListStoriesCommand.
 */
public class ListStoriesCommandTest {

    private Model model;
    private Model expectedModel;

    @BeforeEach
    public void setUp() {
        model = new ModelManager(getTypicalAddressBook(), new UserPrefs());
        model.addStory(STORY_A);
        model.addStory(STORY_B);
        expectedModel = new ModelManager(model.getAddressBook(), new UserPrefs());
    }

    @Test
    public void execute_listIsNotFiltered_showsSameList() {
        String expectedMessage = String.format(ListStoriesCommand.MESSAGE_SUCCESS,
                expectedModel.getFilteredStoryList().size());
        assertCommandSuccess(new ListStoriesCommand(), model,
                expectedMessage, expectedModel);
    }

    @Test
    public void execute_listIsFiltered_showsEverything() {
        model.updateFilteredStoryList(PREDICATE_SHOW_NO_STORIES);

        String expectedMessage = String.format(ListStoriesCommand.MESSAGE_SUCCESS,
                expectedModel.getFilteredStoryList().size());
        assertCommandSuccess(new ListStoriesCommand(), model,
                expectedMessage, expectedModel);
    }

    @Test
    public void execute_validContactIndex_showsAssignedStories() {
        Contact contact = model.getFilteredContactList().get(
                INDEX_FIRST_CONTACT.getZeroBased());
        Story story = model.getFilteredStoryList().get(0);

        model.assignStory(contact, story);

        Contact assignedContact = model.getFilteredContactList().get(
                INDEX_FIRST_CONTACT.getZeroBased());

        expectedModel = new ModelManager(model.getAddressBook(), new UserPrefs());
        expectedModel.updateFilteredStoryList(new StoryAssignedToContactPredicate(assignedContact));

        String expectedMessage = String.format(ListStoriesCommand.MESSAGE_LIST_ASSIGNED_STORIES_SUCCESS,
                1, assignedContact.getName());

        assertCommandSuccess(
                new ListStoriesCommand(INDEX_FIRST_CONTACT),
                model, expectedMessage, expectedModel);

        assertEquals(1, model.getFilteredStoryList().size());
        assertEquals(story.getName(),
                model.getFilteredStoryList().get(0).getName());
    }

    @Test
    public void execute_validContactIndexNoAssignments_showsEmptyList() {
        Contact contact = model.getFilteredContactList().get(
                INDEX_FIRST_CONTACT.getZeroBased());

        expectedModel.updateFilteredStoryList(new StoryAssignedToContactPredicate(contact));

        String expectedMessage = String.format(ListStoriesCommand.MESSAGE_LIST_ASSIGNED_STORIES_SUCCESS,
                0, contact.getName());

        assertCommandSuccess(
                new ListStoriesCommand(INDEX_FIRST_CONTACT),
                model, expectedMessage, expectedModel);

        assertTrue(model.getFilteredStoryList().isEmpty());
    }

    @Test
    public void execute_editStoryAfterListStories_storyRemainsInFilteredList() throws Exception {
        Contact contact = model.getFilteredContactList().get(INDEX_FIRST_CONTACT.getZeroBased());
        Story story = model.getFilteredStoryList().get(INDEX_FIRST_STORY.getZeroBased());
        model.assignStory(contact, story);

        ListStoriesCommand listStoriesCommand = new ListStoriesCommand(INDEX_FIRST_CONTACT);
        listStoriesCommand.execute(model);
        assertEquals(1, model.getFilteredStoryList().size());

        // Edit the story's tags while under liststories filter
        Story storyToEdit = model.getFilteredStoryList().get(0);
        Story editedStory = new Story(storyToEdit.getName(), Collections.emptySet(), storyToEdit.getContacts());
        model.setStory(storyToEdit, editedStory);

        // The edited story must still be present in the filtered story list!
        assertEquals(1, model.getFilteredStoryList().size());
        assertEquals(editedStory.getName(), model.getFilteredStoryList().get(0).getName());
    }

    @Test
    public void execute_editContactNameAfterListStories_updatesFilterAndAssignedStoriesRemain() throws Exception {
        Contact contact = model.getFilteredContactList().get(INDEX_FIRST_CONTACT.getZeroBased());
        Story story = model.getFilteredStoryList().get(INDEX_FIRST_STORY.getZeroBased());
        model.assignStory(contact, story);

        ListStoriesCommand listStoriesCommand = new ListStoriesCommand(INDEX_FIRST_CONTACT);
        listStoriesCommand.execute(model);
        assertEquals(1, model.getFilteredStoryList().size());

        // Edit the contact's name
        Contact contactToEdit = model.getFilteredContactList().get(INDEX_FIRST_CONTACT.getZeroBased());
        Contact editedContact = new ContactBuilder(contactToEdit).withName("Renamed Contact").build();
        model.setContact(contactToEdit, editedContact);

        // Filter status and filtered stories should be updated
        String expectedStatus = new StoryAssignedToContactPredicate(editedContact).toString();
        assertEquals(expectedStatus, model.getStoryFilterStatusProperty().getValue());
        assertEquals(1, model.getFilteredStoryList().size());
        assertEquals(story.getName(), model.getFilteredStoryList().get(0).getName());
    }

    @Test
    public void execute_invalidContactIndex_throwsCommandException() {
        Index invalidIndex = Index.fromOneBased(
                model.getFilteredContactList().size() + 1);

        assertCommandFailure(
                new ListStoriesCommand(invalidIndex),
                model,
                Messages.MESSAGE_INVALID_CONTACT_DISPLAYED_INDEX);
    }

    @Test
    public void equals() {
        ListStoriesCommand listStories = new ListStoriesCommand();
        ListStoriesCommand listStoriesWithIndex = new ListStoriesCommand(INDEX_FIRST_CONTACT);

        // same object -> returns true
        assertTrue(listStories.equals(listStories));

        // same values -> returns true
        assertTrue(listStories.equals(new ListStoriesCommand()));
        assertTrue(listStoriesWithIndex.equals(new ListStoriesCommand(INDEX_FIRST_CONTACT)));

        // different types -> returns false
        assertFalse(listStories.equals(1));

        // null -> returns false
        assertFalse(listStories.equals(null));

        // different indices -> returns false
        assertFalse(listStoriesWithIndex.equals(new ListStoriesCommand(INDEX_SECOND_CONTACT)));
        assertFalse(listStories.equals(listStoriesWithIndex));
    }

    @Test
    public void constructor_nullIndex_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> new ListStoriesCommand(null));
    }

    @Test
    public void execute_nullModel_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> new ListStoriesCommand().execute(null));
        assertThrows(NullPointerException.class, () -> new ListStoriesCommand(INDEX_FIRST_CONTACT).execute(null));
    }

    @Test
    public void hashCodeMethod() {
        ListStoriesCommand listStories1 = new ListStoriesCommand();
        ListStoriesCommand listStories2 = new ListStoriesCommand();
        assertEquals(listStories1.hashCode(), listStories2.hashCode());

        ListStoriesCommand listStoriesWithIndex1 = new ListStoriesCommand(INDEX_FIRST_CONTACT);
        ListStoriesCommand listStoriesWithIndex2 = new ListStoriesCommand(INDEX_FIRST_CONTACT);
        assertEquals(listStoriesWithIndex1.hashCode(), listStoriesWithIndex2.hashCode());
    }

    @Test
    public void toStringMethod() {
        ListStoriesCommand listStories = new ListStoriesCommand(INDEX_FIRST_CONTACT);
        String expected = ListStoriesCommand.class.getCanonicalName()
                + "{contactIndex=Optional[" + INDEX_FIRST_CONTACT + "]}";
        assertEquals(expected, listStories.toString());
    }
}
