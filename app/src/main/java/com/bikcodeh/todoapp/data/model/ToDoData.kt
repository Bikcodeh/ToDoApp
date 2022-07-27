package com.bikcodeh.todoapp.data.model

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize
import java.util.*

@Entity
@Parcelize
data class ToDoData(
    @PrimaryKey(autoGenerate = true)
    var id: Int = 0,
    var title: String,
    var priority: Priority,
    var description: String,
    var date: Long = System.currentTimeMillis(),
    var isSelected: Boolean = false,
    var displaySelector: Boolean = false
): Parcelable
