/*
 * Copyright (c) 2020.
*/
package kudos26.bounty

import android.app.NotificationManager
import android.app.PendingIntent
import android.os.Bundle
import androidx.core.app.NotificationCompat
import androidx.essentials.events.Events
import androidx.navigation.NavDeepLinkBuilder
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.google.gson.Gson
import kudos26.bounty.firebase.Extensions.archivedGroup
import kudos26.bounty.firebase.Extensions.currentMember
import kudos26.bounty.firebase.Extensions.date
import kudos26.bounty.firebase.Extensions.formerMember
import kudos26.bounty.firebase.Extensions.group
import kudos26.bounty.firebase.Extensions.invitation
import kudos26.bounty.firebase.Extensions.inviter
import kudos26.bounty.firebase.Extensions.leavingDate
import kudos26.bounty.firebase.Extensions.observeValue
import kudos26.bounty.firebase.Extensions.pendingInvite
import kudos26.bounty.firebase.Extensions.user
import kudos26.bounty.firebase.Firebase
import kudos26.bounty.source.model.Channel
import kudos26.bounty.source.model.Group
import kudos26.bounty.source.model.Invitation
import kudos26.bounty.source.model.Notification
import kudos26.bounty.ui.MainActivity
import kudos26.bounty.utils.CalendarUtils.currentDate
import kudos26.bounty.utils.Extensions.Try
import org.koin.android.ext.android.inject

/**
 * Created by kunal on 17-12-2019.
 * */

class MessagingService : FirebaseMessagingService() {

    private val dataMap = setOf(
            Pair(Group::class.java, R.id.archive),
            Pair(Invitation::class.java, R.id.invitations)
    )

    private val gson: Gson by inject()
    private val firebase: Firebase by inject()
    private val firebaseAuth: FirebaseAuth by inject()
    private val database: DatabaseReference by inject()
    private val notificationManager: NotificationManager by inject()
    override fun onNewToken(token: String) = firebase.onNewToken(token)

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        for (pair in dataMap) Try {
            when (val data = gson.fromJson(gson.toJson(remoteMessage.data), pair.first)) {
                // Group Invitation
                is Invitation -> {
                    database.group(data.id).pendingInvite(firebaseAuth.currentUser?.uid!!).setValue(currentDate)
                    database.user(firebaseAuth.currentUser?.uid!!).apply {
                        archivedGroup(data.id).removeValue()
                        invitation(data.id).let {
                            it.date.setValue(data.date)
                            it.inviter.setValue(data.uid)
                        }
                    }
                    Notification().apply {
                        title = getString(R.string.new_group_invitation)
                        body = getString(R.string.you_have_a_pending_group_invitation)
                        channel = Channel(R.string.invitations, getString(R.string.invitations))
                        notificationManager.notify(
                                channel.id,
                                notification(
                                        this,
                                        deepLink(pair.second, Bundle()),
                                        channel.name
                                )
                        )
                    }
                }
                // Group Removal
                is Group -> {
                    Events.publish(data)
                    firebaseAuth.currentUser?.uid?.let { uid ->
                        database.user(uid).group(data.id).removeValue()
                        database.group(data.id).currentMember(uid).removeValue()
                        database.user(uid).archivedGroup(data.id).setValue(currentDate)
                        database.group(data.id).formerMember(uid).leavingDate.setValue(currentDate)
                    }
                    database.group(data.id).observeValue({
                        // TODO Simplify
                        if (!it.child("Members").exists()
                                and !it.child("Formers").exists()
                                and !it.child("Pending").exists()
                        ) {
                            database.group(data.id).removeValue()
                        }
                    })
                    Notification().apply {
                        title = getString(R.string.group_removal)
                        body = "You have been removed from group - ${data.name}"
                        channel = Channel(R.string.group, getString(R.string.group))
                        notificationManager.notify(
                                channel.id,
                                notification(
                                        this,
                                        deepLink(pair.second, Bundle()),
                                        channel.name
                                )
                        )
                    }
                }
            }
        }
    }

    private fun notification(
            notification: Notification,
            intent: PendingIntent,
            channel: String
    ) = NotificationCompat.Builder(this, channel).apply {
        priority = NotificationCompat.PRIORITY_DEFAULT
        setSmallIcon(R.drawable.ic_beta)
        setContentTitle(notification.title)
        setContentText(notification.body)
        setContentIntent(intent)
        setAutoCancel(true)
    }.build()

    private fun deepLink(
            destination: Int,
            arguments: Bundle
    ) = NavDeepLinkBuilder(this).apply {
        setComponentName(MainActivity::class.java)
        setGraph(R.navigation.app_navigation)
        setDestination(destination)
        setArguments(arguments)
    }.createPendingIntent()

}