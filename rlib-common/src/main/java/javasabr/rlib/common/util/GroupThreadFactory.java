package javasabr.rlib.common.util;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;

/**
 * @author JavaSaBr
 */
@FieldDefaults(level = AccessLevel.PROTECTED, makeFinal = true)
public class GroupThreadFactory implements ThreadFactory {

  public interface ThreadConstructor {

    Thread create(ThreadGroup group, Runnable runnable, String name);
  }

  AtomicInteger ordinal;
  String name;
  ThreadGroup group;
  ThreadConstructor constructor;

  int priority;
  boolean daemon;

  public GroupThreadFactory(String name) {
    this(name, Thread::new, Thread.NORM_PRIORITY);
  }

  public GroupThreadFactory(String name, int priority) {
    this(name, Thread::new, priority);
  }

  public GroupThreadFactory(String name, ThreadConstructor constructor, int priority) {
    this(name, constructor, priority, false);
  }

  public GroupThreadFactory(String name, ThreadConstructor constructor, int priority, boolean daemon) {
    this.constructor = constructor;
    this.priority = priority;
    this.name = name;
    this.group = new ThreadGroup(name);
    this.ordinal = new AtomicInteger();
    this.daemon = daemon;
  }

  @Override
  public Thread newThread(Runnable runnable) {
    var thread = constructor.create(group, runnable, name + "-" + ordinal.incrementAndGet());
    thread.setPriority(priority);
    thread.setDaemon(daemon);
    return thread;
  }
}
