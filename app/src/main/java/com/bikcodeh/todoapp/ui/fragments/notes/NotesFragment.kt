package com.bikcodeh.todoapp.ui.fragments.notes

import android.os.Bundle
import android.view.*
import android.widget.PopupMenu
import androidx.annotation.MenuRes
import androidx.appcompat.app.AlertDialog
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.bikcodeh.todoapp.R
import com.bikcodeh.todoapp.databinding.FragmentNotesBinding
import com.bikcodeh.todoapp.ui.adapter.ToDoAdapter
import com.bikcodeh.todoapp.ui.util.observeFlows
import com.bikcodeh.todoapp.ui.util.snack
import com.bikcodeh.todoapp.ui.viewmodel.ToDoViewModel
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
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
        setUpViews()
        setCollectors()
        setListener()
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
                    binding.addNoteFab.show()
                }
            }
        })
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
        }
    }

    private fun setCollectors() {
        toDoViewModel.isEmpty.observe(viewLifecycleOwner) {
            isEmpty = it
            if (isEmpty) {
                binding.notesMenuBtn.visibility = View.INVISIBLE
            } else {
                binding.notesMenuBtn.visibility = View.VISIBLE
            }
        }
        observeFlows { scope ->
            scope.launch {
                toDoViewModel.notes.collect { notes ->
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