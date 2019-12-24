package id.ac.ui.cs.mobileprogramming.anisha_inas.wacana.service

import android.app.IntentService
import android.content.Intent
import android.media.MediaPlayer
import android.os.IBinder
import androidx.annotation.Nullable
import id.ac.ui.cs.mobileprogramming.anisha_inas.wacana.R

/**
 * Created by Belal on 12/30/2016.
 */

class MusicService : IntentService("BackgroundMusic") {
    //creating a mediaplayer object
    private var player: MediaPlayer? = null

    @Nullable
    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        //getting systems default ringtone
        player = MediaPlayer.create(
            this,
            R.raw.clean
        )
        //setting loop play to true
        //this will make the ringtone continuously playing
        player?.isLooping = true

        //staring the player
        player?.start()

        //we have some options for service
        //start sticky means service will be explicity started and stopped
        return START_STICKY
    }


    override fun onDestroy() {
        super.onDestroy()
        //stopping the player when service is destroyed
        player?.stop()
    }

    override fun onHandleIntent(intent: Intent?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    fun pause() {
        player?.pause()
    }
}