package com.example.fruitox.ui

import android.Manifest
import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.media.Image
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.provider.MediaStore.Images.Thumbnails.getThumbnail
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.Toast
import androidx.camera.core.ImageCapture
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.fruitox.R
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.navigation.fragment.findNavController
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.label.ImageLabeler
import com.google.mlkit.vision.label.ImageLabeling
import com.google.mlkit.vision.label.defaults.ImageLabelerOptions
import kotlinx.android.synthetic.main.fragment_camera.*
import java.io.File
import java.nio.ByteBuffer
import java.text.SimpleDateFormat
import java.util.*
import java.io.IOException


typealias LumaListener = (luma: Double) -> Unit


class Camera : Fragment() {

    //.................values
    private lateinit var greenBar: ImageView

    private var imageCapture: ImageCapture? = null

    private lateinit var outputDirectory: File
    private lateinit var cameraExecutor: ExecutorService

    lateinit var image: InputImage

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? { val view = inflater.inflate(R.layout.fragment_camera, container, false)

        greenBar = view.findViewById(R.id.green_bar)
        animate()

        val anime = AnimationUtils.loadAnimation(requireContext(), R.anim.scale_up)
        val btn = view.findViewById<ImageButton>(R.id.green_btn)


        // Request camera permissions
        if (allPermissionsGranted()) {
            startCamera()
        } else {
            ActivityCompat.requestPermissions(
                requireActivity(), REQUIRED_PERMISSIONS, REQUEST_CODE_PERMISSIONS)
        }

        // Set up the listener for take photo button
        btn.setOnClickListener {
            btn.startAnimation(anime)
            takePhoto()
            Handler(Looper.getMainLooper()).postDelayed({
                findNavController().navigate(R.id.action_camera_to_fruitInfo)
            }, 2000) }

        outputDirectory = getOutputDirectory(requireContext())

        cameraExecutor = Executors.newSingleThreadExecutor()

        return view
    }

    private fun animate(){
        ObjectAnimator.ofFloat(greenBar, "translationY", 1500f).apply {
            duration = 2000
            start()
            Handler(Looper.getMainLooper()).postDelayed({
                greenBar.y = -200f
                animate()
            }, 2050)
        }
    }

    private fun getOutputDirectory(context: Context): File {
        val appContext = context.applicationContext
        val mediaDir = context.externalMediaDirs.firstOrNull()?.let {
            File(it, appContext.resources.getString(R.string.app_name)).apply { mkdirs() } }
        return if (mediaDir != null && mediaDir.exists())
            mediaDir else appContext.filesDir
    }

    private fun takePhoto() {
        // Get a stable reference of the modifiable image capture use case
        val imageCapture = imageCapture ?: return

        // Create time-stamped output file to hold the image
        val photoFile = File(
            outputDirectory,
            SimpleDateFormat(FILENAME_FORMAT, Locale.US
            ).format(System.currentTimeMillis()) + ".jpg")

        // Create output options object which contains file + metadata
        val outputOptions = ImageCapture.OutputFileOptions.Builder(photoFile).build()

        imageCapture.takePicture(
            outputOptions, ContextCompat.getMainExecutor(requireContext()), object : ImageCapture.OnImageSavedCallback {
                override fun onError(exc: ImageCaptureException) {
                    Log.e(TAG, "Photo capture failed: ${exc.message}", exc)
                }

                override fun onImageSaved(output: ImageCapture.OutputFileResults) {
                    val savedUri = Uri.fromFile(photoFile)
                    //val msg = "Photo capture succeeded: $savedUri"
                    //Toast.makeText(requireContext(), msg, Toast.LENGTH_SHORT).show()
                    //Log.d(TAG, msg)

                    //.........send the image to the ml kit here and navigate
                    try {
                        image = InputImage.fromFilePath(requireContext(), savedUri)
                    } catch (e: IOException) {
                        e.printStackTrace()
                    }

                    val labeler = ImageLabeling.getClient(ImageLabelerOptions.DEFAULT_OPTIONS)

                    labeler.process(image)
                        .addOnSuccessListener { labels ->
                            // Task completed successfully
                            val lbl: MutableList<String>  = mutableListOf()
                            for(label in labels){
                                lbl.add(label.text)
                            }
                            Toast.makeText(requireContext(), lbl.toString(), Toast.LENGTH_LONG).show()
                        }
                        .addOnFailureListener { e ->
                            // Task failed with an exception
                            Toast.makeText(requireContext(), e.toString(), Toast.LENGTH_LONG).show()
                        }


                }
            })

    }

    private fun startCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(requireContext())

        cameraProviderFuture.addListener(Runnable {
            // Used to bind the lifecycle of cameras to the lifecycle owner
            val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()

            // Preview
            val preview = Preview.Builder()
                .build()
                .also {
                    it.setSurfaceProvider(cam_view.surfaceProvider)
                }

            imageCapture = ImageCapture.Builder()
                .build()

            val imageAnalyzer = ImageAnalysis.Builder()
                .build()
                .also {
                    it.setAnalyzer(cameraExecutor, LuminosityAnalyzer { luma ->
                        Log.d(TAG, "Average luminosity: $luma")
                    })
                }

            // Select back camera as a default
            val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

            try {
                // Unbind use cases before rebinding
                cameraProvider.unbindAll()

                // Bind use cases to camera
                cameraProvider.bindToLifecycle(
                    this, cameraSelector, preview, imageCapture, imageAnalyzer)

            } catch(exc: Exception) {
                Log.e(TAG, "Use case binding failed", exc)
            }

        }, ContextCompat.getMainExecutor(requireContext()))
    }

    private fun allPermissionsGranted() = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(
            requireContext(), it) == PackageManager.PERMISSION_GRANTED
    }

    override fun onDestroy() {
        super.onDestroy()
        cameraExecutor.shutdown()
    }

    companion object {
        private const val TAG = "CameraXBasic"
        private const val FILENAME_FORMAT = "yyyy-MM-dd-HH-mm-ss-SSS"
        private const val REQUEST_CODE_PERMISSIONS = 10
        private val REQUIRED_PERMISSIONS = arrayOf(Manifest.permission.CAMERA)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            if (allPermissionsGranted()) {
                startCamera()
            } else {
                Toast.makeText(requireContext(),
                    "Permissions not granted by the user.",
                    Toast.LENGTH_SHORT).show()
            }
        }
    }

    //...........analyzer class
    inner class LuminosityAnalyzer(private val listener: LumaListener) : ImageAnalysis.Analyzer {

        //.................values
        lateinit var image: InputImage
        lateinit var labeler: ImageLabeler

        private fun ByteBuffer.toByteArray(): ByteArray {
            rewind()    // Rewind the buffer to zero
            val data = ByteArray(remaining())
            get(data)   // Copy the buffer into a byte array
            return data // Return the byte array
        }

        @SuppressLint("UnsafeOptInUsageError")
        override fun analyze(imageProxy: ImageProxy) {


            val buffer = imageProxy.planes[0].buffer
            val data = buffer.toByteArray()
            val pixels = data.map { it.toInt() and 0xFF }
            val luma = pixels.average()

            listener(luma)

            imageProxy.close()
        }

    }
}