package javasabr.rlib.reference;

import javasabr.rlib.reusable.BoundReusable;

/**
 * @author JavaSaBr
 */
final class TLByteReference extends ByteReference implements BoundReusable {

  public TLByteReference() {
    super((byte) 0);
  }

  public TLByteReference(byte value) {
    super(value);
  }

  @Override
  public void release() {
    ReferenceFactory.release(this);
  }
}
