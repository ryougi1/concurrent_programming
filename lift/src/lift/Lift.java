package lift;

public class Lift extends Thread {
	private Monitor monitor;

	
	public Lift(Monitor m, LiftView lv) {
		monitor = m;
	}
	
	public void run() {
		// moveLift should not block other threads. 
		while (true) {
			monitor.moveLift();
		}
	}
	
}
