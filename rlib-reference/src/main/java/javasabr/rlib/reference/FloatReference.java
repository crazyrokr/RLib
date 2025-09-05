package javasabr.rlib.reference;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @author JavaSaBr
 */
@Data
@AllArgsConstructor
@Accessors(fluent = true, chain = false)
public class FloatReference extends AbstractReference {
  private float value;
}
