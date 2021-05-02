package com.nfcalarmclock;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;
import java.lang.Comparable;
import java.util.Calendar;
import java.util.EnumSet;
import java.util.Locale;

/**
 * Aspects of a climbing problem that are saved.
 *
 * TODO: Should I put the snooze counter as part of the alarm, in the database?
 */
@Entity(tableName="alarm")
public class NacAlarm
	implements Comparable<NacAlarm>,
		Parcelable
{

	/**
	 * Unique alarm ID.
	 */
	//@PrimaryKey
	@PrimaryKey(autoGenerate=true)
	@ColumnInfo(name="id")
	private long mId;

	/**
	 * Number of times the alarm has been snoozed.
	 */
	@ColumnInfo(name="snooze_count")
	private int mSnoozeCount;

	/**
	 * Flag indicating whether the alarm is currently active or not.
	 */
	@ColumnInfo(name="is_active")
	private boolean mIsActive;

	/**
	 * Flag indicating whether the alarm is enabled or not.
	 */
	@ColumnInfo(name="is_enabled")
	private boolean mIsEnabled;

	/**
	 * Hour at which to run the alarm.
	 */
	@ColumnInfo(name="hour")
	private int mHour;

	/**
	 * Minute at which to run the alarm.
	 */
	@ColumnInfo(name="minute")
	private int mMinute;

	/**
	 * Days on which to run the alarm.
	 */
	@ColumnInfo(name="days")
	private EnumSet<NacCalendar.Day> mDays;

	/**
	 * Flag indicating whether the alarm should be repeated or not.
	 */
	@ColumnInfo(name="should_repeat")
	private boolean mRepeat;

	/**
	 * Flag indicating whether the alarm should vibrate the phone or not.
	 */
	@ColumnInfo(name="should_vibrate")
	private boolean mVibrate;

	/**
	 * Flag indicating whether the alarm should use NFC or not.
	 */
	@ColumnInfo(name="should_use_nfc")
	private boolean mUseNfc;

	/**
	 * ID of the NFC tag that needs to be used to dismiss the alarm.
	 */
	@ColumnInfo(name="nfc_tag_id")
	private String mNfcTagId;

	/**
	 * Type of media.
	 *
	 * TODO: Do I need this? Might need it for Spotify.
	 */
	@ColumnInfo(name="media_type")
	private int mMediaType;

	/**
	 * Path to the media that will play when the alarm is run.
	 */
	@ColumnInfo(name="media_path")
	private String mMediaPath;

	/**
	 * Title of the media that will play when the alarm is run.
	 */
	@ColumnInfo(name="media_title")
	private String mMediaTitle;

	/**
	 * Volume level to set when the alarm is run.
	 */
	@ColumnInfo(name="volume")
	private int mVolume;

	/**
	 * Audio source to use for the media that will play when the alarm is run.
	 */
	@ColumnInfo(name="audio_source")
	private String mAudioSource;

	/**
	 * Name of the alarm.
	 */
	@ColumnInfo(name="name")
	private String mName;

	/**
	 * Flag indicating whether text-to-speech should be used or not.
	 */
	@ColumnInfo(name="should_use_tts")
	private boolean mUseTts;

	/**
	 * Frequency at which to play text-to-speech, in units of min.
	 */
	@ColumnInfo(name="tts_frequency")
	private int mTtsFrequency;

	/**
	 * Helper to build an alarm.
	 */
	public static class Builder
	{

		/**
		 * Alarm object that will be built.
		 */
		private NacAlarm mAlarm;

		/**
		 */
		public Builder()
		{
			this.mAlarm = new NacAlarm();
			Calendar calendar = Calendar.getInstance();

			this.setIsActive(false)
				.setSnoozeCount(0)
				.setIsEnabled(true)
				.setHour(calendar.get(Calendar.HOUR_OF_DAY))
				.setMinute(calendar.get(Calendar.MINUTE))
				.setDays(NacCalendar.Day.none())
				.setRepeat(false)
				.setVibrate(false)
				.setUseNfc(false)
				.setNfcTagId("")
				.setMediaType(NacMedia.TYPE_NONE)
				.setMediaPath("")
				.setMediaTitle("")
				.setVolume(0)
				.setAudioSource("Media")
				.setName("");
		}

		/**
		 */
		public Builder(NacSharedPreferences shared)
		{
			this();

			if (shared != null)
			{
				NacSharedConstants cons = shared.getConstants();
				NacSharedDefaults defaults = shared.getDefaults();

				this.setDays(NacCalendar.Days.valueToDays(defaults.getDays()))
					.setRepeat(defaults.getRepeat())
					.setVibrate(defaults.getVibrate())
					.setUseNfc(defaults.getUseNfc())
					.setVolume(defaults.getVolume())
					.setAudioSource(cons.getAudioSources().get(1));
				// TODO: Default name?
			}
		}

		/**
		 * Build the alarm.
		 */
		public NacAlarm build()
		{
			return this.getAlarm();
		}

		/**
		 * @return The alarm.
		 */
		private NacAlarm getAlarm()
		{
			return this.mAlarm;
		}

		/**
		 * Set the audio source.
		 *
		 * @param  source  The audio source.
		 *
		 * @return The Builder.
		 */
		public Builder setAudioSource(String source)
		{
			this.getAlarm().setAudioSource(source);
			return this;
		}

		/**
		 * Set the days to the run the alarm.
		 *
		 * @param  days  The set of days to run the alarm on.
		 *
		 * @return The Builder.
		 */
		public Builder setDays(EnumSet<NacCalendar.Day> days)
		{
			this.getAlarm().setDays(days);
			return this;
		}

		/**
		 * @see #setDays(EnumSet)
		 */
		public Builder setDays(int value)
		{
			return this.setDays(NacCalendar.Days.valueToDays(value));
		}

		/**
		 * Set the hour.
		 *
		 * @param  hour  The hour at which to run the alarm.
		 *
		 * @return The Builder.
		 */
		public Builder setHour(int hour)
		{
			this.getAlarm().setHour(hour);
			return this;
		}

		/**
		 * Set the alarm ID.
		 *
		 * @param  id  The alarm id.
		 *
		 * @return The Builder.
		 */
		public Builder setId(long id)
		{
			this.getAlarm().setId(id);
			return this;
		}

		/**
		 * Set whether the alarm is active or not.
		 *
		 * @param  active  The active flag.
		 *
		 * @return The Builder.
		 */
		public Builder setIsActive(boolean active)
		{
			this.getAlarm().setIsActive(active);
			return this;
		}

		/**
		 * Set whether the alarm is enabled or not.
		 *
		 * @param  enabled  True if the alarm is enabled and False otherwise.
		 *
		 * @return The Builder.
		 */
		public Builder setIsEnabled(boolean enabled)
		{
			this.getAlarm().setIsEnabled(enabled);
			return this;
		}

		/**
		 * Set the path, name, and type of the sound to play.
		 *
		 * @param  context  The application context.
		 * @param  path  The path to the sound to play.
		 *
		 * @return The Builder.
		 */
		public Builder setMedia(Context context, String path)
		{
			int type = NacMedia.getType(context, path);
			String title = NacMedia.getTitle(context, path);

			this.setMediaType(type);
			this.setMediaPath(path);
			this.setMediaTitle(title);
			return this;
		}

		/**
		 * Set the media title.
		 *
		 * @param  title  The title of the media file to play.
		 *
		 * @return The Builder.
		 */
		public Builder setMediaTitle(String title)
		{
			this.getAlarm().setMediaTitle(title);
			return this;
		}

		/**
		 * Set the sound to play when the alarm goes off.
		 *
		 * @param  path  The path to the media file to play when the alarm goes
		 *               off.
		 *
		 * @return The Builder.
		 */
		public Builder setMediaPath(String path)
		{
			this.getAlarm().setMediaPath(path);
			return this;
		}

		/**
		 * Set the type of sound to play.
		 *
		 * @param  type  The type of media file to play.
		 *
		 * @return The Builder.
		 */
		public Builder setMediaType(int type)
		{
			this.getAlarm().setMediaType(type);
			return this;
		}

		/**
		 * Set the minute.
		 *
		 * @param  minute  The minute at which to run the alarm.
		 *
		 * @return The Builder.
		 */
		public Builder setMinute(int minute)
		{
			this.getAlarm().setMinute(minute);
			return this;
		}

		/**
		 * Set the name of the alarm.
		 *
		 * @param  name  The alarm name.
		 *
		 * @return The Builder.
		 */
		public Builder setName(String name)
		{
			this.getAlarm().setName(name);
			return this;
		}

		/**
		 * Set the NFC tag ID of the tag that will be used to dismiss the alarm.
		 *
		 * @param  tagId  The ID of the NFC tag.
		 *
		 * @return The Builder.
		 */
		public Builder setNfcTagId(String tagId)
		{
			this.getAlarm().setNfcTagId(tagId);
			return this;
		}

		/**
		 * Set whether the alarm should repeat every week or not.
		 *
		 * @param  repeat  True if repeating the alarm after it runs, and False
		 *                 otherwise.
		 *
		 * @return The Builder.
		 */
		public Builder setRepeat(boolean repeat)
		{
			this.getAlarm().setRepeat(repeat);
			return this;
		}

		/**
		 * Set the snooze count.
		 *
		 * @param  count  The snooze count.
		 *
		 * @return The Builder.
		 */
		public Builder setSnoozeCount(int count)
		{
			this.getAlarm().setSnoozeCount(count);
			return this;
		}

		/**
		 * Set whether the alarm should use NFC to dismiss or not.
		 *
		 * @param  useNfc  True if the phone should use NFC to dismiss, and False
		 *                 otherwise.
		 *
		 * @return The Builder.
		 */
		public Builder setUseNfc(boolean useNfc)
		{
			this.getAlarm().setUseNfc(useNfc);
			return this;
		}

		/**
		 * Set whether the alarm should vibrate the phone or not.
		 *
		 * @param  vibrate  True if the phone should vibrate when the alarm is
		 *                  going off and false otherwise.
		 *
		 * @return The Builder.
		 */
		public Builder setVibrate(boolean vibrate)
		{
			this.getAlarm().setVibrate(vibrate);
			return this;
		}

		/**
		 * Set the volume level.
		 *
		 * @param  volume  The volume level.
		 *
		 * @return The Builder.
		 */
		public Builder setVolume(int volume)
		{
			this.getAlarm().setVolume(volume);
			return this;
		}

	}

	/**
	 */
	public NacAlarm()
	{
	}

	/**
	 * Populate values with input parcel.
	 */
	private NacAlarm(Parcel input)
	{
		this();
		this.setId(input.readLong());
		this.setIsActive(input.readInt() != 0);
		this.setSnoozeCount(input.readInt());
		this.setIsEnabled((input.readInt() != 0));
		this.setHour(input.readInt());
		this.setMinute(input.readInt());
		this.setDays(input.readInt());
		this.setRepeat((input.readInt() != 0));
		this.setVibrate((input.readInt() != 0));
		this.setUseNfc((input.readInt() != 0));
		this.setNfcTagId(input.readString());
		this.setMediaType(input.readInt());
		this.setMediaPath(input.readString());
		this.setMediaTitle(input.readString());
		this.setVolume(input.readInt());
		this.setAudioSource(input.readString());
		this.setName(input.readString());
	}

	///**
	// * TODO: Change this. Maybe don't need this method if can be done manually?
	// *
	// * @return True if the alarm can be snoozed, and False otherwise.
	// */
	//public boolean canSnooze()
	//{
	//	int snoozeCount = this.getSnoozeCount() + 1;
	//	int maxSnoozeCount = shared.getMaxSnoozeValue();

	//	return (snoozeCount <= maxSnoozeCount) || (maxSnoozeCount < 0);
	//	//return (snoozeCount > maxSnoozeCount) && (maxSnoozeCount >= 0);
	//}

	/**
	 * @return True if the alarm can be snoozed, and False otherwise.
	 */
	public boolean canSnooze(NacSharedPreferences shared)
	{
		int snoozeCount = this.getSnoozeCount(shared) + 1;
		int maxSnoozeCount = shared.getMaxSnoozeValue();

		return (snoozeCount <= maxSnoozeCount) || (maxSnoozeCount < 0);
		//return (snoozeCount > maxSnoozeCount) && (maxSnoozeCount >= 0);
	}

	/**
	 * @return True if any days are selected, and False otherwise.
	 */
	public boolean areDaysSelected()
	{
		return !this.getDays().isEmpty();
	}

	/**
	 * Compare the next day this alarm will run with another alarm.
	 *
	 * @param  alarm  An alarm.
	 *
	 * @return A negative integer, zero, or a positive integer as this object is
	 *     less than, equal to, or greater than the specified object.
	 */
	public int compareDay(NacAlarm alarm)
	{
		Calendar cal = NacCalendar.getNextAlarmDay(this);
		Calendar otherCal = NacCalendar.getNextAlarmDay(alarm);

		return cal.compareTo(otherCal);
	}

	/**
	 * Compare the in use value in this alarm with another alarm.
	 *
	 * At least one alarm should be in use, otherwise the comparison is
	 * meaningless.
	 *
	 * @param  alarm  An alarm.
	 *
	 * @return A negative integer, zero, or a positive integer as this object is
	 *     less than, equal to, or greater than the specified object.
	 */
	public int compareInUseValue(NacAlarm alarm)
	{
		int value = this.computeInUseValue();
		int otherValue = alarm.computeInUseValue();

		if (otherValue < 0)
		{
			return -1;
		}
		else if (value < 0)
		{
			return 1;
		}
		else if (value == otherValue)
		{
			return 0;
		}
		else
		{
			return (value < otherValue) ? -1 : 1;
		}
	}

	/**
	 * Compare the time of this alarm with another alarm.
	 *
	 * @param  alarm  An alarm.
	 *
	 * @return A negative integer, zero, or a positive integer as this object is
	 *     less than, equal to, or greater than the specified object.
	 */
	public int compareTime(NacAlarm alarm)
	{
		Locale locale = Locale.getDefault();
		String format = "%1$02d:%2$02d";
		String time = String.format(locale, format, this.getHour(),
			this.getMinute());
		String otherTime = String.format(locale, format, alarm.getHour(),
			alarm.getMinute());

		return time.compareTo(otherTime);
	}

	/**
	 * Compare this alarm with another alarm.
	 *
	 * @param  alarm  An alarm.
	 *
	 * @return A negative integer, zero, or a positive integer as this object is
	 *     less than, equal to, or greater than the specified object.
	 */
	public int compareTo(NacAlarm alarm)
	{
		if (this.equals(alarm))
		{
			return 0;
		}
		else if (this.isInUse() || alarm.isInUse())
		{
			int value = this.compareInUseValue(alarm);

			if (value == 0)
			{
				return this.compareTime(alarm);
			}
			else
			{
				return value;
			}
		}
		else if (this.isEnabled() ^ alarm.isEnabled())
		{
			return this.isEnabled() ? -1 : 1;
		}
		else
		{
			return this.compareDay(alarm);
		}
	}

	/**
	 * @return A value corresponding to how in use an alarm is. This is used as a
	 *     means to easily compare two alarms that are both in use.
	 *
	 *     If an alarm is NOT IN USE, return -1.
	 *
	 *     If an alarm is ACTIVE AND NOT SNOOZED, return 0.
	 *
	 *     If an alarm is ACTIVE AND SNOOZED, return snooze count.
	 *
	 *     If an alarm is NOT ACTIVE AND SNOOZED, return 1000 * snooze count.
	 */
	public int computeInUseValue()
	{
		if (!this.isInUse())
		{
			return -1;
		}

		int scale = this.isActive() ? 1 : 1000;
		return scale*this.getSnoozeCount();
	}

	/**
	 * Create a copy of this alarm with the given ID.
	 *
	 * TODO: Change ID to set to 0.
	 *
	 * @return A copy of this alarm.
	 *
	 * @param  id  The ID of the created alarm.
	 */
	public NacAlarm copy(long id)
	{
		return new NacAlarm.Builder()
			.setId(id)
			.setIsActive(this.isActive())
			.setSnoozeCount(this.getSnoozeCount())
			.setIsEnabled(this.isEnabled())
			.setHour(this.getHour())
			.setMinute(this.getMinute())
			.setDays(this.getDays())
			.setRepeat(this.shouldRepeat())
			.setVibrate(this.shouldVibrate())
			.setUseNfc(this.shouldUseNfc())
			.setNfcTagId(this.getNfcTagId())
			.setMediaType(this.getMediaType())
			.setMediaPath(this.getMediaPath())
			.setMediaTitle(this.getMediaTitle())
			.setVolume(this.getVolume())
			.setAudioSource(this.getAudioSource())
			.setName(this.getName())
			.build();
	}

	/**
	 * Describe contents (required for Parcelable).
	 */
	@Override
	public int describeContents()
	{
		return 0;
	}

	/**
	 * Check if this alarm equals another alarm.
	 *
	 * @param  alarm  An alarm.
	 *
	 * @return True if both alarms are the same, and false otherwise.
	 */
	public boolean equals(NacAlarm alarm)
	{
		return (alarm != null)
			&& (this.equalsId(alarm))
			&& (this.isActive() == alarm.isActive())
			&& (this.isEnabled() == alarm.isEnabled())
			&& (this.getHour() == alarm.getHour())
			&& (this.getMinute() == alarm.getMinute())
			&& (this.getDays().equals(alarm.getDays()))
			&& (this.shouldRepeat() == alarm.shouldRepeat())
			&& (this.shouldVibrate() == alarm.shouldVibrate())
			&& (this.shouldUseNfc() == alarm.shouldUseNfc())
			&& (this.getNfcTagId().equals(alarm.getNfcTagId()))
			&& (this.getMediaType() == alarm.getMediaType())
			&& (this.getMediaPath().equals(alarm.getMediaPath()))
			&& (this.getMediaTitle().equals(alarm.getMediaTitle()))
			&& (this.getVolume() == alarm.getVolume())
			&& (this.getAudioSource().equals(alarm.getAudioSource()))
			&& (this.getName().equals(alarm.getName()));
	}

	/**
	 * Check if this alarm has the same ID as another alarm.
	 *
	 * @param  alarm  An alarm.
	 *
	 * @return True if both alarms are the same, and false otherwise.
	 */
	public boolean equalsId(NacAlarm alarm)
	{
		return this.getId() == alarm.getId();
	}

	/**
	 * @return The audio source.
	 */
	public String getAudioSource()
	{
		return this.mAudioSource;
	}

	/**
	 * @return The time string.
	 */
	public String getClockTime(Context context)
	{
		int hour = this.getHour();
		int minute = this.getMinute();
		return NacCalendar.Time.getClockTime(context, hour, minute);
	}

	/**
	 * @return The days on which to run the alarm.
	 */
	public EnumSet<NacCalendar.Day> getDays()
	{
		return this.mDays;
	}

	/**
	 * @return The full time string.
	 */
	public String getFullTime(Context context)
	{
		Calendar next = NacCalendar.getNextAlarmDay(this);
		return NacCalendar.Time.getFullTime(context, next);
	}

	/**
	 * @return The hour.
	 */
	public int getHour()
	{
		return this.mHour;
	}

	/**
	 * @return The alarm ID.
	 */
	public long getId()
	{
		return this.mId;
	}

	/**
	 * @return The title of the media file.
	 */
	public String getMediaTitle()
	{
		return this.mMediaTitle;
	}

	/**
	 * @return The path to the media file to play when the alarm goes off.
	 */
	public String getMediaPath()
	{
		return this.mMediaPath;
	}

	/**
	 * @return The path to the media file to play when the alarm goes off.
	 */
	public int getMediaType()
	{
		return this.mMediaType;
	}

	/**
	 * @return The meridian (AM or PM).
	 */
	public String getMeridian(Context context)
	{
		int hour = this.getHour();
		return NacCalendar.Time.getMeridian(context, hour);
	}

	/**
	 * @return The minute.
	 */
	public int getMinute()
	{
		return this.mMinute;
	}

	/**
	 * @return The alarm name.
	 */
	public String getName()
	{
		String name = this.mName;
		return (name != null) ? name : "";
	}

	/**
	 * @return The normalized alarm name (with newlines replaced with spaces).
	 */
	public String getNameNormalized()
	{
		String name = this.getName();
		return !name.isEmpty() ? name.replace("\n", " ") : name;
	}

	/**
	 * @see #getNameNormalized()
	 */
	public String getNameNormalizedForMessage(int max)
	{
		String name = this.getNameNormalized();
		Locale locale = Locale.getDefault();
		return (name.length() > max) ?
			String.format(locale, "%1$s...", name.substring(0, max-3)) : name;
	}

	/**
	 * @return The alarm name.
	 */
	public String getNfcTagId()
	{
		String tagId = this.mNfcTagId;
		return (tagId != null) ? tagId : "";
	}

	/**
	 * @return True if should repeat the alarm after it runs and false otherwise.
	 */
	public boolean getRepeat()
	{
		return this.mRepeat;
	}

	/**
	 * @return The snooze count.
	 */
	public int getSnoozeCount()
	{
		return this.mSnoozeCount;
	}

	/**
	 * @return The current snooze count.
	 */
	public int getSnoozeCount(NacSharedPreferences shared)
	{
		long id = this.getId();
		return shared.getSnoozeCount(id);
	}

	/**
	 * @return The frequency at which to use TTS, in units of min.
	 */
	public int getTtsFrequency()
	{
		return this.mTtsFrequency;
	}

	/**
	 * @return True if should use NFC, and False otherwise.
	 */
	public boolean getUseNfc()
	{
		return this.mUseNfc;
	}

	/**
	 * @return True if should use TTS, and False otherwise.
	 */
	public boolean getUseTts()
	{
		return this.mUseTts;
	}

	/**
	 * @return True if the phone should vibrate when the alarm is run, and False
	 *     otherwise.
	 */
	public boolean getVibrate()
	{
		return this.mVibrate;
	}

	/**
	 * @return The volume level.
	 */
	public int getVolume()
	{
		return this.mVolume;
	}

	/**
	 * @return True if the alarm has a sound that will be played when it goes
	 *         off, and False otherwise.
	 */
	public boolean hasMedia()
	{
		String sound = this.getMediaPath();
		return ((sound != null) && !sound.isEmpty());
	}

	/**
	 * @return The flag indicating whether the alarm is active or not.
	 */
	public boolean isActive()
	{
		return this.mIsActive;
	}

	/**
	 * @return True if the alarm is enabled and false otherwise.
	 */
	public boolean isEnabled()
	{
		return this.mIsEnabled;
	}

	/**
	 * @return True if the alarm is snoozed, and False otherwise.
	 */
	public boolean isSnoozed()
	{
		return (this.getSnoozeCount() > 0);
	}

	/**
	 * @return True if the alarm is snoozed, and False otherwise.
	 */
	public boolean isSnoozed(NacSharedPreferences shared)
	{
		long id = this.getId();
		return (shared.getSnoozeCount(id) > 0);
	}

	/**
	 * @return True if the alarm is being used, by being active or snoozed, and
	 *     False otherwise.
	 */
	public boolean isInUse()
	{
		return this.isActive() || this.isSnoozed();
	}

	/**
	 * @return True if the alarm is being used, by being active or snoozed, and
	 *     False otherwise.
	 */
	public boolean isInUse(NacSharedPreferences shared)
	{
		return this.isActive() || this.isSnoozed(shared);
	}

	/**
	 * Print all values in the alarm object.
	 */
	@SuppressWarnings("unused")
	public void print()
	{
		NacUtility.printf("Alarm Information");
		NacUtility.printf("Id           : %d", this.getId());
		NacUtility.printf("Is Active    : %b", this.isActive());
		NacUtility.printf("Snooze Count : %b", this.getSnoozeCount());
		NacUtility.printf("Is Enabled   : %b", this.isEnabled());
		NacUtility.printf("Hour         : %d", this.getHour());
		NacUtility.printf("Minute       : %d", this.getMinute());
		NacUtility.printf("Days         : %s", this.getDays());
		NacUtility.printf("Repeat       : %b", this.shouldRepeat());
		NacUtility.printf("Vibrate      : %b", this.shouldVibrate());
		NacUtility.printf("Use NFC      : %b", this.shouldUseNfc());
		NacUtility.printf("Nfc Tag Id   : %s", this.getNfcTagId());
		NacUtility.printf("Media Type   : %s", this.getMediaType());
		NacUtility.printf("Media Path   : %s", this.getMediaPath());
		NacUtility.printf("Media Name   : %s", this.getMediaTitle());
		NacUtility.printf("Volume       : %d", this.getVolume());
		NacUtility.printf("Audio Source : %s", this.getAudioSource());
		NacUtility.printf("Name         : %s", this.getName());
	}

	/**
	 * Set the audio source.
	 *
	 * @param  source  The audio source.
	 */
	public void setAudioSource(String source)
	{
		this.mAudioSource = source;
	}

	/**
	 * Set the days to the run the alarm.
	 *
	 * @param  days  The set of days to run the alarm on.
	 */
	public void setDays(EnumSet<NacCalendar.Day> days)
	{
		this.mDays = days;
	}

	/**
	 * @see #setDays(EnumSet)
	 */
	public void setDays(int value)
	{
		this.setDays(NacCalendar.Days.valueToDays(value));
	}

	/**
	 * Set the hour.
	 *
	 * @param  hour  The hour at which to run the alarm.
	 */
	public void setHour(int hour)
	{
		this.mHour = hour;
	}

	/**
	 * Set the alarm ID.
	 *
	 * @param  id  The unique ID of the alarm.
	 */
	public void setId(long id)
	{
		this.mId = id;
	}

	/**
	 * Set the flag indicating whether the alarm is active or not.
	 */
	public void setIsActive(boolean active)
	{
		this.mIsActive = active;
	}

	/**
	 * Set whether the alarm is enabled or not.
	 *
	 * @param  enabled  True if the alarm is enabled and False otherwise.
	 */
	public void setIsEnabled(boolean enabled)
	{
		this.mIsEnabled = enabled;
	}

	/**
	 * Set the sound to play when the alarm goes off.
	 *
	 * @param  context  The application context.
	 * @param  path  The path to sound to play.
	 */
	public void setMedia(Context context, String path)
	{
		int type = NacMedia.getType(context, path);
		String title = NacMedia.getTitle(context, path);

		this.setMediaType(type);
		this.setMediaPath(path);
		this.setMediaTitle(title);
	}

	/**
	 * Set the sound to play when the alarm goes off.
	 *
	 * @param  path  The path to the media file to play when the alarm goes
	 *               off.
	 */
	public void setMediaPath(String path)
	{
		this.mMediaPath = (path != null) ? path : "";
	}

	/**
	 * Set the media title.
	 *
	 * @param  title  The title of the media file to play.
	 */
	public void setMediaTitle(String title)
	{
		this.mMediaTitle = (title != null) ? title : "";
	}

	/**
	 * Set the type of sound to play.
	 *
	 * @param  type  The type of media file to play.
	 */
	public void setMediaType(int type)
	{
		this.mMediaType = type;
	}

	/**
	 * Set the minute.
	 *
	 * @param  minute  The minute at which to run the alarm.
	 */
	public void setMinute(int minute)
	{
		this.mMinute = minute;
	}

	/**
	 * Set the name of the alarm.
	 *
	 * @param  name  The alarm name.
	 */
	public void setName(String name)
	{
		this.mName = (name != null) ? name : "";
	}

	/**
	 * Set the NFC tag ID of the tag that will be used to dismiss the alarm.
	 *
	 * @param  tagId  The ID of the NFC tag.
	 */
	public void setNfcTagId(String tagId)
	{
		this.mNfcTagId = (tagId != null) ? tagId : "";
	}

	/**
	 * Set whether the alarm should repeat every week or not.
	 *
	 * @param  repeat  True if repeating the alarm after it runs, and False
	 *                 otherwise.
	 */
	public void setRepeat(boolean repeat)
	{
		this.mRepeat = repeat;
	}

	/**
	 * Set the snooze count.
	 */
	public void setSnoozeCount(int count)
	{
		this.mSnoozeCount = count;
	}

	/**
	 * Set the frequency at which to use TTS, in units of min.
	 *
	 * @param  freq  The frequency.
	 */
	public void setTtsFrequency(int freq)
	{
		this.mTtsFrequency = freq;
	}

	/**
	 * Set whether the alarm should use NFC to dismiss or not.
	 *
	 * @param  useNfc  Whether to use NFC to dismiss or not.
	 */
	public void setUseNfc(boolean useNfc)
	{
		this.mUseNfc = useNfc;
	}

	/**
	 * Set whether the alarm should use TTS or not.
	 *
	 * @param  useTts  Whether to use TTS or not.
	 */
	public void setUseTts(boolean useTts)
	{
		this.mUseTts = useTts;
	}

	/**
	 * Set whether the alarm should vibrate the phone or not.
	 *
	 * @param  vibrate  True if the phone should vibrate when the alarm is
	 *                  going off and false otherwise.
	 */
	public void setVibrate(boolean vibrate)
	{
		this.mVibrate = vibrate;
	}

	/**
	 * Set the volume level.
	 *
	 * @param  volume  The volume level.
	 */
	public void setVolume(int volume)
	{
		this.mVolume = volume;
	}

	/**
	 * @return True if should repeat the alarm after it runs and false otherwise.
	 */
	public boolean shouldRepeat()
	{
		return this.mRepeat;
	}

	/**
	 * @return True if should use NFC, and False otherwise.
	 */
	public boolean shouldUseNfc()
	{
		return this.mUseNfc;
	}

	/**
	 * @return True if should use TTS, and False otherwise.
	 */
	public boolean shouldUseTts()
	{
		return this.mUseTts;
	}

	/**
	 * @return True if the phone should vibrate when the alarm is run, and False
	 *     otherwise.
	 */
	public boolean shouldVibrate()
	{
		return this.mVibrate;
	}

	/**
	 * Toggle a day.
	 */
	public void toggleDay(NacCalendar.Day day)
	{
		if (this.getDays().contains(day))
		{
			this.getDays().remove(day);
		}
		else
		{
			this.getDays().add(day);
		}
	}

	/**
	 * Toggle repeat.
	 */
	public void toggleRepeat()
	{
		boolean repeat = this.shouldRepeat();
		this.setRepeat(!repeat);
	}

	/**
	 * Toggle today.
	 */
	public void toggleToday()
	{
		NacCalendar.Day day = NacCalendar.Days.getToday();
		this.toggleDay(day);
	}

	/**
	 * Toggle use NFC.
	 */
	public void toggleUseNfc()
	{
		boolean useNfc = this.shouldUseNfc();
		this.setUseNfc(!useNfc);
	}

	/**
	 * Toggle vibrate.
	 */
	public void toggleVibrate()
	{
		boolean vibrate = this.shouldVibrate();
		this.setVibrate(!vibrate);
	}

	/**
	 * Write data into parcel (required for Parcelable).
	 *
	 * Update this when adding/removing an element.
	 */
	@Override
	public void writeToParcel(Parcel output, int flags)
	{
		output.writeLong(this.getId());
		output.writeInt(this.isActive() ? 1 : 0);
		output.writeInt(this.getSnoozeCount());
		output.writeInt(this.isEnabled() ? 1 : 0);
		output.writeInt(this.getHour());
		output.writeInt(this.getMinute());
		output.writeInt(NacCalendar.Days.daysToValue(this.getDays()));
		output.writeInt(this.shouldRepeat() ? 1 : 0);
		output.writeInt(this.shouldVibrate() ? 1 : 0);
		output.writeInt(this.shouldUseNfc() ? 1 : 0);
		output.writeString(this.getNfcTagId());
		output.writeInt(this.getMediaType());
		output.writeString(this.getMediaPath());
		output.writeString(this.getMediaTitle());
		output.writeInt(this.getVolume());
		output.writeString(this.getAudioSource());
		output.writeString(this.getName());
	}

	/**
	 * Generate parcel (required for Parcelable).
	 */
	public static final Parcelable.Creator<NacAlarm> CREATOR = new
		Parcelable.Creator<NacAlarm>()
	{
		public NacAlarm createFromParcel(Parcel input)
		{
			return new NacAlarm(input);
		}

		public NacAlarm[] newArray(int size)
		{
			return new NacAlarm[size];
		}
	};

}
