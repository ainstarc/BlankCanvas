package com.ain.blankcanvas

data class DisplayTask(
    val task: TaskItem,
    val depth: Int,
    var expanded: Boolean
)