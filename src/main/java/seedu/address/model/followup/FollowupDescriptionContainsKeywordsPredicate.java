package seedu.address.model.followup;

import java.util.List;

import seedu.address.model.BaseKeywordPredicate;
import seedu.address.model.contact.Contact;

/**
 * Tests that a Followup's description contains any of the keywords given.
 */
public class FollowupDescriptionContainsKeywordsPredicate
        extends BaseKeywordPredicate<Followup, Contact> {

    public FollowupDescriptionContainsKeywordsPredicate(List<String> keywords) {
        super(keywords);
    }

    @Override
    public boolean matchesKeyword(Followup item, String keyword) {
        return item.isDescriptionMatching(keyword);
    }
}
