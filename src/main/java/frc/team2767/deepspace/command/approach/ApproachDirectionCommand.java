package frc.team2767.deepspace.command.approach;

import edu.wpi.first.wpilibj.command.InstantCommand;
import frc.team2767.deepspace.Robot;
import frc.team2767.deepspace.subsystem.DriveSubsystem;
import frc.team2767.deepspace.subsystem.FieldDirection;
import frc.team2767.deepspace.subsystem.VisionSubsystem;

public class ApproachDirectionCommand extends InstantCommand {

  private static final VisionSubsystem VISION = Robot.VISION;
  private static final DriveSubsystem DRIVE = Robot.DRIVE;

  public ApproachDirectionCommand() {}

  @SuppressWarnings("Duplicates")
  @Override
  protected void initialize() {
    double bearing = Math.IEEEremainder(DRIVE.getGyro().getAngle(), 360);
    if (bearing <= 0) {
      VISION.setFieldDirection(FieldDirection.LEFT);
    } else VISION.setFieldDirection(FieldDirection.RIGHT);
  }
}
