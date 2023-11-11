package com.nfcalarmclock.main

import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.content.res.ColorStateList
import android.graphics.drawable.InsetDrawable
import android.os.Bundle
import android.view.ContextMenu
import android.view.ContextMenu.ContextMenuInfo
import android.view.HapticFeedbackConstants
import android.view.Menu
import android.view.MenuItem
import android.view.MotionEvent
import android.view.View
import android.view.View.OnCreateContextMenuListener
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.OnItemTouchListener
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.textview.MaterialTextView
import com.nfcalarmclock.BuildConfig
import com.nfcalarmclock.R
import com.nfcalarmclock.activealarm.NacActiveAlarmService
import com.nfcalarmclock.alarm.NacAlarmViewModel
import com.nfcalarmclock.alarm.db.NacAlarm
import com.nfcalarmclock.audiooptions.NacAlarmAudioOptionsDialog
import com.nfcalarmclock.audiooptions.NacAlarmAudioOptionsDialog.OnAudioOptionClickedListener
import com.nfcalarmclock.audiosource.NacAudioSourceDialog
import com.nfcalarmclock.audiosource.NacAudioSourceDialog.OnAudioSourceSelectedListener
import com.nfcalarmclock.card.NacCardAdapter
import com.nfcalarmclock.card.NacCardAdapter.OnViewHolderBoundListener
import com.nfcalarmclock.card.NacCardAdapter.OnViewHolderCreatedListener
import com.nfcalarmclock.card.NacCardAdapterLiveData
import com.nfcalarmclock.card.NacCardHolder
import com.nfcalarmclock.card.NacCardHolder.OnCardAudioOptionsClickedListener
import com.nfcalarmclock.card.NacCardHolder.OnCardCollapsedListener
import com.nfcalarmclock.card.NacCardHolder.OnCardDeleteClickedListener
import com.nfcalarmclock.card.NacCardHolder.OnCardMediaClickedListener
import com.nfcalarmclock.card.NacCardHolder.OnCardUpdatedListener
import com.nfcalarmclock.card.NacCardHolder.OnCardUseNfcChangedListener
import com.nfcalarmclock.card.NacCardTouchHelper
import com.nfcalarmclock.card.NacCardTouchHelper.OnSwipedListener
import com.nfcalarmclock.dismissearly.NacDismissEarlyDialog
import com.nfcalarmclock.dismissearly.NacDismissEarlyDialog.OnDismissEarlyOptionSelectedListener
import com.nfcalarmclock.graduallyincreasevolume.NacGraduallyIncreaseVolumeDialog
import com.nfcalarmclock.graduallyincreasevolume.NacGraduallyIncreaseVolumeDialog.OnGraduallyIncreaseVolumeListener
import com.nfcalarmclock.mediapicker.NacMediaActivity
import com.nfcalarmclock.nfc.NacNfc
import com.nfcalarmclock.nfc.NacNfc.getTag
import com.nfcalarmclock.nfc.NacNfc.parseId
import com.nfcalarmclock.nfc.NacNfc.wasScanned
import com.nfcalarmclock.nfc.NacNfcTag
import com.nfcalarmclock.nfc.NacScanNfcTagDialog
import com.nfcalarmclock.nfc.NacScanNfcTagDialog.OnScanNfcTagListener
import com.nfcalarmclock.permission.NacPermissionRequestManager
import com.nfcalarmclock.ratemyapp.NacRateMyApp
import com.nfcalarmclock.restrictvolume.NacRestrictVolumeDialog
import com.nfcalarmclock.restrictvolume.NacRestrictVolumeDialog.OnRestrictVolumeListener
import com.nfcalarmclock.scheduler.NacScheduler
import com.nfcalarmclock.settings.NacMainSettingActivity
import com.nfcalarmclock.shared.NacSharedPreferences
import com.nfcalarmclock.shutdown.NacShutdownBroadcastReceiver
import com.nfcalarmclock.statistics.NacAlarmStatisticViewModel
import com.nfcalarmclock.tts.NacTextToSpeechDialog
import com.nfcalarmclock.tts.NacTextToSpeechDialog.OnTextToSpeechOptionsSelectedListener
import com.nfcalarmclock.upcomingalarm.NacUpcomingAlarmNotification
import com.nfcalarmclock.util.NacCalendar
import com.nfcalarmclock.util.NacContext.dismissAlarmActivityWithNfc
import com.nfcalarmclock.util.NacContext.startAlarm
import com.nfcalarmclock.util.NacContext.startAlarmActivity
import com.nfcalarmclock.util.NacIntent.getSetAlarm
import com.nfcalarmclock.util.NacIntent.toIntent
import com.nfcalarmclock.util.NacUtility.quickToast
import com.nfcalarmclock.view.snackbar.NacSnackbar
import com.nfcalarmclock.whatsnew.NacWhatsNewDialog
import com.nfcalarmclock.whatsnew.NacWhatsNewDialog.OnReadWhatsNewListener
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.util.Locale

/**
 * The application's main activity.
 */
@AndroidEntryPoint
class NacMainActivity

	// Constructor
	: AppCompatActivity(),

	// Interface
	OnCreateContextMenuListener,
	Toolbar.OnMenuItemClickListener,
	OnItemTouchListener,
	OnSwipedListener,
	OnViewHolderBoundListener,
	OnViewHolderCreatedListener,
	OnCardCollapsedListener,
	OnCardUpdatedListener,
	OnCardUseNfcChangedListener,
	OnScanNfcTagListener
{

	/**
	 * Shared preferences.
	 */
	private var sharedPreferences: NacSharedPreferences? = null

	/**
	 * Root view.
	 */
	private var root: View? = null

	/**
	 * Top toolbar.
	 */
	private var toolbar: MaterialToolbar? = null

	/**
	 * Next alarm text view.
	 */
	private var nextAlarmTextView: MaterialTextView? = null

	/**
	 * Recycler view containing the alarm cards.
	 */
	private var recyclerView: RecyclerView? = null

	/**
	 * Floating action button to add new alarms.
	 */
	private var floatingActionButton: FloatingActionButton? = null

	/**
	 * Alarm card adapter.
	 */
	private var alarmCardAdapter: NacCardAdapter? = null

	/**
	 * The snackbar.
	 */
	private var snackbar: NacSnackbar? = null

	/**
	 * Alarm view model.
	 */
	private val alarmViewModel: NacAlarmViewModel by viewModels()

	/**
	 * Statistic view model.
	 */
	private val statisticViewModel: NacAlarmStatisticViewModel by viewModels()

	/**
	 * Mutable live data for the alarm card that can be modified and sorted, or
	 * not sorted, depending on the circumstance.
	 *
	 * Live data from the view model cannot be sorted, hence the need for this.
	 */
	private var alarmCardAdapterLiveData: NacCardAdapterLiveData? = null

	/**
	 * Alarm card touch helper.
	 */
	private var alarmCardTouchHelper: NacCardTouchHelper? = null

	/**
	 * Shutdown broadcast receiver.
	 *
	 * TODO: Should this be final like mTimeTickReceiver?
	 */
	private var shutdownBroadcastReceiver: NacShutdownBroadcastReceiver = NacShutdownBroadcastReceiver()

	/**
	 * The IDs of alarms that were recently added.
	 */
	private var recentlyAddedAlarmIds: MutableList<Long> = ArrayList()

	/**
	 * The IDs of alarms that were recently updated.
	 */
	private var recentlyUpdatedAlarmIds: MutableList<Long> = ArrayList()

	/**
	 * Last action on an alarm card.
	 */
	private var lastAlarmCardAction = NacLastAlarmCardAction()

	/**
	 * Permission request manager, handles requesting permissions from the user.
	 */
	private var permissionRequestManager: NacPermissionRequestManager? = null

	/**
	 * Indicator of whether the activity is shown or not.
	 */
	private var isActivityShown = false

	/**
	 * Scan an NFC tag dialog.
	 */
	private var scanNfcTagDialog: NacScanNfcTagDialog? = null

	/**
	 * Alarm that is being used by an open audio options dialog.
	 */
	private var audioOptionsAlarm: NacAlarm? = null

	/**
	 * The NFC tag that was scanned for an active alarm.
	 */
	private var nfcTag: NacNfcTag? = null

	/**
	 * Listener for when the floating action button is clicked.
	 */
	private val mFloatingActionButtonListener = View.OnClickListener { view: View ->

		// Haptic feedback so that the user knows the action was received
		view.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY)

		// Max number of alarms reached
		if (hasCreatedMaxAlarms)
		{
			val message = getString(R.string.error_message_max_alarms)

			// Show a toast that the max number of alarms was created
			quickToast(this@NacMainActivity, message)
			return@OnClickListener
		}

		// Create the alarm
		val alarm = NacAlarm.Builder(sharedPreferences)
			.build()

		// Add the alarm
		addAlarm(alarm)
	}

	/**
	 * Capture the click event on the Snackbar button.
	 */
	private val onSwipeSnackbarActionListener = View.OnClickListener {

		// Get the alarm
		val alarm = lastAlarmCardAction.alarm!!

		// Undo copy
		if (lastAlarmCardAction.wasCopy())
		{
			// Delete the alarm
			deleteAlarm(alarm)
		}
		// Undo delete
		else if (lastAlarmCardAction.wasDelete())
		{
			// Restore the alarm
			restoreAlarm(alarm)
		}
		// Undo restore
		else if (lastAlarmCardAction.wasRestore())
		{
			// Delete the alarm
			deleteAlarm(alarm)
		}
	}

	/**
	 * Receiver for the time tick intent. This is called when the time increments
	 * every minute.
	 *
	 * TODO: Should this be its own class like NacShutdownBroadcastReceiver?
	 */
	private val timeTickReceiver = object : BroadcastReceiver()
	{
		override fun onReceive(context: Context, intent: Intent)
		{
			// Set the message for when the next alarm will be run
			setNextAlarmMessage()

			// Refresh alarms that will run soon
			refreshAlarmsThatWillAlarmSoon()
		}
	}

	/**
	 * Get the number of alarm cards that are expanded.
	 */
	private val cardsExpandedCount: Int
		get() = alarmCardAdapter!!.getCardsExpandedCount(recyclerView!!)

	/**
	 * Check if an NFC tag was scanned to dismiss an alarm and is ready for the
	 * active alarm activity.
	 */
	private val isNfcTagReady: Boolean
		get() = (nfcTag != null) && nfcTag!!.isReady

	/**
	 * Check if the user has created the maximum number of alarms.
	 */
	private val hasCreatedMaxAlarms: Boolean
		get()
		{
			// Get the current and max counts
			val currentSize = alarmCardAdapter!!.itemCount
			val maxAlarms = resources.getInteger(R.integer.max_alarms)

			// Check the size
			return (currentSize+1 > maxAlarms)
		}

	/**
	 * Check if the What's New dialog should be shown.
	 */
	private val shouldShowWhatsNewDialog: Boolean
		get()
		{
			// Get the previous version
			val previousVersion = sharedPreferences!!.previousAppVersion

			// The current version and previously saved version match. This means there
			// is no update that has occurred. Alternatively, something is wrong with the
			// current version (if it is empty)
			return (BuildConfig.VERSION_NAME != previousVersion)
		}

	/**
	 * Add an alarm to the database.
	 *
	 * TODO: Test that this sequence is legit because this is new
	 *
	 * @param  alarm  An alarm.
	 */
	private fun addAlarm(alarm: NacAlarm)
	{
		lifecycleScope.launch {

			// Insert alarm
			alarmViewModel.insert(alarm)

			println("THIS IS THE ALARM ID : " + alarm.id)

			// Schedule the alarm
			NacScheduler.update(this@NacMainActivity, alarm)

			// Save the recently added alarm ID
			recentlyAddedAlarmIds.add(alarm.id)

		}

		// Save the statistics
		statisticViewModel.insertCreated()
	}

	/**
	 * Add the first alarm, when the app is first run.
	 */
	private fun addFirstAlarm()
	{
		// Create the alarm
		val alarm = NacAlarm.Builder(sharedPreferences)
			.setId(0)
			.setHour(8)
			.setMinute(0)
			.setName("Work")
			.build()

		// Add the alarm
		addAlarm(alarm)

		// Avoid having interact() called for the alarm card, that way it does not
		// get expanded and show the time dialog
		recentlyAddedAlarmIds.remove(alarm.id)
	}

	/**
	 * Add an alarm that was created from the SET_ALARM intent.
	 */
	private fun addSetAlarmFromIntent()
	{
		// Get the alarm from the intent
		val alarm = getSetAlarm(this, intent)

		// Check if the alarm is not null
		if (alarm != null)
		{
			// Add the alarm
			addAlarm(alarm)
		}
	}

	/**
	 * Cleanup the show activity delay handler.
	 *
	 * TODO: I don't think I need to do this? See if this can be removed after
	 * stabilizing changed.
	 */
	private fun cleanupScanNfcTagDialog()
	{
		// Dismiss the dialog
		scanNfcTagDialog?.dismissAllowingStateLoss()

		// Cleanup the dialog
		scanNfcTagDialog = null
	}

	/**
	 * Cleanup the shutdown broadcast receiver.
	 */
	private fun cleanupShutdownBroadcastReceiver()
	{
		try
		{
			// Unregister the receiver
			unregisterReceiver(shutdownBroadcastReceiver)
		}
		catch (e: IllegalArgumentException)
		{
			//e.printStackTrace();
		}
	}

	/**
	 * Cleanup the time tick receiver.
	 */
	private fun cleanupTimeTickReceiver()
	{
		try
		{
			// Unregister the receiver
			unregisterReceiver(timeTickReceiver)
		}
		catch (e: IllegalArgumentException)
		{
			//e.printStackTrace();
		}
	}

	/**
	 * Copy an alarm and add it to the database.
	 *
	 * @param  alarm  An alarm.
	 */
	private fun copyAlarm(alarm: NacAlarm)
	{
		// Get the message and action for the snackbar
		val message = getString(R.string.message_alarm_copy)
		val action = getString(R.string.action_undo)

		// Create a copy of the alarm
		val copiedAlarm = alarm.copy()

		// Add the copied alarm
		addAlarm(copiedAlarm)

		// Set the last action that was executed
		lastAlarmCardAction[copiedAlarm] = NacLastAlarmCardAction.Type.COPY

		// Show the snackbar
		// TODO: Get rid of onSwipeSnackbarActionListener and just do it inline and maybe get rid of last alarm card action
		showSnackbar(message, action, onSwipeSnackbarActionListener)
	}

	/**
	 * Delete an alarm from the database.
	 *
	 * @param  alarm  An alarm.
	 */
	private fun deleteAlarm(alarm: NacAlarm)
	{
		// Get the message and action for the snackbar
		val message = getString(R.string.message_alarm_delete)
		val action = getString(R.string.action_undo)

		// Delete the alarm
		alarmViewModel.delete(alarm)
		statisticViewModel.insertDeleted(alarm)

		// Cancel the alarm
		NacScheduler.cancel(this, alarm)

		// Set the last action that was executed
		lastAlarmCardAction[alarm] = NacLastAlarmCardAction.Type.DELETE

		// Show the snackbar
		showSnackbar(message, action, onSwipeSnackbarActionListener)
	}

	/**
	 * Disable the alias for the main activity so that tapping an NFC tag
	 * DOES NOT open the main activity.
	 */
	private fun disableActivityAlias()
	{
		// Get the alias and component name
		val aliasName = "$packageName.main.NacMainAliasActivity"
		val componentName = ComponentName(this, aliasName)

		// Disable the alias
		packageManager.setComponentEnabledSetting(componentName,
			PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
			PackageManager.DONT_KILL_APP)
	}

	/**
	 * Attempt to dismiss the first active alarm found.
	 *
	 * If unable to dismiss the alarm, the alarm activity is shown.
	 */
	private fun dismissActiveAlarm()
	{
		// Do nothing if the NFC tag is null
		// TODO: This should never happen?
		if (nfcTag == null)
		{
			return
		}
		// Check if the NFC tag of the alarm matches the tag ID
		else if (nfcTag!!.check(this))
		{
			dismissAlarmActivityWithNfc(this, nfcTag!!)
		}
		// Start the alarm
		else
		{
			startAlarm(this, nfcTag!!.activeAlarm)
		}

		// Set the NFC tag to null
		nfcTag = null
	}

	/**
	 * Get the alarm card at the given index.
	 *
	 * @return The alarm card at the given index.
	 */
	private fun getAlarmCardAt(index: Int): NacCardHolder?
	{
		return recyclerView!!.findViewHolderForAdapterPosition(index) as NacCardHolder?
	}

	/**
	 * Get the message to show for the next alarm.
	 *
	 * @param  alarms  List of alarms.
	 *
	 * @return The message to show for the next alarm.
	 */
	private fun getNextAlarmMessage(alarms: List<NacAlarm>): String
	{
		// Get the next alarm
		val nextAlarm = NacCalendar.getNextAlarm(alarms)

		// Get a message for the next alarm
		return NacCalendar.getMessageNextAlarm(sharedPreferences, nextAlarm)
	}

	/**
	 * Measure a card.
	 */
	private fun measureCard(card: NacCardHolder)
	{
		// Array that will store the heights
		val heights = IntArray(3)

		// Measure the card
		card.measureCard(heights)

		// Setup the shared preferences with those heights
		sharedPreferences!!.editCardHeightCollapsed(heights[0])
		sharedPreferences!!.editCardHeightCollapsedDismiss(heights[1])
		sharedPreferences!!.editCardHeightExpanded(heights[2])
		sharedPreferences!!.editCardIsMeasured(true)
	}

	/**
	 * Called when the user cancels the scan NFC tag dialog.
	 */
	override fun onCancelNfcTagScan(alarm: NacAlarm)
	{
		// Get the card that corresponds to the alarm
		val cardHolder = recyclerView!!.findViewHolderForItemId(alarm.id) as NacCardHolder

		// Uncheck the NFC button when the dialog is canceled.
		cardHolder.nfcButton.isChecked = false
		cardHolder.doNfcButtonClick()

		// Cleanup the dialog
		cleanupScanNfcTagDialog()
	}

	/**
	 * Called when the alarm card is collapsed.
	 */
	override fun onCardCollapsed(holder: NacCardHolder, alarm: NacAlarm)
	{
		// Sort the list when no cards are expanded
		if (cardsExpandedCount == 0)
		{
			alarmCardAdapterLiveData!!.sort()
		}

		// Check if this alarm was recently updated
		if (recentlyUpdatedAlarmIds.contains(alarm.id))
		{
			// Show the next time the alarm will go off
			showUpdatedAlarmSnackbar(alarm)

			// Highlight the card
			holder.highlight()

			// Remove the alarm from the recently updated list
			recentlyUpdatedAlarmIds.remove(alarm.id)
		}
	}

	/**
	 * Called when the alarm has been changed.
	 *
	 * @param  alarm  The alarm that was changed.
	 */
	override fun onCardUpdated(holder: NacCardHolder, alarm: NacAlarm)
	{
		// Set the next alarm message
		this.setNextAlarmMessage()

		// Card is collapsed
		if (holder.isCollapsed)
		{
			showUpdatedAlarmSnackbar(alarm)
			holder.highlight()
		}
		else
		{
			recentlyUpdatedAlarmIds.add(alarm.id)
		}

		// Update the view model
		alarmViewModel.update(alarm)

		// Reschedule the alarm
		NacScheduler.update(this, alarm)
	}

	/**
	 * Called when the use NFC button is clicked.
	 */
	override fun onCardUseNfcChanged(holder: NacCardHolder, alarm: NacAlarm)
	{
		if (!alarm.shouldUseNfc)
		{
			return
		}

		// Create and set the dialog
		scanNfcTagDialog = NacScanNfcTagDialog()

		// Setup the dialog
		scanNfcTagDialog!!.alarm = alarm
		scanNfcTagDialog!!.onScanNfcTagListener = this

		// Show the dialog
		scanNfcTagDialog!!.show(supportFragmentManager, NacScanNfcTagDialog.TAG)
	}

	/**
	 * Called when an alarm card was swiped to copy.
	 *
	 * @param  index  The index of the alarm card.
	 */
	override fun onCopySwipe(alarm: NacAlarm, index: Int)
	{
		// Get the alarm card
		val card = getAlarmCardAt(index)

		// Haptic feedback so that the user knows the action was received
		card?.root?.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY)

		// Reset the view on the alarm that was swiped
		alarmCardAdapter!!.notifyItemChanged(index)

		// Check if the max number of alarms was created
		if (hasCreatedMaxAlarms)
		{
			val message = getString(R.string.error_message_max_alarms)

			// Show toast that the max number of alarms were created
			quickToast(this, message)
			return
		}

		// Set the index of the new alarm that will be created. This way, the
		// the snackbar can undo any action on that alarm
		lastAlarmCardAction.index = alarmCardAdapter!!.itemCount

		// Copy the alarm
		copyAlarm(alarm)
	}

	/**
	 * Called when the activity is created.
	 */
	@SuppressLint("NewApi")
	override fun onCreate(savedInstanceState: Bundle?)
	{
		// Setup
		super.onCreate(savedInstanceState)

		// Set the content view
		setContentView(R.layout.act_main)

		// Set member variables
		sharedPreferences = NacSharedPreferences(this)
		root = findViewById(R.id.activity_main)
		toolbar = findViewById(R.id.tb_top_bar)
		nextAlarmTextView = findViewById(R.id.tv_next_alarm)
		recyclerView = findViewById(R.id.rv_alarm_list)
		floatingActionButton = findViewById(R.id.fab_add_alarm)
		alarmCardAdapter = NacCardAdapter()
		snackbar = NacSnackbar(root!!)
		alarmCardAdapterLiveData = NacCardAdapterLiveData()
		alarmCardTouchHelper = NacCardTouchHelper(this)
		permissionRequestManager = NacPermissionRequestManager(this)

		// Set flag that cards need to be measured
		sharedPreferences!!.editCardIsMeasured(false)

		// Setup live data
		setupLiveDataObservers()

		// Setup UI
		toolbar!!.setOnMenuItemClickListener(this)
		setupAlarmCardAdapter()
		setupRecyclerView()

		// NFC tag was scanned for an active alarm
		if (wasNfcScannedForActiveAlarm(intent))
		{
			setNfcTagIntent(intent)
		}

		// Disable the activity alias so that tapping an NFC tag will NOT open
		// the main activity
		disableActivityAlias()
	}

	/**
	 * Create the context menu.
	 */
	override fun onCreateContextMenu(menu: ContextMenu, view: View,
		menuInfo: ContextMenuInfo)
	{
		// Inflate the context menu
		menuInflater.inflate(R.menu.menu_card, menu)

		// Iterate over each menu item
		for (i in 0 until menu.size())
		{
			// Get the menu item
			val item = menu.getItem(i)

			// Set the listener for a menu item
			// TODO: Can change this to a method?
			item.setOnMenuItemClickListener { menuItem: MenuItem ->

				// Get the card holder
				val holder = recyclerView!!.findContainingViewHolder(view) as NacCardHolder?

				// Check to make sure the card holder is not null
				if (holder != null)
				{
					// Check if the show next alarm item was clicked
					if (menuItem.itemId == R.id.menu_show_next_alarm)
					{
						// Show the next time the alarm will run
						showAlarmSnackbar(holder.alarm!!)
					}
					// Check if the NFC tag item was clicked
					else if (menuItem.itemId == R.id.menu_show_nfc_tag_id)
					{
						// Show the NFC tag ID that was set for this alarm
						showNfcTagId(holder.alarm!!)
					}
				}

				// Return
				true
			}
		}
	}

	/**
	 * Called when the options menu is created.
	 */
	override fun onCreateOptionsMenu(menu: Menu): Boolean
	{
		// Inflate the menu bar
		menuInflater.inflate(R.menu.menu_action_bar, menu)

		return true
	}

	/**
	 * Called when an alarm card was swiped to delete.
	 *
	 * @param  index  The index of the alarm card.
	 */
	override fun onDeleteSwipe(alarm: NacAlarm, index: Int)
	{
		// Get the alarm card
		val card = getAlarmCardAt(index)

		// Haptic feedback so that the user knows the action was received
		card?.root?.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY)

		// Set the index of the new alarm that will be created. This way, the
		// the snackbar can undo any action on that alarm
		lastAlarmCardAction.index = index

		// Delete the alarm
		deleteAlarm(alarm)
	}

	/**
	 * Needed for RecyclerView.OnItemTouchListener
	 */
	override fun onInterceptTouchEvent(rv: RecyclerView, ev: MotionEvent): Boolean
	{
		if (ev.action == MotionEvent.ACTION_UP)
		{
			if (snackbar!!.canDismiss)
			{
				snackbar!!.dismiss()
			}
		}
		return false
	}

	/**
	 * Catch when a menu item is clicked.
	 */
	override fun onMenuItemClick(item: MenuItem): Boolean
	{
		// Check which menu item was clicked
		return when (item.itemId)
		{
			// Settings
			R.id.menu_settings ->
			{
				// Create the intent to show the settings activity
				val settingsIntent = Intent(this, NacMainSettingActivity::class.java)

				// Start the activity
				startActivity(settingsIntent)
				true
			}

			// Unknown
			else -> false
		}
	}

	/**
	 * Called when a new intent is received.
	 */
	override fun onNewIntent(intent: Intent)
	{
		// Super
		super.onNewIntent(intent)

		// NFC tag was scanned for the NFC dialog
		if (wasNfcScannedForDialog(intent))
		{
			// Save the NFC tag ID
			saveNfcTagId(intent)

			// Show a toast that NFC is required
			val message = getString(R.string.message_nfc_required)

			quickToast(this, message)

			// Set the listener to null so that it does not get called
			scanNfcTagDialog?.onScanNfcTagListener = null

			// Dismiss the dialog
			scanNfcTagDialog?.dismiss()
		}
		// NFC was scanned for the active alarm
		else if (wasNfcScannedForActiveAlarm(intent))
		{
			// Set the intent
			setNfcTagIntent(intent)

			// Dismiss the active alarm
			dismissActiveAlarm()
		}
	}

	/**
	 * Called when the activity is paused.
	 */
	override fun onPause()
	{
		// Super
		super.onPause()

		// Set the flag that the activity is not shown
		isActivityShown = false

		// Cleanup
		cleanupTimeTickReceiver()
		cleanupShutdownBroadcastReceiver()

		// Stop scanning for NFC
		NacNfc.stop(this)
	}

	/**
	 * Note: Needed for RecyclerView.OnItemTouchListener
	 */
	override fun onRequestDisallowInterceptTouchEvent(disallowIntercept: Boolean)
	{
	}

	/**
	 * Called when the activity is resumed.
	 */
	override fun onResume()
	{
		// Super
		super.onResume()

		// Check if the main activity should be refreshed
		if (sharedPreferences!!.shouldRefreshMainActivity)
		{
			// Refresh the activity
			refreshMainActivity()
			return
		}

		// Set flag that the activity is being shown
		isActivityShown = true

		// Set the next alarm text
		setNextAlarmMessage()

		// Setup UI
		setupFloatingActionButton()
		setupInitialDialogToShow()

		// Register the time tick receiver
		registerReceiver(timeTickReceiver, IntentFilter(Intent.ACTION_TIME_TICK))

		// Register the shutdown receiver
		registerReceiver(shutdownBroadcastReceiver, IntentFilter(Intent.ACTION_SHUTDOWN))

		// Start scanning for NFC
		NacNfc.start(this)

		// Add alarm from SET_ALARM intent (if it is present in intent)
		addSetAlarmFromIntent()
	}

	/**
	 * Note: Needed for RecyclerView.OnItemTouchListener
	 */
	override fun onTouchEvent(rv: RecyclerView, e: MotionEvent)
	{
	}

	/**
	 * Called when theh user wants to use any NFC tag.
	 */
	override fun onUseAnyNfcTag(alarm: NacAlarm)
	{
		// Set the default (empty) NFC tag ID.
		alarm.nfcTagId = ""

		// Update the alarm
		alarmViewModel.update(alarm)

		// Reschedule the alarm
		NacScheduler.update(this, alarm)

		// Toast to the that NFC is required
		quickToast(this, R.string.message_nfc_required)

		// Cleanup the dialog
		cleanupScanNfcTagDialog()
	}

	/**
	 * Needed for NacAlarmCardAdapter.OnViewHolderBoundListener.
	 */
	override fun onViewHolderBound(holder: NacCardHolder, index: Int)
	{
		// Check if the alarm card has not been measured
		if (!sharedPreferences!!.cardIsMeasured)
		{
			// Measure the card
			measureCard(holder)
		}

		// Get the alarm
		val alarm = alarmCardAdapter!!.getAlarmAt(index)

		// Check if the alarm was recently added
		if (recentlyAddedAlarmIds.contains(alarm.id))
		{
			// Interact with card holder of that alarm, showing the time dialog
			// and expanding the card if the user wants that
			holder.interact()

			// Remove the alarm from the recently added list
			recentlyAddedAlarmIds.remove(alarm.id)
		}
	}

	/**
	 * Needed for NacAlarmCardAdapter.OnViewHolderCreatedListener.
	 */
	override fun onViewHolderCreated(holder: NacCardHolder)
	{
		holder.onCardCollapsedListener = this

		// Set the listener for when the delete button is clicked
		holder.onCardDeleteClickedListener = OnCardDeleteClickedListener { _, alarm ->

			// Delete the alarm
			deleteAlarm(alarm)

		}

		// Set the listener for when the media button is clicked
		holder.onCardMediaClickedListener = OnCardMediaClickedListener { _, alarm ->

			// Create an intent for the media activity with the alarm attached
			val intent = toIntent(this, NacMediaActivity::class.java, alarm)

			// Start the activity
			startActivity(intent)

		}

		// Set the listener for when the audio options button is clicked
		holder.onCardAudioOptionsClickedListener = OnCardAudioOptionsClickedListener { _, alarm ->

			// Show the audio options dialog
			showAudioOptionsDialog(alarm)

		}

		holder.onCardUpdatedListener = this
		holder.onCardUseNfcChangedListener = this
		holder.setOnCreateContextMenuListener(this)
	}

	/**
	 * Prepare an active alarm to be shown to the user.
	 *
	 *
	 * If the alarm is null, or the activity is not shown, it will not be run.
	 *
	 * @param  alarm  An active alarm.
	 */
	private fun prepareActiveAlarm(alarm: NacAlarm?)
	{
		// Check if the alalarm is not null
		if (alarm != null)
		{
			setNfcTagAlarm(alarm)
		}

		// Check if NFC tag is ready
		if (isNfcTagReady)
		{
			// Dismiss the active alarm
			dismissActiveAlarm()
		}
		// Check if the main activity is shown and should show the alarm activity
		else if (isActivityShown && shouldShowAlarmActivity(alarm))
		{
			// TODO: This caused the active alarm to show up a million times!
			//NacSharedPreferences shared = this.getSharedPreferences();
			//Remove this setting: shared.getPreventAppFromClosing()?

			// Check if the alarm service is not running
			if (!NacActiveAlarmService.isRunning(this))
			{
				// Run the alarm service
				NacActiveAlarmService.startService(this, alarm)
			}

			// Start the alarm activity
			startAlarmActivity(this, alarm)
		}
	}

	/**
	 * Refresh alarms that will alarm soon.
	 */
	private fun refreshAlarmsThatWillAlarmSoon()
	{
		// Iterate over each alarm card in the adapter
		for (i in 0 until alarmCardAdapter!!.itemCount)
		{
			// Get the card and the alarm
			val card = getAlarmCardAt(i)
			val alarm = alarmCardAdapter!!.getAlarmAt(i)

			// Check if the alarm will alarm soon and the card needs to be updated
			if (card != null && alarm.willAlarmSoon() && card.shouldRefreshDismissView())
			{
				// Refresh the alarm
				alarmCardAdapter!!.notifyItemChanged(i)
			}
		}
	}

	/**
	 * Refresh the main activity.
	 */
	private fun refreshMainActivity()
	{
		// Disable that flag indicating that the main activity should refresh
		sharedPreferences!!.editShouldRefreshMainActivity(false)

		// Recreate the activity
		recreate()
	}

	/**
	 * Restore an alarm and add it back to the database.
	 *
	 * @param  alarm  An alarm.
	 */
	private fun restoreAlarm(alarm: NacAlarm)
	{
		lifecycleScope.launch {

			// Get the message and action for the snackbar
			val message = getString(R.string.message_alarm_restore)
			val action = getString(R.string.action_undo)

			// Insert the alarm
			alarmViewModel.insert(alarm)

			// Reschedule the alarm
			NacScheduler.update(this@NacMainActivity, alarm)

			// Set the last action that was executed
			lastAlarmCardAction[alarm] = NacLastAlarmCardAction.Type.RESTORE

			// Show the snackbar
			showSnackbar(message, action, onSwipeSnackbarActionListener)

		}
	}

	/**
	 * Save the scanned NFC tag ID.
	 */
	private fun saveNfcTagId(intent: Intent)
	{
		// Check if the intent corresponds with the scan NFC tag dialog
		if (!wasNfcScannedForDialog(intent))
		{
			return
		}

		// Get the NFC tag or return if it is null
		val nfcTag = getTag(intent) ?: return

		// Get the alarm from the dialog or return if it is null
		val alarm = scanNfcTagDialog!!.alarm ?: return

		// Set the NFC tag ID
		alarm.nfcTagId = parseId(nfcTag).toString()

		// Update the alarm
		alarmViewModel.update(alarm)

		// Reschedule the alarm
		NacScheduler.update(this, alarm)
	}

	/**
	 * Set the next alarm message in the text view.
	 */
	private fun setNextAlarmMessage()
	{
		// Set the next alarm message from the current list of alarms
		this.setNextAlarmMessage(alarmCardAdapter!!.currentList)
	}

	/**
	 * Set the next alarm message in the text view.
	 */
	private fun setNextAlarmMessage(alarms: List<NacAlarm>)
	{
		// Get the next alarm message
		val message = getNextAlarmMessage(alarms)

		// Set the message in the text view
		nextAlarmTextView!!.text = message
	}

	/**
	 * Set the NFC tag active alarm.
	 *
	 * @param  activeAlarm  The active alarm to use in conjunction with the NFC tag.
	 */
	private fun setNfcTagAlarm(activeAlarm: NacAlarm)
	{
		if (nfcTag == null)
		{
			nfcTag = NacNfcTag(activeAlarm)
		}
		else
		{
			nfcTag!!.activeAlarm = activeAlarm
		}
	}

	/**
	 * Set the NFC tag action and ID from an Intent.
	 *
	 * @param  nfcIntent  The intent received when scanning an NFC tag.
	 */
	private fun setNfcTagIntent(nfcIntent: Intent)
	{
		if (nfcTag == null)
		{
			nfcTag = NacNfcTag(nfcIntent)
		}
		else
		{
			nfcTag!!.setNfcId(nfcIntent)
			nfcTag!!.setNfcAction(nfcIntent)
		}
	}

	/**
	 * Setup the alarm card adapter.
	 */
	private fun setupAlarmCardAdapter()
	{
		// Setup the listeners
		alarmCardAdapter!!.onViewHolderBoundListener = this
		alarmCardAdapter!!.onViewHolderCreatedListener = this

		// Attach the recycler view to the touch helper
		alarmCardTouchHelper!!.attachToRecyclerView(recyclerView)
	}

	/**
	 * Setup the floating action button.
	 */
	private fun setupFloatingActionButton()
	{
		// Get the theme color
		val color = ColorStateList.valueOf(sharedPreferences!!.themeColor)

		// Set the listener
		floatingActionButton!!.setOnClickListener(mFloatingActionButtonListener)

		// Set the color
		floatingActionButton!!.backgroundTintList = color
	}

	/**
	 * Run the setup when it is the app's first time running.
	 */
	private fun setupForAppFirstRun()
	{
		// Set the flags indicating that this is no longer the app's first run
		// and that statistics does not need to be started, since they have alaready started
		sharedPreferences!!.editAppFirstRun(this, false)
		sharedPreferences!!.editAppStartStatistics(false)

		// Add the first alarm
		addFirstAlarm()
	}

	/**
	 * Setup an initial dialog, if any, that need to be shown.
	 */
	private fun setupInitialDialogToShow()
	{
		// Check if there are any permissions that need to be requested
		if (permissionRequestManager!!.count() > 0)
		{
			// Request permissions
			permissionRequestManager!!.requestPermissions(this)
		}
		// Attempt to show the What's new dialog
		else if (shouldShowWhatsNewDialog)
		{
			// Show the What's New dialog
			showWhatsNewDialog()
		}
		// Attempt to request to show the Google Play Store rate my app thing
		else if (NacRateMyApp.request(this, sharedPreferences!!))
		{
		}
	}

	/**
	 * Setup LiveData observers.
	 */
	private fun setupLiveDataObservers()
	{
		// Observer is called when list of all alarms changes. Including when the app
		// starts and the list is initially empty
		alarmViewModel.allAlarms.observe(this) { alarms: List<NacAlarm> ->

			// Check if statitistcs have not started yet
			if (!sharedPreferences!!.appStartStatistics)
			{
				// Setup statistics
				setupStatistics(alarms)
			}

			// Check if no cards are expanded
			if (cardsExpandedCount == 0)
			{
				// Merge and sort the alarms
				alarmCardAdapterLiveData!!.mergeSort(alarms)
			}
			// One or more cards is expanded
			else
			{
				// Merge the alarms but do not sort yet
				alarmCardAdapterLiveData!!.merge(alarms)
			}

			// Set the next alarm message
			this.setNextAlarmMessage(alarms)
		}

		// Observe when the active alarm changes
		alarmViewModel.activeAlarm.observe(this) { alarm: NacAlarm? ->

			// Prepare the active alarm
			prepareActiveAlarm(alarm)

		}

		// Observe any changes to the alarms in the adapter
		alarmCardAdapterLiveData!!.observe(this) { alarms ->

			 // Alarm list has changed.
			 // TODO: There is a race condition between snoozing an alarm, writing to the
			 // database, and refreshing the main activity.

			// If this is the first time the app is running, set the flags accordingly
			if (sharedPreferences!!.getAppFirstRun(this))
			{
				setupForAppFirstRun()
			}

			// Check if upcoming alarm notifications should be shown
			if (sharedPreferences!!.upcomingAlarmNotification)
			{
				// Update the upcoming alarm notification
				this.updateUpcomingNotification(alarms)
			}

			// Update the alarm adapter
			alarmCardAdapter!!.storeIndicesOfExpandedCards(recyclerView!!)
			alarmCardAdapter!!.submitList(alarms)

			// Check if the main activity should be refreshed and if so, refresh it
			// TODO: Why is this here?
			if (sharedPreferences!!.shouldRefreshMainActivity)
			{
				refreshMainActivity()
			}

		}
	}

	/**
	 * Setup the recycler view.
	 */
	private fun setupRecyclerView()
	{
		// Create the divider drawable
		val padding = resources.getDimensionPixelSize(R.dimen.normal)
		val drawable = ContextCompat.getDrawable(this, R.drawable.card_divider)
		val divider = InsetDrawable(drawable, padding, 0, padding, 0)

		// Create the item decoration
		val decoration = DividerItemDecoration(this, LinearLayoutManager.VERTICAL)

		// Set the divider on the decoration
		decoration.setDrawable(divider)

		// Add the decoration to the recycler view. This will divide every item by this
		// decoration
		recyclerView!!.addItemDecoration(decoration)

		// Setup everything else
		recyclerView!!.adapter = alarmCardAdapter
		recyclerView!!.layoutManager = NacLayoutManager(this)
		recyclerView!!.addOnItemTouchListener(this)
		recyclerView!!.setHasFixedSize(true)
	}

	/**
	 * Setup statistics, and start collecting the data.
	 *
	 * Note: This is only done if this is not the app's first time running and
	 *       the statistics table was not created yet, so it should be started.
	 *
	 * @param  alarms  List of alarms.
	 */
	private fun setupStatistics(alarms: List<NacAlarm>)
	{
		lifecycleScope.launch {

			// Check if nothing has been created
			if (statisticViewModel.createdCount() == 0L)
			{
				// Iterate over each alarm
				for (a in alarms)
				{
					// Add a created alarm row to the table
					statisticViewModel.insertCreated()
				}
			}

			// Disable the flag indicating that statistics does not need to be started
			// anymore
			sharedPreferences!!.editAppStartStatistics(false)

		}
	}

	/**
	 * Check if the alarm activity should be shown.
	 *
	 * @return True if the alarm activity should be shown, and False otherwise.
	 */
	private fun shouldShowAlarmActivity(alarm: NacAlarm?): Boolean
	{
		return (alarm != null) && !wasScanned(intent)
	}

	/**
	 * Show a snackbar for the alarm.
	 */
	private fun showAlarmSnackbar(alarm: NacAlarm)
	{
		// Get the message and action for the snackbar
		val message = NacCalendar.getMessageWillRun(sharedPreferences, alarm)
		val action = getString(R.string.action_alarm_dismiss)

		// Show the snackbar
		showSnackbar(message, action)
	}

	/**
	 * Show the audio options dialog.
	 */
	private fun showAudioOptionsDialog(alarm: NacAlarm)
	{
		// Create the dialog
		val dialog = NacAlarmAudioOptionsDialog()

		// Setup the dialog
		dialog.alarmId = alarm.id
		dialog.onAudioOptionClickedListener = OnAudioOptionClickedListener { alarmId, which ->

			lifecycleScope.launch {

				// Set the alarm that had the audio options button clicked
				audioOptionsAlarm = alarmViewModel.findAlarm(alarmId)

				// Determine which option to do
				when (which)
				{
					0 -> showAudioSourceDialog()
					1 -> showDismissEarlyDialog()
					2 -> showGraduallyIncreaseVolumeDialog()
					3 -> showRestrictVolumeDialog()
					4 -> showTextToSpeechDialog()
					else ->
					{
					}
				}

			}

		}

		// Show the dialog
		dialog.show(supportFragmentManager, NacAlarmAudioOptionsDialog.TAG)
	}

	/**
	 * Show the audio source dialog.
	 */
	private fun showAudioSourceDialog()
	{
		// Create the dialog
		val dialog = NacAudioSourceDialog()

		// Set the default audio source
		dialog.defaultAudioSource = audioOptionsAlarm!!.audioSource

		// Setup the listener
		dialog.onAudioSourceSelectedListener = OnAudioSourceSelectedListener { audioSource ->

			// Set the audio source of the audio options alarm
			audioOptionsAlarm!!.audioSource = audioSource

			// Update the alarm
			alarmViewModel.update(audioOptionsAlarm!!)

			// Reschedule the alarm
			NacScheduler.update(this, audioOptionsAlarm!!)

		}

		// Show the dialog
		dialog.show(supportFragmentManager, NacAudioSourceDialog.TAG)
	}

	/**
	 * Show the dismiss early dialog.
	 */
	private fun showDismissEarlyDialog()
	{
		// Create the dialog
		val dialog = NacDismissEarlyDialog()

		// Set the default values
		dialog.defaultShouldDismissEarly = audioOptionsAlarm!!.shouldUseDismissEarly
		dialog.defaultShouldDismissEarlyIndex = audioOptionsAlarm!!.dismissEarlyIndex

		// Setup the listener
		dialog.onDismissEarlyOptionSelectedListener = OnDismissEarlyOptionSelectedListener { useDismissEarly, index ->

			// Set the new dismiss early values
			audioOptionsAlarm!!.useDismissEarly = useDismissEarly
			audioOptionsAlarm!!.setDismissEarlyTimeFromIndex(index)

			// Update the alarm
			alarmViewModel.update(audioOptionsAlarm!!)

			// Reschedule the alarm
			NacScheduler.update(this, audioOptionsAlarm!!)

		}

		// Show the dialog
		dialog.show(supportFragmentManager, NacDismissEarlyDialog.TAG)
	}

	/**
	 * Show the gradually increase volume dialog.
	 */
	private fun showGraduallyIncreaseVolumeDialog()
	{
		// Create the dialog
		val dialog = NacGraduallyIncreaseVolumeDialog()

		// Set the default value
		dialog.defaultShouldGraduallyIncreaseVolume = audioOptionsAlarm!!.shouldGraduallyIncreaseVolume

		// Setup the listener
		dialog.onGraduallyIncreaseVolumeListener = OnGraduallyIncreaseVolumeListener { shouldIncrease ->

			// Set the new gradually increase value
			audioOptionsAlarm!!.shouldGraduallyIncreaseVolume = shouldIncrease

			// Update the alarm
			alarmViewModel.update(audioOptionsAlarm!!)

			// Reschedule the alarm
			NacScheduler.update(this, audioOptionsAlarm!!)

		}

		// Show the dialog
		dialog.show(supportFragmentManager, NacGraduallyIncreaseVolumeDialog.TAG)
	}

	/**
	 * Show a snackbar for the next alarm that will run.
	 */
	private fun showNextAlarmSnackbar()
	{
		// Get the next alarm
		val nextAlarm = NacCalendar.getNextAlarm(alarmCardAdapter!!.currentList)

		// Get the message and action for the snackbar
		val message = NacCalendar.getMessageNextAlarm(sharedPreferences, nextAlarm)
		val action = getString(R.string.action_alarm_dismiss)

		// Show the snackbar
		showSnackbar(message, action)
	}

	/**
	 * Show the saved NFC tag ID of the given alarm.
	 */
	private fun showNfcTagId(alarm: NacAlarm)
	{
		// Get the default locale
		val locale = Locale.getDefault()

		// Build the message
		val message: String = if (alarm.nfcTagId.isNotEmpty())
		{
			// Get the string to show a specific NFC tag
			val nfcId = getString(R.string.message_show_nfc_tag_id)

			String.format(locale, "%1\$s %2\$s", nfcId, alarm.nfcTagId)
		}
		else
		{
			// Get the string to show any NFC tag
			val anyNfc = getString(R.string.message_any_nfc_tag_id)

			String.format(locale, "%1\$s", anyNfc)
		}

		// Toast the message
		quickToast(this, message)
	}

	/**
	 * Show the restrict volume dialog.
	 */
	private fun showRestrictVolumeDialog()
	{
		// Create the dialog
		val dialog = NacRestrictVolumeDialog()

		// Set the default value
		dialog.defaultShouldRestrictVolume = audioOptionsAlarm!!.shouldRestrictVolume

		// Setup the listener
		dialog.onRestrictVolumeListener = OnRestrictVolumeListener { shouldRestrict ->

			// Set the new restrict volume value
			audioOptionsAlarm!!.shouldRestrictVolume = shouldRestrict

			// Update the alarm
			alarmViewModel.update(audioOptionsAlarm!!)

			// Reschedule the alarm
			NacScheduler.update(this, audioOptionsAlarm!!)

		}

		// Show the dialog
		dialog.show(supportFragmentManager, NacRestrictVolumeDialog.TAG)
	}
	/**
	 * Create a snackbar message.
	 */
	/**
	 * @see .showSnackbar
	 */
	private fun showSnackbar(message: String, action: String,
		listener: View.OnClickListener? = null)
	{
		snackbar!!.show(message, action, listener, true)
	}

	/**
	 * Show the text-to-speech dialog.
	 */
	private fun showTextToSpeechDialog()
	{
		// Create the dialog
		val dialog = NacTextToSpeechDialog()

		// Set the default values
		dialog.defaultUseTts = audioOptionsAlarm!!.shouldUseTts
		dialog.defaultTtsFrequency = audioOptionsAlarm!!.ttsFrequency

		// Setup the listener
		dialog.onTextToSpeechOptionsSelectedListener = OnTextToSpeechOptionsSelectedListener { useTts, freq ->

			// Set the new text to speech values
			audioOptionsAlarm!!.useTts = useTts
			audioOptionsAlarm!!.ttsFrequency = freq

			// Update the alarm
			alarmViewModel.update(audioOptionsAlarm!!)

			// Reschedule the alarm
			NacScheduler.update(this, audioOptionsAlarm!!)

		}

		// Show the dialog
		dialog.show(supportFragmentManager, NacTextToSpeechDialog.TAG)
	}

	/**
	 * Show a snackbar for the updated alarm.
	 *
	 *
	 * If this alarm is disabled, a snackbar for the next alarm will be shown.
	 */
	private fun showUpdatedAlarmSnackbar(alarm: NacAlarm)
	{
		// Check if the alarm is enabled
		if (alarm.isEnabled)
		{
			// Show the snackbar for the alarm
			showAlarmSnackbar(alarm)
		}
		else
		{
			// Show the snackbar for the next alarm that will run
			showNextAlarmSnackbar()
		}
	}

	/**
	 * Show the What's New dialog.
	 */
	private fun showWhatsNewDialog()
	{
		// Create the dialog
		val dialog = NacWhatsNewDialog()

		// Setup the listener
		dialog.onReadWhatsNewListener = OnReadWhatsNewListener {

			// Set the previous app version as the current version. This way, the What's
			// New dialog does not show again
			sharedPreferences!!.editPreviousAppVersion(BuildConfig.VERSION_NAME)

		}

		// Show the dialog
		dialog.show(supportFragmentManager, NacWhatsNewDialog.TAG)
	}

	/**
	 * Update the notification.
	 *
	 * TODO: Check if race condition with this being called after submitList?
	 * Should I just pass a list of alarms to this method?
	 */
	private fun updateUpcomingNotification(alarms: List<NacAlarm>)
	{
		// Create the notification
		val notification = NacUpcomingAlarmNotification(this)

		// Set the alarms
		notification.alarmList = alarms

		// Show the notification
		notification.show()
	}

	/**
	 * @return True if an NFC tag was scanned to dismiss an alarm, and False
	 * otherwise. This is to say that if an NFC tag was scanned for the
	 * dialog, this would return False.
	 */
	private fun wasNfcScannedForActiveAlarm(intent: Intent): Boolean
	{
		return wasScanned(intent) && !wasNfcScannedForDialog(intent)
	}

	/**
	 * @return True if an NFC tag was scanned while the Scan NFC Tag dialog was
	 * open, and False otherwise.
	 */
	private fun wasNfcScannedForDialog(intent: Intent): Boolean
	{
		return (scanNfcTagDialog != null) && wasScanned(intent)
	}

}