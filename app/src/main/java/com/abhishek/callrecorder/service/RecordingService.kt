package com.abhishek.callrecorder.service

import android.accessibilityservice.AccessibilityService
import android.graphics.PixelFormat
import android.media.MediaRecorder
import android.view.Gravity
import android.view.LayoutInflater
import android.view.WindowManager
import android.view.accessibility.AccessibilityEvent
import android.widget.Button
import android.widget.FrameLayout
import com.abhishek.callrecorder.R
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

class RecordingService : AccessibilityService() {
    private var mLayout: FrameLayout? = null
    private lateinit var mRecorder: MediaRecorder
    private lateinit var mRecordButton: Button
    private var isRecording = false

    override fun onAccessibilityEvent(AccessibilityEvent: AccessibilityEvent?) {
    }

    override fun onInterrupt() {

    }

    override fun onServiceConnected() {
        val windowManager = getSystemService(WINDOW_SERVICE) as WindowManager
        mLayout = FrameLayout(this)
        val layoutParams = WindowManager.LayoutParams()
        layoutParams.type = WindowManager.LayoutParams.TYPE_ACCESSIBILITY_OVERLAY
        layoutParams.format = PixelFormat.TRANSLUCENT
        layoutParams.flags = layoutParams.flags or WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
        layoutParams.width = WindowManager.LayoutParams.WRAP_CONTENT
        layoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT
        layoutParams.gravity = Gravity.TOP
        val inflater = LayoutInflater.from(this)
        inflater.inflate(R.layout.activity_main, mLayout)
        windowManager.addView(mLayout, layoutParams)
        startRecording()
    }

    private fun startRecording() {
        mRecorder = MediaRecorder()
        mRecordButton = mLayout!!.findViewById(R.id.record)
        mRecordButton.setOnClickListener {
            if (!isRecording) {
                mRecordButton.text = "Stop Recording"
                var audiofile: File? = null

                val soundDir = File(getExternalFilesDir(null), "/SoundRecorder")

                if (!soundDir.exists()) {
                    soundDir.mkdirs()
                }

                val dateTime = SimpleDateFormat("dd-MM-yyyy hh-mm-ss").format(Date())
                val fileName = "Record$dateTime"

                try {
                    audiofile = File.createTempFile(fileName, ".wav", soundDir)
                } catch (e: IOException) {
                    e.printStackTrace()
                }
                mRecorder.setAudioSource(MediaRecorder.AudioSource.VOICE_RECOGNITION)
                mRecorder.setOutputFormat(MediaRecorder.OutputFormat.AMR_NB)
                mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB)
                mRecorder.setOutputFile(audiofile?.absolutePath)

                try {
                    mRecorder.prepare()
                } catch (e: IllegalStateException) {
                    e.printStackTrace()
                } catch (e: IOException) {
                    e.printStackTrace()
                }
                mRecorder.start()
                isRecording = true
            } else {
                mRecorder.stop()
                isRecording = false
                mRecordButton.text = "Start Recording"
            }
        }
    }
}