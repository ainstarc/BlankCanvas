package com.ain.blankcanvas

enum class ItemType {
    GROUP, LIST, TASK, SUBTASK
}

data class TaskItem(
    val id: Long = 0,
    val parentId: Long? = null,
    val type: ItemType,
    val title: String,
    val description: String? = null,
    val children: MutableList<TaskItem> = mutableListOf()
)
