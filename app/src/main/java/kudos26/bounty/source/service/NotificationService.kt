package kudos26.bounty.source.service

import io.reactivex.rxjava3.core.Single
import kudos26.bounty.source.model.Payload
import retrofit2.http.Body
import retrofit2.http.POST

/**
 * Created by kunal on 10-02-2020.
 */

interface NotificationService {

    @POST("fcm/send")
    fun notify(@Body payload: Payload): Single<Any?>
}