package todo;

import done.AbstractWashingMachine;

public class WashingProgram2 extends WashingProgram {

	// ------------------------------------------------------------- CONSTRUCTOR

	/**
	 * @param mach
	 *            The washing machine to control
	 * @param speed
	 *            Simulation speed
	 * @param tempController
	 *            The TemperatureController to use
	 * @param waterController
	 *            The WaterController to use
	 * @param spinController
	 *            The SpinController to use
	 */
	public WashingProgram2(AbstractWashingMachine mach, double speed, TemperatureController tempController,
			WaterController waterController, SpinController spinController) {
		super(mach, speed, tempController, waterController, spinController);
	}

	/**
	 * Like program 1, but with a 15 minute pre-wash in 40C. The main wash is
	 * performed in 90C.
	 * 
	 * The drain pump must not be running while the input valve is open. The machine
	 * must not be heated while it is free of water. There must not be any water in
	 * the machine when the hatch is unlocked. Centrifuging must not be performed
	 * when there is any measurable amounts of water in the machine.
	 */
	@Override
	protected void wash() throws InterruptedException {
		/**
		 * Pre-wash
		 */
		myMachine.setLock(true);
		myWaterController.putEvent(new WaterEvent(this, WaterEvent.WATER_FILL, 0.5));
		mailbox.doFetch();
		myWaterController.putEvent(new WaterEvent(this, WaterEvent.WATER_IDLE, 0.5));
		mySpinController.putEvent(new SpinEvent(this, SpinEvent.SPIN_SLOW));
		myTempController.putEvent(new TemperatureEvent(this, TemperatureEvent.TEMP_SET, 40.0));
		mailbox.doFetch();
		sleep((long) (60 * 15 * 1000 / mySpeed));
		
		/**
		 * Washing
		 */
		myTempController.putEvent(new TemperatureEvent(this, TemperatureEvent.TEMP_SET, 90.0));
		mailbox.doFetch();

		sleep((long) (60 * 30 * 1000 / mySpeed));

		/**
		 * Rinsing
		 */
		myTempController.putEvent(new TemperatureEvent(this, TemperatureEvent.TEMP_IDLE, 0.0));
		for (int i = 0; i < 5; ++i) {
			myWaterController.putEvent(new WaterEvent(this, WaterEvent.WATER_DRAIN, 0.0));
			mailbox.doFetch();
			myWaterController.putEvent(new WaterEvent(this, WaterEvent.WATER_FILL, 0.5));
			mailbox.doFetch();
			sleep((long) (60 * 2 * 1000 / mySpeed));
		}
		/**
		 * Centrifuging
		 */
		myWaterController.putEvent(new WaterEvent(this, WaterEvent.WATER_DRAIN, 0.0));
		mailbox.doFetch();
		myWaterController.putEvent(new WaterEvent(this, WaterEvent.WATER_IDLE, 0.0));
		mySpinController.putEvent(new SpinEvent(this, SpinEvent.SPIN_FAST));
		sleep((long) (60 * 5 * 1000 / mySpeed));

		/**
		 * Done
		 */
		mySpinController.putEvent(new SpinEvent(this, SpinEvent.SPIN_OFF));
		myMachine.setLock(false);
	}

}