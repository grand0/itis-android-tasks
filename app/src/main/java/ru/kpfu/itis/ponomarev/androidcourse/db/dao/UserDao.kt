package ru.kpfu.itis.ponomarev.androidcourse.db.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import ru.kpfu.itis.ponomarev.androidcourse.db.entity.UserEntity

@Dao
interface UserDao {

    @Query("SELECT * FROM user WHERE email = :email")
    fun getUserByEmail(email: String): UserEntity?

    @Query("SELECT * FROM user WHERE phone = :phone")
    fun getUserByPhone(phone: String): UserEntity?

    @Query("UPDATE user SET phone = :phone WHERE id = :userId")
    fun updatePhone(userId: Int, phone: String)

    @Query("UPDATE user SET password = :password WHERE id = :userId")
    fun updatePassword(userId: Int, password: String)

    @Query("UPDATE user SET deletion_request_ts = :timestamp WHERE id = :userId")
    fun requestDelete(userId: Int, timestamp: Long)

    @Query("UPDATE user SET deletion_request_ts = null WHERE id = :userId")
    fun cancelDeleteRequest(userId: Int)

    @Delete
    fun deleteUser(user: UserEntity)

    @Insert
    fun saveUser(user: UserEntity)
}
