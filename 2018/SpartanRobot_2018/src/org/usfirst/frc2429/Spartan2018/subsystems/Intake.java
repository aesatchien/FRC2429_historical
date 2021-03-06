// RobotBuilder Version: 2.0
//
// This file was generated by RobotBuilder. It contains sections of
// code that are automatically generated and assigned by robotbuilder.
// These sections will be updated in the future when you export to
// Java from RobotBuilder. Do not put any code or make any change in
// the blocks indicating autogenerated code or it will be lost on an
// update. Deleting the comments indicating the section will prevent
// it from being updated in the future.


package org.usfirst.frc2429.Spartan2018.subsystems;

import org.usfirst.frc2429.Spartan2018.Robot;
import org.usfirst.frc2429.Spartan2018.commands.SmoothTankDrive;
import org.usfirst.frc2429.Spartan2018.subIntake.IntakeReceiveOnTrigger;

import edu.wpi.first.wpilibj.command.Subsystem;
import edu.wpi.first.wpilibj.hal.HAL;
import edu.wpi.first.wpilibj.hal.FRCNetComm.tInstances;
import edu.wpi.first.wpilibj.hal.FRCNetComm.tResourceType;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
// BEGIN AUTOGENERATED CODE, SOURCE=ROBOTBUILDER ID=IMPORTS
import edu.wpi.first.wpilibj.AnalogInput;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.SpeedController;
import edu.wpi.first.wpilibj.Talon;
import edu.wpi.first.wpilibj.Victor;

/**
 * Intake subsystem
 */
public class Intake extends Subsystem {

	//------------------------------------------------------------------------------------

	//If we want this to be a drivetrain we have to make sure it is fine doing nothing... i think a drivetrain is unnecessary
    //public static DifferentialDrive intakeDrive;
    public static AnalogInput intakecubeSensor;
    private final SpeedController intakeMotorLeft = new Talon(6);
    private final SpeedController intakeMotorRight = new Talon(7);
    //private final DifferentialDrive intakeDifferentialDrive = new DifferentialDrive(intakeintakeMotorLeft, intakeintakeMotorRight);
    private final AnalogInput cubeSensor = new AnalogInput(1);
	private boolean intakeOn = false;
	int updateCounter = 0;
	//------------------------------------------------------------------------------------

	
    //Constructor added 1/27/2018 CJH modeled after the GearsBot template - getting rid of RobotMap redundancy      
    public Intake() {
		super();
	    // left false, right true for practice bot
		// left true, right false for comp bot
		intakeMotorLeft.setInverted(true);
	    intakeMotorRight.setInverted(false);
	    SmartDashboard.putBoolean("Intake Motors", intakeOn);

		// Let's name the sensors on the LiveWindow - do this here instead of RobotMap
	    if (Robot.bDebugging) {
			//addChild("intakeMotorLeft", intakeMotorLeft);
			//addActuator("intakeMotorRight",intakeMotorRight);
			addChild("cubeSensor", cubeSensor);
			
	    }
	}
  //------------------------------------------------------------------------------------

    /**
     * Adapting standard arcade drive to work with our intake motors.
     *
     * @param xSpeed        The robot's speed along the X axis [-1.0..1.0]. Forward is positive.
     * @param zRotation     The robot's rotation rate around the Z axis [-1.0..1.0]. Clockwise is
     *                      positive.
     */

    public void intakeDrive(double xSpeed, double zRotation) {

      double leftMotorOutput;
      double rightMotorOutput;

      double maxInput = Math.copySign(Math.max(Math.abs(xSpeed), Math.abs(zRotation)), xSpeed);

      if (xSpeed >= 0.0) {
        // First quadrant, else second quadrant
        if (zRotation >= 0.0) {
          leftMotorOutput = maxInput;
          rightMotorOutput = xSpeed - zRotation;
        } else {
          leftMotorOutput = xSpeed + zRotation;
          rightMotorOutput = maxInput;
        }
      } else {
        // Third quadrant, else fourth quadrant
        if (zRotation >= 0.0) {
          leftMotorOutput = xSpeed + zRotation;
          rightMotorOutput = maxInput;
        } else {
          leftMotorOutput = maxInput;
          rightMotorOutput = xSpeed - zRotation;
        }
      }

      intakeOn = true;
      intakeMotorLeft.set(leftMotorOutput);
      intakeMotorRight.set(rightMotorOutput);

    }
    
    public void intakeDrive(Joystick joy) {
    	intakeDrive(joy.getRawAxis(Robot.oi.INTAKE_YAXIS),joy.getRawAxis(Robot.oi.INTAKE_XAXIS));
    }

    public void intakeToggle() {
    	//Used in command - IntakeToggle
    	if (!intakeOn){
    		intakeStart();
        	intakeOn = true;
        }
    	else {
    		intakeOn = false;
    		intakeStop();
    	}
    	SmartDashboard.putBoolean("Intake Motors", intakeOn);
    }
    
    public void intakeTurn(double dir) {
    	intakeMotorLeft.set(0.5*dir);
		intakeMotorRight.set(-0.5*dir);
		intakeOn = true;
    }
    
    public void intakeTrigger() {
    	//Used in command - IntakeStart
    	intakeMotorLeft.set(Robot.oi.driveStick.getRawAxis(2));
    	intakeMotorRight.set(Robot.oi.driveStick.getRawAxis(2));
    	intakeOn = true;
    }
    
    public void outputTrigger() {
    	//Used in command - IntakeEject
    	intakeMotorLeft.set(-Robot.oi.driveStick.getRawAxis(3));
    	intakeMotorRight.set(-Robot.oi.driveStick.getRawAxis(3));
    	intakeOn = true;
    }
    
    public void intakeStart() {
    	intakeMotorLeft.set(1);
    	intakeMotorRight.set(1);
    	intakeOn = true;
    	SmartDashboard.putBoolean("Intake Motors", intakeOn);
    }
    
    public void intakeReverse() {
    	intakeMotorLeft.set(-1);
    	intakeMotorRight.set(-1);
    	intakeOn = true;
    	SmartDashboard.putBoolean("Intake Motors", intakeOn);
    }
    
    public void intakeHold() {
    	intakeMotorLeft.set(-0.1);
    	intakeMotorRight.set(-0.1);
    	intakeOn = true;
    	SmartDashboard.putBoolean("Intake Motors", intakeOn);
    }
    
    public void intakeStop() {
    	intakeMotorLeft.set(0);
    	intakeMotorRight.set(0);
    	intakeOn = false;
    	SmartDashboard.putBoolean("Intake Motors", intakeOn);
    }
   
    public void setIntakeOn(boolean intake) {
    	intakeOn = intake;
    	SmartDashboard.putBoolean("Intake Motors", intakeOn);
    }
    
    @Override
    public void initDefaultCommand() {
    }

    @Override
    public void periodic() {
        // Put code here to be run every loop
    	
    }

    // Put methods for controlling this subsystem
    // here. Call these from Commands.
	public void log() {
		updateCounter++;
		if (updateCounter%10 == 0) {
			SmartDashboard.putBoolean("Intake Motors", intakeOn);
			//SmartDashboard.putNumber("CubeSensor", cubeSensor.getVoltage());
		}
	}
}

