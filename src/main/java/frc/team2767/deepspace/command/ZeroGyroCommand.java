package frc.team2767.deepspace.command;

import edu.wpi.first.wpilibj.command.InstantCommand;
import frc.team2767.deepspace.Robot;
import frc.team2767.deepspace.subsystem.DriveSubsystem;

public final class ZeroGyroCommand extends InstantCommand {

  private static final DriveSubsystem DRIVE = Robot.DRIVE;

  public ZeroGyroCommand() {
    requires(DRIVE);
  }

  @Override
  protected void initialize() {
    DRIVE.zeroGyro();
  }
}
