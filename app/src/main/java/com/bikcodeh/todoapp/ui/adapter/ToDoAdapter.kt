package com.bikcodeh.todoapp.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bikcodeh.todoapp.R
import com.bikcodeh.todoapp.data.model.Priority
import com.bikcodeh.todoapp.data.model.ToDoData
import com.bikcodeh.todoapp.databinding.ItemTodoBinding

class ToDoAdapter : ListAdapter<ToDoData, ToDoAdapter.ToDoViewHolder>(ToDoDiffUtil()) {

    inner class ToDoViewHolder(private val binding: ItemTodoBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(toDoData: ToDoData) {
            with(binding) {
                todoTitleItem.text = toDoData.title
                todoDescriptionItem.text = toDoData.description
                val color = when (toDoData.priority) {
                    Priority.HIGH -> R.color.red
                    Priority.MEDIUM -> R.color.yellow
                    Priority.LOW -> R.color.green
                }
                priorityIndicator.setCardBackgroundColor(
                    ContextCompat.getColor(
                        binding.root.context,
                        color
                    )
                )
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ToDoViewHolder {
        return ToDoViewHolder(
            ItemTodoBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        )
    }

    override fun onBindViewHolder(holder: ToDoViewHolder, position: Int) {
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