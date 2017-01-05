package rlib.util.array;

import org.jetbrains.annotations.Nullable;

import java.util.Comparator;

/**
 * The interface for implementing a comparator for Array.
 *
 * @author JavaSaBr
 */
public interface ArrayComparator<T> extends Comparator<T> {

    @Override
    default int compare(@Nullable final T first, @Nullable final T second) {

        if (first == null) {
            return 1;
        } else if (second == null) {
            return -1;
        }

        return compareImpl(first, second);
    }

    /**
     * Compare the two objects.
     *
     * @return a negative integer, zero, or a positive integer as the first argument is less than, equal to, or greater
     * than the second.
     */
    int compareImpl(@Nullable T first, @Nullable T second);
}
