package kudos26.bounty.koin

import com.facebook.stetho.okhttp3.StethoInterceptor
import com.google.gson.GsonBuilder
import okhttp3.OkHttpClient
import okhttp3.Request
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory

/**
 * Created by kunal on 10-02-2020.
 */

private const val BASE_URL = "https://fcm.googleapis.com/"

val networking = module {
    single { GsonBuilder().create() }
    single {
        OkHttpClient.Builder().apply {
            networkInterceptors().add(StethoInterceptor())
            addInterceptor { chain ->
                val original: Request = chain.request()
                val request: Request = original.newBuilder().apply {
                    header("Content-Type", "application/json")
                    header("Authorization", "key=AAAAZ8TX-go:APA91bHaXdtojq6Cm43wTOoxKbPyUsyMQrNBoxA3wSKYYVAkCk063AD-6qLvhR4o3cpLnm1Ty1TltBdkk4J4izfm8GpgHymmokO5j4PDqxga8FXMBjKkZ7q9zRVceLvqEvYJx4y6Xkj_")
                    method(original.method(), original.body())
                }.build()
                chain.proceed(request)
            }
            retryOnConnectionFailure(true)
        }.build()
    }
    single {
        Retrofit.Builder().apply {
            baseUrl(BASE_URL)
            client(get())
            addConverterFactory(GsonConverterFactory.create(get()))
            addCallAdapterFactory(RxJava2CallAdapterFactory.create())
        }.build()
    }
}