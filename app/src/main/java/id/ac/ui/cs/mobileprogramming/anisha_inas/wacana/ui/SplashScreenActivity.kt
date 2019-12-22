package id.ac.ui.cs.mobileprogramming.anisha_inas.wacana.ui

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import androidx.appcompat.app.AppCompatActivity
import id.ac.ui.cs.mobileprogramming.anisha_inas.wacana.R
import id.ac.ui.cs.mobileprogramming.anisha_inas.wacana.ui.home.HomeActivity

class SplashScreenActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)
        Handler().postDelayed(Runnable {
            startActivity(Intent(this, HomeActivity::class.java))
            finish()
        }, 2000)

    }
}