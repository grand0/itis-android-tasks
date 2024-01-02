package ru.kpfu.itis.ponomarev.androidcourse.session

import ru.kpfu.itis.ponomarev.androidcourse.model.UserModel
import ru.kpfu.itis.ponomarev.androidcourse.service.UserService

object AppSession {

    var authorizedUser: UserModel? = null
        private set

    fun authorizeUser(email: String, passwordHash: String) {
        authorizedUser = UserService.getUserByRawCredentials(email, passwordHash)
    }

    fun clearAuthorizedUser() {
        authorizedUser = null
    }
}
