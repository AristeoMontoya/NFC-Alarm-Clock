package com.nfcalarmclock.alarm.db

import android.content.Context
import android.os.Parcel
import android.os.Parcelable
import android.text.format.DateFormat
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.nfcalarmclock.media.NacMedia
import com.nfcalarmclock.shared.NacSharedPreferences
import com.nfcalarmclock.util.NacCalendar
import com.nfcalarmclock.util.NacCalendar.Day
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import java.util.Calendar
import java.util.EnumSet
import java.util.Locale

/**
 * Alarm.
 */
@Entity(tableName = "alarm")
class NacAlarm()
	: Comparable<NacAlarm>,
	Parcelable
{

	/**
	 * Unique alarm ID.
	 * <p>
	 * When setting the Id manually, it must be 0 to be autogenerated.
	 */
	@PrimaryKey(autoGenerate = true)
	@ColumnInfo(name = "id")
	var id: Long = 0

	/**
	 * Flag indicating whether the alarm is currently active or not.
	 */
	@ColumnInfo(name = "is_active")
	var isActive: Boolean = false

	/**
	 * Amount of time, in milliseconds, the alarm has been active for.
	 *
	 * This will typically only change when the alarm is snoozed.
	 */
	@ColumnInfo(name = "time_active")
	var timeActive: Long = 0

	/**
	 * Number of times the alarm has been snoozed.
	 */
	@ColumnInfo(name = "snooze_count")
	var snoozeCount: Int = 0

	/**
	 * Flag indicating whether the alarm is enabled or not.
	 */
	@ColumnInfo(name = "is_enabled")
	var isEnabled: Boolean = false

	/**
	 * Hour at which to run the alarm.
	 */
	@ColumnInfo(name = "hour")
	var hour: Int = 0

	/**
	 * Minute at which to run the alarm.
	 */
	@ColumnInfo(name = "minute")
	var minute: Int = 0

	/**
	 * Hour at which to run the alarm, when it is snoozed.
	 */
	@ColumnInfo(name = "snooze_hour")
	var snoozeHour: Int = 0

	/**
	 * Minute at which to run the alarm, when it is snoozed
	 */
	@ColumnInfo(name = "snooze_minute")
	var snoozeMinute: Int = 0

	/**
	 * Days on which to run the alarm.
	 */
	@ColumnInfo(name = "days")
	var days: EnumSet<Day> = EnumSet.noneOf(Day::class.java)

	/**
	 * Flag indicating whether the alarm should be repeated or not.
	 */
	@ColumnInfo(name = "should_repeat")
	var repeat: Boolean = false

	/**
	 * Flag indicating whether the alarm should vibrate the phone or not.
	 */
	@ColumnInfo(name = "should_vibrate")
	var vibrate: Boolean = false

	/**
	 * Flag indicating whether the alarm should use NFC or not.
	 */
	@ColumnInfo(name = "should_use_nfc")
	var useNfc: Boolean = false

	/**
	 * Flag indicating whether the alarm should use the flashlight or not.
	 */
	@ColumnInfo(name = "should_use_flashlight", defaultValue = "0")
	var useFlashlight: Boolean = false

	/**
	 * Strength level of the flashlight. Only available on API >= 33.
	 */
	@ColumnInfo(name = "flashlight_strength_level", defaultValue = "0")
	var flashlightStrengthLevel: Int = 0

	/**
	 * Amount of time, in seconds, to wait before gradually increasing the flashlight
	 * strength level another step.
	 */
	@ColumnInfo(name = "gradually_increase_flashlight_strength_level_wait_time", defaultValue = "5")
	var graduallyIncreaseFlashlightStrengthLevelWaitTime: Int = 5

	/**
	 * Whether the flashlight should be blinked or not.
	 */
	@ColumnInfo(name = "should_blink_flashlight", defaultValue = "0")
	var shouldBlinkFlashlight: Boolean = false

	/**
	 * Number of seconds to turn on the flashlight.
	 */
	@ColumnInfo(name = "flashlight_on_duration", defaultValue = "0")
	var flashlightOnDuration: String = "0"

	/**
	 * Number of seconds to turn off the flashlight.
	 */
	@ColumnInfo(name = "flashlight_off_duration", defaultValue = "0")
	var flashlightOffDuration: String = "0"

	/**
	 * ID of the NFC tag that needs to be used to dismiss the alarm.
	 */
	@ColumnInfo(name = "nfc_tag_id")
	var nfcTagId: String = ""

	/**
	 * Path to the media that will play when the alarm is run.
	 */
	@ColumnInfo(name = "media_path")
	var mediaPath: String = ""

	/**
	 * Title of the media that will play when the alarm is run.
	 */
	@ColumnInfo(name = "media_title")
	var mediaTitle: String = ""

	/**
	 * Type of media.
	 */
	@ColumnInfo(name = "media_type")
	var mediaType: Int = 0

	/**
	 * Whether to shuffle the media.
	 *
	 * Note: This is only applicable if playing a directory, otherwise it will
	 *       be ignored.
	 */
	@ColumnInfo(name = "should_shuffle_media", defaultValue = "0")
	var shuffleMedia: Boolean = false

	/**
	 * Whether to recursively play the media in a directory.
	 *
	 * Note: This is only applicable if playing a directory, otherwise it will
	 *       be ignored.
	 */
	@ColumnInfo(name = "should_recursively_play_media", defaultValue = "0")
	var recursivelyPlayMedia: Boolean = false

	/**
	 * Volume level to set when the alarm is run.
	 */
	@ColumnInfo(name = "volume")
	var volume: Int = 0

	/**
	 * Audio source to use for the media that will play when the alarm is run.
	 */
	@ColumnInfo(name = "audio_source")
	var audioSource: String = ""

	/**
	 * Name of the alarm.
	 */
	@ColumnInfo(name = "name")
	var name: String = ""

	/**
	 * Flag indicating whether to say the current time via text-to-speech when
	 * the alarm goes off.
	 */
	@ColumnInfo(name = "should_say_current_time", defaultValue = "0")
	var sayCurrentTime: Boolean = false

	/**
	 * Flag indicating whether to say the alarm name via text-to-speech when
	 * the alarm goes off.
	 */
	@ColumnInfo(name = "should_say_alarm_name", defaultValue = "0")
	var sayAlarmName: Boolean = false

	/**
	 * Frequency at which to play text-to-speech, in units of minutes.
	 */
	@ColumnInfo(name = "tts_frequency")
	var ttsFrequency: Int = 0

	/**
	 * Flag indicating whether to gradually increase the volume or not, when an
	 * alarm is active.
	 */
	@ColumnInfo(name = "should_gradually_increase_volume")
	var shouldGraduallyIncreaseVolume: Boolean = false

	/**
	 * Amount of time, in seconds, to wait before gradually increasing the
	 * volume another step.
	 */
	@ColumnInfo(name = "gradually_increase_volume_wait_time", defaultValue = "5")
	var graduallyIncreaseVolumeWaitTime: Int = 5

	/**
	 * Flag indicating whether to restrict changing the volume or not, when an
	 * alarm is active.
	 */
	@ColumnInfo(name = "should_restrict_volume")
	var shouldRestrictVolume: Boolean = false

	/**
	 * Time in which to auto dismiss the alarm.
	 */
	@ColumnInfo(name = "auto_dismiss_time", defaultValue = "0")
	var autoDismissTime: Int = 15

	/**
	 * Flag indicating whether or not to use dismiss early.
	 */
	@ColumnInfo(name = "should_dismiss_early")
	var useDismissEarly: Boolean = false

	/**
	 * Amount of time, in minutes, to allow a user to dismiss early by.
	 */
	@ColumnInfo(name = "dismiss_early_time")
	var dismissEarlyTime: Int = 30

	/**
	 * Time of alarm that would have been next but was dismissed early.
	 */
	@ColumnInfo(name = "time_of_dismiss_early_alarm")
	var timeOfDismissEarlyAlarm: Long = 0

	/**
	 * Snooze duration.
	 */
	@ColumnInfo(name = "snooze_duration", defaultValue = "0")
	var snoozeDuration: Int = 5

	/**
	 * Max number of snoozes.
	 */
	@ColumnInfo(name = "max_snooze", defaultValue = "0")
	var maxSnooze: Int = 0

	/**
	 * Whether snoozing is easy or not.
	 */
	@ColumnInfo(name = "should_use_easy_snooze", defaultValue = "0")
	var useEasySnooze: Boolean = false

	/**
	 * Whether to show a reminder or not.
	 */
	@ColumnInfo(name = "should_show_reminder", defaultValue = "0")
	var showReminder: Boolean = false

	/**
	 * The time to start showing a reminder.
	 */
	@ColumnInfo(name = "time_to_show_reminder", defaultValue = "5")
	var timeToShowReminder: Int = 5

	/**
	 * Frequency at which to show the reminder, in units of minutes.
	 */
	@ColumnInfo(name = "reminder_frequency", defaultValue = "0")
	var reminderFrequency: Int = 0

	/**
	 * Whether to use text-to-speech for the reminder.
	 */
	@ColumnInfo(name = "should_use_tts_for_reminder", defaultValue = "0")
	var useTtsForReminder: Boolean = false

	/**
	 * Check if any days are selected.
	 */
	val areDaysSelected: Boolean
		get() = days.isNotEmpty()

	/**
	 * Check if the alarm has a sound that will be played when it goes off.
	 */
	val hasMedia: Boolean
		get() = mediaPath.isNotEmpty()

	/**
	 * Check if the alarm can be snoozed.
	 *
	 * @return True if the alarm can be snoozed, and False otherwise.
	 */
	val canSnooze: Boolean
		get() = (snoozeCount < maxSnooze) || (maxSnooze < 0)

	/**
	 * Check if the alarm is snoozed.
	 */
	val isSnoozed: Boolean
		get() = snoozeCount > 0

	/**
	 * Check if the alarm is being used, by being active or snoozed.
	 */
	val isInUse: Boolean
		get() = isActive || isSnoozed

	/**
	 * The normalized alarm name (with newlines replaced with spaces).
	 */
	val nameNormalized: String
		get()
		{
			return if (name.isNotEmpty())
			{
				name.replace("\n", " ")
			}
			else
			{
				name
			}
		}

	/**
	 * Check if should repeat the alarm after it runs or not.
	 */
	val shouldRepeat: Boolean
		get() = repeat

	/**
	 * Check if the phone should vibrate when the alarm is run.
	 */
	val shouldVibrate: Boolean
		get() = vibrate

	/**
	 * Check if should use NFC or not.
	 */
	val shouldUseNfc: Boolean
		get() = useNfc

	/**
	 * Check if should use TTS or not.
	 */
	val shouldUseTts: Boolean
		get() = sayCurrentTime || sayAlarmName

	/**
	 * Check if should use text-to-speech for the reminder or not.
	 */
	val shouldUseTtsForReminder: Boolean
		get() = shouldUseTts && useTtsForReminder

	/**
	 * Populate values with input parcel.
	 */
	private constructor(input: Parcel) : this()
	{
		// ID
		id = input.readLong()

		// Active alarm flags
		isActive = input.readInt() != 0
		timeActive = input.readLong()
		snoozeCount = input.readInt()

		// Normal stuff
		isEnabled = input.readInt() != 0
		hour = input.readInt()
		minute = input.readInt()
		setDays(input.readInt())
		repeat = input.readInt() != 0
		vibrate = input.readInt() != 0
		useNfc = input.readInt() != 0
		nfcTagId = input.readString() ?: ""
		useFlashlight = input.readInt() != 0
		flashlightStrengthLevel = input.readInt()
		graduallyIncreaseFlashlightStrengthLevelWaitTime = input.readInt()
		shouldBlinkFlashlight = input.readInt() != 0
		flashlightOnDuration = input.readString() ?: ""
		flashlightOffDuration = input.readString() ?: ""

		// Media
		mediaPath = input.readString() ?: ""
		mediaTitle = input.readString() ?: ""
		mediaType = input.readInt()
		shuffleMedia = input.readInt() != 0
		recursivelyPlayMedia = input.readInt() != 0

		// Other normal stuff
		volume = input.readInt()
		audioSource = input.readString() ?: ""
		name = input.readString() ?: ""

		// Text-to-speech
		sayCurrentTime = input.readInt() != 0
		sayAlarmName = input.readInt() != 0
		ttsFrequency = input.readInt()

		// Volume features
		shouldGraduallyIncreaseVolume = input.readInt() != 0
		graduallyIncreaseVolumeWaitTime = input.readInt()
		shouldRestrictVolume = input.readInt() != 0

		// Auto dismiss
		autoDismissTime = input.readInt()

		// Dismiss early
		useDismissEarly = input.readInt() != 0
		dismissEarlyTime = input.readInt()
		timeOfDismissEarlyAlarm = input.readLong()

		// Snooze
		snoozeDuration = input.readInt()
		maxSnooze = input.readInt()
		useEasySnooze = input.readInt() != 0

		// Reminder
		showReminder = input.readInt() != 0
		timeToShowReminder = input.readInt()
		reminderFrequency = input.readInt()
		useTtsForReminder = input.readInt() != 0
	}

	/**
	 * Add to the time, in milliseconds, that the alarm is active.
	 *
	 * @param  time  Time, in milliseconds, to add to the active time.
	 */
	fun addToTimeActive(time: Long)
	{
		timeActive += time
	}

	/**
	 * Compare the next day this alarm will run with another alarm.
	 *
	 * @param  alarm  An alarm.
	 *
	 * @return A negative integer, zero, or a positive integer as this object is
	 * less than, equal to, or greater than the specified object.
	 */
	private fun compareDay(alarm: NacAlarm): Int
	{
		val cal = NacCalendar.getNextAlarmDay(this)
		val otherCal = NacCalendar.getNextAlarmDay(alarm)

		return cal.compareTo(otherCal)
	}

	/**
	 * Compare the in use value in this alarm with another alarm.
	 *
	 *
	 * At least one alarm should be in use, otherwise the comparison is
	 * meaningless.
	 *
	 * @param  alarm  An alarm.
	 *
	 * @return A negative integer, zero, or a positive integer as this object is
	 * less than, equal to, or greater than the specified object.
	 */
	private fun compareInUseValue(alarm: NacAlarm): Int
	{
		val value = computeInUseValue()
		val otherValue = alarm.computeInUseValue()

		return if (otherValue < 0)
		{
			-1
		} else if (value < 0)
		{
			1
		} else if (value == otherValue)
		{
			0
		} else
		{
			if (value < otherValue) -1 else 1
		}
	}

	/**
	 * Compare the time of this alarm with another alarm.
	 *
	 * @param  alarm  An alarm.
	 *
	 * @return A negative integer, zero, or a positive integer as this object is
	 * less than, equal to, or greater than the specified object.
	 */
	private fun compareTime(alarm: NacAlarm): Int
	{
		val locale = Locale.getDefault()
		val format = "%1$02d:%2$02d"
		val time = String.format(locale, format, hour,
			minute)
		val otherTime = String.format(locale, format, alarm.hour,
			alarm.minute)

		return time.compareTo(otherTime)
	}

	/**
	 * Compare this alarm with another alarm.
	 *
	 * @param  other  An alarm.
	 *
	 * @return A negative integer, zero, or a positive integer as this object is
	 * less than, equal to, or greater than the specified object.
	 */
	override fun compareTo(other: NacAlarm): Int
	{
		return if (this.equals(other))
		{
			0
		} else if (isInUse || other.isInUse)
		{
			val value = compareInUseValue(other)
			if (value == 0)
			{
				compareTime(other)
			} else
			{
				value
			}
		} else if (isEnabled xor other.isEnabled)
		{
			if (isEnabled) -1 else 1
		} else
		{
			compareDay(other)
		}
	}

	/**
	 * Get value corresponding to how in use an alarm is. This is used as a
	 * means to easily compare two alarms that are both in use.
	 *
	 * If an alarm is NOT IN USE, return -1.
	 *
	 * If an alarm is ACTIVE AND NOT SNOOZED, return 0.
	 *
	 * If an alarm is ACTIVE AND SNOOZED, return snooze count.
	 *
	 * If an alarm is NOT ACTIVE AND SNOOZED, return 1000 * snooze count.
	 *
	 * @return A value corresponding to how in use an alarm is.
	 */
	private fun computeInUseValue(): Int
	{
		// Check if alarm is not in use
		if (!isInUse)
		{
			return -1
		}

		// Scale the in use value
		val scale = if (isActive) 1 else 1000

		// Compute value by how many times the alarm has been snoozed
		return scale * snoozeCount
	}

	/**
	 * Create a copy of this alarm.
	 *
	 *
	 * The ID of the new alarm will be set to 0.
	 *
	 * @return A copy of this alarm.
	 */
	fun copy(): NacAlarm
	{
		val alarm = build()

		// ID
		alarm.id = 0

		// Normal stuff
		alarm.isEnabled = isEnabled
		alarm.hour = hour
		alarm.minute = minute
		alarm.days = days
		alarm.repeat = shouldRepeat
		alarm.vibrate = shouldVibrate
		alarm.useNfc = shouldUseNfc
		alarm.nfcTagId = nfcTagId
		alarm.useFlashlight = useFlashlight
		alarm.flashlightStrengthLevel = flashlightStrengthLevel
		alarm.graduallyIncreaseFlashlightStrengthLevelWaitTime = graduallyIncreaseFlashlightStrengthLevelWaitTime
		alarm.shouldBlinkFlashlight = shouldBlinkFlashlight
		alarm.flashlightOnDuration = flashlightOnDuration
		alarm.flashlightOffDuration = flashlightOffDuration

		// Media
		alarm.mediaPath = mediaPath
		alarm.mediaTitle = mediaTitle
		alarm.mediaType = mediaType
		alarm.shuffleMedia = shuffleMedia
		alarm.recursivelyPlayMedia = recursivelyPlayMedia

		// Other normal stuff
		alarm.volume = volume
		alarm.audioSource = audioSource
		alarm.name = name

		// Text-to-speech
		alarm.sayCurrentTime = sayCurrentTime
		alarm.sayAlarmName = sayAlarmName
		alarm.ttsFrequency = ttsFrequency

		// Volume features
		alarm.shouldGraduallyIncreaseVolume = shouldGraduallyIncreaseVolume
		alarm.graduallyIncreaseVolumeWaitTime = graduallyIncreaseVolumeWaitTime
		alarm.shouldRestrictVolume = shouldRestrictVolume

		// Auto dismiss
		alarm.autoDismissTime = autoDismissTime

		// Dismiss early
		alarm.useDismissEarly = useDismissEarly
		alarm.dismissEarlyTime = dismissEarlyTime
		alarm.timeOfDismissEarlyAlarm = timeOfDismissEarlyAlarm

		// Snooze
		alarm.snoozeDuration = snoozeDuration
		alarm.maxSnooze = maxSnooze
		alarm.useEasySnooze = useEasySnooze

		// Reminder
		alarm.showReminder = showReminder
		alarm.timeToShowReminder = timeToShowReminder
		alarm.reminderFrequency = reminderFrequency
		alarm.useTtsForReminder = useTtsForReminder

		return alarm
	}

	/**
	 * Describe contents (required for Parcelable).
	 */
	override fun describeContents(): Int
	{
		return 0
	}

	/**
	 * Dismiss an alarm.
	 *
	 *
	 * This will not update the database, or schedule the next alarm. That
	 * still needs to be done after calling this method.
	 */
	fun dismiss()
	{
		isActive = false
		timeActive = 0
		snoozeCount = 0
		snoozeHour = -1
		snoozeMinute = -1

		// Check if the alarm should not be repeated
		if (!shouldRepeat)
		{
			// Toggle the alarm
			toggleAlarm()
		}
	}

	/**
	 * Dismiss an alarm early.
	 */
	fun dismissEarly()
	{
		// Alarm should be repeated
		if (shouldRepeat)
		{
			val cal = NacCalendar.getNextAlarmDay(this)
			val time = cal.timeInMillis

			timeOfDismissEarlyAlarm = time
		}
		// Alarm should only be run once
		else
		{
			toggleAlarm()
		}
	}

	/**
	 * Check if this alarm equals another alarm.
	 *
	 * @param  alarm  An alarm.
	 *
	 * @return True if both alarms are the same, and false otherwise.
	 */
	@Suppress("CovariantEquals")
	fun equals(alarm: NacAlarm?): Boolean
	{
		return (alarm != null)
			&& (this.equalsId(alarm))
			&& (isActive == alarm.isActive)
			&& (timeActive == alarm.timeActive)
			&& (snoozeCount == alarm.snoozeCount)
			&& (isEnabled == alarm.isEnabled)
			&& (hour == alarm.hour)
			&& (minute == alarm.minute)
			&& (days == alarm.days)
			&& (shouldRepeat == alarm.shouldRepeat)
			&& (shouldVibrate == alarm.shouldVibrate)
			&& (shouldUseNfc == alarm.shouldUseNfc)
			&& (useFlashlight == alarm.useFlashlight)
			&& (flashlightStrengthLevel == alarm.flashlightStrengthLevel)
			&& (graduallyIncreaseFlashlightStrengthLevelWaitTime == alarm.graduallyIncreaseFlashlightStrengthLevelWaitTime)
			&& (shouldBlinkFlashlight == alarm.shouldBlinkFlashlight)
			&& (flashlightOnDuration == alarm.flashlightOnDuration)
			&& (flashlightOffDuration == alarm.flashlightOffDuration)
			&& (nfcTagId == alarm.nfcTagId)
			&& (mediaPath == alarm.mediaPath)
			&& (mediaTitle == alarm.mediaTitle)
			&& (mediaType == alarm.mediaType)
			&& (shuffleMedia == alarm.shuffleMedia)
			&& (recursivelyPlayMedia == alarm.recursivelyPlayMedia)
			&& (volume == alarm.volume)
			&& (audioSource == alarm.audioSource)
			&& (name == alarm.name)
			&& (sayCurrentTime == alarm.sayCurrentTime)
			&& (sayAlarmName == alarm.sayAlarmName)
			&& (ttsFrequency == alarm.ttsFrequency)
			&& (shouldGraduallyIncreaseVolume == alarm.shouldGraduallyIncreaseVolume)
			&& (graduallyIncreaseVolumeWaitTime == alarm.graduallyIncreaseVolumeWaitTime)
			&& (shouldRestrictVolume == alarm.shouldRestrictVolume)
			&& (autoDismissTime == alarm.autoDismissTime)
			&& (useDismissEarly == alarm.useDismissEarly)
			&& (dismissEarlyTime == alarm.dismissEarlyTime)
			&& (timeOfDismissEarlyAlarm == alarm.timeOfDismissEarlyAlarm)
			&& (snoozeDuration == alarm.snoozeDuration)
			&& (maxSnooze == alarm.maxSnooze)
			&& (useEasySnooze == alarm.useEasySnooze)
			&& (showReminder == alarm.showReminder)
			&& (timeToShowReminder == alarm.timeToShowReminder)
			&& (reminderFrequency == alarm.reminderFrequency)
			&& (useTtsForReminder == alarm.useTtsForReminder)
	}

	/**
	 * Check if this alarm has the same ID as another alarm.
	 *
	 * @param  alarm  An alarm.
	 *
	 * @return True if both alarms are the same, and false otherwise.
	 */
	fun equalsId(alarm: NacAlarm): Boolean
	{
		return id == alarm.id
	}

	/**
	 * @return The time string.
	 */
	fun getClockTime(context: Context): String
	{
		return NacCalendar.getClockTime(context, hour, minute)
	}

	/**
	 * Get the full time string.
	 *
	 * @return The full time string.
	 */
	fun getFullTime(context: Context): String
	{
		val nextCalendar = NacCalendar.getNextAlarmDay(this)
		val is24HourFormat = DateFormat.is24HourFormat(context)

		return NacCalendar.getFullTime(nextCalendar, is24HourFormat)
	}

	/**
	 * Get the meridian (AM or PM).
	 *
	 * @return The meridian (AM or PM).
	 */
	fun getMeridian(context: Context): String
	{
		return NacCalendar.getMeridian(context, hour)
	}

	/**
	 * @see .getNameNormalized
	 */
	fun getNameNormalizedForMessage(max: Int): String
	{
		val locale = Locale.getDefault()
		val name = nameNormalized

		return if (name.length > max)
		{
			String.format(locale, "%1\$s...", name.substring(0, max - 3))
		}
		else
		{
			name
		}
	}

	/**
	 * Increment the snooze count by 1.
	 */
	private fun incrementSnoozeCount()
	{
		snoozeCount += 1
	}

	/**
	 * Print all values in the alarm object.
	 */
	@Suppress("unused")
	fun print()
	{
		println("Alarm Information")
		println("Id                  : $id")
		println("Is Active           : $isActive")
		println("Time Active         : $timeActive")
		println("Snooze Count        : $snoozeCount")
		println("Is Enabled          : $isEnabled")
		println("Hour                : $hour")
		println("Minute              : $minute")
		println("Days                : $days")
		println("Repeat              : $shouldRepeat")
		println("Vibrate             : $shouldVibrate")
		println("Use NFC             : $shouldUseNfc")
		println("Nfc Tag Id          : $nfcTagId")
		println("Use Flashlight      : $useFlashlight")
		println("Flashlight Strength : $flashlightStrengthLevel")
		println("Grad Inc Flash      : $graduallyIncreaseFlashlightStrengthLevelWaitTime")
		println("Should Blink Flash  : $shouldBlinkFlashlight")
		println("Flashlight On       : $flashlightOnDuration")
		println("Flashlight Off      : $flashlightOffDuration")
		println("Media Path          : $mediaPath")
		println("Media Name          : $mediaTitle")
		println("Media Type          : $mediaType")
		println("Shuffle media       : $shuffleMedia")
		println("Recusively Play     : $recursivelyPlayMedia")
		println("Volume              : $volume")
		println("Audio Source        : $audioSource")
		println("Name                : $name")
		println("Tts say time        : $sayCurrentTime")
		println("Tts say name        : $sayAlarmName")
		println("Tts Freq            : $ttsFrequency")
		println("Grad Inc Vol        : $shouldGraduallyIncreaseVolume")
		println("Grad Inc Vol Wait T : $graduallyIncreaseVolumeWaitTime")
		println("Restrict Vol        : $shouldRestrictVolume")
		println("Auto Dismiss        : $autoDismissTime")
		println("Use Dismiss Early   : $useDismissEarly")
		println("Dismiss Early       : $dismissEarlyTime")
		println("Time of Early Alarm : $timeOfDismissEarlyAlarm")
		println("Snooze Duration     : $snoozeDuration")
		println("Max Snooze          : $maxSnooze")
		println("Use Easy Snooze     : $useEasySnooze")
		println("Show Reminder       : $showReminder")
		println("Time to show remind : $timeToShowReminder")
		println("Reminder freq       : $reminderFrequency")
		println("Use Tts 4 Reminder  : $useTtsForReminder")
	}

	/**
	 * @see .setDays
	 */
	fun setDays(value: Int)
	{
		days = Day.valueToDays(value)
	}

	/**
	 * Set the sound to play when the alarm goes off.
	 *
	 * @param  context  The application context.
	 * @param  path  The path to sound to play.
	 */
	fun setMedia(context: Context, path: String)
	{
		// TODO: Set shuffle and recurse here?
		val title = NacMedia.getTitle(context, path)
		val type = NacMedia.getType(context, path)

		mediaPath = path
		mediaTitle = title
		mediaType = type
	}

	/**
	 * Snooze the alarm.
	 *
	 * @return Calendar instance of when the snoozed alarm will go off.
	 */
	fun snooze(): Calendar
	{
		// Reset the active flag
		isActive = false

		// Add the snooze duration value to the current time
		val cal = Calendar.getInstance()
		cal.add(Calendar.MINUTE, snoozeDuration)

		// Set the snooze hour and minute
		snoozeHour = cal[Calendar.HOUR_OF_DAY]
		snoozeMinute = cal[Calendar.MINUTE]

		// Increment the snooze count. The "isSnoozed" variable checks the
		// snooze count so this basically makes "isSnoozed" true
		incrementSnoozeCount()

		return cal
	}

	/**
	 * Toggle the the current day/enabled attribute of the alarm.
	 *
	 * An alarm can only be toggled if repeat is not enabled.
	 */
	private fun toggleAlarm()
	{
		// Check if the alarm should be repeated
		if (shouldRepeat)
		{
			return
		}

		// Check if there are any days selected
		if (areDaysSelected)
		{
			toggleToday()
		}
		// No days selected
		else
		{
			isEnabled = false
		}
	}

	/**
	 * Toggle a day.
	 */
	fun toggleDay(day: Day)
	{
		// Check if day is contained in the list of alarm days
		if (days.contains(day))
		{
			days.remove(day)
		}
		// Day is not present in alarm days
		else
		{
			days.add(day)
		}
	}

	/**
	 * Toggle repeat.
	 */
	fun toggleRepeat()
	{
		repeat = !shouldRepeat
	}

	/**
	 * Toggle today.
	 */
	private fun toggleToday()
	{
		// Get today's day
		val day = Day.TODAY

		// Toggle today
		toggleDay(day)
	}

	/**
	 * Toggle use the flashlight.
	 */
	fun toggleUseFlashlight()
	{
		useFlashlight = !useFlashlight
	}

	/**
	 * Toggle use NFC.
	 */
	fun toggleUseNfc()
	{
		useNfc = !shouldUseNfc
	}

	/**
	 * Toggle vibrate.
	 */
	fun toggleVibrate()
	{
		vibrate = !shouldVibrate
	}

	/** Whether or not the alarm will alarm soon.
	 *
	 *
	 * "Soon" is determined by the dismiss early time. If it will alarm within
	 * that time, then it is soon.
	 */
	fun willAlarmSoon(): Boolean
	{
		// Alarm is disabled or unable to use dismiss early
		if (!isEnabled || !useDismissEarly || dismissEarlyTime == 0)
		{
			return false
		}

		// Determine the difference in time between the next alarm and today
		val today = Calendar.getInstance()
		val cal = NacCalendar.getNextAlarmDay(this)
		val diff = (cal.timeInMillis - today.timeInMillis) / 1000 / 60

		// Compare the two amounts of time
		return (diff < dismissEarlyTime)
	}

	/**
	 * Write data into parcel (required for Parcelable).
	 *
	 * Update this when adding/removing an element.
	 */
	override fun writeToParcel(output: Parcel, flags: Int)
	{
		// ID
		output.writeLong(id)

		// Active alarm flags
		output.writeInt(if (isActive) 1 else 0)
		output.writeLong(timeActive)
		output.writeInt(snoozeCount)

		// Normal stuff
		output.writeInt(if (isEnabled) 1 else 0)
		output.writeInt(hour)
		output.writeInt(minute)
		output.writeInt(Day.daysToValue(days))
		output.writeInt(if (shouldRepeat) 1 else 0)
		output.writeInt(if (shouldVibrate) 1 else 0)
		output.writeInt(if (shouldUseNfc) 1 else 0)
		output.writeString(nfcTagId)
		output.writeInt(if (useFlashlight) 1 else 0)
		output.writeInt(flashlightStrengthLevel)
		output.writeInt(graduallyIncreaseFlashlightStrengthLevelWaitTime)
		output.writeInt(if (shouldBlinkFlashlight) 1 else 0)
		output.writeString(flashlightOnDuration)
		output.writeString(flashlightOffDuration)

		// Media
		output.writeString(mediaPath)
		output.writeString(mediaTitle)
		output.writeInt(mediaType)
		output.writeInt(if (shuffleMedia) 1 else 0)
		output.writeInt(if (recursivelyPlayMedia) 1 else 0)

		// Other normal stuff
		output.writeInt(volume)
		output.writeString(audioSource)
		output.writeString(name)

		// Text-to-speech
		output.writeInt(if (sayCurrentTime) 1 else 0)
		output.writeInt(if (sayAlarmName) 1 else 0)
		output.writeInt(ttsFrequency)

		// Volume features
		output.writeInt(if (shouldGraduallyIncreaseVolume) 1 else 0)
		output.writeInt(graduallyIncreaseVolumeWaitTime)
		output.writeInt(if (shouldRestrictVolume) 1 else 0)

		// Auto dismiss
		output.writeInt(autoDismissTime)

		// Dismiss early
		output.writeInt(if (useDismissEarly) 1 else 0)
		output.writeInt(dismissEarlyTime)
		output.writeLong(timeOfDismissEarlyAlarm)

		// Snooze
		output.writeInt(snoozeDuration)
		output.writeInt(maxSnooze)
		output.writeInt(if (useEasySnooze) 1 else 0)

		// Reminder
		output.writeInt(if (showReminder) 1 else 0)
		output.writeInt(timeToShowReminder)
		output.writeInt(reminderFrequency)
		output.writeInt(if (useTtsForReminder) 1 else 0)
	}

	companion object
	{

		/**
		 * Generate parcel (required for Parcelable).
		 */
		@Suppress("unused")
		@JvmField
		val CREATOR: Parcelable.Creator<NacAlarm> = object : Parcelable.Creator<NacAlarm>
		{
			override fun createFromParcel(input: Parcel): NacAlarm
			{
				return NacAlarm(input)
			}

			override fun newArray(size: Int): Array<NacAlarm?>
			{
				return arrayOfNulls(size)
			}
		}

		/**
		 * Build an alarm.
		 */
		fun build(shared: NacSharedPreferences? = null): NacAlarm
		{
			val alarm = NacAlarm()
			val calendar = Calendar.getInstance()

			// Normal stuff
			alarm.isEnabled = true
			alarm.hour = calendar[Calendar.HOUR_OF_DAY]
			alarm.minute = calendar[Calendar.MINUTE]
			alarm.days = if (shared != null) Day.valueToDays(shared.days) else Day.NONE
			alarm.repeat = shared?.shouldRepeat ?: false
			alarm.vibrate = shared?.shouldVibrate ?: false
			alarm.useNfc = shared?.shouldUseNfc ?: false
			alarm.nfcTagId = ""
			alarm.useFlashlight = shared?.shouldUseFlashlight ?: false
			alarm.flashlightStrengthLevel = shared?.flashlightStrengthLevel ?: 0
			alarm.graduallyIncreaseFlashlightStrengthLevelWaitTime = shared?.graduallyIncreaseFlashlightStrengthLevelWaitTime ?: 0
			alarm.shouldBlinkFlashlight = shared?.shouldBlinkFlashlight ?: false
			alarm.flashlightOnDuration = shared?.flashlightOnDuration ?: ""
			alarm.flashlightOffDuration = shared?.flashlightOffDuration ?: ""

			// Media
			alarm.mediaPath = shared?.mediaPath ?: ""
			alarm.mediaTitle = ""
			alarm.mediaType = NacMedia.TYPE_NONE
			alarm.shuffleMedia = shared?.shouldShuffleMedia ?: false
			alarm.recursivelyPlayMedia = shared?.recursivelyPlayMedia ?: false

			// Other normal stuff
			alarm.volume = shared?.volume ?: 0
			alarm.audioSource = shared?.audioSource ?: ""
			alarm.name = shared?.name ?: ""

			// Text-to-speech
			alarm.sayCurrentTime = shared?.shouldSayCurrentTime ?: false
			alarm.sayAlarmName = shared?.shouldSayAlarmName ?: false
			alarm.ttsFrequency = shared?.ttsFrequency ?: 0

			// Volume features
			alarm.shouldGraduallyIncreaseVolume = shared?.shouldGraduallyIncreaseVolume ?: false
			alarm.graduallyIncreaseVolumeWaitTime = shared?.graduallyIncreaseVolumeWaitTime ?: 5
			alarm.shouldRestrictVolume = shared?.shouldRestrictVolume ?: false

			// Auto dismiss
			alarm.autoDismissTime = shared?.autoDismissTime ?: 15

			// Dismiss early
			alarm.useDismissEarly = shared?.canDismissEarly ?: false
			alarm.dismissEarlyTime = shared?.dismissEarlyTime ?: 30
			alarm.timeOfDismissEarlyAlarm = 0

			// Snooze
			alarm.snoozeDuration = shared?.snoozeDuration ?: 5
			alarm.maxSnooze = shared?.maxSnooze ?: 0
			alarm.useEasySnooze = shared?.easySnooze ?: false

			// Reminder
			alarm.showReminder = shared?.shouldShowReminder ?: false
			alarm.timeToShowReminder = shared?.timeToShowReminder ?: 5
			alarm.reminderFrequency = shared?.reminderFrequency ?: 0
			alarm.useTtsForReminder = shared?.shouldUseTtsForReminder ?: false

			return alarm
		}

		/**
		 * Calculate the auto dismiss time from an index.
		 */
		fun calcAutoDismissTime(index: Int): Int
		{
			return if (index < 5)
			{
				index
			}
			else
			{
				(index - 4) * 5
			}
		}

		/**
		 * Calculate the auto dismiss index from a value.
		 */
		fun calcAutoDismissIndex(time: Int): Int
		{
			return if (time < 5)
			{
				time
			}
			else
			{
				time / 5 + 4
			}
		}

		/**
		 * Calculate the dismiss early index from a time.
		 */
		fun calcDismissEarlyIndex(time: Int): Int
		{
			return if (time <= 5)
			{
				time - 1
			}
			else
			{
				time / 5 + 3
			}
		}

		/**
		 * Calculate the dismiss early time from an index.
		 */
		fun calcDismissEarlyTime(index: Int): Int
		{
			return if (index < 5)
			{
				index + 1
			}
			else
			{
				(index - 3) * 5
			}
		}

		/**
		 * Calculate the flashlight on/off duration.
		 */
		fun calcFlashlightOnOffDuration(index: Int): String
		{
			return when (index)
			{
				0    -> "0.5"
				1    -> "1.0"
				2    -> "1.5"
				3    -> "2.0"
				4    -> "2.5"
				5    -> "3.0"
				6    -> "3.5"
				7    -> "4.0"
				8    -> "4.5"
				9    -> "5.0"
				10   -> "5.5"
				11   -> "6.0"
				12   -> "6.5"
				13   -> "7.0"
				14   -> "7.5"
				15   -> "8.0"
				16   -> "8.5"
				17   -> "9.0"
				18   -> "9.5"
				19   -> "10.0"
				else -> "1.0"
			}
		}

		/**
		 * Calculate the flashlight on/off duration index.
		 */
		fun calcFlashlightOnOffDurationIndex(duration: String): Int
		{
			return when (duration)
			{
				"0.5"  -> 0
				"1.0"  -> 1
				"1.5"  -> 2
				"2.0"  -> 3
				"2.5"  -> 4
				"3.0"  -> 5
				"3.5"  -> 6
				"4.0"  -> 7
				"4.5"  -> 8
				"5.0"  -> 9
				"5.5"  -> 10
				"6.0"  -> 11
				"6.5"  -> 12
				"7.0"  -> 13
				"7.5"  -> 14
				"8.0"  -> 15
				"8.5"  -> 16
				"9.0"  -> 17
				"9.5"  -> 18
				"10.0" -> 19
				else   -> 1
			}
		}

		/**
		 * Calculate the gradually increase volume index from a time.
		 */
		fun calcGraduallyIncreaseVolumeIndex(time: Int): Int
		{
			return if (time <= 10)
			{
				time - 1
			}
			else
			{
				time / 5 + 7
			}
		}

		/**
		 * Calculate the gradually increase volume wait time from an index.
		 */
		fun calcGraduallyIncreaseVolumeWaitTime(index: Int): Int
		{
			return if (index < 10)
			{
				index + 1
			}
			else
			{
				(index - 7) * 5
			}
		}

		/**
		 * Calculate the max snooze value from an index.
		 */
		fun calcMaxSnooze(index: Int): Int
		{
			return if (index == 11)
			{
				-1
			}
			else
			{
				index
			}
		}

		/**
		 * Calculate the max snooze index from a value.
		 */
		fun calcMaxSnoozeIndex(value: Int): Int
		{
			return if (value == -1)
			{
				11
			}
			else
			{
				value
			}
		}

		/**
		 * Calculate the snooze duration value from an index.
		 */
		fun calcSnoozeDuration(index: Int): Int
		{
			return if (index < 9)
			{
				index + 1
			}
			else
			{
				(index-7) * 5
			}
		}

		/**
		 * Calculate the snooze duration index from a value.
		 */
		fun calcSnoozeDurationIndex(value: Int): Int
		{
			return if (value < 9)
			{
				value - 1
			}
			else
			{
				value / 5 + 7
			}
		}

		/**
		 * Calculate the upcoming reminder time to show index from a time.
		 */
		fun calcUpcomingReminderTimeToShowIndex(time: Int): Int
		{
			return if (time == 0)
			{
				4
			}
			else if (time <= 10)
			{
				time - 1
			}
			else
			{
				time/5 + 7
			}
		}

		/**
		 * Calculate the upcoming reminder time to show from an index.
		 */
		fun calcUpcomingReminderTimeToShow(index: Int): Int
		{
			return if (index < 10)
			{
				index + 1
			}
			else
			{
				(index-7) * 5
			}
		}

	}

}

/**
 * Hilt module to provide an instance of an alarm.
 */
@InstallIn(SingletonComponent::class)
@Module
class NacAlarmModule
{

	/**
	 * Provide an instance of an alarm.
	 */
	@Provides
	fun provideAlarm() : NacAlarm
	{
		return NacAlarm.build()
	}

}
