package frc.team2767.deepspace.command.teleop;

import edu.wpi.first.wpilibj.command.InstantCommand;
import frc.team2767.deepspace.Robot;
import frc.team2767.deepspace.subsystem.*;

public class InterruptCommand extends InstantCommand {

  private static final DriveSubsystem DRIVE = Robot.DRIVE;
  private static final VisionSubsystem VISION = Robot.VISION;
  private static final BiscuitSubsystem BISCUIT = Robot.BISCUIT;

  public InterruptCommand() {
    requires(DRIVE);
    requires(BISCUIT);
  }

  @Override
  protected void initialize() {
    VISION.setAction(Action.PLACE);
    BISCUIT.setPosition(
        VISION.direction == FieldDirection.RIGHT
            ? BiscuitSubsystem.kRightPositionDeg
            : BiscuitSubsystem.kLeftPositionDeg);
  }
}
