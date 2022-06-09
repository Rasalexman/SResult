package com.rasalexman.sresult.data.dto

class SResultUtils {
    companion object {
        fun empty(): ISResult = SResult.Empty
        fun loading(): ISResult = SResult.Loading()
        fun error(message: String, code: Int): ISResult = SResult.AbstractFailure.Error(message, code)
        fun<T: Any> success(data: T): ISResult = SResult.Success(data)
    }
}