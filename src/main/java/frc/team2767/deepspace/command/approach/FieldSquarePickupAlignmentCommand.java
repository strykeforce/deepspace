package frc.team2767.deepspace.command.approach;

import edu.wpi.first.wpilibj.command.CommandGroup;
import edu.wpi.first.wpilibj.command.ConditionalCommand;
import frc.team2767.deepspace.Robot;
import frc.team2767.deepspace.command.log.LogCommand;
import frc.team2767.deepspace.subsystem.DriveSubsystem;
import frc.team2767.deepspace.subsystem.FieldDirection;
import frc.team2767.deepspace.subsystem.VisionSubsystem;

public class FieldSquarePickupAlignmentCommand extends ConditionalCommand {

  private static final VisionSubsystem VISION = Robot.VISION;
  private static final DriveSubsystem DRIVE = Robot.DRIVE;

  public FieldSquarePickupAlignmentCommand() {
    super(
        new CommandGroup() {
          {
            addSequential(new LogCommand("Field left"));
            addSequential(new YawToTargetCommand(-90.0));
            addSequential(new SetTargetYawCommand(-90.0));
          }
        },
        new CommandGroup() {
          {
            addSequential(new LogCommand("Field right"));
            addSequential(new YawToTargetCommand(90.0));
            addSequential(new SetTargetYawCommand(90.0));
          }
        });
  }

  @Override
  protected boolean condition() {
    double bearing = Math.IEEEremainder(DRIVE.getGyro().getAngle(), 360);
    if (bearing <= 0) {
      VISION.setFieldDirection(FieldDirection.LEFT);
    } else {
      VISION.setFieldDirection(FieldDirection.RIGHT);
    }
    return VISION.direction == FieldDirection.LEFT;
  }
}
