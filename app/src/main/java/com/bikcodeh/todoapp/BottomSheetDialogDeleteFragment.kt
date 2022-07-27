package com.bikcodeh.todoapp

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.bikcodeh.todoapp.databinding.FragmentBottomSheetDialogDeleteBinding
import com.bikcodeh.todoapp.databinding.FragmentNotesBinding
import com.bikcodeh.todoapp.ui.viewmodel.ToDoViewModel
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class BottomSheetDialogDeleteFragment : BottomSheetDialogFragment() {

    private var _binding: FragmentBottomSheetDialogDeleteBinding? = null
    private val binding: FragmentBottomSheetDialogDeleteBinding
        get() = _binding!!

    private val toDoViewModel: ToDoViewModel by activityViewModels()

    private val args by navArgs<BottomSheetDialogDeleteFragmentArgs>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentBottomSheetDialogDeleteBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setListeners()
        binding.bottomDeleteItems.text = resources.getQuantityString(
            R.plurals.delete_items,
            args.itemsToDelete.count(),
            args.itemsToDelete.count().toString()
        )
    }

    private fun setListeners() {
        with(binding) {
            deleteNotesConfirmBtn.setOnClickListener {
                toDoViewModel.deleteItems(args.itemsToDelete.toList())
                toDoViewModel.setIsSelecting(true)
                this@BottomSheetDialogDeleteFragment.dismiss()
            }
            cancelDeleteNotesBtn.setOnClickListener {
                this@BottomSheetDialogDeleteFragment.dismiss()
            }
        }
    }
}