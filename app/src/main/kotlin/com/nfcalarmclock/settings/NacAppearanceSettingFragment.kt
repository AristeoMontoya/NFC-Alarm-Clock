package com.nfcalarmclock.settings

import android.os.Bundle
import androidx.preference.Preference
import androidx.preference.PreferenceManager
import com.nfcalarmclock.R
import com.nfcalarmclock.alarm.options.nextalarmformat.NacNextAlarmFormatPreference
import com.nfcalarmclock.settings.preference.NacCheckboxPreference
import com.nfcalarmclock.alarm.options.startweekon.NacStartWeekOnPreference
import com.nfcalarmclock.view.colorpicker.NacColorPickerPreference

/**
 * Appearance fragment.
 */
class NacAppearanceSettingFragment
	: NacGenericSettingFragment()
{

	/**
	 * Initialize the color settings fragment.
	 */
	private fun init()
	{
		// Inflate the XML file and add the hierarchy to the current preference
		addPreferencesFromResource(R.xml.appearance_preferences)

		// Set the default values in the XML
		PreferenceManager.setDefaultValues(requireContext(),
			R.xml.appearance_preferences, false)

		// Setup color and styles
		setupAlarmScreenPreferences()
		setupColorPreferences()
		setupShowHideButtonPreferences()
		setupDayButtonStylePreference()

		// Setup on click listeners
		setupColorPickerOnClickListeners()
		setupStartWeekOnClickListener()
		setupNexAlarmFormatOnClickListener()
	}

	/**
	 * Called when the preferences are created.
	 */
	override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?)
	{
		// Initialize the color settings
		init()
	}

	/**
	 * Setup the preferences for the alarm screen.
	 */
	private fun setupAlarmScreenPreferences()
	{
		// Get the new alarm screen preference
		val newScreenKey = getString(R.string.key_use_new_alarm_screen)
		val newScreenPref = findPreference<NacCheckboxPreference>(newScreenKey)!!

		// Setup the dependent alarm screen preferences
		setupDependentNewAlarmScreenPreferences(newScreenPref.isChecked)

		// Set the listener for when the new screen preference is changed
		newScreenPref.onPreferenceChangeListener = Preference.OnPreferenceChangeListener { _, status ->

			// Set the usability of the dependent preferences
			setupDependentNewAlarmScreenPreferences(status as Boolean)

			// Return
			true

		}
	}

	/**
	 * Setup the listeners for when a color picker preference is clicked.
	 */
	private fun setupColorPickerOnClickListeners()
	{
		// Get the keys
		val themeKey = getString(R.string.theme_color_key)
		val nameKey = getString(R.string.name_color_key)
		val dayKey = getString(R.string.days_color_key)
		val timeKey = getString(R.string.time_color_key)
		val amKey = getString(R.string.am_color_key)
		val pmKey = getString(R.string.pm_color_key)

		// Get the color preferences
		val themePref = findPreference<NacColorPickerPreference>(themeKey)
		val namePref = findPreference<NacColorPickerPreference>(nameKey)
		val daysPref = findPreference<NacColorPickerPreference>(dayKey)
		val timePref = findPreference<NacColorPickerPreference>(timeKey)
		val amPref = findPreference<NacColorPickerPreference>(amKey)
		val pmPref = findPreference<NacColorPickerPreference>(pmKey)

		// Create list of all color preferences
		val allPrefs = listOf(themePref, namePref, daysPref, timePref, amPref, pmPref)

		// Iterate over each color preference
		for (p in allPrefs)
		{
			// Set the on click listener
			p!!.onPreferenceClickListener = Preference.OnPreferenceClickListener { pref ->

				// Show the dialog
				(pref as NacColorPickerPreference).showDialog(childFragmentManager)

				// Return
				true

			}
		}
	}

	/**
	 * Setup the color preferences.
	 */
	private fun setupColorPreferences()
	{
		// Get the keys
		val themeKey = getString(R.string.theme_color_key)
		val nameKey = getString(R.string.name_color_key)
		val dayKey = getString(R.string.days_color_key)
		val timeKey = getString(R.string.time_color_key)
		val amKey = getString(R.string.am_color_key)
		val pmKey = getString(R.string.pm_color_key)

		// Put the keys in a list
		val allKeys = arrayOf(themeKey, nameKey, dayKey, timeKey, amKey, pmKey)

		// Iterate over each color key
		for (k in allKeys)
		{
			// Get the preference
			val pref = findPreference<Preference>(k)

			// Set the listener for when the prference is changed
			pref!!.onPreferenceChangeListener = Preference.OnPreferenceChangeListener { p, _ ->

				// Set flag to refresh the main activity
				sharedPreferences!!.shouldRefreshMainActivity = true

				// Preference key is for the theme
				if (p.key == themeKey)
				{
					// Reset the screen
					preferenceScreen = null

					// Reinitialize the colors
					init()
				}

				// Return
				true

			}
		}
	}

	/**
	 * Setup the day button style preference.
	 */
	private fun setupDayButtonStylePreference()
	{
		// Get the preference
		val dayButtonStyleKey = getString(R.string.day_button_style_key)
		val dayButtonStylePref = findPreference<Preference>(dayButtonStyleKey)

		// Set the listener for when the preference is changed
		dayButtonStylePref!!.onPreferenceChangeListener = Preference.OnPreferenceChangeListener { _, _ ->

			// Set flag to refresh the main activity
			sharedPreferences!!.shouldRefreshMainActivity = true

			// Return
			true

		}
	}

	/**
	 * Setup the preferences that are dependent on the new alarm screen.
	 */
	private fun setupDependentNewAlarmScreenPreferences(enabled: Boolean)
	{
		// Get the keys
		val currentDateAndTimeKey = getString(R.string.key_show_current_date_and_time)
		val musicInfoKey = getString(R.string.key_show_music_info)

		// Get the dependent preferences
		val currentDateAndTimePref = findPreference<NacCheckboxPreference>(currentDateAndTimeKey)!!
		val musicInfoPref = findPreference<NacCheckboxPreference>(musicInfoKey)!!

		// Set the usability of those preferences
		currentDateAndTimePref.isEnabled = enabled
		musicInfoPref.isEnabled = enabled
	}

	/**
	 * Setup the listener for when the next alarm format preference is clicked.
	 */
	private fun setupNexAlarmFormatOnClickListener()
	{
		// Get the preference
		val key = getString(R.string.next_alarm_format_key)
		val pref = findPreference<NacNextAlarmFormatPreference>(key)

		// Set the on click listener
		pref!!.onPreferenceClickListener = Preference.OnPreferenceClickListener { p ->

			// Show the dialog
			(p as NacNextAlarmFormatPreference).showDialog(childFragmentManager)

			// Return
			true

		}
	}

	/**
	 * Setup the show/hide buttons.
	 */
	private fun setupShowHideButtonPreferences()
	{
		// Get the keys
		val vibrateKey = getString(R.string.show_hide_vibrate_button_key)
		val nfcKey = getString(R.string.show_hide_nfc_button_key)
		val flashlightKey = getString(R.string.show_hide_flashlight_button_key)

		// Put the keys in a list
		val allKeys = arrayOf(vibrateKey, nfcKey, flashlightKey)

		// Iterate over each color key
		for (k in allKeys)
		{
			// Get the preference
			val pref = findPreference<Preference>(k)

			// Set the listener for when the prference is changed
			pref!!.onPreferenceChangeListener = Preference.OnPreferenceChangeListener { _, _ ->

				// Set flag to refresh the main activity
				sharedPreferences!!.shouldRefreshMainActivity = true

				// Return
				true

			}
		}
	}

	/**
	 * Setup the listener for when the start week on preference is clicked.
	 */
	private fun setupStartWeekOnClickListener()
	{
		// Get the preference
		val key = getString(R.string.start_week_on_key)
		val pref = findPreference<NacStartWeekOnPreference>(key)

		// Set the on click listener
		pref!!.onPreferenceClickListener = Preference.OnPreferenceClickListener { p ->

			// Show the dialog
			(p as NacStartWeekOnPreference).showDialog(childFragmentManager)

			// Return
			true
		}
	}

}