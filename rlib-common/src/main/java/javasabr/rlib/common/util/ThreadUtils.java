package javasabr.rlib.common.util;

import javasabr.rlib.logger.api.Logger;
import javasabr.rlib.logger.api.LoggerManager;

/**
 * @author JavaSaBr
 */
public class ThreadUtils {

  private static final Logger LOGGER = LoggerManager.getLogger(ThreadUtils.class);

  public static void sleep(long time) {
    try {
      Thread.sleep(time);
    } catch (InterruptedException e) {
      LOGGER.warning(e);
    }
  }
}
