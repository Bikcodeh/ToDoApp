package com.bikcodeh.todoapp.ui.fragments.notes

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.PopupMenu
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AlertDialog
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.bikcodeh.todoapp.R
import com.bikcodeh.todoapp.data.model.Priority
import com.bikcodeh.todoapp.data.model.ToDoData
import com.bikcodeh.todoapp.databinding.FragmentBottomSheetDialogDeleteBinding
import com.bikcodeh.todoapp.databinding.FragmentNotesBinding
import com.bikcodeh.todoapp.ui.adapter.ToDoAdapter
import com.bikcodeh.todoapp.ui.util.initAnimation
import com.bikcodeh.todoapp.ui.util.observeFlows
import com.bikcodeh.todoapp.ui.viewmodel.ToDoViewModel
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import jp.wasabeef.recyclerview.animators.SlideInUpAnimator
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


@AndroidEntryPoint
class NotesFragment : Fragment() {

    private var _binding: FragmentNotesBinding? = null
    private val binding: FragmentNotesBinding
        get() = _binding!!

    private val todoAdapter: ToDoAdapter by lazy {
        ToDoAdapter {
            val action = NotesFragmentDirections.actionNotesFragmentToUpdateFragment(it)
            findNavController().navigate(action)
        }
    }
    private val toDoViewModel: ToDoViewModel by activityViewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activity?.onBackPressedDispatcher?.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (toDoViewModel.isSelecting) {
                    todoAdapter.displaySelectors(display = false, unSelect = false)
                    todoAdapter.setIsEditing(false)
                } else {
                    activity?.finish()
                }
            }
        })
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentNotesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpViews()
        setCollectors()
        setListener()
    }

    private fun setListener() {

        binding.addNoteFab.setOnClickListener {
            findNavController().navigate(R.id.action_notesFragment_to_addFragment)
        }

        binding.notesDelete.setOnClickListener {
            val action = NotesFragmentDirections.actionNotesFragmentToDeleteFragment(todoAdapter.getItemsToDelete().toTypedArray())
            findNavController().navigate(action)
        }

        binding.searchNote.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun onTextChanged(text: CharSequence?, p1: Int, p2: Int, p3: Int) {

                if (text.toString().isNotEmpty()) {
                    binding.addNoteFab.hide()
                    binding.clearTextIvBtn.initAnimation(R.anim.show)
                    binding.clearTextIvBtn.visibility = View.VISIBLE
                } else {
                    binding.addNoteFab.show()
                    binding.clearTextIvBtn.initAnimation(R.anim.fade_out)
                    binding.clearTextIvBtn.visibility = View.GONE
                    binding.noDataGroup.isVisible = toDoViewModel.notes.value.isEmpty()
                }
            }

            override fun afterTextChanged(text: Editable?) {
                if (text.toString().isEmpty()) {
                    binding.notesMenuBtn.initAnimation(R.anim.fade_in)
                    binding.notesMenuBtn.visibility = View.VISIBLE
                    binding.notesMenuBtn.isEnabled = true
                    toDoViewModel.onEvent(ToDoViewModel.ToDoUiEvent.OnFilterClear)
                    toDoViewModel.setIsSearching(false)
                } else {
                    binding.notesMenuBtn.initAnimation(R.anim.hide)
                    binding.notesMenuBtn.visibility = View.INVISIBLE
                    binding.notesMenuBtn.isEnabled = false
                    toDoViewModel.setIsSearching(true)
                    toDoViewModel.onEvent(ToDoViewModel.ToDoUiEvent.FilterNotes(text.toString()))
                }
            }
        })

        binding.clearTextIvBtn.setOnClickListener {
            binding.searchNote.text = null
            toDoViewModel.setIsSearching(false)
            toDoViewModel.onEvent(ToDoViewModel.ToDoUiEvent.OnFilterClear)
        }

        binding.notesMenuBtn.setOnClickListener {
            showMenu(it)
        }
    }

    private fun showMenu(view: View) {
        val popUp = PopupMenu(requireContext(), view)
        popUp.menuInflater.inflate(R.menu.list_fragment_menu, popUp.menu)

        popUp.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.menu_delete_all -> {
                    confirmDeleteAll()
                    popUp.dismiss()
                    true
                }
                R.id.menu_priority_high -> {
                    toDoViewModel.onEvent(ToDoViewModel.ToDoUiEvent.SortNotes(Priority.HIGH))
                    popUp.dismiss()
                    true
                }
                R.id.menu_priority_low -> {
                    toDoViewModel.onEvent(ToDoViewModel.ToDoUiEvent.SortNotes(Priority.LOW))
                    popUp.dismiss()
                    true
                }
                R.id.menu_priority_none -> {
                    toDoViewModel.onEvent(ToDoViewModel.ToDoUiEvent.SortNotes(Priority.LOW))
                    popUp.dismiss()
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
                val deletedItem = todoAdapter.getNotes()[viewHolder.adapterPosition]
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
        observeFlows { scope ->
            scope.launch {
                toDoViewModel.notes.collect { notes ->
                    binding.notesRecyclerView.isVisible = notes.isNotEmpty()
                    binding.noDataGroup.isVisible = notes.isEmpty()
                    todoAdapter.setData(notes)
                    todoAdapter.notifyDataSetChanged()

                    if (toDoViewModel.isSearching) {
                        binding.notesMenuBtn.visibility = View.INVISIBLE
                        binding.notesMenuBtn.initAnimation(R.anim.hide)
                        binding.notesMenuBtn.isEnabled = false
                    } else {
                        if (notes.isEmpty()) {
                            binding.notesMenuBtn.visibility = View.INVISIBLE
                            binding.notesMenuBtn.initAnimation(R.anim.hide)
                            binding.notesMenuBtn.isEnabled = false
                            binding.searchContainer.visibility = View.GONE

                        } else {
                            binding.notesMenuBtn.visibility = View.VISIBLE
                            binding.notesMenuBtn.initAnimation(R.anim.show)
                            binding.notesMenuBtn.isEnabled = true
                            binding.searchContainer.visibility = View.VISIBLE
                        }
                    }

                    if (toDoViewModel.isSelecting) {
                        todoAdapter.setIsEditing(false)
                    }
                }
            }

            scope.launch {
                todoAdapter.isSelectedSomeItem.collect { isSomeItemSelected ->
                    binding.notesDelete.isEnabled = isSomeItemSelected
                }
            }

            scope.launch {
                todoAdapter.isEditing.collect { isEditing ->
                        toDoViewModel.setIsSelecting(isEditing)
                        if (isEditing) {
                            binding.notesDelete.visibility = View.VISIBLE
                            binding.searchNote.isEnabled = false
                            binding.addNoteFab.hide()
                            binding.notesMenuBtn.initAnimation(R.anim.hide)
                            binding.notesMenuBtn.visibility = View.INVISIBLE
                            binding.notesMenuBtn.isEnabled = false
                        } else {
                            if (todoAdapter.getNotes().isEmpty()) {
                                binding.notesMenuBtn.initAnimation(R.anim.hide)
                                binding.notesMenuBtn.visibility = View.INVISIBLE
                                binding.notesMenuBtn.isEnabled = false
                            } else {
                                binding.notesMenuBtn.visibility = View.VISIBLE
                                binding.notesMenuBtn.initAnimation(R.anim.fade_in)
                                binding.notesMenuBtn.isEnabled = true
                            }
                            binding.notesDelete.visibility = View.GONE
                            binding.searchNote.isEnabled = true
                            binding.addNoteFab.show()
                        }
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