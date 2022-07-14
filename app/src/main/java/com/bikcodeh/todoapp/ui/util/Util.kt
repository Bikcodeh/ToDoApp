package com.bikcodeh.todoapp.ui.util

import java.text.SimpleDateFormat
import java.util.*

object Util {

    fun formatDate(dateInMilliSeconds: Long): String {
        val simpleDateFormat = SimpleDateFormat("MM/dd/yyyy")
        val dateFormat = Date(dateInMilliSeconds)
        return simpleDateFormat.format(dateFormat)
    }
}