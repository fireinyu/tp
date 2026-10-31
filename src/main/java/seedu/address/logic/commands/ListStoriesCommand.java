package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;
import static seedu.address.model.Model.PREDICATE_SHOW_ALL_STORIES;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

import seedu.address.commons.core.index.Index;
import seedu.address.commons.util.ToStringBuilder;
import seedu.address.logic.Messages;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.Model;
import seedu.address.model.contact.Contact;
import seedu.address.model.story.StoryAssignedToContactPredicate;

/**
 * Lists stories in PressMemo to the user.
 */
public class ListStoriesCommand extends Command {

    public static final String COMMAND_WORD = "liststories";
    public static final String MESSAGE_SUCCESS = "Listed all %1$d story(ies).";
    public static final String MESSAGE_LIST_ASSIGNED_STORIES_SUCCESS =
            "Listed %1$d story(ies) assigned to %2$s";

    public static final String MESSAGE_USAGE = COMMAND_WORD + ": Lists stories in PressMemo. "
            + "Parameters: [ci/CONTACT_INDEX]\n"
            + "Example: " + COMMAND_WORD + " ci/1";

    private final Optional<Index> contactIndex;

    public ListStoriesCommand() {
        this.contactIndex = Optional.empty();
    }

    public ListStoriesCommand(Index contactIndex) {
        this.contactIndex = Optional.of(requireNonNull(contactIndex));
    }

    @Override
    public CommandResult execute(Model model) throws CommandException {
        requireNonNull(model);

        // No index: display all stories
        if (contactIndex.isEmpty()) {
            model.updateFilteredStoryList(PREDICATE_SHOW_ALL_STORIES);
            return new CommandResult(String.format(MESSAGE_SUCCESS,
                    model.getFilteredStoryList().size()));
        }

        // Index provided: identify the contact
        List<Contact> lastShownContactList = model.getFilteredContactList();
        int index = contactIndex.get().getZeroBased();

        if (index >= lastShownContactList.size()) {
            throw new CommandException(
                    Messages.MESSAGE_INVALID_CONTACT_DISPLAYED_INDEX);
        }

        Contact selectedContact = lastShownContactList.get(index);

        // Display only stories assigned to this contact
        model.updateFilteredStoryList(new StoryAssignedToContactPredicate(selectedContact));

        return new CommandResult(
                String.format(MESSAGE_LIST_ASSIGNED_STORIES_SUCCESS,
                        model.getFilteredStoryList().size(),
                        selectedContact.getName()));
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        if (!(other instanceof ListStoriesCommand otherListStoriesCommand)) {
            return false;
        }

        return contactIndex.equals(otherListStoriesCommand.contactIndex);
    }

    @Override
    public int hashCode() {
        return Objects.hash(contactIndex);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .add("contactIndex", contactIndex)
                .toString();
    }
}
