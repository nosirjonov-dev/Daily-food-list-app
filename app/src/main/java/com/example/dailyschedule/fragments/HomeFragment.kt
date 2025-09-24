package com.example.dailyschedule.fragments

import android.app.AlertDialog
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.example.dailyschedule.R
import com.example.dailyschedule.adapters.DateAdapter
import com.example.dailyschedule.databinding.DialogEditDateBinding
import com.example.dailyschedule.databinding.FragmentHomeBinding
import com.example.dailyschedule.db.PlanDatabaseHelper
import com.example.dailyschedule.models.DateModel
import java.util.Date

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private lateinit var db: PlanDatabaseHelper
    private lateinit var adapter: DateAdapter
    private lateinit var dateList: MutableList<DateModel>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        db = PlanDatabaseHelper(requireContext())
        loadData()

        binding.btnAddDate.setOnClickListener {
            findNavController().navigate(R.id.addDateFragment)
        }
    }

    private fun loadData() {
        dateList = db.getAllDates().toMutableList()
        adapter = DateAdapter(
            list = dateList,
            onClick = { dateModel ->
                val bundle = Bundle().apply {
                    putInt("dateId", dateModel.id)
                    putString("dateText", dateModel.date)
                }
                findNavController().navigate(R.id.taskListFragment, bundle)
            },
            onLongClick = { dateModel ->
                showEditDialog(dateModel)
            }
        )

        binding.rvDates.layoutManager = GridLayoutManager(requireContext(), 2)
        binding.rvDates.adapter = adapter
    }

    private fun showEditDialog(dateModel: DateModel) {
        val dialogBinding = DialogEditDateBinding.inflate(LayoutInflater.from(requireContext()))

        dialogBinding.etDate.setText(dateModel.date)
        dialogBinding.etDay.setText(dateModel.day)

        AlertDialog.Builder(requireContext())
            .setTitle("Edit date")
            .setView(dialogBinding.root)
            .setPositiveButton("Save") { _, _ ->
                val newDate = dialogBinding.etDate.text.toString()
                val newDay = dialogBinding.etDay.text.toString()
                val updateDate = DateModel(dateModel.id, newDate, newDay)
                db.updateDate(updateDate)
                loadData()
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
