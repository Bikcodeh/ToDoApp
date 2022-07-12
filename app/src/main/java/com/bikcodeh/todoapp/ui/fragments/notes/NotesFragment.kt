package com.bikcodeh.todoapp.ui.fragments.notes

import android.os.Bundle
import android.view.*
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
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.bikcodeh.todoapp.R
import com.bikcodeh.todoapp.databinding.FragmentNotesBinding
import com.bikcodeh.todoapp.ui.adapter.ToDoAdapter
import com.bikcodeh.todoapp.ui.util.snack
import com.bikcodeh.todoapp.ui.viewmodel.ToDoViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

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
    private val toDoViewModel: ToDoViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentNotesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val menuHost: MenuHost = requireActivity()

        menuHost.addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.list_fragment_menu, menu)
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                return when (menuItem.itemId) {
                    R.id.menu_delete_all -> {
                        confirmDeleteAll()
                        true
                    }
                    else -> false
                }
            }
        }, viewLifecycleOwner, Lifecycle.State.RESUMED)
        setUpViews()
        setUpListeners()
        initObservers()
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

    private fun setUpListeners() {
        binding.addNoteFab.setOnClickListener {
            findNavController().navigate(R.id.action_notesFragment_to_addFragment)
        }
    }

    private fun initObservers() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.CREATED) {

                launch {
                    toDoViewModel.notes.collect { notes ->
                        todoAdapter.submitList(notes)
                        binding.notesRecyclerView.isVisible = notes.isNotEmpty()
                        binding.noDataGroup.isVisible = notes.isEmpty()
                    }
                }

                launch {
                    toDoViewModel.deleteAllNotesEvent.collect {
                        when (it) {
                            ToDoViewModel.DeleteAllUiEvent.Idle -> {}
                            ToDoViewModel.DeleteAllUiEvent.Success -> requireView().snack(
                                getString(
                                    R.string.deleted_all
                                )
                            )
                        }
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