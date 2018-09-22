package todo;

import done.ClockInput;
import done.ClockOutput;
import se.lth.cs.realtime.semaphore.Semaphore;

public class SetTimeAlarmThread extends Thread {
	private ClockThread clock;
	private static ClockInput input;
	private static Semaphore sem;

	public SetTimeAlarmThread(ClockThread c, ClockInput i) {
		clock = c;
		input = i;
		sem = input.getSemaphoreInstance();
	}

	public void run() {
		int lastMode = -1;
		while (true) {
			sem.take();

			if (clock.isAlarmRinging()) {
				/*
				 * If the alarm is ringing, then any user counts as turning the alarm off. Let
				 * ClockThread know.
				 */
				clock.endAlarm();
			} else {
				/*
				 * Keep track of which mode we're in and which mode we were in previously.
				 * Necessary for correct returns from input.getValue(). Call appropriate methods
				 * in ClockThread.
				 */
				int mode = input.getChoice();

				if (mode == ClockInput.SET_TIME) {
					lastMode = ClockInput.SET_TIME;
				}

				if (mode == ClockInput.SET_ALARM) {
					lastMode = ClockInput.SET_ALARM;
				}

				if ((lastMode == ClockInput.SET_TIME) && (mode == ClockInput.SHOW_TIME)) {
					lastMode = mode;
					clock.setTime(input.getValue());
				}

				if ((lastMode == ClockInput.SET_ALARM) && (mode == ClockInput.SHOW_TIME)) {
					lastMode = mode;
					clock.setAlarm(input.getValue());
				}
			}
		}
	}

}
