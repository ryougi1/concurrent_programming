package todo;

import done.ClockInput;
import done.ClockOutput;
import se.lth.cs.realtime.semaphore.MutexSem;

public class ClockThread extends Thread {
	private static ClockOutput output;
	private static ClockInput input;
	private int hh, mm, ss; // Internal time representation
	private int alarmTime, alarmSecs;
	MutexSem mutex;

	public ClockThread(ClockOutput o, ClockInput i) {
		mutex = new MutexSem();
		output = o;
		input = i;
		hh = 0;
		mm = 0;
		ss = 55;
		alarmTime = 0;
		alarmSecs = 0;
	}

	public void run() {
		long startTime, currentTime, difference;
		startTime = System.currentTimeMillis();

		while (true) {
			/*
			 * Calculate if a second has passed. If so, update internal time represantation
			 * and update GUI. If not, sleep a bit so as to not hog the CPU.
			 */
			currentTime = System.currentTimeMillis();
			difference = (currentTime - startTime);
			if (difference >= 1000) {
				startTime += difference;

				/*
				 * Changing internal time representation requires mutual exclusion to avoid
				 * real-time problems.
				 */
				int time = getIncrementedTime();
				output.showTime(time);

				/*
				 * Check if it's time to ring the alarm. If it is, start ringing and ring for 20
				 * secs, unless turned off by user.
				 */
				checkForAlarm();
				if (alarmSecs-- > 0) {
					output.doAlarm();
				}
			} else {
				try {
					sleep(300);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
	}

	/*
	 * Gets called from SetTimeAlarmThread when user sets a new time. Changing
	 * internal time representation requires mutual exclusion to avoid real-time
	 * problems.
	 */
	public void setTime(int hhmmss) {
		mutex.take();
		hh = hhmmss / 10000;
		mm = (hhmmss % 10000) / 100;
		ss = hhmmss % 100;
		mutex.give();
	}

	/*
	 * Increment internal time representation by one second and return it in proper
	 * format. Again, needs mutual exclusion to avoid real-time problems.
	 */
	private int getIncrementedTime() {
		mutex.take();
		if (++ss == 60) {
			ss = 0;
			if (++mm == 60) {
				mm = 0;
				if (++hh == 24) {
					hh = 0;
				}
			}
		}
		int incrementedTime = ss + mm * 100 + hh * 10000;
		mutex.give();
		return incrementedTime;
	}

	/**
	 * Gets called from SetTimeAlarmThread when user sets a new alarm.
	 */
	public void setAlarm(int hhmmss) {
		alarmTime = hhmmss;
	}

	/**
	 * Gets called from SetTimeAlarmThread when user turns the alarm off.
	 */
	public void endAlarm() {
		alarmSecs = 0;
	}
	
	public boolean isAlarmRinging() {
		return alarmSecs > 0;
	}

	private void checkForAlarm() {
		int hha = alarmTime / 10000;
		int mma = (alarmTime % 10000) / 100;
		int ssa = alarmTime % 100;
		if ((hha == hh) && (mma == mm) && (ssa == ss) && input.getAlarmFlag()) {
			alarmSecs = 20;
		}
	}

}
