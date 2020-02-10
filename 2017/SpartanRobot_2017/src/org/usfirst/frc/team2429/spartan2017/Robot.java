// RobotBuilder Version: 2.0
//
// This file was generated by RobotBuilder. It contains sections of
// code that are automatically generated and assigned by robotbuilder.
// These sections will be updated in the future when you export to
// Java from RobotBuilder. Do not put any code or make any change in
// the blocks indicating autogenerated code or it will be lost on an
// update. Deleting the comments indicating the section will prevent
// it from being updated in the future.

package org.usfirst.frc.team2429.spartan2017;

import org.usfirst.frc.team2429.spartan2017.commands.*;
import org.usfirst.frc.team2429.spartan2017.deprecated.*;
import org.usfirst.frc.team2429.spartan2017.movement.*;
import org.usfirst.frc.team2429.spartan2017.subsystems.*;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.IterativeRobot;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.DriverStation.Alliance;
import edu.wpi.first.wpilibj.command.Command;
import edu.wpi.first.wpilibj.command.Scheduler;
import edu.wpi.first.wpilibj.livewindow.LiveWindow;
import edu.wpi.first.wpilibj.networktables.NetworkTable;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;


/**
 * The VM is configured to automatically run this class, and to call the
 * functions corresponding to each mode, as described in the IterativeRobot
 * documentation. If you change the name of this class or the package after
 * creating this project, you must also update the manifest file in the resource
 * directory.
 */
public class Robot extends IterativeRobot {

	Command autonomousCommand;
	SendableChooser autoChooser;
	SendableChooser autoShootingChooser;
	Command autonomousPreparation;
	Command teleopCommand;
	Command calibrateJoystickCommand;
	public int autonomousPosition;
	
	double testDistance;
	

	public static OI oi;
	// BEGIN AUTOGENERATED CODE, SOURCE=ROBOTBUILDER ID=DECLARATIONS
	public static Drivetrain drivetrain;
	public static ImageProcessor imageProcessor;    
	public static BallAgitator ballAgitator;
    public static BallGate ballGate;
    public static BallCollector ballCollector;
    public static BallShooter ballShooter;
    public static RopeClimber ropeClimber;
	private NetworkTable gearTable;
	private NetworkTable shooterTable;
    
	
	// END AUTOGENERATED CODE, SOURCE=ROBOTBUILDER ID=DECLARATIONS

	/**
	 * This function is run when the robot is first started up and should be
	 * used for any initialization code.
	 */
	public void robotInit() {
		RobotMap.init();
		
		//Initialize all subsystems

		drivetrain = new Drivetrain();
		imageProcessor = new ImageProcessor();
		ballAgitator = new BallAgitator();
		ballGate = new BallGate();
		ballCollector = new BallCollector();
		ballShooter = new BallShooter();
		ropeClimber = new RopeClimber();
		
		// OI must be constructed after subsystems. If the OI creates Commands
		// (which it very likely will), subsystems are not guaranteed to be
		// constructed yet. Thus, their requires() statements may grab null
		// pointers. Bad news. Don't move it.
		oi = new OI();
		
		gearTable = NetworkTable.getTable("GearCam");
		shooterTable = NetworkTable.getTable("ShooterCam");
		resetDashBoard();
		//Set the default autonomous position - 1, 2 or 3
		SmartDashboard.putNumber("Autonomous Position", 3);
		

	}

	/**
	 * This function is called when the disabled button is hit. You can use it
	 * to reset subsystems before shutting down.
	 */
	public void disabledInit() {
		System.out.println("\nSwitching to disabled at: " + String.format("%.2f",Timer.getFPGATimestamp())+"s");
		teleopCommand = new SensorUpdate();	
		teleopCommand.start();
		//Reset all subsystems and sensors
		resetSubsystems();
		//resetDashBoard();
		shooterTable.putBoolean("connected",false);
		gearTable.putBoolean("connected",false);
		}

	public void disabledPeriodic() {
		Scheduler.getInstance().run();
		Robot.imageProcessor.isGearcamConnected = gearTable.getBoolean("connected",false);
		Robot.imageProcessor.isShootercamConnected = shooterTable.getBoolean("connected",false);
		SmartDashboard.putBoolean("ShooterCam", Robot.imageProcessor.isShootercamConnected);
		SmartDashboard.putBoolean("GearCam", Robot.imageProcessor.isGearcamConnected);
		SmartDashboard.putNumber("Distance", (double) ((int) (RobotMap.wheelEncoder.getDistance()* 10)) / 10.0);
		
	}

	public void autonomousInit() {
			
		//Reset all subsystems and sensors
		//Have to reset the drive train BEFORE you start the drivetrain command
		resetSubsystems();
		resetDashBoard();
		
		System.out.println("\nAutonomous init starting at: " + String.format("%.2f",Timer.getFPGATimestamp())+"s");
		// schedule the autonomous command (example)
		//if (autonomousCommand != null){
		//autonomousCommand.start();
		//}
		
			//Define all of our starting moves based on our position - can't use the DS in competition because we never line up where we're assigned
			// autonomousPosition = DriverStation.getInstance().getLocation();
			autonomousPosition = (int) (0.2 + SmartDashboard.getNumber("Autonomous Position", 3) );  // Just in case the double rounds to 2.999 etc
			System.out.println("\nAutonomous position is: " + String.format("%.2f",(double) autonomousPosition)+"\n");
			// autonomousPosition = 3;
			Robot.oi.setShootingAllowed(false);
			
			//Ready to implement this if statement 
			if (autonomousPosition == 3) {
				//Dist = 90 in. 
				//Angle = 30 deg
				
				Robot.oi.setAutonomousLocation("RIGHT");
				Robot.oi.setAutonomousStartingDistance(85.0);
				Robot.oi.setAutonomousGearRotationAngle(30.0);	
				//autonomousCommand = new AutonomousCommandGroupRight();
				testDistance = -10;
				if (DriverStation.getInstance().getAlliance() == Alliance.Red){
					//RED: The Hopper is on your RIGHT
					Robot.oi.setAutonomousShootingPosition(true);
					Robot.oi.setAutonomousInitialShooterRotationAngle(-70.0);
					Robot.oi.setAutonomousSecondShooterRotationAngle(0.0);
					Robot.oi.setAutonomousShooterTravelDistance(-50);
				}
				else if(DriverStation.getInstance().getAlliance() == Alliance.Blue){
					//BLUE: The Hopper is on your LEFT
					//Do nothing; we can't get there.  Just try to get the gear in twice
					Robot.oi.setAutonomousShootingPosition(false);
				}
			
			}
			else if (autonomousPosition == 1) {
				//Dist = 90 in. 
				//Angle = 150 deg  
				Robot.oi.setAutonomousLocation("LEFT");
				//Robot.oi.setAutonomousStartingDistance(85.0);
				//Robot.oi.setAutonomousGearRotationAngle(150.0);
				//Changed to line up backwards on position 1 for faster alignment
				Robot.oi.setAutonomousStartingDistance(-85.0);
				Robot.oi.setAutonomousGearRotationAngle(-30.0);

				if (DriverStation.getInstance().getAlliance() == Alliance.Red){
					//RED: The Hopper is on your RIGHT
					//Do nothing; we can't get there.  Just try to get the gear in twice
					Robot.oi.setAutonomousShootingPosition(false);
				}
				else if(DriverStation.getInstance().getAlliance() == Alliance.Blue){
					//BLUE: The Hopper is on your LEFT
					Robot.oi.setAutonomousShootingPosition(true);
					//Going to spin around and back up
					Robot.oi.setAutonomousInitialShooterRotationAngle(-125.0);
					Robot.oi.setAutonomousShooterTravelDistance(-50);
					Robot.oi.setAutonomousSecondShooterRotationAngle(20.0);
				}
			}
			else if (autonomousPosition == 2) {
				//Dist = 25 in. 
				//Angle = 90 deg
				Robot.oi.setAutonomousLocation("CENTER");
				Robot.oi.setAutonomousStartingDistance(25.0);
				Robot.oi.setAutonomousGearRotationAngle(83.0);
				//autonomousCommand = new AutonomousCommandGroupCenter();
				testDistance = -5;
				if (DriverStation.getInstance().getAlliance() == Alliance.Red){
					//RED: The Hopper is on your RIGHT
					Robot.oi.setAutonomousShootingPosition(true);
					Robot.oi.setAutonomousInitialShooterRotationAngle(0.0);
					Robot.oi.setAutonomousShooterTravelDistance(80);
					Robot.oi.setAutonomousSecondShooterRotationAngle(-135.0);
				}
				else if(DriverStation.getInstance().getAlliance() == Alliance.Blue){
					//BLUE: The Hopper is on your LEFT
					Robot.oi.setAutonomousShootingPosition(true);
					Robot.oi.setAutonomousInitialShooterRotationAngle(0.0);
					Robot.oi.setAutonomousShooterTravelDistance(-80);
					Robot.oi.setAutonomousSecondShooterRotationAngle(-45);
				}
			}
			else  {
				autonomousCommand = null;
				System.out.println("Cannot find driver station alliance info " + String.format("%.2f",Timer.getFPGATimestamp())+"s");
				System.out.println("Probably need to restart roboRIO - don't reboot");
			}
/*		
			//time to get rid of this switch, just prove that it is failing first - 3/21/2017
			switch(autonomousPosition) {
			case 1: 
				//Dist = 90 in. 
				//Angle = 150 deg
				
				Robot.oi.setAutonomousLocation("LEFT");
				Robot.oi.setAutonomousStartingDistance(85.0);
				Robot.oi.setAutonomousGearRotationAngle(150.0);
				//autonomousCommand = new AutonomousCommandGroupLeft();
				testDistance = 50;
				if (DriverStation.getInstance().getAlliance() == Alliance.Red){
					//RED: The Hopper is on your RIGHT
					//Do nothing; we can't get there.  Just try to get the gear in twice
					Robot.oi.setAutonomousShooting(false);
				}
				else if(DriverStation.getInstance().getAlliance() == Alliance.Blue){
					//BLUE: The Hopper is on your LEFT
					Robot.oi.setAutonomousShooting(true);
					//Going to spin around and back up
					Robot.oi.setAutonomousInitialShooterRotationAngle(-125.0);
					Robot.oi.setAutonomousShooterTravelDistance(-50);
					Robot.oi.setAutonomousSecondShooterRotationAngle(20.0);

				}
					break;
					
			case 2: 
				//Dist = 25 in. 
				//Angle = 90 deg
				Robot.oi.setAutonomousLocation("CENTER");
				Robot.oi.setAutonomousStartingDistance(25.0);
				Robot.oi.setAutonomousGearRotationAngle(83.0);
				//autonomousCommand = new AutonomousCommandGroupCenter();
				testDistance = -5;
				if (DriverStation.getInstance().getAlliance() == Alliance.Red){
					//RED: The Hopper is on your RIGHT
					Robot.oi.setAutonomousShooting(true);
					Robot.oi.setAutonomousInitialShooterRotationAngle(0.0);
					Robot.oi.setAutonomousShooterTravelDistance(80);
					Robot.oi.setAutonomousSecondShooterRotationAngle(-135.0);
					
				}
				else if(DriverStation.getInstance().getAlliance() == Alliance.Blue){
					//BLUE: The Hopper is on your LEFT
					Robot.oi.setAutonomousShooting(true);
					Robot.oi.setAutonomousInitialShooterRotationAngle(0.0);
					Robot.oi.setAutonomousShooterTravelDistance(-80);
					Robot.oi.setAutonomousSecondShooterRotationAngle(-45);
				}
					break;
					
			case 3:     	
				//Dist = 90 in. 
				//Angle = 30 deg
				Robot.oi.setAutonomousLocation("RIGHT");
				Robot.oi.setAutonomousStartingDistance(85.0);
				Robot.oi.setAutonomousGearRotationAngle(30.0);	
				//autonomousCommand = new AutonomousCommandGroupRight();
				testDistance = -10;
				if (DriverStation.getInstance().getAlliance() == Alliance.Red){
					//RED: The Hopper is on your RIGHT
					Robot.oi.setAutonomousShooting(true);
					Robot.oi.setAutonomousInitialShooterRotationAngle(-70.0);
					Robot.oi.setAutonomousSecondShooterRotationAngle(0.0);
					Robot.oi.setAutonomousShooterTravelDistance(-50);
				}
				else if(DriverStation.getInstance().getAlliance() == Alliance.Blue){
					//BLUE: The Hopper is on your LEFT
					//Do nothing; we can't get there.  Just try to get the gear in twice
					Robot.oi.setAutonomousShooting(false);
				}
					break;
					
			default:
					autonomousCommand = null;
					System.out.println("Cannot find driver station alliance info " + String.format("%.2f",Timer.getFPGATimestamp())+"s");
					System.out.println("Probably need to restart roboRIO - don't reboot");
					break;
			}
*/
			System.out.println("\nAutonomous position is set to: " + Robot.oi.getAutonomousLocation()+ 
					"\nwith starting distance " + Robot.oi.getAutonomousStartingDistance() +
					"\nand starting angle " + Robot.oi.getAutonomousGearRotationAngle());
						
			//Run a lot of debugging commands here here
			//autoChooser = new SendableChooser();
			//autoChooser.addDefault("Forward 3", new RobotMoveStraightPID(3));
			//autoChooser.addObject("Forward 10", new RobotMoveStraightPID(10));
			//autoChooser.addObject("Forward 50", new RobotMoveStraightPID(50));
			//autoChooser.addObject("Rotate 10", new RobotRotateCustomPID(10,false));
			//autoChooser.addObject("Rotate -10", new RobotRotateCustomPID(-10,false));
			//autoChooser.addObject("Center Using Pi Live Value", new RobotCenterOnGearPID(true));
			//autoChooser.addObject("SetStrafeCorr", new AutonomousSetImagingVariables());
			//autoChooser.addObject("Rotate Using Pi Live Value", new RobotRotateCustomPID(0, true));
			//autoChooser.addObject("Center Using Correction", new RobotCenterOnGearPID(false));
			//autoChooser.addObject("Just GearDelivery", new RobotDriveToGearTarget());
			//autoChooser.addObject("Center and GearDelivery", new AutonomousGearDelivery());
			//autoChooser.addObject("Entire Routine", new AutonomousFullRoutine());
			//SmartDashboard.putData("Autonomous Mode Chooser", autoChooser);
			
			
			//NOTE: this is what killed the autonomous not being able to choose anything but #3
			//autoShootingChooser = new SendableChooser();
			//autoShootingChooser.addDefault("No Shooting", new AutonomousProhibitShooting() );
			//autoShootingChooser.addObject("Shooting", new AutonomousAllowShooting());
			//SmartDashboard.putData("AutoShootMode", autoShootingChooser);

			//autoChooser = new SendableChooser();
			//autoChooser.addDefault("Default", new AutonomousFullRoutine());
			//autoChooser.addObject("Default No Shoot", new AutonomousFullRoutineNoShooting());
			//autoChooser.addObject("Position One", new AutonomousFullPos1());
			//autoChooser.addObject("Position Two", new AutonomousFullPos2());
			//autoChooser.addObject("Position Three", new AutonomousFullPos3());
			//autoChooser.addObject("No Autonomous", new SitStill(1.0));
			//SmartDashboard.putData("Autonomous Choices", autoChooser);

			
			if (teleopCommand != null)
				teleopCommand.cancel();
			teleopCommand = new SensorUpdate();
			teleopCommand.start();
			
			//Multiple ways to calibrate the joystick - doesn't really need to use a Command
			oi.calibrateJoystick();
			
			//Start the autonomous command - AutonomousFullRoutine is the ultimate one
			autonomousCommand = new AutonomousFullRoutine();
			//autonomousCommand = (Command) autoChooser.getSelected();
			//autonomousCommand = new AutonomousTimed();
			autonomousCommand.start();

	}

	/**
	 * This function is called periodically during autonomous
	 */
	public void autonomousPeriodic() {
		
		Scheduler.getInstance().run();
	}

	public void teleopInit() { 
		// This makes sure that the autonomous stops running when
		// teleop starts running. If you want the autonomous to
		// continue until interrupted by another command, remove
		// this line or comment it out.

		System.out.println("Stopping Autonomous Command");
		if (autonomousCommand != null)
			autonomousCommand.cancel();
		//Reset all subsystems and sensors
		Scheduler.getInstance().removeAll();
		System.out.println("Stopped Autonomous Command");
		resetSubsystems();
		resetDashBoard();
		
		//Multiple ways to calibrate the joystick - doesn't really need to use a Command
		oi.calibrateJoystick();
		
		//Start updating the sensors.  Everything else is handled by default commands and the joystick
		teleopCommand = new SensorUpdate();	
		teleopCommand.start();
	}

	/**
	 * This function is called periodically during operator control
	 */
	public void teleopPeriodic() {
		Scheduler.getInstance().run();
	}

	/**
	 * This function is called periodically during test mode
	 */
	public void testPeriodic() {
		LiveWindow.run();
	}
	private void resetDashBoard() {
		SmartDashboard.putString("Current Command", "None");
		SmartDashboard.putNumber("Parameter", 0);
		SmartDashboard.putNumber("Iterations", 0);
		SmartDashboard.putNumber("Error", 0);
		
	}
	
	private void resetSubsystems() {
		//Reset the gyro, the encoder, the shooter, collector and climber subsystems
		drivetrain.reset();
		ballShooter.stop();
		ballAgitator.stop();
		ballCollector.stop();
	   	ballGate.stop();
	   	ropeClimber.stop();
	}
}