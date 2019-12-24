package id.ac.ui.cs.mobileprogramming.anisha_inas.wacana.ui.splashScreen

import android.content.Context
import android.opengl.GLSurfaceView
import android.util.AttributeSet


class SplashGLSurfaceView(context: Context, attrs: AttributeSet) : GLSurfaceView(context, attrs) {

    private val renderer: SplashGLRenderer

    init {

        // Create an OpenGL ES 2.0 context
        setEGLContextClientVersion(2)

        renderer = SplashGLRenderer(context)

        // Set the Renderer for drawing on the GLSurfaceView
        setRenderer(renderer)
        renderMode = RENDERMODE_WHEN_DIRTY
    }
}