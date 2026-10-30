package com.example.liferpg.core.time

import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId

/**
 * Single source of "now" for the whole app.
 *
 * Every time-dependent decision goes through this interface so tests can pin the clock and the
 * zone without touching system state.
 */
interface TimeProvider {
  val zone: ZoneId

  fun now(): Instant

  fun nowMillis(): Long = now().toEpochMilli()

  fun today(): LocalDate = now().atZone(zone).toLocalDate()
}

class SystemTimeProvider(override val zone: ZoneId = ZoneId.systemDefault()) : TimeProvider {
  override fun now(): Instant = Instant.now()
}
