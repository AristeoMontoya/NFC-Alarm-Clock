package com.nfcalarmclock;

import android.content.Context;
import android.content.res.Resources;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.preference.PreferenceManager;

/**
 * Container for the values of each preference.
 */
public class NacSharedPreferences
{

	/**
	 * Shared preferences instance.
	 */
	public SharedPreferences instance;

	/**
	 * Auto dismiss.
	 */
	public int autoDismiss;

	/**
	 * Max snoozes.
	 */
	public int maxSnoozes;

	/**
	 * Snooze duration.
	 */
	public int snoozeDuration;

	/**
	 * Repeat.
	 */
	public boolean repeat;

	/**
	 * Day of week.
	 */
	public int days;

	/**
	 * Vibrate.
	 */
	public boolean vibrate;

	/**
	 * Sound path.
	 */
	public String sound;

	/**
	 * Name of the alarm.
	 */
	public String name;

	/**
	 * Theme color.
	 */
	public int themeColor;

	/**
	 * Color of the name of the alarm.
	 */
	public int nameColor;

	/**
	 * Color of the text that shows which days the alarm runs.
	 */
	public int daysColor;

	/**
	 * Time color.
	 */
	public int timeColor;

	/**
	 * AM color.
	 */
	public int amColor;

	/**
	 * PM color.
	 */
	public int pmColor;

	/**
	 */
	public NacSharedPreferences(Context context)
	{
		SharedPreferences shared =
			PreferenceManager.getDefaultSharedPreferences(context);
		NacPreferenceKeys keys = new NacPreferenceKeys(context);

		this.instance = shared;
		this.autoDismiss = shared.getInt(keys.autoDismiss, 15);
		this.maxSnoozes = shared.getInt(keys.maxSnoozes, -1);
		this.snoozeDuration = shared.getInt(keys.snoozeDuration, 5);
		this.repeat = shared.getBoolean(keys.repeat, NacAlarm.getRepeatDefault());
		this.days = shared.getInt(keys.days, NacAlarm.getDaysDefault());
		this.vibrate = shared.getBoolean(keys.vibrate, NacAlarm.getVibrateDefault());
		this.sound = shared.getString(keys.sound, "");
		this.name = shared.getString(keys.name, "");
		this.themeColor = shared.getInt(keys.themeColor, 0xfffb8c00);
		this.timeColor = shared.getInt(keys.timeColor, Color.WHITE);
		this.amColor = shared.getInt(keys.amColor, Color.WHITE);
		this.pmColor = shared.getInt(keys.pmColor, Color.WHITE);
		this.nameColor = shared.getInt(keys.nameColor, 0xff00c0fb);
		this.daysColor = shared.getInt(keys.daysColor, 0xfffb8c00);

		this.autoDismiss = (this.autoDismiss == 1) ? 1 : 5*(this.autoDismiss-1);
		this.maxSnoozes = (this.maxSnoozes == 11) ? -1 : this.maxSnoozes;
		this.snoozeDuration = (this.snoozeDuration == 0) ? 1 : 5*this.snoozeDuration;
	}

}
