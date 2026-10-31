package seedu.address.model;

import java.util.function.Predicate;

/**
 * Represents a predicate that is aware of and responds to state changes of an associated entity.
 *
 * @param <T> Type of the item to be tested.
 * @param <U> Type of the state object tracked by this predicate.
 */
public abstract class StateAwarePredicate<T, U> implements Predicate<T> {

    public StateAwarePredicate<T, U> notifyStateChanged(U oldstate, U newState) {
        return this;
    }

    public StateAwarePredicate<T, U> notifyStateDeleted(U oldstate) {
        return this;
    }
}
