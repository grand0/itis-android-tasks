package ru.kpfu.itis.ponomarev.androidcourse.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import ru.kpfu.itis.ponomarev.androidcourse.db.dao.FavoriteDao
import ru.kpfu.itis.ponomarev.androidcourse.db.dao.FilmDao
import ru.kpfu.itis.ponomarev.androidcourse.db.dao.RatingDao
import ru.kpfu.itis.ponomarev.androidcourse.db.dao.UserDao
import ru.kpfu.itis.ponomarev.androidcourse.db.entity.FavoriteEntity
import ru.kpfu.itis.ponomarev.androidcourse.db.entity.FilmEntity
import ru.kpfu.itis.ponomarev.androidcourse.db.entity.RatingEntity
import ru.kpfu.itis.ponomarev.androidcourse.db.entity.UserEntity

@Database(
    entities = [
        UserEntity::class,
        FilmEntity::class,
        FavoriteEntity::class,
        RatingEntity::class,
    ],
    version = 1,
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun filmDao(): FilmDao
    abstract fun favoriteDao(): FavoriteDao
    abstract fun ratingDao(): RatingDao

    companion object {
        private const val DB_NAME = "movies-db"

        private var database: AppDatabase? = null

        fun init(context: Context) {
            database = Room.databaseBuilder(
                context,
                AppDatabase::class.java,
                DB_NAME,
            )
                .fallbackToDestructiveMigration()
                .build()
        }

        fun getInstance() : AppDatabase {
            return database ?: throw IllegalStateException("Database was not initialized")
        }
    }
}
