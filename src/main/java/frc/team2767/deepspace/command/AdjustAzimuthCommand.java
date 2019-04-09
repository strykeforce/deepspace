package frc.team2767.deepspace.command;

import edu.wpi.first.wpilibj.command.InstantCommand;
import frc.team2767.deepspace.Robot;
import frc.team2767.deepspace.subsystem.DriveSubsystem;

public class AdjustAzimuthCommand extends InstantCommand {
  private final DriveSubsystem DRIVE = Robot.DRIVE;
  int wheel;
  int teeth;

  public AdjustAzimuthCommand(int wheel, int teeth) {
    requires(DRIVE);
    setRunWhenDisabled(true);
    this.wheel = wheel;
    this.teeth = teeth;
  }

  @Override
  protected void _initialize() {
    DRIVE.adjustZero(wheel, teeth);
  }
}
