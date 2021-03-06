// RobotBuilder Version: 2.0
//
// This file was generated by RobotBuilder. It contains sections of
// code that are automatically generated and assigned by robotbuilder.
// These sections will be updated in the future when you export to
// Java from RobotBuilder. Do not put any code or make any change in
// the blocks indicating autogenerated code or it will be lost on an
// update. Deleting the comments indicating the section will prevent
// it from being updated in the future.


package org.usfirst.frc2429.Spartan2018.subLifter;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.command.Command;
import org.usfirst.frc2429.Spartan2018.Robot;

/**
 *  Call the function that sends the lifter right to the top
 */
public class LifterTop extends Command {
    public LifterTop() {
        requires(Robot.liftMechanism);
    }

    // Called just before this Command runs the first time
    @Override
    protected void initialize() {
    	System.out.println("LifterTop called at: "  + String.format("%.2f", Timer.getFPGATimestamp()- Robot.enabledTime)  + "s");
    	Robot.liftMechanism.setArmMoving(true);
    	//Robot.liftMechanism.lifterMoveToTop();
    	
    	setTimeout(3);
    }

    // Called repeatedly when this Command is scheduled to run
    @Override
    protected void execute() {
    	Robot.liftMechanism.moveUp();
    	//Timer.delay(0.015);
    }

    // Make this return true when this Command no longer needs to run execute()
    @Override
    protected boolean isFinished() {
        return (Robot.liftMechanism.isArmAtTop() || Robot.liftMechanism.getArmSetpoint() > (Robot.liftMechanism.LIFTER_SETPOINT_MAX -100));
    	//return !Robot.liftMechanism.isArmMoving();
    	//return false;
    }

    // Called once after isFinished returns true
    @Override
    protected void end() {
    	System.out.println("LifterTop ended at: "  + String.format("%.2f", Timer.getFPGATimestamp()- Robot.enabledTime)  + "s");
    	Robot.liftMechanism.setArmMoving(false);
    	//Robot.liftMechanism.setkF(0);
    }

    // Called when another command which requires one or more of the same
    // subsystems is scheduled to run
    @Override
    protected void interrupted() {
    	System.out.println("LifterTop interrupted at: "  + String.format("%.2f", Timer.getFPGATimestamp()- Robot.enabledTime)  + "s");
    	Robot.liftMechanism.setArmMoving(false);
    }
}
