package id.ac.ui.cs.mobileprogramming.anisha_inas.wacana.ui.splashScreen

import android.content.Context
import android.opengl.GLES20
import android.opengl.GLSurfaceView
import android.opengl.Matrix
import android.os.SystemClock
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

class SplashGLRenderer(val context: Context) : GLSurfaceView.Renderer {

    private lateinit var mSquare1: Square
    private lateinit var mSquare2: Square

    private val mViewMatrix = FloatArray(16)
    private val mMVPMatrix = FloatArray(16)
    private val mProjectionMatrix = FloatArray(16)
    private val mRotationMatrix = FloatArray(16)
    private var mTempMatrix = FloatArray(16)
    private val mModelMatrix = FloatArray(16)
    private var dx = 0f

    override fun onSurfaceCreated(unused: GL10, config: EGLConfig) {
        GLES20.glClearColor(211.0f, 47.0f, 47.0f, 1.0f)

        val squareCoords = floatArrayOf(
            0f, 0.5f, 0.0f,
            -0.5f, 0f, 0.0f,
            0f, -0.5f, 0.0f,
            0.5f, 0f, 0.0f
        )
        val color1 = floatArrayOf(0.60392f, 0f, 0.02745f, 0.0f)
        val color2 = floatArrayOf(0.82745f, 0.18431f, 0.18431f, 1.0f)

        mSquare1 = Square(squareCoords, color1)
        mSquare2 = Square(squareCoords, color2)
    }

    override fun onDrawFrame(unused: GL10) {
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT)

        rotateSquare(mSquare1, 1)
        rotateSquare(mSquare2, -1)
    }

    override fun onSurfaceChanged(unused: GL10, width: Int, height: Int) {
        GLES20.glViewport(0, 0, width, height)
        val ratio = width.toFloat() / height

        Matrix.frustumM(mProjectionMatrix, 0, -ratio, ratio, -1f, 1f, 3f, 7f)
    }

    private fun rotateSquare(square: Square, direction: Int) {
        Matrix.setIdentityM(mModelMatrix, 0)
        Matrix.translateM(mModelMatrix, 0, direction * dx, 0f, 0f)
        dx += 0.0007f

        Matrix.setLookAtM(mViewMatrix, 0, 0f, 0f, -3f, 0f, 0f, 0f, 0f, 1.0f, 0.0f)
        Matrix.multiplyMM(mMVPMatrix, 0, mProjectionMatrix, 0, mViewMatrix, 0)

        val time = SystemClock.uptimeMillis() % 4000L
        val angle = direction * 0.090f * time.toInt()
        Matrix.setRotateM(mRotationMatrix, 0, angle, 0f, 0f, -1.0f)

        mTempMatrix = mModelMatrix.clone()
        Matrix.multiplyMM(mModelMatrix, 0, mTempMatrix, 0, mRotationMatrix, 0)
        mTempMatrix = mMVPMatrix.clone()
        Matrix.multiplyMM(mMVPMatrix, 0, mTempMatrix, 0, mModelMatrix, 0)
        square.draw(mMVPMatrix)
    }
}