package com.nfcalarmclock;

import android.content.Context;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.LayoutInflater;
import android.view.MenuItem;
//import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

/**
 * Alarm card adapter.
 */
@SuppressWarnings("UnnecessaryInterfaceModifier")
public class NacAlarmCardAdapter
	extends ListAdapter<NacAlarm, NacCardHolder>
{

	/**
	 * Listener for when an alarm card is created.
	 */
	public interface OnViewHolderCreatedListener
	{
		public void onViewHolderCreated(NacCardHolder holder);
	}

	/**
	 * Listener for when an alarm card is created.
	 */
	private OnViewHolderCreatedListener mOnViewHolderCreatedListener;

	/**
	 * Main activity root view.
	 */
	private final CoordinatorLayout mRoot;

	/**
	 * RecyclerView containing list of alarm cards.
	 */
	private final RecyclerView mRecyclerView;

	/**
	 * Shared preferences.
	 */
	private final NacSharedPreferences mSharedPreferences;

	/**
	 * Indicator that the alarm was added through the floating action button.
	 */
	private boolean mWasAddedWithFloatingActionButton;

	/**
	 */
	public static final DiffUtil.ItemCallback<NacAlarm> DIFF_CALLBACK =
		new DiffUtil.ItemCallback<NacAlarm>() {

			/**
			 */
			@Override
			public boolean areItemsTheSame(@NonNull NacAlarm oldAlarm,
				@NonNull NacAlarm newAlarm)
			{
				return oldAlarm.getId() == newAlarm.getId();
			}

			/**
			 */
			@Override
			public boolean areContentsTheSame(@NonNull NacAlarm oldAlarm,
				@NonNull NacAlarm newAlarm)
			{
				// NOTE: if you use equals, your object must properly override Object#equals()
				// Incorrectly returning false here will result in too many animations.
				return oldAlarm.equals(newAlarm);
			}
		};

	/**
	 */
	public NacAlarmCardAdapter(Context context)
	{
		super(DIFF_CALLBACK);

		AppCompatActivity activity = (AppCompatActivity) context;

		CoordinatorLayout root = activity.findViewById(R.id.activity_main);
		RecyclerView rv = root.findViewById(R.id.content_alarm_list);
		this.mRoot = root;
		this.mRecyclerView = rv;
		this.mSharedPreferences = new NacSharedPreferences(context);
		this.mWasAddedWithFloatingActionButton = false;

		setHasStableIds(true);
	}

	///**
	// * Create a new alarm and add it to the list.
	// *
	// * @see #addAlarm(NacAlarm)
	// */
	//public void addAlarm()
	//{
	//	Context context = this.getContext();
	//	NacSharedPreferences shared = this.getSharedPreferences();
	//	int id = this.getUniqueId();
	//	NacAlarm alarm = new NacAlarm.Builder(context)
	//		.setId(id)
	//		.setRepeat(shared.shouldRepeat())
	//		.setDays(shared.getDays())
	//		.setUseNfc(shared.shouldUseNfc())
	//		.setVibrate(shared.shouldVibrate())
	//		.setVolume(shared.getVolume())
	//		.setMedia(context, shared.getMediaPath())
	//		.setName(shared.getName())
	//		.build();

	//	this.addAlarm(alarm);
	//}

	///**
	// * @see #addAlarm(NacAlarm, int)
	// *
	// * @param  alarm  The alarm to add.
	// */
	//public void addAlarm(NacAlarm alarm)
	//{
	//	int index = this.whereToInsertAlarm(alarm);
	//	this.addAlarm(alarm, index);
	//}

	///**
	// * Add an alarm to the given index in the list.
	// *
	// * @param  alarm  The alarm to add.
	// * @param  index  The index to add the alarm.
	// */
	//public void addAlarm(NacAlarm alarm, int index)
	//{
	//	if (this.atMaxAlarmCapacity())
	//	{
	//		this.toastMaxAlarmsError();
	//		return;
	//	}

	//	Context context = this.getContext();

	//	NacTaskWorker.addAlarm(context, alarm);
	//	this.notifyInsertAlarm(alarm, index);
	//	this.updateNotification();
	//}

	/**
	 * @return True if the maximum number of alarms has been created, and False
	 *     otherwise.
	 */
	public boolean atMaxAlarmCapacity()
	{
		NacSharedConstants cons = this.getSharedConstants();
		int size = getItemCount();
		//int size = this.size();

		return ((size+1) > cons.getMaxAlarms());
		//return ((size+1) <= cons.getMaxAlarms());
	}

	/**
	 * Build the alarm list.
	 */
	public void build()
	{
		Context context = this.getContext();
		NacSharedPreferences shared = this.getSharedPreferences();
		//this.mAlarmList = NacDatabase.read(context);

		//if (shared.getAppFirstRun())
		//{
		//	for (NacAlarm a : this.getAlarms())
		//	{
		//		this.onAlarmChange(a);
		//	}

		//	shared.editAppFirstRun(false);
		//}

		/*
		 * This might have to do with why build() is used in onResume()?
		 */
		shared.editCardIsMeasured(false);
		//this.getTouchHelper().setRecyclerView(this.getRecyclerView());
		//this.getTouchHelper().reset();
		this.sort();
		//this.updateNotification();
		//notifyDataSetChanged();
	}

	/**
	 * @return True if the alarm can be inserted at the current state in the
	 *         alarm list, or False otherwise.
	 */
	private boolean canInsertAlarm(NacAlarm alarmToInsert, NacAlarm alarmInList,
		Calendar nextRun)
	{
		NacSharedPreferences shared = this.getSharedPreferences();
		boolean insertInUse = alarmToInsert.isInUse(shared);
		boolean listInUse = alarmInList.isInUse(shared);
		boolean insertEnabled = alarmToInsert.isEnabled();
		boolean listEnabled = alarmInList.isEnabled();

		if (insertInUse && !listInUse)
		{
			return true;
		}
		else if (insertInUse && listInUse)
		{
			Calendar cal = NacCalendar.getNextAlarmDay(alarmInList);
			return nextRun.before(cal);
		}

		if (insertEnabled && !listEnabled)
		{
			return true;
		}
		else if (insertEnabled == listEnabled)
		{
			Calendar cal = NacCalendar.getNextAlarmDay(alarmInList);
			return nextRun.before(cal);
		}

		return false;
	}

	///**
	// * Copy the alarm at the given index.
	// *
	// * @param  index  The index of the alarm to copy.
	// */
	//public void copyAlarm(int index)
	//{
	//	notifyItemChanged(index);

	//	if (this.atMaxAlarmCapacity())
	//	{
	//		this.toastMaxAlarmsError();
	//		return;
	//	}

	//	NacSharedConstants cons = this.getSharedConstants();
	//	NacAlarm alarm = this.getAlarmAt(index);
	//	NacAlarm alarmCopy = alarm.copy(this.getUniqueId());
	//	String message = cons.getMessageAlarmCopy();
	//	String action = cons.getActionUndo();

	//	this.addAlarm(alarmCopy, index+1);
	//	this.undo(alarmCopy, index+1, Undo.Type.COPY);
	//	this.showSnackbar(message, action, this);
	//}

	///**
	// * Delete the alarm at the given index.
	// *
	// * @param  index  The index of the alarm to delete.
	// */
	//public void deleteAlarm(int index)
	//{
	//	Context context = this.getContext();
	//	NacSharedConstants cons = this.getSharedConstants();
	//	NacAlarm alarm = this.getAlarmAt(index);
	//	String message = cons.getMessageAlarmDelete();
	//	String action = cons.getActionUndo();

	//	NacTaskWorker.deleteAlarm(context, alarm);
	//	this.notifyDeleteAlarm(index);
	//	this.updateNotification();
	//	this.undo(alarm, index, Undo.Type.DELETE);
	//	this.showSnackbar(message, action, this);
	//}

	/**
	 * Call the listener for when an alarm card is created.
	 */
	public void callOnViewHolderCreatedListener(NacCardHolder holder)
	{
		OnViewHolderCreatedListener listener = this.getOnViewHolderCreatedListener();

		if (listener != null)
		{
			listener.onViewHolderCreated(holder);
		}
	}

	/**
	 * @return The index at which the given alarm is found.
	 */
	public int findAlarm(NacAlarm alarm)
	{
		if (alarm == null)
		{
			return -1;
		}

		//List<NacAlarm> alarmList = this.getAlarms();
		//int size = this.size();
		List<NacAlarm> alarmList = getCurrentList();
		int size = getItemCount();
		long id = alarm.getId();

		for (int i=0; i < size; i++)
		{
			NacAlarm a = alarmList.get(i);

			if (a.getId() == id)
			{
				return i;
			}
		}

		return -1;
	}

	/**
	 * @return The alarm at the given index.
	 */
	public NacAlarm getAlarmAt(int index)
	{
		int size = getItemCount();

		if ((index < 0) || (index >= size))
		{
			return null;
		}
		else
		{
			return getCurrentList().get(index);
		}
	}

	/**
	 * @return The NacCardHolder for a given alarm.
	 *
	 * TODO: Change this to be used in main activity.
	 */
	public NacCardHolder getCardHolder(NacAlarm alarm)
	{
		RecyclerView rv = this.getRecyclerView();
		if ((alarm == null) || (rv == null))
		{
			return null;
		}

		long id = alarm.getId();
		if (id <= 0)
		{
			return null;
		}

		return (NacCardHolder) rv.findViewHolderForItemId((int)id);
	}

	/**
	 * @return The context.
	 */
	private Context getContext()
	{
		return this.getRoot().getContext();
	}

	/**
	 * @return The unique ID of an alarm. Used alongside setHasStableIds().
	 */
	@Override
	public long getItemId(int position)
	{
		return this.getAlarmAt(position).getId();
	}

	///**
	// * @return The next alarm that will be triggered.
	// */
	//public NacAlarm getNextAlarm()
	//{
	//	List<NacAlarm> alarms = getCurrentList();
	//	return NacCalendar.getNextAlarm(alarms);
	//}

	/**
	 * @return The listener for when an alarm card is created.
	 */
	public OnViewHolderCreatedListener getOnViewHolderCreatedListener()
	{
		return this.mOnViewHolderCreatedListener;
	}

	/**
	 * @return The RecyclerView.
	 */
	private RecyclerView getRecyclerView()
	{
		return this.mRecyclerView;
	}

	/**
	 * @return The root view.
	 */
	private View getRoot()
	{
		return this.mRoot;
	}

	/**
	 * @return The shared constants.
	 */
	private NacSharedConstants getSharedConstants()
	{
		return this.getSharedPreferences().getConstants();
	}

	/**
	 * @return The shared preferences.
	 */
	private NacSharedPreferences getSharedPreferences()
	{
		return this.mSharedPreferences;
	}

	/**
	 * @return The list of alarms, in sort order, from soonest to latest, with
	 *         disabled alarms at the end.
	 */
	public List<NacAlarm> getSortedAlarms()
	{
		NacSharedPreferences shared = this.getSharedPreferences();
		List<NacAlarm> inUseAlarms = new ArrayList<>();
		List<NacAlarm> enabledAlarms = new ArrayList<>();
		List<NacAlarm> disabledAlarms = new ArrayList<>();
		List<NacAlarm> sorted = new ArrayList<>();

		for (NacAlarm a : getCurrentList())
		{
			List<NacAlarm> list;
			if (a.isInUse(shared))
			{
				list = inUseAlarms;
			}
			else if (a.isEnabled())
			{
				list = enabledAlarms;
			}
			else
			{
				list = disabledAlarms;
			}

			Calendar next = NacCalendar.getNextAlarmDay(a);
			int pos = 0;

			for (NacAlarm e : list)
			{
				Calendar cal = NacCalendar.getNextAlarmDay(e);

				if (next.before(cal))
				{
					break;
				}

				pos++;
			}

			list.add(pos, a);
		}

		sorted.addAll(inUseAlarms);
		sorted.addAll(enabledAlarms);
		sorted.addAll(disabledAlarms);
		return sorted;
	}

	/**
	 * @return The index where the sorted alarm should be inserted.
	 */
	private int getSortInsertIndex(NacAlarm alarm, int index)
	{
		if (alarm == null)
		{
			return -1;
		}

		int whereIndex = this.whereToInsertAlarm(alarm);
		return (whereIndex > index) ? whereIndex-1 : whereIndex;
	}

	/**
	 * @return The touch helper.
	 */
	//private NacCardTouchHelper getTouchHelper()
	//{
	//	return this.mTouchHelper;
	//}

	///**
	// * @return The undo object.
	// */
	//private Undo getUndo()
	//{
	//	return this.mUndo;
	//}

	///**
	// * Determine a unique integer ID number to use for newly created alarms.
	// */
	//public int getUniqueId()
	//{
	//	List<Integer> used = new ArrayList<>();

	//	for (NacAlarm a : getCurrentList())
	//	{
	//		used.add(a.getId());
	//	}

	//	for (int i=1; i < Integer.MAX_VALUE; i+=7)
	//	{
	//		if (!used.contains(i))
	//		{
	//			return i;
	//		}
	//	}

	//	return -1;
	//}

	///**
	// * Highlight an alarm card.
	// */
	//public void highlight(NacAlarm alarm)
	//{
	//	NacCardHolder holder = this.getCardHolder(alarm);
	//	if (holder != null)
	//	{
	//		holder.highlight();
	//	}
	//}

	/**
	 * @return True if is a valid sort index, and False otherwise.
	 */
	private boolean isValidSortIndex(int index, int insertIndex)
	{
		return (index >= 0) && (index != insertIndex);
	}

	///**
	// * Delete an alarm and notify any registered observers.
	// */
	//public void notifyDeleteAlarm(int index)
	//{
	//	if (index < 0)
	//	{
	//		return;
	//	}

	//	List<NacAlarm> alarmList = this.getAlarms();

	//	alarmList.remove(index);
	//	notifyItemRemoved(index);
	//}

	///**
	// * Insert an alarm and notify any registered observers.
	// */
	//public void notifyInsertAlarm(NacAlarm alarm, int index)
	//{
	//	if ((alarm == null) || (index < 0))
	//	{
	//		return;
	//	}

	//	List<NacAlarm> alarmList = this.getAlarms();

	//	alarmList.add(index, alarm);
	//	notifyItemInserted(index);
	//}

	///**
	// * Called when the alarm has been changed.
	// *
	// * @param  alarm  The alarm that was changed.
	// */
	//@Override
	//public void onAlarmChange(NacAlarm alarm)
	//{
	//	Context context = this.getContext();

	//	alarm.print();
	//	if (alarm.wasChanged())
	//	{
	//		if (alarm.wasUseNfcChanged())
	//		{
	//			this.callOnUseNfcChangeListener(alarm);
	//		}
	//		else if (!alarm.isChangeTrackerLatched())
	//		{
	//			this.showAlarmChange(alarm);
	//			this.sortHighlight(alarm);
	//		}
	//	}

	//	NacTaskWorker.updateAlarm(context, alarm);
	//	//this.updateNotification();
	//}

	/**
	 * Setup the alarm card.
	 *
	 * @param  card  The alarm card.
	 * @param  position  The position of the alarm card.
	 */
	@Override
	public void onBindViewHolder(final NacCardHolder holder, int position)
	{
		NacAlarm alarm = this.getAlarmAt(position);

		holder.init(alarm);

		if (this.wasAddedWithFloatingActionButton())
		{
			holder.interact();
		}

		this.setWasAddedWithFloatingActionButton(false);
	}

	///**
	// * Called when the alarm card is collapsed.
	// */
	//@Override
	//public void onCardCollapsed(NacCardHolder holder, NacAlarm alarm)
	//{
	//	if (alarm.wasChanged())
	//	{
	//		this.showAlarmChange(alarm);
	//		this.sortHighlight(alarm);
	//	}
	//}

	///**
	// * Called when the alarm card is expanded.
	// */
	//@Override
	//public void onCardExpanded(NacCardHolder holder, NacAlarm alarm)
	//{
	//}

	///**
	// * Capture the click event on the delete button, and delete the card it
	// * belongs to.
	// *
	// * @param  view  The view that was clicked.
	// */
	//@Override
	//public void onClick(View view)
	//{
	//	Undo undo = this.getUndo();
	//	NacAlarm alarm = undo.getAlarm();
	//	int position = undo.getPosition();
	//	Undo.Type type = undo.getType();

	//	undo.reset();

	//	if (type == Undo.Type.COPY)
	//	{
	//		this.deleteAlarm(position);
	//	}
	//	else if (type == Undo.Type.DELETE)
	//	{
	//		this.restore(alarm, position);
	//	}
	//	else if (type == Undo.Type.RESTORE)
	//	{
	//		this.deleteAlarm(position);
	//	}
	//}

	///**
	// * Create the context menu.
	// */
	//@Override
	//public void onCreateContextMenu(ContextMenu menu, View view,
	//	ContextMenuInfo menuInfo)
	//{
	//	if (menu.size() > 0)
	//	{
	//		return;
	//	}

	//	Context context = this.getContext();
	//	AppCompatActivity activity = (AppCompatActivity) context;
	//	this.mLastCardClicked = view;

	//	activity.getMenuInflater().inflate(R.menu.menu_card, menu);

	//	for (int i=0; i < menu.size(); i++)
	//	{
	//		MenuItem item = menu.getItem(i);

	//		item.setOnMenuItemClickListener(this);
	//	}
	//}

	/**
	 * Create the view holder.
	 *
	 * @param  parent  The parent view.
	 * @param  viewType  The type of view.
	 */
	@NonNull
    @Override
	public NacCardHolder onCreateViewHolder(ViewGroup parent, int viewType)
	{
		Context context = parent.getContext();
		LayoutInflater inflater = LayoutInflater.from(context);
		View root = inflater.inflate(R.layout.card_frame, parent, false);
		NacCardHolder holder = new NacCardHolder(root);

		this.callOnViewHolderCreatedListener(holder);

		return holder;
	}

	///**
	// * Restore a previously deleted alarm.
	// * 
	// * @param  alarm  The alarm to restore.
	// * @param  position  The position to insert the alarm.
	// */
	//public void restore(NacAlarm alarm, int position)
	//{
	//	if (this.atMaxAlarmCapacity())
	//	{
	//		this.toastMaxAlarmsError();
	//		return;
	//	}

	//	NacSharedConstants cons = this.getSharedConstants();
	//	Locale locale = Locale.getDefault();
	//	String message = String.format(locale, "%1$s.",
	//		cons.getMessageAlarmRestore());
	//	String action = cons.getActionUndo();

	//	this.addAlarm(alarm, position);
	//	this.undo(alarm, position, Undo.Type.RESTORE);
	//	this.showSnackbar(message, action, this);
	//}

	/**
	 * Set the listener for when an alarm card is created.
	 */
	public void setOnViewHolderCreatedListener(OnViewHolderCreatedListener listener)
	{
		this.mOnViewHolderCreatedListener = listener;
	}

	/**
	 * Set whether an alarm was added with the floating button.
	 */
	public void setWasAddedWithFloatingActionButton(boolean added)
	{
		this.mWasAddedWithFloatingActionButton = added;
	}

	/**
	 * @return True if the alarm should be sorted, and False otherwise.
	 */
	private boolean shouldSortAlarm(NacAlarm alarm)
	{
		return false;
		//NacCardHolder holder = this.getCardHolder(alarm);
		//return (holder != null) && holder.isCollapsed() && alarm.wasChanged();
	}

	///**
	// * Show the most recently edited alarm.
	// */
	//public void showAlarm(NacAlarm alarm)
	//{
	//	if (alarm == null)
	//	{
	//		return;
	//	}

	//	NacSharedConstants cons = this.getSharedConstants();
	//	NacSharedPreferences shared = this.getSharedPreferences();
	//	String message = NacCalendar.getMessageWillRun(shared, alarm);
	//	String action = cons.getActionDismiss();

	//	this.showSnackbar(message, action);
	//}

	///**
	// * Show a message either for the recently changed alarm, or the next alarm.
	// */
	//private void showAlarmChange(NacAlarm alarm)
	//{
	//	if (!alarm.wasChanged())
	//	{
	//		return;
	//	}

	//	if (alarm.isEnabled())
	//	{
	//		this.showAlarm(alarm);
	//	}
	//	else
	//	{
	//		this.showNextAlarm();
	//	}
	//}

	///**
	// * Show the next alarm.
	// */
	//public void showNextAlarm()
	//{
	//	NacSharedConstants cons = this.getSharedConstants();
	//	NacSharedPreferences shared = this.getSharedPreferences();
	//	NacAlarm alarm = this.getNextAlarm();
	//	String message = NacCalendar.getMessageNextAlarm(shared, alarm);
	//	String action = cons.getActionDismiss();

	//	this.showSnackbar(message, action);
	//}

	///**
	// * Show the saved NFC tag ID of the given alarm.
	// */
	//public void showNfcTagId(NacAlarm alarm)
	//{
	//	if (alarm == null)
	//	{
	//		return;
	//	}

	//	Context context = this.getContext();
	//	NacSharedConstants cons = this.getSharedConstants();
	//	Locale locale = Locale.getDefault();
	//	String id = alarm.getNfcTagId();
	//	String message;
	//	
	//	if (!id.isEmpty())
	//	{
	//		message = String.format(locale, "%1$s %2$s",
	//			cons.getMessageShowNfcTagId(), id);
	//	}
	//	else
	//	{
	//		message = String.format(locale, "%1$s", cons.getMessageAnyNfcTagId());
	//	}

	//	NacUtility.quickToast(context, message);
	//}

	/**
	 * Sort the enabled alarms from soonest to latest.
	 */
	public void sort()
	{
		//this.mAlarmList = this.getSortedAlarms();
		//notifyDataSetChanged();
	}

	/**
	 * Sort an alarm into the alarm list.
	 */
	public int sortAlarm(NacAlarm alarm)
	{
		//if (!this.shouldSortAlarm(alarm))
		//{
		//	return -1;
		//}

		//int findIndex = this.findAlarm(alarm);
		//int insertIndex = this.getSortInsertIndex(alarm, findIndex);

		//if (this.isValidSortIndex(findIndex, insertIndex))
		//{
		//	this.notifyDeleteAlarm(findIndex);
		//	this.notifyInsertAlarm(alarm, insertIndex);
		//}

		//return insertIndex;
		return -1;
	}

	/**
	 * Sort and highlight the alarm, if the alarm card is collapsed.
	 */
	public void sortHighlight(NacAlarm alarm)
	{
		//int sortIndex = this.sortAlarm(alarm);

		////if (alarm.isChangeTrackerLatched() && (findIndex == sortIndex))
		////{
		////}
		////else if (sortIndex >= 0)
		//if (sortIndex >= 0)
		//{
		//	this.getRecyclerView().scrollToPosition(sortIndex);
		//	this.highlight(alarm);
		//}
	}

	/**
	 * Toast for when the maximum number of alarms has been created.
	 */
	public void toastMaxAlarmsError()
	{
		Context context = this.getContext();
		NacSharedConstants cons = this.getSharedConstants();

		NacUtility.quickToast(context, cons.getErrorMessageMaxAlarms());
	}

	/**
	 * @return True if the alarm was added, and False otherwise.
	 */
	public boolean wasAddedWithFloatingActionButton()
	{
		return this.mWasAddedWithFloatingActionButton;
	}

	/**
	 * @return Where to insert the alarm in the list (ignoring it's current
	 *         position if it already exists in the list.
	 */
	private int whereToInsertAlarm(NacAlarm alarm)
	{
		return getItemCount();
		//List<NacAlarm> alarmList = this.getAlarms();
		//Calendar next = NacCalendar.getNextAlarmDay(alarm);
		//int id = alarm.getId();
		//int size = this.size();

		//for (int index=0; index < size; index++)
		//{
		//	NacAlarm a = alarmList.get(index);

		//	if (a.getId() == id)
		//	{
		//		continue;
		//	}

		//	if (this.canInsertAlarm(alarm, a, next))
		//	{
		//		return index;
		//	}
		//}

		//return size;
	}

}
