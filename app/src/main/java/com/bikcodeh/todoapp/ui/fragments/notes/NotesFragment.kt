package com.bikcodeh.todoapp.ui.fragments.notes

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.*
import android.view.View.OnTouchListener
import android.widget.PopupMenu
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.*
import com.bikcodeh.todoapp.R
import com.bikcodeh.todoapp.data.model.Priority
import com.bikcodeh.todoapp.data.model.ToDoData
import com.bikcodeh.todoapp.databinding.FragmentNotesBinding
import com.bikcodeh.todoapp.ui.adapter.ToDoAdapter
import com.bikcodeh.todoapp.ui.util.observeFlows
import com.bikcodeh.todoapp.ui.viewmodel.ToDoViewModel
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import jp.wasabeef.recyclerview.animators.SlideInUpAnimator
import kotlinx.coroutines.launch


@AndroidEntryPoint
class NotesFragment : Fragment() {

    private var _binding: FragmentNotesBinding? = null
    private val binding: FragmentNotesBinding
        get() = _binding!!

    private var isEmpty: Boolean = true

    private val todoAdapter: ToDoAdapter by lazy {
        ToDoAdapter {
            val action = NotesFragmentDirections.actionNotesFragmentToUpdateFragment(it)
            findNavController().navigate(action)
        }
    }
    private val toDoViewModel: ToDoViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentNotesBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = this
        binding.toDoViewModel = toDoViewModel
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.notesMenuBtn.setOnClickListener {
            showMenu(it)
        }
        setListener()
        setUpViews()
        setCollectors()
    }

    private fun setListener() {

        binding.notesRecyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                if (dy > 0) {
                    binding.addNoteFab.hide();
                } else if (dy < 0) {
                    binding.addNoteFab.show()
                } else {
                    binding.addNoteFab.isVisible = binding.searchNote.text.toString().isEmpty()
                }

            }

            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    binding.addNoteFab.show()
                }
            }
        })

        binding.searchNote.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun onTextChanged(text: CharSequence?, p1: Int, p2: Int, p3: Int) {
                binding.clearTextIvBtn.isVisible = text.toString().isNotEmpty()
                if (text.toString().isNotEmpty()) {
                    binding.addNoteFab.hide()
                    binding.notesMenuBtn.visibility = View.INVISIBLE
                    toDoViewModel.onEvent(ToDoViewModel.ToDoUiEvent.FilterNotes(text.toString()))
                } else {
                    binding.addNoteFab.show()
                    binding.notesMenuBtn.visibility = View.VISIBLE
                    binding.noDataGroup.isVisible = toDoViewModel.notes.value.isEmpty()
                    toDoViewModel.onEvent(ToDoViewModel.ToDoUiEvent.OnFilterClear)
                }
            }

            override fun afterTextChanged(text: Editable?) {}
        })

        binding.clearTextIvBtn.setOnClickListener {
            binding.searchNote.text = null
            toDoViewModel.onEvent(ToDoViewModel.ToDoUiEvent.OnFilterClear)
        }
    }

    private fun showMenu(view: View) {
        val popUp = PopupMenu(requireContext(), view)
        popUp.menuInflater.inflate(R.menu.list_fragment_menu, popUp.menu)

        popUp.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.menu_delete_all -> {
                    confirmDeleteAll()
                    true
                }
                R.id.menu_priority_high -> {
                    toDoViewModel.onEvent(ToDoViewModel.ToDoUiEvent.SortNotes(Priority.HIGH))
                    true
                }
                R.id.menu_priority_low -> {
                    toDoViewModel.onEvent(ToDoViewModel.ToDoUiEvent.SortNotes(Priority.LOW))
                    true
                }
                R.id.menu_priority_none -> {
                    toDoViewModel.onEvent(ToDoViewModel.ToDoUiEvent.SortNotes(Priority.LOW))
                    true
                }
                else -> false
            }
        }
        popUp.show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun setUpViews() {
        binding.notesRecyclerView.apply {
            layoutManager = StaggeredGridLayoutManager(2, 1)
            adapter = todoAdapter
            itemAnimator = SlideInUpAnimator().apply {
                addDuration = 300
            }
            //swipeToDelete(this)
        }
    }

    private fun swipeToDelete(recyclerView: RecyclerView) {
        val swipeToDeleteCallback = object : SwipeToDelete() {
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                //Delete item
                val deletedItem = todoAdapter.currentList[viewHolder.adapterPosition]
                toDoViewModel.onEvent(ToDoViewModel.ToDoUiEvent.DeleteNote(deletedItem))
                //Restore deleted item
                restoreDeletedData(deletedItem, viewHolder.adapterPosition)
            }
        }
        val itemTouchHelper = ItemTouchHelper(swipeToDeleteCallback)
        itemTouchHelper.attachToRecyclerView(recyclerView)
    }

    private fun restoreDeletedData(deletedItem: ToDoData, position: Int) {
        val snackBar =
            Snackbar.make(binding.addNoteFab, "Deleted ${deletedItem.title}", Snackbar.LENGTH_SHORT)
        snackBar.setAction(getString(R.string.undo)) {
            toDoViewModel.onEvent(ToDoViewModel.ToDoUiEvent.InsertNote(deletedItem))
            todoAdapter.notifyItemChanged(position)
        }
        snackBar.show()
    }

    private fun setCollectors() {
        toDoViewModel.isEmpty.observe(viewLifecycleOwner) {
            isEmpty = it
            binding.noDataGroup.isVisible = it
            if (isEmpty) {
                binding.notesMenuBtn.visibility = View.INVISIBLE
            } else {
                binding.notesMenuBtn.visibility = View.VISIBLE
            }
        }
        observeFlows { scope ->
            scope.launch {
                toDoViewModel.notes.collect { notes ->
                    binding.notesRecyclerView.isVisible = notes.isNotEmpty()
                    binding.noDataGroup.isVisible = notes.isEmpty()
                    todoAdapter.submitList(notes)
                }
            }
        }
    }

    private fun confirmDeleteAll() {
        val builder = AlertDialog.Builder(requireContext())
        builder.setPositiveButton(getString(R.string.yes_option)) { _, _ ->
            toDoViewModel.onEvent(ToDoViewModel.ToDoUiEvent.DeleteAllNotes)
        }
        builder.setNegativeButton(getString(R.string.no_option)) { _, _ -> }
        builder.setTitle(getString(R.string.delete_all_notes_title))
        builder.setMessage(getString(R.string.delete_all_description))
        builder.create().show()
    }

}