package com.writershub.app.data.repository

import com.writershub.app.data.model.Task
import com.writershub.app.data.model.TaskType
import com.writershub.app.data.model.TaskDifficulty

object TaskRepository {

    // Daily Tasks
    private val dailyTasks = listOf(
        Task(
            id = "1",
            title = "Write a product description",
            description = "Write a 50-word description for a new smartphone",
            reward = 4.0,
            type = TaskType.DAILY,
            difficulty = TaskDifficulty.EASY,
            timeInMinutes = 5,
            icon = "📱"
        ),
        Task(
            id = "2",
            title = "Kenya Trivia Quiz",
            description = "Answer 5 questions about Kenyan history",
            reward = 5.0,
            type = TaskType.DAILY,
            difficulty = TaskDifficulty.EASY,
            timeInMinutes = 3,
            icon = "🇰🇪"
        ),
        Task(
            id = "3",
            title = "Shopping Survey",
            description = "Complete a 2-minute survey about online shopping",
            reward = 8.0,
            type = TaskType.DAILY,
            difficulty = TaskDifficulty.EASY,
            timeInMinutes = 2,
            icon = "🛒"
        ),
        Task(
            id = "4",
            title = "Translate English to Swahili",
            description = "Translate 5 simple sentences",
            reward = 6.0,
            type = TaskType.DAILY,
            difficulty = TaskDifficulty.MEDIUM,
            timeInMinutes = 4,
            icon = "🌍"
        )
    )

    // Short Videos Tasks
    private val videoTasks = listOf(
        Task(
            id = "5",
            title = "Watch Ad - 30 seconds",
            description = "Watch a short advertisement",
            reward = 2.0,
            type = TaskType.VIDEO,
            difficulty = TaskDifficulty.EASY,
            timeInMinutes = 1,
            icon = "📺"
        ),
        Task(
            id = "6",
            title = "Product Review Video",
            description = "Watch and rate a product review",
            reward = 3.0,
            type = TaskType.VIDEO,
            difficulty = TaskDifficulty.EASY,
            timeInMinutes = 2,
            icon = "🎬"
        ),
        Task(
            id = "7",
            title = "Tutorial Video",
            description = "Watch a 2-minute tutorial and answer 1 question",
            reward = 4.0,
            type = TaskType.VIDEO,
            difficulty = TaskDifficulty.MEDIUM,
            timeInMinutes = 3,
            icon = "📚"
        )
    )

    // Premium Tasks (require activation)
    private val premiumTasks = listOf(
        Task(
            id = "8",
            title = "Write Blog Post",
            description = "Write a 300-word blog about technology",
            reward = 50.0,
            type = TaskType.PREMIUM,
            difficulty = TaskDifficulty.MEDIUM,
            timeInMinutes = 30,
            icon = "✍️"
        ),
        Task(
            id = "9",
            title = "Video Script Writing",
            description = "Write a 2-minute YouTube video script",
            reward = 35.0,
            type = TaskType.PREMIUM,
            difficulty = TaskDifficulty.MEDIUM,
            timeInMinutes = 20,
            icon = "🎥"
        ),
        Task(
            id = "10",
            title = "Advanced Trivia",
            description = "Answer 10 challenging science questions",
            reward = 25.0,
            type = TaskType.PREMIUM,
            difficulty = TaskDifficulty.HARD,
            timeInMinutes = 15,
            icon = "🔬"
        ),
        Task(
            id = "11",
            title = "Website Content Writing",
            description = "Write 5 product descriptions for an e-commerce site",
            reward = 75.0,
            type = TaskType.PREMIUM,
            difficulty = TaskDifficulty.HARD,
            timeInMinutes = 45,
            icon = "💻"
        )
    )

    fun getDailyTasks(): List<Task> = dailyTasks

    fun getVideoTasks(): List<Task> = videoTasks

    fun getPremiumTasks(): List<Task> = premiumTasks

    fun getTaskById(id: String): Task? {
        return (dailyTasks + videoTasks + premiumTasks).find { it.id == id }
    }
}