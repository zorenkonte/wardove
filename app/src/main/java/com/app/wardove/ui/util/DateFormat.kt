package com.app.wardove.ui.util

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

fun Long.formatDateOnly(): String =
    SimpleDateFormat("MMMM d, yyyy", Locale.getDefault()).format(Date(this))

fun Long.formatDateShort(): String =
    SimpleDateFormat("MMM d", Locale.getDefault()).format(Date(this))
