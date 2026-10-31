package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;

import seedu.address.commons.util.ToStringBuilder;
import seedu.address.logic.Messages;
import seedu.address.model.Model;
import seedu.address.model.followup.FollowupDescriptionContainsKeywordsPredicate;

/**
 * Finds followups whose descriptions contain
 * any of the supplied keywords (case-insensitive).
 */
public class FindFollowupsCommand extends Command {

    public static final String COMMAND_WORD = "findfollowups";

    public static final String MESSAGE_USAGE = COMMAND_WORD
            + ": Finds followups whose descriptions contain any of "
            + "the specified keywords (case-insensitive).\n"
            + "Parameters: KEYWORD [MORE_KEYWORDS]...\n"
            + "Example: " + COMMAND_WORD + " interview meeting";

    private final FollowupDescriptionContainsKeywordsPredicate predicate;

    public FindFollowupsCommand(
            FollowupDescriptionContainsKeywordsPredicate predicate) {
        this.predicate = requireNonNull(predicate);
    }

    @Override
    public CommandResult execute(Model model) {
        requireNonNull(model);

        model.updateFilteredFollowupList(predicate);

        return new CommandResult(
                String.format(Messages.MESSAGE_FOLLOWUPS_LISTED_OVERVIEW,
                        model.getFilteredFollowupList().size()));
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .add("predicate", predicate)
                .toString();
    }
}
