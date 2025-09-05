package javasabr.rlib.reference;

import javasabr.rlib.reusable.BoundReusable;

/**
 * @author JavaSaBr
 */
final class TLCharReference extends CharReference implements BoundReusable {

  public TLCharReference() {
    super('\0');
  }

  public TLCharReference(char value) {
    super(value);
  }

  @Override
  public void release() {
    ReferenceFactory.release(this);
  }
}
