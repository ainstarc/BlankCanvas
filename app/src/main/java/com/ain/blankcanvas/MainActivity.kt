package com.ain.blankcanvas

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.EditText
import android.widget.Spinner
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.ain.blankcanvas.TaskDatabaseHelper.Companion.COLUMN_ID
import com.ain.blankcanvas.TaskDatabaseHelper.Companion.TABLE_NAME
import com.google.android.material.floatingactionbutton.FloatingActionButton

class MainActivity : AppCompatActivity() {

    lateinit var taskTree: List<TaskNode>
    lateinit var adapter: TaskAdapter
    private lateinit var dbHelper: TaskDatabaseHelper
    private var selectedParentId: Long? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.hide()
        setContentView(R.layout.activity_main)

        dbHelper = TaskDatabaseHelper(this)

        // Load tasks from DB
        val tasks = dbHelper.getAllTasks()
        taskTree = buildTaskTree(tasks)
        val displayList = flattenTaskTree(taskTree)

        val recyclerView = findViewById<RecyclerView>(R.id.taskRecyclerView)
        adapter = TaskAdapter(
            tasks = taskTree,
            items = displayList,
            onItemToggle = {
                val updatedList = flattenTaskTree(taskTree)
                adapter.updateData(updatedList)
            },
            onTaskLongClick = { task ->
                // Show dialog with Add Child or Delete option
                val options = arrayOf("Add Child Task", "Delete Task")
                AlertDialog.Builder(this)
                    .setTitle("Select Action")
                    .setItems(options) { dialog, which ->
                        when (which) {
                            0 -> { // Add child
                                selectedParentId = task.id
                                showAddTaskDialog()
                            }

                            1 -> { // Delete task
                                dbHelper.deleteTask(task.id)
                                // Refresh UI
                                val tasks = dbHelper.getAllTasks()
                                taskTree = buildTaskTree(tasks)
                                val displayList = flattenTaskTree(taskTree)
                                adapter.updateData(displayList)
                            }
                        }
                    }
                    .show()
            }
        )



        recyclerView.adapter = adapter

        val fab = findViewById<FloatingActionButton>(R.id.addTaskFab)
        fab.setOnClickListener {
            showAddTaskDialog()
        }

    }


    private fun buildTaskTree(tasks: List<TaskItem>): List<TaskNode> {
        val nodeMap = mutableMapOf<Long, TaskNode>()
        val roots = mutableListOf<TaskNode>()

        // Create nodes for all tasks
        for (task in tasks) {
            nodeMap[task.id] = TaskNode(task)
        }

        // Link children to parents
        for (task in tasks) {
            val node = nodeMap[task.id]!!
            val parentId = task.parentId
            if (parentId == null) {
                // Root node
                roots.add(node)
            } else {
                nodeMap[parentId]?.children?.add(node)
            }
        }
        return roots
    }

    private fun showAddTaskDialog() {
        val dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_add_task, null)
        val titleInput = dialogView.findViewById<EditText>(R.id.taskTitleInput)
        val typeSpinner = dialogView.findViewById<Spinner>(R.id.taskTypeSpinner)
        val descriptionInput = dialogView.findViewById<EditText>(R.id.taskDescriptionInput)
        val description = descriptionInput.text.toString()


        val dialog = AlertDialog.Builder(this)
            .setTitle("Add Task")
            .setView(dialogView)
            .setPositiveButton("Add") { _, _ ->
                val title = titleInput.text.toString()
                val description =
                    descriptionInput?.text.toString()  // if you have description input
                val type = ItemType.valueOf(typeSpinner.selectedItem.toString())

                val parentId = selectedParentId ?: 0L  // Use selectedParentId or 0 if null

                dbHelper.insertTask(parentId, type, title, description)

                // Reset parent selection after adding task
                selectedParentId = null

                // Refresh UI
                val tasks = dbHelper.getAllTasks()
                taskTree = buildTaskTree(tasks)
                val displayList = flattenTaskTree(taskTree)
                adapter.updateData(displayList)
            }

            .setNegativeButton("Cancel", null)
            .create()

        dialog.show()
    }


}