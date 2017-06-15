package com.ss.rlib.database.impl;

import com.jolbox.bonecp.BoneCP;
import com.jolbox.bonecp.BoneCPConfig;
import com.ss.rlib.database.ConnectFactory;
import com.ss.rlib.logging.Logger;
import com.ss.rlib.logging.LoggerManager;
import org.jetbrains.annotations.NotNull;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * The implementation of connect factory base on {@link BoneCP}.
 *
 * @author JavaSaBr
 */
public final class BoneCPConnectFactory implements ConnectFactory {

    private static final Logger LOGGER = LoggerManager.getLogger(BoneCPConnectFactory.class);

    /**
     * The source.
     */
    private BoneCP source;

    /**
     * Close the source.
     */
    public synchronized void close() {
        source.close();
        source.shutdown();
    }

    @Override
    public Connection getConnection() throws SQLException {
        return source.getConnection();
    }

    /**
     * Init a connection pool.
     *
     * @param config the config.
     * @param driver the driver.
     * @throws SQLException the sql exception
     */
    public synchronized void init(@NotNull final BoneCPConfig config, @NotNull final String driver) throws SQLException {
        try {
            Class.forName(driver).newInstance();
            source = new BoneCP(config);
            source.getConnection().close();
        } catch (final Exception e) {
            LOGGER.warning(new SQLException("could not init DB connection:" + e));
        }
    }
}