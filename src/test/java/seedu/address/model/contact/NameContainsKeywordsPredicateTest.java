package seedu.address.model.contact;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.Test;

import seedu.address.testutil.ContactBuilder;

public class NameContainsKeywordsPredicateTest {

    @Test
    public void test_sameKeywords_sameFilteringBehavior() {
        List<String> keywords = List.of("Alice", "Bob");
        NameContainsKeywordsPredicate firstPredicate = new NameContainsKeywordsPredicate(keywords);
        NameContainsKeywordsPredicate secondPredicate = new NameContainsKeywordsPredicate(keywords);

        Contact matchingContact = new ContactBuilder().withName("Alice Bob").build();
        Contact nonMatchingContact = new ContactBuilder().withName("Carol Danvers").build();

        assertEquals(firstPredicate.test(matchingContact), secondPredicate.test(matchingContact));
        assertTrue(firstPredicate.test(matchingContact));

        assertEquals(firstPredicate.test(nonMatchingContact), secondPredicate.test(nonMatchingContact));
        assertFalse(firstPredicate.test(nonMatchingContact));

        assertEquals(firstPredicate.toString(), secondPredicate.toString());
    }

    @Test
    public void test_differentKeywords_differentFilteringBehavior() {
        NameContainsKeywordsPredicate firstPredicate = new NameContainsKeywordsPredicate(List.of("Alice"));
        NameContainsKeywordsPredicate secondPredicate = new NameContainsKeywordsPredicate(List.of("Bob"));

        Contact alice = new ContactBuilder().withName("Alice").build();
        Contact bob = new ContactBuilder().withName("Bob").build();

        assertTrue(firstPredicate.test(alice));
        assertFalse(secondPredicate.test(alice));

        assertFalse(firstPredicate.test(bob));
        assertTrue(secondPredicate.test(bob));
    }

    @Test
    public void test_nameContainsKeywords_returnsTrue() {
        // One keyword
        NameContainsKeywordsPredicate predicate = new NameContainsKeywordsPredicate(List.of("Alice"));
        assertTrue(predicate.test(new ContactBuilder().withName("Alice Bob").build()));

        // Multiple keywords
        predicate = new NameContainsKeywordsPredicate(List.of("Alice", "Bob"));
        assertTrue(predicate.test(new ContactBuilder().withName("Alice Bob").build()));

        // Only one matching keyword
        predicate = new NameContainsKeywordsPredicate(List.of("Bob", "Carol"));
        assertTrue(predicate.test(new ContactBuilder().withName("Alice Carol").build()));

        // Mixed-case keywords
        predicate = new NameContainsKeywordsPredicate(List.of("aLIce", "bOB"));
        assertTrue(predicate.test(new ContactBuilder().withName("Alice Bob").build()));
    }

    @Test
    public void test_nameDoesNotContainKeywords_returnsFalse() {
        // Zero keywords
        NameContainsKeywordsPredicate predicate = new NameContainsKeywordsPredicate(List.of());
        assertFalse(predicate.test(new ContactBuilder().withName("Alice").build()));

        // Non-matching keyword
        predicate = new NameContainsKeywordsPredicate(List.of("Carol"));
        assertFalse(predicate.test(new ContactBuilder().withName("Alice Bob").build()));

        // Keywords match phone, email and address, but do not match name
        predicate = new NameContainsKeywordsPredicate(List.of("12345", "alice@email.com", "Main", "Street"));
        assertFalse(predicate.test(new ContactBuilder().withName("Alice").withPhone("12345")
                .withEmail("alice@email.com").withAddress("Main Street").build()));
    }

    @Test
    public void toStringMethod() {
        List<String> keywords = List.of("keyword1", "keyword2");
        NameContainsKeywordsPredicate predicate = new NameContainsKeywordsPredicate(keywords);

        String expected = "Filtered: \"keyword1 keyword2\"";
        assertEquals(expected, predicate.toString());
    }
}
