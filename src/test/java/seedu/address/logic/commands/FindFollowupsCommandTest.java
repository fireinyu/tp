package seedu.address.logic.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static seedu.address.logic.Messages.MESSAGE_FOLLOWUPS_LISTED_OVERVIEW;
import static seedu.address.logic.commands.CommandTestUtil.assertCommandSuccess;
import static seedu.address.testutil.Assert.assertThrows;
import static seedu.address.testutil.TypicalFollowups.FOLLOWUP_A;
import static seedu.address.testutil.TypicalFollowups.FOLLOWUP_B;
import static seedu.address.testutil.TypicalFollowups.FOLLOWUP_C;
import static seedu.address.testutil.TypicalFollowups.getTypicalAddressBook;

import java.util.List;

import org.junit.jupiter.api.Test;

import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.model.UserPrefs;
import seedu.address.model.followup.FollowupDescriptionContainsKeywordsPredicate;

public class FindFollowupsCommandTest {

    private final Model model =
            new ModelManager(getTypicalAddressBook(), new UserPrefs());
    private final Model expectedModel =
            new ModelManager(getTypicalAddressBook(), new UserPrefs());

    @Test
    public void execute_distinctPredicates_yieldDistinctFilteringResults() {
        FollowupDescriptionContainsKeywordsPredicate firstPredicate =
                new FollowupDescriptionContainsKeywordsPredicate(List.of("A"));
        FollowupDescriptionContainsKeywordsPredicate secondPredicate =
                new FollowupDescriptionContainsKeywordsPredicate(List.of("B"));

        FindFollowupsCommand firstCommand = new FindFollowupsCommand(firstPredicate);
        FindFollowupsCommand secondCommand = new FindFollowupsCommand(secondPredicate);

        Model modelForFirst = new ModelManager(getTypicalAddressBook(), new UserPrefs());
        Model modelForSecond = new ModelManager(getTypicalAddressBook(), new UserPrefs());

        CommandResult firstResult = firstCommand.execute(modelForFirst);
        CommandResult secondResult = secondCommand.execute(modelForSecond);

        assertEquals(String.format(MESSAGE_FOLLOWUPS_LISTED_OVERVIEW, 1), firstResult.getFeedbackToUser());
        assertEquals(String.format(MESSAGE_FOLLOWUPS_LISTED_OVERVIEW, 1), secondResult.getFeedbackToUser());

        assertEquals(List.of(FOLLOWUP_A), modelForFirst.getFilteredFollowupList());
        assertEquals(List.of(FOLLOWUP_B), modelForSecond.getFilteredFollowupList());
    }

    @Test
    public void execute_samePredicateInstances_yieldIdenticalFilteringResults() {
        FollowupDescriptionContainsKeywordsPredicate firstPredicate =
                new FollowupDescriptionContainsKeywordsPredicate(List.of("A"));
        FollowupDescriptionContainsKeywordsPredicate secondPredicate =
                new FollowupDescriptionContainsKeywordsPredicate(List.of("A"));

        FindFollowupsCommand firstCommand = new FindFollowupsCommand(firstPredicate);
        FindFollowupsCommand secondCommand = new FindFollowupsCommand(secondPredicate);

        Model modelForFirst = new ModelManager(getTypicalAddressBook(), new UserPrefs());
        Model modelForSecond = new ModelManager(getTypicalAddressBook(), new UserPrefs());

        CommandResult firstResult = firstCommand.execute(modelForFirst);
        CommandResult secondResult = secondCommand.execute(modelForSecond);

        assertEquals(firstResult.getFeedbackToUser(), secondResult.getFeedbackToUser());
        assertEquals(modelForFirst.getFilteredFollowupList(), modelForSecond.getFilteredFollowupList());
        assertEquals(List.of(FOLLOWUP_A), modelForFirst.getFilteredFollowupList());
    }

    @Test
    public void execute_singleKeyword_oneFollowupFound() {
        FollowupDescriptionContainsKeywordsPredicate predicate =
                new FollowupDescriptionContainsKeywordsPredicate(List.of("B"));

        FindFollowupsCommand command = new FindFollowupsCommand(predicate);
        expectedModel.updateFilteredFollowupList(predicate);

        assertCommandSuccess(command, model, String.format(MESSAGE_FOLLOWUPS_LISTED_OVERVIEW, 1), expectedModel);
        assertEquals(List.of(FOLLOWUP_B), model.getFilteredFollowupList());
    }

    @Test
    public void execute_multipleKeywords_multipleFollowupsFound() {
        FollowupDescriptionContainsKeywordsPredicate predicate =
                new FollowupDescriptionContainsKeywordsPredicate(List.of("A", "C"));

        FindFollowupsCommand command = new FindFollowupsCommand(predicate);
        expectedModel.updateFilteredFollowupList(predicate);

        assertCommandSuccess(command, model, String.format(MESSAGE_FOLLOWUPS_LISTED_OVERVIEW, 2), expectedModel);
        assertEquals(List.of(FOLLOWUP_A, FOLLOWUP_C), model.getFilteredFollowupList());
    }

    @Test
    public void execute_nonMatchingKeyword_noFollowupFound() {
        FollowupDescriptionContainsKeywordsPredicate predicate =
                new FollowupDescriptionContainsKeywordsPredicate(List.of("missing"));

        FindFollowupsCommand command = new FindFollowupsCommand(predicate);
        expectedModel.updateFilteredFollowupList(predicate);

        assertCommandSuccess(command, model, String.format(MESSAGE_FOLLOWUPS_LISTED_OVERVIEW, 0), expectedModel);
        assertEquals(List.of(), model.getFilteredFollowupList());
    }

    @Test
    public void constructor_nullPredicate_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> new FindFollowupsCommand(null));
    }

    @Test
    public void execute_nullModel_throwsNullPointerException() {
        FollowupDescriptionContainsKeywordsPredicate predicate =
                new FollowupDescriptionContainsKeywordsPredicate(List.of("interview"));
        FindFollowupsCommand command = new FindFollowupsCommand(predicate);
        assertThrows(NullPointerException.class, () -> command.execute(null));
    }

    @Test
    public void toStringMethod() {
        FollowupDescriptionContainsKeywordsPredicate predicate =
                new FollowupDescriptionContainsKeywordsPredicate(List.of("interview"));
        FindFollowupsCommand command = new FindFollowupsCommand(predicate);
        String expected = FindFollowupsCommand.class.getCanonicalName() + "{predicate=" + predicate + "}";
        assertEquals(expected, command.toString());
    }
}
