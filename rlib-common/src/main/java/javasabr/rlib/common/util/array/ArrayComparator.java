package javasabr.rlib.common.util.array;

import java.util.Comparator;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

/**
 * The interface to implement a comparator for {@link Array}.
 *
 * @param <T> the type parameter
 * @author JavaSaBr
 */
@NullMarked
public interface ArrayComparator<T> extends Comparator<T> {

  @Override
  default int compare(@Nullable T left, @Nullable T right) {

    if (left == null) {
      return 1;
    } else if (right == null) {
      return -1;
    }

    return compareImpl(left, right);
  }

  /**
   * Compare the two objects.
   *
   * @param first the first.
   * @param second the second.
   * @return a negative integer, zero, or a positive integer as the first argument is less than, equal to, or greater
   * than the second.
   */
  int compareImpl(T first, T second);
}
