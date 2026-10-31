package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;

import seedu.address.commons.util.ToStringBuilder;
import seedu.address.logic.Messages;
import seedu.address.model.Model;
import seedu.address.model.story.StoryNameContainsKeywordsPredicate;

/**
 * Finds and lists stories whose names contain any of the supplied keywords.
 */
public class FindStoriesCommand extends Command {

    public static final String COMMAND_WORD = "findstories";

    public static final String MESSAGE_USAGE = COMMAND_WORD + ": Finds all stories whose names contain any of "
            + "the specified keywords (case-insensitive).\n"
            + "Parameters: STORY_NAME [MORE_KEYWORDS]...\n"
            + "Example: " + COMMAND_WORD + " housing investigation";

    private final StoryNameContainsKeywordsPredicate predicate;

    public FindStoriesCommand(StoryNameContainsKeywordsPredicate predicate) {
        this.predicate = predicate;
    }

    @Override
    public CommandResult execute(Model model) {
        requireNonNull(model);
        model.updateFilteredStoryList(predicate);
        return new CommandResult(
                String.format(Messages.MESSAGE_STORIES_LISTED_OVERVIEW, model.getFilteredStoryList().size()));
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .add("predicate", predicate)
                .toString();
    }
}
