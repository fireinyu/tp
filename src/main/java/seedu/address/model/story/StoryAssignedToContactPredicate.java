package seedu.address.model.story;


import seedu.address.model.BaseLinkedPredicate;
import seedu.address.model.contact.Contact;

/**
 * Tests that a {@code Story} is assigned to the specified {@code Contact}.
 */
public class StoryAssignedToContactPredicate extends BaseLinkedPredicate<Story, Contact> {
    public StoryAssignedToContactPredicate(Contact key) {
        super(key);
    }

    @Override
    public boolean isLinked(Story item, Contact other) {
        return item.isAssigned(other);
    }
}
