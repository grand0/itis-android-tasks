package ru.kpfu.itis.ponomarev.androidcourse.data

import android.content.Context
import dagger.hilt.android.qualifiers.ActivityContext
import dagger.hilt.android.qualifiers.ApplicationContext
import retrofit2.HttpException
import ru.kpfu.itis.ponomarev.androidcourse.R
import ru.kpfu.itis.ponomarev.androidcourse.data.exception.BadRequestException
import ru.kpfu.itis.ponomarev.androidcourse.data.exception.CityNotFoundException
import ru.kpfu.itis.ponomarev.androidcourse.data.exception.ServerErrorException
import ru.kpfu.itis.ponomarev.androidcourse.data.exception.TooManyRequestsException
import ru.kpfu.itis.ponomarev.androidcourse.data.exception.UnauthorizedException
import javax.inject.Inject

class ExceptionHandlerDelegate @Inject constructor(
    @ApplicationContext private val context: Context,
) {

    fun handleException(ex: Exception): Exception {
        return when (ex) {
            is HttpException -> {
                when (ex.code()) {
                    400 -> BadRequestException(context.getString(R.string.bad_request_error))
                    401 -> UnauthorizedException(context.getString(R.string.bad_request_error))
                    404 -> CityNotFoundException(context.getString(R.string.city_not_found_error))
                    429 -> TooManyRequestsException(context.getString(R.string.too_many_requests_error))
                    in 500..599 -> ServerErrorException(context.getString(R.string.server_error))
                    else -> ex
                }
            }
            else -> ex
        }
    }
}
