package javasabr.rlib.common.util;

import java.util.Objects;
import java.util.concurrent.CompletionException;
import lombok.experimental.UtilityClass;
import org.jspecify.annotations.Nullable;

/**
 * @author JavaSaBr
 */
@UtilityClass
public class AsyncUtils {

  @Nullable
  public static <T> T skip(Throwable throwable) {
    return null;
  }

  public static <T> T continueCompletableStage(@Nullable T result, @Nullable Throwable throwable) {
    if (throwable instanceof RuntimeException) {
      throw (RuntimeException) throwable;
    } else if (throwable != null) {
      throw new CompletionException(throwable);
    } else {
      return Objects.requireNonNull(result);
    }
  }
}
