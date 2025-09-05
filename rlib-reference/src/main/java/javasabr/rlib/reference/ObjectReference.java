package javasabr.rlib.reference;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.experimental.Accessors;
import org.jspecify.annotations.Nullable;

/**
 * @author JavaSaBr
 */

@Data
@AllArgsConstructor
@Accessors(fluent = true, chain = false)
public class ObjectReference<T> extends AbstractReference {

  @Nullable
  private T value;
}
