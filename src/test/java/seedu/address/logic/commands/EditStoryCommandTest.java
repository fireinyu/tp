package seedu.address.logic.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.logic.Messages.MESSAGE_INVALID_STORY_DISPLAYED_INDEX;
import static seedu.address.logic.commands.CommandTestUtil.DESC_CRIME;
import static seedu.address.logic.commands.CommandTestUtil.DESC_FRAUD;
import static seedu.address.logic.commands.CommandTestUtil.VALID_STORYNAME_CRIME;
import static seedu.address.logic.commands.CommandTestUtil.VALID_STORYNAME_FRAUD;
import static seedu.address.logic.commands.CommandTestUtil.VALID_STORYTAG_MALAYSIA;
import static seedu.address.logic.commands.CommandTestUtil.assertCommandFailure;
import static seedu.address.logic.commands.CommandTestUtil.assertCommandSuccess;
import static seedu.address.logic.commands.CommandTestUtil.showStoryAtIndex;
import static seedu.address.logic.commands.EditStoryCommand.MESSAGE_DUPLICATE_STORY;
import static seedu.address.logic.commands.EditStoryCommand.MESSAGE_EDIT_STORY_SUCCESS;
import static seedu.address.testutil.TypicalIndexes.INDEX_FIRST_STORY;
import static seedu.address.testutil.TypicalIndexes.INDEX_SECOND_STORY;
import static seedu.address.testutil.TypicalStories.getTypicalAddressBook;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.Test;

import seedu.address.commons.core.index.Index;
import seedu.address.logic.Messages;
import seedu.address.logic.commands.CommandTestUtil.StoryNameEqualsPredicate;
import seedu.address.logic.commands.EditStoryCommand.EditStoryDescriptor;
import seedu.address.model.AddressBook;
import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.model.UserPrefs;
import seedu.address.model.contact.Contact;
import seedu.address.model.contact.ContactAssignedToStoryPredicate;
import seedu.address.model.story.Story;
import seedu.address.model.story.StoryName;
import seedu.address.model.tag.Tag;
import seedu.address.testutil.ContactBuilder;
import seedu.address.testutil.EditStoryDescriptorBuilder;
import seedu.address.testutil.StoryBuilder;

/**
 * Contains integration tests (interaction with the Model) and unit tests for EditStoryCommand.
 */
public class EditStoryCommandTest {

    private Model model = new ModelManager(getTypicalAddressBook(), new UserPrefs());

    @Test
    public void execute_allFieldsSpecifiedUnfilteredList_success() {
        Story editedStory = new StoryBuilder().build();
        EditStoryDescriptor descriptor = new EditStoryDescriptorBuilder(editedStory).build();
        EditStoryCommand editStoryCommand = new EditStoryCommand(INDEX_FIRST_STORY, descriptor);

        String expectedMessage =
                String.format(MESSAGE_EDIT_STORY_SUCCESS, Messages.format(editedStory));

        Model expectedModel = new ModelManager(new AddressBook(model.getAddressBook()), new UserPrefs());
        expectedModel.setStory(model.getFilteredStoryList().get(0), editedStory);

        assertCommandSuccess(editStoryCommand, model, expectedMessage, expectedModel);
    }

    @Test
    public void execute_onlyNameFieldSpecifiedUnfilteredList_success() {
        Index indexLastStory = Index.fromOneBased(model.getFilteredStoryList().size());
        Story lastStory = model.getFilteredStoryList().get(indexLastStory.getZeroBased());

        StoryBuilder storyInList = new StoryBuilder(lastStory);
        Story editedStory = storyInList.withStoryName(VALID_STORYNAME_CRIME).build();

        EditStoryDescriptor descriptor = new EditStoryDescriptorBuilder().withName(VALID_STORYNAME_CRIME).build();
        EditStoryCommand editStoryCommand = new EditStoryCommand(indexLastStory, descriptor);

        String expectedMessage =
                String.format(MESSAGE_EDIT_STORY_SUCCESS, Messages.format(editedStory));

        Model expectedModel = new ModelManager(new AddressBook(model.getAddressBook()), new UserPrefs());
        expectedModel.setStory(lastStory, editedStory);

        assertCommandSuccess(editStoryCommand, model, expectedMessage, expectedModel);
    }

    @Test
    public void execute_onlyTagFieldSpecifiedUnfilteredList_success() {
        Index indexLastStory = Index.fromOneBased(model.getFilteredStoryList().size());
        Story lastStory = model.getFilteredStoryList().get(indexLastStory.getZeroBased());

        StoryBuilder storyInList = new StoryBuilder(lastStory);
        Story editedStory = storyInList.withTags(VALID_STORYTAG_MALAYSIA).build();

        EditStoryDescriptor descriptor = new EditStoryDescriptorBuilder().withTags(VALID_STORYTAG_MALAYSIA).build();
        EditStoryCommand editStoryCommand = new EditStoryCommand(indexLastStory, descriptor);

        String expectedMessage =
                String.format(MESSAGE_EDIT_STORY_SUCCESS, Messages.format(editedStory));

        Model expectedModel = new ModelManager(new AddressBook(model.getAddressBook()), new UserPrefs());
        expectedModel.setStory(lastStory, editedStory);

        assertCommandSuccess(editStoryCommand, model, expectedMessage, expectedModel);
    }

    @Test
    public void execute_noFieldSpecifiedUnfilteredList_success() {
        EditStoryCommand editStoryCommand = new EditStoryCommand(
                INDEX_FIRST_STORY,
                new EditStoryDescriptor()
        );
        Story editedStory = model.getFilteredStoryList().get(INDEX_FIRST_STORY.getZeroBased());

        String expectedMessage =
                String.format(MESSAGE_EDIT_STORY_SUCCESS, Messages.format(editedStory));

        Model expectedModel = new ModelManager(new AddressBook(model.getAddressBook()), new UserPrefs());

        assertCommandSuccess(editStoryCommand, model, expectedMessage, expectedModel);
    }

    @Test
    public void execute_filteredList_success() {
        showStoryAtIndex(model, INDEX_FIRST_STORY);

        Story storyInFilteredList = model.getFilteredStoryList().get(INDEX_FIRST_STORY.getZeroBased());
        Story editedStory = new StoryBuilder(storyInFilteredList).withStoryName(VALID_STORYNAME_CRIME).build();
        EditStoryCommand editStoryCommand = new EditStoryCommand(INDEX_FIRST_STORY,
                new EditStoryDescriptorBuilder().withName(VALID_STORYNAME_CRIME).build());

        String expectedMessage =
                String.format(EditStoryCommand.MESSAGE_EDIT_STORY_SUCCESS, Messages.format(editedStory));

        Model expectedModel = new ModelManager(new AddressBook(model.getAddressBook()), new UserPrefs());
        showStoryAtIndex(expectedModel, INDEX_FIRST_STORY);
        expectedModel.setStory(model.getFilteredStoryList().get(0), editedStory);

        assertCommandSuccess(editStoryCommand, model, expectedMessage, expectedModel);
    }

    @Test
    public void execute_filteredList_preservesMatchingStory() {
        Story storyInFilteredList = model.getFilteredStoryList().get(INDEX_FIRST_STORY.getZeroBased());
        model.updateFilteredStoryList(new StoryNameEqualsPredicate(storyInFilteredList.getName()));
        assertEquals(1, model.getFilteredStoryList().size());

        Story editedStory = new StoryBuilder(storyInFilteredList).withTags(VALID_STORYTAG_MALAYSIA).build();
        EditStoryCommand editStoryCommand = new EditStoryCommand(INDEX_FIRST_STORY,
                new EditStoryDescriptorBuilder().withTags(VALID_STORYTAG_MALAYSIA).build());

        String expectedMessage =
                String.format(EditStoryCommand.MESSAGE_EDIT_STORY_SUCCESS, Messages.format(editedStory));

        Model expectedModel = new ModelManager(new AddressBook(model.getAddressBook()), new UserPrefs());
        expectedModel.updateFilteredStoryList(new StoryNameEqualsPredicate(storyInFilteredList.getName()));
        expectedModel.setStory(model.getFilteredStoryList().get(0), editedStory);

        assertCommandSuccess(editStoryCommand, model, expectedMessage, expectedModel);
        assertEquals(List.of(editedStory), model.getFilteredStoryList());
    }

    @Test
    public void execute_duplicateStoryUnfilteredList_failure() {
        Story firstStory = model.getFilteredStoryList().get(INDEX_FIRST_STORY.getZeroBased());
        EditStoryDescriptor descriptor = new EditStoryDescriptorBuilder(firstStory).build();
        EditStoryCommand editStoryCommand = new EditStoryCommand(INDEX_SECOND_STORY, descriptor);

        assertCommandFailure(editStoryCommand, model, MESSAGE_DUPLICATE_STORY);
    }

    @Test
    public void execute_duplicateStoryFilteredList_failure() {
        showStoryAtIndex(model, INDEX_FIRST_STORY);

        // edit story in filtered list into a duplicate in address book
        Story storyInList = model.getAddressBook().getStoryList().get(INDEX_SECOND_STORY.getZeroBased());
        EditStoryCommand editStoryCommand = new EditStoryCommand(INDEX_FIRST_STORY,
                new EditStoryDescriptorBuilder(storyInList).build());

        assertCommandFailure(editStoryCommand, model, MESSAGE_DUPLICATE_STORY);
    }

    @Test
    public void execute_invalidStoryIndexUnfilteredList_failure() {
        Index outOfBoundIndex = Index.fromOneBased(model.getFilteredStoryList().size() + 1);
        EditStoryDescriptor descriptor = new EditStoryDescriptorBuilder().withName(VALID_STORYNAME_FRAUD).build();
        EditStoryCommand editStoryCommand = new EditStoryCommand(outOfBoundIndex, descriptor);

        assertCommandFailure(editStoryCommand, model, MESSAGE_INVALID_STORY_DISPLAYED_INDEX);
    }

    /**
     * Edit filtered list where index is larger than size of filtered list,
     * but smaller than size of address book
     */
    @Test
    public void execute_invalidStoryIndexFilteredList_failure() {
        showStoryAtIndex(model, INDEX_FIRST_STORY);
        Index outOfBoundIndex = INDEX_SECOND_STORY;
        // ensures that outOfBoundIndex is still in bounds of address book list
        assertTrue(outOfBoundIndex.getZeroBased() < model.getAddressBook().getStoryList().size());

        EditStoryCommand editStoryCommand = new EditStoryCommand(outOfBoundIndex,
                new EditStoryDescriptorBuilder().withName(VALID_STORYNAME_CRIME).build());

        assertCommandFailure(editStoryCommand, model, MESSAGE_INVALID_STORY_DISPLAYED_INDEX);
    }

    @Test
    public void equals() {
        final EditStoryCommand standardCommand = new EditStoryCommand(INDEX_FIRST_STORY, DESC_CRIME);

        // same values -> returns true
        EditStoryDescriptor copyDescriptor = new EditStoryDescriptor(DESC_CRIME);
        EditStoryCommand commandWithSameValues = new EditStoryCommand(INDEX_FIRST_STORY, copyDescriptor);
        assertTrue(standardCommand.equals(commandWithSameValues));

        // same object -> returns true
        assertTrue(standardCommand.equals(standardCommand));

        // null -> returns false
        assertFalse(standardCommand.equals(null));

        // different types -> returns false
        assertFalse(standardCommand.equals(new ClearCommand()));

        // different index -> returns false
        assertFalse(standardCommand.equals(new EditStoryCommand(INDEX_SECOND_STORY, DESC_CRIME)));

        // different descriptor -> returns false
        assertFalse(standardCommand.equals(new EditStoryCommand(INDEX_FIRST_STORY, DESC_FRAUD)));
    }

    @Test
    public void toStringMethod() {
        Index index = Index.fromOneBased(1);
        EditStoryDescriptor editStoryDescriptor = new EditStoryDescriptor();
        EditStoryCommand editStoryCommand = new EditStoryCommand(index, editStoryDescriptor);
        String expected = EditStoryCommand.class.getCanonicalName() + "{index=" + index + ", editStoryDescriptor="
                + editStoryDescriptor + "}";
        assertEquals(expected, editStoryCommand.toString());
    }

    @Test
    public void execute_resetTagsOnly_success() {
        Story storyToEdit = model.getFilteredStoryList().get(INDEX_FIRST_STORY.getZeroBased());
        Story editedStory = new Story(storyToEdit.getName(), new HashSet<>());

        EditStoryDescriptor descriptor = new EditStoryDescriptorBuilder().withTags().build();
        EditStoryCommand editStoryCommand = new EditStoryCommand(INDEX_FIRST_STORY, descriptor);

        String expectedMessage = String.format(EditStoryCommand.MESSAGE_EDIT_STORY_SUCCESS,
                Messages.format(editedStory));

        Model expectedModel = new ModelManager(new AddressBook(model.getAddressBook()), new UserPrefs());
        expectedModel.setStory(storyToEdit, editedStory);

        assertCommandSuccess(editStoryCommand, model, expectedMessage, expectedModel);
    }

    @Test
    public void execute_bothNameAndTags_success() {
        Story storyToEdit = model.getFilteredStoryList().get(INDEX_FIRST_STORY.getZeroBased());
        Story editedStory = new Story(new StoryName("Updated Unique Story"), Set.of(new Tag("urgent")));

        EditStoryDescriptor descriptor = new EditStoryDescriptorBuilder()
                .withName("Updated Unique Story").withTags("urgent").build();
        EditStoryCommand editStoryCommand = new EditStoryCommand(INDEX_FIRST_STORY, descriptor);

        String expectedMessage = String.format(EditStoryCommand.MESSAGE_EDIT_STORY_SUCCESS,
                Messages.format(editedStory));

        Model expectedModel = new ModelManager(new AddressBook(model.getAddressBook()), new UserPrefs());
        expectedModel.setStory(storyToEdit, editedStory);

        assertCommandSuccess(editStoryCommand, model, expectedMessage, expectedModel);
    }

    @Test
    public void execute_boundaryMaxIntegerIndex_failure() {
        Index maxIntIndex = Index.fromOneBased(Integer.MAX_VALUE);
        EditStoryDescriptor descriptor = new EditStoryDescriptorBuilder().withName(VALID_STORYNAME_FRAUD).build();
        EditStoryCommand editStoryCommand = new EditStoryCommand(maxIntIndex, descriptor);

        assertCommandFailure(editStoryCommand, model, MESSAGE_INVALID_STORY_DISPLAYED_INDEX);
    }

    @Test
    public void execute_editStoryName_updatesContactFilter() {
        Story storyToEdit = model.getFilteredStoryList().get(INDEX_FIRST_STORY.getZeroBased());
        Contact contact = new ContactBuilder().withName("Alice Pauline").build();
        model.addContact(contact);
        model.assignStory(contact, storyToEdit);

        Story assignedStory = model.getFilteredStoryList().get(INDEX_FIRST_STORY.getZeroBased());

        model.updateFilteredContactList(new ContactAssignedToStoryPredicate(assignedStory));

        EditStoryDescriptor descriptor = new EditStoryDescriptorBuilder().withName(VALID_STORYNAME_FRAUD).build();
        EditStoryCommand editStoryCommand = new EditStoryCommand(INDEX_FIRST_STORY, descriptor);

        Story editedStory = new Story(new StoryName(VALID_STORYNAME_FRAUD),
                assignedStory.getTags(), assignedStory.getContacts());
        String expectedMessage = String.format(EditStoryCommand.MESSAGE_EDIT_STORY_SUCCESS,
                Messages.format(editedStory));

        Model expectedModel = new ModelManager(new AddressBook(model.getAddressBook()), new UserPrefs());
        expectedModel.updateFilteredContactList(new ContactAssignedToStoryPredicate(assignedStory));
        expectedModel.setStory(assignedStory, editedStory);

        assertCommandSuccess(editStoryCommand, model, expectedMessage, expectedModel);

        String expectedStatus = new ContactAssignedToStoryPredicate(editedStory).toString();
        assertEquals(expectedStatus, model.getContactFilterStatusProperty().getValue());
        assertEquals(1, model.getFilteredContactList().size());
    }
}
