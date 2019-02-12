package frc.team2767.deepspace.command.intake;

import edu.wpi.first.wpilibj.command.Command;
import frc.team2767.deepspace.Robot;
import frc.team2767.deepspace.subsystem.IntakeSubsystem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class IntakeZeroCommand extends Command {

  private final IntakeSubsystem INTAKE = Robot.INTAKE;
  private Logger logger = LoggerFactory.getLogger(this.getClass());

  public IntakeZeroCommand() {
    requires(INTAKE);
  }

  @Override
  protected void initialize() {
    logger.debug("zeroing shoulder");
    INTAKE.shoulderToZero();
  }

  @Override
  protected boolean isFinished() {
    return INTAKE.onZero();
  }

  @Override
  protected void end() {
    logger.debug("finished zeroing");
    INTAKE.shoulderZeroWithLimitSwitch();
  }
}
