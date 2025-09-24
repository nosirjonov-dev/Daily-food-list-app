package com.example.dailyschedule.fragments

import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.dailyschedule.R
import com.example.dailyschedule.adapters.TaskAdapter
import com.example.dailyschedule.databinding.DialogEditTaskBinding
import com.example.dailyschedule.databinding.FragmentTaskListBinding
import com.example.dailyschedule.db.PlanDatabaseHelper
import com.example.dailyschedule.models.TaskModel
import com.example.dailyschedule.models.Yordamchi

class TaskListFragment : Fragment() {

    private var _binding: FragmentTaskListBinding? = null
    private val binding get() = _binding!!
    private lateinit var db: PlanDatabaseHelper
    private var dateId: Int = -1
    private var dateText: String = ""

    // Hozir tahrirlanayotgan task
    private var currentTaskToEdit: TaskModel? = null

    // Galereyadan rasm tanlash uchun launcher
    private val editImageLauncher = registerForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            Yordamchi.file = it
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTaskListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        db = PlanDatabaseHelper(requireContext())

        // Bundle orqali kelgan ma'lumotlarni olish
        arguments?.let {
            dateId = it.getInt("dateId", -1)
            dateText = it.getString("dateText") ?: ""
        }

        binding.tvDateTitle.text = dateText

        binding.btnAddTask.setOnClickListener {
            val bundle = Bundle().apply {
                putInt("dateId", dateId)
            }
            findNavController().navigate(R.id.addTaskFragment, bundle)
        }

        loadTasks()

    }

    private fun loadTasks() {
        val tasks = db.getTasksByDate(dateId)
        val adapter = TaskAdapter(
            tasks.toMutableList(),
            onEditClick = { showEditDialog(it) },
            onDeleteClick = { showDeleteDialog(it) }
        )
        binding.rvTasks.layoutManager = LinearLayoutManager(requireContext())
        binding.rvTasks.adapter = adapter

        enableDragAndDrop(adapter)
    }

    private fun showDeleteDialog(task: TaskModel) {
        AlertDialog.Builder(requireContext())
            .setTitle("Attention!")
            .setMessage("Do you want to delete this task?")
            .setPositiveButton("Yes") { _, _ ->
                db.deleteTask(task.id)
                loadTasks()
            }
            .setNegativeButton("No", null)
            .show()
    }


    private fun showEditDialog(task: TaskModel) {
        val dialogBinding = DialogEditTaskBinding.inflate(layoutInflater)
        currentTaskToEdit = task

        dialogBinding.etEditName.setText(task.title)
        dialogBinding.etEditTime.setText(task.time)

        // Avvalgi rasmni ko‘rsatish
        val currentImage = task.image
        if (currentImage.isNotEmpty()) {
            dialogBinding.etImage.setImageURI(Uri.parse(currentImage))
            Yordamchi.file = Uri.parse(currentImage) // default sifatida mavjud rasm
        }

        // Rasmga bosilganda yangi rasm tanlash
        dialogBinding.etImage.setOnClickListener {
            editImageLauncher.launch("image/*")
        }

        AlertDialog.Builder(requireContext())
            .setTitle("Edit this task")
            .setView(dialogBinding.root)
            .setPositiveButton("Save") { _, _ ->
                val newTask = task.copy(
                    title = dialogBinding.etEditName.text.toString(),
                    time = dialogBinding.etEditTime.text.toString(),
                    image = Yordamchi.file.toString()
                )
                db.updateTask(newTask)
                loadTasks()
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun enableDragAndDrop(adapter: TaskAdapter) {
        val callback = object : ItemTouchHelper.SimpleCallback(
            ItemTouchHelper.UP or ItemTouchHelper.DOWN, 0
        ) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                val from = viewHolder.adapterPosition
                val to = target.adapterPosition
                adapter.moveItem(from, to)
                return true
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                // Swipe yo‘q
            }
        }

        val itemTouchHelper = ItemTouchHelper(callback)
        itemTouchHelper.attachToRecyclerView(binding.rvTasks)
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}