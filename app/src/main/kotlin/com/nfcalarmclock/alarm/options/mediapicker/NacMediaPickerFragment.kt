package com.nfcalarmclock.alarm.options.mediapicker

import android.app.Activity
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.media3.common.util.UnstableApi
import com.nfcalarmclock.R
import com.nfcalarmclock.alarm.NacAlarmViewModel
import com.nfcalarmclock.alarm.db.NacAlarm
import com.nfcalarmclock.system.mediaplayer.NacMediaPlayer
import com.nfcalarmclock.system.scheduler.NacScheduler
import com.nfcalarmclock.shared.NacSharedPreferences
import com.nfcalarmclock.util.NacBundle
import com.nfcalarmclock.util.NacUtility
import dagger.hilt.android.AndroidEntryPoint

/**
 * Media fragment for ringtones and music files.
 */
@AndroidEntryPoint
open class NacMediaPickerFragment
	: Fragment()
{

	/**
	 * Alarm view model.
	 */
	private val alarmViewModel: NacAlarmViewModel by viewModels()

	/**
	 * Alarm.
	 */
	private var alarm: NacAlarm? = null

	/**
	 * Media player.
	 */
	var mediaPlayer: NacMediaPlayer? = null

	/**
	 * The media path.
	 */
	var mediaPath: String = ""
		get()
		{
			return alarm?.mediaPath ?: field
		}
		set(value)
		{
			if (alarm != null)
			{
				alarm!!.setMedia(requireContext(), value)
			}
			else
			{
				field = value
			}
		}

	/**
	 * Whether to shuffle the media.
	 */
	var shuffleMedia: Boolean = false
		get()
		{
			return alarm?.shuffleMedia ?: field
		}
		set(value)
		{
			if (alarm != null)
			{
				alarm!!.shuffleMedia = value
			}
			else
			{
				field = value
			}
		}

	/**
	 * Whether to recursively play the media in a directory.
	 */
	var recursivelyPlayMedia: Boolean = false
		get()
		{
			return alarm?.recursivelyPlayMedia ?: field
		}
		set(value)
		{
			if (alarm != null)
			{
				alarm!!.recursivelyPlayMedia = value
			}
			else
			{
				field = value
			}
		}

	/**
	 * Called when the Cancel button is clicked.
	 */
	open fun onCancelClicked()
	{
		requireActivity().finish()
	}

	/**
	 * Called when the Clear button is clicked.
	 */
	@UnstableApi
	open fun onClearClicked()
	{
		// Clear the media that is being used
		mediaPath = ""

		// Stop any media that is already playing
		mediaPlayer?.exoPlayer?.stop()
	}

	/**
	 * Called when the fragment is created.
	 */
	@UnstableApi
	override fun onCreate(savedInstanceState: Bundle?)
	{
		// Super
		super.onCreate(savedInstanceState)

		// Set the alarm
		alarm = NacBundle.getAlarm(arguments)

		// Check if the alarm was not set
		if (alarm == null)
		{
			// Set the media info
			mediaPath = NacBundle.getMediaPath(arguments)
			shuffleMedia = NacBundle.getShuffleMedia(arguments)
			recursivelyPlayMedia = NacBundle.getRecursivelyPlayMedia(arguments)
		}

		// Create the media player
		val context = requireContext()
		mediaPlayer = NacMediaPlayer(context)

		// Gain transient audio focus
		mediaPlayer!!.shouldGainTransientAudioFocus = true
	}

	/**
	 * Called when the fragment is destroyed.
	 */
	@UnstableApi
	override fun onDestroy()
	{
		// Super
		super.onDestroy()

		// Cleanup the media player
		mediaPlayer?.release()
	}

	/**
	 * Called when the Ok button is clicked.
	 */
	open fun onOkClicked()
	{
		val activity = requireActivity()

		// Check if alarm is set
		if (alarm != null)
		{
			// Update the alarm for the activity
			alarmViewModel.update(alarm!!)

			// Reschedule the alarm
			NacScheduler.update(activity, alarm!!)
		}
		// The media must be set
		else
		{
			// Create an intent with the media
			val intent = NacMediaPickerActivity.getStartIntentWithMedia(
				mediaPath = mediaPath,
				shuffleMedia = shuffleMedia,
				recursivelyPlayMedia = recursivelyPlayMedia)

			// Set the result of the activity with the media path as part of
			// the intent
			activity.setResult(Activity.RESULT_OK, intent)
		}

		// Finish the activity
		activity.finish()
	}

	/**
	 * Play audio from the media player.
	 *
	 * @param  uri  The Uri of the content to play.
	 */
	@UnstableApi
	protected fun play(uri: Uri)
	{
		val path = uri.toString()

		// Invalid URI path since it does not start with "content://"
		if (!path.startsWith("content://"))
		{
			// Show an error toast
			NacUtility.quickToast(requireContext(), R.string.error_message_play_audio)
			return
		}

		// Set the path of the media that is going to play
		mediaPath = path

		// Stop any media that is already playing
		mediaPlayer?.exoPlayer?.stop()

		// Save the current volume
		mediaPlayer!!.audioAttributes.saveCurrentVolume()

		// Play the media
		mediaPlayer!!.playUri(uri)
	}

	/**
	 * Setup action buttons.
	 */
	@UnstableApi
	protected fun setupActionButtons(root: View)
	{
		val shared = NacSharedPreferences(requireContext())
		val clear = root.findViewById<Button>(R.id.clear)
		val cancel = root.findViewById<Button>(R.id.cancel)
		val ok = root.findViewById<Button>(R.id.ok)

		// Set the color of the buttons
		clear.setTextColor(shared.themeColor)
		cancel.setTextColor(shared.themeColor)
		ok.setTextColor(shared.themeColor)

		// Set the Clear on click listener
		clear.setOnClickListener {
			onClearClicked()
		}

		// Set the Cancel on click listener
		cancel.setOnClickListener {
			onCancelClicked()
		}

		// Set the Ok on click listener
		ok.setOnClickListener {
			onOkClicked()
		}
	}

	///**
	// * Setup the media player.
	// */
	//@UnstableApi
	//private fun setupMediaPlayer()
	//{
	//	// Get the context
	//	val context = requireContext()

	//	// Create the media player
	//	mediaPlayer = NacMediaPlayer(context)

	//	// Gain transient audio focus
	//	mediaPlayer!!.shouldGainTransientAudioFocus = true
	//}

}
