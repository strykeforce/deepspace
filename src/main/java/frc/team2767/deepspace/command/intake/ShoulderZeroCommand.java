package frc.team2767.deepspace.command.intake;

import edu.wpi.first.wpilibj.command.Command;
import frc.team2767.deepspace.Robot;
import frc.team2767.deepspace.subsystem.IntakeSubsystem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ShoulderZeroCommand extends Command {

  private static final IntakeSubsystem INTAKE = Robot.INTAKE;
  private final Logger logger = LoggerFactory.getLogger(this.getClass());

  public ShoulderZeroCommand() {
    requires(INTAKE);
  }

  @Override
  protected void initialize() {
    INTAKE.shoulderToZero();
  }

  @Override
  protected boolean isFinished() {
    return INTAKE.onZero() || isTimedOut();
  }

  @Override
  protected void end() {
    INTAKE.shoulderZeroWithLimitSwitch();
  }
}
