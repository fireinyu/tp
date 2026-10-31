package seedu.address.logic.parser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.logic.Messages.MESSAGE_CONTACTS_LISTED_OVERVIEW;
import static seedu.address.logic.Messages.MESSAGE_FOLLOWUPS_LISTED_OVERVIEW;
import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.Messages.MESSAGE_STORIES_LISTED_OVERVIEW;
import static seedu.address.logic.Messages.MESSAGE_UNKNOWN_COMMAND;
import static seedu.address.logic.parser.CliSyntax.PREFIX_CONTACTINDEX;
import static seedu.address.logic.parser.CliSyntax.PREFIX_DESCRIPTION;
import static seedu.address.logic.parser.CliSyntax.PREFIX_NAME;
import static seedu.address.logic.parser.CliSyntax.PREFIX_STORYINDEX;
import static seedu.address.testutil.Assert.assertThrows;
import static seedu.address.testutil.TypicalIndexes.INDEX_FIRST_CONTACT;
import static seedu.address.testutil.TypicalIndexes.INDEX_FIRST_STORY;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.junit.jupiter.api.Test;

import seedu.address.commons.core.index.Index;
import seedu.address.logic.commands.AddContactCommand;
import seedu.address.logic.commands.AddFollowupCommand;
import seedu.address.logic.commands.AddStoryCommand;
import seedu.address.logic.commands.AssignCommand;
import seedu.address.logic.commands.ClearCommand;
import seedu.address.logic.commands.Command;
import seedu.address.logic.commands.CommandResult;
import seedu.address.logic.commands.DeleteContactCommand;
import seedu.address.logic.commands.DeleteFollowupCommand;
import seedu.address.logic.commands.DeleteStoryCommand;
import seedu.address.logic.commands.EditContactCommand;
import seedu.address.logic.commands.EditContactCommand.EditContactDescriptor;
import seedu.address.logic.commands.EditFollowupCommand;
import seedu.address.logic.commands.EditFollowupCommand.EditFollowupDescriptor;
import seedu.address.logic.commands.EditStoryCommand;
import seedu.address.logic.commands.EditStoryCommand.EditStoryDescriptor;
import seedu.address.logic.commands.ExitCommand;
import seedu.address.logic.commands.FindContactsCommand;
import seedu.address.logic.commands.FindFollowupsCommand;
import seedu.address.logic.commands.FindStoriesCommand;
import seedu.address.logic.commands.HelpCommand;
import seedu.address.logic.commands.ListCommand;
import seedu.address.logic.commands.ListContactsCommand;
import seedu.address.logic.commands.ListFollowupsCommand;
import seedu.address.logic.commands.ListStoriesCommand;
import seedu.address.logic.commands.ShortcutCommand;
import seedu.address.logic.commands.UnassignCommand;
import seedu.address.logic.commands.UnshortcutCommand;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.AddressBook;
import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.model.UserPrefs;
import seedu.address.model.contact.Contact;
import seedu.address.model.contact.Name;
import seedu.address.model.followup.Followup;
import seedu.address.model.followup.FollowupDescription;
import seedu.address.model.story.Story;
import seedu.address.model.story.StoryName;
import seedu.address.testutil.ContactBuilder;
import seedu.address.testutil.ContactUtil;
import seedu.address.testutil.EditContactDescriptorBuilder;

public class AddressBookParserTest {

    private final AddressBookParser parser = new AddressBookParser();

    @Test
    public void parseCommand_add() throws Exception {
        Contact contact = new ContactBuilder().build();
        AddContactCommand command = (AddContactCommand) parser.parseCommand(ContactUtil.getAddCommand(contact));
        assertEquals(new AddContactCommand(contact), command);
    }

    @Test
    public void parseCommand_assign() throws Exception {
        Index contactIndex = Index.fromOneBased(1);
        Index storyIndex = Index.fromOneBased(2);
        AssignCommand command = (AssignCommand) parser.parseCommand(AssignCommand.COMMAND_WORD + " "
                + PREFIX_CONTACTINDEX + contactIndex.getOneBased() + " "
                + PREFIX_STORYINDEX + storyIndex.getOneBased());

        assertEquals(new AssignCommand(storyIndex, contactIndex), command);
    }

    @Test
    public void parseCommand_clear() throws Exception {
        assertTrue(parser.parseCommand(ClearCommand.COMMAND_WORD) instanceof ClearCommand);
        assertTrue(parser.parseCommand(ClearCommand.COMMAND_WORD + "   ") instanceof ClearCommand);
        assertThrows(ParseException.class, String.format(MESSAGE_INVALID_COMMAND_FORMAT, ClearCommand.MESSAGE_USAGE), ()
                -> parser.parseCommand(ClearCommand.COMMAND_WORD + " 3"));
    }

    @Test
    public void parseCommand_delete() throws Exception {
        DeleteContactCommand command = (DeleteContactCommand) parser.parseCommand(
                DeleteContactCommand.COMMAND_WORD + " " + INDEX_FIRST_CONTACT.getOneBased());
        assertEquals(new DeleteContactCommand(INDEX_FIRST_CONTACT), command);
    }

    @Test
    public void parseCommand_deleteStory() throws Exception {
        DeleteStoryCommand command = (DeleteStoryCommand) parser.parseCommand(
                DeleteStoryCommand.COMMAND_WORD + " " + INDEX_FIRST_STORY.getOneBased());
        assertEquals(new DeleteStoryCommand(INDEX_FIRST_STORY), command);
    }

    @Test
    public void parseCommand_edit() throws Exception {
        Contact contact = new ContactBuilder().build();
        EditContactDescriptor descriptor = new EditContactDescriptorBuilder(contact).build();
        EditContactCommand command = (EditContactCommand) parser.parseCommand(EditContactCommand.COMMAND_WORD + " "
                + INDEX_FIRST_CONTACT.getOneBased() + " " + ContactUtil.getEditContactDescriptorDetails(descriptor));
        assertEquals(new EditContactCommand(INDEX_FIRST_CONTACT, descriptor), command);
    }

    @Test
    public void parseCommand_exit() throws Exception {
        assertTrue(parser.parseCommand(ExitCommand.COMMAND_WORD) instanceof ExitCommand);
        assertTrue(parser.parseCommand(ExitCommand.COMMAND_WORD + "   ") instanceof ExitCommand);
        assertThrows(ParseException.class, String.format(MESSAGE_INVALID_COMMAND_FORMAT, ExitCommand.MESSAGE_USAGE), ()
                -> parser.parseCommand(ExitCommand.COMMAND_WORD + " 3"));
    }

    @Test
    public void parseCommand_find() throws Exception {
        List<String> keywords = List.of("foo", "bar", "baz");
        Command command = parser.parseCommand(
                FindContactsCommand.COMMAND_WORD + " " + keywords.stream().collect(Collectors.joining(" ")));
        assertTrue(command instanceof FindContactsCommand);

        AddressBook addressBook = new AddressBook();
        Contact matchingContact = new ContactBuilder().withName("foo bar").build();
        Contact nonMatchingContact = new ContactBuilder().withName("other").build();
        addressBook.addContact(matchingContact);
        addressBook.addContact(nonMatchingContact);

        Model model = new ModelManager(addressBook, new UserPrefs());
        CommandResult result = command.execute(model);

        assertEquals(String.format(MESSAGE_CONTACTS_LISTED_OVERVIEW, 1), result.getFeedbackToUser());
        assertEquals(List.of(matchingContact), model.getFilteredContactList());
    }

    @Test
    public void parseCommand_findStory() throws Exception {
        List<String> keywords = List.of("housing", "investigation");
        Command command = parser.parseCommand(
                FindStoriesCommand.COMMAND_WORD + " " + String.join(" ", keywords));
        assertTrue(command instanceof FindStoriesCommand);

        AddressBook addressBook = new AddressBook();
        Story matchingStory = new Story(new StoryName("housing investigation"), Collections.emptySet());
        Story nonMatchingStory = new Story(new StoryName("health report"), Collections.emptySet());
        addressBook.addStory(matchingStory);
        addressBook.addStory(nonMatchingStory);

        Model model = new ModelManager(addressBook, new UserPrefs());
        CommandResult result = command.execute(model);

        assertEquals(String.format(MESSAGE_STORIES_LISTED_OVERVIEW, 1), result.getFeedbackToUser());
        assertEquals(List.of(matchingStory), model.getFilteredStoryList());
    }

    @Test
    public void parseCommand_help() throws Exception {
        assertTrue(parser.parseCommand(HelpCommand.COMMAND_WORD) instanceof HelpCommand);
        assertTrue(parser.parseCommand(HelpCommand.COMMAND_WORD + "   ") instanceof HelpCommand);
        assertThrows(ParseException.class, String.format(MESSAGE_INVALID_COMMAND_FORMAT, HelpCommand.MESSAGE_USAGE), ()
                -> parser.parseCommand(HelpCommand.COMMAND_WORD + " 3"));
    }

    @Test
    public void parseCommand_list() throws Exception {
        assertTrue(parser.parseCommand(ListCommand.COMMAND_WORD) instanceof ListCommand);
        assertTrue(parser.parseCommand(ListCommand.COMMAND_WORD + "   ") instanceof ListCommand);
        assertThrows(ParseException.class, String.format(MESSAGE_INVALID_COMMAND_FORMAT, ListCommand.MESSAGE_USAGE), ()
                -> parser.parseCommand(ListCommand.COMMAND_WORD + " 3"));
    }

    @Test
    public void parseCommand_listcontacts() throws Exception {
        assertTrue(parser.parseCommand(ListContactsCommand.COMMAND_WORD) instanceof ListContactsCommand);
        assertTrue(parser.parseCommand(ListContactsCommand.COMMAND_WORD + " si/2") instanceof ListContactsCommand);
    }

    @Test
    public void parseCommand_liststories() throws Exception {
        assertTrue(parser.parseCommand(ListStoriesCommand.COMMAND_WORD) instanceof ListStoriesCommand);
        assertTrue(parser.parseCommand(ListStoriesCommand.COMMAND_WORD + " ci/2") instanceof ListStoriesCommand);
    }

    @Test
    public void parseCommand_listfollowups() throws Exception {
        assertTrue(parser.parseCommand(ListFollowupsCommand.COMMAND_WORD) instanceof ListFollowupsCommand);
        assertTrue(parser.parseCommand(ListFollowupsCommand.COMMAND_WORD + " ci/2") instanceof ListFollowupsCommand);
    }

    @Test
    public void parseCommand_addStory() throws Exception {
        Story story = new Story(new StoryName("Housing Investigation"), Collections.emptySet());
        AddStoryCommand command = (AddStoryCommand) parser.parseCommand(
                AddStoryCommand.COMMAND_WORD + " Housing Investigation");
        assertEquals(new AddStoryCommand(story), command);
    }

    @Test
    public void parseCommand_addFollowup() throws Exception {
        AddFollowupCommand command = (AddFollowupCommand) parser.parseCommand(
                AddFollowupCommand.COMMAND_WORD + " Interview "
                + PREFIX_CONTACTINDEX + INDEX_FIRST_CONTACT.getOneBased());
        assertEquals(new AddFollowupCommand(new FollowupDescription("Interview"), INDEX_FIRST_CONTACT), command);
    }

    @Test
    public void parseCommand_deleteFollowup() throws Exception {
        DeleteFollowupCommand command = (DeleteFollowupCommand) parser.parseCommand(
                DeleteFollowupCommand.COMMAND_WORD + " 1");
        assertEquals(new DeleteFollowupCommand(Index.fromOneBased(1)), command);
    }

    @Test
    public void parseCommand_editStory() throws Exception {
        EditStoryDescriptor descriptor = new EditStoryDescriptor();
        descriptor.setName(new StoryName("Housing Investigation"));
        EditStoryCommand command = (EditStoryCommand) parser.parseCommand(
                EditStoryCommand.COMMAND_WORD + " " + INDEX_FIRST_STORY.getOneBased() + " "
                + PREFIX_NAME + "Housing Investigation");
        assertEquals(new EditStoryCommand(INDEX_FIRST_STORY, descriptor), command);
    }

    @Test
    public void parseCommand_editFollowup() throws Exception {
        EditFollowupDescriptor descriptor = new EditFollowupDescriptor();
        descriptor.setDescription(new FollowupDescription("Interview"));
        EditFollowupCommand command = (EditFollowupCommand) parser.parseCommand(
                EditFollowupCommand.COMMAND_WORD + " 1 " + PREFIX_DESCRIPTION + "Interview");
        assertEquals(new EditFollowupCommand(Index.fromOneBased(1), descriptor), command);
    }

    @Test
    public void parseCommand_findFollowups() throws Exception {
        List<String> keywords = List.of("interview", "urgent");
        Command command = parser.parseCommand(
                FindFollowupsCommand.COMMAND_WORD + " " + String.join(" ", keywords));
        assertTrue(command instanceof FindFollowupsCommand);

        AddressBook addressBook = new AddressBook();
        Followup matchingFollowup = new Followup(
                new FollowupDescription("interview"), new Name("Alice"));
        Followup nonMatchingFollowup = new Followup(
                new FollowupDescription("unrelated"), new Name("Alice"));
        addressBook.addFollowup(matchingFollowup);
        addressBook.addFollowup(nonMatchingFollowup);

        Model model = new ModelManager(addressBook, new UserPrefs());
        CommandResult result = command.execute(model);

        assertEquals(String.format(MESSAGE_FOLLOWUPS_LISTED_OVERVIEW, 1), result.getFeedbackToUser());
        assertEquals(List.of(matchingFollowup), model.getFilteredFollowupList());
    }

    @Test
    public void parseCommand_unassign() throws Exception {
        Index contactIndex = Index.fromOneBased(1);
        Index storyIndex = Index.fromOneBased(2);
        UnassignCommand command = (UnassignCommand) parser.parseCommand(UnassignCommand.COMMAND_WORD + " "
                + PREFIX_CONTACTINDEX + contactIndex.getOneBased() + " "
                + PREFIX_STORYINDEX + storyIndex.getOneBased());

        assertEquals(new UnassignCommand(storyIndex, contactIndex), command);
    }

    @Test
    public void parseCommand_shortcut() throws Exception {
        assertEquals(new ShortcutCommand("ls", "list"),
                parser.parseCommand(ShortcutCommand.COMMAND_WORD + " s/ls c/list"));
        assertEquals(new ShortcutCommand(), parser.parseCommand(ShortcutCommand.COMMAND_WORD));
    }

    @Test
    public void parseCommand_unshortcut() throws Exception {
        assertEquals(new UnshortcutCommand("ls"),
                parser.parseCommand(UnshortcutCommand.COMMAND_WORD + " ls"));
    }

    @Test
    public void parseCommand_shortcut_resolvesCommand() throws Exception {
        AddressBookParser parserWithShortcuts = new AddressBookParser(() -> Map.of("ls", "list"));

        assertTrue(parserWithShortcuts.parseCommand("ls") instanceof ListCommand);
    }

    @Test
    public void parseCommand_shortcutToZeroArgCommandWithArguments_throwsParseException() {
        AddressBookParser parserWithShortcuts = new AddressBookParser(() -> Map.of("c", "clear"));
        assertThrows(ParseException.class, String.format(MESSAGE_INVALID_COMMAND_FORMAT, ClearCommand.MESSAGE_USAGE), ()
                -> parserWithShortcuts.parseCommand("c 123"));
    }

    @Test
    public void parseCommand_shortcutWithArguments_preservesArguments() throws Exception {
        AddressBookParser parserWithShortcuts = new AddressBookParser(() -> Map.of("d", "deletecontact"));

        DeleteContactCommand command = (DeleteContactCommand) parserWithShortcuts.parseCommand(
                "d " + INDEX_FIRST_CONTACT.getOneBased());

        assertEquals(new DeleteContactCommand(INDEX_FIRST_CONTACT), command);
    }

    @Test
    public void parseCommand_shortcutsChangedAfterConstruction_usesLatestShortcuts() throws Exception {
        Map<String, String> shortcuts = new HashMap<>();
        AddressBookParser parserWithShortcuts = new AddressBookParser(() -> shortcuts);
        shortcuts.put("stories", "liststories");

        assertTrue(parserWithShortcuts.parseCommand("stories") instanceof ListStoriesCommand);
    }

    @Test
    public void parseCommand_builtinCommandAlsoStoredAsShortcut_builtinCommandTakesPriority() throws Exception {
        AddressBookParser parserWithShortcuts = new AddressBookParser(() -> Map.of("list", "findcontacts"));

        assertTrue(parserWithShortcuts.parseCommand("list") instanceof ListCommand);
    }

    @Test
    public void parseCommand_unrecognisedInput_throwsParseException() {
        assertThrows(ParseException.class, String.format(MESSAGE_INVALID_COMMAND_FORMAT, HelpCommand.MESSAGE_USAGE), ()
            -> parser.parseCommand(""));
    }

    @Test
    public void parseCommand_unknownCommand_throwsParseException() {
        assertThrows(ParseException.class, MESSAGE_UNKNOWN_COMMAND, () -> parser.parseCommand("unknownCommand"));
    }
}
