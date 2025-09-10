package javasabr.rlib.collections.deque.impl;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import lombok.experimental.FieldDefaults;
import org.jspecify.annotations.Nullable;

@Getter
@Setter
@AllArgsConstructor
@Accessors(fluent = true, chain = false)
@FieldDefaults(level = AccessLevel.PROTECTED)
public class LinkedNode<E> {

  @Nullable
  E item;
  @Nullable
  LinkedNode<E> prev;
  @Nullable
  LinkedNode<E> next;
}
