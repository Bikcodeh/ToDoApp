package com.bikcodeh.todoapp.ui.fragments.add

import android.os.Bundle
import android.view.*
import android.widget.AdapterView
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.bikcodeh.todoapp.R
import com.bikcodeh.todoapp.data.model.ToDoData
import com.bikcodeh.todoapp.data.model.parsePriority
import com.bikcodeh.todoapp.databinding.FragmentAddBinding
import com.bikcodeh.todoapp.ui.util.observeFlows
import com.bikcodeh.todoapp.ui.viewmodel.ToDoViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class AddFragment : Fragment() {

    private var _binding: FragmentAddBinding? = null
    private val binding: FragmentAddBinding
        get() = _binding!!

    private val toDoViewModel: ToDoViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAddBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        val listener: AdapterView.OnItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    when (position) {
                        0 -> {
                            (parent?.getChildAt(0) as TextView).setTextColor(
                                ContextCompat.getColor(
                                    requireContext(),
                                    R.color.red
                                )
                            )
                        }
                        1 -> {
                            (parent?.getChildAt(0) as TextView).setTextColor(
                                ContextCompat.getColor(
                                    requireContext(),
                                    R.color.yellow
                                )
                            )
                        }
                        2 -> {
                            (parent?.getChildAt(0) as TextView).setTextColor(
                                ContextCompat.getColor(
                                    requireContext(),
                                    R.color.green
                                )
                            )
                        }
                    }
                }

                override fun onNothingSelected(p0: AdapterView<*>?) {}
            }

        binding.prioritySpinner.onItemSelectedListener = listener
        setUpObservers()
        setListeners()
        binding.constraintLayout.transitionToEnd()
    }

    private fun setListeners() {
        with(binding) {
            addNoteBtn.setOnClickListener {
                addNote(
                    binding.titleNote.text.toString(),
                    binding.descriptionNote.text.toString()
                )
            }
            addNoteBackBtn.setOnClickListener {
                findNavController().popBackStack()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun setUpObservers() {
        observeFlows { scope ->
            scope.launch {
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

            scope.launch {
                toDoViewModel.addNoteEvent.collect {
                    when (it) {
                        ToDoViewModel.AddNoteUiEvent.Idle -> {}
                        ToDoViewModel.AddNoteUiEvent.Success -> {
                            findNavController().popBackStack()
                        }
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