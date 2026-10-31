package seedu.address.logic.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.logic.parser.CliSyntax.PREFIX_ADDRESS;
import static seedu.address.logic.parser.CliSyntax.PREFIX_EMAIL;
import static seedu.address.logic.parser.CliSyntax.PREFIX_NAME;
import static seedu.address.logic.parser.CliSyntax.PREFIX_PHONE;
import static seedu.address.logic.parser.CliSyntax.PREFIX_TAG;
import static seedu.address.testutil.Assert.assertThrows;

import java.util.ArrayList;
import java.util.List;

import seedu.address.commons.core.index.Index;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.AddressBook;
import seedu.address.model.Model;
import seedu.address.model.StateAwarePredicate;
import seedu.address.model.contact.Contact;
import seedu.address.model.contact.NameContainsKeywordsPredicate;
import seedu.address.model.followup.Followup;
import seedu.address.model.story.Story;
import seedu.address.model.story.StoryName;
import seedu.address.testutil.EditContactDescriptorBuilder;
import seedu.address.testutil.EditStoryDescriptorBuilder;

/**
 * Contains helper methods for testing commands.
 */
public class CommandTestUtil {

    public static final String VALID_NAME_AMY = "Amy Bee";
    public static final String VALID_NAME_BOB = "Bob Choo";
    public static final String VALID_PHONE_AMY = "11111111";
    public static final String VALID_PHONE_BOB = "22222222";
    public static final String VALID_EMAIL_AMY = "amy@example.com";
    public static final String VALID_EMAIL_BOB = "bob@example.com";
    public static final String VALID_ADDRESS_AMY = "Block 312, Amy Street 1";
    public static final String VALID_ADDRESS_BOB = "Block 123, Bobby Street 3";
    public static final String VALID_TAG_HUSBAND = "husband";
    public static final String VALID_TAG_FRIEND = "friend";

    public static final String NAME_DESC_AMY = " " + PREFIX_NAME + VALID_NAME_AMY;
    public static final String NAME_DESC_BOB = " " + PREFIX_NAME + VALID_NAME_BOB;
    public static final String PHONE_DESC_AMY = " " + PREFIX_PHONE + VALID_PHONE_AMY;
    public static final String PHONE_DESC_BOB = " " + PREFIX_PHONE + VALID_PHONE_BOB;
    public static final String EMAIL_DESC_AMY = " " + PREFIX_EMAIL + VALID_EMAIL_AMY;
    public static final String EMAIL_DESC_BOB = " " + PREFIX_EMAIL + VALID_EMAIL_BOB;
    public static final String ADDRESS_DESC_AMY = " " + PREFIX_ADDRESS + VALID_ADDRESS_AMY;
    public static final String ADDRESS_DESC_BOB = " " + PREFIX_ADDRESS + VALID_ADDRESS_BOB;
    public static final String TAG_DESC_FRIEND = " " + PREFIX_TAG + VALID_TAG_FRIEND;
    public static final String TAG_DESC_HUSBAND = " " + PREFIX_TAG + VALID_TAG_HUSBAND;

    public static final String INVALID_NAME_DESC = " " + PREFIX_NAME; // empty string not allowed for names
    public static final String INVALID_PHONE_DESC = " " + PREFIX_PHONE + "911a"; // 'a' not allowed in phones
    public static final String INVALID_EMAIL_DESC = " " + PREFIX_EMAIL + "bob!yahoo"; // missing '@' symbol
    public static final String INVALID_ADDRESS_DESC = " " + PREFIX_ADDRESS; // empty string not allowed for addresses
    public static final String INVALID_TAG_DESC = " " + PREFIX_TAG + " " + PREFIX_TAG; // empty tag not allowed

    public static final String PREAMBLE_WHITESPACE = "\t  \r  \n";
    public static final String PREAMBLE_NON_EMPTY = "NonEmptyPreamble";

    public static final StateAwarePredicate<Contact, Story> PREDICATE_SHOW_NO_CONTACTS =
            new StateAwarePredicate<>() {
                @Override
                public boolean test(Contact contact) {
                    return false;
                }
            };
    public static final StateAwarePredicate<Story, Contact> PREDICATE_SHOW_NO_STORIES =
            new StateAwarePredicate<>() {
                @Override
                public boolean test(Story story) {
                    return false;
                }
            };
    public static final StateAwarePredicate<Followup, Contact> PREDICATE_SHOW_NO_FOLLOWUPS =
            new StateAwarePredicate<>() {
                @Override
                public boolean test(Followup followup) {
                    return false;
                }
            };

    public static final EditContactCommand.EditContactDescriptor DESC_AMY;
    public static final EditContactCommand.EditContactDescriptor DESC_BOB;

    static {
        DESC_AMY = new EditContactDescriptorBuilder().withName(VALID_NAME_AMY)
                .withPhone(VALID_PHONE_AMY).withEmail(VALID_EMAIL_AMY).withAddress(VALID_ADDRESS_AMY)
                .withTags(VALID_TAG_FRIEND).build();
        DESC_BOB = new EditContactDescriptorBuilder().withName(VALID_NAME_BOB)
                .withPhone(VALID_PHONE_BOB).withEmail(VALID_EMAIL_BOB).withAddress(VALID_ADDRESS_BOB)
                .withTags(VALID_TAG_HUSBAND, VALID_TAG_FRIEND).build();
    }

    //=========== Story ==================================================================================
    public static final String VALID_STORYNAME_CRIME = "Crime Investigation";
    public static final String VALID_STORYNAME_FRAUD = "Financial Fraud";

    public static final String VALID_STORYTAG_SINGAPORE = "Singapore";
    public static final String VALID_STORYTAG_MALAYSIA = "Malaysia";

    public static final String NAME_DESC_CRIME = " " + PREFIX_NAME + VALID_STORYNAME_CRIME;
    public static final String NAME_DESC_FRAUD = " " + PREFIX_NAME + VALID_STORYNAME_FRAUD;
    public static final String TAG_DESC_SINGAPORE = " " + PREFIX_TAG + VALID_STORYTAG_SINGAPORE;
    public static final String TAG_DESC_MALAYSIA = " " + PREFIX_TAG + VALID_STORYTAG_MALAYSIA;

    public static final String INVALID_STORYNAME_DESC = " " + PREFIX_NAME; // empty string not allowed for story names
    public static final String INVALID_STORYTAG_DESC = " " + PREFIX_TAG + " " + PREFIX_TAG; // empty tag not allowed

    public static final EditStoryCommand.EditStoryDescriptor DESC_CRIME;
    public static final EditStoryCommand.EditStoryDescriptor DESC_FRAUD;

    static {
        DESC_CRIME = new EditStoryDescriptorBuilder().withName(VALID_STORYNAME_CRIME)
                .withTags(VALID_STORYTAG_SINGAPORE).build();
        DESC_FRAUD = new EditStoryDescriptorBuilder().withName(VALID_STORYNAME_FRAUD)
                .withTags(VALID_STORYTAG_MALAYSIA).build();
    }

    /**
     * Executes the given {@code command}, confirms that <br>
     * - the returned {@link CommandResult} matches {@code expectedCommandResult} <br>
     * - the {@code actualModel} matches {@code expectedModel}
     */
    public static void assertCommandSuccess(Command command, Model actualModel, CommandResult expectedCommandResult,
            Model expectedModel) {
        try {
            CommandResult result = command.execute(actualModel);
            assertEquals(expectedCommandResult, result);
            assertEquals(expectedModel, actualModel);
        } catch (CommandException ce) {
            throw new AssertionError("Execution of command should not fail.", ce);
        }
    }

    /**
     * Convenience wrapper to {@link #assertCommandSuccess(Command, Model, CommandResult, Model)}
     * that takes a string {@code expectedMessage}.
     */
    public static void assertCommandSuccess(Command command, Model actualModel, String expectedMessage,
            Model expectedModel) {
        CommandResult expectedCommandResult = new CommandResult(expectedMessage);
        assertCommandSuccess(command, actualModel, expectedCommandResult, expectedModel);
    }

    /**
     * Executes the given {@code command}, confirms that <br>
     * - a {@code CommandException} is thrown <br>
     * - the CommandException message matches {@code expectedMessage} <br>
     * - the address book, filtered contact list, and filtered story list in
     *   {@code actualModel} remain unchanged
     */
    public static void assertCommandFailure(Command command, Model actualModel, String expectedMessage) {
        // we are unable to defensively copy the model for comparison later, so we can
        // only do so by copying its components.
        AddressBook expectedAddressBook = new AddressBook(actualModel.getAddressBook());
        List<Contact> expectedFilteredList = new ArrayList<>(actualModel.getFilteredContactList());
        List<Story> expectedFilteredStoryList = new ArrayList<>(actualModel.getFilteredStoryList());

        assertThrows(CommandException.class, expectedMessage, () -> command.execute(actualModel));
        assertEquals(expectedAddressBook, actualModel.getAddressBook());
        assertEquals(expectedFilteredList, actualModel.getFilteredContactList());
        assertEquals(expectedFilteredStoryList, actualModel.getFilteredStoryList());
    }
    /**
     * Updates {@code model}'s filtered list to show only the contact at the given {@code targetIndex} in the
     * {@code model}'s address book.
     */
    public static void showContactAtIndex(Model model, Index targetIndex) {
        assertTrue(targetIndex.getZeroBased() < model.getFilteredContactList().size());

        Contact contact = model.getFilteredContactList().get(targetIndex.getZeroBased());
        final String[] splitName = contact.getName().toString().split("\\s+");
        model.updateFilteredContactList(new NameContainsKeywordsPredicate(List.of(splitName[0])));

        assertEquals(1, model.getFilteredContactList().size());
    }

    /**
     * A predicate that returns true if the contact equals the specified contact.
     */
    public static class ContactEqualsPredicate extends StateAwarePredicate<Contact, Story> {
        private final Contact contact;

        public ContactEqualsPredicate(Contact contact) {
            this.contact = contact;
        }

        @Override
        public boolean test(Contact c) {
            return c.equals(contact);
        }

        @Override
        public boolean equals(Object other) {
            return other == this || (other instanceof ContactEqualsPredicate
                    && contact.equals(((ContactEqualsPredicate) other).contact));
        }
    }

    /**
     * A predicate that returns true if the story's name equals the specified story name.
     */
    public static class StoryNameEqualsPredicate extends StateAwarePredicate<Story, Contact> {
        private final StoryName storyName;

        public StoryNameEqualsPredicate(StoryName storyName) {
            this.storyName = storyName;
        }

        @Override
        public boolean test(Story s) {
            return s.getName().equals(storyName);
        }

        @Override
        public boolean equals(Object other) {
            return other == this || (other instanceof StoryNameEqualsPredicate
                    && storyName.equals(((StoryNameEqualsPredicate) other).storyName));
        }
    }

    /**
     * A predicate that returns true if the story equals the specified story.
     */
    public static class StoryEqualsPredicate extends StateAwarePredicate<Story, Contact> {
        private final Story story;

        public StoryEqualsPredicate(Story story) {
            this.story = story;
        }

        @Override
        public boolean test(Story s) {
            return s.equals(story);
        }

        @Override
        public boolean equals(Object other) {
            return other == this || (other instanceof StoryEqualsPredicate
                    && story.equals(((StoryEqualsPredicate) other).story));
        }
    }

    /**
     * A predicate that returns true if the followup equals the specified followup.
     */
    public static class FollowupEqualsPredicate extends StateAwarePredicate<Followup, Contact> {
        private final Followup followup;

        public FollowupEqualsPredicate(Followup followup) {
            this.followup = followup;
        }

        @Override
        public boolean test(Followup f) {
            return f.equals(followup);
        }

        @Override
        public boolean equals(Object other) {
            return other == this || (other instanceof FollowupEqualsPredicate
                    && followup.equals(((FollowupEqualsPredicate) other).followup));
        }
    }

    /**
     * Updates {@code model}'s filtered list to show only the story at the given {@code targetIndex}.
     */
    public static void showStoryAtIndex(Model model, Index targetIndex) {
        assertTrue(targetIndex.getZeroBased() < model.getFilteredStoryList().size());

        Story story = model.getFilteredStoryList().get(targetIndex.getZeroBased());
        model.updateFilteredStoryList(new StoryEqualsPredicate(story));

        assertEquals(1, model.getFilteredStoryList().size());
    }

    /**
     * Updates {@code model}'s filtered list to show only the followup at the given {@code targetIndex} in the
     * {@code model}'s address book.
     */
    public static void showFollowupAtIndex(Model model, Index targetIndex) {
        assertTrue(targetIndex.getZeroBased() < model.getFilteredFollowupList().size());

        Followup followup = model.getFilteredFollowupList().get(targetIndex.getZeroBased());
        model.updateFilteredFollowupList(new FollowupEqualsPredicate(followup));

        assertEquals(1, model.getFilteredFollowupList().size());
    }
}

