package id.ac.ui.cs.mobileprogramming.anisha_inas.wacana.ui.splashScreen

import android.content.Context
import android.opengl.GLES20
import android.opengl.GLSurfaceView
import android.opengl.Matrix
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

class SplashGLRenderer(val context: Context) : GLSurfaceView.Renderer {

    private lateinit var mSquare1: Square
    private lateinit var mSquare2: Square
    private val mViewMatrix = FloatArray(16)
    private val mMVPMatrix = FloatArray(16)
    private val mProjectionMatrix = FloatArray(16)

    override fun onSurfaceCreated(unused: GL10, config: EGLConfig) {
        // Set the background frame color
        GLES20.glClearColor(211.0f, 47.0f, 47.0f, 1.0f)
        // initialize a triangle

        var squareCoords1 = floatArrayOf(
            0f, 0.5f, 0.0f,      // top left
            -0.5f, 0f, 0.0f,      // bottom left
            0f, -0.5f, 0.0f,      // bottom right
            0.5f, 0f, 0.0f       // top right
        )
        val color1 = floatArrayOf(0.60392f, 0f, 0.02745f, 0.0f)

        var squareCoords2 = floatArrayOf(
            -0.35f, 0.35f, 0.0f,      // top left
            -0.35f, -0.35f, 0.0f,      // bottom left
            0.35f, -0.35f, 0.0f,      // bottom right
            0.35f, 0.35f, 0.0f       // top right
        )
        val color2 = floatArrayOf(0.82745f, 0.18431f, 0.18431f, 1.0f)

        mSquare1 = Square(squareCoords1, color1)
        mSquare2 = Square(squareCoords2, color2)
    }

    override fun onDrawFrame(unused: GL10) {
        // Redraw background color
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT)
//        mSquare1.draw()
//        mSquare2.draw()

        // Set the camera position (View matrix)
        Matrix.setLookAtM(mViewMatrix, 0, 0f, 0f, -3f, 0f, 0f, 0f, 0f, 1.0f, 0.0f)

        // Calculate the projection and view transformation
        Matrix.multiplyMM(mMVPMatrix, 0, mProjectionMatrix, 0, mViewMatrix, 0)

        // Draw square
        mSquare1.draw(mMVPMatrix)
        mSquare2.draw(mMVPMatrix)
    }

    override fun onSurfaceChanged(unused: GL10, width: Int, height: Int) {
        GLES20.glViewport(0, 0, width, height)
        val ratio = width.toFloat() / height

        // this projection matrix is applied to object coordinates
        // in the onDrawFrame() method
        Matrix.frustumM(mProjectionMatrix, 0, -ratio, ratio, -1f, 1f, 3f, 7f)
    }

}