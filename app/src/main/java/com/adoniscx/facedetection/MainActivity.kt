package com.adoniscx.facedetection

import android.hardware.Camera
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.SurfaceHolder
import kotlinx.android.synthetic.main.activity_main.*
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.info

class MainActivity : AppCompatActivity(), AnkoLogger {

    private val faceDetectionListener = Camera.FaceDetectionListener { faces, camera ->
        info { "faces : ${faces.size}" }

        if (faces.size > 0) {
            // do something!!!
            info { "Detect the human face!!!" }
        }
    }

    private var camera: Camera? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    override fun onPostCreate(savedInstanceState: Bundle?) {
        super.onPostCreate(savedInstanceState)
        mainSurfaceView.holder.addCallback(object : SurfaceHolder.Callback {
            override fun surfaceChanged(surfaceHolder: SurfaceHolder?, format: Int, width: Int, height: Int) {
                if (surfaceHolder?.surface == null) {
                    return
                }

                try {
                    camera?.stopPreview()
                } catch (e: Exception) {
                    info("camera stopPreview error.", e)
                }

                camera?.parameters?.apply {
                    val previewSizes = supportedPreviewSizes
                    val targetRatio = width.toDouble() / height
                    val previewSize = Util.getOptimalPreviewSize(this@MainActivity, previewSizes, targetRatio)
                    setPreviewSize(previewSize.width, previewSize.height)
                    focusMode = Camera.Parameters.FOCUS_MODE_CONTINUOUS_VIDEO
                }

                camera?.apply {
                    startPreview()
                    setFaceDetectionListener(faceDetectionListener)
                    startFaceDetection()
                }
            }


            override fun surfaceCreated(surfaceHolder: SurfaceHolder?) {
                // open camera.
                camera = Camera.open()
                try {
                    camera?.setPreviewDisplay(surfaceHolder)
                } catch (e: Exception) {
                    info("camera setPreviewDisplay error.", e)
                }

            }


            override fun surfaceDestroyed(surfaceHolder: SurfaceHolder?) {
                // release camera when surface destroyed.
                camera?.apply {
                    setPreviewCallback(null)
                    setFaceDetectionListener(null)
                    release()
                }
                camera = null
            }

        })
    }

}
