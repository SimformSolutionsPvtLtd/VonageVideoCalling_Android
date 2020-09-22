package com.simform.videocalldemo.firebase

import android.content.Intent
import com.simform.videocalldemo.videocall.activity.IncomingCallScreenActivity
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

// Currently this class is not in use. It will be used in future scope
class MyMessagingService : FirebaseMessagingService() {

    override fun onNewToken(token: String) {
        super.onNewToken(token)
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)
        val intent = Intent(applicationContext, IncomingCallScreenActivity::class.java)
        intent.apply {
            putExtra(SESSION_KEY, remoteMessage.data[SESSION_KEY])
            putExtra(TOKEN_KEY, remoteMessage.data[TOKEN_KEY])
            putExtra(CALLER_NAME_KEY, remoteMessage.data[CALLER_NAME_KEY])
            putExtra(CALLER_RATING_KEY, remoteMessage.data[CALLER_RATING_KEY])
        }
        startActivity(intent)
    }

    companion object {
        const val SESSION_KEY = "sessionId"
        const val TOKEN_KEY = "token"
        const val CALLER_NAME_KEY = "caller_name"
        const val CALLER_RATING_KEY = "caller_rating"
    }
}