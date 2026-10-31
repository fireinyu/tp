package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;
import static seedu.address.logic.parser.CliSyntax.PREFIX_CONTACTINDEX;
import static seedu.address.logic.parser.CliSyntax.PREFIX_STORYINDEX;

import java.util.List;

import seedu.address.commons.core.index.Index;
import seedu.address.commons.util.ToStringBuilder;
import seedu.address.logic.Messages;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.Model;
import seedu.address.model.contact.Contact;
import seedu.address.model.story.Story;

/**
 * Unassigns a contact from a story.
 */
public class UnassignCommand extends AddressBookMutatingCommand {

    public static final String COMMAND_WORD = "unassign";
    public static final String MESSAGE_NOT_ASSIGNED = "%s is not assigned to %s.";
    public static final String MESSAGE_USAGE = COMMAND_WORD + ": Unassigns a contact from the story. "
            + "Parameters: "
            + PREFIX_CONTACTINDEX + "CONTACT_INDEX "
            + PREFIX_STORYINDEX + "STORY_INDEX\n"
            + "Example: " + COMMAND_WORD + " "
            + PREFIX_CONTACTINDEX + "1 "
            + PREFIX_STORYINDEX + "1";

    public static final String MESSAGE_SUCCESS = "Unassigned %s from %s";

    private final Index storyIndex;
    private final Index contactIndex;
    /**
     * @param storyIndex of the story in the filtered story list to unassign contact from
     * @param contactIndex of the contact in the filtered contact list to unassign from story
     */
    public UnassignCommand(Index storyIndex, Index contactIndex) {
        requireNonNull(storyIndex);
        requireNonNull(contactIndex);

        this.storyIndex = storyIndex;
        this.contactIndex = contactIndex;
    }

    @Override
    public CommandResult execute(Model model) throws CommandException {
        requireNonNull(model);
        List<Contact> lastShownContactList = model.getFilteredContactList();
        List<Story> lastShownStoryList = model.getFilteredStoryList();

        if (contactIndex.getZeroBased() >= lastShownContactList.size()) {
            throw new CommandException(Messages.MESSAGE_INVALID_CONTACT_DISPLAYED_INDEX);
        }

        if (storyIndex.getZeroBased() >= lastShownStoryList.size()) {
            throw new CommandException(Messages.MESSAGE_INVALID_STORY_DISPLAYED_INDEX);
        }

        Contact contactToUnassign = lastShownContactList.get(contactIndex.getZeroBased());
        Story storyToUnassign = lastShownStoryList.get(storyIndex.getZeroBased());

        if (!contactToUnassign.isAssigned(storyToUnassign)) {
            throw new CommandException(String.format(MESSAGE_NOT_ASSIGNED,
                    contactToUnassign.getName(), storyToUnassign.getName()));
        }

        model.unassignStory(contactToUnassign, storyToUnassign);
        return new CommandResult(String.format(MESSAGE_SUCCESS, contactToUnassign.getName(),
                storyToUnassign.getName()));
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        if (!(other instanceof UnassignCommand otherUnassignCommand)) {
            return false;
        }

        return storyIndex.equals(otherUnassignCommand.storyIndex)
                && contactIndex.equals(otherUnassignCommand.contactIndex);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .add("storyIndex", storyIndex)
                .add("contactIndex", contactIndex)
                .toString();
    }
}
