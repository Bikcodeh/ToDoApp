package com.bikcodeh.todoapp.ui.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bikcodeh.todoapp.R
import com.bikcodeh.todoapp.data.model.ToDoData
import com.bikcodeh.todoapp.databinding.ItemTodoBinding
import com.bikcodeh.todoapp.ui.util.initAnimation
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class ToDoAdapter(private val onToDoClick: (ToDoData) -> Unit) :
    ListAdapter<ToDoData, ToDoAdapter.ToDoViewHolder>(ToDoDiffUtil()) {

    private val _isSelectedSomeItem: MutableStateFlow<Boolean> = MutableStateFlow(false)

    val isSelectedSomeItem: StateFlow<Boolean>
        get() = _isSelectedSomeItem.asStateFlow()

    inner class ToDoViewHolder(private val binding: ItemTodoBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(toDoData: ToDoData) {
            with(binding) {
                this.toDoData = toDoData
                this.executePendingBindings()
                todoCheckboxItem.visibility =
                    if (toDoData.displaySelector) {
                        binding.todoCheckboxItem.initAnimation(R.anim.fade_in)
                        View.VISIBLE
                    } else {
                        binding.todoCheckboxItem.initAnimation(R.anim.fade_out)
                        View.GONE
                    }

                todoCheckboxItem.isChecked = toDoData.isSelected
                this.root.setOnClickListener {
                    if (toDoData.isSelected || toDoData.displaySelector) {
                        toDoData.isSelected = !toDoData.isSelected
                        todoCheckboxItem.isChecked = !todoCheckboxItem.isChecked
                        notifyItemChanged(adapterPosition)
                        isSomeItemSelected()
                    } else {
                        onToDoClick(toDoData)
                    }
                }
                this.todoItemContainer.setOnLongClickListener {
                    toDoData.isSelected = !toDoData.isSelected
                    displaySelectors(true)
                    todoCheckboxItem.isChecked = toDoData.isSelected
                    notifyItemChanged(adapterPosition)
                    isSomeItemSelected()
                    true
                }
                todoCheckboxItem.setOnClickListener {
                    toDoData.isSelected = !toDoData.isSelected
                    todoCheckboxItem.isChecked = toDoData.isSelected
                    notifyItemChanged(adapterPosition)
                    isSomeItemSelected()
                }
            }
        }
    }

    private fun isSomeItemSelected() {
        _isSelectedSomeItem.value = currentList.any { it.isSelected || it.displaySelector }
    }

    fun displaySelectors(display: Boolean, unSelect: Boolean? = null) {
        currentList.forEachIndexed { index, toDoData ->
            toDoData.displaySelector = display
            unSelect?.let {
                toDoData.isSelected = unSelect
            }
        }
        notifyDataSetChanged()
        isSomeItemSelected()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ToDoViewHolder {
        return ToDoViewHolder(
            ItemTodoBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        )
    }

    override fun onBindViewHolder(holder: ToDoViewHolder, position: Int) {
        holder.setIsRecyclable(false)
        holder.bind(getItem(position))
    }
}

private class ToDoDiffUtil : DiffUtil.ItemCallback<ToDoData>() {

    override fun areItemsTheSame(oldItem: ToDoData, newItem: ToDoData): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: ToDoData, newItem: ToDoData): Boolean {
        return oldItem == newItem
    }
}