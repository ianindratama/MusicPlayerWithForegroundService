package com.ianindratama.musicplayerwithforegroundservice

import android.media.MediaPlayer
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.SeekBar
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.ianindratama.musicplayerwithforegroundservice.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private lateinit var mediaPlayer: MediaPlayer

    private lateinit var runnable: Runnable
    private lateinit var handler: Handler
    private var delay = 100L
    private var jumpValue = 5000

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        setUpMediaPlayer()

    }

    private fun setUpMediaPlayer(){

        mediaPlayer = MediaPlayer.create(this@MainActivity, R.raw.memory_reboot)

        binding.seekbarAudio.max = mediaPlayer.duration

        handler = Handler(Looper.getMainLooper())
        runnable = Runnable {
            binding.seekbarAudio.progress = mediaPlayer.currentPosition
            handler.postDelayed(runnable, delay)
        }

        binding.btnPlay.setOnClickListener {
            setUpPlayPauseButton()
        }

        binding.btnForward.setOnClickListener {
            setUpForwardButton()
        }

        binding.btnBackward.setOnClickListener {
            setUpBackwardButton()
        }


        mediaPlayer.setOnCompletionListener {
            binding.btnPlay.background = ResourcesCompat.getDrawable(resources, R.drawable.round_play_circle_24, theme)
            handler.removeCallbacks(runnable)
        }

        binding.seekbarAudio.setOnSeekBarChangeListener(object: SeekBar.OnSeekBarChangeListener{
            override fun onProgressChanged(p0: SeekBar?, p1: Int, p2: Boolean) {
                if (p2) mediaPlayer.seekTo(p1)

//                binding.tvTrackProgress.text = durationStringFormat(p1, mediaPlayer.duration)
            }

            override fun onStartTrackingTouch(p0: SeekBar?) {}

            override fun onStopTrackingTouch(p0: SeekBar?) {}

        })

    }

    private fun setUpPlayPauseButton(){
        if (!mediaPlayer.isPlaying){
            if (binding.seekbarAudio.progress == mediaPlayer.duration){
                binding.seekbarAudio.progress = 0
                mediaPlayer.seekTo(0)
            }
            mediaPlayer.start()
            binding.btnPlay.background = ResourcesCompat.getDrawable(resources, R.drawable.round_pause_circle_24, theme)
            handler.postDelayed(runnable, 0)
        }else{
            mediaPlayer.pause()
            binding.btnPlay.background = ResourcesCompat.getDrawable(resources, R.drawable.round_play_circle_24, theme)
            handler.removeCallbacks(runnable)
        }
    }

    private fun setUpForwardButton(){
        mediaPlayer.seekTo(mediaPlayer.currentPosition + jumpValue)
        binding.seekbarAudio.progress += jumpValue
    }

    private fun setUpBackwardButton(){
        mediaPlayer.seekTo(mediaPlayer.currentPosition - jumpValue)
        binding.seekbarAudio.progress -= jumpValue
    }

}