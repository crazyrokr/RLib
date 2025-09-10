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
public class ShortReference extends AbstractReference {
  private short value;
}
