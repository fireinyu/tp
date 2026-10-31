package seedu.address.model.contact;

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
import seedu.address.model.story.Story;
import seedu.address.model.story.StoryName;
import seedu.address.model.tag.Tag;

/**
 * Represents a Contact in the address book.
 * Guarantees: details are present and not null, field values are validated, immutable.
 */
public class Contact implements Titled, SemanticRepresenter {

    // Identity fields
    private final Name name;
    private final Phone phone;
    private final Email email;

    // Data fields
    private final Address address;
    private final Set<Tag> tags = new HashSet<>();

    // Assigned stories
    private final List<StoryName> stories;

    /**
     * Every field except stories must be present and not null.
     * Initialises with no assigned stories.
     */
    public Contact(Name name, Phone phone, Email email, Address address, Set<Tag> tags) {
        this(name, phone, email, address, tags, List.of());

    }

    /**
     * Every field must be present and not null.
     */
    public Contact(Name name, Phone phone, Email email, Address address, Set<Tag> tags, List<StoryName> stories) {
        requireAllNonNull(name, phone, email, address, tags, stories);
        this.name = name;
        this.phone = phone;
        this.email = email;
        this.address = address;
        this.tags.addAll(tags);
        this.stories = stories;
    }


    public Name getName() {
        return name;
    }

    @Override
    public String getInstanceTitle() {
        return name.toString();
    }

    @Override
    public String getTypeTitle() {
        return "Contact";
    }

    public Phone getPhone() {
        return phone;
    }

    public Email getEmail() {
        return email;
    }

    public Address getAddress() {
        return address;
    }

    /**
     * Returns true if the contact's name matches the given keyword.
     */
    public boolean isNameMatching(String keyword) {
        return name.isMatching(keyword);
    }

    /**
     * Returns whether the story has been assigned to this Contact
     */
    public boolean isAssigned(Story story) {
        return stories.contains(story.getName());
    }

    /**
     * Assigns a story to this Contact, but does not assign this Contact to the story.
     * Does nothing if the story has already been assigned.
     * @return A Contact with the story assigned, or this Contact if it was already assigned.
     */
    public Contact assign(Story story) {
        if (isAssigned(story)) {
            return this;
        }
        List<StoryName> updatedStories = IntStream
                .range(0, stories.size() + 1)
                .mapToObj(idx -> idx < stories.size() ? stories.get(idx) : story.getName())
                .toList();
        return new Contact(name, phone, email, address, tags, updatedStories);
    }

    /**
     * Unassigns a story from this contact, but does not unassign this contact from the story.
     * Does nothing if the story has already been unassigned.
     * @return  A Contact with the story unassigned, or this Contact if it was already unassigned.
     *
     */
    public Contact unassign(Story story) {
        if (!isAssigned(story)) {
            return this;
        }
        StoryName removedStoryName = story.getName();
        List<StoryName> updatedStories = stories.stream()
                        .filter(storyName -> !removedStoryName.equals(storyName))
                        .toList();
        return new Contact(name, phone, email, address, tags, updatedStories);
    }

    /**
     * Returns an immutable list of stories, which throws {@code UnsupportedOperationException}
     * if modification is attempted.
     */
    public List<StoryName> getStories() {
        return List.copyOf(stories);
    }


    /**
     * Returns an immutable tag set, which throws {@code UnsupportedOperationException}
     * if modification is attempted.
     */
    public Set<Tag> getTags() {
        return Collections.unmodifiableSet(tags);
    }

    /**
     * Returns true if both contacts have the same name.
     * This defines a weaker notion of equality between two contacts.
     */
    @Override
    public boolean representsSameItem(SemanticRepresenter other) {
        requireAllNonNull(other);

        return other instanceof Contact otherContact
                && otherContact.getName().equals(getName());
    }

    /**
     * Returns true if both contacts have the same identity, data, and assigned-story fields.
     * This defines a stronger notion of equality between two contacts.
     */
    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof Contact otherContact)) {
            return false;
        }

        return name.equals(otherContact.name)
                && phone.equals(otherContact.phone)
                && email.equals(otherContact.email)
                && address.equals(otherContact.address)
                && tags.equals(otherContact.tags)
                && stories.equals(otherContact.stories);
    }

    @Override
    public int hashCode() {
        // use this method for custom fields hashing instead of implementing your own
        return Objects.hash(name, phone, email, address, tags, stories);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .add("name", name)
                .add("phone", phone)
                .add("email", email)
                .add("address", address)
                .add("tags", tags)
                .add("stories", stories)
                .toString();
    }

}
