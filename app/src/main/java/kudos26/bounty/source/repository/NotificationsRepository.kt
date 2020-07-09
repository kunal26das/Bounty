package kudos26.bounty.source.repository

import kudos26.bounty.source.model.Payload
import kudos26.bounty.source.service.NotificationService
import retrofit2.Retrofit

/**
 * Created by kunal on 10-02-2020.
 */

class NotificationsRepository(retrofit: Retrofit) {
    private val notificationService = retrofit.create(NotificationService::class.java)
    fun notify(payload: Payload) = notificationService.notify(payload)
}