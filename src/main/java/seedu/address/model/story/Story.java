package seedu.address.model.story;

import static seedu.address.commons.util.CollectionUtil.requireAllNonNull;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.IntStream;

import seedu.address.commons.util.ToStringBuilder;
import seedu.address.model.SemanticRepresenter;
import seedu.address.model.Titled;
import seedu.address.model.contact.Contact;
import seedu.address.model.contact.Name;
import seedu.address.model.tag.Tag;

/**
 * Represents a Story in the address book.
 * Guarantees: details are present and not null, field values are validated, immutable.
 */
public class Story implements Titled, SemanticRepresenter {

    // Identity fields
    private final StoryName name;

    // Data fields
    private final Set<Tag> tags = new HashSet<>();

    // Assigned contacts
    private final List<Name> contacts;

    /**
     * Every field except contacts must be present and not null.
     */
    public Story(StoryName name, Set<Tag> tags) {
        this(name, tags, List.of());
    }

    /**
     * Every field must be present and not null.
     */
    public Story(StoryName name, Set<Tag> tags, List<Name> contacts) {
        requireAllNonNull(name, tags);
        this.name = name;
        this.tags.addAll(tags);
        this.contacts = contacts;
    }

    public StoryName getName() {
        return name;
    }

    @Override
    public String getInstanceTitle() {
        return name.toString();
    }

    @Override
    public String getTypeTitle() {
        return "Story";
    }

    /**
     * Returns true if the story's name matches the given keyword.
     */
    public boolean isNameMatching(String keyword) {
        return name.isMatching(keyword);
    }

    /**
     * Returns an immutable list of contacts, which throws {@code UnsupportedOperationException}
     * if modification is attempted.
     */
    public List<Name> getContacts() {
        return List.copyOf(contacts);
    }

    /**
     * Returns whether the contact has been assigned to this Story
     */
    public boolean isAssigned(Contact contact) {
        return contacts.contains(contact.getName());
    }

    /**
     * Assigns a contact to this story, but does not assign this story to the contact.
     * Does nothing if the contact has already been assigned.
     * @return  A Story with the contact assigned, or this Story if it was already assigned.
     */
    public Story assign(Contact contact) {
        if (isAssigned(contact)) {
            return this;
        }
        List<Name> updatedContacts = IntStream
                .range(0, contacts.size() + 1)
                .mapToObj(idx -> idx < contacts.size() ? contacts.get(idx) : contact.getName())
                .toList();
        return new Story(name, tags, updatedContacts);
    }

    /**
     * Unassigns a contact from this story, but does not unassign this story from the contact.
     * Does nothing if the contact has already been unassigned.
     * @return  A Story with the contact unassigned, or this Story if it was already unassigned.
     *
     */
    public Story unassign(Contact contact) {
        if (!isAssigned(contact)) {
            return this;
        }
        Name removedContactName = contact.getName();
        List<Name> updatedContacts = contacts.stream()
                .filter(contactName -> !removedContactName.equals(contactName))
                .toList();
        return new Story(name, tags, updatedContacts);
    }

    /**
     * Returns an immutable tag set, which throws {@code UnsupportedOperationException}
     * if modification is attempted.
     */
    public Set<Tag> getTags() {
        return Collections.unmodifiableSet(tags);
    }

    /**
     * Returns true if both stories have the same name.
     * This defines a weaker notion of equality between two stories.
     */
    @Override
    public boolean representsSameItem(SemanticRepresenter other) {
        requireAllNonNull(other);

        return other instanceof Story otherStory
                && otherStory.getName().equals(getName());
    }

    /**
     * Returns true if both stories have the same identity, data, and assigned-contact fields.
     * This defines a stronger notion of equality between two stories.
     */
    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof seedu.address.model.story.Story otherStory)) {
            return false;
        }

        return name.equals(otherStory.name)
                && tags.equals(otherStory.tags)
                && contacts.equals(otherStory.contacts);
    }

    @Override
    public int hashCode() {
        // use this method for custom fields hashing instead of implementing your own
        return Objects.hash(name, tags, contacts);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .add("name", name)
                .add("tags", tags)
                .add("contacts", contacts)
                .toString();
    }

}
