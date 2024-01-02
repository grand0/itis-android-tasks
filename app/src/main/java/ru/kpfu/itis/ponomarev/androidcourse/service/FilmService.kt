package ru.kpfu.itis.ponomarev.androidcourse.service

import ru.kpfu.itis.ponomarev.androidcourse.db.AppDatabase
import ru.kpfu.itis.ponomarev.androidcourse.db.entity.FavoriteEntity
import ru.kpfu.itis.ponomarev.androidcourse.db.entity.FilmEntity
import ru.kpfu.itis.ponomarev.androidcourse.db.entity.RatingEntity
import ru.kpfu.itis.ponomarev.androidcourse.event.FilmAddedEventManager
import ru.kpfu.itis.ponomarev.androidcourse.model.FilmModel
import ru.kpfu.itis.ponomarev.androidcourse.session.AppSession
import java.lang.IllegalStateException

object FilmService {

    fun getAllFilms(): List<FilmModel> = AppDatabase.getInstance().filmDao().getAll().map(::toModel)

    fun checkFilmExists(film: FilmModel) =
        AppDatabase.getInstance().filmDao().getByTitleAndYear(film.title, film.year) != null

    fun addFilm(film: FilmModel) {
        val id = AppDatabase.getInstance().filmDao().saveFilm(toEntity(film)).toInt()
        film.id = id
        FilmAddedEventManager.notifyEvent(film)
    }

    fun getFilmRating(film: FilmModel): Double {
        return AppDatabase.getInstance().ratingDao().getFilmRating(film.id)
    }

    fun getRatingOfUser(filmId: Int): Int {
        val curUserId = AppSession.authorizedUser?.id
        return if (curUserId != null) AppDatabase.getInstance().ratingDao().getFilmRatingOfUser(
            filmId = filmId,
            userId = curUserId,
        ) else 0
    }

    fun rateFilm(filmId: Int, rating: Int) {
        val curUserId = AppSession.authorizedUser?.id
        if (curUserId != null) {
            AppDatabase.getInstance().ratingDao().rateFilm(
                RatingEntity(
                    filmId = filmId,
                    userId = curUserId,
                    rating = rating,
                ),
            )
        }
    }

    fun unrateFilm(filmId: Int) {
        val curUserId = AppSession.authorizedUser?.id
        if (curUserId != null) {
            AppDatabase.getInstance().ratingDao().removeRating(
                RatingEntity(
                    filmId = filmId,
                    userId = curUserId,
                ),
            )
        }
    }

    fun isFavorite(filmId: Int): Boolean {
        val curUserId = AppSession.authorizedUser?.id
        if (curUserId != null) {
            return AppDatabase.getInstance().favoriteDao().isFavorite(
                userId = curUserId,
                filmId = filmId,
            )
        }
        throw IllegalStateException("User is not authorized.")
    }

    fun markFavorite(filmId: Int) {
        val curUserId = AppSession.authorizedUser?.id
        if (curUserId != null) {
            AppDatabase.getInstance().favoriteDao().addFavorite(FavoriteEntity(
                filmId = filmId,
                userId = curUserId,
            ))
        }
    }

    fun unmarkFavorite(filmId: Int) {
        val curUserId = AppSession.authorizedUser?.id
        if (curUserId != null) {
            AppDatabase.getInstance().favoriteDao().removeFavorite(FavoriteEntity(
                filmId = filmId,
                userId = curUserId,
            ))
        }
    }

    fun deleteFilm(film: FilmModel) {
        AppDatabase.getInstance().filmDao().deleteFilm(toEntity(film))
    }

    private fun toModel(film: FilmEntity): FilmModel {
        val curUserId = AppSession.authorizedUser?.id
        val isFav = if (curUserId == null) false else AppDatabase.getInstance().favoriteDao().isFavorite(curUserId, film.id)
        return FilmModel(
            id = film.id,
            imageUrl = film.imageUrl,
            title = film.title,
            description = film.description,
            year = film.year,
            rating = AppDatabase.getInstance().ratingDao().getFilmRating(film.id),
            ratesCount = AppDatabase.getInstance().ratingDao().getFilmRatesCount(film.id),
            isFavorite = isFav,
        )
    }

    private fun toEntity(film: FilmModel) = FilmEntity(
        id = film.id,
        imageUrl = film.imageUrl,
        title = film.title,
        description = film.description,
        year = film.year,
    )
}
