package todo;

import se.lth.cs.realtime.*;
import done.AbstractWashingMachine;

public class SpinController extends PeriodicThread {
	// TODO: add suitable attributes
	AbstractWashingMachine machine;
	int mode, spinDirection;
	double time;

	public SpinController(AbstractWashingMachine mach, double speed) {
		super((long) (1000 / speed)); // TODO: replace with suitable period
		machine = mach;
		spinDirection = AbstractWashingMachine.SPIN_LEFT;
	}

	public void perform() {
		// TODO: implement this method
		SpinEvent event = (SpinEvent) mailbox.tryFetch();
		if (event != null)  {
			mode = event.getMode();
			switch (mode) {
			case SpinEvent.SPIN_OFF:
				machine.setSpin(AbstractWashingMachine.SPIN_OFF);
				break;
			case SpinEvent.SPIN_SLOW:
				machine.setSpin(spinDirection);
				// time = event.getMillis(); // Not sure if returns System.currentTimeMillis()
				time = System.currentTimeMillis();
				break;
			case SpinEvent.SPIN_FAST:
				machine.setSpin(AbstractWashingMachine.SPIN_FAST);
				break;
			default:
				break;
			}
		}
		
		/**
		 * While washing, the barrel should alternate between left and right rotation.
		 * The direction should be changed once every minute.
		 */
		if ((System.currentTimeMillis() - time) >= 60 * 1000 && mode == SpinEvent.SPIN_SLOW) {
			if (spinDirection == AbstractWashingMachine.SPIN_LEFT) {
				spinDirection = AbstractWashingMachine.SPIN_RIGHT;
			} else {
				spinDirection = AbstractWashingMachine.SPIN_LEFT;
			}
			machine.setSpin(spinDirection);
		}
	}
}
