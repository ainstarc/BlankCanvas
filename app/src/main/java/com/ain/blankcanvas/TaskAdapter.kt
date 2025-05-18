package com.ain.blankcanvas

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

data class TaskNode(
    val task: TaskItem,
    val children: MutableList<TaskNode> = mutableListOf(),
    var isExpanded: Boolean = false
)

data class DisplayTask(
    val task: TaskItem,
    val level: Int,
    val node: TaskNode
)

fun flattenTaskTree(tree: List<TaskNode>, level: Int = 0): List<DisplayTask> {
    val list = mutableListOf<DisplayTask>()
    for (node in tree) {
        list.add(DisplayTask(node.task, level, node))
        if (node.isExpanded) {
            list.addAll(flattenTaskTree(node.children, level + 1))
        }
    }
    return list
}


class TaskAdapter(
    private var tasks: List<TaskNode>,
    private var items: List<DisplayTask>,
    private val onItemToggle: () -> Unit,
    private val onTaskLongClick: (TaskItem) -> Unit
) :
    RecyclerView.Adapter<TaskAdapter.TaskViewHolder>() {

    inner class TaskViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val titleView: TextView = itemView.findViewById(R.id.taskTitle)
        val typeView: TextView = itemView.findViewById(R.id.taskType)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_task, parent, false)
        return TaskViewHolder(view)
    }

    override fun onBindViewHolder(holder: TaskViewHolder, position: Int) {
        val item = items[position]
        holder.titleView.text = item.task.title
        holder.typeView.text = item.task.type.name

        // Indent based on level
        val paddingLeft = 20 * item.level
        holder.itemView.setPadding(paddingLeft, 10, 10, 10)

        // Toggle expand/collapse
        holder.itemView.setOnClickListener {
            if (item.node.children.isNotEmpty()) {
                item.node.isExpanded = !item.node.isExpanded
                onItemToggle() // Callback to refresh list
            }
        }
        holder.itemView.setOnLongClickListener {
            onTaskLongClick(item.task)
            true
        }
    }

    fun updateData(newItems: List<DisplayTask>) {
        items = newItems
        notifyDataSetChanged()
    }


    override fun getItemCount(): Int = items.size

}

