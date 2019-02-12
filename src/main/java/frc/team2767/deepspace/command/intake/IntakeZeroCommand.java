package frc.team2767.deepspace.command.intake;

import edu.wpi.first.wpilibj.command.Command;
import frc.team2767.deepspace.Robot;
import frc.team2767.deepspace.subsystem.IntakeSubsystem;

public class IntakeZeroCommand extends Command {

  private final IntakeSubsystem INTAKE = Robot.INTAKE;

  public IntakeZeroCommand() {
    requires(INTAKE);
  }

  @Override
  protected void initialize() {
    INTAKE.shoulderToZero();
  }

  @Override
  protected boolean isFinished() {
    return INTAKE.onZero();
  }

  @Override
  protected void end() {
    INTAKE.shoulderZeroWithLimitSwitch();
  }
}
