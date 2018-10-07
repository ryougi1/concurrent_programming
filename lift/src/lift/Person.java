package lift;

public class Person extends Thread {
	private int entryFloor, exitFloor;
	private Monitor monitor;

	public Person(Monitor m) {
		monitor = m;
	}
	
	public void run() {
		while (true) {		
			initializePerson();
			monitor.travel(entryFloor, exitFloor);
		}
	}
	
	private void initializePerson() {
		int delay = 1000 * ((int)(Math.random() * 46.0));
//		int delay = 1000 * ((int)(Math.random() * 6.0));
		try {
			sleep(delay);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		entryFloor = ((int)(Math.random() * 7.0));
		exitFloor = ((int)(Math.random() * 7.0));
		while(exitFloor == entryFloor) {
			exitFloor = ((int)(Math.random() * 7.0));
		}
	}
}
