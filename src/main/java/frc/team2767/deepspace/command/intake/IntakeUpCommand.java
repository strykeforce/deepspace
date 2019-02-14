package frc.team2767.deepspace.command.intake;

import edu.wpi.first.wpilibj.command.InstantCommand;
import frc.team2767.deepspace.Robot;
import frc.team2767.deepspace.subsystem.IntakeSubsystem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class IntakeUpCommand extends InstantCommand {
  private static final IntakeSubsystem INTAKE = Robot.INTAKE;
  private Logger logger = LoggerFactory.getLogger(this.getClass());

  public IntakeUpCommand() {
    requires(INTAKE);
  }

  @Override
  protected void initialize() {
    INTAKE.shoulderOpenLoop(0.15);
  }
}
