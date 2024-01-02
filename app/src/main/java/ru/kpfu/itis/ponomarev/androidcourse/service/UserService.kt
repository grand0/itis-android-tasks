package ru.kpfu.itis.ponomarev.androidcourse.service

import ru.kpfu.itis.ponomarev.androidcourse.config.AppConfig.USER_DELETION_GRACE_PERIOD_MS
import ru.kpfu.itis.ponomarev.androidcourse.db.AppDatabase
import ru.kpfu.itis.ponomarev.androidcourse.db.entity.UserEntity
import ru.kpfu.itis.ponomarev.androidcourse.model.UserModel
import ru.kpfu.itis.ponomarev.androidcourse.util.PasswordUtil

object UserService {

    fun checkEmailUnique(email: String): Boolean =
        AppDatabase.getInstance().userDao().getUserByEmail(email) == null

    fun checkPhoneUnique(phone: String): Boolean =
        AppDatabase.getInstance().userDao().getUserByPhone(phone) == null

    fun getUserByCredentials(email: String, password: String): UserModel? {
        val encPassword = PasswordUtil.encrypt(password)
        val user = getUserEntityByRawCredentials(email, encPassword)
        return toModel(user)
    }

    fun getUserByRawCredentials(email: String, passwordHash: String): UserModel? {
        val user = getUserEntityByRawCredentials(email, passwordHash)
        if (user?.deletionRequestTimestamp != null) {
            if (System.currentTimeMillis() - user.deletionRequestTimestamp!! >= USER_DELETION_GRACE_PERIOD_MS) {
                AppDatabase.getInstance().userDao().deleteUser(user)
                return null
            }
        }
        return toModel(user)
    }

    private fun getUserEntityByRawCredentials(email: String, passwordHash: String): UserEntity? {
        val user = AppDatabase.getInstance().userDao().getUserByEmail(email)
        return if (user?.password == passwordHash) user
        else null
    }

    fun saveUser(user: UserModel, password: String) {
        val encPass = PasswordUtil.encrypt(password)
        user.passwordHash = encPass
        val ent = toEntity(user)
        AppDatabase.getInstance().userDao().saveUser(ent)
    }

    fun updatePhone(user: UserModel, phone: String) {
        AppDatabase.getInstance().userDao().updatePhone(user.id, phone)
        user.phone = phone
    }

    fun checkPassword(user: UserModel, password: String): Boolean {
        val encPass = PasswordUtil.encrypt(password)
        return user.passwordHash == encPass
    }

    fun updatePassword(user: UserModel, password: String) {
        val encPass = PasswordUtil.encrypt(password)
        AppDatabase.getInstance().userDao().updatePassword(user.id, encPass)
        user.passwordHash = encPass
    }

    fun requestDelete(user: UserModel) {
        AppDatabase.getInstance().userDao().requestDelete(user.id, System.currentTimeMillis())
    }

    fun cancelDeleteRequest(user: UserModel) {
        AppDatabase.getInstance().userDao().cancelDeleteRequest(user.id)
    }

    fun fullyDelete(user: UserModel) {
        AppDatabase.getInstance().userDao().deleteUser(toEntity(user))
    }

    private fun toEntity(user: UserModel) = UserEntity(
        id = user.id,
        name = user.name,
        phone = user.phone,
        email = user.email,
        password = user.passwordHash!!,
        deletionRequestTimestamp = user.deletionRequestTimestamp
    )

    private fun toModel(user: UserEntity?) =
        if (user == null)
            null
        else UserModel(
            id = user.id,
            name = user.name,
            phone = user.phone,
            email = user.email,
            passwordHash = user.password,
            deletionRequestTimestamp = user.deletionRequestTimestamp,
        )
}
