package seedu.address.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.logic.commands.CommandTestUtil.PREDICATE_SHOW_NO_FOLLOWUPS;
import static seedu.address.model.Model.PREDICATE_SHOW_ALL_CONTACTS;
import static seedu.address.model.Model.PREDICATE_SHOW_ALL_FOLLOWUPS;
import static seedu.address.model.Model.PREDICATE_SHOW_ALL_STORIES;
import static seedu.address.testutil.Assert.assertThrows;
import static seedu.address.testutil.TypicalContacts.ALICE;
import static seedu.address.testutil.TypicalContacts.BENSON;
import static seedu.address.testutil.TypicalContacts.BOB;

import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.Test;

import seedu.address.commons.core.GuiSettings;
import seedu.address.model.contact.Contact;
import seedu.address.model.contact.ContactAssignedToStoryPredicate;
import seedu.address.model.contact.NameContainsKeywordsPredicate;
import seedu.address.model.contact.exceptions.ContactNotFoundException;
import seedu.address.model.followup.Followup;
import seedu.address.model.followup.FollowupDescription;
import seedu.address.model.followup.FollowupIsForContactPredicate;
import seedu.address.model.story.Story;
import seedu.address.model.story.StoryAssignedToContactPredicate;
import seedu.address.model.story.StoryName;
import seedu.address.model.story.exceptions.StoryNotFoundException;
import seedu.address.testutil.AddressBookBuilder;
import seedu.address.testutil.ContactBuilder;

public class ModelManagerTest {

    private ModelManager modelManager = new ModelManager();

    @Test
    public void constructor() {
        assertEquals(new UserPrefs(), modelManager.getUserPrefs());
        assertEquals(new GuiSettings(), modelManager.getGuiSettings());
        assertEquals(new AddressBook(), new AddressBook(modelManager.getAddressBook()));
    }

    @Test
    public void constructor_validUserPrefs_copiesUserPrefs() {
        UserPrefs userPrefs = new UserPrefs();
        userPrefs.setGuiSettings(new GuiSettings(1, 2, 3, 4));
        modelManager = new ModelManager(new AddressBook(), userPrefs);
        assertEquals(userPrefs, modelManager.getUserPrefs());

        // Modifying userPrefs should not modify modelManager's userPrefs
        UserPrefs oldUserPrefs = new UserPrefs(userPrefs);
        userPrefs.setGuiSettings(new GuiSettings(5, 6, 7, 8));
        assertEquals(oldUserPrefs, modelManager.getUserPrefs());
    }

    @Test
    public void setGuiSettings_nullGuiSettings_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> modelManager.setGuiSettings(null));
    }

    @Test
    public void setGuiSettings_validGuiSettings_setsGuiSettings() {
        GuiSettings guiSettings = new GuiSettings(1, 2, 3, 4);
        modelManager.setGuiSettings(guiSettings);
        assertEquals(guiSettings, modelManager.getGuiSettings());
    }

    @Test
    public void hasContact_nullContact_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> modelManager.hasContact(null));
    }

    @Test
    public void hasContact_contactNotInAddressBook_returnsFalse() {
        assertFalse(modelManager.hasContact(ALICE));
    }

    @Test
    public void hasContact_contactInAddressBook_returnsTrue() {
        modelManager.addContact(ALICE);
        assertTrue(modelManager.hasContact(ALICE));
    }

    @Test
    public void removeContact_contactNotInAddressBook_throws() {
        modelManager.addContact(ALICE);
        assertThrows(ContactNotFoundException.class, () -> modelManager.deleteContact(BOB));
        assertTrue(modelManager.hasContact(ALICE));
        assertFalse(modelManager.hasContact(BOB));
    }

    @Test
    public void removeStory_storyNotInAddressBook_throws() {
        Story s = new Story(new StoryName("ant"), Set.of());
        Story s1 = new Story(new StoryName("bee"), Set.of());
        modelManager.addStory(s);
        assertThrows(StoryNotFoundException.class, () -> modelManager.deleteStory(s1));
        assertTrue(modelManager.hasStory(s));
        assertFalse(modelManager.hasStory(s1));
    }

    @Test
    public void getFilteredContactList_modifyList_throwsUnsupportedOperationException() {
        assertThrows(UnsupportedOperationException.class, () -> modelManager.getFilteredContactList().remove(0));
    }

    @Test
    public void getFilteredStoryList_modifyList_throwsUnsupportedOperationException() {
        assertThrows(UnsupportedOperationException.class, () -> modelManager.getFilteredStoryList().remove(0));
    }

    @Test
    public void getFilteredFollowupList_modifyList_throwsUnsupportedOperationException() {
        assertThrows(UnsupportedOperationException.class, () -> modelManager.getFilteredFollowupList().remove(0));
    }

    @Test
    public void updateFilteredContactList_validPredicate_filtersListAndUpdatesStatus() {
        AddressBook addressBook = new AddressBookBuilder().withContact(ALICE).withContact(BENSON).build();
        UserPrefs userPrefs = new UserPrefs();
        modelManager = new ModelManager(addressBook, userPrefs);

        assertEquals(List.of(ALICE, BENSON), modelManager.getFilteredContactList());
        assertEquals(Model.FILTER_STATUS_ALL, modelManager.getContactFilterStatusProperty().getValue());

        NameContainsKeywordsPredicate predicate = new NameContainsKeywordsPredicate(List.of("Alice"));
        modelManager.updateFilteredContactList(predicate);

        assertEquals(List.of(ALICE), modelManager.getFilteredContactList());
        assertEquals(predicate.toString(), modelManager.getContactFilterStatusProperty().getValue());

        modelManager.updateFilteredContactList(PREDICATE_SHOW_ALL_CONTACTS);
        assertEquals(List.of(ALICE, BENSON), modelManager.getFilteredContactList());
        assertEquals(Model.FILTER_STATUS_ALL, modelManager.getContactFilterStatusProperty().getValue());
    }

    @Test
    public void updateFilteredStoryList_validPredicate_filtersListAndUpdatesStatus() {
        Story story1 = new Story(new StoryName("Alpha"), Set.of());
        Story story2 = new Story(new StoryName("Beta"), Set.of());
        modelManager.addStory(story1);
        modelManager.addStory(story2);

        assertEquals(List.of(story1, story2), modelManager.getFilteredStoryList());
        assertEquals(Model.FILTER_STATUS_ALL, modelManager.getStoryFilterStatusProperty().getValue());

        Contact contact = new ContactBuilder().withName("Alex").build();
        StoryAssignedToContactPredicate predicate = new StoryAssignedToContactPredicate(contact);
        modelManager.updateFilteredStoryList(predicate);

        assertEquals(List.of(), modelManager.getFilteredStoryList());
        assertEquals(predicate.toString(), modelManager.getStoryFilterStatusProperty().getValue());

        modelManager.updateFilteredStoryList(PREDICATE_SHOW_ALL_STORIES);
        assertEquals(List.of(story1, story2), modelManager.getFilteredStoryList());
        assertEquals(Model.FILTER_STATUS_ALL, modelManager.getStoryFilterStatusProperty().getValue());
    }

    @Test
    public void updateFilteredFollowupList_validPredicate_filtersListAndUpdatesStatus() {
        Followup followup1 = new Followup(new FollowupDescription("Call Alice"), ALICE.getName());
        Followup followup2 = new Followup(new FollowupDescription("Email Benson"), BENSON.getName());
        modelManager.addFollowup(followup1);
        modelManager.addFollowup(followup2);

        assertEquals(List.of(followup1, followup2), modelManager.getFilteredFollowupList());
        assertEquals(Model.FILTER_STATUS_ALL, modelManager.getFollowupFilterStatusProperty().getValue());

        FollowupIsForContactPredicate predicate = new FollowupIsForContactPredicate(ALICE);
        modelManager.updateFilteredFollowupList(predicate);

        assertEquals(List.of(followup1), modelManager.getFilteredFollowupList());
        assertEquals(predicate.toString(), modelManager.getFollowupFilterStatusProperty().getValue());

        modelManager.updateFilteredFollowupList(PREDICATE_SHOW_ALL_FOLLOWUPS);
        assertEquals(List.of(followup1, followup2), modelManager.getFilteredFollowupList());
        assertEquals(Model.FILTER_STATUS_ALL, modelManager.getFollowupFilterStatusProperty().getValue());
    }

    @Test
    public void equals() {
        AddressBook addressBook = new AddressBookBuilder().withContact(ALICE).withContact(BENSON).build();
        AddressBook differentAddressBook = new AddressBook();
        UserPrefs userPrefs = new UserPrefs();

        // same values -> returns true
        modelManager = new ModelManager(addressBook, userPrefs);
        ModelManager modelManagerCopy = new ModelManager(addressBook, userPrefs);
        assertTrue(modelManager.equals(modelManagerCopy));

        // same object -> returns true
        assertTrue(modelManager.equals(modelManager));

        // null -> returns false
        assertFalse(modelManager.equals(null));

        // different types -> returns false
        assertFalse(modelManager.equals(5));

        // different addressBook -> returns false
        assertFalse(modelManager.equals(new ModelManager(differentAddressBook, userPrefs)));

        // different filteredList -> returns false
        String[] keywords = ALICE.getName().toString().split("\\s+");
        modelManager.updateFilteredContactList(new NameContainsKeywordsPredicate(List.of(keywords)));
        assertFalse(modelManager.equals(new ModelManager(addressBook, userPrefs)));

        // resets modelManager to initial state for upcoming tests
        modelManager.updateFilteredContactList(PREDICATE_SHOW_ALL_CONTACTS);

        // different filteredStoryList -> returns false
        Story story = new Story(new StoryName("Crime Investigation"), Set.of());
        modelManager.addStory(story);
        ModelManager storyModelManager = new ModelManager(modelManager.getAddressBook(), userPrefs);
        modelManager.updateFilteredStoryList(new StoryAssignedToContactPredicate(ALICE));
        assertFalse(modelManager.equals(storyModelManager));
        modelManager.updateFilteredStoryList(PREDICATE_SHOW_ALL_STORIES);

        // different filteredFollowupList -> returns false
        Followup followup = new Followup(new FollowupDescription("Call"), ALICE.getName());
        modelManager.addFollowup(followup);
        ModelManager otherModelManager = new ModelManager(modelManager.getAddressBook(), userPrefs);
        modelManager.updateFilteredFollowupList(PREDICATE_SHOW_NO_FOLLOWUPS);
        assertFalse(modelManager.equals(otherModelManager));

        // different userPrefs -> returns false
        UserPrefs differentUserPrefs = new UserPrefs();
        differentUserPrefs.setGuiSettings(new GuiSettings(1, 2, 3, 4));
        assertFalse(modelManager.equals(new ModelManager(addressBook, differentUserPrefs)));
    }

    @Test
    public void getContactFilterStatusProperty_defaultAndUpdates_success() {
        ModelManager modelManager = new ModelManager();
        assertEquals(Model.FILTER_STATUS_ALL, modelManager.getContactFilterStatusProperty().getValue());

        Story story = new Story(new StoryName("Story"), Set.of());
        ContactAssignedToStoryPredicate predicate = new ContactAssignedToStoryPredicate(story);
        modelManager.updateFilteredContactList(predicate);
        assertEquals(predicate.toString(), modelManager.getContactFilterStatusProperty().getValue());

        modelManager.updateFilteredContactList(PREDICATE_SHOW_ALL_CONTACTS);
        assertEquals(Model.FILTER_STATUS_ALL, modelManager.getContactFilterStatusProperty().getValue());
    }

    @Test
    public void getStoryFilterStatusProperty_defaultAndUpdates_success() {
        ModelManager modelManager = new ModelManager();
        assertEquals(Model.FILTER_STATUS_ALL, modelManager.getStoryFilterStatusProperty().getValue());

        Contact contact = new ContactBuilder().withName("Alex").build();
        StoryAssignedToContactPredicate predicate = new StoryAssignedToContactPredicate(contact);
        modelManager.updateFilteredStoryList(predicate);
        assertEquals(predicate.toString(), modelManager.getStoryFilterStatusProperty().getValue());

        modelManager.updateFilteredStoryList(PREDICATE_SHOW_ALL_STORIES);
        assertEquals(Model.FILTER_STATUS_ALL, modelManager.getStoryFilterStatusProperty().getValue());
    }

    @Test
    public void getFollowupFilterStatusProperty_defaultAndUpdates_success() {
        ModelManager modelManager = new ModelManager();
        assertEquals(Model.FILTER_STATUS_ALL, modelManager.getFollowupFilterStatusProperty().getValue());

        Contact contact = new ContactBuilder().withName("Alex").build();
        FollowupIsForContactPredicate predicate = new FollowupIsForContactPredicate(contact);
        modelManager.updateFilteredFollowupList(predicate);
        assertEquals(predicate.toString(), modelManager.getFollowupFilterStatusProperty().getValue());

        modelManager.updateFilteredFollowupList(PREDICATE_SHOW_ALL_FOLLOWUPS);
        assertEquals(Model.FILTER_STATUS_ALL, modelManager.getFollowupFilterStatusProperty().getValue());
    }

    @Test
    public void setContact_nameChangedWhenFilteredByContact_updatesStoryAndFollowupFilters() {
        ModelManager modelManager = new ModelManager();
        Contact contact = new ContactBuilder().withName("Alice Pauline").build();
        Story story = new Story(new StoryName("Alpha Story"), Set.of(), List.of(contact.getName()));
        Followup followup = new Followup(new FollowupDescription("Call back"), contact.getName());

        modelManager.addContact(contact);
        modelManager.addStory(story);
        modelManager.addFollowup(followup);

        modelManager.updateFilteredStoryList(new StoryAssignedToContactPredicate(contact));
        modelManager.updateFilteredFollowupList(new FollowupIsForContactPredicate(contact));

        assertEquals(1, modelManager.getFilteredStoryList().size());
        assertEquals(1, modelManager.getFilteredFollowupList().size());

        Contact editedContact = new ContactBuilder(contact).withName("Alicia Pauline").build();
        modelManager.setContact(contact, editedContact);

        String expectedStoryStatus = new StoryAssignedToContactPredicate(editedContact).toString();
        String expectedFollowupStatus = new FollowupIsForContactPredicate(editedContact).toString();
        assertEquals(expectedStoryStatus, modelManager.getStoryFilterStatusProperty().getValue());
        assertEquals(1, modelManager.getFilteredStoryList().size());
        assertEquals(story.getName(), modelManager.getFilteredStoryList().get(0).getName());

        assertEquals(expectedFollowupStatus, modelManager.getFollowupFilterStatusProperty().getValue());
        assertEquals(1, modelManager.getFilteredFollowupList().size());
        assertEquals(followup.getDescription(), modelManager.getFilteredFollowupList().get(0).getDescription());
    }

    @Test
    public void setContact_nameChangedWhenFilteredByDifferentContact_preservesFilters() {
        ModelManager modelManager = new ModelManager();
        Contact contact = new ContactBuilder().withName("Alice Pauline").build();
        Contact otherContact = new ContactBuilder().withName("Bob Choo").build();

        modelManager.addContact(contact);
        modelManager.addContact(otherContact);

        StoryAssignedToContactPredicate storyPredicate = new StoryAssignedToContactPredicate(otherContact);
        FollowupIsForContactPredicate followupPredicate = new FollowupIsForContactPredicate(otherContact);
        modelManager.updateFilteredStoryList(storyPredicate);
        modelManager.updateFilteredFollowupList(followupPredicate);

        Contact editedContact = new ContactBuilder(contact).withName("Alicia Pauline").build();
        modelManager.setContact(contact, editedContact);

        assertEquals(storyPredicate.toString(), modelManager.getStoryFilterStatusProperty().getValue());
        assertEquals(followupPredicate.toString(), modelManager.getFollowupFilterStatusProperty().getValue());
    }

    @Test
    public void setStory_nameChangedWhenFilteredByStory_updatesContactFilter() {
        ModelManager modelManager = new ModelManager();
        Contact contact = new ContactBuilder().withName("Alice Pauline").withStories("Alpha Story").build();
        Story story = new Story(new StoryName("Alpha Story"), Set.of(), List.of(contact.getName()));

        modelManager.addContact(contact);
        modelManager.addStory(story);

        modelManager.updateFilteredContactList(new ContactAssignedToStoryPredicate(story));

        assertEquals(1, modelManager.getFilteredContactList().size());

        Story editedStory = new Story(new StoryName("Beta Story"), Set.of(), List.of(contact.getName()));
        modelManager.setStory(story, editedStory);

        String expectedStatus = new ContactAssignedToStoryPredicate(editedStory).toString();
        assertEquals(expectedStatus, modelManager.getContactFilterStatusProperty().getValue());
        assertEquals(1, modelManager.getFilteredContactList().size());
        assertEquals(contact.getName(), modelManager.getFilteredContactList().get(0).getName());
    }

    @Test
    public void setStory_nameChangedWhenFilteredByDifferentStory_preservesFilters() {
        ModelManager modelManager = new ModelManager();
        Story story = new Story(new StoryName("Alpha Story"), Set.of());
        Story otherStory = new Story(new StoryName("Other Story"), Set.of());

        modelManager.addStory(story);
        modelManager.addStory(otherStory);

        ContactAssignedToStoryPredicate contactPredicate = new ContactAssignedToStoryPredicate(otherStory);
        modelManager.updateFilteredContactList(contactPredicate);

        Story editedStory = new Story(new StoryName("Beta Story"), Set.of());
        modelManager.setStory(story, editedStory);

        assertEquals(contactPredicate.toString(), modelManager.getContactFilterStatusProperty().getValue());
    }

    @Test
    public void activeFilters_clearedWhenPredicateReset() {
        ModelManager modelManager = new ModelManager();
        Contact contact = new ContactBuilder().withName("Alice Pauline").build();
        Story story = new Story(new StoryName("Alpha Story"), Set.of());

        ContactAssignedToStoryPredicate contactPredicate = new ContactAssignedToStoryPredicate(story);
        modelManager.updateFilteredContactList(contactPredicate);
        assertEquals(contactPredicate.toString(), modelManager.getContactFilterStatusProperty().getValue());
        modelManager.updateFilteredContactList(PREDICATE_SHOW_ALL_CONTACTS);
        assertEquals(Model.FILTER_STATUS_ALL, modelManager.getContactFilterStatusProperty().getValue());

        StoryAssignedToContactPredicate storyPredicate = new StoryAssignedToContactPredicate(contact);
        modelManager.updateFilteredStoryList(storyPredicate);
        assertEquals(storyPredicate.toString(), modelManager.getStoryFilterStatusProperty().getValue());
        modelManager.updateFilteredStoryList(PREDICATE_SHOW_ALL_STORIES);
        assertEquals(Model.FILTER_STATUS_ALL, modelManager.getStoryFilterStatusProperty().getValue());

        FollowupIsForContactPredicate followupPredicate = new FollowupIsForContactPredicate(contact);
        modelManager.updateFilteredFollowupList(followupPredicate);
        assertEquals(followupPredicate.toString(), modelManager.getFollowupFilterStatusProperty().getValue());
        modelManager.updateFilteredFollowupList(PREDICATE_SHOW_ALL_FOLLOWUPS);
        assertEquals(Model.FILTER_STATUS_ALL, modelManager.getFollowupFilterStatusProperty().getValue());
    }
}
