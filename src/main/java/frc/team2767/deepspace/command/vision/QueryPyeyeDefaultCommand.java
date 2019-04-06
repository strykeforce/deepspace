package frc.team2767.deepspace.command.vision;

import edu.wpi.first.wpilibj.command.Command;
import frc.team2767.deepspace.Robot;
import frc.team2767.deepspace.subsystem.VisionSubsystem;

public class QueryPyeyeDefaultCommand extends Command {

  private static final VisionSubsystem VISION = Robot.VISION;

  public QueryPyeyeDefaultCommand() {
    setInterruptible(true);
    requires(VISION);
  }

  @Override
  protected void execute() {
    VISION.queryPyeye();
  }

  @Override
  protected boolean isFinished() {
    return false;
  }
}
