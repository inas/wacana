package id.ac.ui.cs.mobileprogramming.anisha_inas.wacana.ui.splashScreen

import android.opengl.GLSurfaceView
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import id.ac.ui.cs.mobileprogramming.anisha_inas.wacana.R

class SplashScreenActivity : AppCompatActivity() {

    private lateinit var gLView: GLSurfaceView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Create a GLSurfaceView instance and set it
        // as the ContentView for this Activity.
//        gLView =
//            SplashGLSurfaceView(
//                this
//            )
        setContentView(R.layout.activity_splash_screen)
        gLView = findViewById(R.id.surface_view)
//        Handler().postDelayed(Runnable {
//            startActivity(Intent(this, HomeActivity::class.java))
//            finish()
//        }, 2000)

    }

}