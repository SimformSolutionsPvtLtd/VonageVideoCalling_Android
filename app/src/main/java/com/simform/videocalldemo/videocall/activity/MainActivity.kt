package com.simform.videocalldemo.videocall.activity

import android.Manifest
import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PictureInPictureParams
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.net.Uri
import android.opengl.GLSurfaceView
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.view.WindowManager
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.MutableLiveData
import com.simform.videocalldemo.BuildConfig
import com.simform.videocalldemo.R
import com.simform.videocalldemo.databinding.ActivityMainBinding
import com.simform.videocalldemo.utils.DialogUtils
import com.simform.videocalldemo.utils.PermissionUtils
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.opentok.android.*
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*


class MainActivity : AppCompatActivity(), PublisherKit.PublisherListener, Session.SessionListener,
    View.OnTouchListener {
    private var mSession: Session? = null
    private var mPublisher: Publisher? = null
    private var mSubscriber: Subscriber? = null
    private var audioEnabled = true
    private var mSessionId: String? = null
    private var mToken: String? = null
    private var videoEnabled = true
    private var connectionHasUserTimer: Timer? = null
    private var startTime: Long? = null
    var callTime = MutableLiveData(0)
    private var publisherX: Float? = null
    private var publisherY: Float? = null
    private var time: Timer? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding: ActivityMainBinding =
            DataBindingUtil.setContentView(this, R.layout.activity_main)
        binding.lifecycleOwner = this
        binding.activity = this

        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createNotificationChannel()
        }

        val incomingCallIntent = intent
        incomingCallIntent?.let {
            mSessionId = it.getStringExtra(SESSION_KEY)
            mToken = it.getStringExtra(TOKEN_KEY)
        }
        frame_layout_publisher_container.setOnTouchListener(this)
        requestPermission()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            RC_VIDEO_PERM -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED &&
                    grantResults[1] == PackageManager.PERMISSION_GRANTED
                ) {
                    createSession()
                } else {
                    if (PermissionUtils.canShowMessage(
                            perms[0],
                            this
                        ) || PermissionUtils.canShowMessage(perms[1], this)
                    ) {
                        AlertDialog.Builder(this).apply {
                            setMessage(getString(R.string.msg_audio_video_needed))
                            setCancelable(false)
                            setPositiveButton(getString(R.string.dialog_ok)) { _, _ -> requestPermission() }
                        }.create().apply {
                            setTitle(getString(R.string.title_permission_dialog))
                        }.show()
                    } else {
                        AlertDialog.Builder(this).apply {
                            setMessage(getString(R.string.msg_goto_setting_intent))
                            setCancelable(false)
                            setPositiveButton(getString(R.string.dialog_ok)) { _, _ ->
                                startActivity(getSettingIntent())
                                finish()
                            }
                            setNegativeButton(getString(R.string.dialog_deny)) { _, _ -> finish() }
                        }.create().apply {
                            setTitle(getString(R.string.title_permission_dialog))
                        }.show()
                    }
                }
            }
        }
    }

    private fun getSettingIntent(): Intent {
        val applicationSettingIntent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
        applicationSettingIntent.apply {
            data = Uri.fromParts("package", packageName, null)
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }
        return applicationSettingIntent
    }

    private fun requestPermission() {
        if (PermissionUtils.checkHasPermission(
                perms[0],
                this
            ) && PermissionUtils.checkHasPermission(perms[1], this)
        ) {
            createSession()
        } else {
            PermissionUtils.askPermission(this, perms, RC_VIDEO_PERM)
        }
    }


    private fun createSession() {
        mSession = Session.Builder(this, API_KEY, SESSION_ID).build()
        mSession?.setSessionListener(this)
        mSession?.connect(TOKEN)
    }

    override fun onStreamCreated(publisherKit: PublisherKit?, stream: Stream?) {
        Log.i(LOG_TAG, getString(R.string.msg_stream_created))
    }

    override fun onError(publisherKit: PublisherKit?, opentokError: OpentokError?) {
        Log.i(LOG_TAG, getString(R.string.msg_publisher_error) + " " + { opentokError?.message })
        DialogUtils.showDialog(this, opentokError?.message!!,
            DialogInterface.OnClickListener { dialogInterface, _ -> dialogInterface.dismiss() })
    }

    override fun onStreamDestroyed(publisherKit: PublisherKit?, stream: Stream?) {
        Log.i(LOG_TAG, getString(R.string.msg_stream_destroyed))
    }

    override fun onStop() {
        super.onStop()
        mSession?.disconnect()
        mSubscriber = null
        mPublisher = null
    }

    companion object {
        const val RC_VIDEO_PERM = 1
        val perms = arrayOf(
            Manifest.permission.CAMERA,
            Manifest.permission.RECORD_AUDIO,
            Manifest.permission.INTERNET
        )
        const val SESSION_ID = BuildConfig.SESSION_ID
        const val TOKEN = BuildConfig.TOKEN
        const val API_KEY = BuildConfig.API_KEY
        val LOG_TAG = MainActivity::class.simpleName
        const val SESSION_KEY = "sessionId"
        const val TOKEN_KEY = "token"
    }

    fun onClickImpl(view: View) {
        when (view.id) {
            R.id.button_call_end -> {
                Toast.makeText(this, getString(R.string.msg_call_end), Toast.LENGTH_LONG).show()
                if (mSession != null) {
                    mPublisher?.destroy()
                    mSubscriber?.destroy()
                    mSession?.disconnect()
                    mPublisher = null
                    mSubscriber = null
                    mSession = null
                    frame_layout_publisher_container.removeAllViews()
                    frame_layout_subscriber_container.removeAllViews()
                    finish()
                }
            }
            R.id.button_mute_audio -> {
                Toast.makeText(this, audioEnabled.toString(), Toast.LENGTH_LONG).show()
                if (audioEnabled) {
                    (view as FloatingActionButton).setImageDrawable(
                        ContextCompat.getDrawable(
                            this,
                            R.drawable.ic_outline_mic_off_24
                        )
                    )
                    audioEnabled = false
                    mPublisher?.publishAudio = false
                } else {
                    (view as FloatingActionButton).setImageDrawable(
                        ContextCompat.getDrawable(
                            this,
                            R.drawable.ic_outline_mic_none_24
                        )
                    )
                    audioEnabled = true
                    mPublisher?.publishAudio = true
                }
            }
            R.id.button_video_off -> {
                Toast.makeText(this, videoEnabled.toString(), Toast.LENGTH_LONG).show()
                if (videoEnabled) {
                    (view as FloatingActionButton).setImageDrawable(
                        ContextCompat.getDrawable(
                            this,
                            R.drawable.ic_outline_videocam_off_24
                        )
                    )
                    videoEnabled = false
                    mPublisher?.publishVideo = false
                    frame_layout_publisher_container.visibility = View.GONE
                } else {
                    (view as FloatingActionButton).setImageDrawable(
                        ContextCompat.getDrawable(
                            this,
                            R.drawable.ic_outline_videocam_24
                        )
                    )
                    videoEnabled = true
                    mPublisher?.publishVideo = true
                    frame_layout_publisher_container.visibility = View.VISIBLE
                }
            }
        }
    }

    override fun onUserLeaveHint() {
        super.onUserLeaveHint()
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.O) {
            enterPictureInPictureMode(PictureInPictureParams.Builder().build())
        }
    }

    @SuppressLint("RestrictedApi")
    override fun onPictureInPictureModeChanged(
        isInPictureInPictureMode: Boolean,
        newConfig: Configuration?
    ) {
        super.onPictureInPictureModeChanged(isInPictureInPictureMode, newConfig)
        if (isInPictureInPictureMode) {
            frame_layout_publisher_container.visibility = View.GONE
            hideCallUI()
        } else {
            frame_layout_publisher_container.visibility = View.VISIBLE
            showCallUI()
        }
    }

    override fun onConnected(session: Session?) {
        Log.i(LOG_TAG, getString(R.string.msg_session_created))
        mPublisher = Publisher.Builder(this@MainActivity).build()
        mPublisher?.setPublisherListener(this@MainActivity)
        mPublisher?.renderer?.setStyle(
            BaseVideoRenderer.STYLE_VIDEO_SCALE,
            BaseVideoRenderer.STYLE_VIDEO_FILL
        )
        frame_layout_publisher_container.addView(mPublisher?.view)
        if (mPublisher?.view is GLSurfaceView) {
            (mPublisher?.view as GLSurfaceView).setZOrderOnTop(true)
        }
        mSession?.publish(mPublisher)
    }

    override fun onDisconnected(session: Session?) {
        Log.i(LOG_TAG, getString(R.string.msg_session_disconnected))
    }

    override fun onError(session: Session?, opentokError: OpentokError?) {
        Log.i(LOG_TAG, getString(R.string.msg_session_error) + " " + opentokError?.message)
        DialogUtils.showDialog(this, opentokError?.message!!,
            DialogInterface.OnClickListener { dialogInterface, _ ->
                dialogInterface.dismiss()
                finish()
            })
    }

    override fun onStreamDropped(session: Session?, stream: Stream?) {
        if (mSubscriber != null) {
            mSubscriber = null
            frame_layout_subscriber_container.removeAllViews()
        }
    }

    override fun onStreamReceived(session: Session?, stream: Stream?) {
        Log.i(LOG_TAG, getString(R.string.msg_session_stream_received))
        if (mSubscriber == null) {
            mSubscriber = Subscriber.Builder(this@MainActivity, stream).build()
            mSession?.subscribe(mSubscriber)
            startTime = System.currentTimeMillis()
            startTimer()
            mSubscriber?.renderer?.setStyle(
                BaseVideoRenderer.STYLE_VIDEO_SCALE,
                BaseVideoRenderer.STYLE_VIDEO_FILL
            )
            frame_layout_subscriber_container.addView(mSubscriber?.view)
            connectionHasUserTimer?.cancel()
        }
    }

    private fun startTimer() {
        time = Timer()
        time?.schedule(object : TimerTask() {
            override fun run() {
                if (mSubscriber != null) {
                    runOnUiThread {
                        startTime?.let {
                            callTime.value = (System.currentTimeMillis() - it).toInt()
                        }
                    }
                } else {
                    cancel()
                }
            }
        }, 0, 1000)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun createNotificationChannel() {
        val channel = NotificationChannel(
            getString(R.string.notification_channel_id),
            getString(R.string.notification_channel_name), NotificationManager.IMPORTANCE_HIGH
        ).apply {
            description = getString(R.string.notification_channel_description)
        }
        val notificationManager: NotificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)
    }

    private fun showCallUI() {
        button_video_off.show()
        button_mute_audio.show()
        button_call_end.show()
        text_call_timer.visibility = View.VISIBLE
    }

    private fun hideCallUI() {
        button_video_off.hide()
        button_mute_audio.hide()
        button_call_end.hide()
        text_call_timer.visibility = View.GONE
    }

    override fun onTouch(view: View?, motionEvent: MotionEvent?): Boolean {
        when (motionEvent?.actionMasked) {
            MotionEvent.ACTION_MOVE -> {
                publisherX?.let { view?.x = motionEvent.rawX + it }
                publisherY?.let { view?.y = motionEvent.rawY + it }
            }
            MotionEvent.ACTION_DOWN -> {
                publisherX = view?.x?.minus(motionEvent.rawX)
                publisherY = view?.y?.minus(motionEvent.rawY)
            }
            MotionEvent.ACTION_UP -> {

            }
            else -> {
                return false
            }
        }
        return true
    }
}


