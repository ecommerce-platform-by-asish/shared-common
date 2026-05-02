package com.app.common.context;

import java.util.concurrent.Callable;
import org.jspecify.annotations.NullMarked;

/** Modern context propagation using ScopedValue. */
@NullMarked
public final class UserContext {
  public static final ScopedValue<String> TRACE_ID = ScopedValue.newInstance();
  public static final ScopedValue<String> USER_ID = ScopedValue.newInstance();

  private UserContext() {}

  public static <T> T withContext(String traceId, String userId, Callable<T> op) throws Exception {
    return ScopedValue.where(TRACE_ID, traceId).where(USER_ID, userId).call(op::call);
  }

  public static void runWithContext(String traceId, String userId, Runnable op) {
    ScopedValue.where(TRACE_ID, traceId).where(USER_ID, userId).run(op);
  }
}
