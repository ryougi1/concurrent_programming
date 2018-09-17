/*
 * Real-time and concurrent programming course, washing machine lab.
 * Department of Computer Science, Lund Institute of Technology
 *
 * PP 980923 Created
 * PP 990924 Revised
 */

package done;

import todo.WashingController;

/**
 * Main program for the washing machine.
 *
 * @see todo.WashingController
 */

public class Wash {

    // Change these if you wish
    private static double SIMULATION_SPEED = 50.0;
    private static boolean SHOW_HACKER_PANEL = true;
    
	// ------------------------------------------------------------- MAIN METHOD

	/** Parses command line arguments and creates a WashingController. */
	public static void main(String[] args) throws InterruptedException {

		AbstractWashingMachine theMachine = new WashingMachineSimulation(SIMULATION_SPEED);
		
		if (SHOW_HACKER_PANEL) {
	        new HackerPanel(theMachine);
		}

		theMachine.setButtonListener(new WashingController(theMachine, SIMULATION_SPEED));
		theMachine.start();

		// Block indefinitely
		Thread.currentThread().join();
	}
};
