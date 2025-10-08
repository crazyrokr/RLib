package javasabr.rlib.network;

public interface UnsafeConnection<C extends UnsafeConnection<C>> extends Connection<C> {

  void onConnected();
}
