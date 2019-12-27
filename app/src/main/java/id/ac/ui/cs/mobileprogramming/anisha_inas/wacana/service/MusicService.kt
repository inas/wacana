package id.ac.ui.cs.mobileprogramming.anisha_inas.wacana.service

import android.app.Service
import android.content.Intent
import android.media.MediaPlayer
import android.os.IBinder
import androidx.annotation.Nullable
import id.ac.ui.cs.mobileprogramming.anisha_inas.wacana.R

/**
 * Created by Belal on 12/30/2016.
 */

class MusicService : Service(), MediaPlayer.OnPreparedListener {

    companion object {
        const val ACTION_PLAY: String = "ACTION_PLAY"
        const val ACTION_PAUSE: String = "ACTION_PAUSE"
        private const val STATE_PAUSED: String = "STATE_PAUSED"
        private const val STATE_PLAYING: String = "STATE_PLAYING"
        private const val STATE_NOT_STARTED: String = "STATE_NOT_STARTED"
    }

    private var mPlayer: MediaPlayer? = null
    private var mState: String = STATE_NOT_STARTED
    private var mCurrentPosition: Int = 0

    @Nullable
    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            ACTION_PLAY -> {
                if (mState == STATE_NOT_STARTED) {
                    mPlayer = MediaPlayer.create(this, R.raw.clean).apply {
                        setOnPreparedListener(this@MusicService)
                    }
                } else {
                    mPlayer?.let {
                        it.seekTo(mCurrentPosition)
                        it.start()
                        mState = STATE_PLAYING
                    }
                }
            }
            ACTION_PAUSE -> {
                mPlayer?.let {
                    mCurrentPosition = it.currentPosition
                    it.pause()
                    mState = STATE_PAUSED
                }
            }
        }

        return START_STICKY
    }


    override fun onDestroy() {
        super.onDestroy()
        mPlayer?.stop()
        mPlayer?.release()
    }

    override fun onPrepared(mp: MediaPlayer?) {
        mPlayer?.start()
        mPlayer?.isLooping = true
        mState = STATE_PLAYING
    }

}