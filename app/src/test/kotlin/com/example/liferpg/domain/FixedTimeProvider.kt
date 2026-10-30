package com.example.liferpg.domain

import com.example.liferpg.core.time.TimeProvider
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.ZoneOffset

/** Test clock pinned to a fixed instant. */
class FixedTimeProvider(
  private val instant: Instant,
  override val zone: ZoneId = ZoneOffset.UTC,
) : TimeProvider {
  override fun now(): Instant = instant

  companion object {
    fun at(date: LocalDate, zone: ZoneId = ZoneOffset.UTC): FixedTimeProvider =
      FixedTimeProvider(date.atTime(12, 0).atZone(zone).toInstant(), zone)

    fun atDateTime(value: LocalDateTime, zone: ZoneId = ZoneOffset.UTC): FixedTimeProvider =
      FixedTimeProvider(value.atZone(zone).toInstant(), zone)
  }
}
