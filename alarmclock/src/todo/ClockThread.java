package todo;

import done.ClockOutput;
import se.lth.cs.realtime.semaphore.MutexSem;

public class ClockThread extends Thread{
	private static ClockOutput	output;
	private long startTime, currentTime;
	private int hh, mm, ss;
	MutexSem mutex;

	public ClockThread(ClockOutput o) {
		mutex = new MutexSem();
		output = o;
		hh = 0;
		mm = 0;
		ss = 55;
	}

	public void run() {
		startTime = System.currentTimeMillis();
	
		while (true) {
			currentTime = System.currentTimeMillis();
			long difference = currentTime - startTime;
			difference = difference / 1000;
			
			if (difference >= 1) {
				startTime += difference * 1000;

				mutex.take();
				output.showTime(getIncrementedTime());
				mutex.give();
			} else {
				try {
					sleep(300);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	public void setTime(int hhmmss) {		
		mutex.take();
		hh = hhmmss / 10000;
		mm = (hhmmss % 10000) / 100;
		ss = (hhmmss % 100);
		mutex.give();
	}
	
	private int getIncrementedTime() {
		if(++ss == 60) {
			ss = 0;
			if(++mm == 60) {
				mm = 0;
				if(++hh == 24) {
					hh = 0;
				}
			}
		}
		return ss + mm * 100 + hh * 10000;
	}
}
