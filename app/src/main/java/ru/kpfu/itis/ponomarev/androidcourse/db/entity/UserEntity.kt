package ru.kpfu.itis.ponomarev.androidcourse.db.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "user",
    indices = [
        Index(value = ["phone"], unique = true),
        Index(value = ["email"], unique = true),
    ],
)
data class UserEntity(
    @PrimaryKey(autoGenerate = true)
    var id: Int = 0,
    var name: String,
    var phone: String,
    var email: String,
    var password: String,
    @ColumnInfo(name = "deletion_request_ts")
    var deletionRequestTimestamp: Long? = null,
)
