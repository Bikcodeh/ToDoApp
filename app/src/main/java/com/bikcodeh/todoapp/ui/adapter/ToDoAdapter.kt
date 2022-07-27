package com.bikcodeh.todoapp.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bikcodeh.todoapp.R
import com.bikcodeh.todoapp.data.model.ToDoData
import com.bikcodeh.todoapp.databinding.ItemTodoBinding
import com.bikcodeh.todoapp.ui.util.initAnimation
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

class ToDoAdapter(private val onToDoClick: (ToDoData) -> Unit) :
    RecyclerView.Adapter<ToDoAdapter.ToDoViewHolder>() {

    private var _notes = emptyList<ToDoData>()

    fun setData(notes: List<ToDoData>) {
        val diffUtil = DiffUtilCustom(_notes, notes)
        val diffResults = DiffUtil.calculateDiff(diffUtil)
        _notes = notes
        diffResults.dispatchUpdatesTo(this)
    }

    fun getNotes(): List<ToDoData> = _notes

    private val _isSelectedSomeItem = Channel<Boolean>(Channel.UNLIMITED)
    val isSelectedSomeItem = _isSelectedSomeItem.receiveAsFlow()

    private val _isEditing = Channel<Boolean>(Channel.UNLIMITED)
    val isEditing = _isEditing.receiveAsFlow()


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
                    if (toDoData.displaySelector) {
                        toDoData.isSelected = !toDoData.isSelected
                        todoCheckboxItem.isChecked = !todoCheckboxItem.isChecked
                        setIsEditing(true)
                        isSomeItemSelected()
                    } else {
                        setIsEditing(false)
                        onToDoClick(toDoData)
                    }
                }
                this.todoItemContainer.setOnLongClickListener {
                    if (!toDoData.displaySelector) {
                        displaySelectors(true)
                        toDoData.isSelected = !toDoData.isSelected
                        notifyItemChanged(adapterPosition)
                        todoCheckboxItem.isChecked = !toDoData.isSelected
                        setIsEditing(true)
                        isSomeItemSelected()
                        true
                    } else {
                        false
                    }
                }
                todoCheckboxItem.setOnClickListener {
                    toDoData.isSelected = !toDoData.isSelected
                    todoCheckboxItem.isChecked = toDoData.isSelected
                    isSomeItemSelected()
                }
            }
        }
    }

    private fun isSomeItemSelected() {
        CoroutineScope(Dispatchers.IO).launch {
            _isSelectedSomeItem.send(_notes.any { it.isSelected })
        }
    }

    fun setIsEditing(isEditing: Boolean) {
        CoroutineScope(Dispatchers.IO).launch {
            _isEditing.send(isEditing)
        }
    }

    fun displaySelectors(display: Boolean, unSelect: Boolean? = null) {
        _notes.forEachIndexed { index, toDoData ->
            toDoData.displaySelector = display
            unSelect?.let {
                toDoData.isSelected = unSelect
            }
        }
        notifyDataSetChanged()
        isSomeItemSelected()
    }

    fun getItemsToDelete(): List<ToDoData> {
        return _notes.filter { it.isSelected }
    }

    fun onDeleteAllComplete() {
        CoroutineScope(Dispatchers.IO).launch {
            _isSelectedSomeItem.send(false)
            _isEditing.send(false)
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
        holder.setIsRecyclable(false)
        holder.bind(_notes[position])
    }

    override fun getItemCount(): Int {
        return _notes.count()
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

private class DiffUtilCustom(
    private val oldList: List<ToDoData>,
    private val newList: List<ToDoData>
) : DiffUtil.Callback() {
    override fun getOldListSize(): Int {
        return oldList.size
    }

    override fun getNewListSize(): Int {
        return newList.size
    }

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldList[oldItemPosition].id == newList[newItemPosition].id
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldList[oldItemPosition] == newList[newItemPosition]
    }

}