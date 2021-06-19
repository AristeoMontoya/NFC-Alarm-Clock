package com.nfcalarmclock.statistics;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;
import java.util.Date;

/**
 * Statistics for when an alarm is created.
 */
@Entity(tableName="alarm_created_statistic",
	ignoredColumns={"hour", "minute", "name"})
public class NacAlarmCreatedStatistic
	extends NacAlarmStatistic
{

}
