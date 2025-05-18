package com.ain.blankcanvas

data class TaskNode(
    val task: TaskItem,
    val children: MutableList<TaskNode> = mutableListOf(),
    var expanded: Boolean = false
)