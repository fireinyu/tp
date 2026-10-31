package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.testutil.Assert.assertThrows;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;

import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import seedu.address.commons.core.GuiSettings;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.AddressBook;
import seedu.address.model.Model;
import seedu.address.model.ReadOnlyAddressBook;
import seedu.address.model.ReadOnlyUserPrefs;
import seedu.address.model.StateAwarePredicate;
import seedu.address.model.contact.Contact;
import seedu.address.model.followup.Followup;
import seedu.address.model.story.Story;
import seedu.address.model.story.StoryName;

public class AddStoryCommandTest {

    @Test
    public void constructor_nullStory_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> new AddStoryCommand(null));
    }

    @Test
    public void execute_storyAcceptedByModel_addSuccessful() throws Exception {
        ModelStubAcceptingStoryAdded modelStub = new ModelStubAcceptingStoryAdded();
        Story validStory = new Story(new StoryName("Election News"), new HashSet<>());

        CommandResult commandResult = new AddStoryCommand(validStory).execute(modelStub);

        assertEquals(String.format(AddStoryCommand.MESSAGE_SUCCESS, validStory.getName()),
                commandResult.getFeedbackToUser());
        assertEquals(List.of(validStory), modelStub.storiesAdded);
    }

    @Test
    public void execute_duplicateStory_throwsCommandException() {
        Story validStory = new Story(new StoryName("Election News"), new HashSet<>());
        AddStoryCommand addStoryCommand = new AddStoryCommand(validStory);
        ModelStub modelStub = new ModelStubWithStory(validStory);

        assertThrows(CommandException.class,
                String.format(AddStoryCommand.MESSAGE_DUPLICATE_STORY, validStory.getName()), () ->
                addStoryCommand.execute(modelStub));
    }

    @Test
    public void equals() {
        Story storyA = new Story(new StoryName("Story A"), new HashSet<>());
        Story storyB = new Story(new StoryName("Story B"), new HashSet<>());
        AddStoryCommand addStoryACommand = new AddStoryCommand(storyA);
        AddStoryCommand addStoryBCommand = new AddStoryCommand(storyB);

        // same object -> returns true
        assertTrue(addStoryACommand.equals(addStoryACommand));

        // same values -> returns true
        AddStoryCommand addStoryACommandCopy = new AddStoryCommand(storyA);
        assertTrue(addStoryACommand.equals(addStoryACommandCopy));

        // different types -> returns false
        assertFalse(addStoryACommand.equals(1));

        // null -> returns false
        assertFalse(addStoryACommand.equals(null));

        // different story -> returns false
        assertFalse(addStoryACommand.equals(addStoryBCommand));
    }

    @Test
    public void toStringMethod() {
        Story story = new Story(new StoryName("Story A"), new HashSet<>());
        AddStoryCommand addStoryCommand = new AddStoryCommand(story);
        String expected = AddStoryCommand.class.getCanonicalName() + "{toAdd=" + story + "}";
        assertEquals(expected, addStoryCommand.toString());
    }

    /**
     * A default model stub that has all of the methods failing.
     */
    private class ModelStub implements Model {
        @Override
        public ReadOnlyUserPrefs getUserPrefs() {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public GuiSettings getGuiSettings() {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public void setGuiSettings(GuiSettings guiSettings) {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public Map<String, String> getShortcuts() {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public void assignShortcut(String shortcut, String commandWord) {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public boolean removeShortcut(String shortcut) {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public void addContact(Contact contact) {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public void setAddressBook(ReadOnlyAddressBook newData) {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public ReadOnlyAddressBook getAddressBook() {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public boolean hasContact(Contact contact) {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public void deleteContact(Contact target) {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public void setContact(Contact target, Contact editedContact) {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public boolean hasStory(Story story) {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public void addStory(Story story) {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public void setStory(Story target, Story editedStory) {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public void assignStory(Contact target, Story story) {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public void unassignStory(Contact target, Story story) {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public boolean hasFollowup(Followup followup) {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public void addFollowup(Followup followup) {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public void setFollowup(Followup target, Followup editedFollowup) {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public void deleteFollowup(Followup followup) {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public ObservableList<Contact> getFilteredContactList() {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public ObservableList<Story> getFilteredStoryList() {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public ObservableList<Followup> getFilteredFollowupList() {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public void updateFilteredContactList(StateAwarePredicate<Contact, Story> predicate) {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public void updateFilteredStoryList(StateAwarePredicate<Story, Contact> predicate) {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public void updateFilteredFollowupList(StateAwarePredicate<Followup, Contact> predicate) {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public ObservableValue<String> getContactFilterStatusProperty() {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public ObservableValue<String> getStoryFilterStatusProperty() {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public ObservableValue<String> getFollowupFilterStatusProperty() {
            throw new AssertionError("This method should not be called.");
        }
    }

    /**
     * A Model stub that contains a single story.
     */
    private class ModelStubWithStory extends ModelStub {
        private final Story story;

        ModelStubWithStory(Story story) {
            requireNonNull(story);
            this.story = story;
        }

        @Override
        public boolean hasStory(Story story) {
            requireNonNull(story);
            return this.story.isSameStory(story);
        }
    }

    /**
     * A Model stub that always accepts the story being added.
     */
    private class ModelStubAcceptingStoryAdded extends ModelStub {
        final ArrayList<Story> storiesAdded = new ArrayList<>();

        @Override
        public boolean hasStory(Story story) {
            requireNonNull(story);
            return storiesAdded.stream().anyMatch(story::isSameStory);
        }

        @Override
        public void addStory(Story story) {
            requireNonNull(story);
            storiesAdded.add(story);
        }

        @Override
        public ReadOnlyAddressBook getAddressBook() {
            return new AddressBook();
        }
    }

}
