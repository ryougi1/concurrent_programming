package todo;

import done.*;

/**
 * 
 * In main of Wash, WashingMachineSimulation is created. Simulates a physical
 * washing machine. The simulator gets passed a WashingController and then
 * started.
 *
 */
public class WashingController implements ButtonListener {
	// TODO: add suitable attributes
	AbstractWashingMachine machine;
	double speed;
	TemperatureController tempController;
	WaterController waterController;
	SpinController spinController;
	WashingProgram currentProgram;

	public WashingController(AbstractWashingMachine theMachine, double theSpeed) {
		// TODO: implement this constructor
		machine = theMachine;
		speed = theSpeed;
		currentProgram = null;
		/**
		 * Init and start controllers
		 */
		tempController = new TemperatureController(machine, speed);
		waterController = new WaterController(machine, speed);
		spinController = new SpinController(machine, speed);
		tempController.start();
		waterController.start();
		spinController.start();
	}

	public void processButton(int theButton) {
		// TODO: implement this method
		switch (theButton) {
		case 0:
			/**
			 * All motors, as well as any filling/draining of water, should be turned off
			 * immediately.
			 */
			if (currentProgram != null) {
				currentProgram.interrupt(); // Interrupt already does a "program 0". 
//				currentProgram = new WashingProgram0(machine, speed, tempController, waterController, spinController);
//				currentProgram.start();
				currentProgram = null;
			} else {
				System.out.println("NO ONGOING PROGRAM FOOL");
			}
			break;
		case 1:
			/**
			 * Lock the hatch, let water into the machine, heat to 60C, keep the temperature
			 * for 30min, rinse 5 times 2 minutes in cold water, centrifuge for 5 minutes
			 * and unlock the hatch.
			 */
			if (currentProgram == null) {
				currentProgram = new WashingProgram1(machine, speed, tempController, waterController, spinController);
				currentProgram.start();
			} else {
				System.out.println("Program " + currentProgram + " is already ongoing. Cannot start program " + theButton + ".");
			}
			break;
		case 2:
			/**
			 * Like program 1, but with a 15 minute pre-wash in 40C. The main wash is
			 * performed in 90C.
			 */
			if (currentProgram == null) {
				currentProgram = new WashingProgram2(machine, speed, tempController, waterController, spinController);
				currentProgram.start();
			} else {
				System.out.println("Program " + currentProgram + " is already ongoing. Cannot start program " + theButton + ".");
			}
			break;
		case 3:
			/**
			 * Turn of heating and rotation. After pumping out the water (if any), unlock
			 * the hatch. Note: the user should select this program as soon as possible
			 * after 0 (stop).
			 */
			if (currentProgram == null) {
				currentProgram = new WashingProgram3(machine, speed, tempController, waterController, spinController);
				currentProgram.start();
			} else {
				System.out.println("Program " + currentProgram + " is already ongoing. Cannot start program " + theButton + ".");
			}
			break;
		default:
			System.out.println("Invalid button pressed, cannot execute program " + theButton);
			break;
		}
	}
}
