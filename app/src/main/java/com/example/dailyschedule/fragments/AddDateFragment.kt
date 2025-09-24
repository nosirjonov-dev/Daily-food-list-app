package com.example.dailyschedule.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.example.dailyschedule.R
import com.example.dailyschedule.databinding.FragmentAddDateBinding
import com.example.dailyschedule.db.PlanDatabaseHelper
import com.example.dailyschedule.models.DateModel

class AddDateFragment : Fragment() {

    private var _binding: FragmentAddDateBinding? = null
    private val binding get() = _binding!!
    private lateinit var db: PlanDatabaseHelper

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAddDateBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        db = PlanDatabaseHelper(requireContext())

        binding.btnSaveDate.setOnClickListener {
            val date = binding.etDate.text.toString()
            val day = binding.etDay.selectedItem.toString()

            if (date.isNotEmpty() && day.isNotEmpty()) {
                val dateModel = DateModel(0, date, day) // id = 0, chunki u AUTOINCREMENT
                db.insertDate(dateModel)
                findNavController().popBackStack()
            } else {
                Toast.makeText(requireContext(), "Bo'sh maydon bo'lmasin", Toast.LENGTH_SHORT).show()
            }

        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
