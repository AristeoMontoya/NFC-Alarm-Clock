package com.nfcalarmclock.statistics.db

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Query

/**
 * Data access object for storing when alarms were missed.
 */
@Dao
interface NacAlarmMissedStatisticDao : NacAlarmStatisticDao<NacAlarmMissedStatistic>
{

	/**
	 * Delete all rows from the table.
	 */
	@Query("DELETE FROM alarm_missed_statistic")
	fun deleteAll(): Int
	//TODO suspend fun deleteAll(): Int

	/**
	 * Get all instances when alarms were missed.
	 *
	 * @return All instances when alarms were missed.
	 */
	@get:Query("SELECT * FROM alarm_missed_statistic")
	val all: LiveData<List<NacAlarmMissedStatistic>>

	/**
	 * Count the number of missed alarm statistics.
	 *
	 * @return The number of missed alarm statistics.
	 */
	@get:Query("SELECT COUNT(id) FROM alarm_missed_statistic")
	val count: Long

}