package javasabr.rlib.reference;

import javasabr.rlib.reusable.BoundReusable;

/**
 * @author JavaSaBr
 */
final class TLDoubleReference extends DoubleReference implements BoundReusable {

  public TLDoubleReference() {
    super(0d);
  }

  public TLDoubleReference(double value) {
    super(value);
  }

  @Override
  public void release() {
    ReferenceFactory.release(this);
  }
}
