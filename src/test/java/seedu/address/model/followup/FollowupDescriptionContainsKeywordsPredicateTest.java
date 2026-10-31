package seedu.address.model.followup;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.Test;

import seedu.address.model.contact.Name;

public class FollowupDescriptionContainsKeywordsPredicateTest {

    private final Followup followup = new Followup(
            new FollowupDescription("Schedule interview with editor"),
            new Name("John Doe"));

    @Test
    public void test_descriptionContainsKeywords_returnsTrue() {
        // One keyword
        FollowupDescriptionContainsKeywordsPredicate predicate =
                new FollowupDescriptionContainsKeywordsPredicate(List.of("interview"));
        assertTrue(predicate.test(followup));

        // Multiple matching keywords
        predicate = new FollowupDescriptionContainsKeywordsPredicate(
                List.of("interview", "editor"));
        assertTrue(predicate.test(followup));

        // Only one keyword matches
        predicate = new FollowupDescriptionContainsKeywordsPredicate(
                List.of("meeting", "editor"));
        assertTrue(predicate.test(followup));

        // Case-insensitive matching
        predicate = new FollowupDescriptionContainsKeywordsPredicate(
                List.of("INTERVIEW"));
        assertTrue(predicate.test(followup));
    }

    @Test
    public void test_descriptionDoesNotContainKeywords_returnsFalse() {
        // No keywords
        FollowupDescriptionContainsKeywordsPredicate predicate =
                new FollowupDescriptionContainsKeywordsPredicate(List.of());
        assertFalse(predicate.test(followup));

        // Non-matching keyword
        predicate = new FollowupDescriptionContainsKeywordsPredicate(
                List.of("meeting"));
        assertFalse(predicate.test(followup));

        // Partial word does not match
        predicate = new FollowupDescriptionContainsKeywordsPredicate(
                List.of("inter"));
        assertFalse(predicate.test(followup));

        // Contact name should not be searched
        predicate = new FollowupDescriptionContainsKeywordsPredicate(
                List.of("John"));
        assertFalse(predicate.test(followup));
    }

    @Test
    public void test_sameKeywords_sameFilteringBehavior() {
        FollowupDescriptionContainsKeywordsPredicate first =
                new FollowupDescriptionContainsKeywordsPredicate(
                        List.of("interview"));
        FollowupDescriptionContainsKeywordsPredicate second =
                new FollowupDescriptionContainsKeywordsPredicate(
                        List.of("interview"));

        Followup nonMatchingFollowup = new Followup(
                new FollowupDescription("Submit draft report"),
                new Name("John Doe"));

        assertEquals(first.test(followup), second.test(followup));
        assertTrue(first.test(followup));

        assertEquals(first.test(nonMatchingFollowup), second.test(nonMatchingFollowup));
        assertFalse(first.test(nonMatchingFollowup));

        assertEquals(first.toString(), second.toString());
    }

    @Test
    public void test_differentKeywords_differentFilteringBehavior() {
        FollowupDescriptionContainsKeywordsPredicate first =
                new FollowupDescriptionContainsKeywordsPredicate(
                        List.of("interview"));
        FollowupDescriptionContainsKeywordsPredicate second =
                new FollowupDescriptionContainsKeywordsPredicate(
                        List.of("meeting"));

        Followup meetingFollowup = new Followup(
                new FollowupDescription("Meeting with editor"),
                new Name("John Doe"));

        assertTrue(first.test(followup));
        assertFalse(second.test(followup));

        assertFalse(first.test(meetingFollowup));
        assertTrue(second.test(meetingFollowup));
    }

    @Test
    public void toStringMethod() {
        List<String> keywords = List.of("interview", "meeting");
        FollowupDescriptionContainsKeywordsPredicate predicate =
                new FollowupDescriptionContainsKeywordsPredicate(keywords);

        String expected = "Filtered: \"interview meeting\"";
        assertEquals(expected, predicate.toString());
    }
}
