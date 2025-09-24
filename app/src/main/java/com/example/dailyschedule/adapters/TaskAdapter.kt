package com.example.dailyschedule.adapters

import android.graphics.BitmapFactory
import android.view.*
import androidx.recyclerview.widget.RecyclerView
import com.example.dailyschedule.databinding.TaskListItemBinding
import com.example.dailyschedule.models.TaskModel
import java.io.File

class TaskAdapter(
    private val list: MutableList<TaskModel>,
    private val onEditClick: (TaskModel) -> Unit,
    private val onDeleteClick: (TaskModel) -> Unit
) : RecyclerView.Adapter<TaskAdapter.VH>() {

    inner class VH(val binding: TaskListItemBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val binding = TaskListItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return VH(binding)
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        val task = list[position]
        holder.binding.apply {
            tvTaskName.text = task.title
            tvTaskTime.text = task.time

            val imageFile = File(task.image)
            if (imageFile.exists()) {
                val bitmap = BitmapFactory.decodeFile(imageFile.absolutePath)
                image.setImageBitmap(bitmap)
            } else {
                image.setImageResource(android.R.drawable.ic_menu_report_image) // fallback image
            }

            editTask.setOnClickListener { onEditClick(task) }
            deleteTask.setOnClickListener { onDeleteClick(task) }
        }
    }

    override fun getItemCount(): Int = list.size

    fun moveItem(from: Int, to: Int) {
        val item = list.removeAt(from)
        list.add(to, item)
        notifyItemMoved(from, to)
    }
}
