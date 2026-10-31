package seedu.address.model.contact;

import seedu.address.model.BaseLinkedPredicate;
import seedu.address.model.story.Story;

/**
 * Tests that a {@code Contact} is assigned to the specified {@code Story}.
 */
public class ContactAssignedToStoryPredicate extends BaseLinkedPredicate<Contact, Story> {
    public ContactAssignedToStoryPredicate(Story key) {
        super(key);
    }

    @Override
    public boolean isLinked(Contact item, Story other) {
        return item.isAssigned(other);
    }
}
