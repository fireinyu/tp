package seedu.address.model;

/**
 * A data object that semantically represents an underlying item
 */
public interface SemanticRepresenter {
    boolean representsSameItem(SemanticRepresenter other);
}
