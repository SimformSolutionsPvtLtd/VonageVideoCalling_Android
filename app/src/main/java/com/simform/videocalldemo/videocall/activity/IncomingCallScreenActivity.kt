package com.simform.videocalldemo.videocall.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.simform.videocalldemo.R

// Currently this class is not in use. It will be used in future scope
class IncomingCallScreenActivity : AppCompatActivity() {
    private var sessionId: String? = null
    private var token: String? = null
    var name: String? = null
    var rating: String? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_incoming_call_screen)
        val i = intent
        i.extras?.let {
            sessionId = it.getString(SESSION_KEY)
            token = it.getString(TOKEN_KEY)
            name = it.getString(CALLER_NAME_KEY)
            rating = it.getString(CALLER_RATING_KEY)
        }
    }

    fun onclick(view: View) {
        when (view.id) {
            R.id.button_answer_call -> {
                val i = Intent(this, MainActivity::class.java)
                i.putExtra(SESSION_KEY, sessionId)
                i.putExtra(TOKEN_KEY, token)
                i.putExtra(CALLER_NAME_KEY, name)
                i.putExtra(CALLER_RATING_KEY, rating)
                startActivity(i)
            }
            R.id.button_decline_call -> {
                finish()
            }
        }
    }

    companion object {
        const val SESSION_KEY = "sessionId"
        const val TOKEN_KEY = "token"
        const val CALLER_NAME_KEY = "caller_name"
        const val CALLER_RATING_KEY = "caller_rating"

    }
}