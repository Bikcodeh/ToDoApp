package com.bikcodeh.todoapp.ui.fragments.update

import android.os.Bundle
import android.view.*
import android.widget.AdapterView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.lifecycle.Lifecycle
import androidx.navigation.fragment.navArgs
import com.bikcodeh.todoapp.R
import com.bikcodeh.todoapp.data.model.toIndex
import com.bikcodeh.todoapp.databinding.FragmentUpdateBinding

class UpdateFragment : Fragment() {

    private var _binding: FragmentUpdateBinding? = null
    private val binding: FragmentUpdateBinding
        get() = _binding!!

    private val args by navArgs<UpdateFragmentArgs>()

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
                    R.id.menu_save -> true
                    else -> false
                }
            }
        }, viewLifecycleOwner, Lifecycle.State.RESUMED)
        setSpinnerListener()
        setData()
    }

    private fun setSpinnerListener() {
        val listener: AdapterView.OnItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                when (position) {
                    0 -> { (parent?.getChildAt(0) as TextView).setTextColor(ContextCompat.getColor(requireContext(), R.color.red)) }
                    1 -> { (parent?.getChildAt(0) as TextView).setTextColor(ContextCompat.getColor(requireContext(), R.color.yellow)) }
                    2 -> { (parent?.getChildAt(0) as TextView).setTextColor(ContextCompat.getColor(requireContext(), R.color.green)) }
                }
            }
            override fun onNothingSelected(p0: AdapterView<*>?) {}
        }
        binding.prioritySpinner.onItemSelectedListener = listener
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