package seedu.address.model;

import java.util.Map;

import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import seedu.address.commons.core.GuiSettings;
import seedu.address.model.contact.Contact;
import seedu.address.model.followup.Followup;
import seedu.address.model.story.Story;

/**
 * The API of the Model component.
 */
public interface Model {
    /** {@code Predicate} that always evaluates to true */
    StateAwarePredicate<Contact, Story> PREDICATE_SHOW_ALL_CONTACTS = new ListAllPredicate<>();
    StateAwarePredicate<Story, Contact> PREDICATE_SHOW_ALL_STORIES = new ListAllPredicate<>();
    StateAwarePredicate<Followup, Contact> PREDICATE_SHOW_ALL_FOLLOWUPS = new ListAllPredicate<>();

    /** Default filter status when all items are shown. */
    String FILTER_STATUS_ALL = new ListAllPredicate<>().toString();

    /**
     * Returns the user prefs.
     */
    ReadOnlyUserPrefs getUserPrefs();

    /**
     * Returns the user prefs' GUI settings.
     */
    GuiSettings getGuiSettings();

    /**
     * Sets the user prefs' GUI settings.
     */
    void setGuiSettings(GuiSettings guiSettings);

    /** Returns the user's command shortcuts mapped to their command words. */
    Map<String, String> getShortcuts();

    /** Assigns a shortcut to a command, replacing conflicting mappings. */
    void assignShortcut(String shortcut, String commandWord);

    /** Removes a shortcut, returning whether a mapping was removed. */
    boolean removeShortcut(String shortcut);

    /**
     * Replaces address book data with the data in {@code addressBook}.
     */
    void setAddressBook(ReadOnlyAddressBook addressBook);

    /** Returns the AddressBook */
    ReadOnlyAddressBook getAddressBook();

    /**
     * Returns true if a contact with the same identity as {@code contact} exists in the address book.
     */
    boolean hasContact(Contact contact);

    /**
     * Deletes the given contact.
     * The contact must exist in the address book.
     */
    void deleteContact(Contact target);

    /**
     * Deletes the given story.
     * The story must exist in the address book.
     */
    default void deleteStory(Story target) {}

    /**
     * Adds the given contact.
     * {@code contact} must not already exist in the address book.
     */
    void addContact(Contact contact);

    /**
     * Replaces the given contact {@code target} with {@code editedContact}.
     * {@code target} must exist in the address book.
     * The contact identity of {@code editedContact} must not be the same as another existing contact in the address
     * book.
     */
    void setContact(Contact target, Contact editedContact);

    /**
     * Returns true if a story with the same identity as {@code story} exists in the address book.
     */
    boolean hasStory(Story story);

    /**
     * Adds the given story.
     * {@code story} must not already exist in the address book.
     */
    void addStory(Story story);

    /**
     * Replaces the given story {@code target} with {@code editedStory}.
     * {@code target} must exist in the address book.
     * The story identity of {@code editedStory} must not be the same as another existing story in the address book.
     */
    void setStory(Story target, Story editedStory);

    /**
     * Assigns the given contact to the given story
     * {@code target} must exist in the address book.
     * {@code story} must exist in the address book.
     */
    void assignStory(Contact target, Story story);

    /**
     * Unassigns the given contact from the given story
     * {@code target} must exist in the address book.
     * {@code story} must exist in the address book.
     */
    void unassignStory(Contact target, Story story);

    /**
     * Returns true if a followup with the same identity as {@code followup} exists in the address book.
     */
    boolean hasFollowup(Followup followup);

    /**
     * Adds the given followup.
     * {@code followup} must not already exist in the address book.
     */
    void addFollowup(Followup followup);

    /**
     * Deletes the given followup.
     * The followup must exist in the address book.
     */
    void deleteFollowup(Followup followup);

    /** Replaces an existing followup with the given edited followup. */
    void setFollowup(Followup target, Followup editedFollowup);

    /** Returns an unmodifiable view of the filtered contact list */
    ObservableList<Contact> getFilteredContactList();

    /** Returns an unmodifiable view of the filtered story list */
    ObservableList<Story> getFilteredStoryList();

    /** Returns an unmodifiable view of the filtered followup list */
    ObservableList<Followup> getFilteredFollowupList();

    /**
     * Updates the filter of the filtered contact list to filter by the given {@code predicate}.
     * @throws NullPointerException if {@code predicate} is null.
     */
    void updateFilteredContactList(StateAwarePredicate<Contact, Story> predicate);

    /**
     * Updates the filter of the filtered story list to filter by the given {@code predicate}.
     * @throws NullPointerException if {@code predicate} is null.
     */
    void updateFilteredStoryList(StateAwarePredicate<Story, Contact> predicate);


    /**
     * Updates the filter of the filtered followup list to filter by the given {@code predicate}.
     * @throws NullPointerException if {@code predicate} is null.
     */
    void updateFilteredFollowupList(StateAwarePredicate<Followup, Contact> predicate);

    /** Returns the filter status property of the contact list. */
    ObservableValue<String> getContactFilterStatusProperty();

    /** Returns the filter status property of the story list. */
    ObservableValue<String> getStoryFilterStatusProperty();

    /** Returns the filter status property of the followup list. */
    ObservableValue<String> getFollowupFilterStatusProperty();
}
