package seedu.address.model.contact;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.logic.commands.CommandTestUtil.VALID_ADDRESS_BOB;
import static seedu.address.logic.commands.CommandTestUtil.VALID_EMAIL_BOB;
import static seedu.address.logic.commands.CommandTestUtil.VALID_NAME_BOB;
import static seedu.address.logic.commands.CommandTestUtil.VALID_PHONE_BOB;
import static seedu.address.logic.commands.CommandTestUtil.VALID_TAG_HUSBAND;
import static seedu.address.testutil.Assert.assertThrows;
import static seedu.address.testutil.TypicalContacts.ALICE;
import static seedu.address.testutil.TypicalContacts.BOB;

import java.util.Set;

import org.junit.jupiter.api.Test;

import seedu.address.model.story.Story;
import seedu.address.model.story.StoryName;
import seedu.address.testutil.ContactBuilder;

public class ContactTest {

    @Test
    public void asObservableList_modifyList_throwsUnsupportedOperationException() {
        Contact contact = new ContactBuilder().build();
        assertThrows(UnsupportedOperationException.class, () -> contact.getTags().remove(0));
    }

    @Test
    public void representsSameItem() {
        // same object -> returns true
        assertTrue(ALICE.representsSameItem(ALICE));

        // null -> throws NullPointerException
        assertThrows(NullPointerException.class, () -> ALICE.representsSameItem(null));

        // same name, all other attributes different -> returns true
        Contact editedAlice = new ContactBuilder(ALICE).withPhone(VALID_PHONE_BOB).withEmail(VALID_EMAIL_BOB)
                .withAddress(VALID_ADDRESS_BOB).withTags(VALID_TAG_HUSBAND).build();
        assertTrue(ALICE.representsSameItem(editedAlice));

        // different name, all other attributes same -> returns false
        editedAlice = new ContactBuilder(ALICE).withName(VALID_NAME_BOB).build();
        assertFalse(ALICE.representsSameItem(editedAlice));

        // name differs in case, all other attributes same -> returns false
        Contact editedBob = new ContactBuilder(BOB).withName(VALID_NAME_BOB.toLowerCase()).build();
        assertFalse(BOB.representsSameItem(editedBob));

        // name has trailing spaces, all other attributes same -> returns false
        String nameWithTrailingSpaces = VALID_NAME_BOB + " ";
        editedBob = new ContactBuilder(BOB).withName(nameWithTrailingSpaces).build();
        assertFalse(BOB.representsSameItem(editedBob));
    }

    @Test
    public void equals() {
        // same values -> returns true
        Contact aliceCopy = new ContactBuilder(ALICE).build();
        assertTrue(ALICE.equals(aliceCopy));

        // same object -> returns true
        assertTrue(ALICE.equals(ALICE));

        // null -> returns false
        assertFalse(ALICE.equals(null));

        // different type -> returns false
        assertFalse(ALICE.equals(5));

        // different contact -> returns false
        assertFalse(ALICE.equals(BOB));

        // different name -> returns false
        Contact editedAlice = new ContactBuilder(ALICE).withName(VALID_NAME_BOB).build();
        assertFalse(ALICE.equals(editedAlice));

        // different phone -> returns false
        editedAlice = new ContactBuilder(ALICE).withPhone(VALID_PHONE_BOB).build();
        assertFalse(ALICE.equals(editedAlice));

        // different email -> returns false
        editedAlice = new ContactBuilder(ALICE).withEmail(VALID_EMAIL_BOB).build();
        assertFalse(ALICE.equals(editedAlice));

        // different address -> returns false
        editedAlice = new ContactBuilder(ALICE).withAddress(VALID_ADDRESS_BOB).build();
        assertFalse(ALICE.equals(editedAlice));

        // different tags -> returns false
        editedAlice = new ContactBuilder(ALICE).withTags(VALID_TAG_HUSBAND).build();
        assertFalse(ALICE.equals(editedAlice));

        // different assigned stories -> returns false
        Story story = new Story(new StoryName("test"), Set.of());
        editedAlice = ALICE.assign(story);
        assertFalse(ALICE.equals(editedAlice));

        // same assigned stories -> returns true
        Contact aliceWithStoryCopy = ALICE.assign(story);
        assertTrue(editedAlice.equals(aliceWithStoryCopy));
        assertEquals(editedAlice.hashCode(), aliceWithStoryCopy.hashCode());
    }

    @Test
    public void toStringMethod() {
        String expected = Contact.class.getCanonicalName() + "{name=" + ALICE.getName() + ", phone=" + ALICE.getPhone()
                + ", email=" + ALICE.getEmail() + ", address=" + ALICE.getAddress() + ", tags=" + ALICE.getTags()
                + ", stories=" + ALICE.getStories() + "}";
        assertEquals(expected, ALICE.toString());
    }

    @Test
    public void stories_emptyInitially() {
        Contact p = new ContactBuilder().build();
        assertEquals(0, p.getStories().size());
    }

    @Test
    public void isAssigned_notAssigned_returnsFalse() {
        Contact p = new ContactBuilder().build();
        Story s = new Story(new StoryName("test"), Set.of());
        assertFalse(p.isAssigned(s));
    }

    @Test
    public void assign_newStory_addsStory() {
        Contact p = new ContactBuilder().build();
        Story s = new Story(new StoryName("test"), Set.of());
        Contact p1 = p.assign(s);
        assertTrue(p1.isAssigned(s));
        assertFalse(s.isAssigned(p));
        assertFalse(p.isAssigned(s));
    }

    @Test
    public void unassign_notAssigned_doesNothing() {
        Story a = new Story(new StoryName("ant"), Set.of());
        Story b = new Story(new StoryName("blu"), Set.of());
        Contact p = new ContactBuilder().build();
        Contact p1 = p.assign(a).unassign(b);
        assertTrue(p1.isAssigned(a));
        assertFalse(p1.isAssigned(b));
    }

    @Test
    public void isNameMatching() {
        Contact contact = new ContactBuilder().withName("Alice Pauline").build();

        // matches name keyword
        assertTrue(contact.isNameMatching("Alice"));
        assertTrue(contact.isNameMatching("pauline")); // case-insensitive

        // does not match
        assertFalse(contact.isNameMatching("Bob"));
        assertFalse(contact.isNameMatching("Ali"));
    }
}
