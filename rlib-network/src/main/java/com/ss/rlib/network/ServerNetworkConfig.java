package com.ss.rlib.network;

import com.ss.rlib.common.concurrent.GroupThreadFactory;
import lombok.Builder;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;

import java.nio.ByteOrder;

/**
 * The interface to implement a server network config.
 *
 * @author JavaSaBr
 */
public interface ServerNetworkConfig extends NetworkConfig {

    @Builder
    @Getter
    class SimpleServerNetworkConfig implements ServerNetworkConfig {

        @Builder.Default
        private String groupName = "ServerNetworkThread";
        @Builder.Default
        private ByteOrder byteOrder = ByteOrder.BIG_ENDIAN;
        @Builder.Default
        private GroupThreadFactory.ThreadConstructor threadConstructor = Thread::new;

        @Builder.Default
        private int readBufferSize = 2048;
        @Builder.Default
        private int pendingBufferSize = 4096;
        @Builder.Default
        private int writeBufferSize = 2048;
        @Builder.Default
        private int groupSize = 1;
        @Builder.Default
        private int threadPriority = Thread.NORM_PRIORITY;
    }

    @NotNull ServerNetworkConfig DEFAULT_SERVER = new ServerNetworkConfig() {

        @Override
        public int getGroupSize() {
            return 2;
        }

        @Override
        public @NotNull String getGroupName() {
            return "ServerNetworkThread";
        }
    };

    /**
     * Get a minimal size of network thread executor.
     *
     * @return the minimal executor size.
     */
    default int getGroupSize() {
        return 1;
    }

    /**
     * Get a maximum size of network thread executor.
     *
     * @return the maximum executor size.
     */
    default int getGroupMaxSize() {
        return getGroupSize();
    }

    /**
     * Get a thread constructor which should be used to create network threads.
     *
     * @return the thread constructor.
     */
    default @NotNull GroupThreadFactory.ThreadConstructor getThreadConstructor() {
        return Thread::new;
    }

    /**
     * Get a priority of network threads.
     *
     * @return the priority of network threads.
     */
    default int getThreadPriority() {
        return Thread.NORM_PRIORITY;
    }
}
