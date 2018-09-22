package todo;

import done.*;
import se.lth.cs.realtime.semaphore.Semaphore;

public class AlarmClock extends Thread {

	private static ClockInput input;
	private static ClockOutput output;

	public AlarmClock(ClockInput i, ClockOutput o) {
		input = i;
		output = o;
	}

	/**
	 * The AlarmClock thread is started by the simulator. No need to start it by
	 * yourself, if you do you will get an IllegalThreadStateException.
	 */

	public void run() {
		/**
		 * Start a ClockThread thread which keeps track of time, and has methods for
		 * setting time and alarms. Start a SetTimeAlarmThread thread which takes care
		 * of user input such as setting the time and setting alarms and calls the
		 * appropriate methods in ClockThead.
		 */
		ClockThread clock = new ClockThread(output, input);
		clock.start();
		SetTimeAlarmThread setTimeAlarmThread = new SetTimeAlarmThread(clock, input);
		setTimeAlarmThread.start();
		while (true) {
			// Would like for AlarmClock to not be a thread but not sure if I'm allowed to change that.
		}
	}
}
