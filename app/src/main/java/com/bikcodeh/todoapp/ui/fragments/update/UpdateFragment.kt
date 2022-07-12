package com.bikcodeh.todoapp.ui.fragments.update

import android.os.Bundle
import android.view.*
import android.widget.AdapterView
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.bikcodeh.todoapp.R
import com.bikcodeh.todoapp.data.model.ToDoData
import com.bikcodeh.todoapp.data.model.parsePriority
import com.bikcodeh.todoapp.data.model.toIndex
import com.bikcodeh.todoapp.databinding.FragmentUpdateBinding
import com.bikcodeh.todoapp.ui.util.observeFlows
import com.bikcodeh.todoapp.ui.util.snack
import com.bikcodeh.todoapp.ui.viewmodel.ToDoViewModel
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class UpdateFragment : Fragment() {

    private var _binding: FragmentUpdateBinding? = null
    private val binding: FragmentUpdateBinding
        get() = _binding!!

    private val args by navArgs<UpdateFragmentArgs>()
    private val toDoViewModel: ToDoViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentUpdateBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val menuHost: MenuHost = requireActivity()

        menuHost.addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.update_fragment_menu, menu)
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                return when (menuItem.itemId) {
                    R.id.menu_save -> {
                        updateNote(
                            binding.titleNote.text.toString(),
                            binding.descriptionNote.text.toString()
                        )
                        true
                    }
                    R.id.menu_delete -> {
                        deleteNote(
                            ToDoData(
                                id = args.toDoItem.id,
                                title = args.toDoItem.title,
                                description = args.toDoItem.description,
                                priority = args.toDoItem.priority
                            )
                        )
                        true
                    }
                    else -> false
                }
            }
        }, viewLifecycleOwner, Lifecycle.State.RESUMED)
        setSpinnerListener()
        setData()
        setUpObserver()
    }

    private fun setSpinnerListener() {
        val listener: AdapterView.OnItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    val color = when (position) {
                        0 -> R.color.red
                        1 -> R.color.yellow
                        2 -> R.color.green
                        else -> R.color.green
                    }

                    (parent?.getChildAt(0) as TextView).setTextColor(
                        ContextCompat.getColor(
                            requireContext(),
                            color
                        )
                    )
                }

                override fun onNothingSelected(p0: AdapterView<*>?) {}
            }
        binding.prioritySpinner.onItemSelectedListener = listener
    }

    private fun setUpObserver() {

        observeFlows { scope ->
            scope.launch {
                toDoViewModel.updateNoteUiEvent.collect {
                    when (it) {
                        ToDoViewModel.UpdateNoteUiEvent.Idle -> {}
                        ToDoViewModel.UpdateNoteUiEvent.Success -> {
                            requireView().snack(getString(R.string.updated_success))
                            findNavController().popBackStack()
                        }
                        ToDoViewModel.UpdateNoteUiEvent.SuccessDelete -> {
                            requireView().snack(getString(R.string.delete_success))
                            findNavController().popBackStack()
                        }
                    }
                }
            }

            scope.launch {
                toDoViewModel.formState.collect { formState ->
                    if (formState.isFormValid) {
                        toDoViewModel.onEvent(
                            ToDoViewModel.ToDoUiEvent.UpdateNote(
                                ToDoData(
                                    id = args.toDoItem.id,
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
        }
    }

    private fun updateNote(title: String, description: String) {
        toDoViewModel.onEvent(
            ToDoViewModel.ToDoUiEvent.ValidateForm(
                title, description
            )
        )
    }

    private fun deleteNote(todoData: ToDoData) {
        val builder = AlertDialog.Builder(requireContext())
        builder.setPositiveButton(getString(R.string.yes_option)) { _, _ ->
            toDoViewModel.onEvent(ToDoViewModel.ToDoUiEvent.DeleteNote(todoData))
        }
        builder.setNegativeButton(getString(R.string.no_option)) { _, _ -> }
        builder.setTitle(getString(R.string.delete_note_title, args.toDoItem.title))
        builder.setMessage(getString(R.string.delete_description))
        builder.create().show()
    }

    private fun setData() {
        with(binding) {
            titleNote.setText(args.toDoItem.title)
            descriptionNote.setText(args.toDoItem.description)
            prioritySpinner.setSelection(args.toDoItem.priority.toIndex())
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}