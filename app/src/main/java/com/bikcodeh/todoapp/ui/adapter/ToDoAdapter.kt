package com.bikcodeh.todoapp.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bikcodeh.todoapp.data.model.ToDoData
import com.bikcodeh.todoapp.databinding.ItemTodoBinding

class ToDoAdapter(private val onToDoClick: (ToDoData) -> Unit) : ListAdapter<ToDoData, ToDoAdapter.ToDoViewHolder>(ToDoDiffUtil()) {

    inner class ToDoViewHolder(private val binding: ItemTodoBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(toDoData: ToDoData) {
            with(binding) {
                this.toDoData = toDoData
                this.executePendingBindings()
                this.root.setOnClickListener {
                    onToDoClick(toDoData)
                }
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