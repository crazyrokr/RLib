package javasabr.rlib.reference;

import javasabr.rlib.reusable.BoundReusable;

/**
 * @author JavaSaBr
 */
final class TLFloatReference extends FloatReference implements BoundReusable {

  public TLFloatReference() {
    super(0f);
  }

  public TLFloatReference(float value) {
    super(value);
  }

  @Override
  public void release() {
    ReferenceFactory.release(this);
  }
}
