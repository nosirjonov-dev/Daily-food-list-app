package com.example.dailyschedule.fragments

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.dailyschedule.databinding.FragmentAddTaskBinding
import com.example.dailyschedule.db.PlanDatabaseHelper
import com.example.dailyschedule.models.TaskModel
import com.example.dailyschedule.models.Yordamchi
import java.io.File
import java.io.FileOutputStream

class AddTaskFragment : Fragment() {

    private var _binding: FragmentAddTaskBinding? = null
    private val binding get() = _binding!!
    private lateinit var db: PlanDatabaseHelper
    private var dateId: Int = -1

    private val imagePicker = registerForActivityResult(ActivityResultContracts.OpenDocument()) { uri ->
        uri ?: return@registerForActivityResult
        requireContext().contentResolver.takePersistableUriPermission(uri, Intent.FLAG_GRANT_READ_URI_PERMISSION)
        binding.image.setImageURI(uri)

        // Fayl nusxasini ichki xotiraga saqlash
        val inputStream = requireContext().contentResolver.openInputStream(uri)
        val file = File(requireContext().filesDir, "task_${System.currentTimeMillis()}.jpg")
        val outputStream = FileOutputStream(file)
        inputStream?.copyTo(outputStream)
        inputStream?.close()
        outputStream.close()

        Yordamchi.file = Uri.fromFile(file) // saqlangan rasm yo'li
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAddTaskBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        db = PlanDatabaseHelper(requireContext())
        dateId = arguments?.getInt("dateId", -1) ?: -1

        binding.image.setOnClickListener {
            imagePicker.launch(arrayOf("image/*"))
        }

        binding.btnSaveTask.setOnClickListener {
            val title = binding.etTaskName.text.toString().trim()
            val time = binding.etTaskTime.text.toString().trim()
            val image = Yordamchi.file?.path ?: ""

            if (title.isNotEmpty() && time.isNotEmpty() && image.isNotEmpty()) {
                val task = TaskModel(0, dateId, title, time, image)
                db.insertTask(task)
                Toast.makeText(requireContext(), "Vazifa saqlandi", Toast.LENGTH_SHORT).show()
                findNavController().popBackStack()
            } else {
                Toast.makeText(requireContext(), "Barcha maydonlarni to'ldiring", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
