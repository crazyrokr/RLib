package javasabr.rlib.network.exception;

public class UserDefinedNetworkException extends NetworkException {
  protected UserDefinedNetworkException(String message) {
    super(message);
  }

  protected UserDefinedNetworkException(String message, Throwable cause) {
    super(message, cause);
  }

  protected UserDefinedNetworkException(Throwable cause) {
    super(cause);
  }
}
