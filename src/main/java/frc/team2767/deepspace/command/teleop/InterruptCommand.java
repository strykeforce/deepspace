package frc.team2767.deepspace.command.teleop;

import edu.wpi.first.wpilibj.command.InstantCommand;
import frc.team2767.deepspace.Robot;
import frc.team2767.deepspace.subsystem.DriveSubsystem;

public class InterruptCommand extends InstantCommand {

  private static final DriveSubsystem DRIVE = Robot.DRIVE;

  public InterruptCommand() {
    requires(DRIVE);
  }
}
