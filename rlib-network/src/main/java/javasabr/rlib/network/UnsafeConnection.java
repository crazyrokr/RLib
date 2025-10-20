package javasabr.rlib.network;

import java.nio.channels.AsynchronousSocketChannel;

public interface UnsafeConnection<C extends UnsafeConnection<C>> extends Connection<C> {

  Network<?> network();

  BufferAllocator bufferAllocator();

  AsynchronousSocketChannel channel();

  void onConnected();
}
