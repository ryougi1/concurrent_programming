package todo;


import se.lth.cs.realtime.*;
import done.AbstractWashingMachine;


public class TemperatureController extends PeriodicThread {
	// TODO: add suitable attributes
	AbstractWashingMachine machine;
	int mode;
	double targetTemp;
	boolean sendAck;
	WashingProgram source;
	final static double MARGIN = 0.01;

	public TemperatureController(AbstractWashingMachine mach, double speed) {
		super((long) (1000/speed)); // TODO: replace with suitable period
		machine = mach;
	}

	public void perform() {
		// TODO: implement this method
		TemperatureEvent event = (TemperatureEvent) mailbox.tryFetch();
		
		if(event != null) {
			source = (WashingProgram) event.getSource();
			mode = event.getMode();
			if (event.getMode() == TemperatureEvent.TEMP_IDLE) {
				machine.setHeating(false);
			} else if (event.getMode() == TemperatureEvent.TEMP_SET) {
				targetTemp = event.getTemperature();
				sendAck = true;
			}
		}
		
		if (mode == TemperatureEvent.TEMP_SET) {
			if (machine.getTemperature() < targetTemp - 2 + MARGIN) {
				machine.setHeating(true);
			} else if (machine.getTemperature() >= targetTemp - MARGIN) {
				if (sendAck) {
					source.putEvent(new AckEvent(this));
					sendAck = false;
				}
				machine.setHeating(false);
			} 
		}
	}
}
