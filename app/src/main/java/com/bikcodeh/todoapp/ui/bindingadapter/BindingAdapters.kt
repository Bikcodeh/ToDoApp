package com.bikcodeh.todoapp.ui.bindingadapter

import android.view.View
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.core.view.isVisible
import androidx.databinding.BindingAdapter
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.navigation.findNavController
import com.bikcodeh.todoapp.R
import com.bikcodeh.todoapp.data.model.Priority
import com.bikcodeh.todoapp.ui.util.Util
import com.google.android.material.card.MaterialCardView
import com.google.android.material.floatingactionbutton.FloatingActionButton

class BindingAdapters {

    companion object {

        @BindingAdapter("android:navigateToAddFragment")
        @JvmStatic
        fun navigateToAddFragment(view: FloatingActionButton, navigate: Boolean) {
            view.setOnClickListener {
                if (navigate) {
                    view.findNavController().navigate(R.id.action_notesFragment_to_addFragment)
                }
            }
        }

        @BindingAdapter("android:emptyData")
        @JvmStatic
        fun emptyData(view: View, isEmpty: LiveData<Boolean>) {
            view.isVisible = isEmpty.value ?: false
        }

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
    }
}