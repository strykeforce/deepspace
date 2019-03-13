package frc.team2767.deepspace.command.approach;

import edu.wpi.first.wpilibj.command.ConditionalCommand;
import frc.team2767.deepspace.Robot;
import frc.team2767.deepspace.subsystem.FieldDirection;
import frc.team2767.deepspace.subsystem.VisionSubsystem;

public class ChooseTargetYawCommand extends ConditionalCommand {

  private static final VisionSubsystem VISION = Robot.VISION;

  public ChooseTargetYawCommand() {
    super(new SetTargetYawCommand(-90.0), new SetTargetYawCommand(90.0));
  }

  @Override
  protected boolean condition() {
    return VISION.direction == FieldDirection.LEFT;
  }
}
