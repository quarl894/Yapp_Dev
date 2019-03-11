package yapp.dev_diary.api

import io.reactivex.Single
import retrofit2.http.GET
import retrofit2.http.Query

interface Api {
    @GET()
    fun sttfun(@Query("id") id : String): Single<SttText>
}