package kudos26.bounty.source.repository

import kudos26.bounty.source.model.Payload
import kudos26.bounty.source.service.FirebaseCloudMessagingService
import retrofit2.Retrofit

/**
 * Created by kunal on 10-02-2020.
 */

class FirebaseCloudMessagingRepository(retrofit: Retrofit) {
    private val firebaseCloudMessagingService = retrofit.create(FirebaseCloudMessagingService::class.java)
    fun sendNotification(payload: Payload) = firebaseCloudMessagingService.sendNotification(payload)
}