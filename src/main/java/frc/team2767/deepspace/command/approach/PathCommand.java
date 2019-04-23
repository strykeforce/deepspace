package frc.team2767.deepspace.command.approach;

import edu.wpi.first.wpilibj.command.Command;
import frc.team2767.deepspace.Robot;
import frc.team2767.deepspace.subsystem.DriveSubsystem;

public class PathCommand extends Command {

  private static final DriveSubsystem DRIVE = Robot.DRIVE;
  private final String name;
  private final double targetYaw;
  private final boolean isDriftOut;

  public PathCommand(String name, double targetYaw) {
    this(name, targetYaw, true);
  }

  public PathCommand(String name, double targetYaw, boolean isDriftOut) {
    this.name = name;
    this.targetYaw = targetYaw;
    this.isDriftOut = isDriftOut;
    requires(DRIVE);
  }

  @Override
  protected void initialize() {
    DRIVE.startPath(name, targetYaw, isDriftOut);
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
