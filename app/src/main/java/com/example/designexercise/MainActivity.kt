package com.example.designexercise

import android.animation.Animator
import android.animation.AnimatorInflater
import android.animation.AnimatorListenerAdapter
import android.animation.ObjectAnimator
import android.animation.PropertyValuesHolder
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.SeekBar
import android.widget.TextView
import androidx.activity.ComponentActivity
import kotlin.random.Random

class MainActivity : ComponentActivity() {

  private val handler = Handler(Looper.getMainLooper())
  private lateinit var playPauseButton: ImageButton
  private lateinit var likeButton: ImageButton

  private class State {
    var trackIndex: Int = 0
    var isPlaying: Boolean = false
    var isRepeat: Boolean = false
    var currentPlayMs: Long = 0
  }

  private val state = State()

  // Data Structure for Track
  class Track(
    val title: String,
    val artists: String,
    val durationMs: Long,
    val albumCoverResId: Int,
  ) {
    var isFavorited = false
  }

  private val tracks = listOf(
    Track(
      title = "Irreplaceable",
      artists = "Beyonce",
      durationMs = 311000L, // 5:11
      R.drawable.bday,
    ),
    Track(
      title = "JUST FOR FUN (really long)",
      artists = "Beyonce",
      durationMs = 500000L, // 8:20
      R.drawable.album_cowboy_carter,
    ),
    Track(
      title = "Halo",
      artists = "Queen Beeeeeeeeeeeeeeee aka Sasha Fierce",
      durationMs = 234345L, // 3:54
      R.drawable.album_sasha_fierce,
    )
  )

  private val currentTrack: Track
    get() = tracks[state.trackIndex]

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_layout)

    playPauseButton = findViewById(R.id.play_pause_button)
    playPauseButton.stateListAnimator = AnimatorInflater.loadStateListAnimator(
      this, R.animator.scale_up_down
    )
    playPauseButton.setOnClickListener { togglePlayback() }

    likeButton = findViewById(R.id.like_button)
    likeButton.setOnClickListener { toggleLike() }

    findViewById<ImageButton>(R.id.repeat_button).setOnClickListener { toggleRepeat() }

    findViewById<ImageButton>(R.id.next_button).setOnClickListener { playNextTrack() }
    findViewById<ImageButton>(R.id.previous_button).setOnClickListener { playPreviousTrack() }

    findViewById<SeekBar>(R.id.progress_slider).setOnSeekBarChangeListener(
      object : SeekBar.OnSeekBarChangeListener {
        override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
          findViewById<TextView>(R.id.current_time).text = formatTime(progress.toLong())
          if (fromUser) {
            state.currentPlayMs = progress.toLong()
          }
        }

        override fun onStartTrackingTouch(seekBar: SeekBar?) {}
        override fun onStopTrackingTouch(seekBar: SeekBar?) {}
      }
    )

    renderCurrentTrack()

  }

  private fun renderCurrentTrack() {
    findViewById<ImageView>(R.id.album_art).setImageResource(currentTrack.albumCoverResId)
    findViewById<TextView>(R.id.track_title).text = currentTrack.title
    findViewById<TextView>(R.id.artist_name).text = currentTrack.artists
    findViewById<TextView>(R.id.current_time).text = formatTime(0)
    findViewById<TextView>(R.id.track_duration).text = formatTime(currentTrack.durationMs)
    findViewById<SeekBar>(R.id.progress_slider).apply {
      max = currentTrack.durationMs.toInt()
      progress = state.currentPlayMs.toInt()
    }
    val iconRes = if (state.isPlaying) R.drawable.pause_48px else R.drawable.play_arrow_48px
    val contentDesc = if (state.isPlaying) "Pause playback" else "Play playback"
    playPauseButton.apply {
      setImageResource(iconRes)
      contentDescription = contentDesc
    }
    likeButton.setImageResource(
      if (currentTrack.isFavorited) {
        R.drawable.heart_checked
      } else {
        R.drawable.fitbit_heart_rate_48px
      }
    )

    findViewById<ImageButton>(R.id.next_button).isEnabled = state.trackIndex < (tracks.size - 1)
    findViewById<ImageButton>(R.id.previous_button).isEnabled = state.trackIndex > 0
  }

  private fun formatTime(ms: Long): String {
    val totalSeconds = ms / 1000
    val minutes = totalSeconds / 60
    val seconds = totalSeconds % 60
    return String.format("%d:%02d", minutes, seconds)
  }

  private fun toggleRepeat() {
    state.isRepeat = !state.isRepeat
    findViewById<ImageButton>(R.id.repeat_button).setImageResource(
      if (state.isRepeat) {
        R.drawable.repeat_on_48px
      } else {
        R.drawable.repeat_48px
      }
    )
  }

  /**
   * Toggles the playback state with an animation for Feel & Motion.
   */
  private fun togglePlayback() {
    state.isPlaying = !state.isPlaying
    val iconRes = if (state.isPlaying) R.drawable.pause_48px else R.drawable.play_arrow_48px
    val contentDesc = if (state.isPlaying) "Pause playback" else "Play playback"

    handler.removeCallbacks(updateSeekBarRunnable)
    if (state.isPlaying) {
      handler.post(updateSeekBarRunnable)
    }

    ObjectAnimator.ofFloat(playPauseButton, "alpha", 1f, 0f).apply {
      duration = 100
      addListener(object : AnimatorListenerAdapter() {
        override fun onAnimationEnd(animation: Animator) {
          playPauseButton.apply {
            setImageResource(iconRes)
            contentDescription = contentDesc
          }
          ObjectAnimator.ofFloat(playPauseButton, "alpha", 0f, 1f).setDuration(150).start()
        }
      })
      start()
    }
  }

  private val updateSeekBarRunnable: Runnable = Runnable {
    // Check if playback is active (important for stopping the timer)
    if (state.isPlaying) {
      val seekBar = findViewById<SeekBar>(R.id.progress_slider)
      if (seekBar.progress < seekBar.max) {
        val newTime = seekBar.progress + 1000
        seekBar.progress = newTime
        findViewById<TextView>(R.id.current_time).text = formatTime(newTime.toLong())
        handler.postDelayed(updateSeekBarRunnable, 1000)
      } else {
        if (state.trackIndex < (tracks.size - 1)) {
          playNextTrack()
        } else {
          togglePlayback()
        }
      }
    } else {
      handler.removeCallbacks(updateSeekBarRunnable)
    }
  }

  private fun toggleLike() {
    currentTrack.isFavorited = !currentTrack.isFavorited
    renderCurrentTrack()

    // Add Scale Animation for Pop Effect (Feel & Motion)
    ObjectAnimator.ofPropertyValuesHolder(
      likeButton,
      PropertyValuesHolder.ofFloat("scaleX", 1f, 1.3f, 1f),
      PropertyValuesHolder.ofFloat("scaleY", 1f, 1.3f, 1f)
    ).setDuration(200).start()
  }

  private fun playNextTrack() {
    state.trackIndex = if (state.isRepeat) {
      Random.nextInt(tracks.size)
    } else {
      state.trackIndex + 1
    }
    state.currentPlayMs = 0
    renderCurrentTrack()
  }

  private fun playPreviousTrack() {
    state.trackIndex--
    state.currentPlayMs = 0
    renderCurrentTrack()
  }
}