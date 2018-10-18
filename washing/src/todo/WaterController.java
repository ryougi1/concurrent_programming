package todo;


import se.lth.cs.realtime.*;
import done.AbstractWashingMachine;


public class WaterController extends PeriodicThread {
	// TODO: add suitable attributes
	AbstractWashingMachine machine;
	double targetWaterLevel;
	boolean sendAck;
	WashingProgram source;
	static final double MARGIN = 0.01;

	public WaterController(AbstractWashingMachine mach, double speed) {
		super((long) (1000/speed)); // TODO: replace with suitable period
		machine = mach;
	}

	public void perform() {
		// TODO: implement this method
		WaterEvent event = (WaterEvent) mailbox.tryFetch();
		
		if (event != null) {
			source = (WashingProgram) event.getSource();
			switch (event.getMode()) {
			case WaterEvent.WATER_IDLE:
				machine.setFill(false);
				machine.setDrain(false);
				break; 
			case WaterEvent.WATER_FILL:
			case WaterEvent.WATER_DRAIN:
				targetWaterLevel = event.getLevel();
				sendAck = true;
				break;
			default:
				break;
			}
		}
		
		if (machine.getWaterLevel() < targetWaterLevel - MARGIN) { 
			machine.setFill(true);
			machine.setDrain(false);
		} else if (machine.getWaterLevel() > targetWaterLevel + MARGIN) {
			machine.setFill(false);
			machine.setDrain(true);
		} else {
			if (sendAck) {
				source.putEvent(new AckEvent(this));
				sendAck = false;
			}
			machine.setFill(false);
			machine.setDrain(false);
		}
	}
}
