package com.ain.blankcanvas

enum class ItemType {
    GROUP, TASK
}

data class TaskItem(
    val id: Long,
    val parentId: Long?,
    val type: ItemType,
    val title: String,
    val description: String?
)