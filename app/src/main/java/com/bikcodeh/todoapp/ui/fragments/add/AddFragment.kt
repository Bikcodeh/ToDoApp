package com.bikcodeh.todoapp.ui.fragments.add

import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.bikcodeh.todoapp.R
import com.bikcodeh.todoapp.data.model.ToDoData
import com.bikcodeh.todoapp.data.model.parsePriority
import com.bikcodeh.todoapp.databinding.FragmentAddBinding
import com.bikcodeh.todoapp.ui.viewmodel.ToDoViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class AddFragment : Fragment() {

    private var _binding: FragmentAddBinding? = null
    private val binding: FragmentAddBinding
        get() = _binding!!

    private val toDoViewModel: ToDoViewModel by viewModels()

    private lateinit var menuHost: MenuHost

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAddBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        menuHost = requireActivity()

        menuHost.addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.add_fragment_menu, menu)
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                return when (menuItem.itemId) {
                    R.id.menu_add -> {
                        addNote(
                            binding.titleNote.text.toString(),
                            binding.descriptionNote.text.toString()
                        )
                        true
                    }
                    else -> false
                }
            }
        }, viewLifecycleOwner, Lifecycle.State.RESUMED)
        setUpObservers()
    }

    private fun setUpObservers() {
        viewLifecycleOwner.lifecycleScope.launch {
            toDoViewModel.formState.collect { formState ->
                if (formState.isFormValid) {
                    toDoViewModel.onEvent(
                        ToDoViewModel.ToDoUiEvent.InsertNote(
                            ToDoData(
                                title = binding.titleNote.text.toString(),
                                priority = parsePriority(binding.prioritySpinner.selectedItem.toString()),
                                description = binding.descriptionNote.text.toString()
                            )
                        )
                    )
                }
                formState.titleError?.let { error ->
                    binding.titleNote.error = getString(error)
                }
                formState.descriptionError?.let { error ->
                    binding.descriptionNote.error = getString(error)
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            toDoViewModel.events.collect {
                when (it) {
                    ToDoViewModel.ToDoValidationFormEvent.Success -> {
                        Toast.makeText(
                            requireContext(),
                            getString(R.string.note_added),
                            Toast.LENGTH_SHORT
                        ).show()
                        findNavController().popBackStack()
                    }
                }
            }
        }
    }

    private fun addNote(titleNote: String, descriptionNote: String) {
        toDoViewModel.onEvent(
            ToDoViewModel.ToDoUiEvent.ValidateForm(
                titleNote, descriptionNote
            )
        )

    }
}