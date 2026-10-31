package seedu.address.model;

import java.util.List;

/**
 * Base keyword-matching predicate for items of type {@code T}.
 */
public abstract class BaseKeywordPredicate<T, U> extends StateAwarePredicate<T, U> {
    private static final String LABEL = "Filtered: \"%s\"";

    private final List<String> keywords;

    public BaseKeywordPredicate(List<String> keywords) {
        this.keywords = List.copyOf(keywords);
    }

    @Override
    public boolean test(T item) {
        return keywords.stream()
                .anyMatch(kw -> matchesKeyword(item, kw));
    }

    public abstract boolean matchesKeyword(T item, String keyword);

    @Override
    public String toString() {
        return String.format(LABEL, String.join(" ", keywords));
    }
}
