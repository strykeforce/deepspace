package frc.team2767.deepspace.command.pathfinder;

import edu.wpi.first.wpilibj.command.Command;
import frc.team2767.deepspace.Robot;
import frc.team2767.deepspace.subsystem.DriveSubsystem;

public class PathCommand extends Command {

  private static final DriveSubsystem DRIVE = Robot.DRIVE;

  private double targetYaw;

  public PathCommand(String pathName, double targetYaw) {
    requires(DRIVE);
    this.targetYaw = targetYaw;
    setInterruptible(true);
  }

  @Override
  protected void initialize() {
    DRIVE.startPath("loading_to_cargo", targetYaw);
  }

  @Override
  protected boolean isFinished() {
    return DRIVE.isPathFinished();
  }

  @Override
  protected void interrupted() {
    DRIVE.interruptPath();
  }
}
