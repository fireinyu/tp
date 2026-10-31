package seedu.address.model;

/**
 * A predicate that checks whether an item of type {@code T} is linked to an entity of type {@code U}.
 */
public abstract class BaseLinkedPredicate<T, U extends Titled> extends StateAwarePredicate<T, U> {

    private static final String LABEL = "Filtered by %s: \"%s\"";

    private U key;

    public BaseLinkedPredicate(U key) {
        this.key = key;
    }

    @Override
    public String toString() {
        return String.format(LABEL, key.getTypeTitle(), key.getInstanceTitle());
    }

    @Override
    public boolean test(T item) {
        return isLinked(item, key);
    }

    @Override
    public BaseLinkedPredicate<T, U> notifyStateChanged(U oldState, U newState) {
        if (key.equals(oldState)) {
            key = newState;
        }
        return this;
    }

    public abstract boolean isLinked(T item, U other);
}
