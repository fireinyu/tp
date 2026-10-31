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
 * Assigns a contact to a story.
 */
public class AssignCommand extends AddressBookMutatingCommand {

    public static final String COMMAND_WORD = "assign";
    public static final String MESSAGE_ALREADY_ASSIGNED = "%s is already assigned to %s.";
    public static final String MESSAGE_USAGE = COMMAND_WORD + ": Assigns a contact to the story. "
            + "Parameters: "
            + PREFIX_CONTACTINDEX + "CONTACT_INDEX "
            + PREFIX_STORYINDEX + "STORY_INDEX\n"
            + "Example: " + COMMAND_WORD + " "
            + PREFIX_CONTACTINDEX + "1 "
            + PREFIX_STORYINDEX + "1";

    public static final String MESSAGE_SUCCESS = "Assigned %s to %s";

    private final Index storyIndex;
    private final Index contactIndex;
    /**
     * @param storyIndex of the story in the filtered story list to assign contact to
     * @param contactIndex of the contact in the filtered contact list to assign to story
     */
    public AssignCommand(Index storyIndex, Index contactIndex) {
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

        Contact contactToAssign = lastShownContactList.get(contactIndex.getZeroBased());
        Story storyToAssign = lastShownStoryList.get(storyIndex.getZeroBased());

        if (contactToAssign.isAssigned(storyToAssign)) {
            throw new CommandException(String.format(
                    MESSAGE_ALREADY_ASSIGNED,
                    contactToAssign.getName(),
                    storyToAssign.getName()));
        }

        model.assignStory(contactToAssign, storyToAssign);
        return new CommandResult(String.format(MESSAGE_SUCCESS, contactToAssign.getName(), storyToAssign.getName()));
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        if (!(other instanceof AssignCommand otherAssignCommand)) {
            return false;
        }

        return storyIndex.equals(otherAssignCommand.storyIndex)
                && contactIndex.equals(otherAssignCommand.contactIndex);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .add("storyIndex", storyIndex)
                .add("contactIndex", contactIndex)
                .toString();
    }
}
