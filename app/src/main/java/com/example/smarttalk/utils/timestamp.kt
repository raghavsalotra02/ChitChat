package com.example.smarttalk.utils

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale



fun formatTimestamp(timestampMillis: Long): String {
    val date = Date(timestampMillis)
    val sdf = SimpleDateFormat("hh:mm a", Locale.getDefault())  // "hh" for 12-hour, "a" for AM/PM
    return sdf.format(date)
}