package com.writershub.app.data.model

data class Announcement(
    val id: String = "",
    val title: String = "",
    val message: String = "",
    val isActive: Boolean = true,
    val isPinned: Boolean = false,
    val createdAt: Long = System.currentTimeMillis(),
    val createdBy: String = "",
    val link: String? = null
)