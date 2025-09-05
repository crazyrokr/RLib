package javasabr.rlib.reference;

import javasabr.rlib.reusable.BoundReusable;
import org.jspecify.annotations.Nullable;

/**
 * @author JavaSaBr
 */
final class TLObjectReference<T> extends ObjectReference<T> implements BoundReusable {

  public TLObjectReference() {
    super(null);
  }

  public TLObjectReference(@Nullable T value) {
    super(value);
  }

  @Override
  public void release() {
    ReferenceFactory.release(this);
  }
}
