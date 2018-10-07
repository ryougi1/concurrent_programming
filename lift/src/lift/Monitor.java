package lift;

import java.util.ArrayList;

public class Monitor {
	/**
	 * If here != next, here (floor number) tells from which floor the lift is
	 * moving and next to which floor it is moving. If here == next, the lift is
	 * standing still on the floor given by here.
	 */
	private int here, next;
	/**
	 * The number of persons waiting to enter the lift at the various floors.
	 */
	private int[] waitEntry;
	/**
	 * The number of persons (inside the lift) waiting to leave the lift at the
	 * various floors.
	 */
	private int[] waitExit;
	/**
	 * The number of people currently occupying the lift.
	 */
	private int load;
	private LiftView liftView;
	private int liftDirection;
	public static final int MAX_CAPACITY = 4;

	public Monitor(LiftView lv) {
		here = 0;
		next = 0;
		waitEntry = new int[] { 0, 0, 0, 0, 0, 0, 0 };
		waitExit = new int[] { 0, 0, 0, 0, 0, 0, 0 };
		load = 0;
		liftView = lv;
		liftDirection = 1;
	}

	public void moveLift() {
		checkForPeople();
		next = getNextFloor();
		liftView.moveLift(here, next);
		arrive();
	}

	private synchronized void checkForPeople() {
		boolean noPeople = true;
		for (int i = 0; i < waitEntry.length; i++) {
			if (waitEntry[i] != 0 || waitExit[i] != 0) {
				noPeople = false;
			}
		}

		if (noPeople) {
			System.out.println("No people using/waiting for elevator. Halting lift.");
			try {
				wait();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			System.out.println("Resuming lift operations.");
		}
	}

	private int getNextFloor() {
		if (liftDirection > 0) {
			// Going up
			if (here == 6) {
				liftDirection = -1;
				return here - 1;
			}
			return here + 1;
		} else {
			// Going down
			if (here == 0) {
				liftDirection = 1;
				return here + 1;
			}
			return here - 1;
		}
	}

	private synchronized void arrive() {
		try {
			here = next;
			notifyAll(); // Tell persons that elevator has stopped at a new floor.
			/*
			 * It stops at each floor regardless of whether there are persons waiting for
			 * entering or exiting the floor or not.
			 */
			wait(300); // Needs timeout on wait since if nobody gets on or off then we're stuck here.
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	public synchronized void travel(int entryFloor, int exitFloor) {
		try {
			/**
			 * Person arrives at elevator on floor entryFloor. Three conditions must be met
			 * for person to enter elevator: 1. Elevator is at their floor. 2. Elevator is
			 * not moving. 3. elevator is not full. Person will enter while loop every time
			 * notifyAll is called from arrive() to see if they can enter lift.
			 */
			notifyAll(); // This notify is to wake the elevator in case it was halted because no persons
							// were waiting for the lift or located within the lift itself
			liftView.drawLevel(entryFloor, ++waitEntry[entryFloor]);
			while (isFull() || here != entryFloor || here != next) {
				wait();
			}

			/**
			 * Conditions are met, person enters lift.
			 */
			++waitExit[exitFloor];
			liftView.drawLevel(entryFloor, --waitEntry[entryFloor]);
			liftView.drawLift(entryFloor, ++load);
			notifyAll();

			/**
			 * Person waits until lift arrives at exitFloor.
			 */
			while (here != exitFloor || here != next) {
				wait();
			}

			/**
			 * Person exits.
			 */
			--waitExit[exitFloor];
			liftView.drawLift(exitFloor, --load);
			notifyAll();

		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	private boolean isFull() {
		return load == MAX_CAPACITY;
	}

	public static void main(String[] args) {

		LiftView lv = new LiftView();

		Monitor m = new Monitor(lv);

		for (int i = 0; i <= 20; i++) {
			new Person(m).start();
		}

		new Lift(m, lv).start();
	}
}
