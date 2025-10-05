package javasabr.rlib.network.util;

import javax.net.ssl.SSLEngineResult.HandshakeStatus;
import lombok.experimental.UtilityClass;

@UtilityClass
public class SslUtils {

  public static boolean isReadyToDecrypt(HandshakeStatus status) {
    return status == HandshakeStatus.FINISHED || status == HandshakeStatus.NOT_HANDSHAKING;
  }

  public static boolean needToProcess(HandshakeStatus status) {
    return status != HandshakeStatus.FINISHED && status != HandshakeStatus.NOT_HANDSHAKING;
  }
}
