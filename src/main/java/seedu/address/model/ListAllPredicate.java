package seedu.address.model;

/**
 * A predicate that always evaluates to true, indicating all items should be listed.
 */
public class ListAllPredicate<T, U> extends StateAwarePredicate<T, U> {
    @Override
    public boolean test(Object o) {
        return true;
    }

    @Override
    public String toString() {
        return "All";
    }
}
