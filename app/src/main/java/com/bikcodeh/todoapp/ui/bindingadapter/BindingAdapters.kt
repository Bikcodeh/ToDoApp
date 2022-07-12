package com.bikcodeh.todoapp.ui.bindingadapter

import android.view.View
import androidx.core.view.isVisible
import androidx.databinding.BindingAdapter
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.navigation.findNavController
import com.bikcodeh.todoapp.R
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
    }
}