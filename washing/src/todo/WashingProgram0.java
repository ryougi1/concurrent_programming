package todo;

import done.AbstractWashingMachine;

public class WashingProgram0 extends WashingProgram {

	// ------------------------------------------------------------- CONSTRUCTOR

		/**
		 * @param   mach             The washing machine to control
		 * @param   speed            Simulation speed
		 * @param   tempController   The TemperatureController to use
		 * @param   waterController  The WaterController to use
		 * @param   spinController   The SpinController to use
		 */
		public WashingProgram0(AbstractWashingMachine mach,
				double speed,
				TemperatureController tempController,
				WaterController waterController,
				SpinController spinController) {
			super(mach, speed, tempController, waterController, spinController);
		}
		
		/**
		 * All motors, as well as any filling/draining of water, should be turned off
		 * immediately.
		 */
		protected void wash() throws InterruptedException {
			// Switch off spin
			mySpinController.putEvent(new SpinEvent(this, SpinEvent.SPIN_OFF));
			
			// Set water regulation to idle => drain pump stops
			myWaterController.putEvent(new WaterEvent(this,
					WaterEvent.WATER_IDLE,
					0.0));
			
			// Switch of temp regulation
			myTempController.putEvent(new TemperatureEvent(this,
					TemperatureEvent.TEMP_IDLE,
					0.0));

			mailbox.doFetch(); // Wait for Ack
		}
}
