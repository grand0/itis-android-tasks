package ru.kpfu.itis.ponomarev.androidcourse.db.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import ru.kpfu.itis.ponomarev.androidcourse.db.entity.FilmEntity
import ru.kpfu.itis.ponomarev.androidcourse.db.entity.FavoriteEntity

@Dao
interface FavoriteDao {

    @Query("SELECT :filmId IN (SELECT fav.film_id FROM favorite fav WHERE fav.user_id == :userId)")
    fun isFavorite(userId: Int, filmId: Int): Boolean

    @Insert
    fun addFavorite(fav: FavoriteEntity)

    @Delete
    fun removeFavorite(fav: FavoriteEntity)
}
