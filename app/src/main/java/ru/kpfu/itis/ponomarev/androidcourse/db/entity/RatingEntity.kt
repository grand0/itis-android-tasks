package ru.kpfu.itis.ponomarev.androidcourse.db.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey

@Entity(
    tableName = "rating",
    primaryKeys = ["film_id", "user_id"],
    foreignKeys = [
        ForeignKey(
            entity = FilmEntity::class,
            parentColumns = ["id"],
            childColumns = ["film_id"],
            onDelete = ForeignKey.CASCADE,
        ),
        ForeignKey(
            entity = UserEntity::class,
            parentColumns = ["id"],
            childColumns = ["user_id"],
            onDelete = ForeignKey.CASCADE,
        ),
    ],
)
data class RatingEntity(
    @ColumnInfo(name = "film_id")
    val filmId: Int,
    @ColumnInfo(name = "user_id")
    val userId: Int,
    val rating: Int = 0,
)
