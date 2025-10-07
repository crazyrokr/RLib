package javasabr.rlib.network.impl;

import java.nio.channels.AsynchronousSocketChannel;
import java.util.function.BiFunction;
import javasabr.rlib.network.Connection;
import javasabr.rlib.network.Network;
import javasabr.rlib.network.NetworkConfig;
import lombok.AccessLevel;
import lombok.CustomLog;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

/**
 * The base implementation of {@link Network}.
 *
 * @author JavaSaBr
 */
@CustomLog
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PROTECTED, makeFinal = true)
public abstract class AbstractNetwork<C extends Connection<?, ?, C>> implements Network<C> {
  NetworkConfig config;
  BiFunction<Network<C>, AsynchronousSocketChannel, C> channelToConnection;
}
