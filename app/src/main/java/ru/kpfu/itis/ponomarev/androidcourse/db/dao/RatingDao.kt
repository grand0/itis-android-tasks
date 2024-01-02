package ru.kpfu.itis.ponomarev.androidcourse.db.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import ru.kpfu.itis.ponomarev.androidcourse.db.entity.RatingEntity

@Dao
interface RatingDao {

    @Query("SELECT AVG(rating.rating) FROM rating JOIN film ON rating.film_id = film.id WHERE film.id = :filmId")
    fun getFilmRating(filmId: Int): Double

    @Query("SELECT COUNT(rating.rating) FROM rating JOIN film ON rating.film_id = film.id WHERE film.id = :filmId")
    fun getFilmRatesCount(filmId: Int): Int

    @Query("SELECT rating.rating FROM rating WHERE rating.film_id = :filmId AND rating.user_id = :userId")
    fun getFilmRatingOfUser(filmId: Int, userId: Int): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun rateFilm(rating: RatingEntity)

    @Delete
    fun removeRating(rating: RatingEntity)
}
