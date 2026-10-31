package seedu.address.logic.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static seedu.address.logic.Messages.MESSAGE_CONTACTS_LISTED_OVERVIEW;
import static seedu.address.logic.commands.CommandTestUtil.assertCommandSuccess;
import static seedu.address.testutil.TypicalContacts.CARL;
import static seedu.address.testutil.TypicalContacts.ELLE;
import static seedu.address.testutil.TypicalContacts.FIONA;
import static seedu.address.testutil.TypicalContacts.getTypicalAddressBook;

import java.util.List;

import org.junit.jupiter.api.Test;

import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.model.UserPrefs;
import seedu.address.model.contact.NameContainsKeywordsPredicate;

/**
 * Contains integration tests (interaction with the Model) for {@code FindContactsCommand}.
 */
public class FindContactsCommandTest {
    private Model model = new ModelManager(getTypicalAddressBook(), new UserPrefs());
    private Model expectedModel = new ModelManager(getTypicalAddressBook(), new UserPrefs());

    @Test
    public void execute_distinctPredicates_yieldDistinctFilteringResults() {
        NameContainsKeywordsPredicate firstPredicate =
                new NameContainsKeywordsPredicate(List.of("Kurz"));
        NameContainsKeywordsPredicate secondPredicate =
                new NameContainsKeywordsPredicate(List.of("Elle"));

        FindContactsCommand findFirstCommand = new FindContactsCommand(firstPredicate);
        FindContactsCommand findSecondCommand = new FindContactsCommand(secondPredicate);

        Model modelForFirst = new ModelManager(getTypicalAddressBook(), new UserPrefs());
        Model modelForSecond = new ModelManager(getTypicalAddressBook(), new UserPrefs());

        CommandResult firstResult = findFirstCommand.execute(modelForFirst);
        CommandResult secondResult = findSecondCommand.execute(modelForSecond);

        assertEquals(String.format(MESSAGE_CONTACTS_LISTED_OVERVIEW, 1), firstResult.getFeedbackToUser());
        assertEquals(String.format(MESSAGE_CONTACTS_LISTED_OVERVIEW, 1), secondResult.getFeedbackToUser());

        assertEquals(List.of(CARL), modelForFirst.getFilteredContactList());
        assertEquals(List.of(ELLE), modelForSecond.getFilteredContactList());
    }

    @Test
    public void execute_samePredicateInstances_yieldIdenticalFilteringResults() {
        NameContainsKeywordsPredicate firstPredicate =
                new NameContainsKeywordsPredicate(List.of("Kurz"));
        NameContainsKeywordsPredicate secondPredicate =
                new NameContainsKeywordsPredicate(List.of("Kurz"));

        FindContactsCommand findFirstCommand = new FindContactsCommand(firstPredicate);
        FindContactsCommand findSecondCommand = new FindContactsCommand(secondPredicate);

        Model modelForFirst = new ModelManager(getTypicalAddressBook(), new UserPrefs());
        Model modelForSecond = new ModelManager(getTypicalAddressBook(), new UserPrefs());

        CommandResult firstResult = findFirstCommand.execute(modelForFirst);
        CommandResult secondResult = findSecondCommand.execute(modelForSecond);

        assertEquals(firstResult.getFeedbackToUser(), secondResult.getFeedbackToUser());
        assertEquals(modelForFirst.getFilteredContactList(), modelForSecond.getFilteredContactList());
        assertEquals(List.of(CARL), modelForFirst.getFilteredContactList());
    }

    @Test
    public void execute_zeroKeywords_noContactFound() {
        String expectedMessage = String.format(MESSAGE_CONTACTS_LISTED_OVERVIEW, 0);
        NameContainsKeywordsPredicate predicate = preparePredicate(" ");
        FindContactsCommand command = new FindContactsCommand(predicate);
        expectedModel.updateFilteredContactList(predicate);
        assertCommandSuccess(command, model, expectedMessage, expectedModel);
        assertEquals(List.of(), model.getFilteredContactList());
    }

    @Test
    public void execute_multipleKeywords_multipleContactsFound() {
        String expectedMessage = String.format(MESSAGE_CONTACTS_LISTED_OVERVIEW, 3);
        NameContainsKeywordsPredicate predicate = preparePredicate("Kurz Elle Kunz");
        FindContactsCommand command = new FindContactsCommand(predicate);
        expectedModel.updateFilteredContactList(predicate);
        assertCommandSuccess(command, model, expectedMessage, expectedModel);
        assertEquals(List.of(CARL, ELLE, FIONA), model.getFilteredContactList());
    }

    @Test
    public void toStringMethod() {
        NameContainsKeywordsPredicate predicate = new NameContainsKeywordsPredicate(List.of("keyword"));
        FindContactsCommand findContactsCommand = new FindContactsCommand(predicate);
        String expected = FindContactsCommand.class.getCanonicalName() + "{predicate=" + predicate + "}";
        assertEquals(expected, findContactsCommand.toString());
    }

    /**
     * Parses {@code userInput} into a {@code NameContainsKeywordsPredicate}.
     */
    private NameContainsKeywordsPredicate preparePredicate(String userInput) {
        return new NameContainsKeywordsPredicate(List.of(userInput.split("\\s+")));
    }
}
