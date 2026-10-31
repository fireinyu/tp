package seedu.address.model.story;

import java.util.List;

import seedu.address.model.BaseKeywordPredicate;
import seedu.address.model.contact.Contact;

/**
 * Tests that a {@code Story}'s name matches any of the keywords given.
 */
public class StoryNameContainsKeywordsPredicate extends BaseKeywordPredicate<Story, Contact> {

    public StoryNameContainsKeywordsPredicate(List<String> keywords) {
        super(keywords);
    }

    @Override
    public boolean matchesKeyword(Story item, String keyword) {
        return item.isNameMatching(keyword);
    }
}
