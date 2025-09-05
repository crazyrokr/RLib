package javasabr.rlib.reference;

import javasabr.rlib.reusable.BoundReusable;

/**
 * @author JavaSaBr
 */
final class TLLongReference extends LongReference implements BoundReusable {

  public TLLongReference() {
    super(0);
  }

  public TLLongReference(long value) {
    super(value);
  }

  @Override
  public void release() {
    ReferenceFactory.release(this);
  }
}
