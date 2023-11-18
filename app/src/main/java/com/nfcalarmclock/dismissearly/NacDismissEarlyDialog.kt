package com.nfcalarmclock.dismissearly

import android.app.AlertDialog
import android.app.Dialog
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.widget.NumberPicker
import android.widget.RelativeLayout
import android.widget.TextView
import com.google.android.material.checkbox.MaterialCheckBox
import com.nfcalarmclock.R
import com.nfcalarmclock.view.dialog.NacDialogFragment

class NacDismissEarlyDialog
	: NacDialogFragment()
{

	companion object
	{

		/**
		 * Tag for the class.
		 */
		const val TAG = "NacDismissEarlyDialog"

	}

	/**
	 * Listener for when an audio source is selected.
	 */
	fun interface OnDismissEarlyOptionSelectedListener
	{
		fun onDismissEarlyOptionSelected(useDismissEarly: Boolean, index: Int, time: Int)
	}

	/**
	 * Default dismiss early.
	 */
	var defaultShouldDismissEarly = false

	/**
	 * Default dismiss early index.
	 */
	var defaultShouldDismissEarlyIndex = 0

	/**
	 * Whether an alarm should be able to be dismissed early or not.
	 */
	private val shouldDismissEarly: Boolean
		get() = checkBox!!.isChecked

	/**
	 * Check box to dismiss early or not.
	 */
	private var checkBox: MaterialCheckBox? = null

	/**
	 * Scrollable picker to choose the dismiss early time.
	 */
	private var timePicker: NumberPicker? = null

	/**
	 * Listener for when the dismiss early option is clicked.
	 */
	var onDismissEarlyOptionSelectedListener: OnDismissEarlyOptionSelectedListener? = null

	/**
	 * Called when the dialog is created.
	 */
	override fun onCreateDialog(savedInstanceState: Bundle?): Dialog
	{
		// Setup the shared preferences
		setupSharedPreferences()

		// Create the dialog
		return AlertDialog.Builder(requireContext())
			.setTitle(R.string.title_dismiss_early)
			.setPositiveButton(R.string.action_ok) { _, _ ->

				// Get the index value
				val index = timePicker!!.value

				// Calculate the time based on the index
				val time = if (index < 5)
				{
					index + 1
				}
				else
				{
					(index - 3) * 5
				}

				// Call the listener
				onDismissEarlyOptionSelectedListener?.onDismissEarlyOptionSelected(
					shouldDismissEarly, index, time)

			}
			.setNegativeButton(R.string.action_cancel, null)
			.setView(R.layout.dlg_alarm_dismiss_early)
			.create()
	}

	/**
	 * Called when the fragment is resumed.
	 */
	override fun onResume()
	{
		// Super
		super.onResume()

		// Get the container of the dialog and the text view
		val container = dialog!!.findViewById<RelativeLayout>(R.id.should_use_dismiss_early)
		val textView: TextView = dialog!!.findViewById(R.id.should_use_dismiss_early_summary)

		// Set the views
		checkBox = dialog!!.findViewById(R.id.should_use_dismiss_early_checkbox)
		timePicker = dialog!!.findViewById(R.id.dismiss_early_time_picker)

		// Set the status of the checkbox
		checkBox!!.isChecked = defaultShouldDismissEarly

		// Setup the views
		setupOnClickListener(container, textView)
		setupCheckBoxColor()
		setupTextView(textView)
		setupTimePickerValues()
		setupTimePickerUsable()
	}

	/**
	 * Set the default dismiss early index from a time.
	 */
	fun setDefaultDismissEarlyIndexFromTime(time: Int)
	{
		defaultShouldDismissEarlyIndex = if (time <= 5)
		{
			time - 1
		}
		else
		{
			time / 5 + 3
		}
	}

	/**
	 * Setup the color of the dismiss early check box.
	 */
	private fun setupCheckBoxColor()
	{
		// Get the colors for the boolean states
		val colors = intArrayOf(sharedPreferences!!.themeColor, Color.GRAY)

		// Get the IDs of the two states
		val states = arrayOf(intArrayOf(android.R.attr.state_checked), intArrayOf(-android.R.attr.state_checked))

		// Set the state list of the checkbox
		checkBox!!.buttonTintList = ColorStateList(states, colors)
	}

	/**
	 * Setup the on click listener for when the container is clicked.
	 */
	private fun setupOnClickListener(container: RelativeLayout, textView: TextView)
	{
		// Setup the listener
		container.setOnClickListener {

			// Toggle the checkbox
			checkBox!!.isChecked = !shouldDismissEarly

			// Setup the views
			setupTextView(textView)
			setupTimePickerUsable()

		}
	}

	/**
	 * Setup the summary text for whether a user should be able to dismiss an
	 * alarm early or not.
	 */
	private fun setupTextView(textView: TextView)
	{
		// Determine the text ID to use based on whether restrict volume will
		// be used or not
		val textId = if (shouldDismissEarly)
		{
			R.string.dismiss_early_true
		}
		else
		{
			R.string.dismiss_early_false
		}

		// Set the text
		textView.setText(textId)
	}

	/**
	 * Setup whether the dismiss early time container can be used or not.
	 */
	private fun setupTimePickerUsable()
	{
		// Set the alpha
		timePicker!!.alpha = if (shouldDismissEarly) 1.0f else 0.25f

		// Set whether it can be used or not
		timePicker!!.isEnabled = shouldDismissEarly
	}

	/**
	 * Setup scrollable picker for the dismiss early time.
	 */
	private fun setupTimePickerValues()
	{
		// Get the dismiss early times
		val values = requireContext().resources.getStringArray(R.array.dismiss_early_times).toList()

		// Setup the time picker
		timePicker!!.minValue = 0
		timePicker!!.maxValue = values.size - 1
		timePicker!!.displayedValues = values.toTypedArray()
		timePicker!!.value = defaultShouldDismissEarlyIndex
	}

}