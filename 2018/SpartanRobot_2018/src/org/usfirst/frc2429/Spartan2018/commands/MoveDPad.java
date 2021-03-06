// RobotBuilder Version: 2.0
//
// This file was generated by RobotBuilder. It contains sections of
// code that are automatically generated and assigned by robotbuilder.
// These sections will be updated in the future when you export to
// Java from RobotBuilder. Do not put any code or make any change in
// the blocks indicating autogenerated code or it will be lost on an
// update. Deleting the comments indicating the section will prevent
// it from being updated in the future.


package org.usfirst.frc2429.Spartan2018.commands;
import edu.wpi.first.wpilibj.RobotDrive;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.command.Command;
import org.usfirst.frc2429.Spartan2018.Robot;

/**
 *
 */
public class MoveDPad extends Command {

	double distance;
	double heading;
	
    public MoveDPad() {
        requires(Robot.drivetrain);
    }
    
    public MoveDPad(double distance, double heading) {
    	this.distance = distance;
    	this.heading = heading;
        requires(Robot.drivetrain);
    }

    // Called just before this Command runs the first time
    @Override
    protected void initialize() {
		System.out.println("MoveForwardDPad(" + String.format("%.2f",distance)+ "," + String.format("%.2f",heading)+
				") called at: " + String.format("%.2f", Timer.getFPGATimestamp()) + "s");
		
		//Set heading with gyro
    }

    // Called repeatedly when this Command is scheduled to run
    @Override
    protected void execute() {
    	Robot.drivetrain.drive(distance, heading);
    }

    // Make this return true when this Command no longer needs to run execute()
    @Override
    protected boolean isFinished() {
        return false;
    }

    // Called once after isFinished returns true
    @Override
    protected void end() {
    	System.out.println("MoveDpad ended at: " + String.format("%.2f",Timer.getFPGATimestamp())+"s");
		Robot.drivetrain.drive(0, 0);
    }

    // Called when another command which requires one or more of the same
    // subsystems is scheduled to run
    @Override
    protected void interrupted() {
    	System.out.println("MoveDpad interrupted at: " + String.format("%.2f",Timer.getFPGATimestamp())+"s");
		Robot.drivetrain.drive(0, 0);
    }
}

