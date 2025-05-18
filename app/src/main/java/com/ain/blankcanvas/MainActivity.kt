package com.ain.blankcanvas

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton

class MainActivity : AppCompatActivity() {

    private lateinit var taskTree: List<TaskNode>
    private lateinit var adapter: TaskAdapter
    private lateinit var dbHelper: TaskDatabaseHelper
    private var selectedParentId: Long? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        dbHelper = TaskDatabaseHelper(this)
        val tasks = dbHelper.getAllTasks()
        taskTree = buildTaskTree(tasks)
        val displayList = flattenTaskTree(taskTree)

        val recyclerView = findViewById<RecyclerView>(R.id.taskRecyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)
        adapter = TaskAdapter(
            tasks = taskTree,
            items = displayList,
            onItemToggle = {
                val updatedList = flattenTaskTree(taskTree)
                adapter.updateData(updatedList)
            },
            onTaskLongClick = { task ->
                showTaskOptionsDialog(task.id)
            }
        )
        recyclerView.adapter = adapter

        val fab = findViewById<FloatingActionButton>(R.id.addTaskFab)
        fab.setOnClickListener { showAddTaskDialog() }
    }

    private fun showTaskOptionsDialog(taskId: Long) {
        val options = arrayOf("Add Child Task", "Delete Task")
        AlertDialog.Builder(this)
            .setTitle("Select Action")
            .setItems(options) { _, which ->
                when (which) {
                    0 -> {
                        selectedParentId = taskId
                        showAddTaskDialog()
                    }

                    1 -> {
                        dbHelper.deleteTask(taskId)
                        reloadTasks()
                    }
                }
            }.show()
    }

    private fun showAddTaskDialog() {
        // Inflate dialog layout
        val dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_add_task, null)

        val titleInput = dialogView.findViewById<EditText>(R.id.taskTitleInput)
        val typeSpinner = dialogView.findViewById<Spinner>(R.id.taskTypeSpinner)
        val descriptionInput = dialogView.findViewById<EditText>(R.id.taskDescriptionInput)

        // Setup spinner adapter
        val spinnerAdapter = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_item,
            ItemType.values().map { it.name }
        )
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        typeSpinner.adapter = spinnerAdapter

        // Build and show the dialog
        AlertDialog.Builder(this)
            .setTitle("Add Task")
            .setView(dialogView)
            .setPositiveButton("Add") { _, _ ->
                val title = titleInput.text.toString()
                val description = descriptionInput.text.toString()
                val selectedType = typeSpinner.selectedItem

                if (selectedType != null) {
                    val type = ItemType.valueOf(selectedType.toString())
                    val parentId = selectedParentId ?: 0L

                    dbHelper.insertTask(parentId, type, title, description)
                    selectedParentId = null

                    val tasks = dbHelper.getAllTasks()
                    taskTree = buildTaskTree(tasks)
                    val displayList = flattenTaskTree(taskTree)
                    adapter.updateData(displayList)
                } else {
                    Toast.makeText(this, "Please select a task type.", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }


    private fun reloadTasks() {
        val tasks = dbHelper.getAllTasks()
        taskTree = buildTaskTree(tasks)
        val displayList = flattenTaskTree(taskTree)
        adapter.updateData(displayList)
    }

    private fun buildTaskTree(tasks: List<TaskItem>): List<TaskNode> {
        val map = tasks.associateBy { it.id }.mapValues { TaskNode(it.value) }.toMutableMap()
        val roots = mutableListOf<TaskNode>()
        map.values.forEach { node ->
            val parent = node.task.parentId?.let { map[it] }
            if (parent != null) parent.children.add(node)
            else roots.add(node)
        }
        return roots
    }

    private fun flattenTaskTree(nodes: List<TaskNode>, depth: Int = 0): List<DisplayTask> {
        val result = mutableListOf<DisplayTask>()
        for (node in nodes) {
            result.add(DisplayTask(node.task, depth, node.expanded))
            if (node.expanded) {
                result.addAll(flattenTaskTree(node.children, depth + 1))
            }
        }
        return result
    }
}