package seedu.address.model.story;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.HashSet;
import java.util.List;

import org.junit.jupiter.api.Test;

public class StoryNameContainsKeywordsPredicateTest {

    @Test
    public void test_sameKeywords_sameFilteringBehavior() {
        List<String> keywords = List.of("Housing");
        StoryNameContainsKeywordsPredicate firstPredicate = new StoryNameContainsKeywordsPredicate(keywords);
        StoryNameContainsKeywordsPredicate secondPredicate = new StoryNameContainsKeywordsPredicate(keywords);

        Story matchingStory = new Story(new StoryName("Housing Investigation"), new HashSet<>());
        Story nonMatchingStory = new Story(new StoryName("Healthcare Scandal"), new HashSet<>());

        assertEquals(firstPredicate.test(matchingStory), secondPredicate.test(matchingStory));
        assertTrue(firstPredicate.test(matchingStory));

        assertEquals(firstPredicate.test(nonMatchingStory), secondPredicate.test(nonMatchingStory));
        assertFalse(firstPredicate.test(nonMatchingStory));

        assertEquals(firstPredicate.toString(), secondPredicate.toString());
    }

    @Test
    public void test_differentKeywords_differentFilteringBehavior() {
        StoryNameContainsKeywordsPredicate firstPredicate =
                new StoryNameContainsKeywordsPredicate(List.of("Housing"));
        StoryNameContainsKeywordsPredicate secondPredicate =
                new StoryNameContainsKeywordsPredicate(List.of("Healthcare"));

        Story housingStory = new Story(new StoryName("Housing Investigation"), new HashSet<>());
        Story healthStory = new Story(new StoryName("Healthcare Scandal"), new HashSet<>());

        assertTrue(firstPredicate.test(housingStory));
        assertFalse(secondPredicate.test(housingStory));

        assertFalse(firstPredicate.test(healthStory));
        assertTrue(secondPredicate.test(healthStory));
    }

    @Test
    public void test_nameContainsKeyword_returnsTrue() {
        Story story = new Story(new StoryName("Housing Investigation"), new HashSet<>());

        assertTrue(new StoryNameContainsKeywordsPredicate(List.of("Housing")).test(story));
        assertTrue(new StoryNameContainsKeywordsPredicate(List.of("education", "investigation")).test(story));
        assertTrue(new StoryNameContainsKeywordsPredicate(List.of("hOuSiNg")).test(story));
    }

    @Test
    public void test_nameDoesNotContainKeyword_returnsFalse() {
        Story story = new Story(new StoryName("Housing Investigation"), new HashSet<>());

        assertFalse(new StoryNameContainsKeywordsPredicate(List.of()).test(story));
        assertFalse(new StoryNameContainsKeywordsPredicate(List.of("House")).test(story));
        assertFalse(new StoryNameContainsKeywordsPredicate(List.of("Education")).test(story));
    }

    @Test
    public void toStringMethod() {
        List<String> keywords = List.of("housing", "investigation");
        StoryNameContainsKeywordsPredicate predicate = new StoryNameContainsKeywordsPredicate(keywords);

        String expected = "Filtered: \"housing investigation\"";
        assertEquals(expected, predicate.toString());
    }
}
