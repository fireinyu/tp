package seedu.address.logic.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static seedu.address.logic.Messages.MESSAGE_STORIES_LISTED_OVERVIEW;
import static seedu.address.logic.commands.CommandTestUtil.assertCommandSuccess;
import static seedu.address.testutil.TypicalStories.STORY_A;
import static seedu.address.testutil.TypicalStories.STORY_C;
import static seedu.address.testutil.TypicalStories.getTypicalAddressBook;

import java.util.List;

import org.junit.jupiter.api.Test;

import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.model.UserPrefs;
import seedu.address.model.story.StoryNameContainsKeywordsPredicate;

public class FindStoriesCommandTest {
    private final Model model = new ModelManager(getTypicalAddressBook(), new UserPrefs());
    private final Model expectedModel = new ModelManager(getTypicalAddressBook(), new UserPrefs());

    @Test
    public void execute_distinctPredicates_yieldDistinctFilteringResults() {
        StoryNameContainsKeywordsPredicate firstPredicate =
                new StoryNameContainsKeywordsPredicate(List.of("A"));
        StoryNameContainsKeywordsPredicate secondPredicate =
                new StoryNameContainsKeywordsPredicate(List.of("C"));

        FindStoriesCommand firstCommand = new FindStoriesCommand(firstPredicate);
        FindStoriesCommand secondCommand = new FindStoriesCommand(secondPredicate);

        Model modelForFirst = new ModelManager(getTypicalAddressBook(), new UserPrefs());
        Model modelForSecond = new ModelManager(getTypicalAddressBook(), new UserPrefs());

        CommandResult firstResult = firstCommand.execute(modelForFirst);
        CommandResult secondResult = secondCommand.execute(modelForSecond);

        assertEquals(String.format(MESSAGE_STORIES_LISTED_OVERVIEW, 1), firstResult.getFeedbackToUser());
        assertEquals(String.format(MESSAGE_STORIES_LISTED_OVERVIEW, 1), secondResult.getFeedbackToUser());

        assertEquals(List.of(STORY_A), modelForFirst.getFilteredStoryList());
        assertEquals(List.of(STORY_C), modelForSecond.getFilteredStoryList());
    }

    @Test
    public void execute_samePredicateInstances_yieldIdenticalFilteringResults() {
        StoryNameContainsKeywordsPredicate firstPredicate =
                new StoryNameContainsKeywordsPredicate(List.of("A"));
        StoryNameContainsKeywordsPredicate secondPredicate =
                new StoryNameContainsKeywordsPredicate(List.of("A"));

        FindStoriesCommand firstCommand = new FindStoriesCommand(firstPredicate);
        FindStoriesCommand secondCommand = new FindStoriesCommand(secondPredicate);

        Model modelForFirst = new ModelManager(getTypicalAddressBook(), new UserPrefs());
        Model modelForSecond = new ModelManager(getTypicalAddressBook(), new UserPrefs());

        CommandResult firstResult = firstCommand.execute(modelForFirst);
        CommandResult secondResult = secondCommand.execute(modelForSecond);

        assertEquals(firstResult.getFeedbackToUser(), secondResult.getFeedbackToUser());
        assertEquals(modelForFirst.getFilteredStoryList(), modelForSecond.getFilteredStoryList());
        assertEquals(List.of(STORY_A), modelForFirst.getFilteredStoryList());
    }

    @Test
    public void execute_multipleKeywords_multipleStoriesFound() {
        StoryNameContainsKeywordsPredicate predicate =
                new StoryNameContainsKeywordsPredicate(List.of("A", "C"));
        FindStoriesCommand command = new FindStoriesCommand(predicate);
        expectedModel.updateFilteredStoryList(predicate);

        assertCommandSuccess(command, model,
                String.format(MESSAGE_STORIES_LISTED_OVERVIEW, 2), expectedModel);
        assertEquals(List.of(STORY_A, STORY_C), model.getFilteredStoryList());
    }

    @Test
    public void execute_nonMatchingKeyword_noStoryFound() {
        StoryNameContainsKeywordsPredicate predicate =
                new StoryNameContainsKeywordsPredicate(List.of("missing"));
        FindStoriesCommand command = new FindStoriesCommand(predicate);
        expectedModel.updateFilteredStoryList(predicate);

        assertCommandSuccess(command, model,
                String.format(MESSAGE_STORIES_LISTED_OVERVIEW, 0), expectedModel);
        assertEquals(List.of(), model.getFilteredStoryList());
    }

    @Test
    public void toStringMethod() {
        StoryNameContainsKeywordsPredicate predicate =
                new StoryNameContainsKeywordsPredicate(List.of("keyword"));
        FindStoriesCommand command = new FindStoriesCommand(predicate);

        String expected = FindStoriesCommand.class.getCanonicalName() + "{predicate=" + predicate + "}";
        assertEquals(expected, command.toString());
    }
}
