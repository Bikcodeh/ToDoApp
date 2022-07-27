package com.bikcodeh.todoapp.ui.bindingadapter

import android.widget.Spinner
import android.widget.TextView
import androidx.databinding.BindingAdapter
import com.bikcodeh.todoapp.R
import com.bikcodeh.todoapp.data.model.Priority
import com.bikcodeh.todoapp.ui.util.Util
import com.google.android.material.card.MaterialCardView

class BindingAdapters {

    companion object {

        @BindingAdapter("android:parsePriorityColor")
        @JvmStatic
        fun parsePriorityColor(cardView: MaterialCardView, priority: Priority) {
            val color = when (priority) {
                Priority.HIGH -> R.color.red
                Priority.MEDIUM -> R.color.yellow
                Priority.LOW -> R.color.green
            }
            cardView.setCardBackgroundColor(cardView.context.getColor(color))
        }

        @BindingAdapter("android:parseAndSetDate")
        @JvmStatic
        fun parseAndSetDate(view: TextView, dateInMilliSeconds: Long) {
            view.text = Util.formatDate(dateInMilliSeconds)
        }

        @BindingAdapter("android:parsePriorityToInt")
        @JvmStatic
        fun parsePriorityToInt(spinner: Spinner, priority: Priority) {
            when (priority) {
                Priority.HIGH -> spinner.setSelection(0)
                Priority.MEDIUM -> spinner.setSelection(1)
                Priority.LOW -> spinner.setSelection(2)
            }
        }
    }
}