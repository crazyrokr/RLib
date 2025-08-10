package javasabr.rlib.plugin.system;

import java.nio.file.Paths;
import java.util.concurrent.ForkJoinPool;
import javasabr.rlib.logger.api.LoggerLevel;
import javasabr.rlib.logger.api.LoggerManager;
import javasabr.rlib.plugin.system.impl.PluginSystemFactory;
import org.junit.jupiter.api.Test;

/**
 * @author JavaSaBr
 */
public class PluginSystemTests {

  static {
    LoggerManager.enable(PluginSystem.class, LoggerLevel.DEBUG);
  }

  @Test
  public void test() {

    var pluginSystem = PluginSystemFactory.newBasePluginSystem();
    pluginSystem.configureAppVersion(new Version("0.0.1"));
    pluginSystem.configureEmbeddedPluginPath(Paths.get("../gradle/"));

    pluginSystem
        .preLoad(ForkJoinPool.commonPool())
        .thenApply(system -> system.initialize(ForkJoinPool.commonPool()))
        .toCompletableFuture()
        .join();
  }
}
