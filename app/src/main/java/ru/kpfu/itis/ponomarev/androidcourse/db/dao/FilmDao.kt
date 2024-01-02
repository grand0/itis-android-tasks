package ru.kpfu.itis.ponomarev.androidcourse.db.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import ru.kpfu.itis.ponomarev.androidcourse.db.entity.FilmEntity

@Dao
interface FilmDao {

    @Query("SELECT * FROM film ORDER BY year DESC")
    fun getAll(): List<FilmEntity>

    @Query("SELECT * FROM film WHERE title = :title AND year = :year")
    fun getByTitleAndYear(title: String, year: Int): FilmEntity?

    @Insert
    fun saveFilm(film: FilmEntity): Long

    @Delete
    fun deleteFilm(film: FilmEntity)
}
