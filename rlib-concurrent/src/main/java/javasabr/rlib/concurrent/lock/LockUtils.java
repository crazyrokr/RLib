package javasabr.rlib.concurrent.lock;

import java.util.concurrent.locks.Lock;
import lombok.experimental.UtilityClass;

/**
 * @author JavaSaBr
 */
@UtilityClass
public class LockUtils {

  public static void lock(Lockable first, Lock second) {
    first.lock();
    second.lock();
  }

  public static void lock(Lockable first, Lockable second) {
    first.lock();
    second.lock();
  }

  public static <T extends Comparable<T> & Lockable> void lock(T first, T second) {
    int result = first.compareTo(second);
    if (result == 0 || result < 0) {
      first.lock();
      second.lock();
    } else {
      second.lock();
      first.lock();
    }
  }

  public static void unlock(Lock first, Lock second) {
    first.unlock();
    second.unlock();
  }


  public static void unlock(Lockable first, Lockable second) {
    first.unlock();
    second.unlock();
  }

  public static <T extends Comparable<T> & Lockable> void unlock(T first, T second) {
    int result = first.compareTo(second);
    if (result == 0 || result < 0) {
      second.unlock();
      first.unlock();
    } else {
      first.unlock();
      second.unlock();
    }
  }
}
