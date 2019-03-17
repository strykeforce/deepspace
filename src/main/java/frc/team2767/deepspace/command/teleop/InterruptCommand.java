package frc.team2767.deepspace.command.teleop;

import edu.wpi.first.wpilibj.command.InstantCommand;
import frc.team2767.deepspace.Robot;
import frc.team2767.deepspace.subsystem.Action;
import frc.team2767.deepspace.subsystem.DriveSubsystem;
import frc.team2767.deepspace.subsystem.VisionSubsystem;

public class InterruptCommand extends InstantCommand {

  private static final DriveSubsystem DRIVE = Robot.DRIVE;
  private static final VisionSubsystem VISION = Robot.VISION;

  public InterruptCommand() {
    requires(DRIVE);
  }

  @Override
  protected void initialize() {
    VISION.setAction(Action.PLACE);
  }
}
