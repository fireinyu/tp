package seedu.address.model.story;

import static java.util.Objects.requireNonNull;
import static seedu.address.commons.util.CollectionUtil.requireAllNonNull;

import java.util.Iterator;
import java.util.List;
import java.util.stream.IntStream;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import seedu.address.model.SemanticRepresenter;
import seedu.address.model.story.exceptions.DuplicateStoryException;
import seedu.address.model.story.exceptions.StoryNotFoundException;

/**
 * A list of stories that enforces uniqueness between its elements and does not allow nulls.
 * A story is considered unique by comparing using {@code Story#representsSameItem(Story)}. As such,
 * adding and updating of stories uses Story#representsSameItem(Story) for equality so as to ensure
 * that the story being added or updated is unique in terms of identity in the UniqueStoryList.
 * However, the removal of a story uses Story#equals(Object) so as to ensure that the story with
 * exactly the same fields will be removed.
 *
 * @see Story#representsSameItem(SemanticRepresenter)
 */

public class UniqueStoryList implements Iterable<Story> {

    private final ObservableList<Story> internalList = FXCollections.observableArrayList();
    private final ObservableList<Story> internalUnmodifiableList =
            FXCollections.unmodifiableObservableList(internalList);

    /**
     * Returns true if the list contains an equivalent story as the given argument.
     */
    public boolean contains(Story toCheck) {
        requireNonNull(toCheck);
        return internalList.stream().anyMatch(toCheck::representsSameItem);
    }

    /**
     * Adds a story to the list.
     * The story must not already exist in the list.
     */
    public void add(Story toAdd) {
        requireNonNull(toAdd);
        if (contains(toAdd)) {
            throw new DuplicateStoryException();
        }
        internalList.add(toAdd);
    }

    /**
     * Replaces the story {@code target} in the list with {@code editedStory}.
     * {@code target} must exist in the list.
     * The story identity of {@code editedStory} must not be the same as another existing story in the list.
     */
    public void setStory(Story target, Story editedStory) {
        requireAllNonNull(target, editedStory);

        int index = indexOfSameStory(target);
        if (index == -1) {
            throw new StoryNotFoundException();
        }

        if (!target.representsSameItem(editedStory) && contains(editedStory)) {
            throw new DuplicateStoryException();
        }

        internalList.set(index, editedStory);
    }

    /**
     * Removes the equivalent story from the list.
     * The story must exist in the list.
     */
    public void remove(Story toRemove) {
        requireNonNull(toRemove);
        if (!internalList.remove(toRemove)) {
            throw new StoryNotFoundException();
        }
    }

    public void setStories(seedu.address.model.story.UniqueStoryList replacement) {
        requireNonNull(replacement);
        internalList.setAll(replacement.internalList);
    }

    /**
     * Replaces the contents of this list with {@code stories}.
     * {@code stories} must not contain duplicate stories.
     */
    public void setStories(List<Story> stories) {
        requireAllNonNull(stories);
        if (!storiesAreUnique(stories)) {
            throw new DuplicateStoryException();
        }

        internalList.setAll(stories);
    }

    /**
     * Returns the backing list as an unmodifiable {@code ObservableList}.
     */
    public ObservableList<Story> asUnmodifiableObservableList() {
        return internalUnmodifiableList;
    }

    @Override
    public Iterator<Story> iterator() {
        return internalList.iterator();
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof seedu.address.model.story.UniqueStoryList otherUniqueStoryList)) {
            return false;
        }

        return internalList.equals(otherUniqueStoryList.internalList);
    }

    @Override
    public int hashCode() {
        return internalList.hashCode();
    }

    @Override
    public String toString() {
        return internalList.toString();
    }

    /**
     * Returns true if {@code stories} contains only unique stories.
     */
    private boolean storiesAreUnique(List<Story> stories) {
        for (int i = 0; i < stories.size() - 1; i++) {
            for (int j = i + 1; j < stories.size(); j++) {
                if (stories.get(i).representsSameItem(stories.get(j))) {
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * Returns the index of the story with the same identity as {@code target}, or -1 if absent.
     */
    private int indexOfSameStory(Story target) {
        return IntStream.range(0, internalList.size())
                .filter(index -> internalList.get(index).representsSameItem(target))
                .findFirst()
                .orElse(-1);
    }
}
