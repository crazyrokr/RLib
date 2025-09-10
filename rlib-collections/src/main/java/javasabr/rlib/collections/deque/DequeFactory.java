package javasabr.rlib.collections.deque;

import java.util.Deque;
import javasabr.rlib.collections.deque.impl.DefaultLinkedListBasedDeque;
import lombok.experimental.UtilityClass;

/**
 * @author JavaSaBr
 */
@UtilityClass
public class DequeFactory {
  public static <E> Deque<E> linkedListBased() {
    return new DefaultLinkedListBasedDeque<>();
  }
}
