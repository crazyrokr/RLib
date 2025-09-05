package javasabr.rlib.reference;

import javasabr.rlib.reusable.BoundReusable;

/**
 * @author JavaSaBr
 */
final class TLShortReference extends ShortReference implements BoundReusable {

  public TLShortReference() {
    super((short) 0);
  }

  public TLShortReference(short value) {
    super(value);
  }

  @Override
  public void release() {
    ReferenceFactory.release(this);
  }
}
