package app.bankconvert.retrofit.network

import app.bankconvert.retrofit.model.DataEntity
import io.reactivex.Observable
import io.reactivex.Single
import retrofit2.http.GET
import retrofit2.http.Url

public interface RequestInterface {
    @GET
    fun getData(@Url url: String) : Single<DataEntity>
}