package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;
import static seedu.address.logic.parser.CliSyntax.PREFIX_NAME;
import static seedu.address.logic.parser.CliSyntax.PREFIX_TAG;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

import seedu.address.commons.core.index.Index;
import seedu.address.commons.util.CollectionUtil;
import seedu.address.commons.util.ToStringBuilder;
import seedu.address.logic.Messages;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.Model;
import seedu.address.model.story.Story;
import seedu.address.model.story.StoryName;
import seedu.address.model.tag.Tag;

/**
 * Edits the details of an existing story in PressMemo.
 */
public class EditStoryCommand extends AddressBookMutatingCommand {

    public static final String COMMAND_WORD = "editstory";

    public static final String MESSAGE_USAGE = COMMAND_WORD + ": Edits the details of the story identified "
            + "by the index number used in the displayed story list. "
            + "Existing values will be overwritten by the input values.\n"
            + "Parameters: INDEX (must be a positive integer) "
            + "[" + PREFIX_NAME + "STORYNAME] "
            + "[" + PREFIX_TAG + "TAG]...\n"
            + "Example: " + COMMAND_WORD + " 1 "
            + PREFIX_NAME + "Investigation "
            + PREFIX_TAG + "Singapore";

    public static final String MESSAGE_EDIT_STORY_SUCCESS = "Edited story: %1$s";
    public static final String MESSAGE_NOT_EDITED = "At least one field to edit must be provided.";
    public static final String MESSAGE_DUPLICATE_STORY = "This story already exists in PressMemo.";

    private final Index index;
    private final EditStoryDescriptor editStoryDescriptor;

    /**
     * @param index of the story in the filtered story list to edit
     * @param editStoryDescriptor details to edit the story with
     */
    public EditStoryCommand(Index index, EditStoryDescriptor editStoryDescriptor) {
        requireNonNull(index);
        requireNonNull(editStoryDescriptor);

        this.index = index;
        this.editStoryDescriptor = new EditStoryDescriptor(editStoryDescriptor);
    }

    @Override
    public CommandResult execute(Model model) throws CommandException {
        requireNonNull(model);
        List<Story> lastShownList = model.getFilteredStoryList();

        if (index.getZeroBased() >= lastShownList.size()) {
            throw new CommandException(Messages.MESSAGE_INVALID_STORY_DISPLAYED_INDEX);
        }

        Story storyToEdit = lastShownList.get(index.getZeroBased());
        Story editedStory = editStoryDescriptor.createEditedStory(storyToEdit);

        if (!storyToEdit.representsSameItem(editedStory) && model.hasStory(editedStory)) {
            throw new CommandException(MESSAGE_DUPLICATE_STORY);
        }

        model.setStory(storyToEdit, editedStory);
        return new CommandResult(String.format(MESSAGE_EDIT_STORY_SUCCESS, Messages.format(editedStory)));
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof EditStoryCommand otherEditStoryCommand)) {
            return false;
        }

        return index.equals(otherEditStoryCommand.index)
                && editStoryDescriptor.equals(otherEditStoryCommand.editStoryDescriptor);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .add("index", index)
                .add("editStoryDescriptor", editStoryDescriptor)
                .toString();
    }

    /**
     * Stores the details to edit the story with. Each non-empty field value will replace the
     * corresponding field value of the story.
     */
    public static class EditStoryDescriptor {
        private StoryName name;
        private Set<Tag> tags;

        public EditStoryDescriptor() {}

        /**
         * Copy constructor.
         * A defensive copy of {@code tags} is used internally.
         */
        public EditStoryDescriptor(EditStoryDescriptor toCopy) {
            setName(toCopy.name);
            setTags(toCopy.tags);
        }

        /**
         * Returns true if at least one field is edited.
         */
        public boolean isAnyFieldEdited() {
            return CollectionUtil.isAnyNonNull(name, tags);
        }

        public void setName(StoryName name) {
            this.name = name;
        }

        public Optional<StoryName> getName() {
            return Optional.ofNullable(name);
        }

        /**
         * Sets {@code tags} to this object's {@code tags}.
         * A defensive copy of {@code tags} is used internally.
         */
        public void setTags(Set<Tag> tags) {
            this.tags = (tags != null) ? new HashSet<>(tags) : null;
        }

        /**
         * Returns an unmodifiable tag set, which throws {@code UnsupportedOperationException}
         * if modification is attempted.
         * Returns {@code Optional#empty()} if {@code tags} is null.
         */
        public Optional<Set<Tag>> getTags() {
            return (tags != null) ? Optional.of(Collections.unmodifiableSet(tags)) : Optional.empty();
        }

        /**
         * Creates and returns a {@code Story} with the details of {@code storyToEdit}
         * edited with this descriptor's fields.
         */
        public Story createEditedStory(Story storyToEdit) {
            assert storyToEdit != null;

            StoryName updatedName = getName().orElse(storyToEdit.getName());
            Set<Tag> updatedTags = getTags().orElse(storyToEdit.getTags());

            return new Story(
                    updatedName,
                    updatedTags,
                    storyToEdit.getContacts()
            );
        }

        @Override
        public boolean equals(Object other) {
            if (other == this) {
                return true;
            }

            // instanceof handles nulls
            if (!(other instanceof EditStoryDescriptor otherEditStoryDescriptor)) {
                return false;
            }

            return Objects.equals(name, otherEditStoryDescriptor.name)
                    && Objects.equals(tags, otherEditStoryDescriptor.tags);
        }

        @Override
        public String toString() {
            return new ToStringBuilder(this)
                    .add("name", name)
                    .add("tags", tags)
                    .toString();
        }
    }
}
