package com.writershub.app.data.repository

import com.writershub.app.data.model.User

object SessionManager {
    var currentUser: User? = null
        private set

    fun login(user: User) {
        currentUser = user
    }

    fun register(user: User) {
        currentUser = user
    }

    fun activateAccount() {
        currentUser = currentUser?.copy(isActivated = true)
    }

    fun logout() {
        currentUser = null
    }

    fun isUserActivated(): Boolean {
        return currentUser?.isActivated == true
    }
}