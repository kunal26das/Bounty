package kudos26.bounty.koin

import com.facebook.stetho.okhttp3.StethoInterceptor
import com.google.gson.GsonBuilder
import kudos26.bounty.source.Bounty.BASE_URL_FCM
import kudos26.bounty.source.Bounty.CLOUD_MESSAGING_KEY
import kudos26.bounty.source.model.*
import kudos26.bounty.source.model.Group.Companion.groupDeserializer
import kudos26.bounty.source.model.Invitation.Companion.invitationDeserializer
import kudos26.bounty.source.model.Member.Companion.memberDeserializer
import kudos26.bounty.source.model.Share.Companion.shareDeserializer
import kudos26.bounty.source.model.Transaction.Companion.transactionDeserializer
import okhttp3.OkHttpClient
import okhttp3.Request
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory

/**
 * Created by kunal on 10-02-2020.
 */

val networking = module {
    single {
        GsonBuilder().apply {
            registerTypeAdapter(Group::class.java, groupDeserializer)
            registerTypeAdapter(Share::class.java, shareDeserializer)
            registerTypeAdapter(Member::class.java, memberDeserializer)
            registerTypeAdapter(Invitation::class.java, invitationDeserializer)
            registerTypeAdapter(Transaction::class.java, transactionDeserializer)
        }.create()
    }
    single {
        OkHttpClient.Builder().apply {
            addNetworkInterceptor(StethoInterceptor())
            addInterceptor { chain ->
                val original: Request = chain.request()
                val request: Request = original.newBuilder().apply {
                    header("Content-Type", "application/json")
                    header("Authorization", "key=$CLOUD_MESSAGING_KEY")
                    method(original.method(), original.body())
                }.build()
                chain.proceed(request)
            }
            retryOnConnectionFailure(true)
        }.build()
    }
    single {
        Retrofit.Builder().apply {
            baseUrl(BASE_URL_FCM)
            client(get())
            addConverterFactory(GsonConverterFactory.create(get()))
            addCallAdapterFactory(RxJava2CallAdapterFactory.create())
        }.build()
    }
}