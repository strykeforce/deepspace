package frc.team2767.deepspace.command.approach;

import edu.wpi.first.wpilibj.command.CommandGroup;
import edu.wpi.first.wpilibj.command.ConditionalCommand;
import frc.team2767.deepspace.Robot;
import frc.team2767.deepspace.command.YawCommand;
import frc.team2767.deepspace.command.log.LogCommand;
import frc.team2767.deepspace.subsystem.DriveSubsystem;
import frc.team2767.deepspace.subsystem.FieldDirection;
import frc.team2767.deepspace.subsystem.VisionSubsystem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FieldSquarePickupAlignmentCommand extends ConditionalCommand {

  private static final VisionSubsystem VISION = Robot.VISION;
  private static final DriveSubsystem DRIVE = Robot.DRIVE;

  private final Logger logger = LoggerFactory.getLogger(this.getClass());

  public FieldSquarePickupAlignmentCommand() {
    super(
        new CommandGroup() {
          {
            addSequential(new LogCommand("Field left"));
            addSequential(new YawCommand(-90.0));
            addSequential(new SetTargetYawCommand(-90.0));
          }
        },
        new CommandGroup() {
          {
            addSequential(new LogCommand("Field right"));
            addSequential(new YawCommand(90.0));
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
