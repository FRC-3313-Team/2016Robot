package org.usfirst.frc.team3313.robot;

import edu.wpi.first.wpilibj.IterativeRobot;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.Talon;
import edu.wpi.first.wpilibj.buttons.Button;
import edu.wpi.first.wpilibj.buttons.JoystickButton;
import edu.wpi.first.wpilibj.shuffleboard.Shuffleboard;
import edu.wpi.first.wpilibj.shuffleboard.ShuffleboardTab;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.cameraserver.CameraServer;
import edu.wpi.first.networktables.NetworkTableEntry;

//Import properly this time 
/**
 * @author logan The VM is configured to automatically run this class, and to
 *         call the functions corresponding to each mode, as described in the
 *         IterativeRobot documentation. If you change the name of this class or
 *         the package after creating this project, you must also update the
 *         manifest file in the resource directory.
 */
public class Robot extends IterativeRobot {
	// DO NOT TOUCH (unless you know what you're doing that is)
	Joystick main, func;
	String autoSelected;
	SendableChooser<Double> chooser = new SendableChooser<>();
	Talon Shooter = new Talon(3);
	Talon Feeder = new Talon(4);
	Joystick joy1 = new Joystick(1);
	Button Button1 = new JoystickButton(joy1, 2);
	TankDrive drive = new TankDrive(new Talon(1), new Talon(0));

	// Experimental SB code
	ShuffleboardTab tab = Shuffleboard.getTab("Drive");
	NetworkTableEntry SBmaxSpeed = tab.add("Max Speed", 1).getEntry();

	/**
	 * it is 2:04 A.M. my sanity is fading help
	 */

	// Accelerated Movement
	double incrementSpeed = 0; // DO NOT TOUCH
	int currentSpeed = 0; // DO NOT TOUCH
	int noMovement = 0; // DO NOT TOUCH
	int ticksToWaitAfterNoMovement = 40;
	int ticksTillFullSpeed = 7; // 20 ~= 1 sec
	double maxSpeed = 100; // value where 100 is 100% of motor speed
	boolean respectMax = true; // Whether or not to respect full movement of joystick or not, meaning
	double DEFAULT_MOVEMENT_SPEED = 1;
	double DEFAULT_TURN_SPEED = 1;
	// End //max movement on joystick is the same as the maximum speed versus
	// deadzone.

	/**
	 * This function is run when the robot is first started up and should be used
	 * for any initialization code.
	 */

	@Override
	public void robotInit() {
		CameraServer.getInstance().startAutomaticCapture();
		CameraServer.getInstance().startAutomaticCapture();

		// Accelerated Movement DEFAULTS DO NOT ERASE/UNCOMMENT
		// double incrementSpeed = 0; //DO NOT TOUCH
		// int currentSpeed = 0; //DO NOT TOUCH
		// int noMovement = 0; //DO NOT TOUCH
		// int ticksToWaitAfterNoMovement = 40;
		// int ticksTillFullSpeed = 7; //20 ~= 1 sec
		// double maxSpeed = 85; //value where 100 is 100% of motor speed
		// boolean respectMax = true; //Whether or not to respect full movement of
		// joystick or not, meaning
		// End //max movement on joystick is the same as the maximum speed versus
		// deadzone.

	}

	/**
	 * This autonomous (along with the chooser code above) shows how to select
	 * between different autonomous modes using the dashboard. The sendable chooser
	 * code works with the Java SmartDashboard. If you prefer the LabVIEW Dashboard,
	 * remove all of the chooser code and uncomment the getString line to get the
	 * auto name from the text box below the Gyro
	 *
	 * You can add additional auto modes by adding additional comparisons to the
	 * switch structure below with additional strings. If using the SendableChooser
	 * make sure to add them to the chooser code above as well.
	 */
	@Override
	public void autonomousInit() {
		// Do we even want an auton for the demo robot?? If so what should it do?
		// autoSelected = SmartDashboard.getString("Auto Selector",
		// defaultAuto);
	}

	/**
	 * This function is called periodically during autonomous
	 */
	@Override
	public void autonomousPeriodic() {
	}

	/**
	 * This function is called periodically during operator control
	 */
	@Override
	public void teleopPeriodic() {
		// Drive

		advancedDrive(-joy1.getX(), joy1.getRawAxis(5));
		//drive.tankDrive(-joy1.getRawAxis(1), joy1.getRawAxis(5));
		// Dont worry about this error. It should work for now until this method is
		// changed permanently in the future.

		// Shooter
		// Button/Power directory: Right Bumper=100% Power (add as needed for minimal
		// confusion)

		// Shooter
		if (joy1.getRawButton(6)) {
			Shooter.set(1);
		} else {
			Shooter.set(0);
		}
		// Feed Double
		if (joy1.getRawButton(5)) {
			Feeder.set(-1);
		} else {
			Feeder.set(0);
		}
	}

	/**
	 * This function is called periodically during test mode
	 */
	@Override
	public void testPeriodic() {
	}

	// FIX DEADZONES
	private void advancedDrive(double rightStick, double leftStick) {
		double movementSpeedMultiplier = SBmaxSpeed.getDouble(DEFAULT_MOVEMENT_SPEED);

		// rightStick uses Y axis, leftStick uses rawAxis(5)
		if (rightStick == 0 && leftStick == 0) {
			if (noMovement == ticksToWaitAfterNoMovement) {
				currentSpeed = 0; // Reset the speed when no movement
				noMovement = 0;
			} else {
				noMovement++;
			}
			return;
		}

		rightStick = -rightStick * DEFAULT_TURN_SPEED; // Invert
		if (respectMax) {
			double respectedValue = ((rightStick / 100) * maxSpeed); // New Respected speed
			// if (controller.getRawButton(5)) { // Ignore the advanced drive
			// drive.tankDrive(-(controller.getY() / 1.25) + (-controller.getRawAxis(5) /
			// 2),
			// (-controller.getY() / 1.25) + -(-controller.getRawAxis(5) / 2));
			// }
			if (currentSpeed != ticksTillFullSpeed) {
				currentSpeed++; // Calculate the next tick speed based off maxSpeed / ticksTillFullSpeed
				if (respectedValue <= (incrementSpeed * currentSpeed)) {
					drive.tankDrive(respectedValue + (-leftStick * movementSpeedMultiplier), respectedValue + (leftStick * movementSpeedMultiplier));
				} else {
					drive.tankDrive((incrementSpeed * currentSpeed) + (-leftStick * movementSpeedMultiplier),
							(incrementSpeed * currentSpeed) + (leftStick * movementSpeedMultiplier));
				}
			} else {
				drive.tankDrive(respectedValue + (-leftStick * movementSpeedMultiplier), respectedValue + (leftStick * movementSpeedMultiplier));
			}
			// double acclerationValue = (respectedValue / ticksTillFullSpeed) *
			// currentSpeed;
		}
	}
}
