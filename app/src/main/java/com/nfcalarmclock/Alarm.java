package com.nfcalarmclock;

import android.app.AlarmManager;
import java.util.Calendar;

/**
 * @brief Alarm.
 */
public class Alarm
{

    /**
     * @brief A list of possible days the alarm can run on.
     */
    public class Days
    {
        public final static byte NONE = 0;
        public final static byte SUNDAY = 1;
        public final static byte MONDAY = 2;
        public final static byte TUESDAY = 4;
        public final static byte WEDNESDAY = 8;
        public final static byte THURSDAY = 16;
        public final static byte FRIDAY = 32;
        public final static byte SATURDAY = 64;
    }

	/**
	 * @brief The database in which to save the alarm.
	 */
	private NacDatabase mDatabase = null;

	/**
	 * @brief The database ID number.
	 */
	private long mId = -1;

	/**
	 * @brief Indicates whether the alarm is enabled or not.
	 */
    private boolean mEnabled = true;

	/**
	 * @brief The hour at which to run the alarm.
	 */
    private int mHour = 0;

	/**
	 * @brief The minute at which to run the alarm.
	 */
    private int mMinute = 0;

	/**
	 * @brief The days on which to run the alarm.
	 */
    private int mDays = Days.MONDAY|Days.TUESDAY|Days.WEDNESDAY|Days.THURSDAY|Days.FRIDAY;

	/**
	 * @brief Indicates whether the alarm should be repeated or not.
	 */
    private boolean mRepeat = true;

	/**
	 * @brief Indicates whether the phone should vibrate when the alarm is run.
	 */
    private boolean mVibrate = false;

	/**
	 * @brief The sound to play when the alarm is run.
	 */
    private String mSound = "";

	/**
	 * @brief The name of the alarm.
	 */
    private String mName = "";

	/**
	 * @brief The interval of the alarm.
	 */
    private long mInterval = -1;

	/**
	 * @brief The alarm type.
	 */
    private int mType = AlarmManager.RTC_WAKEUP;

    /**
     * @brief Use the default set values for an alarm.
     */
    public Alarm()
    {
        Calendar cal = Calendar.getInstance();
        this.setHour(cal.get(Calendar.HOUR_OF_DAY));
        this.setMinute(cal.get(Calendar.MINUTE));
    }

    /**
     * @brief Set the hour and minute to run the alarm at.
     */
    public Alarm(int hour, int minute)
    {
        this.setHour(hour);
        this.setMinute(minute);
    }

    /**
     * @brief Set the time and date to run the alarm at.
     */
    public Alarm(int hour, int minute, int days)
    {
        this(hour, minute);
        this.setDays(days);
    }

    /**
     * @brief Set the name and the time to run the alarm at.
     */
    public Alarm(String name, int hour, int minute)
    {
        this(hour, minute);
        this.setName(name);
    }

	/**
	 * @brief Update the database.
	 */
	public void update()
	{
		if (this.mDatabase == null)
		{
			return;
		}

		this.mDatabase.update(this);
	}

    /**
     * @brief Print all values in the alarm object.
     */
    public void print()
    {
        NacUtility.printf("Enabled : %b", this.mEnabled);
        NacUtility.printf("Hour    : %d", this.mHour);
        NacUtility.printf("Minute  : %d", this.mMinute);
        NacUtility.printf("Days    : %s", this.getDaysString());
        NacUtility.printf("Repeat  : %b", this.mRepeat);
        NacUtility.printf("Vibrate : %b", this.mVibrate);
        NacUtility.printf("Sound   : %s", this.mSound);
        NacUtility.printf("Name    : %s", this.mName);
        NacUtility.printf("\n\n");
    }

    /**
     * @brief Convert hour to the appropriate form (either 12 or 24 hour
     *        format).
     * 
     * @note See if you can change this to a static method.
     */
    public int toFormat(int hour, boolean is24hourformat)
    {
        int converted = hour;

        if (!is24hourformat)
        {
            if (hour >= 12)
            {
                converted = hour % 12;
            }

            if (converted == 0)
            {
                converted = 12;
            }
        }
        return converted;
    }

    /**
     * @brief Convert a day index to the byte representation of a day.
     */
    public byte indexToDay(int index)
    {
        switch (index)
        {
        case 0:
            return Days.SUNDAY;
        case 1:
            return Days.MONDAY;
        case 2:
            return Days.TUESDAY;
        case 3:
            return Days.WEDNESDAY;
        case 4:
            return Days.THURSDAY;
        case 5:
            return Days.FRIDAY;
        case 6:
            return Days.SATURDAY;
        default:
            return Days.NONE;
        }
    }

	/**
	 * @brief Set the database.
	 */
	public void setDatabase(NacDatabase db)
	{
		this.mDatabase = db;
	}

	/**
	 * @brief Set the database row ID number.
	 */
	public void setId(long id)
	{
		this.mId = id;
	}

    /**
     * @brief Set the enabled/disabled status of the alarm.
     */
    public void setEnabled(boolean state)
    {
        this.mEnabled = state;
		this.update();
    }

    /**
     * @brief Set the hour at which to run the alarm.
     */
    public void setHour(int hour)
    {
        this.mHour = hour;
		this.update();
    }

    /**
     * @brief Set the minute at which to run the alarm.
     */
    public void setMinute(int minute)
    {
        this.mMinute = minute;
		this.update();
    }

    /**
     * @brief Set the days on which the alarm will be run.
     */
    public void setDays(int days)
    {
        this.mDays = days;
		this.setRepeat(true);
		this.update();
    }

    /**
     * @brief Toggle a day.
     */
    public void toggleDay(int day)
    {
		this.setDays(mDays^day);
    }

    /**
     * @brief Set whether the alarm should be repeated or not.
     */
    public void setRepeat(boolean state)
    {
        this.mRepeat = state;
		this.update();
    }

    /**
     * @brief Set whether or not the phone should vibrate when the alarm is
     *        activated.
     */
    public void setVibrate(boolean state)
    {
        this.mVibrate = state;
		this.update();
    }

    /**
     * @brief Set the sound that will be played when the alarm is activated.
     */
    public void setSound(String sound)
    {
        this.mSound = sound;
		this.update();
    }

    /**
     * @brief Set the name of the alarm.
     */
    public void setName(String name)
    {
        this.mName = name;
		this.update();
    }

    /**
     * @brief Set the interval at which to run the alarm.
     */
    public void setInterval(int interval)
    {
        this.mInterval = interval;
    }

    /**
     * @brief Set the type of alarm to run.
     */
    public void setType(int type)
    {
        this.mType = type;
    }

	/**
	 * @brief Return the database.
	 */
	public NacDatabase getDatabase()
	{
		return this.mDatabase;
	}

	/**
	 * @brief Return the database row ID number.
	 */
	public long getId()
	{
		return this.mId;
	}

    /**
     * @brief Return the enabled flag for the alarm.
     */
    public boolean getEnabled()
    {
        return this.mEnabled;
    }

    /**
     * @brief Return the hour at which to run the alarm.
     */
    public int getHour()
    {
        return this.mHour;
    }

    /**
     * @brief Return the minutes at which to run the alarm.
     */
    public int getMinute()
    {
        return this.mMinute;
    }

    /**
     * @brief Return the days on which to run the alarm.
     */
    public int getDays()
    {
        return this.mDays;
    }

    /**
     * @brief Convert days to comma separated string.
     * 
     * @return Comma separated string of days to repeat alarm on.
     */
    public String getDaysString()
    {
        String conv = "";
        String[] names = {"Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat"};
        byte[] values = {Days.SUNDAY, Days.MONDAY, Days.TUESDAY, Days.WEDNESDAY,
                         Days.THURSDAY, Days.FRIDAY, Days.SATURDAY};

        for (int i=0; i < values.length; i++)
        {
            if ((this.mDays & values[i]) != 0)
            {
                if (!conv.isEmpty())
                {
                    conv += ",";
                }
                conv += names[i];
            }
        }

        return conv;
    }

    /**
     * @brief Return whether the alarm should be repeated or not.
     */
    public boolean getRepeat()
    {
        return this.mRepeat;
    }

    /**
     * @brief Return whether or not the phone should vibrate when the alarm is
     *        activated.
     */
    public boolean getVibrate()
    {
        return this.mVibrate;
    }

    /**
     * @brief Return the sound that will be played when the alarm is activated.
     */
    public String getSound()
    {
        return this.mSound;
    }

    /**
     * @brief Return the name of the alarm.
     */
    public String getName()
    {
        return this.mName;
    }

    /**
     * @brief Return the interval at which to run the alarm.
     */
    public long getInterval()
    {
        return this.mInterval;
    }

    /**
     * @brief Return the type of the alarm to run.
     */
    public int getType()
    {
        return this.mType;
    }

    /**
     * @brief Return the meridian (AM or PM).
     */
    public String getMeridian(int hour, boolean is24hourformat)
    {
        if (is24hourformat)
        {
            return "";
        }

        if (hour < 12)
        {
            return "AM";
        }
        else
        {
            return "PM";
        }
    }

}
