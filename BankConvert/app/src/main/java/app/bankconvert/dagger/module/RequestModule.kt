package app.bankconvert.dagger.module

import android.content.Context
import app.bankconvert.page.convert.ConvertActivity
import app.bankconvert.retrofit.network.RequestInterface
import dagger.Module
import dagger.Provides
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory

@Module(includes = [ContextModule::class])
@Suppress("unused")
class RequestModule {
    @Provides
    fun requestInterface(retrofit: Retrofit): RequestInterface {
        return retrofit.create(RequestInterface::class.java)
    }

    @Provides
    fun retrofit(url: String, rxJava2CallAdapterFactory: RxJava2CallAdapterFactory, gsonConverterFactory: GsonConverterFactory): Retrofit {
        return Retrofit.Builder()
            .baseUrl(url)
            .addCallAdapterFactory(rxJava2CallAdapterFactory)
            .addConverterFactory(gsonConverterFactory)
            .build()
    }

    @Provides
    fun myUrlFactory(context: Context): String {
        return (context as ConvertActivity).getUrl()
    }

    @Provides
    fun rxJava2CallAdapterFactory(): RxJava2CallAdapterFactory {
        return RxJava2CallAdapterFactory.create()
    }

    @Provides
    fun gsonConverterFactory(): GsonConverterFactory {
        return GsonConverterFactory.create()
    }
}