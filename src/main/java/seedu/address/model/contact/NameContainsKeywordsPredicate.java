package seedu.address.model.contact;

import java.util.List;

import seedu.address.model.BaseKeywordPredicate;
import seedu.address.model.story.Story;

/**
 * Tests that a {@code Contact}'s {@code Name} matches any of the keywords given.
 */
public class NameContainsKeywordsPredicate extends BaseKeywordPredicate<Contact, Story> {

    public NameContainsKeywordsPredicate(List<String> keywords) {
        super(keywords);
    }

    @Override
    public boolean matchesKeyword(Contact item, String keyword) {
        return item.isNameMatching(keyword);
    }
}
