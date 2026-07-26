package com.cinemateca.repository

import com.cinemateca.domain.Error
import com.cinemateca.domain.Failure
import com.cinemateca.domain.Result
import java.net.ConnectException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

internal const val connectErrorCode = 166
internal const val generalErrorCode = 266

suspend fun <T : Any> fetchData(dataProvider: suspend () -> Result<T>): Result<T> {
    return withContext(Dispatchers.IO) {
        try {
            dataProvider()
        } catch (e: ConnectException) {
            e.printStackTrace()
            Failure(
                Error(code = connectErrorCode),
            )
        } catch (e: Exception) {
            e.printStackTrace()
            Failure(
                Error(code = generalErrorCode),
            )
        }
    }
}
