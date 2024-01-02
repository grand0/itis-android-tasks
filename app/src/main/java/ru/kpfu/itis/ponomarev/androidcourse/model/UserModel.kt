package ru.kpfu.itis.ponomarev.androidcourse.model

data class UserModel(
    var id: Int = 0,
    var name: String,
    var phone: String,
    var email: String,
    var passwordHash: String? = null,
    var deletionRequestTimestamp: Long? = null,
)
