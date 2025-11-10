package javasabr.rlib.network.exception;

public class NetworkException extends RuntimeException {

  protected NetworkException(String message) {
    super(message);
  }

  protected NetworkException(String message, Throwable cause) {
    super(message, cause);
  }

  protected NetworkException(Throwable cause) {
    super(cause);
  }
}
