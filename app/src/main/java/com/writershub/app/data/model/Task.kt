package com.writershub.app.data.model

enum class TaskType {
    DAILY, PREMIUM, VIDEO
}

enum class TaskDifficulty {
    EASY, MEDIUM, HARD
}

data class Task(
    val id: String = "",
    val title: String = "",
    val description: String = "",
    val reward: Double = 0.0,
    val type: TaskType = TaskType.DAILY,
    val difficulty: TaskDifficulty = TaskDifficulty.EASY,
    val timeInMinutes: Int = 5,
    val icon: String = "📝",
    val isCompleted: Boolean = false
)