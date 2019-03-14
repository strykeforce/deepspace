package frc.team2767.deepspace.command.approach;

import edu.wpi.first.wpilibj.command.Command;
import frc.team2767.deepspace.Robot;
import frc.team2767.deepspace.subsystem.DriveSubsystem;
import frc.team2767.deepspace.subsystem.VisionSubsystem;

public class VisionTwistCommand extends Command {

  private static final DriveSubsystem DRIVE = Robot.DRIVE;
  private static final VisionSubsystem VISION = Robot.VISION;

  public VisionTwistCommand() {
    requires(DRIVE);
    setInterruptible(true);
  }

  @Override
  protected void initialize() {
    double heading = VISION.getCorrectedHeading();
    int distance = (int) (DriveSubsystem.TICKS_PER_INCH * VISION.getCorrectedRange());
    double targetYaw = VISION.getTargetYaw();
    DRIVE.startTwist(heading, distance, targetYaw);
  }

  @Override
  protected boolean isFinished() {
    return DRIVE.isTwistFinished();
  }

  @Override
  protected void interrupted() {
    DRIVE.interruptTwist();
  }
}
