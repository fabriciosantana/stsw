package com.example.demo;

import java.time.Instant;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Rate limit didático:
 * - Permite até 5 tentativas falhas em janela de 5 minutos.
 * - Se estourar, "trava" por 5 minutos.
 */
public class RateLimiter {

  private static final int MAX_FAILS = 5;
  private static final long WINDOW_SECONDS = 5 * 60;
  private static final long LOCK_SECONDS = 5 * 60;

  private static final Map<String, Deque<Long>> failsByClient = new ConcurrentHashMap<>();
  private static final Map<String, Long> lockUntilByClient = new ConcurrentHashMap<>();

  public static void registerFailedAttempt(String clientKey) {
    long now = Instant.now().getEpochSecond();
    var q = failsByClient.computeIfAbsent(clientKey, k -> new ArrayDeque<>());
    q.addLast(now);
    // limpa falhas antigas
    while (!q.isEmpty() && now - q.peekFirst() > WINDOW_SECONDS) q.removeFirst();
    if (q.size() > MAX_FAILS) {
      lockUntilByClient.put(clientKey, now + LOCK_SECONDS);
    }
  }

  public static boolean isLocked(String clientKey) {
    long now = Instant.now().getEpochSecond();
    Long until = lockUntilByClient.get(clientKey);
    if (until == null) return false;
    if (now > until) {
      lockUntilByClient.remove(clientKey);
      return false;
    }
    return true;
  }

  public static void reset(String clientKey) {
    failsByClient.remove(clientKey);
    lockUntilByClient.remove(clientKey);
  }
}
