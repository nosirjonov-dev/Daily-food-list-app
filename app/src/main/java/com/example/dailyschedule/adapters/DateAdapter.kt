package com.example.dailyschedule.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.dailyschedule.databinding.DateListItemBinding
import com.example.dailyschedule.models.DateModel

class DateAdapter(
    private val list: List<DateModel>,
    private val onClick: (DateModel) -> Unit,
    private val onLongClick: (DateModel) -> Unit
) : RecyclerView.Adapter<DateAdapter.VH>() {

    inner class VH(var dateListItemBinding: DateListItemBinding) : RecyclerView.ViewHolder(dateListItemBinding.root) {

        fun onBind(model: DateModel) {
            dateListItemBinding.tvDate.text = model.date
            dateListItemBinding.tvDay.text = model.day
            itemView.setOnClickListener { onClick(model) }
            itemView.setOnLongClickListener {
                onLongClick(model)
                true
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        return VH(DateListItemBinding.inflate(LayoutInflater.from(parent.context),parent,false))
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        holder.onBind(list[position])
    }

    override fun getItemCount(): Int = list.size
}