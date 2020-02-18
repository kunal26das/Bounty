/*
 * Copyright (c) 2020.
 */
package kudos26.bounty

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

/**
 * Created by kunal on 17-12-2019.
 */

class MessagingService : FirebaseMessagingService() {

    lateinit var sharedPreferences: SharedPreferences

    override fun onCreate() {
        super.onCreate()
        sharedPreferences = getSharedPreferences(packageName, Context.MODE_PRIVATE)!!
    }

    override fun onNewToken(token: String) {
        with(sharedPreferences.edit()) {
            putString(getString(R.string.firebase_instance_id), token)
            commit()
        }
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        remoteMessage.data.isNotEmpty().let {
            Log.d("FCM", "Message data payload: ${remoteMessage.data}")
        }
        remoteMessage.notification?.let {
            Log.d("FCM", "Message Notification Body: ${it.body}")
        }
    }

}