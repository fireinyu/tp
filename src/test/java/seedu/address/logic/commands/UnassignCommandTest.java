package seedu.address.logic.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.logic.commands.CommandTestUtil.assertCommandFailure;
import static seedu.address.logic.commands.CommandTestUtil.assertCommandSuccess;
import static seedu.address.logic.commands.CommandTestUtil.showContactAtIndex;
import static seedu.address.logic.commands.CommandTestUtil.showStoryAtIndex;
import static seedu.address.testutil.Assert.assertThrows;
import static seedu.address.testutil.TypicalContacts.getTypicalAddressBook;
import static seedu.address.testutil.TypicalIndexes.INDEX_FIRST_CONTACT;
import static seedu.address.testutil.TypicalIndexes.INDEX_FIRST_STORY;
import static seedu.address.testutil.TypicalIndexes.INDEX_SECOND_CONTACT;
import static seedu.address.testutil.TypicalIndexes.INDEX_SECOND_STORY;
import static seedu.address.testutil.TypicalStories.STORY_A;
import static seedu.address.testutil.TypicalStories.STORY_B;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import seedu.address.commons.core.index.Index;
import seedu.address.logic.Messages;
import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.model.UserPrefs;
import seedu.address.model.contact.Contact;
import seedu.address.model.story.Story;

public class UnassignCommandTest {

    private static final Index STORY_INDEX = Index.fromOneBased(1);
    private static final Index CONTACT_INDEX = Index.fromOneBased(2);

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
    public void constructor_nullIndex_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> new UnassignCommand(null, CONTACT_INDEX));
        assertThrows(NullPointerException.class, () -> new UnassignCommand(STORY_INDEX, null));
    }

    @Test
    public void execute_nullModel_throwsNullPointerException() {
        UnassignCommand command = new UnassignCommand(STORY_INDEX, CONTACT_INDEX);
        assertThrows(NullPointerException.class, () -> command.execute(null));
    }

    @Test
    public void execute_validIndicesAssignedContactUnfilteredList_success() {
        Contact contact = model.getFilteredContactList().get(INDEX_FIRST_CONTACT.getZeroBased());
        Story story = model.getFilteredStoryList().get(INDEX_FIRST_STORY.getZeroBased());

        model.assignStory(contact, story);
        expectedModel = new ModelManager(model.getAddressBook(), new UserPrefs());

        UnassignCommand unassignCommand = new UnassignCommand(INDEX_FIRST_STORY, INDEX_FIRST_CONTACT);
        expectedModel.unassignStory(contact, story);

        String expectedMessage = String.format(UnassignCommand.MESSAGE_SUCCESS, contact.getName(), story.getName());
        assertCommandSuccess(unassignCommand, model, expectedMessage, expectedModel);
    }

    @Test
    public void execute_validIndicesFilteredList_success() {
        Contact contact = model.getFilteredContactList().get(INDEX_SECOND_CONTACT.getZeroBased());
        Story story = model.getFilteredStoryList().get(INDEX_SECOND_STORY.getZeroBased());

        model.assignStory(contact, story);
        expectedModel = new ModelManager(model.getAddressBook(), new UserPrefs());

        showContactAtIndex(model, INDEX_SECOND_CONTACT);
        showStoryAtIndex(model, INDEX_SECOND_STORY);

        UnassignCommand unassignCommand = new UnassignCommand(Index.fromOneBased(1), Index.fromOneBased(1));
        showContactAtIndex(expectedModel, INDEX_SECOND_CONTACT);
        showStoryAtIndex(expectedModel, INDEX_SECOND_STORY);
        expectedModel.unassignStory(contact, story);

        String expectedMessage = String.format(UnassignCommand.MESSAGE_SUCCESS, contact.getName(), story.getName());
        assertCommandSuccess(unassignCommand, model, expectedMessage, expectedModel);
    }

    @Test
    public void execute_invalidContactIndexUnfilteredList_throwsCommandException() {
        Index outOfBoundContactIndex = Index.fromOneBased(model.getFilteredContactList().size() + 1);
        UnassignCommand unassignCommand = new UnassignCommand(INDEX_FIRST_STORY, outOfBoundContactIndex);

        assertCommandFailure(unassignCommand, model, Messages.MESSAGE_INVALID_CONTACT_DISPLAYED_INDEX);
    }

    @Test
    public void execute_invalidContactIndexFilteredList_throwsCommandException() {
        showContactAtIndex(model, INDEX_FIRST_CONTACT);
        Index outOfBoundContactIndex = INDEX_SECOND_CONTACT;
        // ensures outOfBoundContactIndex is in address book, but out of bounds in filtered contact list
        assertTrue(outOfBoundContactIndex.getZeroBased() < model.getAddressBook().getContactList().size());

        UnassignCommand unassignCommand = new UnassignCommand(INDEX_FIRST_STORY, outOfBoundContactIndex);
        assertCommandFailure(unassignCommand, model, Messages.MESSAGE_INVALID_CONTACT_DISPLAYED_INDEX);
    }

    @Test
    public void execute_invalidStoryIndexUnfilteredList_throwsCommandException() {
        Index outOfBoundStoryIndex = Index.fromOneBased(model.getFilteredStoryList().size() + 1);
        UnassignCommand unassignCommand = new UnassignCommand(outOfBoundStoryIndex, INDEX_FIRST_CONTACT);

        assertCommandFailure(unassignCommand, model, Messages.MESSAGE_INVALID_STORY_DISPLAYED_INDEX);
    }

    @Test
    public void execute_invalidStoryIndexFilteredList_throwsCommandException() {
        showStoryAtIndex(model, INDEX_FIRST_STORY);
        Index outOfBoundStoryIndex = INDEX_SECOND_STORY;
        // ensures outOfBoundStoryIndex is in address book, but out of bounds in filtered story list
        assertTrue(outOfBoundStoryIndex.getZeroBased() < model.getAddressBook().getStoryList().size());

        UnassignCommand unassignCommand = new UnassignCommand(outOfBoundStoryIndex, INDEX_FIRST_CONTACT);
        assertCommandFailure(unassignCommand, model, Messages.MESSAGE_INVALID_STORY_DISPLAYED_INDEX);
    }

    @Test
    public void execute_bothIndicesOutOfRange_throwsCommandException() {
        Index outOfBoundContactIndex = Index.fromOneBased(model.getFilteredContactList().size() + 1);
        Index outOfBoundStoryIndex = Index.fromOneBased(model.getFilteredStoryList().size() + 1);
        UnassignCommand unassignCommand = new UnassignCommand(outOfBoundStoryIndex, outOfBoundContactIndex);

        assertCommandFailure(unassignCommand, model, Messages.MESSAGE_INVALID_CONTACT_DISPLAYED_INDEX);
    }

    @Test
    public void execute_notCurrentlyAssignedContactAndStory_throwsCommandException() {
        Contact contact = model.getFilteredContactList().get(INDEX_FIRST_CONTACT.getZeroBased());
        Story story = model.getFilteredStoryList().get(INDEX_FIRST_STORY.getZeroBased());

        UnassignCommand unassignCommand = new UnassignCommand(INDEX_FIRST_STORY, INDEX_FIRST_CONTACT);

        String expectedMessage = String.format(UnassignCommand.MESSAGE_NOT_ASSIGNED,
                contact.getName(), story.getName());
        assertCommandFailure(unassignCommand, model, expectedMessage);
    }

    @Test
    public void execute_boundaryMaxIntegerIndices_failure() {
        Index maxIndex = Index.fromOneBased(Integer.MAX_VALUE);

        // Max contact index
        UnassignCommand maxContactCommand = new UnassignCommand(INDEX_FIRST_STORY, maxIndex);
        assertCommandFailure(maxContactCommand, model, Messages.MESSAGE_INVALID_CONTACT_DISPLAYED_INDEX);

        // Max story index
        UnassignCommand maxStoryCommand = new UnassignCommand(maxIndex, INDEX_FIRST_CONTACT);
        assertCommandFailure(maxStoryCommand, model, Messages.MESSAGE_INVALID_STORY_DISPLAYED_INDEX);
    }

    @Test
    public void equals() {
        UnassignCommand command = new UnassignCommand(STORY_INDEX, CONTACT_INDEX);

        assertTrue(command.equals(command));
        assertTrue(command.equals(new UnassignCommand(STORY_INDEX, CONTACT_INDEX)));
        assertFalse(command.equals(new UnassignCommand(Index.fromOneBased(2), CONTACT_INDEX)));
        assertFalse(command.equals(new UnassignCommand(STORY_INDEX, Index.fromOneBased(1))));
        assertFalse(command.equals(null));
        assertFalse(command.equals(new Object()));
    }

    @Test
    public void toStringMethod() {
        UnassignCommand command = new UnassignCommand(STORY_INDEX, CONTACT_INDEX);
        String expected = UnassignCommand.class.getCanonicalName()
                + "{storyIndex=" + STORY_INDEX + ", contactIndex=" + CONTACT_INDEX + "}";

        assertEquals(expected, command.toString());
    }
}
