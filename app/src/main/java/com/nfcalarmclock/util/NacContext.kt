package com.nfcalarmclock.util

import android.content.Context
import android.os.Build
import android.os.Bundle
import com.nfcalarmclock.alarm.db.NacAlarm
import com.nfcalarmclock.nfc.NacNfcTag
import com.nfcalarmclock.util.NacBundle.toBundle
import com.nfcalarmclock.util.NacIntent.createAlarmActivity
import com.nfcalarmclock.util.NacIntent.createForegroundService
import com.nfcalarmclock.util.NacIntent.createMainActivity
import com.nfcalarmclock.util.NacIntent.stopAlarmActivity

/**
 * Context helper object.
 */
object NacContext
{

	/**
	 * Dismiss the alarm activity for the given alarm.
	 * <p>
	 * If alarm is null, it will stop the currently active alarm activity.
	 */
	@JvmStatic
	fun dismissAlarmActivity(context: Context, alarm: NacAlarm?)
	{
		val intent = NacIntent.dismissAlarmActivity(context, alarm)

		context.startActivity(intent)
	}

	/**
	 * Dismiss the alarm activity for the given alarm due with NFC.
	 * <p>
	 * If alarm is null, it will stop the currently active alarm activity.
	 */
	@JvmStatic
	fun dismissAlarmActivityWithNfc(context: Context, tag: NacNfcTag)
	{
		val intent = NacIntent.dismissAlarmActivityWithNfc(context, tag)

		context.startActivity(intent)
	}

	/**
	 * Stop the alarm activity for the given alarm.
	 * <p>
	 * If alarm is null, it will stop the currently active alarm activity.
	 */
	@JvmStatic
	fun stopAlarmActivity(context: Context, alarm: NacAlarm?)
	{
		val intent = stopAlarmActivity(alarm)

		context.sendBroadcast(intent)
	}

	/**
	 * Start the running the alarm activity and service.
	 *
	 * @param  context  A context.
	 * @param  bundle  A bundle, typically with an alarm inside.
	 */
	@JvmStatic
	fun startAlarm(context: Context, bundle: Bundle?)
	{
		// Check if bundle is null
		if (bundle == null)
		{
			return
		}

		val activityIntent = createAlarmActivity(context, bundle)
		val serviceIntent = createForegroundService(context, bundle)

		// Start the activity
		context.startActivity(activityIntent)

		// Check if the API >= 26
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
		{
			// Start the foreground service
			context.startForegroundService(serviceIntent)
		}
		// API is < 26
		else
		{
			// Start the service
			context.startService(serviceIntent)
		}
	}

	/**
	 * @see NacContext.startAlarm
	 */
	@JvmStatic
	fun startAlarm(context: Context, alarm: NacAlarm?)
	{
		// Check if the alarm is null
		if (alarm == null)
		{
			return
		}

		// Add the alarm to the bundle
		val bundle = toBundle(alarm)

		// Start the alarm
		startAlarm(context, bundle)
	}

	/**
	 * Start the alarm activity.
	 */
	@JvmStatic
	fun startAlarmActivity(context: Context, alarm: NacAlarm?)
	{
		// Unable to start the activity because the alarm is null
		if (alarm == null)
		{
			return
		}

		// Create the intent
		val intent = createAlarmActivity(context, alarm)

		// Start the activity
		context.startActivity(intent)
	}

	/**
	 * Start the main activity.
	 *
	 * @param  context  A context.
	 */
	@JvmStatic
	fun startMainActivity(context: Context)
	{
		val intent = createMainActivity(context)

		context.startActivity(intent)
	}

}