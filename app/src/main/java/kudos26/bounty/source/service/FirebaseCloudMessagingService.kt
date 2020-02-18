package kudos26.bounty.source.service

import io.reactivex.Single
import kudos26.bounty.source.model.Payload
import retrofit2.http.Body
import retrofit2.http.POST

/**
 * Created by kunal on 10-02-2020.
 */

interface FirebaseCloudMessagingService {

    @POST("fcm/send")
    fun sendNotification(@Body payload: Payload): Single<Any?>
}