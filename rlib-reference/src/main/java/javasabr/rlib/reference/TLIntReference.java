package javasabr.rlib.reference;

import javasabr.rlib.reusable.BoundReusable;

/**
 * @author JavaSaBr
 */
final class TLIntReference extends IntReference implements BoundReusable {

  public TLIntReference() {
    super(0);
  }

  public TLIntReference(int value) {
    super(value);
  }

  @Override
  public void release() {
    ReferenceFactory.release(this);
  }
}
