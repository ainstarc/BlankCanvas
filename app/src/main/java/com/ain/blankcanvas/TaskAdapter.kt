package com.ain.blankcanvas

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class TaskAdapter(
    private val tasks: List<TaskNode>,
    private var items: List<DisplayTask>,
    private val onItemToggle: () -> Unit,
    private val onTaskLongClick: (TaskItem) -> Unit
) : RecyclerView.Adapter<TaskAdapter.TaskViewHolder>() {

    inner class TaskViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val titleView: TextView = itemView.findViewById(R.id.taskTitle)
        val descriptionView: TextView = itemView.findViewById(R.id.taskDescription)

        init {
            itemView.setOnClickListener {
                val task = items[adapterPosition]
                task.expanded = !task.expanded
                onItemToggle()
            }

            itemView.setOnLongClickListener {
                val task = items[adapterPosition]
                onTaskLongClick(task.task)
                true
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_task, parent, false)
        return TaskViewHolder(view)
    }

    override fun onBindViewHolder(holder: TaskViewHolder, position: Int) {
        val displayTask = items[position]
        val task = displayTask.task
        holder.titleView.text = "${"\t".repeat(displayTask.depth)}${task.title}"
        holder.descriptionView.text = task.description
    }

    override fun getItemCount(): Int = items.size

    fun updateData(newItems: List<DisplayTask>) {
        items = newItems
        notifyDataSetChanged()
    }
}
