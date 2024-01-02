package ru.kpfu.itis.ponomarev.androidcourse.db.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "film",
    indices = [
        Index("title", "year", unique = true)
    ]
)
data class FilmEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int,
    @ColumnInfo(name = "image_url")
    val imageUrl: String,
    val title: String,
    val description: String,
    val year: Int,
)
