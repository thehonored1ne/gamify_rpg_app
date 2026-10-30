package com.example.liferpg.ui.format

import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import java.util.Locale

private val dateTimeFormatter: DateTimeFormatter =
  DateTimeFormatter.ofPattern("MMM d, HH:mm", Locale.getDefault())

private val dateFormatter: DateTimeFormatter =
  DateTimeFormatter.ofPattern("MMM d, yyyy", Locale.getDefault())

fun formatDateTime(epochMillis: Long, zone: ZoneId = ZoneId.systemDefault()): String =
  Instant.ofEpochMilli(epochMillis).atZone(zone).format(dateTimeFormatter)

fun formatDate(epochMillis: Long, zone: ZoneId = ZoneId.systemDefault()): String =
  Instant.ofEpochMilli(epochMillis).atZone(zone).format(dateFormatter)

/** Converts the UTC-midnight value from the Material date picker to local end of day. */
fun datePickerMillisToLocalEndOfDay(
  utcMillis: Long,
  zone: ZoneId = ZoneId.systemDefault(),
): Long {
  val date: LocalDate = Instant.ofEpochMilli(utcMillis).atZone(ZoneOffset.UTC).toLocalDate()
  return date.atTime(23, 59).atZone(zone).toInstant().toEpochMilli()
}
