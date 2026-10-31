package seedu.address.model;

import static java.util.Objects.requireNonNull;
import static seedu.address.commons.util.CollectionUtil.requireAllNonNull;

import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import seedu.address.commons.core.GuiSettings;
import seedu.address.commons.core.LogsCenter;
import seedu.address.model.contact.Contact;
import seedu.address.model.followup.Followup;
import seedu.address.model.story.Story;

/**
 * Represents the in-memory model of the address book data.
 */
public class ModelManager implements Model {
    private static final Logger logger = LogsCenter.getLogger(ModelManager.class);

    private final AddressBook addressBook;
    private final UserPrefs userPrefs;
    private final FilteredList<Contact> filteredContacts;
    private final FilteredList<Story> filteredStories;
    private final FilteredList<Followup> filteredFollowups;
    private final SimpleStringProperty contactFilterStatus = new SimpleStringProperty(FILTER_STATUS_ALL);
    private final SimpleStringProperty storyFilterStatus = new SimpleStringProperty(FILTER_STATUS_ALL);
    private final SimpleStringProperty followupFilterStatus = new SimpleStringProperty(FILTER_STATUS_ALL);
    private StateAwarePredicate<Story, Contact> activeStoryFilter = PREDICATE_SHOW_ALL_STORIES;
    private StateAwarePredicate<Contact, Story> activeContactFilter = PREDICATE_SHOW_ALL_CONTACTS;
    private StateAwarePredicate<Followup, Contact> activeFollowupFilter = PREDICATE_SHOW_ALL_FOLLOWUPS;
    /**
     * Initializes a ModelManager with the given addressBook and userPrefs.
     */
    public ModelManager(ReadOnlyAddressBook addressBook, ReadOnlyUserPrefs userPrefs) {
        requireAllNonNull(addressBook, userPrefs);

        logger.fine("Initializing with address book: " + addressBook + " and user prefs " + userPrefs);

        this.addressBook = new AddressBook(addressBook);
        this.userPrefs = new UserPrefs(userPrefs);
        filteredContacts = new FilteredList<>(this.addressBook.getContactList());
        filteredStories = new FilteredList<>(this.addressBook.getStoryList());
        filteredFollowups = new FilteredList<>(this.addressBook.getFollowupList());
    }

    public ModelManager() {
        this(new AddressBook(), new UserPrefs());
    }

    //=========== UserPrefs ==================================================================================

    @Override
    public ReadOnlyUserPrefs getUserPrefs() {
        return userPrefs;
    }

    @Override
    public GuiSettings getGuiSettings() {
        return userPrefs.getGuiSettings();
    }

    @Override
    public void setGuiSettings(GuiSettings guiSettings) {
        requireNonNull(guiSettings);
        userPrefs.setGuiSettings(guiSettings);
    }

    @Override
    public Map<String, String> getShortcuts() {
        return userPrefs.getShortcuts();
    }

    @Override
    public void assignShortcut(String shortcut, String commandWord) {
        userPrefs.assignShortcut(shortcut, commandWord);
    }

    @Override
    public boolean removeShortcut(String shortcut) {
        return userPrefs.removeShortcut(shortcut);
    }

    //=========== AddressBook ================================================================================

    @Override
    public void setAddressBook(ReadOnlyAddressBook addressBook) {
        this.addressBook.resetData(addressBook);
        updateFilteredContactList(PREDICATE_SHOW_ALL_CONTACTS);
        updateFilteredStoryList(PREDICATE_SHOW_ALL_STORIES);
        updateFilteredFollowupList(PREDICATE_SHOW_ALL_FOLLOWUPS);
    }

    @Override
    public ReadOnlyAddressBook getAddressBook() {
        return addressBook;
    }

    @Override
    public boolean hasContact(Contact contact) {
        requireNonNull(contact);
        return addressBook.hasContact(contact);
    }

    @Override
    public void deleteContact(Contact target) {
        updateFilteredStoryList(activeStoryFilter.notifyStateDeleted(target));
        updateFilteredFollowupList(activeFollowupFilter.notifyStateDeleted(target));
        addressBook.removeContact(target);
    }

    @Override
    public void addContact(Contact contact) {
        addressBook.addContact(contact);
        updateFilteredContactList(PREDICATE_SHOW_ALL_CONTACTS);
    }

    @Override
    public void setContact(Contact target, Contact editedContact) {
        requireAllNonNull(target, editedContact);

        updateFilteredStoryList(activeStoryFilter.notifyStateChanged(target, editedContact));
        updateFilteredFollowupList(activeFollowupFilter.notifyStateChanged(target, editedContact));
        addressBook.setContact(target, editedContact);
    }

    //=========== Story Operations ===========================================================================

    @Override
    public boolean hasStory(Story story) {
        requireNonNull(story);
        return addressBook.hasStory(story);
    }

    @Override
    public void addStory(Story story) {
        addressBook.addStory(story);
        updateFilteredStoryList(PREDICATE_SHOW_ALL_STORIES);
    }

    @Override
    public void setStory(Story target, Story editedStory) {
        requireAllNonNull(target, editedStory);

        updateFilteredContactList(activeContactFilter.notifyStateChanged(target, editedStory));
        addressBook.setStory(target, editedStory);

    }

    @Override
    public void deleteStory(Story target) {
        updateFilteredContactList(activeContactFilter.notifyStateDeleted(target));
        addressBook.removeStory(target);
    }

    @Override
    public void assignStory(Contact target, Story story) {
        requireAllNonNull(target, story);

        addressBook.assignStory(target, story);
    }

    @Override
    public void unassignStory(Contact target, Story story) {
        requireAllNonNull(target, story);

        addressBook.unassignStory(target, story);
    }
    //=========== Followup Operations ===========================================================================

    @Override
    public boolean hasFollowup(Followup followup) {
        requireNonNull(followup);
        return addressBook.hasFollowup(followup);
    }

    @Override
    public void addFollowup(Followup followup) {
        addressBook.addFollowup(followup);
        updateFilteredFollowupList(PREDICATE_SHOW_ALL_FOLLOWUPS);
    }

    @Override
    public void deleteFollowup(Followup followup) {
        addressBook.removeFollowup(followup);
    }
    //=========== Filtered Contact List Accessors =============================================================

    /**
     * Returns an unmodifiable view of the list of {@code Contact} backed by the internal list of
     * {@code addressBook}
     */
    @Override
    public ObservableList<Contact> getFilteredContactList() {
        return filteredContacts;
    }

    @Override
    public void updateFilteredContactList(StateAwarePredicate<Contact, Story> predicate) {
        requireNonNull(predicate);
        activeContactFilter = predicate;
        filteredContacts.setPredicate(predicate);
        contactFilterStatus.set(predicate.toString());
    }



    @Override
    public ObservableValue<String> getContactFilterStatusProperty() {
        return contactFilterStatus;
    }

    //=========== Filtered Story List Accessors =============================================================
    /**
     * Returns an unmodifiable view of the list of {@code Story} backed by the internal list of
     * {@code addressBook}
     */
    @Override
    public ObservableList<Story> getFilteredStoryList() {
        return filteredStories;
    }

    @Override
    public void updateFilteredStoryList(StateAwarePredicate<Story, Contact> predicate) {
        requireNonNull(predicate);
        activeStoryFilter = predicate;
        filteredStories.setPredicate(predicate);
        storyFilterStatus.set(activeStoryFilter.toString());
    }

    @Override
    public ObservableValue<String> getStoryFilterStatusProperty() {
        return storyFilterStatus;
    }

    @Override
    public void setFollowup(Followup target, Followup editedFollowup) {
        requireAllNonNull(target, editedFollowup);
        addressBook.setFollowup(target, editedFollowup);
    }

    //=========== Filtered Followup List Accessors =============================================================
    /**
     * Returns an unmodifiable view of the list of {@code Followup} backed by the internal list of
     * {@code addressBook}
     */
    @Override
    public ObservableList<Followup> getFilteredFollowupList() {
        return filteredFollowups;
    }

    @Override
    public void updateFilteredFollowupList(StateAwarePredicate<Followup, Contact> predicate) {
        requireNonNull(predicate);
        activeFollowupFilter = predicate;
        filteredFollowups.setPredicate(predicate);
        followupFilterStatus.set(predicate.toString());
    }

    @Override
    public ObservableValue<String> getFollowupFilterStatusProperty() {
        return followupFilterStatus;
    }

    private static String formatFilteredKeywords(List<String> keywords) {
        return String.format("Filtered: \"%s\"", String.join(" ", keywords));
    }

    //=========== Object Overrides ==========================================================================
    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof ModelManager otherModelManager)) {
            return false;
        }

        return addressBook.equals(otherModelManager.addressBook)
                && userPrefs.equals(otherModelManager.userPrefs)
                && filteredContacts.equals(otherModelManager.filteredContacts)
                && filteredStories.equals(otherModelManager.filteredStories)
                && filteredFollowups.equals(otherModelManager.filteredFollowups);
    }
}
